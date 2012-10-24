package svolkov.downloader;

import java.util.concurrent.Future;

import svolkov.downloader.api.DownloadResponse;
import svolkov.downloader.api.FetchCallable;

/**
 * Stores runtime info about download.
 * @author vsa
 *
 */
public class FetchInfo {

	private FetchCallable task;

	private Future<DownloadResponse> future;

	private volatile boolean inProgress;

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
