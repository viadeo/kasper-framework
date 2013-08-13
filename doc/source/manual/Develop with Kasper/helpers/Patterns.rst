Recommended software patterns
=============================

State pattern
-------------


Specification pattern
---------------------

more about the `Specification pattern <http://en.wikipedia.org/wiki/Specification_pattern>`_

The **Specification<T>** abstract class should be used at every location where it is possible in order to express business rules on value objects,
entities or any other class.

It will permit to isolate business rules, reuse them, or even move their expression into rules engine at a later time without any modification
in the calling code.

**ex:**

.. code-block:: java
    :linenos:

    /* Define a base specification, with an optional contructor for parameterization */
    class SpecificationStringContains extends Specification<String> {

        final private String containsPattern;

        public SpecificationStringContains(final String containsPattern) {
            this.containsPattern = containsPattern;
        }

        @Override
        public boolean isSatisfiedBy(final String entity) {
            return entity.contains(this.containsPattern);
        }
    }

    /* Instanciate some intermediate semantic specifications */
    final Specification<String> stringContainsLetterA = new SpecificationStringContains("A");
    final Specification<String> stringContainsLetterB = new SpecificationStringContains("B");

    final Specification<String> stringContainsLettersAandB   = stringContainsLetterA.and(stringContainsLetterB);
    final Specification<String> stringContainsLettersAorB    = stringContainsLetterA.or(stringContainsLetterB);
    final Specification<String> stringDoesNotContainsLetterA = stringContainsLetterA.not();

    /* Apply the specifications in your code */
    assertTrue(stringContainsLetterA.isSatisfiedBy("An awesome string"));
    assertFalse(stringContainsLetterB.isSatisfiedBy("An awesome string"));

    assertTrue(stringContainsLettersAandB.isSatisfied("An awesome string for Bob"));
    assertTrue(stringContainsLettersAandB.isSatisfied("Bob is a geek"));
    assertTrue(stringDoesNotContainsLetterA.isSatisfied("Bob is a geek"));

You can get an error message from a not met specification using a slightly modified version of **isSatisfiedBy()**.

.. code-block:: java
    :linenos:

    final SpecificationErrorMessage message = new DefaultSpecificationErrorMessage();
    stringContainsLetterA.isSatisfiedBy("Bob is awesome", message);
    assertEquals("SpecificationStringContains specification was not met for value Bob is awesome");

Specifying a **description** on your specification you'll get a clearer error message :

.. code-block:: java
    :linenos:

    @XKasperSpecification( description = "checks that a string contains some pattern" )
    class SpecificationStringContains extends Specification<String> {
        ...
    }

    final SpecificationErrorMessage message = new DefaultSpecificationErrorMessage();
    stringContainsLetterA.isSatisfiedBy("Bob is awesome", message);
    assertEquals("Specification not met : checks that a string contains some pattern for value Bob is awesome");

Or a dedicated error message, with the **errorMessage** property :

.. code-block:: java
    :linenos:

    @XKasperSpecification( description = "checks that a string contains some pattern",
                           errorMessage = "string does not contain the specified pattern" )
    class SpecificationStringContains extends Specification<String> {
        ...
    }

    final SpecificationErrorMessage message = new DefaultSpecificationErrorMessage();
    stringContainsLetterA.isSatisfiedBy("Bob is awesome", message);
    assertEquals("string does not contain the specified pattern for value Bob is awesome");

You can also decide to not set any annotation and just override the **getDefaultErrorMessage** *protected* method :

.. code-block:: java
    :linenos:

    class SpecificationStringContains extends Specification<String> {
        ...

    	protected String getDefaultErrorMessage(final String entity) {
    		return String.format("%s does not contain pattern %s", entity, this.containsPattern);
        }
    	...
    }

**Kasper framework also propose two convenience abstract classes for correct typing :**

* EntitySpecification<E extends Entity>
* ValueSpecification<V extends Value>

Prefer these classes for creating business rules on your entities or on your value objects.

Anti-corruption layer
---------------------



