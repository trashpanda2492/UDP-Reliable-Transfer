import java.net.*;
import java.nio.file.Files;
import java.io.*;

public class Client {

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
				
				DatagramPacket upload = new DatagramPacket(packetBytes, packetBytes.length, IPAddress, port);
				dataSocket.send(upload);
				System.out.println("File has been uploaded from client");
				
				DatagramPacket inputPacket = new DatagramPacket(input, input.length);
				dataSocket.receive(inputPacket);
				String response = new String(inputPacket.getData());
			
				System.out.println("Response from server: " + response);
				Thread.sleep(1000);
				System.exit(0);
			
			}
			else{
				System.out.println("File not found!");
				System.exit(0);
			}
		} catch (SocketException e) {
			System.out.println("Failed to create soket! ");
			e.printStackTrace();
		} catch (UnknownHostException e) {
			System.out.println("Failed to parse the IP address from input argument! ");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Failed to create output stream!");
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
