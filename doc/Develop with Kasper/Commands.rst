
CQRS: Commands
==============

.. topic:: Wikipedia - The command pattern

    In object-oriented programming, the command pattern is a behavioral design pattern in which an object is used to represent 
    and encapsulate all the information needed to call a method at a later time. This information includes the method name, 
    the object that owns the method and values for the method parameters.

    Four terms always associated with the command pattern are command, receiver, invoker and client. A command object has a 
    receiver object and invokes a method of the receiver in a way that is specific to that receiver's class. The receiver 
    then does the work. A command object is separately passed to an invoker object, which invokes the command, and optionally 
    does bookkeeping about the command execution. Any command object can be passed to the same invoker object. Both an invoker 
    object and several command objects are held by a client object. The client contains the decision making about which commands 
    to execute at which points. To execute a command, it passes the command object to the invoker object. See example code below.

    Using command objects makes it easier to construct general components that need to delegate, sequence or execute method 
    calls at a time of their choosing without the need to know the class of the method or the method parameters. Using an invoker 
    object allows bookkeeping about command executions to be conveniently performed, as well as implementing different modes for 
    commands, which are managed by the invoker object, without the need for the client to be aware of the existence of bookkeeping 
    or modes. 

Using Kasper framework you'll have to define one and only one **command handler** per defined **command**.

Commands
--------

A command is an **immutable** anemic object (*DTO, Data Transfer Object*), implementing the interface **Command** whose class name ends by '*Command*'.

A command can optionally declares some metadata using the **@XKasperCommand** annotation.

**A Command is part of a domain API**.

The abstract class **AbstractCreateCommand** can be used to define an entity creation command, which contains an id to be used as
identifier for the entity to be created.

**usage** ::

    @XKasperCommand( description = "An awesome command used to create a User" )
    public class CreateAUserCommand extends AbstractCreateCommand {

        private final String username;

        public CreateAUserCommand(final String username, final KasperID idToBeUsed) {
            super(idToBeUSed);

            this.username = checkNotNull(username);
        }

        public String getUsername() {
            return this.username;
        }

    }


Command handlers
----------------

A command handler is an object implementing the interface **CommandHandler<Command>**, whose class name ends with '*CommandHandler*'.

A command handler **have to** declares its owning domain into the annotation **@XKasperCommandHandler**.

**A command handler is part of the COMMAND architectural area**.

The abstract class **AbstractCommandHandler<Command>** can be used in order to allow a finer grained handling declaration (ability to access to the command
message and the unit of work, the message alone, or the command only depending on your needs)i and giving access to the domain locator which can then be used
to retrieve domain objects.

The abstract class **AbstractEntityCommandHandler<Command, Entity>** can be used to stick a command handler to a specific entity, it defines a method
*getRepository()* used to retrieve easily the repository corresponding to this entity. This abstract class must generally be used when
defining a command mainly dedicated to create, modify and delete a domain entity.

**usage** ::

    @XKasperCommandHandler( domain = UserDomain.class, description = "Creates a user known to the application" )
    public class CreateAUserCommandHandler extends AbstractEntityCommandHandler<User, CreateAUserCommand> {
        
        public CommandResult handle(final CreateAUserCommand command) {
            final UserRepository repository = this.getRepository();

            final User user = new User(command.getIdToUse(), command.getUsername());
            repository.add(user);

            return CommandResult.ok();
        }

    }

If you need to retrieve a different repository, use the platform domain locator ::

    @XKasperCommandHandler( domain = UserDomain.class, description = "Creates a user known to the application" )
    public class CreateAUserCommandHandler extends AbstractEntityCommandHandler<User, CreateAUserCommand> {
        
        public Thing getThing() {
            Thing thing = null;

            final Optional<ThingRepository> thingRepositoryOpt = this.getDomainLocator().getEntityRepository(Thing.class);
            if (thingRepositoryOpt.isPresent()) {
                thing = thingRepositoryOpt.get().load(...);
            }

            return thing;
        }

        public CommandResult handle(final CreateAUserCommand command) {
            final UserRepository userRepository = this.getRepository();

            if (null != this.getThing()) {
                final User user = new User(command.getIdToUse(), command.getUsername());
                userRepository.add(user);
            }

            return CommandResult.ok();
        }

    }   


TODO: send events
