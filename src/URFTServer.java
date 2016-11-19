import java.io.*;
import java.net.*;
import static java.lang.System.out;

public class URFTServer {
	private UDPPacket packet = null;
	private int SEQ = 0;
	private String outputFile;
	private File dstFile;
	private FileOutputStream fileOutputStream;
	
	public void createSocketAndListen(int port, String path) {
		int count;
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
				count = is.readInt();
				out.println("Number of segments: " + count);
				for (int i = 0; i < count; i++) {
					out.println("Reading one object");
					packet = (UDPPacket) is.readObject();
					SEQ = packet.getSeq();
					if (SEQ == 0) {
						outputFile = path + "/" + packet.getFilename();
						dstFile = new File(outputFile);
						fileOutputStream = new FileOutputStream(dstFile);
					}
					try {
						fileOutputStream.write(packet.getFileData());
						fileOutputStream.flush();
						// send back ACK
						InetAddress IPAddress = incomingPacket.getAddress();
						int incomingPort = incomingPacket.getPort();
						String reply = "" + SEQ;
						byte[] replyBytes = reply.getBytes();
						DatagramPacket replyPacket = new DatagramPacket(replyBytes, replyBytes.length, IPAddress, incomingPort);
						socket.send(replyPacket);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				fileOutputStream.close();
				out.println("Output file : " + outputFile + " was successfully saved ");
			}
		} catch(SocketException se) {
			se.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	} // createSocketAndListen
	
	public static void main(String[] args) {
		if (args.length != 4) {
			out.println("Usage: java URFTServer -p <port_number> -o <server_dir_path>");
			System.exit(0);
		}
		URFTServer server = new URFTServer();
		server.createSocketAndListen(Integer.parseInt(args[1]), args[3]);
	} // main
} // URFTServer