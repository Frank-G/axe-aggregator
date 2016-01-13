package de.uniulm.omi.cloudiator.axe.aggregator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Frank on 12.01.2016.
 */
public class KairosMetricParameter {
    private String metricName;
    private final Map<String, String> tags = new HashMap<>();
    private final Map<String, Integer> aggregations = new HashMap<>();

    public KairosMetricParameter() {
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public Map<String, Integer> getAggregations() {
        return aggregations;
    }

    public void addTag(String key, String value){
        this.tags.put(key, value);
    }

    public void addAggregation(String name, Integer time){
        this.aggregations.put(name, time);
    }

    @Override
    public String toString() {
        String output = "KairosParameter: ";
        output += "\t" + "MetricName: " + metricName + "\n";
        output += "\t" + "Tags: " + "\n";
        for(Map.Entry<String, String> entry : this.tags.entrySet()){
            output += "\t" + entry.getKey() + " : " + entry.getValue() + "\n";
        }
        output += "\t" + "Aggregations: " + "\n";
        for(Map.Entry<String, Integer> entry : this.aggregations.entrySet()){
            output += "\t" + entry.getKey() + " : " + entry.getValue() + " ms" + "\n";
        }

        return output;
    }
}
