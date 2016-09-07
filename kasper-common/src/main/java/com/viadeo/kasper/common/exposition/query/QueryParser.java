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
package com.viadeo.kasper.common.exposition.query;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;

import java.util.*;

public class QueryParser implements Iterable<QueryParser> {

    private static class Scope {
        private String actualName;
        private Deque<String> actualValues;

        public Scope(final String actualName, final Deque<String> actualValues) {
            this.actualName = actualName;
            this.actualValues = actualValues;
        }
    }

    private final Deque<Scope> ctx = new ArrayDeque<Scope>();
    private final Multimap<String, String> queryMap;
    private String actualValue;

    // ------------------------------------------------------------------------

    public QueryParser() {
        this.queryMap = LinkedHashMultimap.create();
    }

    public QueryParser(final SetMultimap<String, String> queryMap) {
        this.queryMap = LinkedHashMultimap.create(queryMap);
    }

    // ------------------------------------------------------------------------

    public boolean exists(final String key) {
        return queryMap.containsKey(key);
    }

    public QueryParser begin(final String key) {
        final Collection<String> values = queryMap.get(key);

        if (null == values) {
            throw new NoSuchElementException("No value found for key[" + key + "].");
        }

        if (1 == values.size()) {
            actualValue = values.iterator().next();
        }

        ctx.push(new Scope(key, new LinkedList<String>(values)));
        queryMap.removeAll(key);

        return this;
    }

    public QueryParser end() {
        if (ctx.isEmpty()) {
            throw new IllegalStateException("Invalid call of end without calling begin.");
        }
        ctx.pop();
        return this;
    }

    public Set<String> names() {
        return  ImmutableSet.copyOf(queryMap.keySet());
    }

    public String name() {
        checkContextNotEmpty();
        return ctx.peek().actualName;
    }

    public String value() {
        checkContextNotEmpty();
        return actualValue;
    }

    public int intValue() {
        checkContextNotEmpty();
        return Integer.valueOf(actualValue);
    }

    public double doubleValue() {
        checkContextNotEmpty();
        return Double.valueOf(actualValue);
    }

    public long longValue() {
        checkContextNotEmpty();
        return Long.valueOf(actualValue);
    }

    public boolean booleanValue() {
        checkContextNotEmpty();
        return Boolean.valueOf(actualValue);
    }

    public boolean hasNext() {
        if ( ! ctx.isEmpty()) {
            return ! ctx.peek().actualValues.isEmpty();
        } else {
            return ! queryMap.isEmpty();
        }
    }

    public QueryParser next() {
        if ( ! hasNext()) {
            throw new NoSuchElementException();
        }
        if ( ! ctx.isEmpty()) {
            actualValue = ctx.peek().actualValues.pop();
        }
        /* else --
         * no need to handle here iteration over key/values, as the access
         * order does not matter
         */

        return this;
    }

    private void checkContextNotEmpty() {
        if (ctx.isEmpty()) {
            throw new IllegalStateException("Call begin before trying to get a value.");
        }
    }

    @Override
    public Iterator<QueryParser> iterator() {
        return new Iterator<QueryParser>() {
            @Override
            public boolean hasNext() {
                return QueryParser.this.hasNext();
            }

            @Override
            public QueryParser next() {
                return QueryParser.this.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

}
