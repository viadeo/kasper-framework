
Develop with Kasper
===================

Kasper framework asks you to implements a limited number of building blocks.

Components which are part of a domain API (sometimes referred later as **C/Q/E**) :

- **C** - Commands
- **Q** - Queries
- **QR** - Query responses
- **E** - Events

Components which manipulates I/O between layers :

- **CH** - Command handlers
- **EL** - Event listeners
- **QH** - Query handlers
- **SA** - Service adapters

Components of the model :

- **DE** - Domain entities (**DC** - Concepts & **DR** - Relations)
- **DR** - Domain repositories
- **DS** - Domain services
- **EF** - Entity factories

Kasper framework is based on some standard libraries and frameworks :

- `Axon Framework <http://www.axonframework.org/>`_
- `Google Guava <https://code.google.com/p/guava-libraries/>`_
- `Joda Time <http://joda-time.sourceforge.net/>`_
- `Jackson <http://jackson.codehaus.org/>`_
- `Jersey <https://jersey.java.net/>`_
- `Paranamer <http://paranamer.codehaus.org/>`_
- `Reflections <https://code.google.com/p/reflections/>`_
- `Json.org <http://json.org/java/>`_
- `Dropwizard <http://dropwizard.codahale.com/>`_
- `Spring <http://www.springsource.org/>`_ (optional)
- `SLF4J <http://www.slf4j.org/>`_/`Logback <http://logback.qos.ch/>`_
- `JUnit <http://junit.org/>`_/`Mockito <https://code.google.com/p/mockito/>`_/`Hamcrest <https://github.com/hamcrest/JavaHamcrest>`_

And encourages you to use some software patterns in your developments :

- The `Builder pattern <http://en.wikipedia.org/wiki/Builder_pattern>`_
- The `State pattern <http://en.wikipedia.org/wiki/State_pattern>`_
- The `Specification pattern <http://en.wikipedia.org/wiki/Specification_pattern>`_
- The `Repository pattern <http://www.codeproject.com/Articles/600097/Why-the-Repository-Pattern>`_
- The `Command pattern <http://en.wikipedia.org/wiki/Command_pattern>`_
- The `Anti-corruption layer pattern <http://moffdub.wordpress.com/2008/09/21/anatomy-of-an-anti-corruption-layer-part-1/>`_

**Contents**

.. toctree::
    :maxdepth: 2

    General
    Domains
    Commands
    Model
    Events
    Queries
    Validation
    Aliasing
    HadoopIndex
    Context
    Helpers
    Testing
    Security
    Authorization


