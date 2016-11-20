import java.io.Serializable;

public class UDPPacket implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String filename;
	private long fileSize;
	private byte[] fileData;
	private int seq;
	private int payloadSize;
	private int segments;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public byte[] getFileData() {
		return fileData;
	}

	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}
	
	public int getSeq() {
		return this.seq;
	}
	
	public void setSeq(int seq) {
		this.seq = seq;
	}
	
	public int getPayloadSize() {
		return this.payloadSize;
	}
	
	public void setPayloadSize(int payloadSize) {
		this.payloadSize = payloadSize;
	}
 
 public int getSegments() {
		return this.segments;
	}
	
	public void setSegments(int segments) {
		this.segments = segments;
	}
} // UDPPacket class
