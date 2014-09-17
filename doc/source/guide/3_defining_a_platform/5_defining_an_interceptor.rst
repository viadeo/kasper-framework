
Defining an interceptor
=======================

Both side (query or command) have a responsibility chain within which you can insert your own interceptors in order
to generalize some behavior to the platform.
But you can also add and interceptor on a specific command or query handler (see `Using query filter` in :doc:`../2_defining_a_domain/2_defining_the_query_side`).

The easy way to add an interceptor for every handlers is to implement `InterceptorFactory` that will be provided by the
platform configuration (see :doc:`./2_defining_configuration`).

An interceptor interferes before the delegation to the related handler and after the corresponding gateways.

**Defining an interceptor for both Query or Command**

.. code-block:: java
    :linenos:

    public class InterceptorA<C extends Object> implements Interceptor<C, Object> {

        public static class Factory implements InterceptorFactory {
            @Override
            public Optional<InterceptorChain> create(TypeToken type) {
                return Optional.of(InterceptorChain.makeChain(new InterceptorA()));
            }
        }

        @Override
        public Object process(C c, Context context, InterceptorChain<C, Object> chain) throws Exception {
            validate(c);
            return chain.next(c, context);
        }
    }

