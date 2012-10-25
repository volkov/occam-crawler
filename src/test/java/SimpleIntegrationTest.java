import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import svolkov.downloader.HttpClientProvider;
import svolkov.downloader.ThreadPoolDownloadManager;
import svolkov.downloader.api.DownloadManager;
import svolkov.downloader.api.DownloadRequest;
import svolkov.downloader.api.DownloadResponse;
import svolkov.downloader.api.DownloadStatus;

public class SimpleIntegrationTest {

	private static final int PORT = 8082;
	private static final String URL = "http://localhost:8082/";
	private static final String VALID_HEADER = "HTTP/1.1 200 OK\n" + "Server: Mathopd/1.5p6\n" + "Date: Thu, 06 Jan 2000 01:29:52 GMT\n" + "Content-Type: text/html\n"
			+ "Content-Length: 93\n" + "Last-Modified: Thu, 06 Jan 2000 01:29:52 GMT\n\n";
	private static final String VALID_CONTENT = "<html><head><meta http-equiv=Refresh content='0; url=index.php'></head><body></body></html>\n\n";

	@Test
	public void testSuccess() throws Exception {
		DownloadManager manager = getManager(1);
		ServerSocket socket = startSimpleServer(1, 0, 0, VALID_HEADER, VALID_CONTENT);

		DownloadRequest request = new DownloadRequest();
		request.setUri(new URI(URL));
		long id = manager.addDownloadRequest(request);
		Thread.sleep(500);
		Assert.assertEquals(DownloadStatus.DONE, manager.getStatus(id));
		DownloadResponse responce = manager.getResponse(id);
		Assert.assertTrue(responce.isSuccess());
		manager.shutdown();
		socket.close();
		Thread.sleep(500);
	}

	@Test
	public void testCancel() throws Exception {
		DownloadManager manager = getManager(1);
		ServerSocket socket = startSimpleServer(1, 0, 10000, VALID_HEADER, VALID_CONTENT);

		DownloadRequest request = new DownloadRequest();
		request.setUri(new URI(URL));
		long id = manager.addDownloadRequest(request);
		Thread.sleep(500);
		Assert.assertEquals(DownloadStatus.IN_PROGRESS, manager.getStatus(id));
		Assert.assertTrue(manager.cancelRequest(id));
		DownloadResponse responce = manager.getResponse(id);
		Assert.assertNull(responce);
		manager.shutdown();
		socket.close();
		Thread.sleep(500);
	}

	@Test
	public void testConcurrent() throws Exception {
		DownloadManager manager = getManager(50);
		ServerSocket socket = startSimpleServer(50, 2000, 0, VALID_HEADER, VALID_CONTENT);
		List<Long> ids = new ArrayList<Long>();
		for (int i = 0; i < 50; i++) {
			DownloadRequest request = new DownloadRequest();
			request.setUri(new URI(URL));
			ids.add(manager.addDownloadRequest(request));
		}
		Thread.sleep(2500);
		for (Long id : ids) {
			Assert.assertEquals(DownloadStatus.DONE, manager.getStatus(id));
			DownloadResponse responce = manager.getResponse(id);
			Assert.assertTrue(responce.isSuccess());
			System.out.println(responce);
		}
		manager.shutdown();
		socket.close();
		Thread.sleep(500);
	}

	private ThreadPoolDownloadManager getManager(int threads) {
		ThreadPoolDownloadManager result = new ThreadPoolDownloadManager(threads, Collections.singletonList(new HttpClientProvider()));
		return result;
	}

	private ServerSocket startSimpleServer(final int requestCount, final long beforeDelay, final long afterDelay, final String header, final String body) throws Exception {
		System.out.println("Trying start server");
		final ServerSocket server = new ServerSocket(PORT);
		Thread t = new Thread(new Runnable() {

			public void run() {
				try {
					System.out.println("server started");
					List<Thread> threads = new ArrayList<Thread>();
					for (int i = 0; i < requestCount; i++) {
						final Socket socket = server.accept();
						System.out.println("socket opened");
						Thread t = new Thread(new Runnable() {

							public void run() {
								try {
									byte[] buff = new byte[10000];
									socket.getInputStream().read(buff);

									//socket.getInputStream().read();
									OutputStream os = socket.getOutputStream();
									Thread.sleep(beforeDelay);
									os.write(header.getBytes());
									Thread.sleep(afterDelay);
									os.write(body.getBytes());
									os.flush();
									socket.close();
									System.out.println("socket closed");
								} catch (InterruptedException e) {
								} catch (IOException e) {
								}
							}
						});
						t.start();
						threads.add(t);
					}
				} catch (Exception e) {
				}
			}
		});
		t.start();
		return server;
	}

}
