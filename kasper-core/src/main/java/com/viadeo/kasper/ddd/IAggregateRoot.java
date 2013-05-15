// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd;

import org.axonframework.eventsourcing.EventSourcedAggregateRoot;

import com.viadeo.kasper.IKasperID;

/**
 *
 * Aggregate Root : storable entity
 *
 ******
 * A collection of objects that are bound together by a root entity, otherwise known as an aggregate root.
 * The aggregate root guarantees the consistency of changes being made within the aggregate by forbidding external
 * objects from holding references to its members.
 * Example: When you drive a car, you do not have to worry about moving the wheels forward, making the engine combust
 * with spark and fuel, etc.; you are simply driving the car. In this context, the car is an aggregate of several
 * other objects and serves as the aggregate root to all of the other systems.
 * (source: Wikipedia)
 * *****
 * Effective Aggregate Design (DDD General) - Vaughn Vernon
 * http://dddcommunity.org/sites/default/files/pdf_articles/Vernon_2011_1.pdf
 * *****
 *
 * 
 * @see IEntity
 * @see IDomain
 */
public interface IAggregateRoot extends EventSourcedAggregateRoot<IKasperID>, IEntity {

}
