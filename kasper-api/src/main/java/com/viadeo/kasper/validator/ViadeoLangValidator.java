package com.viadeo.kasper.validator;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class ViadeoLangValidator {

    public static final Set<String> SUPPORTED_LANG = ImmutableSet.of("fr", "es", "en", "de", "pt", "it", "mx", "ru", "ar");

    public static boolean isValid(String value) {
        return null != value && SUPPORTED_LANG.contains(value);
    }
}
