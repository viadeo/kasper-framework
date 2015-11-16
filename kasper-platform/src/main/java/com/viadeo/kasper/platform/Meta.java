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
