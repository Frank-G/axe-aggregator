/*
 * Copyright (c) 2014-2015 University of Ulm
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.uniulm.omi.cloudiator.axe.aggregator;

import de.uniulm.omi.cloudiator.axe.aggregator.entities.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kairosdb.client.HttpClient;
import org.kairosdb.client.builder.*;
import org.kairosdb.client.response.Queries;
import org.kairosdb.client.response.QueryResponse;
import org.kairosdb.client.response.Results;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by frank on 27.01.15.
 */
public class KairosDbConnection {
    private static final int PULL_DELAY = 5000;
        //TODO maybe not needed anymore, since now visor immediately pushes values
    private static final int MAX_RETRY = 2;
    private String url;
    private String ip;
    private Integer port;

    public static final Logger LOGGER = LogManager.getLogger(KairosDbConnection.class);

    public KairosDbConnection(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
        this.url = "http://" + this.ip + ":" + this.port.toString();
    }

    synchronized public void write(String metricName, List<Tag> tags, double value) {
        long timeStamp = System.currentTimeMillis();
        write(metricName, tags, value, timeStamp);
    }

    synchronized public void write(String metricName, List<Tag> tags, double value,
        long timeStamp) {

        HttpClient httpClient = null;
        try {
            httpClient = new HttpClient(this.url);
        } catch (MalformedURLException e) {
            LOGGER.error("URL is malformed: " + this.url);
            return;
        }

        MetricBuilder metricBuilder = MetricBuilder.getInstance();

        org.kairosdb.client.builder.Metric kairosMetric = metricBuilder.addMetric(metricName)
            .addDataPoint(timeStamp - PULL_DELAY /* TODO testing purpose */, value);
        //we need to add the tags
        for (Tag t : tags) {
            kairosMetric.addTag(t.getName(), t.getValue());
        }

        try {
            httpClient.pushMetrics(metricBuilder);
        } catch (URISyntaxException e) {
            LOGGER.error("URL Syntax is malformed: " + this.url);
            return;
        } catch (IOException e) {
            LOGGER.error("Something went wrong on pushing metrics: " + this.url);
            return;
        }
    }

    synchronized public List<Double> getAggregatedValue(String metricName, List<String> tagValues,
        FormulaOperator function, Window window, long minimumInterval, Schedule schedule) {
        return getAggregatedValue(metricName, tagValues, function, window, minimumInterval,
            schedule, 0);
    }

    synchronized public List<Double> getAggregatedValue(String metricName, List<String> tagValues,
        FormulaOperator function, Window window, long minimumInterval, Schedule schedule,
        int retry) {

        Date now = new Date();
        now.setTime(now.getTime()
            - PULL_DELAY); /** TODO delay must be dynamic, only apply for raw monitors */

        HttpClient httpClient = null;
        try {
            httpClient = new HttpClient(this.url);
        } catch (MalformedURLException e) {
            LOGGER.error("URL is malformed: " + this.url);
            return new ArrayList<>();
        }

        QueryBuilder builder = QueryBuilder.getInstance();

        String tagName = "monitorinstance";


        //QueryBuilder queryBuilder = null;
        int aggregationInMilliSeconds =
            (int) window.aggregationInMilliseconds(schedule, minimumInterval);
        builder = builder.setStart(aggregationInMilliSeconds + PULL_DELAY, TimeUnit.MILLISECONDS);

        QueryMetric queryMetric = builder.setEnd(now).addMetric(metricName);

        for (String tagValue : tagValues) {
            queryMetric = queryMetric.addTag(tagName, tagValue);
        }

        // DO only aggregate in Kairos if it is a TimeWindow
        if (window instanceof TimeWindow) {
            switch (function) {
                case AVG: {
                    queryMetric.addAggregator(AggregatorFactory
                        .createAverageAggregator(aggregationInMilliSeconds, TimeUnit.MILLISECONDS));
                }
                break;
                case SUM: {
                    queryMetric.addAggregator(AggregatorFactory
                        .createSumAggregator(aggregationInMilliSeconds, TimeUnit.MILLISECONDS));
                }
                break;
                case STD: {
                    queryMetric.addAggregator(AggregatorFactory
                        .createStandardDeviationAggregator(aggregationInMilliSeconds,
                            TimeUnit.MILLISECONDS));
                }
                break;
                case COUNT: {
                    queryMetric.addAggregator(AggregatorFactory
                        .createCountAggregator(aggregationInMilliSeconds, TimeUnit.MILLISECONDS));
                }
                break;
                case MIN: {
                    queryMetric.addAggregator(AggregatorFactory
                        .createMinAggregator(aggregationInMilliSeconds, TimeUnit.MILLISECONDS));
                }
                break;
                case MAX: {
                    queryMetric.addAggregator(AggregatorFactory
                        .createMaxAggregator(aggregationInMilliSeconds, TimeUnit.MILLISECONDS));
                }
                break;
                case DIV: {
                    queryMetric.addAggregator(
                        AggregatorFactory.createRateAggregator(TimeUnit.MILLISECONDS));
                }
                break;
                default:
                    break;
                // no exception since we just get the raw values then...
            }
        }


        QueryResponse response = null;
        try {
            response = httpClient.query(builder);
        } catch (URISyntaxException e) {
            LOGGER.error("URI has wrong syntax: " + e.getMessage());
            return new ArrayList<>();
        } catch (IOException e) {
            LOGGER.error("Something went wrong on querying: " + e.getMessage());
            return new ArrayList<>();
        }

        List<Queries> queries = response.getQueries();

        if (queries.size() > 1)
            throw new RuntimeException("more than one query in response");
        if (queries.isEmpty())
            throw new RuntimeException("no query in response");

        List<Results> results = queries.get(0).getResults();

        if (results.size() > 1)
            throw new RuntimeException("too much results received");
        if (results.isEmpty())
            throw new RuntimeException("no results received");

        List<Double> result = new ArrayList<Double>();

        for (DataPoint point : results.get(0).getDataPoints()) {
            try {
                result.add(point.doubleValue());
            } catch (DataFormatException e) {
                LOGGER.error(
                    "Value could not be transformed to value: " + metricName + "; on: " + point
                        .toString());
            }
        }

        if (result.isEmpty() && retry < MAX_RETRY) {
            Window biggerWindow;
            if (window instanceof TimeWindow) {
                TimeWindow tw = (TimeWindow) window;
                biggerWindow = new TimeWindow(-1, tw.getInterval() * 2, tw.getTimeUnit());
            } else if (window instanceof MeasurementWindow) {
                MeasurementWindow mw = (MeasurementWindow) window;
                biggerWindow = new MeasurementWindow(-1, mw.getMeasurements() * 2);
            } else {
                throw new RuntimeException(
                    "Window type not implemented in " + this.getClass().toString());
            }
            return getAggregatedValue(metricName, tagValues, function, biggerWindow,
                minimumInterval, schedule, ++retry);
        }

        if (window instanceof MeasurementWindow
            && (((MeasurementWindow) window).getMeasurements() * tagValues.size()) < result
            .size()) {
            // take only the size of the measurement window - latest values preferred
            result = result.subList(
                result.size() - (((MeasurementWindow) window).getMeasurements() * tagValues.size()),
                result.size());
            // TODO if size < measurments: return getAggregatedValue with higher schedule and at least 5 times
        }

        return result;
    }

    public boolean isAggregationMappable(FormulaOperator function) {
        switch (function) {
            case AVG:
            case SUM:
            case STD:
            case COUNT:
            case MIN:
            case MAX:
            case MINUS:
            case DIV: // DIV needs fixed value! Here it is RATE!
                //case PERCENTILE: not supported in client
                //case SAMPLER: not supported in client
                //case SCALE: not supported in client
                //case LEAST_SQUARES: not supported in client
                return true;
            default:
                return false;
        }
    }
}
