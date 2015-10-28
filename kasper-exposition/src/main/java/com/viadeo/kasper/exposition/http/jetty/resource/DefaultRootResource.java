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
