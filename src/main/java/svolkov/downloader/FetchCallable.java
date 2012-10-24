package svolkov.downloader;

import java.util.concurrent.Callable;

/**
 * Callable with clean support.
 * @author vsa
 *
 */
public interface FetchCallable extends Callable<DownloadResponse> {

	/**
	 * Stops execution if {@link Thread#interrupt()} is not supported.
	 */
	void clean();

}
