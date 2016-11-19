import java.io.*;
import java.net.*;
import static java.lang.System.out;

public class URFTServer {
	private UDPPacket packet = null;
	
	public void createSocketAndListen(int port, String path) {
		try {
			DatagramSocket socket = new DatagramSocket(port);
			out.println("Server listening on port " + port);
			byte[] incomingData = new byte[512];
			while (true) {
				DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
				socket.receive(incomingPacket);
				byte[] data = incomingPacket.getData();
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				ObjectInputStream is = new ObjectInputStream(in);
				packet = (UDPPacket) is.readObject();
				createAndWriteFile(path); // write the file to server dir
				
				UDPPacket responsePacket = new UDPPacket();
				responsePacket.setStatus("Done");
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				ObjectOutputStream objectOutput = new ObjectOutputStream(output);
				objectOutput.writeObject(responsePacket);
				byte[] packetBytes = output.toByteArray();
				
				DatagramPacket response = new DatagramPacket(packetBytes, packetBytes.length, incomingPacket.getAddress(), incomingPacket.getPort());
				socket.send(response);
				System.out.println("Response has been sent successfully!");
				
			}
		} catch(SocketException se) {
			se.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	} // createSocketAndListen
	
	public void createAndWriteFile(String path) {
		String outputFile = path + "/" + packet.getFilename();
		File dstFile = new File(outputFile);
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(dstFile);
			fileOutputStream.write(packet.getFileData());
			fileOutputStream.flush();
			fileOutputStream.close();
			out.println("Output file : " + outputFile + " was successfully saved ");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // createAndWriteFile
	
	public static void main(String[] args) {
		if (args.length != 4) {
			out.println("Usage: java URFTServer -p <port_number> -o <server_dir_path>");
			System.exit(0);
		}
		URFTServer server = new URFTServer();
		server.createSocketAndListen(Integer.parseInt(args[1]), args[3]);
	} // main
} // URFTServer