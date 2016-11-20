import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.System.out;

public class URFTServer {
	private UDPPacket packet = null;
	private int SEQ = 0;
	private String outputFile;
	private File dstFile;
	private FileOutputStream fileOutputStream;
	List<UDPPacket> list = new ArrayList<UDPPacket>();
	Set<UDPPacket> hs = new HashSet<>();
	List<UDPPacket> sortedList = new ArrayList<UDPPacket>();

	public void createSocketAndListen(int port, String path) {
		int counter = 0;
		try {
			DatagramSocket socket = new DatagramSocket(port);
			out.println("Server listening on port " + port);
			byte[] incomingData = new byte[1000];
			while (true) {
				DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
				socket.receive(incomingPacket);
				byte[] data = incomingPacket.getData();
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				ObjectInputStream is = new ObjectInputStream(in);
				packet = (UDPPacket) is.readObject();
				out.println("Packet " + packet.getSeq() + " has been received!");
				while(list.size() < packet.getSegments()) list.add(null);
				SEQ = packet.getSeq();
				if (list.get(SEQ) == null) {
					list.add(SEQ, packet);
					counter++;
					out.println("Counter: " + counter);
				}
				
				//sort the packets with correct seq number
				if(counter == packet.getSegments()){
					for(int i = 0; i < list.size(); i++) {
						System.out.println(list.get(i));
					}
					//delete duplicate packets
					hs.addAll(list);
					System.out.println(hs.size());
					for(int it = 0; it < hs.size();it++){
						for(UDPPacket p : hs){
							if(p.getSeq() == it ){
								sortedList.add(p);
							}

						}
					}

					for(int i = 0; i <sortedList.size();i++){
						if (i == 0) {
							outputFile = path + "/" + sortedList.get(i).getFilename();
							dstFile = new File(outputFile);
							fileOutputStream = new FileOutputStream(dstFile);
						}

						fileOutputStream.write(sortedList.get(i).getFileData());
						if(i == list.size()-1){
							fileOutputStream.flush();
						}
					}
				}
				
				// send back ACK
				String reply = null;
				try{InetAddress IPAddress = incomingPacket.getAddress();
				int incomingPort = incomingPacket.getPort();
				reply = "" + SEQ;
				byte[] replyBytes = reply.getBytes();
				DatagramPacket replyPacket = new DatagramPacket(replyBytes, replyBytes.length, IPAddress, incomingPort);
				socket.send(replyPacket);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
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