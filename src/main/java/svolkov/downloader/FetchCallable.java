package svolkov.downloader;

import java.util.concurrent.Callable;

/**
 * Callable with cancellation support.
 * @author vsa
 *
 */
public interface FetchCallable extends Callable<DownloadResponse> {

	/**
	 * Cancels request if {@link Thread#interrupt()} is not supported.
	 */
	void cancel();

}
