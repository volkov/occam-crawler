package svolkov.downloader;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

public class Main {

	public static void main(String[] args) throws MalformedURLException, InterruptedException, RequestNotSupported, URISyntaxException {
		ThreadPoolDownloadManager manager = new ThreadPoolDownloadManager();
		manager.setProviders(new ArrayList<ProtocolProvider>(Collections.singletonList(new HttpClientProvider())));
		DownloadRequest request = new DownloadRequest();
		request.setUri(new URI("http://www.kernel.org/pub/linux/kernel/v3.0/testing/linux-3.7-rc2.tar.bz2"));
		Long id = manager.addDownloadRequest(request);
		System.out.println(manager.getStatus(id));
		Thread.sleep(3000l);
		System.out.println(manager.getStatus(id));
		manager.cancelRequest(id);
		System.out.println(manager.getStatus(id));
		System.out.println(manager.getResponse(id));
		request.setUri(new URI("http://localhost:8081"));
		id = manager.addDownloadRequest(request);
		Thread.sleep(3000l);
		System.out.println(manager.cancelRequest(id));
		System.out.println(manager.getStatus(id));
		System.out.println(manager.getResponse(id));
		manager.shutdown();
	}
}
