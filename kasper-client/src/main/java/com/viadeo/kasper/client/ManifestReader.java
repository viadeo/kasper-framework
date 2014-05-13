// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static com.google.common.base.Preconditions.checkNotNull;

public class ManifestReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManifestReader.class);

    public static final String KASPER_VERSION_NAME = "Kasper-Version";

    private final URL url;
    private Optional<Manifest> manifest;

    public ManifestReader(final Class<?> clazz) {
        this(toUrl(toPath(clazz)));
    }

    public ManifestReader(final URL url) {
        this.url = checkNotNull(url, "undefined url");
        this.manifest = Optional.absent();
    }

    protected static String toPath(final Class<?> clazz) {
        checkNotNull(clazz, "unable to define a path from an undefined class");
        return clazz.getName().replaceAll("\\.", "/") + ".class";
    }

    protected static URL toUrl(final String path) {
        checkNotNull(path, "unable to define a path from an undefined class");
        return Thread.currentThread().getContextClassLoader().getResource(path);
    }

    public ManifestReader read() {
        Manifest manifest = null;

        try {
            final JarURLConnection connection = (JarURLConnection) url.openConnection();
            final JarFile jar = connection.getJarFile();
            manifest = jar.getManifest();
        } catch (IOException e) {
            LOGGER.error("No found jar file from the specified URL '{}'", url, e);
        }

        this.manifest = Optional.fromNullable(manifest);
        return this;
    }

    public Optional<Manifest> getManifest() {
        return manifest;
    }

    public Optional<String> getKasperVersion() {
        if(manifest.isPresent()) {
            return Optional.fromNullable(manifest.get().getMainAttributes().getValue(KASPER_VERSION_NAME));
        }
        return Optional.absent();
    }
}
