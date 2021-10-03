package cisc335_A2;

import java.util.Map;
import java.util.HashMap;
import java.io.*;
import java.net.*;

public class Server {

	// Here, we use a map to keep track of the current Clients that are Active
	public static Map<Integer, Integer> currentClientsActive = new HashMap<>();
	
	// Here, we use an integer to keep track of client number 
	private static int client = 1;
	
	// Here, we use an integer to keep track of client that's currently running
	private int clientCurrentlyRunning;

	private DatagramSocket UDPdatagramSocket;
	
	// Here, we use a flag for the below while loop
	private boolean flag = true;

	
	//This is the server initializer
	public Server(DatagramSocket UDPdatagramSocket) {
		this.UDPdatagramSocket = UDPdatagramSocket;
	}

	//This is the main running method 
	public void run() throws IOException {
		// We first generate a datagram packet here
		DatagramPacket datagramPKT;
		
		// Here, we use a string to keep track of the string entered 
		String stringEntered;
		
		while (flag) {
			// Here, we initiate the above generated datagram
			datagramPKT = new DatagramPacket(new byte[256], new byte[256].length);
			UDPdatagramSocket.receive(datagramPKT);
			
			// Here, we use a string to keep track of the string entered 
			stringEntered = new String(datagramPKT.getData(), 0, datagramPKT.getLength());
			
			// Here, we use a string to keep track of the resulting string 
			String stringResult;
			
			// Here, this is to get the IP address of the packet
			InetAddress ipAddress = datagramPKT.getAddress();
			
			// Here, we use an integer to keep track of the port number
			int portNumber = datagramPKT.getPort();
			
			// Here, we use a string to keep track of the IP address of the host retrieved
			String hostIpAddress = ipAddress.getHostAddress();
			
			System.out.println("A new client has come in.");
			System.out.println("IP addr: "+hostIpAddress + ", Port No.: " + portNumber);
			System.out.println("-------------------------------------------------");
			
			// At this point, if the user entered exit,  this is what we're gonna do now
			if (stringEntered.equals("exit")) {
				
				// We firstly, remove the currently running client
				currentClientsActive.remove(clientCurrentlyRunning);
				
				// Here, we use a string to say Client Left
				stringResult = "Client Left";
				
				// We initiate a datagram packet here
				datagramPKT = new DatagramPacket(stringResult.getBytes(), stringResult.getBytes().length, ipAddress, portNumber);
				UDPdatagramSocket.send(datagramPKT);
				
				// For each of the currently running active client, 
				// we print its info one by one, using a for-each loop
				for (int x:currentClientsActive.keySet()) {
					System.out.println("Client: " + x + "\tPort No.: " + currentClientsActive.get(x));
				}
			}
			
			else if (stringEntered.contains("Port")) {
				int portNumberClient = Integer.parseInt(stringEntered.split(" ")[1]);
				if (!currentClientsActive.values().contains(portNumberClient)) {
					currentClientsActive.put(client, portNumberClient);
					
					// Here, we use a string to keep track of the client
					stringResult = client + "";
					
					// We initiate a datagram packet here
					datagramPKT = new DatagramPacket(stringResult.getBytes(), stringResult.getBytes().length, ipAddress, portNumberClient);
					UDPdatagramSocket.send(datagramPKT);
					
					// Here, we increment the client number
					client++;
				}
				
				// For each of the currently running active client, 
				// we print its info one by one, using a for-each loop
				for (int x:currentClientsActive.keySet()) {
					System.out.println("Client: " + x + "\tPort No.: " + currentClientsActive.get(x));
				}
				System.out.println("-------------------------------------------------");
			}
			
			// At this point, if the user entered reach,  this is what we're gonna do now
			else if (stringEntered.contains("reach")) {
				
				// we first split out the string to get the client no
				int theClientNumber = Integer.parseInt(stringEntered.split(" ")[1]);
				
				// if the client is active, we say yes, we can reach there.
				if (currentClientsActive.containsKey(theClientNumber)) {
					stringResult = "reach," + theClientNumber +"," + currentClientsActive.get(theClientNumber) + "";
					
				// if the client is inactive, we say no, the client is inactive.
				} else {
					stringResult = "reach,Client " + theClientNumber + " is inactive.";
				}
				
				
				datagramPKT = new DatagramPacket(stringResult.getBytes(), stringResult.getBytes().length, ipAddress, portNumber);
				
				UDPdatagramSocket.send(datagramPKT);
			}
		}

	}

	public static void main(String[] args) throws IOException {
		System.out.println("The Server is starting now...");
		System.out.println("---------------------------------");
		System.out.println("Server started.");
		System.out.println("The server is waiting to accept connections...");
		System.out.println("-------------------------------------------------");

		DatagramSocket datagramSocketServer = new DatagramSocket(7070);
		new Server(datagramSocketServer).run();
	}
}
