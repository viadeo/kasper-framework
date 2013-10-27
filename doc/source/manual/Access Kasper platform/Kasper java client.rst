..  _Kasper_java_client:

==================
Kasper java client
==================

The idea behind the Kasper client library is to provide a very easy and powerful API to hit exposed Kasper implementations. This requires
kasper implementations to share their Query and Commands code with JVM based consumers. This allows the library to only require one liner code
taking as arguments populated queries and commands (basic POJOs), the library takes care of all the ser/deser stuff, error handling, 
asynchronous calls and more.

They share some common code in order to ensure that the way in which the communication is done is symmetric
(ex: what can be serialized can also be deserialized). For example all the databinding part for query is
shared between kasper-client and kasper-web.

.. seealso:: 
   
   This library works with the :ref:`Automated_HTTP_exposition` component enabled on the server side, 
   you must enable it server-side in order to be able to communicate.
   
   Kasper http exposition and Kasper client share some common code (:ref:`TypeAdapters` and jackson configuration), to ensure that the way in which the communication 
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
   if (response.isError()) {
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
   if (response.isError()) {
      // do something useful with response.getError()
   }

|

You like it ? Then **Get it!**

:: 
   
   Gradle : 
      'com.viadeo.kasper:kasper-client:KASPER_LATEST_VERSION'

   Maven: 
      <dependency>
         <groupId>com.viadeo.kasper</groupId>
         <artifactId>kasper-client</artifactId>
         <version>KASPER_LATEST_VERSION</version>
      </dependency>
      

Asynchronous queries & commands
===============================

Kasper client provides two ways of doing asynchronous operations, using Futures and Callbacks.
  
.. code-block:: java
   :linenos:

   client.sendAsync(someCommand, new ICallback<ICommandResponse>() {
       public void done(final ICommandResponse response) {
           // do something smart with my response
       }
   });
   
   /* or using a future */
   
   final Future<ICommandResponse> futureCommandResponse = client.sendAsync(context, someCommand);
   
   // do some other work while the command is being processed
   ...
   
   // block until the response is obtained
   final ICommandResponse commandResponse = futureCommandResponse.get();
      
In most cases you will probably prefer using Futures.

