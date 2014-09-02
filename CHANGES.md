# Kasper Releases #

### Version 0.6.8 (02/09/2014) ([Nexus](http://nexus01.infra.paris.apvo/index.html#nexus-search;gav~com.viadeo.kasper~~0.6.8~~))###

* generalized InterceptorFactories register methodes

### Version 0.6.7 (19/08/2014) ([Nexus](http://nexus01.infra.paris.apvo/index.html#nexus-search;gav~com.viadeo.kasper~~0.6.7~~))###

* Review SerDe of a KasperID:
  - unlink serializer / deserializer to `KasperID` interface
  - link DefaultKasperId<Serializer|deserializer> to `DefaultKasperID`
  - link DefaultKasperRelationId<Serializer|deserializer> to `DefaultKasperRelationID`
  - provide an adapter in order to try to deserialize `KasperID` with `DefaultKasperId` in order to ensure retro-compatibility


### Version 0.6.6 (18/08/2014) ([Nexus](http://nexus01.infra.paris.apvo/index.html#nexus-search;gav~com.viadeo.kasper~~0.6.6~~))###

* Produces a warning instead of an error when the exposure manages a refused response

### Version 0.6.5 (08/07/2014) ([Nexus](http://nexus01.infra.paris.apvo/index.html#nexus-search;gav~com.viadeo.kasper~~0.6.5~~))###
* [Pull 221](https://github.com/viadeo/kasper-framework/pull/221) Add authentication's CoreReasonCodes
* Remove 'println' during the shutdown the event bus
* Call shutdown on publication handlers
* Set 'uowEventId' and 'persistencyType' as transient fields in Event


### Version 0.6.4 (06/04/2014) ([Nexus](http://nexus01.infra.paris.apvo/index.html#nexus-search;gav~com.viadeo.kasper~~0.6.4~~))###
* [Improvement] Enrich light documentation with aliases


### Version 0.6.3 (05/22/2014) ([Nexus](http://nexus01.infra.paris.apvo/index.html#nexus-search;gav~com.viadeo.kasper~~0.6.3~~))###
* [Bug] Reduce level of security logger to warn


### Version 0.6.2 (05/21/2014) ([Nexus](http://nexus01.infra.paris.apvo/index.html#nexus-search;gav~com.viadeo.kasper~~0.6.2~~))###
* Temporary commented return error during interceptor checks and put log instead


### Version 0.6.1 (04/30/2014) ([Nexus](http://nexus01.infra.paris.apvo/index.html#nexus-search;gav~com.viadeo.kasper~~0.6.1~~))###
* [Improvement] Allow platform user to register publication handlers for events in the KasperEventBus


### Version 0.6 (04/09/2014) ([Nexus](http://nexus01.infra.paris.apvo/index.html#nexus-search;gav~com.viadeo.kasper~~0.6~~))###
* [Bug 198](https://github.com/viadeo/kasper-framework/pull/198) Fix issue around the validation of the CoreReasonCode in the fixture
* [Pull 196](https://github.com/viadeo/kasper-framework/pull/196), [Pull 199](https://github.com/viadeo/kasper-framework/pull/199), [Pull 202](https://github.com/viadeo/kasper-framework/pull/202) New user interface of the auto documentation
* [Pull 191](https://github.com/viadeo/kasper-framework/pull/191) enrich MDC, normalize hierarcy of default security strategy
* [Pull 131](https://github.com/viadeo/kasper-framework/pull/131) Use `XKasperAlias` on input object instead of the related handler.
* [Pull 105](https://github.com/viadeo/kasper-framework/pull/105) Rename module from `kviadeo-web` to `kviadeo-expostion`
* [Pull 182](https://github.com/viadeo/kasper-framework/pull/182) Improve documentation : 
  - provide more information on an element of a collection result
  - fix the behavior with the tree
* [Pull 97](https://github.com/viadeo/kasper-framework/pull/97) Provide and display a list of constraints for each property of a bean


### Version 0.5.1 (03/20/2014) ([Nexus](http://nexus01.infra.paris.apvo/index.html#nexus-search;gav~com.viadeo.kasper~~0.5.1~~))###
* [Bug] bad metrics names are used in http exposers


### Version 0.5 (03/19/2014) ([Nexus](http://nexus01.infra.paris.apvo/index.html#nexus-search;gav~com.viadeo.kasper~~0.5~~))###
* [Pull 128](https://github.com/viadeo/kasper-framework/pull/128) Emerge a common exposure base in order to manage properly the deserialization with jackson of a query in case of a POST.
* [Improvement 185](https://github.com/viadeo/kasper-framework/pull/185) Add shutdown hook in order to flush published events in bus
* [Improvement 186](https://github.com/viadeo/kasper-framework/pull/186) Add SecurityConfiguration to KasperPlatformConfiguration :
    + Add Query/Command Interceptors :
    - `kasper.security.callback.ApplicationIdValidator`
    - `kasper.security.callback.IdentityContextProvider`
    - `kasper.security.callback.IpAddressValidator`
    - `kasper.security.callback.SecurityTokenValidator`
    + Add Public/Private resources security strategies:
    - `kasper.security.annotation.XKasperPublic`


### Version 0.4 (03/06/2014, [Nexus](http://nexus01.infra.paris.apvo/index.html#nexus-search;gav~com.viadeo.kasper~~0.4~~))###
* [Improvement X] Add a domain owner field in XKasperDomain annotation
* [Improvement 184](https://github.com/viadeo/kasper-framework/issues/184) Add `getUserLangAsLocal` method in `Context` class
* [Improvement 181](https://github.com/viadeo/kasper-framework/issues/181) Manage natively immutable object :warning: Jackson dependencies was updated to 2.3.0 version
* [Improvement 177](https://github.com/viadeo/kasper-framework/issues/177) Introduce the new Kasper responses format
* [Improvement 170](https://github.com/viadeo/kasper-framework/issues/170) Add new metrics :
  + Add three new metrics for each side of a domain
    - `<domain>.<command|query>.requests-time`
    - `<domain>.<command|query>.requests`
    - `<domain>.<command|query>.errors`
  + Add two new metrics per side of a client
    - `client.<applicationId>.<command|query>.requests`
    - `client.<applicationId>.<command|query>.errors`
* [Improvement 179](https://github.com/viadeo/kasper-framework/pull/179) Update metrics :
  + Delete metrics :
    - `kasper.cqrs.query.impl.KasperQueryGateway.requests-times`
    - `<domain>.query.<name>.request-times`
  + Add metrics :
    - `kasper.cqrs.command.KasperUnitOfWork.commited`
    - `<domain>.eventlistener.<name>.committed`
    - `<domain>.eventlistener.committed`

### Version 0.3.9 (02/14/2014, [Nexus](http://nexus01.infra.paris.apvo/index.html#nexus-search;gav~com.viadeo.kasper~~0.3.9~~))###

* [Improvement 174](https://github.com/viadeo/kasper-framework/issues/174) Add the fully qualified domain name of the answering server as HTTP header by the exposition entries points : `X-KASPER-SERVER-NAME`
* [Bug 172](https://github.com/viadeo/kasper-framework/issues/172) Fix on the global metric name related to `EventListener` component
* [Bug 176](https://github.com/viadeo/kasper-framework/pull/176) UnitOfWork committed even if the handler returns an error


### Version 0.3.8 (02/03/2014, [Nexus](http://nexus01.infra.paris.apvo/index.html#nexus-search;gav~com.viadeo.kasper~~0.3.8~~))###

* [Bug 167](https://github.com/viadeo/kasper-framework/issues/167) Context is lost between handlers and listeners - Always override thread context with event context on listening


### Version 0.3.7 (01/30/2014, [Nexus](http://nexus01.infra.paris.apvo/index.html#nexus-search;gav~com.viadeo.kasper~~0.3.7~~))###

* [Pull 166](https://github.com/viadeo/kasper-framework/pull/166) Fix problem on a bad previous merge around the usage of the domain helper


### Version 0.3.6 (01/24/2014, [Nexus](http://nexus01.infra.paris.apvo/index.html#nexus-search;gav~com.viadeo.kasper~~0.3.6~~))###

* [Pull 164](https://github.com/viadeo/kasper-framework/pull/164) Tools : update docPath.sh to be usable with the new doc platform
* [Pull 163](https://github.com/viadeo/kasper-framework/pull/163) Remove dependency to axon-mongo


### Version 0.3.5 (01/21/2014, [Nexus](http://nexus01.infra.paris.apvo/index.html#nexus-search;gav~com.viadeo.kasper~~0.3.5~~))###

* [Pull 160](https://github.com/viadeo/kasper-framework/pull/160) Test coverage with jacoco


### Version 0.3.4 (01/16/2014, [Nexus](http://nexus01.infra.paris.apvo/index.html#nexus-search;gav~com.viadeo.kasper~~0.3.4~~))###

* [Pull 158](https://github.com/viadeo/kasper-framework/pull/158) Minor Kasper framework's code conventions review
* [Pull 157](https://github.com/viadeo/kasper-framework/pull/158) Avoid to build systematically a platform at each given of the fixture in order to avoid some trouble with a SpringDomainBundle


### Version 0.3.3 (01/16/2014, [Nexus](http://nexus01.infra.paris.apvo/index.html#nexus-search;gav~com.viadeo.kasper~~0.3.3~~))###

* [Pull 156](https://github.com/viadeo/kasper-framework/pull/156) Fix some regression with the ui of the auto documentation
