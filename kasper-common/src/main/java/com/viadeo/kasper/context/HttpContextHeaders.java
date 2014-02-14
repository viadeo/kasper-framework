// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.context;

public final class HttpContextHeaders {

    private HttpContextHeaders() { /* Utility class */ }

    public static final String HEADER_SESSION_CORRELATION_ID = "X-KASPER-SESSION-CID";
    public static final String HEADER_FUNNEL_CORRELATION_ID = "X-KASPER-FUNNEL-CID";
    public static final String HEADER_REQUEST_CORRELATION_ID = "X-KASPER-REQUEST-CID";

    public static final String HEADER_USER_ID = "X-KASPER-UID";

    public static final String HEADER_SERVER_NAME = "X-KASPER-SERVER-NAME";

    public static final String HEADER_USER_LANG = "X-KASPER-LANG";
    public static final String HEADER_USER_COUNTRY = "X-KASPER-COUNTRY";

    public static final String HEADER_APPLICATION_ID = "X-KASPER-CLIENT-APPID";
    public static final String HEADER_SECURITY_TOKEN = "X-KASPER-SECURITY-TOKEN";

    public static final String HEADER_FUNNEL_NAME = "X-KASPER-FUNNEL-NAME";
    public static final String HEADER_FUNNEL_VERSION= "X-KASPER-FUNNEL-VERSION";

    public static final String HEADER_REQUEST_IP_ADDRESS = "X-Forwarded-For";

}
