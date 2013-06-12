..  _Kasper_java_client:

==================
Kasper java client
==================

The idea behind the Kasper client library is to provide a very easy and powerful API to hit exposed Kasper implementations. This requires
kasper implementations to share their Query and Commands code with JVM based consumers. This allows the library to only require one liner code
taking as arguments populated queries and commands (basic POJOs), the library takes care of all the ser/deser stuff, error handling, 
asynchronous calls and more.

, they share some common code in order to ensure
that the way in which the communication is done is symetric (ex: what can be serialized can also be deserialized). For example all the databinding
part for query is shared between kasper-client and kasper-web.

.. seealso:: 
   
   This library works with the :ref:`Automated_HTTP_exposition` component enabled on the server side, 
   you must enable it in order to be able to communicate.
   
   Kasper http exposition and Kasper client share some common code (:ref:`TypeAdapters` and jackson configuration), to ensure that the way in which the communication 
   is done is symetric (ex: what can be serialized can also be deserialized).

The main entry point of the library is KasperClient class, it provides all the required methods to communicate with exposed Kasper implementations.
KasperClient is thread safe and should be reused for optimal performances.

Sending a query is as simple as :

.. code-block:: java

   KasperClient client = new KasperClient();
   SuperCoolDTO dto = client.query(new SuperCoolQuery("what's up?"), SuperCoolDTO.class);

Hard to make it shorter! :)

.. note:: 
   
   By default KasperClient is configured to hit queries at http://localhost:8080/query/ and commands at http://localhost:8080/command. Of course this can be configured using KasperClientBuilder.

   .. code-block:: java

      KasperClient client = new KasperClientBuilder()
                              .queryBaseLocation("http://kasper-platform/query")
                              .commandBaseLocation("http://kasper-platform/query")
                              .create();


**Get it**

.. code-block:: code
   
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
  
   client.sendAsync(someCommand, new ICallback<ICommandResult>() {
       public void done(ICommandResult result) {
           // do something smart with my result
       }
   });
   
   // or using a future
   
   Future<ICommandResult> futureCommandResult = client.sendAsync(someCommand);
   
   // do some other work while the command is being processed
   ...
   
   // block until the result is obtained
   ICommandResult commandResult = futureCommandResult.get();
      
In most cases you will prefer using Future. 