// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.util;


import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.springframework.util.Assert;

import com.viadeo.kasper.ResourcesUtils;

import static junit.framework.Assert.assertEquals;

public class ResourcesUtilsTests {

    @Test
    public void findFileOnFileSystem() throws IOException {
        // Given
    	ResourcesUtils res = new ResourcesUtils(); 
        // When
        File file = res.getFile("dispatcher.json");

        // Then
        Assert.notNull(file);
        Assert.isTrue(file.exists());
    }

    @Test  // : Currently disabled, need to add a jar file
    public void findFileInJar() throws IOException {
        // Given
    	ResourcesUtils res = new ResourcesUtils();
    	
        // When
    	// Be careful, trying to get file inside commons-dbcp-1.4.jar
        File file = res.getFile("testpool.jocl");

        // Then
        Assert.notNull(file);
        Assert.isTrue(file.exists());
    }

    @Test  // : Currently disabled, need to add a jar file
    public void findFileInSudirectoryThroughClasspath() throws IOException {
        // Given
    	ResourcesUtils res = new ResourcesUtils();
    	
        // When
    	// Be careful, trying to get file inside commons-dbcp-1.4.jar
        File file = res.getFile("com/kasper/file.json");

        // Then
        Assert.notNull(file);
        Assert.isTrue(file.exists());
    }

    @Test (expected=IOException.class)
    public void failedFileOnWindowsFileSystem() throws IOException {
        // Given
    	ResourcesUtils res = new ResourcesUtils();
    	
        // When
    	// Be careful, trying to get file inside junit.jar
        File file = res.getFile("c:/dispatcher.json");

        // Then
        // Expect IOException
        Assert.isNull(file);
    }
}
