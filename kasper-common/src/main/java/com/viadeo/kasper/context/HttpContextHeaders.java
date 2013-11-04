// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.context;

public class HttpContextHeaders {

    private HttpContextHeaders() { /* Utility class */ }

    public static final String HEADER_SESSION_CORRELATION_ID = "X-KASPER-SESSION-CID";
    public static final String HEADER_FUNNEL_CORRELATION_ID = "X-KASPER-FUNNEL-CID";
    public static final String HEADER_REQUEST_CORRELATION_ID = "X-KASPER-REQUEST-CID";

    public static final String HEADER_USER_ID = "X-KASPER-UID";

    public static final String HEADER_USER_LANG = "X-KASPER-LANG";
    public static final String HEADER_USER_COUNTRY = "X-KASPER-COUNTRY";

    public static final String HEADER_APPLICATION_ID = "X-KASPER-CLIENT-APPID";
    public static final String HEADER_SECURITY_TOKEN = "X-KASPER-SECURITY-TOKEN";

}
