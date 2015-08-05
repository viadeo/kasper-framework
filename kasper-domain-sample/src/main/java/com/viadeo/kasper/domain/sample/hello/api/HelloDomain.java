// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello.api;

import com.viadeo.kasper.api.annotation.XKasperDomain;
import com.viadeo.kasper.api.component.Domain;

@XKasperDomain(
        label = "Hello",                /* Can be used to override the deducted label from class name */
        prefix = "hel",                 /* Mandatory, will be used for some ontology needs (data analysis) */
        description = "Hello domain",
        owner = "Loïc Dias Da Silva <ldiasdasilva@vieaoteam.com>"
)
public class HelloDomain implements Domain {

    // Validation constants ---------------------------------------------------
    public static final int MIN_BUDDY_LENGTH = 3;
    public static final int MAX_BUDDY_LENGTH = 48;
    public static final String REGEX_BUDDY = "[0-9a-zA-Z ]+";

    public static final int MIN_HELLO_LENGTH = 5;
    public static final int MAX_HELLO_LENGTH = 140;
    public static final String REGEX_HELLO = "[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)";

    // Validation messages ----------------------------------------------------
    public static final String NOT_PROVIDED_BUDDY_MSG = "the buddy must be provided";
    public static final String BAD_LENGTH_BUDDY_MSG = "the buddy must have a (3-48) chars length range";
    public static final String INVALID_BUDDY_MSG = "the buddy must be alphanum string";

    public static final String NOT_PROVIDED_HELLO_MSG = "the message must be provided";
    public static final String BAD_LENGTH_HELLO_MSG = "the message must have a (5-140) chars length range";
    public static final String INVALID_HELLO_MSG = "the message contains invalid characters";

}
