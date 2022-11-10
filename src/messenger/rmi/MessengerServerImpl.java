package messenger.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class MessengerServerImpl implements MessengerServer {

	private HashMap<String, MessengerClient> users;

	public MessengerServerImpl() {
		this.users = new HashMap<String, MessengerClient>();
	}

	@Override
	public void login(MessengerClient client, String userName)
			throws RemoteException {
		System.out.println("Login: " + userName);
		users.put(userName, client);
	}

	@Override
	public boolean sendMsg(String from, String to, String msg)
			throws RemoteException {
		System.out.println("Sending msg from " + from + " to " + to);
		MessengerClient toClient = users.get(to);
		if (toClient == null) {
			return false;
		}
		toClient.receiveMsg(from, msg);
		return true;
	}

	@Override
	public String listUsers() throws RemoteException {
		System.out.println("Listing logged users.");
		return users.keySet().toString();
	}

	@Override
	public void logout(String userName) throws RemoteException {
		System.out.println("Logout: " + userName);
		users.remove(userName);
	}

	public static void main(String[] args) {
		try {
			MessengerServerImpl obj = new MessengerServerImpl();
			MessengerServer stub = (MessengerServer) UnicastRemoteObject
					.exportObject(obj, 0);
			// Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.getRegistry("localhost");
			registry.bind("MessengerServer", stub);

			System.err.println("Server is running...");
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public String timer(String userName, int timeInSeconds) throws RemoteException {
		try {
			Thread.sleep(timeInSeconds*1000);
		} catch (InterruptedException e) { }
		return "Time out!";
	}
}