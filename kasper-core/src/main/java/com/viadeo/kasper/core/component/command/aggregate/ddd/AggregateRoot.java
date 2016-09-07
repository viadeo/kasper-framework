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
package com.viadeo.kasper.core.component.command.aggregate.ddd;

import com.google.common.collect.Maps;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.exception.KasperCommandException;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.command.gateway.ContextualizedUnitOfWork;
import org.axonframework.domain.MetaData;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * Base AGR implementation
 *
 * @see AggregateRoot
 * @see com.viadeo.kasper.api.component.Domain
 */
public abstract class AggregateRoot<I extends KasperID>
		extends AbstractAnnotatedAggregateRoot<I>
        implements Entity {

	private static final long serialVersionUID = 8352516744342839116L;

    public static final String VERSION_METANAME = "VERSION";

	@AggregateIdentifier
	protected I id;

    private Long version;

	// ========================================================================

	@SuppressWarnings("unchecked")
	public I  getEntityId() {
		return this.id;
	}

    protected void setId(final I id) {
        this.id = checkNotNull(id);
    }

    // ------------------------------------------------------------------------

    public void setVersion(final Long version) {
        if (null == super.getVersion()) { /* if aggregate is not event-sourced */
            this.version = checkNotNull(version);
        }
    }

    /**
     * A newly created aggregate will have version null
     * A firstly loaded aggregate (new aggregate, first loaded) will have version 0L
     */
    @Override
    public Long getVersion() {
        final Long superVersion = super.getVersion();
        if (null == superVersion) { /* if aggregate is not event-sourced */
            return this.version;
        }
        return superVersion;
    }

    // ------------------------------------------------------------------------

    @Override
    protected void apply(final Object eventPayload) {
        apply(checkNotNull(eventPayload), enrichMetaData(null));
    }

    @Override
    protected void apply(final Object eventPayload, final MetaData metaData) {
        checkNotNull(eventPayload);
        checkNotNull(metaData);

        if ( ! Event.class.isAssignableFrom(eventPayload.getClass())) {
            throw new KasperCommandException(
                    String.format("Only apply implementations of '%s'", eventPayload.getClass().getName())
            );
        }

        super.apply(eventPayload, enrichMetaData(metaData));
    }

    // ------------------------------------------------------------------------

    protected MetaData enrichMetaData(final MetaData metaData) {
        final Map<String, Object> newMetaData = Maps.newHashMap();

        // Add context
        newMetaData.put(Context.METANAME, ContextualizedUnitOfWork.getCurrentUnitOfWork().getContext().or(Contexts.empty()));

        // Add version
        final Long version = (null == this.getVersion()) ? 0L : this.getVersion();
        newMetaData.put(AggregateRoot.VERSION_METANAME, version);

        if (null == metaData) {
            return MetaData.from(newMetaData);
        } else {
            return metaData.mergedWith(newMetaData);
        }

    }

}
