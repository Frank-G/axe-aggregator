package de.uniulm.omi.cloudiator.axe.aggregator.observer;

import de.uniulm.omi.cloudiator.axe.aggregator.entities.FormulaOperator;
import de.uniulm.omi.cloudiator.axe.aggregator.observer.NetworkThresholdObserver;
import de.uniulm.omi.cloudiator.axe.aggregator.observer.ThresholdObserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Frank on 25.05.2016.
 */
public abstract class HttpObserver extends ThresholdObserver {
    private URL url;
    private HttpURLConnection con;
    private String endpoint;

    public HttpObserver(String externalId, double threshold, FormulaOperator operator, String endpoint) {
        super(externalId, threshold, operator);

        this.endpoint = endpoint;

        this.openConnection();
    }

    protected void openConnection(){
        this.openConnection(true);
    }

    protected void openConnection(boolean firstTry){
        // TODO add error handling
        if(this.con != null){
            this.con.disconnect();
        }

        try {
            this.url = new URL(endpoint);
        } catch (MalformedURLException e) {
            LOGGER.error(e);
        }
        try {
            this.con = (HttpURLConnection) this.url.openConnection();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    protected HttpURLConnection getConnection(){
        return con;
    }
}
