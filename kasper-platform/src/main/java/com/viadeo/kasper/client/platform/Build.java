package com.viadeo.kasper.client.platform;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.viadeo.kasper.cqrs.query.QueryResult;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Build {

    private static final Logger LOGGER = LoggerFactory.getLogger(Build.class);

    public static Info info(ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(Resources.getResource("buildInfo.json"), Info.class);
        } catch (Throwable e) {
            LOGGER.warn("Failed to load platform meta information", e);
        }

        return unknownInfo;
    }

    public static final Info unknownInfo = new Info("nc", "nc", "nc", "nc", "nc", 0L, DateTime.now());

    public static class Info implements QueryResult {

        private final String builder;
        private final String host;
        private final String revision;
        private final String comment;
        private final String platform;
        private final Long timestamp;
        private final DateTime time;

        public Info(String builder, String host, String revision, String comment, String platform, Long timestamp, DateTime time) {
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
