package svolkov.downloader;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

import svolkov.downloader.api.DownloadManager;
import svolkov.downloader.api.DownloadRequest;
import svolkov.downloader.api.DownloadResponse;
import svolkov.downloader.api.DownloadStatus;
import svolkov.downloader.api.FetchCallable;
import svolkov.downloader.api.ProtocolProvider;
import svolkov.downloader.api.RequestNotSupported;

/**
 * Download manager based on {@link ThreadPoolExecutor}
 * @author vsa
 *
 */
public class ThreadPoolDownloadManager implements DownloadManager {

	private Map<Long, FetchInfo> data = new ConcurrentHashMap<Long, FetchInfo>();

	private ExecutorService executorService;

	private List<? extends ProtocolProvider> providers;

	private AtomicLong idSequence = new AtomicLong(0);

	/**
	 * Creates manager with specified number of threads and list of providers.
	 * @param threads
	 * @param providers
	 */
	public ThreadPoolDownloadManager(int threads, List<? extends ProtocolProvider> providers) {
		executorService = Executors.newFixedThreadPool(threads);
		this.providers = providers;
	}

	@Override
	public Long addDownloadRequest(DownloadRequest request) throws RequestNotSupported {

		FetchCallable task = null;
		for (ProtocolProvider provider : providers) {
			if (provider.accepts(request)) {
				task = provider.createCallable(request);
			}
		}
		if (task == null) {
			throw new RequestNotSupported();
		}

		final FetchInfo info = new FetchInfo();
		info.setTask(task);
		info.setFuture(executorService.submit(new Callable<DownloadResponse>() {

			public DownloadResponse call() throws Exception {
				info.setInProgress(true);
				return info.getTask().call();
			}
		}));

		long id = idSequence.incrementAndGet();
		data.put(id, info);
		return id;
	}

	@Override
	public DownloadStatus getStatus(Long id) {
		FetchInfo info = data.get(id);
		return getStatus(info);
	}

	private DownloadStatus getStatus(FetchInfo info) {
		if (info == null) {
			return null;
		}
		if (info.getFuture().isCancelled()) {
			return DownloadStatus.CANCELLED;
		}
		if (info.getFuture().isDone()) {
			return DownloadStatus.DONE;
		}
		if (info.isInProgress()) {
			return DownloadStatus.IN_PROGRESS;
		}
		return DownloadStatus.PENDING;
	}

	@Override
	public boolean cancelRequest(Long id) {
		FetchInfo info = data.get(id);
		if (info == null) {
			return false;
		}
		boolean result = info.getFuture().cancel(true);
		info.getTask().clean();
		return result;
	}

	@Override
	public DownloadResponse getResponse(Long id) {
		FetchInfo info = data.get(id);
		if (getStatus(info) != DownloadStatus.DONE) {
			return null;
		}
		try {
			return info.getFuture().get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
		}
		return null;
	}

	@Override
	public DownloadResponse waitResponse(Long id) {
		FetchInfo info = data.get(id);
		try {
			return info.getFuture().get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
		} catch (CancellationException e) {
		}
		return null;

	}

	@Override
	public void shutdown() {
		executorService.shutdown();
	}

}
