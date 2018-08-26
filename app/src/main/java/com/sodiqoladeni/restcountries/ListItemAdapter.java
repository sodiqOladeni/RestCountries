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

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

/**
 * An {@link ListItemAdapter} knows how to create a list item layout for each earthquake
 * in the data source (a list of {@link ListItemAdapter} objects).
 *
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class ListItemAdapter extends ArrayAdapter<CountryListModel> {

    public ListItemAdapter(Context context, List<CountryListModel> countries) {
        super(context, 0, countries);
    }

    /**
     * Returns a list item view that displays information about the earthquake at the given position
     * in the list of earthquakes.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.model_list_item, parent, false);
        }

        // Find the country at the given position in the list of earthquakes
        CountryListModel countryModel = getItem(position);

        TextView countryInitialView = listItemView.findViewById(R.id.first_initial);
        countryInitialView.setText(countryModel.getNameInitialChar());

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable initialCircle = (GradientDrawable) countryInitialView.getBackground();
        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(countryModel.getNameInitial());
        // Set the color on the magnitude circle
        initialCircle.setColor(magnitudeColor);

        // Find the TextView with view ID country_name
        TextView countryName = listItemView.findViewById(R.id.country_name);
        // Display the magnitude of the current earthquake in that TextView
        countryName.setText(countryModel.getName());

        // Find the TextView with view ID country_currency
        TextView countryCurrency = listItemView.findViewById(R.id.country_currency);
        // Display the magnitude of the current earthquake in that TextView
        countryCurrency.setText(countryModel.getCurrency());

        // Find the TextView with view ID country_currency
        TextView countryLanguage= listItemView.findViewById(R.id.country_language);
        // Display the magnitude of the current earthquake in that TextView
        countryLanguage.setText(countryModel.getLanguage());

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

    /**
     * Return the color for the magnitude circle based on the intensity of the earthquake.
     *
     * @param magnitude of the earthquake
     */
    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }

        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }
}
