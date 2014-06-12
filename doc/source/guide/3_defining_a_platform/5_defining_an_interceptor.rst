
Defining an interceptors
========================

Both side (query or command) have a responsibility chain for which you can add your implementation of interceptor in order
to generalize some behavior to the platform. But you can add interceptor on a specific query handler (see `Using query filter` in :doc:`../2_defining_a_domain/2_defining_the_query_side`).

The easy way to add an interceptor for every handlers is to implement `InterceptorFactory` that will be provided by the
platform configuration (see :doc:`./2_defining_configuration`).


An interceptor interferes before the delegation to the related handler and after the corresponding gateways.

**Defining an query interceptor**

.. code-block:: java
    :linenos:

    public static class InterceptorA implements QueryInterceptor<Query, QueryResult> {
        @Override
        public QueryResponse<QueryResult> process(Query query, Context context, InterceptorChain<Query, QueryResponse<QueryResult>> chain) throws Exception {
            // TODO do something here
            return chain.next(query, context);
        }
    }


**Defining an interceptor**

.. code-block:: java
    :linenos:

    public class CommandValidationInterceptor<C extends Command> extends BaseValidationInterceptor<C> implements Interceptor<C, CommandResponse> {

        public CommandValidationInterceptor(final ValidatorFactory validatorFactory) {
            super(checkNotNull(validatorFactory));
        }

        @Override
        public CommandResponse process(
                final C c,
                final Context context,
                final InterceptorChain<C, CommandResponse> chain) throws Exception {
            validate(c);
            return chain.next(c, context);
        }

    }

