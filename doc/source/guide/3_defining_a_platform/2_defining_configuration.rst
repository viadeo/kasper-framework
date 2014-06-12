
Defining configuration
========================

By default the framework use `KasperPlatformConfiguration` as default implementation of `PlatformConfiguration`.

This implementation provide :

- an asynchronous event bus in memory,
- an empty TypeSafe configuration,
- no extra components,
- a metric registry,
- query and command gateways,
- security, cache, validation and filter interceptor factories,
- security, validation interceptor factories,

Of course you can implement yourself a configuration according to your need. You should specify it during wiring.