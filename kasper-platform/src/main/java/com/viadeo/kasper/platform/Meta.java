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
package com.viadeo.kasper.platform;

import org.joda.time.DateTime;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Meta class provide information about the platform.
 */
public class Meta {

    public static final Meta UNKNOWN = new Meta("unknown", DateTime.now(), DateTime.now());

    private final String version;
    private final DateTime buildingDate;
    private final DateTime deploymentDate;

    // ------------------------------------------------------------------------

    public Meta(final String version, final DateTime buildingDate, final DateTime deploymentDate) {
        this.version = checkNotNull(version);
        this.buildingDate = checkNotNull(buildingDate);
        this.deploymentDate = checkNotNull(deploymentDate);
    }

    // ------------------------------------------------------------------------

    /**
     * @return the version of the platform
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return the building date of the platform
     */
    public DateTime getBuildingDate() {
        return buildingDate;
    }

    /**
     * @return the deployment date of the platform
     */
    public DateTime getDeploymentDate() {
        return deploymentDate;
    }

}
