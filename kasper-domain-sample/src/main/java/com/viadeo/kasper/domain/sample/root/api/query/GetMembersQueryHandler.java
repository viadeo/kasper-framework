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
package com.viadeo.kasper.domain.sample.root.api.query;

import com.viadeo.kasper.api.annotation.XKasperAlias;
import com.viadeo.kasper.api.annotation.XKasperQueryResult;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.id.ID;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.api.validation.AssertID;
import com.viadeo.kasper.core.component.query.AutowiredQueryHandler;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.domain.sample.root.api.Facebook;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@XKasperQueryHandler(domain=Facebook.class)
public class GetMembersQueryHandler extends AutowiredQueryHandler<GetMembersQueryHandler.GetMembersQuery, GetMembersQueryHandler.MembersResult> {

    @XKasperAlias(values = "GetUsers")
	public static class GetMembersQuery implements Query {
		private static final long serialVersionUID = -6513893864054353478L;

        @NotNull
        @Min(3)
		public String name;

        @AssertID
        public ID id;

        @AssertID(vendor = "viadeo", type = "member", format = "db-id")
        public ID id2;
	}
	
	@XKasperQueryResult
	public static class MembersResult implements QueryResult {
		private static final long serialVersionUID = -2174693040511999516L;
		public String lastName;
		public String firstName;
		public KasperID id;
	}

}
