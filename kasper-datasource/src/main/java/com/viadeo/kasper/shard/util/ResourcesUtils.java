// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.shard.util;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;

public class ResourcesUtils {
	static final Logger LOGGER = LoggerFactory.getLogger(ResourcesUtils.class);

	// let checked exception to let caller to decide what to do, ignoring error or throwing runtime exception
	
	public File getFile(final String resourceName) throws IOException  {

		// Try opening resource
		// Convert resourceName in URL mode
		Strings.isNullOrEmpty(resourceName);

        File fileOnDisk = new File(resourceName);
        if (fileOnDisk.exists()) {
            return fileOnDisk;
        }

        final URL url = this.getClass().getClassLoader().getResource(resourceName);
        if (null == url) {
            throw new IOException("Unknown file or resource : Can't read " + resourceName);
        }

        final String protocol = url.getProtocol();
        if (protocol.equals("file")) {
            return new File(url.getFile());
        }

		// Else load stream (http, jar, ...);
		return getFileFromStream(url);

	}

    // ------------------------------------------------------------------------

	private File getFileFromStream(final URL url) throws IOException {

		// Create a File object on file system to be able to use Viadeo library which currently accept only File resource as parameter
		// If the file creation sentence is after the inputStream creation, need to explicitely close inputStream in a try-catch statment

		// Retrieve the filename part (without protocol)
		final String urlAsString = url.toString();
		final String fileName = urlAsString.substring(urlAsString.lastIndexOf('/') + 1, urlAsString.length());

		// Remove extension, no need
		final String fileNamePrefix = fileName.substring(0, fileName.lastIndexOf('.'));
		// And use this filename part as prefix (dispatcher, datasource, proxy, ...)

		File file = null;
		try {
			file = File.createTempFile(fileNamePrefix, ".tmp");
		    file.deleteOnExit();
		} catch (final IOException ioe) {
			// Catch the exception to add more information before throwing it
            throw new IOException("Can't create tmp file, check directory right access or disk space usage", ioe);
		}

		final InputStream inputStream = new URL(url.toString()).openStream();
		if (null == inputStream) {
			throw new IOException("Can't open temp file for handling stream as file" + url.toString());
		}

		// If no Exception 
		OutputStream out = null;
		try {
			// write the inputStream to a FileOutputStream, in temporary file 
			out = new FileOutputStream(file);
			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}

			out.flush();

		} catch (final IOException ioe) {
            LOGGER.error("Loading file failed at location : {}", urlAsString);
            throw new IOException(
                    "Can't create temporary file, check tmp directory or content of the file in the .jar library ["
                    + urlAsString + "]",
                    ioe);
		} finally {
			try {
				inputStream.close();
			}
			// No catch IOException, already thrown (in signature), but need to continue closing resources			
			finally {
				out.close();
			}

		}

		return file;
	}

}
