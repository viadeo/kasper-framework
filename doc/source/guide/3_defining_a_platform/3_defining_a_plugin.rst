
Defining a plugin
========================

A plugin can be considered as an extension of the platform. For that, the framework proposes an interface in order to
define your implementation.

After the wiring of platform is achieved, each registered plugins is initialized.

.. code-block:: java
    :linenos:

    public class MyPlugin implements Plugin {

        @Override
        public void initialize(
              final Platform platform
            , final MetricRegistry metricRegistry
            , final DomainDescriptor... domainDescriptors
        ) {
            // Here your implementation
        }
    }