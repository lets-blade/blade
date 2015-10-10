package com.blade.asset;

import java.io.InputStream;

/**
 * Represents a static asset resource.
 *
 * @author German Escobar
 */
public class Asset {

	/**
	 * The input stream of the asset.
	 */
	private final InputStream inputStream;

	/**
	 * The name of the asset.
	 */
	private final String name;

	/**
	 * The content length of the asset.
	 */
	private final long length;

	/**
	 * The epoch timestamp when this asset was last modified
	 */
	private final long lastModified;

	/**
	 * The content type of the asset.
	 */
	private final String contentType;


    public Asset(InputStream inputStream, String name, String contentType, long length) {
        this(inputStream, name, contentType, length, 0);
    }

	public Asset(InputStream inputStream, String name, String contentType, long length, long lastModified) {
		this.inputStream = inputStream;
		this.name = name;
		this.contentType = contentType;
		this.length = length;
		this.lastModified = lastModified;
	}

	public String getName() {
		return name;
	}

	public String getContentType() {
		return contentType;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public long getLength() {
		return length;
	}

	public long getLastModified() {
		return lastModified;
	}
}
