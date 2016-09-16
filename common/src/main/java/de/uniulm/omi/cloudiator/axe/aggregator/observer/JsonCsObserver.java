package de.uniulm.omi.cloudiator.axe.aggregator.observer;

import de.uniulm.omi.cloudiator.axe.aggregator.entities.FormulaOperator;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Frank on 25.05.2016.
 */
public class JsonCsObserver extends HttpObserver {
    private final String USER_AGENT = "Axe-Aggregator/0.1";

    public JsonCsObserver(String externalId, double threshold, FormulaOperator operator, String endpoint) {
        super(externalId, threshold, operator, endpoint);
    }

    @Override
    public void update(Measurement obj) {
        //TODO add exception handling

        try {
            //TODO dont close and open it each time, but use the running connection
            openConnection();

            getConnection().setRequestMethod("POST");
            getConnection().setRequestProperty("User-Agent", USER_AGENT);
            getConnection().setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            getConnection().setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            getConnection().setDoOutput(true);

            String jsonString = "";

            //TODO modify via JSONObject
            jsonString += "[";
            jsonString += "   {";
            jsonString += "        \"id\": \"" + obj.getIdMonitorInstance() + "\",";
            jsonString += "        \"timestamp\": \"" + obj.getTimeStamp() + "\",";
            jsonString += "        \"data\": \"" + obj.getMeasurement() + "\"";
            jsonString += "    }";
            jsonString += "]";


            // Send post request
            DataOutputStream wr = new DataOutputStream(getConnection().getOutputStream());
            wr.writeBytes(jsonString);
            wr.flush();
            wr.close();

            int responseCode = getConnection().getResponseCode();
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(getConnection().getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            LOGGER.debug("Response: " + response.toString());
        } catch (IOException e){
            LOGGER.error(e);
        }

    }
}
