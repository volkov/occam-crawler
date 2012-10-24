package svolkov.downloader;

import java.util.concurrent.Future;

public class FetchInfo {

	private DownloadRequest request;

	private DownloadResponse responce;

	private FetchCallable task;

	private Future<DownloadResponse> future;

	private volatile boolean inProgress;

	public DownloadRequest getRequest() {
		return request;
	}

	public void setRequest(DownloadRequest request) {
		this.request = request;
	}

	public DownloadResponse getResponce() {
		return responce;
	}

	public void setResponce(DownloadResponse responce) {
		this.responce = responce;
	}

	public FetchCallable getTask() {
		return task;
	}

	public void setTask(FetchCallable task) {
		this.task = task;
	}

	public Future<DownloadResponse> getFuture() {
		return future;
	}

	public void setFuture(Future<DownloadResponse> future) {
		this.future = future;
	}

	public boolean isInProgress() {
		return inProgress;
	}

	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}

}
