

Using HTTP exposition
========================

..  _Installing_the_HTTP_exposition:

Installing the HTTP exposition
------------------------

The exposition is available as plugins :

- The `HttpQueryExposerPlugin` plugin allows to expose an entry point in order to send command.
- The `HttpCommandExposerPlugin` plugin allows to expose an entry point in order to request query.
- The `HttpEventExposerPlugin` plugin allows to expose an entry point in order to publish event.

We can retrieve HttpServlet from each plugins after their initialization by the wiring of platform.

.. code-block:: java
    :linenos:

    ObjectMapper objectMapper = ObjectMapperProvider.INSTANCE.mapper()

    HttpCommandExposerPlugin httpCommandExposerPlugin = new HttpCommandExposerPlugin(objectMapper);
    HttpQueryExposerPlugin httpQueryExposerPlugin = new HttpQueryExposerPlugin(objectMapper);
    HttpEventExposerPlugin httpEventExposerPlugin = new HttpEventExposerPlugin(objectMapper);

    Platform platform = new Platform.Builder()
        .addPlugin(httpCommandExposerPlugin)
        .addPlugin(httpQueryExposerPlugin)
        .addPlugin(httpEventExposerPlugin)
        .build()

    HttpServlet commandExposerServlet = httpCommandExposerPlugin.getHttpExposer();
    HttpServlet queryExposerServlet = httpQueryExposerPlugin.getHttpExposer();
    HttpServlet eventExposerServlet = httpEventExposerPlugin.getHttpExposer();


Once `Servlet`s have been initialized then they can be added as resource to your server.

..  _Predrefined_Error_codes:

Predefined Error codes
----------------------

For query & command errors some codes have been predefined, but users a free to use new ones, defined codes are mapped to HTTP status codes (defaults to 500).

| REQUIRED_INPUT (400 - BAD REQUEST)
| INVALID_INPUT (400 - BAD REQUEST)
| TOO_MANY_ENTRIES (400 - BAD REQUEST)
| CONFLICT (409 - CONFLICT)
| REQUIRE_AUTHENTICATION (401 - UNAUTHORIZED)
| REQUIRE_AUTHORIZATION (403 - FORBIDDEN)
| INVALID_AUTHENTICATION (400 - BAD REQUEST)
| UNKNOWN_REASON (500 - INTERNAL SERVER ERROR)
| INTERNAL_COMPONENT_TIMEOUT (500 - INTERNAL SERVER ERROR)
| INTERNAL_COMPONENT_ERROR (500 - INTERNAL SERVER ERROR)
| INVALID_ID (400 - BAD REQUEST)
| NOT_FOUND (404 - NOT FOUND)

..  _Context_headers:

Context headers
---------------

The following HTTP headers can be set to set the queries and commands context :

* X-KASPER-SESSION-CID (String) : the client SESSION correlation id used for logging and events
* X-KASPER-FUNNEL-CID (String) : the client FUNNEL (functional tunnel) correlation id used for logging and events
* X-KASPER-REQUEST-CID (String) : the client REQUEST correlation id used for logging and events
* X-KASPER-UID (String) : the USER id concerned by this request if any, used for authorization behaviour
* X-KASPER-CLIENT-APPID (String) : the CLIENT APPLICATION ID used for logging and authorization behaviour
* X-KASPER-LANG (String - ISO 639) : the user language used for strings internationalization (will be removed when Kasper security will be made available)
* X-KASPER-COUNTRY (String - ISO 3166) : the user country (will be removed when Kasper security will be made available)
* X-KASPER-SECURITY-TOKEN (String) : the security token used for authentication
* X-KASPER-FUNNEL-NAME (String) : the funnel name declared by the application during this request
* X-KASPER-FUNNEL-VERSION (String) : the funnel version (declination) declared by the application during this request
* X-Forwarded-For (String) : the client REQUEST ip address
* X-KASPER-SERVER-NAME (String) : the fully qualified domain name of the answering server

**Note:** the security token header can be sent back at any time by the platform in the HTTP response, the client has to detect this header in order to
set back this information in its context/session.