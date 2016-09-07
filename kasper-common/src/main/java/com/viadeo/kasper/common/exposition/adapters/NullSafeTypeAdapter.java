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
package com.viadeo.kasper.common.exposition.adapters;

import com.viadeo.kasper.common.exposition.TypeAdapter;
import com.viadeo.kasper.common.exposition.query.QueryBuilder;
import com.viadeo.kasper.common.exposition.query.QueryParser;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Allows to not worry about null values in TypeAdapters. It is used by wrapping
 * it around a typeadapter. Actually it is done by default for every typeadapter
 * (default and custom ones).
 * 
 * @param <T> the type of objects this adapter is dealing with.
 */
public class NullSafeTypeAdapter<T> implements TypeAdapter<T> {

	private final TypeAdapter<T> decoratedAdapter;

    // ------------------------------------------------------------------------

	public NullSafeTypeAdapter(final TypeAdapter<T> decoratedAdapter) {
		this.decoratedAdapter = checkNotNull(decoratedAdapter);
	}

	public static <T> NullSafeTypeAdapter<T> nullSafe(final TypeAdapter<T> adapter) {
		return new NullSafeTypeAdapter<T>(adapter);
	}

    // ------------------------------------------------------------------------

	@Override
	public void adapt(final T value, final QueryBuilder builder) throws Exception {
		if (null != value) {
			decoratedAdapter.adapt(value, builder);
		} else {
			builder.singleNull();
        }
	}

	@Override
	public T adapt(final QueryParser parser) throws Exception {
		/*
		 * FIXME I am not sure it is ok, null safe should also ensure people
		 * dont have to deal with pairs that have a key but no value, actually
		 * their adapt method would still be called...
		 */
		return decoratedAdapter.adapt(parser);
	}

    // ------------------------------------------------------------------------

	public TypeAdapter<T> unwrap() {
		return decoratedAdapter;
	}

}
