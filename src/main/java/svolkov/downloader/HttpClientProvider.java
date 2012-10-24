package svolkov.downloader;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpClientProvider implements ProtocolProvider {

	public boolean accepts(DownloadRequest request) {
		URL url = request.getUrl();
		return url.toString().startsWith("http:");
	}

	public FetchCallable createCallable(final DownloadRequest request) {
		return new FetchCallable() {

			private HttpGet get = new HttpGet(request.getUrl().toString());

			public DownloadResponse call() throws Exception {
				try {
					HttpClient client = new DefaultHttpClient();
					HttpResponse responce = client.execute(get);
					DownloadResponse result = new DownloadResponse();
					InputStream is = responce.getEntity().getContent();
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					byte[] buffer = new byte[2048];
					int len;
					while ((len = is.read(buffer, 0, buffer.length)) != -1) {
						out.write(buffer, 0, len);
					}
					result.setContent(out.toByteArray());
					return result;
				} catch (Exception e) {
					System.out.println(e);
					throw e;
				}
			}

			public void cancel() {
				HttpGet inner = get;
				if (inner != null) {
					inner.abort();
				}
			}
		};
	}

}
