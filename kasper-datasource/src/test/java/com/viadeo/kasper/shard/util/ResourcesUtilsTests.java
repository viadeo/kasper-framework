// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.shard.util;


import com.viadeo.kasper.shard.util.ResourcesUtils;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ResourcesUtilsTests {

    public static String URL_ON_FILESYSTEM;

    @BeforeClass
    public static void init() throws IOException {

        File f = File.createTempFile("test", "txt");
        URL_ON_FILESYSTEM = f.getAbsolutePath();
        System.out.println(URL_ON_FILESYSTEM);
    }


    @Test
    public void findFileOnFileSystem() throws IOException {
        // Given
    	final ResourcesUtils res = new ResourcesUtils();

        // When
        final File file = res.getFile(URL_ON_FILESYSTEM);


        // Then
        Assert.assertNotNull(file);
        Assert.assertTrue(file.exists());
    }

    @Test @Ignore
    public void readFileOnFileSystem() throws IOException {
        // Given
        final ResourcesUtils res = new ResourcesUtils();

        // When
        final File file = res.getFile(URL_ON_FILESYSTEM);
        String text = new Scanner( file ).useDelimiter("\\A").next();
        System.out.println(text);
        // Then
        Assert.assertNotNull(file);
        Assert.assertTrue(file.exists());
    }

    @Test  // : Currently disabled, need to add a jar file
    public void findFileInJar() throws IOException {
        // Given
    	final ResourcesUtils res = new ResourcesUtils();
    	
        // When
    	// Be careful, trying to get file inside commons-dbcp-1.4.jar
        final File file = res.getFile("testpool.jocl");

        // Then
        Assert.assertNotNull(file);
        Assert.assertTrue(file.exists());
    }

    @Test  // : Currently disabled, need to add a jar file
    public void findFileInSudirectoryThroughClasspath() throws IOException {
        // Given
    	final ResourcesUtils res = new ResourcesUtils();
    	
        // When
    	// Be careful, trying to get file inside commons-dbcp-1.4.jar
        final File file = res.getFile("com/kasper/file.json");

        // Then
        Assert.assertNotNull(file);
        Assert.assertTrue(file.exists());
    }

    @Test (expected=IOException.class)
    public void failedFileOnWindowsFileSystem() throws IOException {
        // Given
    	final ResourcesUtils res = new ResourcesUtils();
    	
        // When
    	// Be careful, trying to get file inside junit.jar
        final File file = res.getFile("c:/dispatcher.json");

        // Then
        // Expect IOException
        Assert.assertNull(file);
    }


}
