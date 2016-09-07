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
package com.viadeo.kasper.api.id;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Simple IKasperID parameterizable implementation
 *
 * @param <T> the internal IKasperID type
 */
public abstract class AbstractKasperID<T extends Serializable> implements KasperID {
	private static final long serialVersionUID = 5118678966818650797L;

	private T id;
	
	// ------------------------------------------------------------------------

    protected AbstractKasperID() {

    }

	protected AbstractKasperID(final T id) {
		this.id = checkNotNull(id);
	}	
	
	protected void setId(final T id) {
		this.id = checkNotNull(id);
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	public boolean equals(final Object otherId) {
		if (this == checkNotNull(otherId)) {
			return true;
		}
		if (KasperID.class.isAssignableFrom(otherId.getClass())) {
			final KasperID other = (KasperID) otherId;
			return this.toString().equals(other.toString());
		}		
		return this.id.equals(otherId);
	}	
	
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	// ------------------------------------------------------------------------
	
	@Override
	public T getId() {
		return this.id;
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return this.id.toString();
	}

}
