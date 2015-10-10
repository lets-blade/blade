package com.blade.asset;

/**
 * Implementations of this interface provide mechanisms to load assets.
 *
 * @author German Escobar
 */
public interface AssetLoader {

	/**
	 * Retrieves the asset identified with the <code>uri</code>.
	 *
	 * @param uri represents the location of the asset.
	 *
	 * @return an {@link Asset} object or null if not found.
	 */
	Asset load(String uri);

}
