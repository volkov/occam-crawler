package svolkov.downloader;

/**
 * Downloader for specific protocol.
 * @author vsa
 *
 */
public interface ProtocolProvider {

	/**
	 * Returns <tt>true</tt> if current request is supported.
	 * @param request
	 * @return
	 */
	boolean accepts(DownloadRequest request);

	/**
	 * Creates task for specified request.
	 * @param request request that should be processed.
	 * @return callable task.
	 */
	FetchCallable createCallable(DownloadRequest request);

}
