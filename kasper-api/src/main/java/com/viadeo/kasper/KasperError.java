package com.viadeo.kasper;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Optional;

public final class KasperError {
    public static final String REQUIRED_INPUT = "REQUIRED_INPUT";
    public static final String INVALID_INPUT = "INVALID_INPUT";
    public static final String TOO_MANY_ENTRIES = "TOO_MANY_ENTRIES";
    public static final String CONFLICT = "CONFLICT";
    public static final String REQUIRE_AUTHENTICATION = "REQUIRE_AUTHENTICATION";
    public static final String REQUIRE_AUTHORIZATION = "REQUIRE_AUTHORIZATION";
    public static final String UNKNOWN_ERROR = "UNKNOWN_ERROR";
    public static final String INTERNAL_COMPONENT_TIMEOUT = "INTERNAL_COMPONENT_TIMEOUT";
    public static final String INTERNAL_COMPONENT_ERROR = "INTERNAL_COMPONENT_ERROR";
    public static final String INVALID_ID = "INVALID_ID";
    
    private final String code;
    private final String message;
    private final Optional<String> userMessage;
    
    public KasperError(String code, String message) {
        this(code, message, null);
    }
    
    public KasperError(String code, String message, String userMessage) {
        this.code = checkNotNull(code);
        this.message = checkNotNull(message);
        this.userMessage = Optional.fromNullable(userMessage);
    }
    
    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Optional<String> getUserMessage() {
        return userMessage;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + ((userMessage == null) ? 0 : userMessage.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        KasperError other = (KasperError) obj;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        if (userMessage == null) {
            if (other.userMessage != null)
                return false;
        } else if (!userMessage.equals(other.userMessage))
            return false;
        return true;
    }
}