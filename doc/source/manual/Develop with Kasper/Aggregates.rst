.. _writing-aggregates:

Writing Aggregates
==================

Kasper aggregates must be written as event-sourced aggregates in order to ensure that every mutation will
generate an associated event :

* do not directly mutate the aggregate using a public method (even through constructor)
* use events and event handlers within the aggregate

.. code-block:: java
    :linenos:

    @XKasperConcept( domain = Members.class, label = "Member" )
    public class Member extends AbstractRootConcept {

        private String name;

        // -----

        public Member(final Context context, final String name) {
            apply(new MemberCreatedEvent(context, name));
        }

        @EventHandler
        protected void onCreated(final MemberCreatedEvent event) {
            this.name = event.getName();
        }

        // -----

        public void changeName(final Context, final String name) {
            if ( ! this.name.contentEquals(name)) {
                apply(new MemberNameChanged(context, name));
            }
        }

        @EventHandler
        public void onNameChanged(final MemberNameChanged event) {
            this.name = event.getName();
        }

    }

* apply() will immediately apply the event to the aggregate (eg. calling the declared event handlers within the aggregate)
* once applied the event will be generalized (recorded to the unit of work for further publication)

Entity-store repositories (which directly extends **Repository** instead of **EventSourcedRepository**) will need to
construct aggregates, they can have two different strategies for that :

1. Generate event(s) from the entity store, build an empty aggregate and call their event handlers (*preferred way*)
2. Call a direct constructor of the aggregate which is reserved for this usage

In case you have to choose the second strategy, annotate your constructor with **@XKasperEntityStoreCreator**.




