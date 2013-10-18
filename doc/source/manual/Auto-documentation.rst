
=======================
Automated documentation
=======================

The auto-documentation feature of Kasper framework is a powerful feature which will process all the registered components
of your platform and exposes a JSON documentation webservice and a javascript-only UI exploiting it.

1. Configuration
----------------

You will first have to add a dependency to **com.viadeo.kasper:kasper-documentation**.


In order to configure the auto-documentation, you'll need to instanciate all the documentation processors passing them
a reference to a common **KasperLibrary** then register them to the **platform's root processor** before platform boot.

The following documentation processors must be registered :

* CommandsDocumentationProcessor
* ConceptsDocumentationProcessor
* DomainsDocumentationProcessor
* EventsDocumentationProcessor
* HandlersDocumentationProcessor
* ListenersDocumentationProcessor
* RelationsDocumentationProcessor
* RepositoriesDocumentationProcessor
* QueryHandlersDocumentationProcessor

Configuration without Spring
............................

Without using Spring, you can use the **DefaultAutoDocumentationConfiguration** class in order to make all this stuff for you :

.. code-block:: java
    :linenos:

    final Platform kasper = new PlatformFactory().getPlatform();

    final AutoDocumentationConfiguration docConf = new DefaultAutoDocumentationConfiguration();
    docConf.registerToRootProcessor(kasper.getRootProcessor());

    kasper.boot();

Configuration with Spring
.........................

Add a **DefaultAutoDocumentationSpringConfiguration** bean as a
`Java configuration bean <http://static.springsource.org/spring/docs/current/spring-framework-reference/html/beans.html#beans-java>`_
in your Spring context, overriding it if necessary with your own bean retrieval methods.

**In your XML configuration** :

.. code-block:: xml
    :linenos:

        <beans>
          ...
          <bean id="kasperConf" class="com.viadeo.kasper.platform.configuration.DefaultPlatformSpringConfiguration"/>
          <bean id="kasperDoc" class="com.viadeo.kasper.doc.configuration.DefaultAutoDocumentationSpringConfiguration"/>
          ...
        </beans>

**As an import of another Java configuration** :

.. code-block:: java
    :linenos:

        @Configuration
        @Import({ DefaultPlatformSpringConfiguration.class ,
                  DefaultAutoDocumentationSpringConfiguration.class })
        public class MyApplicationSpringRuntime {

            ...

        }

**Override it in order to specify your own implementation of some components** :

.. code-block:: java
    :linenos:

        @Configuration
        public class KasperAutoDocumentationSpringConfiguration extends DefaultAutoDocumentationSpringConfiguration {

            @Bean
            @Override
            public KasperLibrary getKasperLibrary() {
                return new MyOverridingKasperLibrary();
            }

        }

2. Declare the HTTP resource
----------------------------

The auto-documentation feature (*JSON endpoint*) is provided as a `JAX-RS <http://jax-rs-spec.java.net/>`_ resource, **KasperDocResource**, on
**/kasper/doc** path.

So, just declare it in your JAX-RS container as another resource.

Using Jersey
............

.. code-block:: java
    :linenos:

    /* Retrieve the KasperLibrary */
    KasperLibrary kasperLibrary = docConf.getKasperLibrary();

    /* Create a wrapper class in order to inject KasperLibrary */
    @Path("/")
    public static class WrappedDocResource {

        public WrappedDocResource() { }

        @Path("/")
        public KasperDocResource delegate() {
            final KasperDocResource res = new KasperDocResource();
            res.setKasperLibrary(kasperLibrary);
            return res;
        }

    }

    public class MyApplicaton extends Application {
        public Set<Class<?>> getClasses() {
            Set<Class<?>> s = new HashSet<Class<?>>();

            /* Register the resource */
            s.add(WrappedDocResource.class);

            /* Register the JSON mapper */
            s.add(ObjectMapperKasperResolver);

            return s;
        }
    }

See `Jersey reference <http://docs.oracle.com/cd/E19776-01/820-4867/ggnxs/index.html>`_ for additional details concerning
Jersey and web services deployment.

Using DropWizard
................

Just add the resource to the environment :

.. code-block:: java
    :linenos:

    public class MyApplicationBootstrap extends Service<Configuration> {

        public static void main(String[] args) {
            new MyApplicationBootstrap.launch();
        }

        @Override
        public void initialize(Bootstrap<Configuration> bootstrap) {
            bootstrap.setName("my-application");
        }

        @Override
        public void run(Configuration configuration, Environment environment) throws Exception {
            ...
            environment.addResource(kasperDocResource);
            ...
        }

    }

See `DropWizard <http://dropwizard.codahale.com/manual/core/>`_ help for additional details.

3. Add UI access
----------------

In a Servlet 3.0 container
..........................

If you are using a Servlet 3.0 container, the static UI is available in the **kasper-documentation** artifact using
the `new Servlet 3.0 modularity capabilities <http://alexismp.wordpress.com/2010/04/28/web-inflib-jarmeta-infresources/>`_.

In a non-Servlet-3.0-compliant container
........................................

The static files are available inside **META-INF/resources** directory of the artifact, you have to deploy them somewhere
in your application where they will be accessible to end-user.

Using DropWizard
................

Add the static files as an `assets bundle <http://dropwizard.codahale.com/manual/core/#serving-assets>`_ :

.. code-block:: java
    :linenos:


    public class MyApplicationBootstrap extends Service<Configuration> {

        ...

        @Override
        public void initialize(Bootstrap<Configuration> bootstrap) {
            ...
            bootstrap.addBundle(new AssetsBundle("/META-INF/resources/doc", "/doc"));
            ...
        }

        ...

    }

Access auto-doc
---------------

* try now to access the json documentation at **/kasper/doc/domains**
* or the UI at **/doc** *(or other path if you defined another)*

