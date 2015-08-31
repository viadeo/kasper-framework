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
