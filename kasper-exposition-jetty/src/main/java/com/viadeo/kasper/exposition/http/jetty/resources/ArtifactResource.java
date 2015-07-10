// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http.jetty.resources;

import com.viadeo.kasper.exposition.http.jetty.locators.Resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * TODO Is this resource used? Isn't it replaced by the meta domain?
 */
@Path("/artifact")
public class ArtifactResource implements Resource {

    @GET
    @Path("/version")
    public String getVersion() {
        return getClass().getPackage().getImplementationVersion();
    }

}
