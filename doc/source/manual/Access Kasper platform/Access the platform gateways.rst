Access the platform gateways
============================

Once you got a reference to the Kasper **Platform** instance, you can ask a reference
to the **Command** and **Query** gateways :

.. code-block:: java
   :linenos:

   // == USING PLATFORM WRAPPERS===============================================

   platform.sendCommand(command, context);

   final MyQueryResult res = platform.retrieve(query, context);

   // == COMMAND GATEWAY ======================================================
   final CommandGateway cgw = platform.getCommandGateway();

   /* Fire and forget */
   cgw.sendCommand(command, context);

   /* Fire and get a Future */
   final Future<CommandResult> f1 = cgw.sendCommandForFuture(command, context);

   /* Wait for result */
   final CommandResult cr = cgw.sendCommandAndWaitForAResult(command, context);

   /* Wait for result and get exceptions */
   final CommandResult cr2 = cgw.sendCommandAndWaitForAResultWithException(command, context);

   /* Wait for command execution */
   cgw.sendCommandAndWait(command, context, timeout, unit);

   // == QUERY GATEWAY ========================================================
   final QueryGateway qgw = platform.getQueryGateway();

   final MyQueryResult res = retrieve(query, context);


TODO: document the different ways to send commands and events from handlers, listeners and query services
