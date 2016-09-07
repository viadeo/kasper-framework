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
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.common.tools.ReflectionGenericsResolver;
import com.viadeo.kasper.core.component.annotation.XKasperRepository;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;
import com.viadeo.kasper.core.component.command.repository.Repository;

import static com.google.common.base.Preconditions.checkNotNull;

public class RepositoryResolver extends AbstractResolver<Repository> {

    private EntityResolver entityResolver;

    // ------------------------------------------------------------------------

    public RepositoryResolver() { }

    public RepositoryResolver(final EntityResolver entityResolver) {
        this.entityResolver = checkNotNull(entityResolver);
    }

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "Repository";
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends Repository> clazz) {

        if (DOMAINS_CACHE.containsKey(checkNotNull(clazz))) {
            return Optional.<Class<? extends Domain>>of(DOMAINS_CACHE.get(clazz));
        }

        final Class<? extends AggregateRoot> agr = this.getStoredEntityClass(clazz);
        final Optional<Class<? extends Domain>> domain = this.entityResolver.getDomainClass(agr);

        if (domain.isPresent()) {
            DOMAINS_CACHE.put(clazz, domain.get());
            return domain;
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    @Override
    public String getDescription(final Class<? extends Repository> clazz) {
        final XKasperRepository annotation =
                checkNotNull(clazz).getAnnotation(XKasperRepository.class);

        String description = "";

        if (null != annotation) {
            description = annotation.description();
        }

        if (description.isEmpty()) {
            description = String.format(
                    "The repository for %s aggregates",
                    entityResolver.getLabel(this.getStoredEntityClass(clazz))
            );
        }

        return description;
    }

    @Override
    public String getLabel(final Class<? extends Repository> clazz) {
        return checkNotNull(clazz).getSimpleName().replace("Repository", "");
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public Class<? extends AggregateRoot> getStoredEntityClass(final Class<? extends Repository> clazz) {
        @SuppressWarnings("unchecked")
        final Optional<Class<? extends AggregateRoot>> agr =
                (Optional<Class<? extends AggregateRoot>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                clazz,
                                Repository.class,
                                Repository.ENTITY_PARAMETER_POSITION
                        );

        if ( ! agr.isPresent()) {
            throw new KasperException("Unable to find aggregate type for repository " + clazz.getClass());
        }

        return agr.get();
    }

    // ------------------------------------------------------------------------

    public void setEntityResolver(final EntityResolver entityResolver) {
        this.entityResolver = checkNotNull(entityResolver);
    }

}
