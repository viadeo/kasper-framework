
Wiring a platform
========================

In order to create the platform, you can use the **Platform.Builder**. This builder provides several methods allowing you
to specialize a platform according to your needs.

Thus two choices are available in order to instantiate a Platform. The first way, more easy, consists to initialize the
Builder from a configuration that provides required components for the platform instantiation. The second way is more
verbose but very useful for some case. You should specify manually each required components that you want to have.

.. code-block:: java
    :linenos:

    /* Create a platform from all default components*/
    Platform platform1 = new Platform.Builder(new KasperPlatformConfiguration())
        .build();

    /* Create a platform within specifying components*/
    Platform platform2 = new Platform.Builder()
            .withEventBus(new KasperEventBus())
            .withCommandGateway(new KasperCommandGateway(new KasperCommandBus()))
            .withQueryGateway(new KasperQueryGateway())
            .withConfiguration(ConfigFactory.empty())
            .withMetricRegistry(new MetricRegistry())
            .build();


..  _Create_a_kasper_platform_with_specified_domain:

Create a Kasper platform with specified Domain
................................

The `configure` method will be call by the Builder of the platform for each registered domain bundle.

.. code-block:: java
    :linenos:

    /* Create a platform with a specified domain bundle*/
    Platform platform = new Platform.Builder(new KasperPlatformConfiguration())
        .addDomainBundle(
            new SpringDomainBundle(
                  new TestDomain()
                , Lists.<Class>newArrayList(
                      TestDomainQueryConfiguration.class
                    , TestDomainCommandConfiguration.class
                )
            )
        )
        .build();


..  _Create_a_kasper_platform_with_specified_plugin:

Create a Kasper platform with specified Plugin
................................

The `initialize` method will be call by the Builder of the platform for each registered plugin.

.. code-block:: java
    :linenos:

    /* Create a platform with a specified plugin*/
    Platform platform = new Platform.Builder(new KasperPlatformConfiguration())
        .addPlugin(new MyPlugin())
        .build();