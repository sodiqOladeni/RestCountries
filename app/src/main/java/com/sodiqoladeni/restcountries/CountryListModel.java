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

/**
 *An object representing each contry that will be displayed
 */
public class CountryListModel {

    //The Country name to be displayed
    private String name;

    //The Currency name of the country to be displayed
    private String currency;

    //The Language of the country to be displayed
    private String language;

    //The first letter of the country, just for design purpose
    private double nameInitial;

    //Initial constructor to avoid null instantiation
    public CountryListModel(String name, String currency, String language, double nameInitial) {
        this.name = name;
        this.currency = currency;
        this.language = language;
        this.nameInitial = nameInitial;
    }

    //// Methods for getting value from the object
    public String getName() {
        return name;
    }

    public String getCurrency() {
        return currency;
    }

    public String getLanguage() {
        return language;
    }

    public double getNameInitial() {
        return nameInitial;
    }

    public String getNameInitialChar() {
       return String.valueOf(name.charAt(0));
    }
}
