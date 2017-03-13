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
package com.viadeo.kasper.api.component.query;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class CollectionQueryResult<RES extends QueryResult> implements Iterable<RES>, QueryResult  {
    private static final long serialVersionUID = -3864225412367930495L;

    /** Generic parameter position for Data Transfer Object */
    public static final int PARAMETER_RESULT_POSITION = 0;

	private final List<RES> list;

    // ------------------------------------------------------------------------

    protected CollectionQueryResult() {
        this(Lists.<RES>newArrayList());
    }

	protected CollectionQueryResult(final Collection<RES> list) {
		this.list = Lists.newArrayList(checkNotNull(list));
	}

    // ------------------------------------------------------------------------

	@Override
	public Iterator<RES> iterator() {
		return this.list.iterator();
	}

    // ------------------------------------------------------------------------

	public int getCount() {
		return this.list.size();
	}

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

	public Collection<RES> getList() {
        return ImmutableList.copyOf(this.list);
	}

    /**
     * This method is clearly a weakness of immutability, we keep it only to ensure retro-compatibility of the api.
     * @param list the list to be set
     * @deprecated deprecated in order to keep retro-compatibility before to be deleted.
     */
    @Deprecated
    public void setList(final Collection<RES> list) {
        checkNotNull(list);
        this.list.clear();
        this.list.addAll(Lists.newArrayList(list));
    }

    /**
     * This method is clearly a weakness of immutability, we keep it only to ensure retro-compatibility of the api.
     * @param iterator the iterator to be set
     * @deprecated in order to keep retro-compatibility before to be deleted.
     */
    @Deprecated
    public void setListAsIterator(final Iterator<RES> iterator) {
        checkNotNull(iterator);
        this.list.clear();
        this.list.addAll(Lists.newArrayList(iterator));
    }

    /**
     * @param list a list of resources
     * @param <P> the inferred type of <code>CollectionQueryResult</code>
     * @return the mutated <code>CollectionQueryResult</code> instance
     * @deprecated in order to keep retro-compatibility before to be deleted.
     * @see #setList(java.util.Collection)
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public <P extends CollectionQueryResult> P withList(final Collection<RES> list) {
        this.setList(checkNotNull(list));
        return (P) this;
    }

    /**
     * @param iterator an iterator of resources
     * @param <P> the inferred type of <code>CollectionQueryResult</code>
     * @return the mutated <code>CollectionQueryResult</code> instance
     * @deprecated in order to keep retro-compatibility before to be deleted.
     * @see #setListAsIterator(java.util.Iterator)
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public <P extends CollectionQueryResult> P withListAsIterator(final Iterator<RES> iterator) {
        this.setListAsIterator(checkNotNull(iterator));
        return (P) this;
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(final Object o) {
        if (null == o) {
            return false;
        }

        if (this == checkNotNull(o)) {
            return true;
        }
        if (getClass() != o.getClass()) {
            return false;
        }

        final CollectionQueryResult that = (CollectionQueryResult) o;

        if ((null == this.list) && (null != that.list)) {
            return false;
        }

        if ((null == that.list) && (null != this.list)) {
            return false;
        }

        if (null == that.list) {
            return true;
        }

        if (this.list.size() != that.list.size()) {
            return false;
        }

        final Iterator it1 = this.list.iterator();
        final Iterator it2 = that.list.iterator();

        while (it1.hasNext()) {
            if ( ! it1.next().equals(it2.next())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(list);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("list", list)
                .add("count", getCount())
                .toString();
    }

}
