Talking with Kasper
========================


..  _Using_gateways:

Using gateways
------------------------

Once you got a reference to the Kasper **Platform** instance, you can ask a reference
to the **Command** and **Query** gateways and to the **Event** bus.

From a **Command Handler** instance, you can retrieve a **Command** gateway and/or publish an **Event** using the current
unit of work.

From a **Query Handler** instance, you can retrieve a **Query** gateway and/or publish an **Event** using the bus event :


**send a command**

.. code-block:: java
   :linenos:

   final CommandGateway commandGateway = platform.getCommandGateway();

  /* Fire and forget */
  commandGateway.sendCommand(command, context);

  /* Fire and get a Future */
  final Future<CommandResponse> f1 = commandGateway.sendCommandForFuture(command, context);

  /* Wait for response */
  final CommandResponse cr = commandGateway.sendCommandAndWaitForAResponse(command, context);

  /* Wait for response and get exceptions */
  final CommandResponse cr2 = commandGateway.sendCommandAndWaitForAResponseWithException(command, context);

  /* Wait for command execution */
  commandGateway.sendCommandAndWait(command, context, timeout, unit);


**request a query result**

.. code-block:: java
   :linenos:

   final QueryGateway queryGateway = platform.getQueryGateway();

   /* Retrieve a result */
   final QueryResponse<MyQueryResult> res = queryGateway.retrieve(query, context);


**emit an event**

.. code-block:: java
   :linenos:

   platform.getEventBus().publish(new UserCreatedEvent());


..  _Using_http_exposition:

Using HTTP exposition
------------------------

Kasper framework provides an exposition component allowing to automatically expose commands and queries.
Actually it is an HTTP exposition exchanging JSON messages + standard HTTP Headers and implemented via Java Servlets and a
databinding component mapping query strings and query POJOS.


**Commands & CommandResponse**

Commands are submitted using **POST** or **PUT** requests, there are no query parameters, everything is in the body.
Actually only json content is supported as input and output.

To enable Command exposition register **HttpCommandExposer** servlet.

Ex: the following command would be exposed at **http://host:port/someRootPath/createMember**

.. code-block:: java
   :linenos:

   class CreateMemberCommand implements Command {
     String name;
     // other fields

     // getters
   }

this command serialized in its json form would look like :

.. code-block:: javascript
    :linenos:

    {
        "name": "john"
    }

In response you receive a json of the following form.

.. code-block:: javascript
    :linenos:

    {
        status: "ERROR" // values can be : OK, ERROR or REFUSED
        id: "1124d9e8-6266-4bcf-8035-37a02ba75c69",
        code: 1006, // a mandatory code, describing what is wrong
        label: "NOT_FOUND", // a mandatory human readable code, describing what is wrong
        reason: true, // set to true if we have reasons, false otherwise
        reasons: [
            {
                "message": "user already exists", // a message, providing more information on what happened
            }
            // other reasons
        ]
    }


**Queries & Responses**

A query is submitted using a **GET** or **POST** requests, the parameters will be respectively set in the query string with a **GET** request
or in the body with a **POST** request.

Using GET is handy with tools such as curl or your favorite browser. Of course it imposes restrictions on having flat/simple queries and
limited query size.

To enable Query exposition register HttpQueryExposer servlet.

Ex: suppose we have the following query, it will be **available using GET at http://host:port/someRootPath/getMemberMessages?memberId=999**.

.. code-block:: java
    :linenos:

    class GetMemberMessagesQuery implements Query {
        int memberId;
        Date startingFrom;

        // getters
    }

Query objects will be flattened by the framework to a query string, you should **avoid having complex structures**.

The framework also **supports deserialization to objects that don't have a default no arg constructor**

In case of an error a standard HTTP error code will be set with the reason for this error in the headers and the body will contain (optionally) more
information on what happened, see :doc:`3_defining_a_platform/8_using_http_exposition`.

.. code-block:: javascript
    :linenos:

    {
        status: "ERROR" // values can be : OK, ERROR or REFUSED
        id: "1124d9e8-6266-4bcf-8035-37a02ba75c69",
        code: 1002, // a mandatory code, describing what is wrong
        label: "INVALID_INPUT", // a mandatory human readable code, describing what is wrong
        reason: true, // set to true if we have reasons, false otherwise
        reasons: [
            {
                "message": "Wrong email address?", // a message, providing more information on what happened
            }
            // other reasons
        ]
    }

In case of a success a query Response will be returned serialized to json, this is done with Jackson. That allows you to use standard Jackson
annotations on your query Response (if you want to use constructors with args for example).


**Events**

Events can be emitted to the platform using **POST** or **PUT** requests, there are no query parameters, everything is in the body.
Actually only json content is supported as input and output.

To enable Command exposition register **HttpEventExposer** servlet.

Warning: Domain events exposing is an anti-pattern of the platform's spirit in itself, this endpoint is provided as a migration helper when dealing with a
legacy platform allowing a smooth decoupling : the legacy platform can then send domain events in place of the not-yet-implemented platform's
domain to come.


..  _Using_kasper_client:

Using Kasper client
------------------------

The idea behind the Kasper client library is to provide a very easy and powerful API to hit exposed Kasper implementations. This requires
kasper implementations to share their Query and Commands code with JVM based consumers. This allows the library to only require one liner code
taking as arguments populated queries and commands (basic POJOs), the library takes care of all the ser/deser stuff, error handling,
asynchronous calls and more.

They share some common code in order to ensure that the way in which the communication is done is symmetric
(ex: what can be serialized can also be deserialized). For example all the databinding part for query is
shared between kasper-client and kasper-exposition.

.. seealso::

   This library works with the automated HTTP exposition (see :doc:`3_defining_a_platform/8_using_http_exposition`) component enabled on the server side,
   you must enable it server-side in order to be able to communicate.

   Kasper http exposition and Kasper client share some common code (:doc:`3_defining_a_platform/7_serialization_and_deserialization` and jackson configuration), to ensure that the way in which the communication
   is done is symmetric (ex: what can be serialized can also be deserialized).

The main entry point of the library is the **KasperClient** class, it provides all the required methods to communicate with exposed Kasper implementations.

KasperClient is thread safe and should be reused for optimal performances.

**Sending a query** is as simple as :

.. code-block:: java
   :linenos:

   final Context context = ...
   final KasperClient client = new KasperClient();
   final QueryResponse<SuperCoolResponse> response =
                client.query(context, new SuperCoolQuery("what's up?"), SuperCoolResponse.class);
   if ( ! response.isOK()) {
    KasperReason error = response.getReason();
    // do something using the error code or the messages list
   } else {
    // if no error occured you can access the result
     SuperCoolResponse result = response.get();
   }

If an error occurred during query processing on client side a **KasperQueryException** will be raised, if something goes wrong on server side then a QueryResponse with an error is returned.

.. note::

   By default KasperClient is configured to hit queries at **http://localhost:8080/query** and commands at **http://localhost:8080/command**.

   This can be configured using **KasperClientBuilder**.

   .. code-block:: java
      :linenos:

      final KasperClient client = new KasperClientBuilder()
                                        .queryBaseLocation("http://kasper-platform/query")
                                        .commandBaseLocation("http://kasper-platform/command")
                                        .create();

**Sending a command** is also quite simple:

.. code-block:: java
   :linenos:

   final CommandResponse response = client.send(context, new ICommandYouTo("Enjoy Coding!"));
   if ( ! response.isOK()) {
      // do something useful with response.getReason()
   }


**Emitting an event** is the same stuff :

.. code-block:: java
   :linenos:

   client.emit(context, new MyEvent("Enjoyed Coding!"));

A **KasperException** will be thrown on error.