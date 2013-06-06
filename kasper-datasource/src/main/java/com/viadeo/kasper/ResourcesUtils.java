// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import java.io.*;
import java.net.URL;

public class ResourcesUtils {
	/** The logger of this class. */
	static final Logger LOGGER = LoggerFactory.getLogger(ResourcesUtils.class);

	// let checked exception to let caller to decide what to do, ignoring error or throwing runtime exception
	
	public File getFile(String resourceName) throws IOException  {
		// Try opening resource
		// Convert resourceName in URL mode
		Strings.isNullOrEmpty(resourceName);

		URL url = this.getClass().getClassLoader().getResource(resourceName);
		if (url == null) {
			throw new IOException("Unknown file or resource : Can't read " + resourceName);
		}
		String protocol = url.getProtocol();
		if (protocol.equals("file")) {
			return new File(url.getFile());
		}
		// Else load stream (http, jar, ...);
		try {
			return getFileFromStream(url);
		} catch (IOException e) {
			// convert Exception type to removed catched exception
			throw new IOException(e);
		}
		
	}
	

	private File getFileFromStream(URL url) throws IOException {

		// Create a File object on file system to be able to use Viadeo library which currently accept only File resource as parameter
		// If the file creation sentence is after the inputStream creation, need to explicitely close inputStream in a try-catch statment

		// Retrieve the filename part (without protocol)
		String urlAsString = url.toString();

		String fileName = urlAsString.substring(urlAsString.lastIndexOf('/') + 1, urlAsString.length());
		// Remove extension, no need
		String fileNamePrefix = fileName.substring(0, fileName.lastIndexOf('.'));
		// And use this filename part as prefix (dispatcher, datasource, proxy, ...)

		File file = null;
		try {
			file = File.createTempFile(fileNamePrefix, ".tmp");
		file.deleteOnExit();
		} catch (IOException ioe) {
			// Catch the exception to add more information before throwing it
            throw new IOException("Can't create tmp file, check directory right access or disk space usage", ioe);
		}

		InputStream inputStream = new URL(url.toString()).openStream();
		if (inputStream == null) {
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

		} catch (IOException ioe) {
            LOGGER.error("Loading file failed at location : {}", urlAsString);
            throw new IOException(
                    "Can't create temporary file, check tmp directory or content of the file in the .jar library ["
                    + urlAsString + "]",
                    ioe);
		}

		finally {
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
