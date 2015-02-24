# Kasper Releases #

### Snapshot 0.8.0-pre28 ([Nexus](https://nexus.viadeobackoffice.com/index.html#nexus-search;gav~com.viadeo.kasper~~0.8.0-pre2~~))

* Added Authorization's annotations and auto-doc.

### Snapshot 0.8.0-pre27 ([Nexus](https://nexus.viadeobackoffice.com/index.html#nexus-search;gav~com.viadeo.kasper~~0.8.0-pre27~~))

* Added User-Agent into context's properties.

### Snapshot 0.8.0-pre26 ([Nexus](https://nexus.viadeobackoffice.com/index.html#nexus-search;gav~com.viadeo.kasper~~0.8.0-pre26~~))

_(based on 0.6.9, the recovery from the 0.7 is in progress)_

* Migrated to Gradle 2.1
* Ready to be used with circle-ci
* [Pull 233](https://github.com/viadeo/kasper-framework/pull/233) New documentation
* [Pull 234](https://github.com/viadeo/kasper-framework/pull/234) Various bug fix on auto documentation
  - List every events instead of event referenced by aggregate and/or listener
  - Propose aliases in the result of the auto-completion
  - Review the navigation and particular the behavior with back and previous
  - Fix trouble with collapsible component block
  - Ensure to load query results before queries
  - Be able to provide detail of an object in more of query result
  - Sort collection of commands, queries and events alphabetically
  - Fix trouble with search component
* [Pull 235](https://github.com/viadeo/kasper-framework/pull/235) Ensure to add only one RetryFilter on  the Kasper client in order to avoid stack overflow
* [Pull 236](https://github.com/viadeo/kasper-framework/pull/236) Reduce log level of DomainHelper to debug
* [Pull 238](https://github.com/viadeo/kasper-framework/pull/238) Migrated to Axon framework 2.2
* [Pull 241](https://github.com/viadeo/kasper-framework/pull/241) Improve platform fixture
  - allow to verify that a listener is notified 
  - allow to verify that no listeners are notified
  - allow to verify from an emitted event that a sequence of commands are sent
  - allow to verify from an sent command that a sequence of commands are sent
* [Pull 239](https://github.com/viadeo/kasper-framework/pull/239) Expose the kasper version :
  - set as attribute in the header when we use the kasper client
  - set in the MDC wen we process an HTTP request in the exposition layer
* [Pull 237](https://github.com/viadeo/kasper-framework/pull/237) Clean security configuration and added authorization's annotations
* [Pull 244](https://github.com/viadeo/kasper-framework/pull/244) Deal the context properly in the MDC
* [Pull 243](https://github.com/viadeo/kasper-framework/pull/243) Improve security autodoc
* [Pull 242](https://github.com/viadeo/kasper-framework/pull/242) Refactoring interceptor responsability chain in order to homogenize the way to retrieve security information. All security information must have on handler.
* [Pull 247](https://github.com/viadeo/kasper-framework/pull/247) Added `ID` as new implementation of `KasperID`
* [Pull 249](https://github.com/viadeo/kasper-framework/pull/249) Added header `X-KASPER-CALL-TYPE` allowing to specify the type of a command call. Expected value are 'sync'|'async'|'time(x)' with x in milliseconds
* [Pull 251](https://github.com/viadeo/kasper-framework/pull/251) Add request duration time to MDC logs
* [Pull 252](https://github.com/viadeo/kasper-framework/pull/252) Added possibility to declare an handler as unexposed in using `XKasperUnexposed` annotation
* Fix the behavior of the sendCommand method in order to fire and forget a command call
* Added extract context duration time to MDC logs
* Added extract input duration time to MDC logs
* [Pull 255](https://github.com/viadeo/kasper-framework/pull/255) Update metrics dependency in order to use the java library provided by dropwizard 
* [Pull 262][AC-105] Add tags on logs to be able to find it easily in Kibana

### Version 0.7 (23/07/2014) ([Nexus](http://nexus01.infra.paris.apvo/index.html#nexus-search;gav~com.viadeo.kasper~~0.7~~)) _:warning: abandoned_###

* :white_check_mark: [Pull 228](https://github.com/viadeo/kasper-framework/pull/228) Migrated to Axon framework 2.2
* :white_check_mark: [Pull 211](https://github.com/viadeo/kasper-framework/pull/211) Ensure to add only one RetryFilter on  the Kasper client in order to avoid stack overflow
* :white_check_mark: [Bug 220](https://github.com/viadeo/kasper-framework/pull/220) (auto-doc) List every events instead of event referenced by aggregate and/or listener
* :white_check_mark: [Bug 216](https://github.com/viadeo/kasper-framework/pull/216) (auto-doc) Propose aliases in the result of the auto-completion
* :white_check_mark: (auto-doc) Review the navigation and particular the behavior with back and previous
* :white_check_mark: (auto-doc) Fix trouble with collapsible component block
* [Pull 223](https://github.com/viadeo/kasper-framework/pull/223) Hystrix feature on command and query gateways
* :white_check_mark: [Pull 214](https://github.com/viadeo/kasper-framework/pull/214) Reduce log level of DomainHelper to debug
* [Pull 205](https://github.com/viadeo/kasper-framework/pull/205) Use ObjectMapper implementation to ser/deser query
* [Pull 201](https://github.com/viadeo/kasper-framework/pull/201) Provide a deserializer of enum allowing to manage with unsensitive case
* :white_check_mark: [Pull 195](https://github.com/viadeo/kasper-framework/pull/195) Improve platform fixture
  - allow to verify that a listener is notified 
  - allow to verify that no listeners are notified
  - allow to verify from an emitted event that a sequence of commands are sent
  - allow to verify from an sent command that a sequence of commands are sent
* :white_check_mark: [Pull 207](https://github.com/viadeo/kasper-framework/pull/207) Expose the kasper version :
  - set as attribute in the header when we use the kasper client
  - set in the MDC wen we process an HTTP request in the exposition layer


### Version 0.6.9 (02/09/2014) ([Nexus](http://nexus01.infra.paris.apvo/index.html#nexus-search;gav~com.viadeo.kasper~~0.6.9~~))###

* generalized InterceptorFactories register methodes


### Version 0.6.8 (19/08/2014) ([Nexus](http://nexus01.infra.paris.apvo/index.html#nexus-search;gav~com.viadeo.kasper~~0.6.8~~)) _:warning: abandoned_###


### Version 0.6.7 (19/08/2014) ([Nexus](http://nexus01.infra.paris.apvo/index.html#nexus-search;gav~com.viadeo.kasper~~0.6.7~~))###

* Produces a warning instead of an error when the exposure manages a refused response
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
