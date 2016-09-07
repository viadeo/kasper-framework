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
package com.viadeo.kasper.core.component.command.aggregate.ddd.specification.impl;

import com.viadeo.kasper.core.component.command.aggregate.ddd.specification.ISpecification;
import com.viadeo.kasper.core.component.command.aggregate.ddd.specification.SpecificationErrorMessage;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * @param <T> Object class
 * 
 * @see EntitySpecification
 * @see com.viadeo.kasper.core.component.command.aggregate.ddd.Entity
 */
public class AndSpecification<T> extends AbstractCompositeSpecification<T> {

	public AndSpecification(final ISpecification<T> spec1, final ISpecification<T> spec2) {
		super(spec1, spec2);
	}

	// ----------------------------------------------------------------------

	/**
	 * @see Specification#isSatisfiedBy(Object)
	 */
	@Override
	public boolean isSatisfiedBy(final T object) {
		checkNotNull(object);
		return this.spec1.isSatisfiedBy(object) && this.spec2.isSatisfiedBy(object);
	}

	// ----------------------------------------------------------------------
	
	/**
	 * @see AbstractCompositeSpecification#isSatisfiedBy(Object, com.viadeo.kasper.core.component.command.aggregate.ddd.specification.SpecificationErrorMessage)
	 */
	@Override
	public boolean isSatisfiedBy(final T object, final SpecificationErrorMessage errorMessage) {
		
		final SpecificationErrorMessage errorMessage1 = new SpecificationErrorMessage();
		final boolean isSatisfied1 = this.spec1.isSatisfiedBy(object, errorMessage1);

		final SpecificationErrorMessage errorMessage2 = new SpecificationErrorMessage();
		final boolean isSatisfied2 = this.spec2.isSatisfiedBy(object, errorMessage2);

		if (isSatisfied1 && isSatisfied2) {
			return true;
		}
		
		final StringBuffer sb = new StringBuffer();
		if ( ! isSatisfied1) {
			sb.append(errorMessage1.getMessage()).append("\n");
		}
		if ( ! isSatisfied2) {
			sb.append(errorMessage2.getMessage());
		}
		errorMessage.setMessage(sb.toString());
		
		return false;
	}

}
