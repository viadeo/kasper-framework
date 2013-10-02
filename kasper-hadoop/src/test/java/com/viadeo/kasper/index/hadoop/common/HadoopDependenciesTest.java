// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.index.hadoop.common;

import org.apache.hadoop.fs.Path;
import org.junit.Test;

import java.io.*;

import static com.viadeo.kasper.index.hadoop.common.HadoopDependencies.Dependency;
import static com.viadeo.kasper.index.hadoop.common.HadoopDependencies.DependencyType;
import static junit.framework.Assert.assertEquals;

public class HadoopDependenciesTest {

    private File createFile() throws IOException {
        final File tmpFile = new File(
            System.getProperty("java.io.tmpdir"),
            Long.toString(System.nanoTime())
        );
        tmpFile.createNewFile();
        tmpFile.deleteOnExit();
        return tmpFile;
    }

    private Path createPath() throws IOException {
        return new Path(createFile().getAbsolutePath());
    }

    // ------------------------------------------------------------------------

    @Test
    public void testFileDependency() throws IOException {
        // Given
        final File local = createFile();
        final Path remote = createPath();

        // When
        final Dependency dep = new Dependency(local, remote, DependencyType.LIBRARY);

        // Then
        assertEquals(local, dep.getLocal());
        assertEquals(remote, dep.getRemote());
        assertEquals(new Path(local.getAbsolutePath()), dep.getLocalAsPath());
        assertEquals(DependencyType.LIBRARY, dep.getType());
    }

    @Test
    public void testStreamDependency() throws IOException {
        // Given
        final File local = createFile();
        local.createNewFile();
        local.deleteOnExit();
        final OutputStream out = new FileOutputStream(local);
        out.write(42);
        out.close();
        final InputStream in = new FileInputStream(local);
        final Path remote = createPath();

        // When
        final Dependency dep = new Dependency(in, remote, DependencyType.LIBRARY);

        // Then
        final InputStream in2 = new FileInputStream(local);
        final File depFile = dep.getLocal();
        final InputStream depIn = new FileInputStream(depFile);

        final byte[] bufferIn = new byte[8];
        final int readIn = in2.read(bufferIn);
        assertEquals(1, readIn);

        final byte[] bufferDep = new byte[8];
        final int readDep = depIn.read(bufferDep);
        assertEquals(1, readDep);

        assertEquals(new String(bufferIn), new String(bufferDep));
        assertEquals(new Path(depFile.getAbsolutePath()), dep.getLocalAsPath());
        assertEquals(remote, dep.getRemote());
        assertEquals(DependencyType.LIBRARY, dep.getType());
    }

    // ------------------------------------------------------------------------



}
