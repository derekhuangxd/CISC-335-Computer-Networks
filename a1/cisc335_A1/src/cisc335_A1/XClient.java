package cisc335_A1;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.net.InetAddress;
import java.net.Socket;

public class XClient {
	
	public static void main(String[] args) throws IOException{
		
		// We retrieve our localhost's IP address
		InetAddress ipAddr = InetAddress.getByName("localhost");
		
		// Initiate a new socket, with the above IP address, on port 8563
		System.out.println("Connecting to server using current IP, on port 8563");
		Socket socket = new Socket(ipAddr, 8563);
		
		// Initiate the input with the server
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		// Initiate the output with the server
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		
		// we read in what the response from the server is, and print it out accordingly
		String serverResponse = null;
		serverResponse = in.readLine();
		System.out.println(serverResponse);
		
		// if thread capacity is already reached, disconnect the connection and close the socket
		if (serverResponse.equals("The server is full.")){
			System.out.println("The server is full. Disonnecting.");
			
			// We close the connections then
			
			// turn off the output connection
			out.close();
			// turn off the input connection
			in.close();
			// turn off our socket
			socket.close();
		} 
		
		// if it doesn't reach the capacity limit, then:
		else { 
			System.out.println("Please enter \"list\" to list all the files in the folder, or \"exit\" to exit the connection with server");
			
			// This is a forever loop
			for(;;){
				// We read in what the user's input is
				BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
				
				// We store this in a string
				String inputData = userInput.readLine();
				
				// We output whatever was read in towards the server
				out.println(inputData);
				out.flush();
				System.out.println("Server: " + in.readLine());
				
				// If user type exit, then we break the loop and exit accordingly
				if (inputData.equalsIgnoreCase("exit")) {
					break;

				//  If user type list, then we list the files that are stored
				} else if (inputData.equalsIgnoreCase("list")) {
					for(;;){
						String file = in.readLine();
						System.out.println(file);
						// If we reach the end of the file list, we stop and break our loop
						if (file.equalsIgnoreCase("Now there's no more file. We hit the end of the folder.")) {
							break;
						}
					}
				}
			}
			// Finally, we turn off all of our connections
			
			// turn off the output connection
			out.close();
			// turn off the input connection
			in.close();  
			// turn off our socket
			socket.close(); 
		}
	}

}
