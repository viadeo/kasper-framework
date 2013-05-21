package com.viadeo.kasper.doc.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

import javax.ws.rs.Path;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;
import com.viadeo.kasper.core.boot.AbstractDocumentationProcessor;
import com.viadeo.kasper.core.boot.AnnotationRootProcessor;
import com.viadeo.kasper.core.boot.CommandsDocumentationProcessor;
import com.viadeo.kasper.core.boot.ConceptsDocumentationProcessor;
import com.viadeo.kasper.core.boot.DomainsDocumentationProcessor;
import com.viadeo.kasper.core.boot.EventsDocumentationProcessor;
import com.viadeo.kasper.core.boot.HandlersDocumentationProcessor;
import com.viadeo.kasper.core.boot.ListenersDocumentationProcessor;
import com.viadeo.kasper.core.boot.QueryServicesDocumentationProcessor;
import com.viadeo.kasper.core.boot.RelationsDocumentationProcessor;
import com.viadeo.kasper.core.boot.RepositoriesDocumentationProcessor;
import com.viadeo.kasper.doc.KasperLibrary;
import com.viadeo.kasper.doc.web.ObjectMapperCustomResolver;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * This class manage with automated testing of KasperDocumentation HTTP/JSON endpoints
 * 
 * Add your JSON test result in src/test/resources/json
 * Just name it with the path ofthe resource to request, replacing slashes '/' by underscores '_'
 * Ex:
 *     "json/domain_Facebook_concept_Member.json"
 *     will request the kasper doc HTTP endpoint for /domain/Facebook/concept/Member and it will
 *     apply a simple JSON comparison of the result to the contents of the json test file
 *
 */
public class KasperDocResourceTest extends JerseyTest {

	private static final Logger LOGGER = Logger.getLogger(KasperDocResourceTest.class);
	
	/**
	 * Use with cautious : check all outputs unless you'll introduce regressions in tests
	 * Set to true for ONLY ONE LAUNCH if you are sure of what you're doing, then switch it back to false
	 */
	private static final boolean UPDATE_TESTS = false;
	
	/**
	 * The KasperLibrary instance to be used
	 */
	private static KasperLibrary kasperLibrary;
	
	/**
	 * The processors to register
	 */
	static final Class<?>[] PROCESSORS = {
		CommandsDocumentationProcessor.class,
		ConceptsDocumentationProcessor.class,
		DomainsDocumentationProcessor.class,
		EventsDocumentationProcessor.class,
		HandlersDocumentationProcessor.class,
		ListenersDocumentationProcessor.class,
		RelationsDocumentationProcessor.class,
		RepositoriesDocumentationProcessor.class,
		QueryServicesDocumentationProcessor.class
	};
	
	/**
	 * The wrapped resource, in order to not use injection in the Kasper doc resource
	 * 
	 * Will delegate all calls to the standard Kasper doc root resource
	 */
	@Path("/")
	public static class WrappedDocResource {
		
		public WrappedDocResource() { }
		
		@Path("/")
		public KasperDocResource delegate() {
			final KasperDocResource res = new KasperDocResource();
			res.setKasperLibrary(kasperLibrary);
			return res;
		}
		
	}
	
	static class TestConfiguration extends DefaultResourceConfig {
		
		public TestConfiguration() {
			super(WrappedDocResource.class);
			getProviderSingletons().add(new JacksonJsonProvider(new ObjectMapperCustomResolver().getContext(null)));
		}
	}
	
	// ------------------------------------------------------------------------
	
	/**
	 * Bootstrap the Jersey configuration
	 * Boot the Kasper root processor with only Kasper documentation processors
	 * 
	 * @throws Exception
	 */
	public KasperDocResourceTest() throws Exception {
        super(new LowLevelAppDescriptor.Builder(new TestConfiguration()).contextPath("/").build());
        
        final AnnotationRootProcessor rootProcessor = new AnnotationRootProcessor();
		
        kasperLibrary = new KasperLibrary(); // Assign the static instance
        
        for (final Class<?> processorClazz : PROCESSORS) {
            final AbstractDocumentationProcessor<?,?> processor = 
            		(AbstractDocumentationProcessor<?, ?>) processorClazz.newInstance();
            processor.setKasperLibrary(kasperLibrary);
            rootProcessor.registerProcessor(processor);
        }
       
		rootProcessor.addScanPrefix("com.viadeo.kasper.test"); // Scan test classes (test use case)
		rootProcessor.setDoNotScanDefaultPrefix(true); // Do not use default boot processors
		
		rootProcessor.boot();
    }
	
	// ------------------------------------------------------------------------

	/**
	 * Main run test method
	 * 
	 * Scans all available json files, makethe request to a standalone HTTP server and compare
	 * expected and retrieved results
	 * @throws URISyntaxException 
	 * 
	 * @throws Exception
	 */
    @Test
    public void test() throws IOException, JSONException, URISyntaxException {
    	
    	// Traverse available json results ------------------------------------
        final Predicate<String> filter = new FilterBuilder().include(".*\\.json");
        final Reflections reflections = new Reflections(new ConfigurationBuilder()
                .filterInputsBy(filter)
                .setScanners(new ResourcesScanner())
                .setUrls(Arrays.asList(ClasspathHelper.forClass(this.getClass()))));

        final Set<String> resolved = reflections.getResources(Pattern.compile(".*"));
    	
        // Execute against Kasper documentation -------------------------------
        boolean failed = false;
    	for (final String jsonFilename : resolved) {
			LOGGER.info("** Test result file " + jsonFilename);
			
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
    		fail(String.format("Unable to find result file %s", jsonFilename));
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
                            LOGGER.debug(delta);
                    }
            }
    }    
    
}
