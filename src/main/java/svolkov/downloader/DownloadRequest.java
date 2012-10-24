package svolkov.downloader;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadRequest implements Serializable {

	private static final long serialVersionUID = 2646185615781725091L;

	public DownloadRequest(String url) throws MalformedURLException {
		this.url = new URL(url);
	}

	private URL url;

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

}
