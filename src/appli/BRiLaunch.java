package appli;

import bri.*;
import bri.commandes.AjouterService;
import bri.commandes.MettreAJourService;
import bri.commandes.ModifierAddresseFTP;

public class BRiLaunch {
	private final static int PORT_PROG = 3000;
	private final static int PORT_AMA = 4000;
	
	public static void main(String[] args) {
		/*
		@SuppressWarnings("resource")
		Scanner clavier = new Scanner(System.in);
		
		// URLClassLoader sur ftp
		String fileNameURL = "ftp://localhost:2121/";
		URLClassLoader urlcl = null;
		try {
			urlcl = URLClassLoader.newInstance(new URL[] {new URL(fileNameURL)});
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		System.out.println("Bienvenue dans votre gestionnaire dynamique d'activit� BRi");
		System.out.println("Pour ajouter une activit�, celle-ci doit �tre pr�sente sur votre serveur ftp");
		System.out.println("A tout instant, en tapant le nom de la classe, vous pouvez l'int�grer");
		System.out.println("Les clients se connectent au serveur 3000 pour lancer une activit�");

		CommandeRegistry commandes = initCommandes();

		ServiceProg.setLesCommandes(commandes);
		
		new Thread(new ServeurBRi(PORT_PROG, ServiceProg.class)).start() ;
		new Thread(new ServeurBRi(PORT_AMA, ServiceBRi.class)).start() ;
		/*
		while (true){
				try {
					String classeName = clavier.next();
					ServiceRegistry.addService(urlcl.loadClass(classeName).asSubclass(Service.class));
				} catch (Exception e) {
					System.out.println(e);
				}
			}		*/
	}

	public static CommandeRegistry initCommandes() {
		CommandeRegistry commandeRegistry = new CommandeRegistry();

		commandeRegistry.addCommande(new AjouterService());
		commandeRegistry.addCommande(new MettreAJourService());
		commandeRegistry.addCommande(new ModifierAddresseFTP());

		return commandeRegistry;
	}
}
