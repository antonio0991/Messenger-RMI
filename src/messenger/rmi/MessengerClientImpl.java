package messenger.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.StringTokenizer;

public class MessengerClientImpl implements MessengerClient {

	private String userName;

	public MessengerClientImpl(String userName) {
		this.userName = userName;
	}

	@Override
	public void receiveMsg(String from, String msg) throws RemoteException {
		System.out.println();
		System.out.println("<" + from + "> " + msg);
	}

	private static void handleMsg(String userName, MessengerServer server,
			StringTokenizer lineTokenizer) throws RemoteException {
		String to = lineTokenizer.nextToken();
		StringBuilder msg = new StringBuilder();
		while (lineTokenizer.hasMoreTokens()) {
			msg.append(lineTokenizer.nextToken() + " ");
		}
		if (!server.sendMsg(userName, to, msg.toString())) {
			System.err.println("Message not sent!");
		}
	}

	public static void main(String[] args) {
		try {
			String userName = args[0];
			MessengerClient client = new MessengerClientImpl(userName);
			MessengerClient stub = (MessengerClient) UnicastRemoteObject
					.exportObject(client, 0);
			Registry registry = LocateRegistry.getRegistry("localhost");
			MessengerServer server = (MessengerServer) registry
					.lookup("MessengerServer");
			Scanner input = new Scanner(System.in);
			String line = "";
			System.out.print("> ");
			while (!(line = input.nextLine().trim().toLowerCase())
					.equals("exit")) {
				
				StringTokenizer lineTokenizer = new StringTokenizer(line, " ");
				if (lineTokenizer.hasMoreTokens()) {
					String command = lineTokenizer.nextToken();
					if (command.equals("login")) {
						server.login(client, userName);
					} else if (command.equals("msg")) {
						handleMsg(userName, server, lineTokenizer);
					} else if (command.equals("users")) {
						System.out.println(server.listUsers());
					} else if (command.equals("logout")) {
						server.logout(userName);
					} else if (command.equals("timer")) {
						server.timer(userName, Integer.parseInt(lineTokenizer.nextToken()));
					} else {
						System.err.println("Unknown command: " + command);
					}
				}
				System.out.print("> ");
			}
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
	}

}
