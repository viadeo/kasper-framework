
=======
Metrics
=======

Kasper framework uses `Yammer's Metrics library <http://metrics.codahale.com/>`_ to manage its provided
metrics and encourages you to use it in your platform developments.

Configuration
-------------

Kasper framework uses a global **MetricRegistry** used to record all provided metrics.

This **MetricRegistry** has to be set up in one or several Metrics reporters.

The framework defines by default an SLF4J reporter with log level set to TRACE, through the **Platform**
class logger, publishing metrics every 20 seconds.

You can override or augment this configuration in your own **PlatformConfiguration** (see :ref:`Configuration`),
using the global registry available through **KasperMetrics** :

.. code-block:: java
    :linenos:

    public class MyPlatformConfiguration extends DefaultPlatformConfiguration {

        @Override
        public void initializeMetricsReporters() {
            /* if you want to keep the default SLF4J one */
            super.initializeMetricsReporters();

            /* Add your own reporter (Graphite, CSV, Console, etc..) */
            final ConsoleReporter reporter = ConsoleReporter.forRegistry(KasperMetrics.getRegistry())
                                                            .convertRatesTo(TimeUnit.SECONDS)
                                                            .convertDurationsTo(TimeUnit.MILLISECONDS)
                                                            .build();
            reporter.start(1, TimeUnit.MINUTES);

            /* Sets an optional prefix for metrics names */
            KasperMetrics.setNamePrefix(System.getenv("PLATFORM_ENV"));
        }

    }

Core provided metrics
---------------------

The following table lists all metrics directly provided by the framework :

+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Location        | Type      | Name                                                                 | Description                                                 |
+=================+===========+======================================================================+=============================================================+
| Command gateway | Timer     | <Command Class>.**requests-time**                                    | Time to handle the command                                  |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Command gateway | Timer     | com.viadeo.kasper.cqrs.command.CommandGateway.**requests-time**      | Time to handle the command (all)                            |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Command gateway | Histogram | <Command Class>.**requests-times**                                   | Distribution of request handling time for this command      |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Command gateway | Histogram | com.viadeo.kasper.cqrs.command.CommandGateway.**requests-times**     | Distribution of request handling time for all commands      |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Command gateway | Meter     | <Command Class>.**requests**                                         | Rate of requests for this command                           |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Command gateway | Meter     | com.viadeo.kasper.cqrs.command.CommandGateway.**requests**           | Rate of requests for all commands                           |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Command gateway | Meter     | <Command Class>.**errors**                                           | Rate of failed requests for this command                    |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Command gateway | Meter     | com.viadeo.kasper.cqrs.command.CommandGateway.**errors**             | Rate of failed requests for all commands                    |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Query gateway   | Timer     | <Query Class>.**requests-time**                                      | Time to handle the query                                    |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Query gateway   | Timer     | com.viadeo.kasper.cqrs.query.QueryGateway.**requests-time**          | Time to handle the query (all)                              |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Query gateway   | Timer     | <Query Class>.**requests-result-filters-time**                       | Time to filter the query filters                            |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Query gateway   | Timer     | <Query Class>.**requests-query-filters-time**                        | Time to filter the query result                             |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Query gateway   | Histogram | <Query Class>.**requests-times**                                     | Distribution of request handling time for this query        |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Query gateway   | Histogram | com.viadeo.kasper.cqrs.query.QueryGateway.**requests-times**         | Distribution of request handling time for all queries       |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Query gateway   | Meter     | <Query Class>.**requests**                                           | Rate of requests for this query                             |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Query gateway   | Meter     | com.viadeo.kasper.cqrs.query.QueryGateway.**requests**               | Rate of requests for all queries                            |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Query gateway   | Meter     | <Query Class>.**errors**                                             | Rate of failed requests for this query                      |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Query gateway   | Meter     | com.viadeo.kasper.cqrs.query.QueryGateway.**errors**                 | Rate of failed requests for all queries                     |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Repository      | Timer     | <Repository Class>.**save-time**                                     | Time to save entity for this repository                     |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Repository      | Histogram | <Repository Class>.**save-times**                                    | Distribution of time to save for this repository            |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Repository      | Histogram | com.viadeo.kasper.ddd.IRepository.**save-times**                     | Distribution of time to save for all repositories           |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Repository      | Meter     | <Repository Class>.**saves**                                         | Rate of save operations for this repository                 |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Repository      | Meter     | com.viadeo.kasper.ddd.IRepository.**saves**                          | Rate of save operations for all repositories                |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Repository      | Meter     | <Repository Class>.**save-errors**                                   | Rate of errors during save operations for all repositories  |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Repository      | Meter     | com.viadeo.kasper.ddd.IRepository.**save-errors**                    | Rate of errors during save operations for this repository   |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Repository      | Timer     | <Repository Class>.**load-time**                                     | Time to load entity for this repository                     |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Repository      | Histogram | <Repository Class>.**load-times**                                    | Distribution of time to load for this repository            |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Repository      | Histogram | com.viadeo.kasper.ddd.IRepository.**load-times**                     | Distribution of time to load for all repositories           |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Repository      | Meter     | <Repository Class>.**loads**                                         | Rate of load operations for this repository                 |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Repository      | Meter     | com.viadeo.kasper.ddd.IRepository.**loads**                          | Rate of load operations for all repositories                |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Repository      | Meter     | <Repository Class>.**load-errors**                                   | Rate of errors during load operations for all repositories  |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Repository      | Meter     | com.viadeo.kasper.ddd.IRepository.**load-errors**                    | Rate of errors during load operations for this repository   |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Repository      | Timer     | <Repository Class>.**delete-time**                                   | Time to delete for this repository                          |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Repository      | Histogram | <Repository Class>.**delete-times**                                  | Distribution of time to delete for this repository          |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Repository      | Histogram | com.viadeo.kasper.ddd.IRepository.**delete-times**                   | Distribution of time to delete for all repositories         |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Repository      | Meter     | <Repository Class>.**deletes**                                       | Rate of delete operations for this repository               |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Repository      | Meter     | com.viadeo.kasper.ddd.IRepository.**deletes**                        | Rate of delete operations for all repositories              |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Repository      | Meter     | <Repository Class>.**delete-errors**                                 | Rate of errors during delete operations for all repositories|
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Repository      | Meter     | com.viadeo.kasper.ddd.IRepository.**delete-errors**                  | Rate of errors during delete operations for this repository |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Event listeners | Timer     | <Listener Class>.**handle-time**                                     | Time to handle an listened event                            |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Event listeners | Histogram | <Listener Class>.**handle-times**                                    | Distribution of time to handle an event for this listener   |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Event listeners | Histogram | com.viadeo.kasper.event.EventListener.**handle-times**               | Distribution of time to handle an event for all listeners   |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Event listeners | Meter     | <Listener Class>.**handles**                                         | Rate of event handling operations for this listener         |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Event listeners | Meter     | com.viadeo.kasper.event.EventListener.**handles**                    | Rate of event handling operations for all listeners         |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Event listeners | Meter     | <Listener Class>.**errors**                                          | Rate of errors handling events for this listener            |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+
| Event listeners | Meter     | com.viadeo.kasper.event.EventListener.**errors**                     | Rate of errors handling events for all listeners            |
+-----------------+-----------+----------------------------------------------------------------------+-------------------------------------------------------------+

Use Metrics in your developments
--------------------------------

Get the global **MetricRegistry** using **KasperMetrics.getRegistry()** and simply use it as explained in
the `Metrics documentation <http://metrics.codahale.com/>`_.

