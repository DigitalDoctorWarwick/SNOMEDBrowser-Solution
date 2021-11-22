package com.example.android.nhstest2;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving concept data from SNOMED API.
 */
public final class QueryUtils {

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the SNOMED dataset and return a list of {@link ConceptItem} objects.
     */
    public static List<ConceptItem> fetchConceptItemData(String requestUrl) {
        // Return the list of {@link ConceptItem}s
        return extractFeatureFromJson(tryToConnect(requestUrl));
    }

    public static Concept fetchConceptData(String requestUrl) {
        return extractConceptFromJson(tryToConnect(requestUrl));
    }

    public static Concept fetchRelatedConceptData(
            String requestUrl, Concept concept, int relationType) {
        return extractRelatedConceptFromJson(tryToConnect(requestUrl), concept, relationType);
    }

    private static String tryToConnect(String requestUrl) {
        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e("QueryUtils", "Problem building the URL ", e);
        }

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("QueryUtils", "Problem making the HTTP request.", e);
        }
        return jsonResponse;
    }

    /**
     * Return a list of {@link ConceptItem} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<ConceptItem> extractFeatureFromJson(String conceptJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(conceptJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding conceptItems to
        List<ConceptItem> conceptItems = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject rootJSONObject = new JSONObject(conceptJSON);
            JSONArray matches = rootJSONObject.getJSONArray("items"); //"matches"
            for (int i = 0; i < matches.length(); i++){
                JSONObject link = matches.getJSONObject(i);
                String term = link.getString("term");
                long conceptId = link.getJSONObject("concept").getLong("conceptId");
                String fsn = link.getJSONObject("concept").getJSONObject("fsn").getString("term");

                conceptItems.add(new ConceptItem(term, conceptId, fsn));//, url));
                //Log.i("QueryUtils",term + " " + conceptId + " " + fsn);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the concept JSON results", e);
        }

        // Return the list of conceptItems
        return conceptItems;
    }

    private static Concept extractConceptFromJson(String conceptJSON) {
        try {
            JSONObject rootJSONObject = new JSONObject(conceptJSON);
            String fullySpecifiedName = rootJSONObject.getJSONObject("fsn").getString("term");
            String preferredTerm = rootJSONObject.getJSONObject("pt").getString("term");
            String active = rootJSONObject.getString("active");
            String conceptId = rootJSONObject.getString("conceptId");

            Concept concept = new Concept(fullySpecifiedName, preferredTerm, active, conceptId);

            JSONArray descriptionsArray = rootJSONObject.getJSONArray("descriptions");
            for (int i = 0; i < descriptionsArray.length(); i++){
                JSONObject description = descriptionsArray.getJSONObject(i);

                concept.addDescription(description.getString("descriptionId"),description.getString("term"));
            }

            return concept;
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the concept JSON results", e);
            return null;
        }
    }

    private static Concept extractRelatedConceptFromJson(
            String conceptJSON, Concept concept, int relationType) {
        try {
            JSONArray rootJSONArray = new JSONArray(conceptJSON);

            for (int i = 0; i < rootJSONArray.length(); i++){
                JSONObject relatedConcept = rootJSONArray.getJSONObject(i);

                String conceptId = relatedConcept.getString("conceptId");
                String preferredTerm = relatedConcept.getJSONObject("pt").getString("term");

                if (relationType == ConceptDetailActivity.CHILD_CODE) {
                    concept.addChild(conceptId, preferredTerm);
                } else if (relationType == ConceptDetailActivity.PARENT_CODE) {
                    concept.addParent(conceptId, preferredTerm);
                }
            }

            return concept;
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the childConcept JSON results", e);
            return null;
        }
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(30000 /* milliseconds */);
            urlConnection.setConnectTimeout(20000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            Log.i("QueryUtils", url.toString());
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("QueryUtils", "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("QueryUtils", "Problem retrieving the concept list JSON results. " + e.getMessage(), e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}