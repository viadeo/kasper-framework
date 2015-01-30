package com.viadeo.kasper.cqrs.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.slf4j.MDC;

import com.viadeo.kasper.context.Context;

public final class MdcUtils {

    @SuppressWarnings("unchecked")
    public static void enrichMdcContextMap(Context context) {
        checkNotNull(context);

        Map initialContextMap = MDC.getCopyOfContextMap();
        Map contextMapEnrichedWithContext = context.asMap(initialContextMap);
        MDC.setContextMap(contextMapEnrichedWithContext);
    }

    private MdcUtils() {
    }

}
