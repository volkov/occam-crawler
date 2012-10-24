package svolkov.downloader;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class ThreadPoolDownloadManager implements DownloadManager {

	private Map<Long, FetchInfo> data = new ConcurrentHashMap<Long, FetchInfo>();

	private ExecutorService executorService = Executors.newFixedThreadPool(10);

	private List<ProtocolProvider> providers;

	private AtomicLong idSequence = new AtomicLong(0);

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

		long id = idSequence.incrementAndGet();
		final FetchInfo info = new FetchInfo();
		info.setTask(task);
		info.setRequest(request);
		info.setFuture(executorService.submit(new Callable<DownloadResponse>() {

			public DownloadResponse call() throws Exception {
				info.setInProgress(true);
				info.setResponce(info.getTask().call());
				return null;
			}
		}));
		data.put(id, info);
		return id;
	}

	public DownloadStatus getStatus(Long id) {
		FetchInfo info = data.get(id);
		return getStatus(info);
	}

	private DownloadStatus getStatus(FetchInfo info) {
		if (info == null) {
			return null;
		}
		if (info.getFuture().isDone()) {
			return DownloadStatus.DONE;
		}
		if (info.isInProgress()) {
			return DownloadStatus.IN_PROGRESS;
		}
		return DownloadStatus.PENDING;
	}

	public void cancelRequest(Long id) {
		FetchInfo info = data.get(id);
		if (info == null) {
			return;
		}
		info.getTask().cancel();
		info.getFuture().cancel(true);
	}

	public DownloadResponse getResponse(Long id) {
		FetchInfo info = data.get(id);
		if (getStatus(info) != DownloadStatus.DONE) {
			return null;
		}
		return info.getResponce();
	}

	public List<ProtocolProvider> getProviders() {
		return providers;
	}

	public void setProviders(List<ProtocolProvider> providers) {
		this.providers = providers;
	}

	public void shutdown() {
		executorService.shutdown();
	}

}
