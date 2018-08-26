/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sodiqoladeni.restcountries;

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
import java.util.Random;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class NetworkUtils {

    /** Tag for the log messages */
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link NetworkUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private NetworkUtils() {
    }

    /**
     * Query the USGS dataset and return a list of {@link CountryListModel} objects.
     */
    public static List<CountryListModel> fetchCountriesData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Earthquake}s
        List<CountryListModel> earthquakes = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Earthquake}s
        return earthquakes;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
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
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
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

    /**
     * Return a list of {@link CountryListModel} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<CountryListModel> extractFeatureFromJson(String countriesJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(countriesJSON)) {
            Log.v(LOG_TAG, "countryJSON is null");
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        List<CountryListModel> countriesData = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONArray baseJsonResponse = new JSONArray(countriesJSON);

            // For each countries in the dataArray, create an {@link Earthquake} object
            for (int i = 0; i < baseJsonResponse.length(); i++) {

                //To be used by ListItemAdapter for setting random color for each country
                int anyRandomNumber = new Random().nextInt(10);

                // Get a single country at position i within the list of earthquakes
                JSONObject currentCountryData= baseJsonResponse.getJSONObject(i);

                // For a given country, extract the JSONObject associated with the
                // key called "name", which represents a list of all properties
                // for that country.
                //JSONObject name = currentCountryData.getJSONObject("name");

                // Extract the value for the key called "name"
                String countryName = currentCountryData.getString("name");

                // Extract the value for the key called "currencies" and also check for name of the currency
                String currencyName = currentCountryData.getJSONArray("currencies").
                        getJSONObject(0).getString("name");

                // Extract the value for the key called "currencies" and also check for name of the currency
                String countryLanguage = currentCountryData.getJSONArray("languages").
                        getJSONObject(0).getString("name");

                Log.v(LOG_TAG,"Name: "+countryName+"Currency: "+currencyName+"Language: "+countryLanguage );

                // Create a new {@link CountryListModel} object with the name, currency, language,
                // and url from the JSON response.
                CountryListModel countryModel = new CountryListModel(countryName, currencyName,
                        countryLanguage, anyRandomNumber);

                // Add the new {@link Earthquake} to the list of earthquakes.
                countriesData.add(countryModel);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("CountryListModel", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return countriesData;
    }

}
