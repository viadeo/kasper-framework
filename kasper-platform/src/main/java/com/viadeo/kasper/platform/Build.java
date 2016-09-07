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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.viadeo.kasper.api.component.query.QueryResult;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Build {

    private static final Logger LOGGER = LoggerFactory.getLogger(Build.class);

    public static Info info(final ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(Resources.getResource("buildInfo.json"), Info.class);
        } catch (Throwable e) {
            LOGGER.warn("Failed to load platform meta information", e);
        }

        return unknownInfo;
    }

    public static final Info unknownInfo = new Info("nc", "nc", "nc", "nc", "nc", 0L, DateTime.now());

    // ------------------------------------------------------------------------

    public static class Info implements QueryResult {

        private final String builder;
        private final String host;
        private final String revision;
        private final String comment;
        private final String platform;
        private final Long timestamp;
        private final DateTime time;

        public Info(
                final String builder,
                final String host,
                final String revision,
                final String comment,
                final String platform,
                final Long timestamp,
                final DateTime time) {
            this.builder = builder;
            this.host = host;
            this.revision = revision;
            this.comment = comment;
            this.platform = platform;
            this.timestamp = timestamp;
            this.time = time;
        }

        public String getBuilder() {
            return builder;
        }

        public String getHost() {
            return host;
        }

        public String getRevision() {
            return revision;
        }

        public String getComment() {
            return comment;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public DateTime getTime() {
            return time;
        }

        public String getPlatform() {
            return platform;
        }
    }

}
