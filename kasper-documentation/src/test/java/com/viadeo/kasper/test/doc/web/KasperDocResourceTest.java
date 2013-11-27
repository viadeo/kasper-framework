// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.doc.web;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;
import com.viadeo.kasper.client.platform.domain.descriptor.*;
import com.viadeo.kasper.doc.element.DocumentedPlatform;
import com.viadeo.kasper.doc.initializer.DefaultDocumentedElementInitializer;
import com.viadeo.kasper.doc.web.KasperDocResource2;
import com.viadeo.kasper.doc.web.ObjectMapperKasperResolver;
import com.viadeo.kasper.test.applications.Applications;
import com.viadeo.kasper.test.applications.entities.Application;
import com.viadeo.kasper.test.applications.entities.Member_fanOf_Application;
import com.viadeo.kasper.test.applications.repositories.ApplicationMemberFansRepository;
import com.viadeo.kasper.test.applications.repositories.ApplicationRepository;
import com.viadeo.kasper.test.root.Facebook;
import com.viadeo.kasper.test.root.commands.AddConnectionToMemberCommand;
import com.viadeo.kasper.test.root.entities.Member;
import com.viadeo.kasper.test.root.entities.Member_connectedTo_Member;
import com.viadeo.kasper.test.root.events.MemberCreatedEvent;
import com.viadeo.kasper.test.root.handlers.AddConnectionToMemberHandler;
import com.viadeo.kasper.test.root.listeners.MemberCreatedEventListener;
import com.viadeo.kasper.test.root.queries.GetMembersQueryHandler;
import com.viadeo.kasper.test.root.repositories.MemberConnectionsRepository;
import com.viadeo.kasper.test.root.repositories.MemberRepository;
import com.viadeo.kasper.test.timelines.Timelines;
import com.viadeo.kasper.test.timelines.entities.Status;
import com.viadeo.kasper.test.timelines.entities.Timeline;
import com.viadeo.kasper.test.timelines.repositories.StatusRepository;
import com.viadeo.kasper.test.timelines.repositories.TimelineRepository;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Path;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * This class manage with automated testing of KasperDocumentation HTTP/JSON endpoints
 * 
 * Add your JSON test response in src/test/resources/json
 * Just name it with the path ofthe resource to request, replacing slashes '/' by underscores '_'
 * Ex:
 *     "json/domain_Facebook_concept_Member.json"
 *     will request the kasper doc HTTP endpoint for /domain/Facebook/concept/Member and it will
 *     apply a simple JSON comparison of the response to the contents of the json test file
 *
 */
public class KasperDocResourceTest extends JerseyTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(KasperDocResourceTest.class);
	
	/**
	 * Use with cautious : check all outputs unless you'll introduce regressions in tests
	 * Set to true for ONLY ONE LAUNCH if you are sure of what you're doing, then switch it back to false
	 */
	private static final boolean UPDATE_TESTS = false;

    /**
     * Used to boot Kasper platform for documentation capabilities testing
     */
    private static final KasperConfigurator kasperConfigurator = new KasperConfigurator();

    // ------------------------------------------------------------------------

    /**
     * The wrapped resource, in order to not use injection in the Kasper doc resource
     *
     * Will delegate all calls to the standard Kasper doc root resource
     */
    @Path("/")
    public static class WrappedDocResource {

        public WrappedDocResource() { }

        @Path("/")
        public KasperDocResource2 delegate() {
//            final KasperDocResource res = new KasperDocResource();
//            res.setKasperLibrary(kasperConfigurator.getKasperLibrary());
//            return res;

            DomainDescriptor facebookDomainDescriptor = new DomainDescriptor(
                    Facebook.class
                    , ImmutableList.<QueryHandlerDescriptor>of(new QueryHandlerDescriptor(GetMembersQueryHandler.class, GetMembersQueryHandler.GetMembersQuery.class, GetMembersQueryHandler.MembersResult.class))
                    , ImmutableList.<CommandHandlerDescriptor>of(new CommandHandlerDescriptor(AddConnectionToMemberHandler.class, AddConnectionToMemberCommand.class))
                    , ImmutableList.<RepositoryDescriptor>of(
                        new RepositoryDescriptor(MemberRepository.class, DomainDescriptorFactory.retrieveAggregateDescriptor(Member.class)),
                        new RepositoryDescriptor(MemberConnectionsRepository.class, DomainDescriptorFactory.retrieveAggregateDescriptor(Member_connectedTo_Member.class))
                    )
                    , ImmutableList.<EventListenerDescriptor>of(new EventListenerDescriptor(MemberCreatedEventListener.class, MemberCreatedEvent.class))
            );

            DomainDescriptor applicationDomainDescriptor = new DomainDescriptor(
                    Applications.class
                    , ImmutableList.<QueryHandlerDescriptor>of()
                    , ImmutableList.<CommandHandlerDescriptor>of()
                    , ImmutableList.<RepositoryDescriptor>of(
                        new RepositoryDescriptor(ApplicationRepository.class, DomainDescriptorFactory.retrieveAggregateDescriptor(Application.class)),
                        new RepositoryDescriptor(ApplicationMemberFansRepository.class, DomainDescriptorFactory.retrieveAggregateDescriptor(Member_fanOf_Application.class))
                    )
                    , ImmutableList.<EventListenerDescriptor>of()
            );

            DomainDescriptor timelinesDomainDescriptor = new DomainDescriptor(
                    Timelines.class
                    , ImmutableList.<QueryHandlerDescriptor>of()
                    , ImmutableList.<CommandHandlerDescriptor>of()
                    , ImmutableList.<RepositoryDescriptor>of(
                        new RepositoryDescriptor(StatusRepository.class, DomainDescriptorFactory.retrieveAggregateDescriptor(Status.class)),
                        new RepositoryDescriptor(TimelineRepository.class, DomainDescriptorFactory.retrieveAggregateDescriptor(Timeline.class))
                    )
                    , ImmutableList.<EventListenerDescriptor>of()
            );

            DocumentedPlatform documentedPlatform = new DocumentedPlatform();
            documentedPlatform.registerDomain(Facebook.NAME, facebookDomainDescriptor);
            documentedPlatform.registerDomain(Applications.NAME, applicationDomainDescriptor);
            documentedPlatform.registerDomain(Timelines.NAME, timelinesDomainDescriptor);
            documentedPlatform.accept(new DefaultDocumentedElementInitializer());

            return new KasperDocResource2(documentedPlatform);
        }

    }

    static class TestConfiguration extends DefaultResourceConfig {

        public TestConfiguration() {
            super(WrappedDocResource.class);
            getProviderSingletons().add(new JacksonJsonProvider(new ObjectMapperKasperResolver().getContext(null)));
        }
    }

	// ------------------------------------------------------------------------

    public KasperDocResourceTest() {
        super(new LowLevelAppDescriptor.Builder(new TestConfiguration()).contextPath("/").build());
    }

    // ------------------------------------------------------------------------

	/**
	 * Main run test method
	 * 
	 * Scans all available json files, make the request to a standalone HTTP server and compare
	 * expected and retrieved results
	 * @throws URISyntaxException 
	 * 
	 * @throws Exception
	 */
    @Test
    public void test() throws IOException, JSONException, URISyntaxException {
    	
    	// Traverse available json responses ------------------------------------
        final Predicate<String> filter = new FilterBuilder().include(".*\\.json");
        final Reflections reflections = new Reflections(new ConfigurationBuilder()
                .filterInputsBy(filter)
                .setScanners(new ResourcesScanner())
                .setUrls(Arrays.asList(ClasspathHelper.forClass(this.getClass()))));

        final Set<String> resolved = reflections.getResources(Pattern.compile(".*"));
    	
        // Execute against Kasper documentation -------------------------------
        boolean failed = false;
    	for (final String jsonFilename : resolved) {
			LOGGER.info("** Test response file " + jsonFilename);
			
    		final String json = getJson(jsonFilename);
    		
    		final String path = jsonFilename
    				.replaceAll("json/", "")
    				.replaceAll("-", "/")
    				.replaceAll("\\.json", "");
	
			final WebResource webResource = resource();
	        final String responseMsg = webResource.path("/" + path).get(String.class);
	        
	        try {
	        	
	        	assertJsonEquals(responseMsg, json);
	        	
	        } catch (final JSONException e) {	 
	        	LOGGER.info("\t--> ERROR");
	        	throw e;
	        } catch (final AssertionError e) {
	        	if (!UPDATE_TESTS) {
	        		LOGGER.debug("*** RETURNED RESULT :");
	        		LOGGER.debug(new JSONObject(responseMsg).toString(2));
	        		LOGGER.info("\t--> ERROR");
	        		failed = true;
	        	}
	        }
	     
	    	if (UPDATE_TESTS) {
	    		final URL url = ClassLoader.getSystemResource(jsonFilename);
	    		final String filename = url.getFile().replaceAll("target/test-classes", "src/test/resources");
	    		LOGGER.info("\t--> SAVE to " + filename);
	    		final File file = new File(filename);
	    		final FileOutputStream fos = new FileOutputStream(file);
	    		fos.write(new JSONObject(responseMsg).toString(2).getBytes(Charsets.UTF_8));
	    		fos.close();
	    	}
	        
	        LOGGER.info("\t--> OK");
    	}
    	
    	if (failed) {
    		fail();
    	}
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * Get the JSON contents of the given claspath resource
     * 
     * @param jsonFilename the classpath resource relative location
     * @return the json file contents
     * @throws IOException
     */
    private String getJson(final String jsonFilename) throws IOException {    	
    	final InputStream jsonStream = ClassLoader.getSystemResourceAsStream(jsonFilename);
    	
    	if (null == jsonStream) {
    		fail(String.format("Unable to find response file %s", jsonFilename));
    	}
    	
    	final String jsonString = IOUtils.toString(jsonStream, "UTF-8"); // Check jsonStream null
    	jsonStream.close();
    	
    	return jsonString.isEmpty() ? "{}" : jsonString;
    }
    
    // ------------------------------------------------------------------------
    
    /**
     * Compare the content of two json files
     * The two contents are first normalized using org.json.JSONObject
     * Then raw strings are simply compared, after being cleaned of any space or carriage return occurences
     * 
     * @param jsonA the first json string
     * @param jsonB the second json string
     * @throws JSONException if one json string is not parseable
     */
    private void assertJsonEquals(final String jsonA, final String jsonB) throws JSONException {
    	final String jsonAA;
    	final String jsonBB;
    	
    	try {
    		jsonAA = new JSONObject(jsonA).toString(2);
    	} catch (final JSONException e) {
        	LOGGER.debug("*** BAD JSON :");
        	LOGGER.debug(jsonA);
        	throw e;
    	}
    	
    	try {
    		jsonBB = new JSONObject(jsonB).toString(2);
    	} catch (final JSONException e) {
        	LOGGER.debug("*** BAD JSON :");
        	LOGGER.debug(jsonB);
        	throw e;
    	}
    	
    	try {
    		assertEquals(jsonAA.replaceAll("[\\s\\n]", ""), jsonBB.replaceAll("[\\s\\n]", ""));
    	} catch (final AssertionError e) {
    		LOGGER.debug("*** DIFF RESULT (RESPONSE vs EXPECTED) :");
    		new StringLinesDiffer().output(jsonBB, jsonAA);
    		throw e;
    	}
    }
	
    // ------------------------------------------------------------------------
        
    /**
     * Outputs diff deltas between two String (splitted by lines)
     */
    public class StringLinesDiffer {
    	
            private List<String> stringToLines(final String data) {
            		return Lists.newArrayList(data.split("\\r?\\n"));
            }

            public void output(final String strA, final String strB) {
                    final List<String> original = stringToLines(strA);
                    final List<String> revised  = stringToLines(strB);
                    
                    final Patch patch = DiffUtils.diff(original, revised);

                    for (Delta delta: patch.getDeltas()) {
                            LOGGER.debug(delta.toString());
                    }
            }
    }    
    
}
