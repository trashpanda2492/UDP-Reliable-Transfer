import java.io.*;
import java.net.*;
import static java.lang.System.out;

public class URFTServer {
	class FileEvent implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private String destinationDirectory;
		private String sourceDirectory;
		private String filename;
		private long fileSize;
		private byte[] fileData;
		private String status;
		
		public String getDestinationDirectory() {
			return destinationDirectory;
		}

		public void setDestinationDirectory(String destinationDirectory) {
			this.destinationDirectory = destinationDirectory;
		}

		public String getSourceDirectory() {
			return sourceDirectory;
		}

		public void setSourceDirectory(String sourceDirectory) {
			this.sourceDirectory = sourceDirectory;
		}

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

		public String getStatus() {
		return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public byte[] getFileData() {
			return fileData;
		}

		public void setFileData(byte[] fileData) {
			this.fileData = fileData;
		}
	} // FileEvent class
	
	public void createSocketAndListen(int port) {
		try {
			DatagramSocket socket = new DatagramSocket(port);
			out.println("Server listening on port " + port);
			byte[] incomingData = new byte[512];
			while (true) {
				DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
				try {
					socket.receive(incomingPacket);
					byte[] data = incomingPacket.getData();
					ByteArrayInputStream in = new ByteArrayInputStream(data);
					ObjectInputStream is = new ObjectInputStream(in);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch(SocketException se) {
			se.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		if (args.length != 4) {
			out.println("Usage: java URFTServer -p <port_number> -o <server_dir_path>");
		}
		URFTServer server = new URFTServer();
		server.createSocketAndListen(Integer.parseInt(args[1]));
	} // main
} // URFTServer