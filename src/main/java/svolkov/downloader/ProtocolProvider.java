package svolkov.downloader;

public interface ProtocolProvider {

	boolean accepts(DownloadRequest request);

	FetchCallable createCallable(DownloadRequest request);

}
