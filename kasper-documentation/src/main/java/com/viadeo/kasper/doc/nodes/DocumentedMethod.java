// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import java.util.List;
import java.util.Map;

public class DocumentedMethod {
    private final String name;
    private final List<Map<String,String>> parameters;
    private final String returnType;
    private final String exceptionType;

    // -------------------------------------------------------------

    public DocumentedMethod(String name, List<Map<String, String>> parameters, String returnType, String exceptionType) {
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
        this.exceptionType =exceptionType;
    }

    // -------------------------------------------------------------


    public String getName() {
        return name;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public List<Map<String, String>> getParameters() {
        return parameters;

    }

    public String getReturnType() {
        return returnType;
    }
}
