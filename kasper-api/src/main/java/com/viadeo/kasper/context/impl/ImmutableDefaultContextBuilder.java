package com.viadeo.kasper.context.impl;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.ID;
import com.viadeo.kasper.context.ContextValidator;
import com.viadeo.kasper.context.ImmutableContextBuilder;

import java.io.Serializable;
import java.util.Map;

public class ImmutableDefaultContextBuilder implements ImmutableContextBuilder<ImmutableDefaultContext> {

    private ID userID;
    private String userLang;
    private String userCountry;

    private String applicationId;

    private String requestCorrelationId;
    private String funnelCorrelationId;
    private String sessionCorrelationId;

    private String securityToken;

    private String funnelName;
    private String funnelVersion;

    private String ipAddress;

    private Map<String, Serializable> properties;

    private ContextValidator<ImmutableDefaultContext> validator;

    private int sequenceIncrement;


    public ImmutableDefaultContextBuilder() {
        validator = new DefaultContextValidator<>();
    }

    @Override
    public ImmutableDefaultContext build() {

        ImmutableDefaultContext context = new ImmutableDefaultContext(userID, userLang, userCountry, applicationId,
                requestCorrelationId, funnelCorrelationId, sessionCorrelationId,
                securityToken, funnelName, funnelVersion, ipAddress, sequenceIncrement);

        Optional<ContextValidator<ImmutableDefaultContext>> validatorOptional = getValidator();

        if (validatorOptional.isPresent()) {
            validatorOptional.get().validate(context);
        }

        return context;
    }

    @Override
    public ImmutableContextBuilder withUserID(ID id) {
        this.userID = id;
        return this;
    }

    @Override
    public ImmutableContextBuilder withSecurityToken(String token) {
        this.securityToken = token;
        return this;
    }

    @Override
    public ImmutableContextBuilder withApplicationId(String applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    @Override
    public ImmutableContextBuilder withUserLang(String lang) {
        this.userLang = lang;
        return this;
    }

    @Override
    public ImmutableContextBuilder withUserCountry(String country) {
        this.userCountry = country;
        return this;
    }

    @Override
    public ImmutableContextBuilder withRequestCorrelationId(String requestCorrelationId) {
        this.requestCorrelationId = requestCorrelationId;
        return this;
    }

    @Override
    public ImmutableContextBuilder withFunnelCorrelationId(String funnelCorrelationId) {
        this.funnelCorrelationId = funnelCorrelationId;
        return this;
    }

    @Override
    public ImmutableContextBuilder withFunnelName(String funnelName) {
        this.funnelName = funnelName;
        return this;
    }

    @Override
    public ImmutableContextBuilder withFunnelVersion(String funnelVersion) {
        this.funnelVersion = funnelVersion;
        return this;
    }

    @Override
    public ImmutableContextBuilder withIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    @Override
    public ImmutableContextBuilder withSessionCorrelationId(String sessionCorrelationId) {
        this.sessionCorrelationId = sessionCorrelationId;
        return this;
    }

    @Override
    public ImmutableContextBuilder withProperty(String key, Serializable value) {
        if (null == this.properties) {
            this.properties = Maps.newHashMap();
        }
        this.properties.put(key, value);
        return this;
    }

    @Override
    public ImmutableContextBuilder withProperties(Map<String, Serializable> properties) {
        if (null == this.properties) {
            this.properties = Maps.newHashMap();
        }
        this.properties.putAll(properties);
        return this;
    }

    @Override
    public ImmutableContextBuilder withContext(ImmutableDefaultContext context) {
        return this.withApplicationId(context.getApplicationId())
                .withFunnelCorrelationId(context.getFunnelCorrelationId())
                .withFunnelName(context.getFunnelName())
                .withFunnelVersion(context.getFunnelVersion())
                .withIpAddress(context.getIpAddress())
                .withProperties(context.getProperties())
                .withRequestCorrelationId(context.getRequestCorrelationId())
                .withSecurityToken(context.getSecurityToken())
                .withSessionCorrelationId(context.getSessionCorrelationId())
                .withUserCountry(context.getUserCountry())
                .withUserID(context.getUserID().isPresent() ? context.getUserID().get() : null)
                .withUserLang(context.getUserLang());
    }

    @Override
    public ImmutableContextBuilder withValidator(ContextValidator<ImmutableDefaultContext> validator) {
        this.validator = validator;
        return this;
    }

    @Override
    public Optional<ContextValidator<ImmutableDefaultContext>> getValidator() {
        return Optional.fromNullable(validator);
    }

    @Override
    public ImmutableContextBuilder withSequenceIncrement(int sequenceIncrement) {
        this.sequenceIncrement = sequenceIncrement;
        return this;
    }

}
