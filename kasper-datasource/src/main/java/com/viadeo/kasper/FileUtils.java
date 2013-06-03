package com.viadeo.kasper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.URL;

public class FileUtils {
	/** The logger of this class. */
	static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

	public static File getFileOrStream(String resource) throws IOException {
		URL url = ResourceUtils.getURL(resource);
		if (ResourceUtils.isFileURL(url)) {
			return ResourceUtils.getFile(url);
		}
		// Check if resource in not on the file system, but inside a jar file
		if (ResourceUtils.isJarURL(url)) {

			return getFileFromStream(url);
		}
		// Unknown case
		throw new IOException("Unknown protocol:Can't handle " + resource);
	}

	private static File getFileFromStream(URL url) throws IOException {

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
