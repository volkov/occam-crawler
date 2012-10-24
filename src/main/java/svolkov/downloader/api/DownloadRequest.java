package svolkov.downloader.api;

import java.io.Serializable;
import java.net.URI;

/**
 * Stores info about request.
 * @author vsa
 *
 */
public class DownloadRequest implements Serializable {

	private static final long serialVersionUID = 2646185615781725091L;

	private URI uri;

	/**
	 * Returns uri.
	 * @return uri.
	 */
	public URI getUri() {
		return uri;
	}

	/**
	 * Sets uri.
	 * @param uri.
	 */
	public void setUri(URI uri) {
		this.uri = uri;
	}

}
