package de.uniulm.omi.cloudiator.axe.aggregator.observer;

import de.uniulm.omi.cloudiator.axe.aggregator.AggregatorService;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.Constants;
import de.uniulm.omi.cloudiator.axe.aggregator.entities.FormulaOperator;
import de.uniulm.omi.cloudiator.colosseum.client.entities.internal.KeyValue;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.api.StepsApi;

/**
 * Created by Frank on 11.01.2017.
 */
public class ActivationObserver extends HttpObserver {
    private final String USER_AGENT = "Axe-Aggregator/0.1";

    private final String apiEndpoint;

    public ActivationObserver(String externalId, double threshold, FormulaOperator operator, String endpoint) {
        super(externalId, threshold, operator, endpoint);

        this.apiEndpoint = endpoint;
    }

    @Override
    public void update(Measurement obj) {

        /*TODO not very clean */
        String stepId = "";
        for(KeyValue kv : AggregatorService.getService(null, Constants.LOCALHOST_IP).getFc()
                .getMonitorInstance(obj.getIdMonitorInstance()).getExternalReferences()){
            if("STEP".equals(kv.getKey())){
                stepId = kv.getValue();
            }
        }

        // Adaptation management client:
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(apiEndpoint);
        DefaultApi defaultApi = new DefaultApi(apiClient);

        try {
            defaultApi.stepIdActivatePut(stepId);
        } catch (ApiException e) {
            LOGGER.error(e);
        }
    }
}

