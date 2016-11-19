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
			InetAddress IPAddress = InetAddress.getByName(ip);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			ObjectOutputStream objectOutput = new ObjectOutputStream(output);

			File file = new File(filePath);
			if(file.exists()){
				UDPPacket packet = new UDPPacket();
				String fileName = filePath.substring(filePath.lastIndexOf('/')+1, filePath.length());
				packet.setFilename(fileName);
				DataInputStream di = new DataInputStream(new FileInputStream(file));
				
				int bytes = 0, offset = 0;
				byte[] fileBuffer = new byte[(int)file.length()];
<<<<<<< HEAD
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
=======
				while(bytes < fileBuffer .length && (offset = di.read(fileBuffer, bytes, fileBuffer.length - bytes)) >= 0){
					bytes += offset;
				}
				packet.setFileSize(file.length());
				packet.setFileData(fileBuffer);
				
				objectOutput.writeObject(packet);
				byte[] packetBytes = output.toByteArray();
				
				DatagramSocket dataSocket = new DatagramSocket();
				DatagramPacket upload = new DatagramPacket(packetBytes, packetBytes.length, IPAddress, port);
				dataSocket.send(upload);
				System.out.println("File packet (Sequence number " + packet.getSeq() + " has been uploaded from client");
				
				byte[] response = new byte[512];
				DatagramPacket inputPacket = new DatagramPacket(response, response.length);
				dataSocket.receive(inputPacket);
				ByteArrayInputStream in = new ByteArrayInputStream(response);
				ObjectInputStream is = new ObjectInputStream(in);
				UDPPacket responsePacket = (UDPPacket) is.readObject();
				int ACK = responsePacket.getAck();
			
				System.out.println("Acknowledge from server: " + ACK);
				Thread.sleep(100);
				
				
>>>>>>> f24fbcddec168089c57d158949eb88d1e93e1286
				System.exit(0);
			
			}
			else{
				out.println("File not found!");
				System.exit(0);
			}
		} catch (SocketException e) {
<<<<<<< HEAD
			out.println("Failed to create socket! ");
=======
			System.out.println("Failed to create soket! ");
>>>>>>> f24fbcddec168089c57d158949eb88d1e93e1286
			e.printStackTrace();
		} catch (UnknownHostException e) {
			out.println("Failed to parse the IP address from input argument! ");
			e.printStackTrace();
		} catch (IOException e) {
<<<<<<< HEAD
			out.println("Failed to create output stream!");
			e.printStackTrace();
		}
	}
}
=======
			System.out.println("Failed to create output stream!");
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
>>>>>>> f24fbcddec168089c57d158949eb88d1e93e1286
