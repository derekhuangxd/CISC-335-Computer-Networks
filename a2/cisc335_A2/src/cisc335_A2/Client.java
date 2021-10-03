package cisc335_A2;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.concurrent.Executors;

public class Client {
	
	// Here, we create a datagram socket here
	DatagramSocket udpDatagramSocket;
	
	// Here, we create an Internet address
	InetAddress InternetAddress;
	
	// Here, we create a datagram Packet
	DatagramPacket datagramPacket;
	
	// Here, we use a boolean to keep track of whether or not the connection has been attempted
	public boolean connectionAttempted;
	
	// Here, we use an integer to keep track of port number
	int port;
	
	// Here, we use a boolean to keep track of whether or not the answer has been received
	public boolean receivedAnswer;

	// Here, we create an executor Service
	ExecutorService executorService;
	
	// Here, we use an integer to keep track of the client number
	int client;
	
	// Here, we create a buffer reader
	BufferedReader bufferReader;

	// Here, we use a flag for the below while loop
	private boolean flag = true;
	
	// Here, we use a map to keep track of the clients' sequence number
	public Map<Integer, Integer> sequenceNoClient = new HashMap<>();
	
	// Here, we use a map to keep track of the clients that are currently connected
	public Map<Integer, Integer> clientsThatAreConnected = new HashMap<>();

	//This is the Client initializer
	public Client() throws SocketException, UnknownHostException {
		// we set receivedAnswer to false 
		receivedAnswer= false;
		
		// we set connectionAttempted to false 
		connectionAttempted = false;
		
		// we initialize the udpDatagramSocket
		udpDatagramSocket = new DatagramSocket();
		
		// we initialize the InternetAddress
		InternetAddress = InetAddress.getByName("localhost");
		
		// we initialize the executorService 
		executorService = Executors.newCachedThreadPool();
		
		// we initialize the port number
		port = udpDatagramSocket.getLocalPort();
	}
	
	//This is the main running method 
	public void run() throws IOException, InterruptedException {
		System.out.println("The Client is starting now...");
		System.out.println("Client started.");
		String theCommandEntered = "Port " + port;
		System.out.println(theCommandEntered);
		System.out.println("----------------------------------------------------------------------");
		System.out.println("Commands:\n\"reach z\" -\tCreate a connection with client z, e.g. reach 3" +
				"\n\"chat a b\" -\tChat with client a with theChatMessage b"+ "\n\"exit\" -\tExit and terminate client session");
		System.out.println("----------------------------------------------------------------------");
		
		// Here, we will send the packet, with the port 7070
		sendPacket(theCommandEntered, 7070);
		
		// Here, we use a string to keep track of the string entered 
		String stringEntered = receivePacket();
		
		client = Integer.parseInt(stringEntered);
		
		// Here, we instantiate the bufferReader
		bufferReader = new BufferedReader(new InputStreamReader(System.in));
		
		// Here, we instantiate the executorService submission's runnable tasks
		executorService.submit(new ClientHandler(udpDatagramSocket, this, bufferReader));
		
		while (flag) {
			
			//Here, we try to ask the user to enter the command as input
			System.out.print("Please enter command: ");
			
			//Here, we ask the user to enter the command in the console
			theCommandEntered = bufferReader.readLine();
			
			// At this point, if the user enters reach,  this is what we're gonna do now
			if (theCommandEntered.contains("reach")) {
				
				// Here, we will send the packet, with the port 7070
				sendPacket(theCommandEntered, 7070);
				
				// while no answers are being received, this is what we're gonna do now
				while (!receivedAnswer) {
					// we wait for about a second
					Thread.sleep(1333);
					
					// we print out waiting for response
					System.out.println("Waiting for response...");
				}
				setBoolForReceivedAnswer(false);
				
				// At this point, if the user enters chat,  this is what we're gonna do now	
			} else if (theCommandEntered.contains("chat")) {
				
				// we split the string so that we can get who the user is wishing to chat with
				String[] commandArr = theCommandEntered.split(" ");
				int clientTarget = Integer.parseInt(commandArr[1]);
				
				// If the target client is within the connected clients keyset,  this is what we're gonna do now	
				if (clientsThatAreConnected.keySet().contains(clientTarget)) {
					
					// Here, we make the next sequence number incremented by 1
					int sequenceNumber = 1 + sequenceNoClient.get(clientTarget); 
					String theChatMessage = "Seq " + sequenceNumber + ", From Client " + client;
					
					// Here, we put together the chat message
					for (int i=2; i<commandArr.length; i++) {
						theChatMessage=theChatMessage+" "+commandArr[i];
					}
					
					// here, we send the chat message towards the target port number 
					int targetPortNum = clientsThatAreConnected.get(clientTarget);
					sendPacket(theChatMessage, targetPortNum);
					
				} else {
					
					// Otherwise, we say that we couldn't chat with another client
					System.out.println("Couldn't chat with " + clientTarget);
				}
				
				// At this point, if the user enters exit,  this is what we're gonna do now	
			}  else if (theCommandEntered.equals("exit")) {
				sendPacket(theCommandEntered, 7070);
				
				// Here, we terminate our while loop
				flag = false;
				
				// Here, we disconnect the udp socket connection 
				udpDatagramSocket.disconnect();
				
				// Here, we close the udp socket  
				udpDatagramSocket.close();
			}
		}
	}

	// This is the sendPacket method responsible for sending a chat message
	public void sendPacket(String theChatMessage, int destPort) throws IOException {
		datagramPacket = new DatagramPacket(theChatMessage.getBytes(), theChatMessage.getBytes().length, InternetAddress, destPort);
		
		// We will send the message here  
		udpDatagramSocket.send(datagramPacket);
	}

	// This is the receivePacket method responsible for receiving a packet
	public String receivePacket() throws IOException {
		datagramPacket = new DatagramPacket(new byte[256], new byte[256].length);
		
		// Here, we receive the datagram just initialized
		udpDatagramSocket.receive(datagramPacket);
		return new String(datagramPacket.getData(), 0, datagramPacket.getLength());
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		new Client().run();
		System.out.println("Session Ended");
	}

	// This is to set the Received Answer to another boolean value
	public synchronized void setBoolForReceivedAnswer(boolean bool) {
		receivedAnswer = bool;
	}

	// This is to set the connectionAttempted to another boolean value
	public synchronized void setBoolForconnectionAttempted(boolean bool) {
		connectionAttempted = bool;
	}
}