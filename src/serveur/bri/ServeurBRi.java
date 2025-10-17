package serveur.bri;


import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;


public class ServeurBRi implements Runnable {
	private ServerSocket listen_socket;
	private Class<? extends Service> service_class;
	
	// Cree un serveur TCP - objet de la classe ServerSocket
	public ServeurBRi(int port, Class<? extends Service> classeService) {
		try {
			listen_socket = new ServerSocket(port);
			service_class = classeService;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
}

	// Le serveur ecoute et accepte les connections.
	// pour chaque connection, il cree un ServiceInversion, 
	// qui va la traiter.
	public void run() {
		try {
			while(true)
				new Thread(service_class.getConstructor(Socket.class).newInstance(listen_socket.accept())).start();
		}
		catch (IOException e) { 
			try {this.listen_socket.close();} catch (IOException e1) {}
			System.err.println("Pb sur le port d'écoute :"+e);
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e){
			System.err.println("Erreur lors de l'instanciation du service :"+e);
		}
	}

	 // restituer les ressources --> finalize
	protected void finalize() throws Throwable {
		try {this.listen_socket.close();} catch (IOException e1) {}
	}

	// lancement du serveur
	public void lancer() {
		(new Thread(this)).start();		
	}
}
