// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http.jetty.resources;

import com.viadeo.kasper.exposition.http.jetty.locators.Resource;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This resource is called by the L5 in order to known if we can route HTTP traffic to this application instance.
  */
@Path("/acv")
public class InfrastructureAcvResource implements Resource {

    protected File acvFfile;

    // ------------------------------------------------------------------------

    public InfrastructureAcvResource(final String acvFilePath) {
        this.acvFfile = new File(checkNotNull(acvFilePath));
    }

    public InfrastructureAcvResource(final File acvFfile) {
        this.acvFfile = checkNotNull(acvFfile);
    }

    @GET
    @Path("/status")
    public Response getPoolStatus() {
        boolean isInPool = false;
        if (null != acvFfile) {
            try {
                isInPool = "IN".equalsIgnoreCase(IOUtils.toString(new FileInputStream(acvFfile)).trim());
            } catch (final FileNotFoundException e) {
                //do nothing
            } catch (final IOException e) {
                //do nothing
            }
        }

        if (isInPool) {
            return Response.ok().entity("IN").build();
        } else {
            return Response.status(Response.Status.GONE).entity("OUT").build();
        }
    }

}
