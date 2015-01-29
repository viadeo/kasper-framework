package com.viadeo.kasper.context.impl;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.viadeo.kasper.context.ContextValidationException;
import com.viadeo.kasper.context.ContextValidator;
import com.viadeo.kasper.context.ImmutableContext;

import java.util.Locale;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultContextValidator<C extends ImmutableContext> implements ContextValidator<C> {

    private static final Set<String> SUPPORTED_LANG = ImmutableSet.of("fr", "es", "en", "de", "pt", "it", "mx", "ru", "ar");
    private static Set<String> countries = Sets.newHashSet(Locale.getISOCountries());


    @Override
    public void validate(C context) throws ContextValidationException {

        try {

            checkNotNull(context.getApplicationId());
            checkNotNull(context.getRequestCorrelationId());
            checkNotNull(context.getFunnelCorrelationId());
            checkNotNull(context.getFunnelName());
            checkNotNull(context.getFunnelVersion());
            checkNotNull(context.getSessionCorrelationId());
            checkNotNull(context.getIpAddress());

            if (context.getUserID().isPresent()) {

                checkNotNull(context.getSecurityToken());

                checkArgument(
                        null != context.getUserLang() && SUPPORTED_LANG.contains(context.getUserLang()),
                        context.getUserLang() + " is not a lang supported by viadeo (fr, es, en, de, pt, it, mx, ru, ar)"
                );

                checkArgument(
                        null != context.getUserCountry() && countries.contains(context.getUserCountry().toUpperCase()),
                        context.getUserCountry() + " is not a country code ISO 3166 (2 letters)"
                );
            }

        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ContextValidationException(e);
        }
    }
}
