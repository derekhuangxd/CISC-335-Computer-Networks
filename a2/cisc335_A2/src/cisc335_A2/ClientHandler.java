package cisc335_A2;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.io.BufferedReader;

public class ClientHandler implements Runnable {
	
	// Here, we use a flag for the below while loop
	private boolean flag = true;
	
	// Here, we create a Client here
	private Client client;
	
	// Here, we create a datagram socket here
	private DatagramSocket datagramSocket;
	
	// Here, we create a buffer reader
	BufferedReader bufferReader;
	
	// Here, we use an integer to keep track of the sequence number
	int sequenceNumber;

	//This is the ClientHandler's initializer
	public ClientHandler(DatagramSocket datagramSocket, Client client, BufferedReader bufferReader) {
		
		// we set this.client to client
		this.client = client;
		
		// we set this.datagramSocket to datagramSocket
		this.datagramSocket = datagramSocket;
	}

	
	//This is the main running method 
	@Override
	public void run() {
		
		// Here, we create a datagram Packet
		DatagramPacket datagramPacket;
		
		// Here, we instantiate the buffer reader
		bufferReader = new BufferedReader(new InputStreamReader(System.in));
		
		// Here, we use a string to keep track of the resulting string 
		String stringResult;
		
		while (flag) {
			try {
				
				// Here, we instantiate the datagram Packet
				datagramPacket = new DatagramPacket(new byte[256], new byte[256].length);
				datagramSocket.receive(datagramPacket);
				
				// Here, we use a string to keep track of the string entered 
				String stringEntered = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
				
				// At this point, if the user enters reach,  this is what we're gonna do now
				if (stringEntered.startsWith("reach")) {
					
					// Here, we split the stringReply so that we can get the client Number that we are Connecting To
					String[] stringReply = stringEntered.split(",");
					
					// Here, we get the client Number that we are Connecting To
					int clientNumberConnectingTo = Integer.parseInt(stringReply[1]);
					System.out.println(stringEntered);
					
					// At this point, if the client is inactive,  this is what we're gonna do now
					if (stringEntered.contains("inactive")) {
						stringEntered = stringEntered.replace("reach", "");
						System.out.println(stringEntered);
						
						// We are gonna say that the client is inactive
						System.out.println("Client Inactive");
					} else {
						int portTargetingAt = Integer.parseInt(stringReply[2]);
						client.setBoolForconnectionAttempted(true);

						if (portTargetingAt != client.port) {
							
							// Here, we say that the client is requesting to connect in here
							stringResult = "Client " + client.client + " is requesting to connect in";
							
							// Here, we instantiate the datagram packet
							datagramPacket = new DatagramPacket(stringResult.getBytes(), stringResult.getBytes().length, InetAddress.getByName("localhost"), portTargetingAt);
							
							// Here, we send the datagram packet
							datagramSocket.send(datagramPacket);
							
							// Here, we set a time-out interval to wait for 10 seconds
							datagramSocket.setSoTimeout(10000);
							stringEntered = "";
							while (stringEntered.equals("")) {
								try {
									
									// Here, we say that we are attempting to connect to Client in here
									System.out.println("We are attempting to connect to Client " + clientNumberConnectingTo);
									
									// Here, we instantiate the datagram packet
									datagramPacket = new DatagramPacket(new byte[256], new byte[256].length);
									
									// Here, we receive the datagram packet
									datagramSocket.receive(datagramPacket);
									
									// Here we get the string that was entered and store it as stringEntered
									stringEntered = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
									
									// if the connection is accepted, this is what we are doing now
									if (stringEntered.toLowerCase().equals("Connection Accepted")) {
										
										// Here, we put the related info into the map
										client.clientsThatAreConnected.put(clientNumberConnectingTo, portTargetingAt);
										
										// Here, we put the related info into the map
										client.sequenceNoClient.put(clientNumberConnectingTo, 0);
									}
								} catch (SocketTimeoutException e) {
									// TODO Auto-generated catch block
									
									// As we reach here, a timed out error has occurred and we are saying this out
									System.out.println("Unfortunately, the connection attempt has timed out.");
									
									// Then, we try to reconnect
									System.out.println("Reconnecting...");
								}
							}
							datagramSocket.setSoTimeout(0);
							System.out.println(stringEntered);
						} else {
							
							// Otherwise, we say that the client is indeed inactive
							System.out.println("Client Inactive");
						}
					}
					client.setBoolForReceivedAnswer(true);
				} else if (stringEntered.contains("connec")) {
					System.out.println(stringEntered);
					System.out.println("----------------------------------------------------------------------");
					System.out.println("A client is attemping to request to connect into here...");
					System.out.println("Do you want to reject or accept this connection [accept/reject]? (Enter on the next & next line:)");
					
					// Here, we read in whatever input the user entered
					stringResult = bufferReader.readLine();
					
					// If the user enters accept, we will set stringResult to "Connection Accepted"
					if (stringResult.toLowerCase().contains("accept")) {
						
						// If the user enters accept, we will set stringResult to "Connection Accepted"
						stringResult = "Connection Accepted";
					} else {
						
						// If the user enters reject, we will set stringResult to "Connection Rejected"
						stringResult = "Connection Rejected";
					}
					
					// Here, we get the Internet Address of the datagram packet
					InetAddress InternetAddress = datagramPacket.getAddress();
					
					// Here, we use an integer to store the port number
					int portNo = datagramPacket.getPort();
					
					// we instantiate the datagram packet
					datagramPacket = new DatagramPacket(stringResult.getBytes(), stringResult.getBytes().length, InternetAddress, portNo);
					
					// Here, we send the datagram packet towards
					datagramSocket.send(datagramPacket);
					
				// At this point, if the user enters left,  this is what we're gonna do now
				} else if (stringEntered.contains("Left")) {
					System.out.println(stringEntered);
					
					// We will terminate the while loop that was running in the next iteration
					flag = false;
				} else {
					System.out.println(stringEntered);
					
					// This is to get the sequence number from the string entered
					sequenceNumber = Integer.parseInt(stringEntered.split(" ")[0]);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
