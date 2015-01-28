package com.viadeo.kasper.validator;

import com.google.common.collect.Sets;

import java.util.Locale;
import java.util.Set;

public class CountryValidator {

    private static Set<String> countries = Sets.newHashSet(Locale.getISOCountries());

    public static boolean isValid(String value) {
        return null != value && countries.contains(value.toUpperCase());
    }
}
