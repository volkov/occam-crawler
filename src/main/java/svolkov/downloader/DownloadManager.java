package svolkov.downloader;

/**
 * Download manager facade, allows to add request, inspect request execution status, cancel request execution and get result.
 * @author vsa
 *
 */
public interface DownloadManager {

	/**
	 * Adds request to download queue.
	 * @param request request to download.
	 * @return identifier of request in manager.
	 * @throws RequestNotSupported if request type is not supported.
	 */
	Long addDownloadRequest(DownloadRequest request) throws RequestNotSupported;
	
	/**
	 * Returns status of request execution.
	 * @param id identifier of request in manager.
	 * @return status
	 */
	DownloadStatus getStatus(Long id);

	/**
	 * Cancels request with specified id
	 * @param id identifier of request in manager.
	 */
	boolean cancelRequest(Long id);

	/**
	 * Returns response for request with specified id.
	 * @param id
	 * @return
	 */
	DownloadResponse getResponse(Long id);

}
