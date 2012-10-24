package svolkov.downloader.api;

/**
 * Stores result of downloading {@link DownloadManager}.
 * @author vsa
 *
 */
public class DownloadResponse {

	private boolean success;

	private Integer protocolResponse;

	private String message;

	private byte[] content;

	/**
	 * Returns downloaded content.
	 * @return content
	 */
	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	/**
	 * Returns <tt>true</tt> if download successfully completes.
	 * @return
	 */
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * Returns protocol response code if any. 
	 * @return response code.
	 */
	public Integer getProtocolResponse() {
		return protocolResponse;
	}

	public void setProtocolResponse(Integer protocolResponce) {
		this.protocolResponse = protocolResponce;
	}

	/**
	 * Returns protocol message if any.
	 * @return message.
	 */
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return String.format("[success=%s,code=%s,message=%s]", success, protocolResponse, message);
	}

}
