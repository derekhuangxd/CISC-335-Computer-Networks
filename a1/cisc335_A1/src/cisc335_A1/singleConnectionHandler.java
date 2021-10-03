package cisc335_A1;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class singleConnectionHandler extends Thread {
	
	// the input
	public BufferedReader in = null;
	// the output 
	public PrintWriter out = null;
	// the Client's Name
	public String clientName;
	// the client Socket
	public Socket clientSocket;

	// Initialization method
	public singleConnectionHandler(BufferedReader in, PrintWriter out,  String clientName, Socket clientSocket) {
		// the input
		this.in = in;
		// the output 
		this.out = out;
		// the Client's Name
		this.clientName = clientName;
		// the client Socket
		this.clientSocket = clientSocket;
	}
	
	@Override
	public void run() {
		// We propose a new dateFormat
		DateFormat dateFormat = new SimpleDateFormat();
		// We increment the number of clients
		XServer.numberOfClients++;
		// We print to the command line to say that a client is now connected at a certain time
		System.out.println(clientName + " has been connected to the server (" + dateFormat.format(new Date()) + ")");
		out.println("Client has been connected to the server");
		out.flush();
		
		// This is a forever loop
		for(;;){
			
			// we create a string called userInput as to store what the user has typed in
			String userInput = "";
			try {
				userInput = in.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(clientName + " sent: " + userInput);
			
			// If the user input is list, we list all the files stored in the 'files' folder
			if (userInput.equalsIgnoreCase("LIST")) {
				out.println("The list of files stored in the folder are as below:");
				out.flush();
				File[] directory = new File(XServer.filePath).listFiles();
				try {
					for (File f:directory) {
						out.println(f.getName());
						out.flush();
				    }
				}
				catch(NullPointerException e) {
					System.out.println("Error: The folder does not exist.");
				}
				// If we hit here, there's no more file. We hit the end of the folder.
				out.println("Now there's no more file. We hit the end of the folder.");
				out.flush();
				
			// If the user input is exit, we exit at the current time.
			} else if (userInput.equalsIgnoreCase("EXIT")) {
				System.out.println("Disconnecting. (" + dateFormat.format(new Date()) + ")");
				out.println("We've disconnected.");
				break;
				
			} else {
				// Otherwise, we've indicate here that some kind of input has been acknowledged but not run
				out.println(userInput + " ACK");
				out.flush();
			}
		}
		
		// Finally, we turn off all of our connections
		out.close();
		try {
			// turn off the input connection
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			// turn off the client socket connection
			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// We decrement the number of clients at the end
		XServer.numberOfClients--;
	}
		
}
