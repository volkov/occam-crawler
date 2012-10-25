package svolkov.downloader;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import svolkov.downloader.api.DownloadRequest;
import svolkov.downloader.api.DownloadResponse;
import svolkov.downloader.api.FetchCallable;
import svolkov.downloader.api.ProtocolProvider;

/**
 * Protocol provider using {@link HttpClient}.
 * @author vsa
 *
 */
public class HttpClientProvider implements ProtocolProvider {

	/**
	 * Accepts URI stating with http: or https:
	 */
	@Override
	public boolean accepts(DownloadRequest request) {
		URI uri = request.getUri();
		if (uri == null) {
			return false;
		}
		return uri.toString().startsWith("http:") || uri.toString().startsWith("https:");
	}

	@Override
	public FetchCallable createCallable(final DownloadRequest request) {
		return new FetchCallable() {

			private final HttpGet get = new HttpGet(request.getUri());

			public DownloadResponse call() throws Exception {
				InputStream is = null;
				DownloadResponse result = new DownloadResponse();
				try {
					HttpResponse responce = null;
					HttpClient client = new DefaultHttpClient();
					responce = client.execute(get);
					StatusLine status = responce.getStatusLine();
					result.setProtocolResponse(status.getStatusCode());
					result.setMessage(status.toString());

					is = responce.getEntity().getContent();
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					byte[] buffer = new byte[2048];
					int len;
					while ((len = is.read(buffer, 0, buffer.length)) != -1) {
						out.write(buffer, 0, len);
					}

					result.setContent(out.toByteArray());
					result.setSuccess(true);
					return result;
				} catch (Exception e) {
					if (Thread.interrupted()) {
						throw e;
					}
					result.setSuccess(false);
					result.setMessage(e.getMessage());
					return result;
				} finally {
					if (is != null) {
						is.close();
					}
				}
			}

			public void clean() {
				get.abort();
			}
		};
	}

}
