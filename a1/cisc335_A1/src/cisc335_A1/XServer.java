package cisc335_A1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class XServer {

	public static int numberOfClients = 0; // the number of Clients
	public static int clientNumber = 1; // the client number 
	static File file = new File("");
	static String path = file.getAbsolutePath();
	public static String filePath = path+"/files"; // the path where files are stored
	
	public static void main(String[] args) throws IOException {
		System.out.println(filePath);
		// Creating a thread pool, with a size of 3
		System.out.println("Creating a thread pool, with a size of 3");
		ExecutorService threadPool = Executors.newFixedThreadPool(3);

		// Initiating a server, on port 1563
		System.out.println("Initiating a server, on port 8563");
		ServerSocket serverSocket = new ServerSocket(8563);

		// This is a forever loop
		for(;;) {
			// As long as the loop holds, we accept client's connection 
			Socket clientSocket = serverSocket.accept();
			
			PrintWriter out = null;
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = null;
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			// If the number of Clients reaches more than 3, we say that the capacity limit has been reached, and we off and close.
			if (XServer.numberOfClients >= 3) {
				System.out.println("The server is full.");
				System.out.println("Rejecting connection on " + "Client#0" + clientNumber );
				out.println("The server is full.");
				out.close();
				in.close();
				clientSocket.close();
			}
			// If it does't reaches our capacity limit of 3, then:
			else {
				//This is for handling each of the single client within our thread pool.
				threadPool.execute(new singleConnectionHandler(in, out, "Client#0" + clientNumber, clientSocket));
			}
			clientNumber++; //increment client number
		}
		
	}

}
