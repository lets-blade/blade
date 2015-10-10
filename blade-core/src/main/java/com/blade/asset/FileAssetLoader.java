package com.blade.asset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.jetty.http.MimeTypes;

/**
 * An {@link AssetLoader} implementation that uses the file system to retrieve assets.
 *
 * @author German Escobar
 */
public class FileAssetLoader implements AssetLoader {

	private static final String DEFAULT_BASE_DIRECTORY = "assets";

	private File parent;

	/**
	 * Constructor. Initializes the object with the default base directory.
	 */
	public FileAssetLoader() {
		this(DEFAULT_BASE_DIRECTORY);
	}

	/**
	 * Constructor. Initializes the object with the specified base <code>directory</code>.
	 *
	 * @param directory
	 */
	public FileAssetLoader(String directory) {
		this.parent = new File(directory);
	}

	public FileAssetLoader(File parent) {
		this.parent = parent;
	}

	@Override
	public Asset load(String fileName) {
		try {
			File file = new File(parent, fileName);

			if (!file.exists() || !file.isFile()) {
				return null;
			}

			long lastModified = file.lastModified();
			MimeTypes mimeTypes = new MimeTypes();

			String contentType = "text/plain";
			String buffer = mimeTypes.getMimeByExtension(file.getName());
			if (buffer != null) {
				contentType = buffer.toString();
			}

			return new Asset(new FileInputStream(file), file.getName(), contentType, file.length(), lastModified);
		} catch (FileNotFoundException e) {
			return null;
		}
	}

}
