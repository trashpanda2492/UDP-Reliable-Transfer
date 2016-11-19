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
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}