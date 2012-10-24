package svolkov.downloader;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class Main {

	public static void nmain(String[] args) throws MalformedURLException, InterruptedException, RequestNotSupported {
		ThreadPoolDownloadManager manager = new ThreadPoolDownloadManager();
		manager.setProviders(new ArrayList<ProtocolProvider>(Collections.singletonList(new HttpClientProvider())));
		Long id = manager.addDownloadRequest(new DownloadRequest("http://www.kernel.org/pub/linux/kernel/v3.0/testing/linux-3.7-rc2.tar.bz2"));
		System.out.println(manager.getStatus(id));
		Thread.sleep(10000l);
		System.out.println(manager.getStatus(id));
		manager.cancelRequest(id);
		System.out.println(manager.getStatus(id));
		System.out.println(manager.getResponse(id));
		id = manager.addDownloadRequest(new DownloadRequest("http://localhost:8081"));
		Thread.sleep(3000l);
		manager.cancelRequest(id);
		System.out.println(manager.getStatus(id));
		manager.shutdown();
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ExecutorService pool = Executors.newFixedThreadPool(1);
		final HttpClient client = new DefaultHttpClient();
		final HttpGet get = new HttpGet("http://www.kernel.org/pub/linux/kernel/v3.0/testing/linux-3.7-rc2.tar.bz2");
		Future<String> future = pool.submit(new Callable<String>() {

			public String call() throws Exception {
				try {

					HttpResponse responce = client.execute(get);
					InputStream is = responce.getEntity().getContent();
					byte[] data = new byte[100000];
					System.out.println("Obtaining data");
					while (is.read(data, 0, data.length) != -1) {
						System.out.print(".");
						/*if (Thread.interrupted()) {
							System.out.println();
							System.out.println("Interrupted");
							throw new InterruptedException();
						}*/
					}
					System.out.println("[done]");
					is.close();
					return "done";
				} catch (Exception e) {
					System.out.println(e);
					throw e;
				}
			}
		});
		Thread.sleep(2000);
		System.out.println(future.cancel(true));
		System.out.println(future.isDone());
		System.out.println("cancelled");
		pool.shutdown();
		System.out.println("Thread Shutdown");
	}
}
