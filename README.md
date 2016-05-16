[![Codacy Badge](https://api.codacy.com/project/badge/grade/0da5a8cc1a3a47c392a3fe06649a8c20)](https://www.codacy.com)
[![Circle CI](https://circleci.com/gh/viadeo/kasper-framework.svg?style=svg&circle-token=82b6a490196b316a6c77e01d33f39d5646372e4f)](https://circleci.com/gh/viadeo/kasper-framework)

![Kasper](doc/source/_static/kasper-logo.png)

KASPER - Kasper is the treasure keeper
======================================

**Viadeo Framework for effective CQRS/DDD architecture**

Parts of Kasper framework are based on meta-framework [Axon](http://www.axonframework.org/)

Kasper framework is currently proposed to the community as-is, there is some work to be done in order to ensure its usability : updated documentation, tutorial, some spring refactoring, a better modules system.

* **kasper-api**                    : API classes
* **kasper-client**                 : Java client to access a Kasper platform
* **kasper-common**                 : Common code to be shared between api and core modules
* **kasper-core**                   : Kasper core : bootstrap, DDD modelisation, CQRS framework
* **kasper-documentation**          : Automated documentation
* **kasper-domain-sample**          : A sample Kasper domain
* **kasper-eventbus-rabbitmq**      : An eventbus implementation using RabbitMQ
* **kasper-exposition-jetty**       : An exposition layer implementation using jetty
* **kasper-exposition**             : Exposition layer and helpers
* **kasper-platform**               : The Kasper platform
* **kasper-spring**                 : Spring module, to wire a Kasper platform using Spring
* **kasper-test**                   : Functional/Integration end to end testing
* **doc**                           : Kasper developers documentation (cf README)
* **tools**                         : Some Kasper developer tools


**If you want to make changes in this framework, please** :

1. Fork the **develop** branch as a private repo
2. Make your changes in a separate branch
3. Rebase your branch on viadeo **develop** branch
4. Submit a pull request on **develop** branch

* [Access to changes](./CHANGES.md)



