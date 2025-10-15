package bri;

import programmeurs.Programmeur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class ServiceProg implements Service {
    private static List<Programmeur> programmeurs = new ArrayList<Programmeur>();
    //private static List<Method> fonctionnalites = new ArrayList<>(){};
    private Socket client;

    ServiceProg(Socket socket) {
        client = socket;
    }
    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            out.println("Entrez votre identifiant");
            String identifiant = in.readLine();

            out.println("Entrez votre mot de passe");
            String mdp = in.readLine();

            out.println("Entrez l'adresse de votre serveur ftp");
            String adresseServeur = in.readLine();

            Programmeur programmeur = addProgrammeur(identifiant, mdp, adresseServeur);

            out.println("Tapez le numéro de la fonctionnalité désirée\n" +
                    "1 - Fournir un nouveau service\n" +
                    "2 - Mettre à jour un service\n" +
                    "3 - Déclarer un changement de l'adresse de votre serveur ftp\n");
            int choix = Integer.parseInt(in.readLine());
            if(choix == 1) {
                addNewService(in, out, programmeur);
            }
            else if(choix == 2) {
                updateService(in, out, programmeur);
            }
            else if(choix == 3) {
                updateFtpAdress(in, out, programmeur);
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }



    private void addNewService(BufferedReader in , PrintWriter out, Programmeur programmeur ) throws IOException, ClassNotFoundException {
        out.println("Entrez le nom du service à ajouter");
        String nomClasseService = in.readLine();
        URLClassLoader urlcl = null;
        try {
            urlcl = URLClassLoader.newInstance(new URL[] {new URL(programmeur.getAdresseFtp())});
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ServiceRegistry.addService(urlcl.loadClass(nomClasseService).asSubclass(Service.class));
        out.println("Service ajouté " + nomClasseService);
    }

    private void updateService(BufferedReader in , PrintWriter out, Programmeur programmeur ) throws IOException, ClassNotFoundException {
        out.println("Entrez le nom du service à modifier");
        String nomClasseService = in.readLine();
        URLClassLoader urlcl = null;
        try {
            urlcl = URLClassLoader.newInstance(new URL[] {new URL(programmeur.getAdresseFtp())});
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ServiceRegistry.updateService(urlcl.loadClass(nomClasseService).asSubclass(Service.class));
        out.println("Service modifié " + nomClasseService);
    }

    private void updateFtpAdress(BufferedReader in , PrintWriter out, Programmeur programmeur) throws IOException {
        out.println("Entrez la nouvelle adresse de votre serveur ftp");
        String newAdresseFtp = in.readLine();
        programmeur.setAdresseFtp(newAdresseFtp);
        out.println("Adresse modifiée, nouvelle adresse : " + newAdresseFtp);
    }

    public static Programmeur addProgrammeur(String identifiant, String mdp, String adresseServeur) {
        for (Programmeur p : programmeurs) {
            if(p.verifier(identifiant, mdp))
                return p;
        }
        Programmeur p = new Programmeur(identifiant, mdp, adresseServeur);
        programmeurs.add(p);
        return p;
    }
}
