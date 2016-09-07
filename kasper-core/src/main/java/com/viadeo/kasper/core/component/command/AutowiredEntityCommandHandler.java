// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.exception.KasperCommandException;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.common.tools.ReflectionGenericsResolver;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;
import com.viadeo.kasper.core.component.command.repository.Repository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base implementation for an auto wired Kasper entity command handler.
 *
 * @param <C> Command
 * @param <AGR> the entity (aggregate root)
 *
 * @see com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot
 * @see AutowiredEntityCommandHandler
 * @see AutowiredCommandHandler
 * @see com.viadeo.kasper.core.component.command.aggregate.ddd.Entity
 * @see com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot
 */
public abstract class AutowiredEntityCommandHandler<C extends Command, AGR extends AggregateRoot>
        extends AutowiredCommandHandler<C>
        implements EntityCommandHandler<C,AGR>, WirableCommandHandler<C>
{

    protected final Class<AGR> aggregateClass;
    protected Repository<? extends KasperID, AGR> repository;

    // ------------------------------------------------------------------------

    public AutowiredEntityCommandHandler() {
        super();

        @SuppressWarnings("unchecked")
        final Optional<Class<AGR>> entityAssignClass = (Optional<Class<AGR>>) ReflectionGenericsResolver
                .getParameterTypeFromClass(
                        this.getClass(),
                        AutowiredEntityCommandHandler.class,
                        ENTITY_PARAMETER_POSITION
                );

        if ( ! entityAssignClass.isPresent()) {
            throw new KasperCommandException(
                    "Cannot determine entity type for "
                            + this.getClass().getName()
            );
        }

        this.aggregateClass = entityAssignClass.get();
    }

    // ------------------------------------------------------------------------

    @Override
    public Class<AGR> getAggregateClass() {
        return aggregateClass;
    }

    // ------------------------------------------------------------------------

    /**
     * @param repository the repository related to the aggregate handled by this instance
     * @see AutowiredEntityCommandHandler#setRepository(com.viadeo.kasper.core.component.command.repository.Repository)
     */
    public <ID extends KasperID> void setRepository(final Repository<ID,AGR> repository) {
        this.repository = checkNotNull(repository);
    }

    /**
     * Get the related repository of the entity handled by this command handler
     *
     * @return the repository
     */
    @SuppressWarnings("unchecked")
    public <REPO extends Repository> REPO getRepository() {
        if (null == this.repository) {

            if (null == repositoryManager) {
                throw new KasperCommandException("Unable to resolve repository, no repository manager was provided");
            }

            final Optional<Repository<KasperID,AGR>> optRepo = repositoryManager.getEntityRepository(getAggregateClass());

            if ( ! optRepo.isPresent()) {
                throw new KasperCommandException(String.format(
                        "The entity %s has not been recorded on any domain", getAggregateClass().getSimpleName())
                );
            }

            this.repository = optRepo.get();
        }

        return (REPO) repository;
    }

    // ------------------------------------------------------------------------

}
