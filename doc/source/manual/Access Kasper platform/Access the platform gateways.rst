Access the platform gateways
============================

Once you got a reference to the Kasper **Platform** instance, you can ask a reference
to the **Command** and **Query** gateways :

.. code-block:: java
   :linenos:

   // == COMMAND GATEWAY ======================================================
   final CommandGateway cgw = platform.getCommandGateway();

   /* Fire and forget */
   cgw.sendCommand(command, context);

   /* Fire and get a Future */
   final Future<CommandResult> f1 = sendCommandForFuture(command, context);

   /* Wait for result */
   final CommandResult cr = sendCommandAndWaitForAResult(command, context);

   /* Wait for result and get exceptions */
   final CommandResult cr2 = sendCommandAndWaitForAResultWithException(command, context);

   /* Wait for command execution */
   sendCommandAndWait(command, context, timeout, unit);

   // == QUERY GATEWAY ========================================================
   final QueryGateway qgw = platform.getQueryGateway();

   final MyQuery query = ...;
   final MyQueryResult res = retrieve(query, context);

