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
package com.viadeo.kasper.exposition.http.jetty.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class DefaultRootResource implements Resource {
    @GET
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public String get() {
        return  "                                            __   __             __ __ ___   _____ ____  __________ \n" +
                "    ____  ____ _      _____  ________  ____/ /  / /_  __  __   / //_//   | / ___// __ \\/ ____/ __ \\\n" +
                "   / __ \\/ __ \\ | /| / / _ \\/ ___/ _ \\/ __  /  / __ \\/ / / /  / ,<  / /| | \\__ \\/ /_/ / __/ / /_/ /\n" +
                "  / /_/ / /_/ / |/ |/ /  __/ /  /  __/ /_/ /  / /_/ / /_/ /  / /| |/ ___ |___/ / ____/ /___/ _, _/ \n" +
                " / .___/\\____/|__/|__/\\___/_/   \\___/\\__,_/  /_.___/\\__, /  /_/ |_/_/  |_/____/_/   /_____/_/ |_|  \n" +
                "/_/                                                /____/                                          ";
    }
}
