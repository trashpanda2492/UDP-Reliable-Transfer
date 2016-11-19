import java.net.*;
import java.util.Arrays;
import java.io.*;
import static java.lang.System.out;

public class URFTClient {

	public static void main(String[] args) {
		String ip = args[1];
		int port = Integer.parseInt(args[3]);
		String filePath = args[5];

		try {
			DatagramSocket dataSocket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName(ip);
			byte[] input = new byte[512];
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			ObjectOutputStream objectOutput = new ObjectOutputStream(output);

			File file = new File(filePath);
			if(file.isFile()){
				UDPPacket packet = new UDPPacket();
				String fileName = filePath.substring(filePath.lastIndexOf('/')+1, filePath.length());
				packet.setFilename(fileName);
				DataInputStream di = new DataInputStream(new FileInputStream(file));
				
				int bytes = 0, offset = 0;
				byte[] fileBuffer = new byte[(int)file.length()];
				// read file data into fileBuffer
				while(bytes < fileBuffer.length && (offset = di.read(fileBuffer, bytes, fileBuffer.length - bytes)) >= 0){
					bytes += offset;
				}
				int segments = (int)Math.ceil(fileBuffer.length/512);
				int start = 0, end = 511;
				byte[] payload;
				for (int i = 0; i < segments; i++) {
					if (i == segments - 1) {
						end += fileBuffer.length - start;
						packet.setFileSize(end - start + 1);
						payload = Arrays.copyOfRange(fileBuffer, start, end);
					}
					else {
						end += 512;
						packet.setFileSize(512);
						payload = Arrays.copyOfRange(fileBuffer, start, end);
					}
					packet.setFileData(payload);
					packet.setSeq(start);
					start += 512;
					objectOutput.writeInt(segments);
					objectOutput.writeObject(packet);
					byte[] packetBytes = output.toByteArray();
					
					DatagramPacket upload = new DatagramPacket(packetBytes, packetBytes.length, IPAddress, port);
					dataSocket.send(upload);
					out.println("Packet " + start + " sent from client.");
				}
				
				// receive ACKs from server
				dataSocket.setSoTimeout(1000);
				int lastACK = 0;
				while (lastACK != fileBuffer.length) {
					DatagramPacket inputPacket = new DatagramPacket(input, input.length);
					try {
						dataSocket.receive(inputPacket);
					} catch (SocketTimeoutException e) {
						continue;
					}
					String response = new String(inputPacket.getData());
					lastACK = Integer.parseInt(response);
					out.println("Received ACK: " + lastACK);
				}
				System.exit(0);
			}
			else{
				out.println("File not found!");
				System.exit(0);
			}
		} catch (SocketException e) {
			out.println("Failed to create socket! ");
			e.printStackTrace();
		} catch (UnknownHostException e) {
			out.println("Failed to parse the IP address from input argument! ");
			e.printStackTrace();
		} catch (IOException e) {
			out.println("Failed to create output stream!");
			e.printStackTrace();
		}
	}
}