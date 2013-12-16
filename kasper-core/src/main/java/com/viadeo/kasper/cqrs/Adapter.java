package com.viadeo.kasper.cqrs;

import com.viadeo.kasper.context.Context;

/**
* Defines the method used to adapt an element. This interface is used into the framework in order to provide specialization
* that is used by domain in order to write a custom adapting code.
*/
public interface Adapter<ELEM> {

    /**
     * Adapt the element before use
     *
     * @param context the context used with the element
     * @param input   the element to be adapted
     * @return the adapted element
     */
    ELEM adapt(final Context context, final ELEM input);

    /**
     * Return the name of the adapter
     *
     * @return the adapter name
     */
    String getName();
 }