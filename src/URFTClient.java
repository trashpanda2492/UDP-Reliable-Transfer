import java.net.*;
import java.util.Arrays;
import java.io.*;

public class URFTClient {

	public static void main(String[] args) {
		String ip = args[1];
		int port = Integer.parseInt(args[3]);
		String filePath = args[5];

		try {
			InetAddress IPAddress = InetAddress.getByName(ip);

			File file = new File(filePath);
			if(file.exists()){
				String fileName = filePath.substring(filePath.lastIndexOf('/')+1, filePath.length());
				UDPPacket packet = new UDPPacket();
				packet.setFilename(fileName);
				DataInputStream di = new DataInputStream(new FileInputStream(file));
				DatagramSocket dataSocket = new DatagramSocket();

				int bytes = 0, offset = 0;
				byte[] fileBuffer = new byte[(int)file.length()];
				while(bytes < fileBuffer.length && (offset = di.read(fileBuffer, bytes, fileBuffer.length - bytes)) >= 0){
					bytes += offset;
				}

				packet.setFileSize(file.length());
				//packet.setFileData(fileBuffer);
				int segments = (int)(Math.ceil((double)file.length()/512));
				System.out.println("Number of segments: " + segments);
				int seq = 0;
				int bytesRead = 0;
				int bytesLeft =  fileBuffer.length;
				while(bytesRead < fileBuffer.length){
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					ObjectOutputStream objectOutput = new ObjectOutputStream(output);
					byte[] payload = new byte[512];
					if(bytesLeft <= 512){
						payload = Arrays.copyOfRange(fileBuffer, seq * 512, fileBuffer.length);
					}else{
						payload = Arrays.copyOfRange(fileBuffer, seq *512, seq*512 + 512);
					}
					packet.setFileData(payload);
					packet.setSeq(seq);
					packet.setPayloadSize(payload.length);
					packet.setSegments(segments);
					objectOutput.writeObject(packet);
          
					byte[] packetBytes = output.toByteArray();
          System.out.println(packetBytes.length);
					DatagramPacket upload = new DatagramPacket(packetBytes, packetBytes.length, IPAddress, port);
					dataSocket.send(upload);
                                  
          objectOutput.flush();
          objectOutput.close();
          output.flush();
          output.close();
          
					System.out.println("File packet (Sequence number " + seq + " has been uploaded from client");
					bytesRead += payload.length;
					seq ++;
					bytesLeft -= payload.length;		
				}
				
        while(true){
  				byte[] response = new byte[512];
  				DatagramPacket inputPacket = new DatagramPacket(response, response.length);
  				dataSocket.receive(inputPacket);
  				String ACK = new String(inputPacket.getData());
  				if(ACK.equalsIgnoreCase("done")){
            dataSocket.close();
  					di.close();
  					System.exit(0);

  				}
  				else{
  					System.out.println("Acknowledge from server: " + ACK);
  				}
        }


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
		}
	}

}
