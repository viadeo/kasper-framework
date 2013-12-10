Access the platform gateways
============================

Once you got a reference to the Kasper **Platform** instance, you can ask a reference
to the **Command** and **Query** gateways and to the **Event** bus :

.. code-block:: java
   :linenos:

   // == EVENT BUS ===============================================
   final KasperEventBus eb = platform.getEventBus();

   /* Fire an event */
   eb.publish(event);

   // == COMMAND GATEWAY ======================================================
   final CommandGateway cgw = platform.getCommandGateway();

   /* Fire and forget */
   cgw.sendCommand(command, context);

   /* Fire and get a Future */
   final Future<CommandResponse> f1 = cgw.sendCommandForFuture(command, context);

   /* Wait for response */
   final CommandResponse cr = cgw.sendCommandAndWaitForAResponse(command, context);

   /* Wait for response and get exceptions */
   final CommandResponse cr2 = cgw.sendCommandAndWaitForAResponseWithException(command, context);

   /* Wait for command execution */
   cgw.sendCommandAndWait(command, context, timeout, unit);

   // == QUERY GATEWAY ========================================================
   final QueryGateway qgw = platform.getQueryGateway();

   /* Retrieve a result */
   final QueryResponse<MyQueryResult> res = qgw.retrieve(query, context);

From a **Command Handler** instance, you can retrieve a **Command** gateway and/or publish an **Event** using the current
unit of work :

.. code-block:: java
   :linenos:

    // == COMMAND HANDLER ===============================
    final CommandGateway cgw = commandHandler.getCommandGateway()

    /* Fire and forget */
    cgw.sendCommand(command, context);

    /* Fire and get a Future */
    final Future<CommandResponse> f1 = cgw.sendCommandForFuture(command, context);

    /* Wait for response */
    final CommandResponse cr = cgw.sendCommandAndWaitForAResponse(command, context);

    /* Wait for response and get exceptions */
    final CommandResponse cr2 = cgw.sendCommandAndWaitForAResponseWithException(command, context);

    /* Wait for command execution */
    cgw.sendCommandAndWait(command, context, timeout, unit);

    // == EVENT PUBLISHING ===============================
    commandHandler.publish(event)

From a **Query Handler** instance, you can retrieve a **Query** gateway and/or publish an **Event** using the bus event :

.. code-block:: java
   :linenos:

    // == QUERY HANDLER ===============================
    final QueryGateway qgw = queryHandler.getQueryGateway();

    final QueryResponse<MyQueryResult> res = qgw.retrieve(query, context);

    // == EVENT PUBLISHING ===============================
    queryHandler.publish(event)

TODO: document the different ways to send commands and events from listeners handlers
