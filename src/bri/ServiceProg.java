package bri;

import exceptions.BRiException;
import programmeurs.Programmeur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceProg implements Service {
    private static List<Programmeur> programmeurs = new ArrayList<Programmeur>();

    private Socket client;

    private String messageClient = "";

    private static class Commande {
        final String description;
        final Method action;

        Commande(String description, Method action) {
            this.description = description;
            this.action = action;
        }
    }

    private static final Map<Integer, Commande> commandes = new HashMap<>();

    public ServiceProg(Socket socket) {
        client = socket;
        try {
            commandes.put(1, new Commande("Fournir un nouveau service", ServiceProg.class.getDeclaredMethod("ajouterService", BufferedReader.class, PrintWriter.class, Programmeur.class)));
            commandes.put(2, new Commande("Mettre à jour un service",ServiceProg.class.getDeclaredMethod("mettreAJourService", BufferedReader.class, PrintWriter.class, Programmeur.class)));
            commandes.put(3, new Commande("Modifier l'adresse de votre serveur FTP", ServiceProg.class.getDeclaredMethod("modifierAdresseFTP", BufferedReader.class, PrintWriter.class, Programmeur.class)));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            Programmeur programmeur = null;
            while(programmeur == null)
                programmeur = authentifierProgrammeur(in, out);

            while (true){
                int choix = demanderChoix(in, out);

                if (choix > 0)
                    traiterChoix(choix, in, out, programmeur);
            }

        } catch (IOException e) {}

        System.out.println("*********Connexion au service programmeur terminée");
        try {client.close();} catch (IOException e2) {}

    }

    private String lireLigne(BufferedReader in, PrintWriter out) throws IOException {
        String input = in.readLine();
        if (input == null || input.equalsIgnoreCase("exit")) {
            throw new IOException("Client déconnecté");
        }
        return input;
    }


    private int demanderChoix(BufferedReader in, PrintWriter out) throws IOException {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(messageClient);
        sBuilder.append("Tapez le numéro de la fonctionnalité désirée :");
        for (Map.Entry<Integer, Commande> entry : commandes.entrySet()) {
            int num = entry.getKey();
            Commande c = entry.getValue();
            sBuilder.append("##").append(num).append("- ").append(c.description);
        }
        out.println(sBuilder.toString());
        try {
            return Integer.parseInt(lireLigne(in, out));
        } catch (NumberFormatException e) {
            messageClient = "Choix invalide. Opération annulée##";
            return -1;
        }
    }

    private void traiterChoix(int choix, BufferedReader in, PrintWriter out, Programmeur programmeur) {
        Method method = commandes.get(choix).action;

        if (method != null) {
            try {
                method.invoke(this, in, out, programmeur);
            } catch (InvocationTargetException | NullPointerException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                messageClient = "Le service demandée n'est pas accessible##";
            }
        } else {
            messageClient = "Option inconnue##";
        }
    }

    private void ajouterService(BufferedReader in , PrintWriter out, Programmeur programmeur) throws IOException {
        out.println("Entrez le nom du service à ajouter");
        String nomClasseService = lireLigne(in, out);
        try {
            ServiceRegistry.addService(getURLClassLoader(programmeur).loadClass(nomClasseService).asSubclass(Service.class), programmeur.getIdentifiant());
            messageClient =  "Service ajouté " + nomClasseService + "##";
        } catch (ClassNotFoundException e) {
            messageClient = "Le service demandé est introuvable sur votre serveur FTP##";
        } catch (MalformedURLException e) {
            messageClient = "L'adresse FTP fournie est invalide##";
        } catch (BRiException e){
            messageClient = e.getMessage() + "##";
        }
    }

    private void mettreAJourService(BufferedReader in , PrintWriter out, Programmeur programmeur ) throws IOException {
        out.println("Entrez le nom du service à modifier");
        String nomClasseService = lireLigne(in, out);
        try {
            ServiceRegistry.updateService(getURLClassLoader(programmeur).loadClass(nomClasseService).asSubclass(Service.class), programmeur.getIdentifiant());
            messageClient = "Service modifié " + nomClasseService + "##";
        } catch (ClassNotFoundException e) {
            messageClient = "Le service demandé est introuvable sur votre serveur FTP##";
        } catch (MalformedURLException e) {
            messageClient = "L'adresse FTP fournie est invalide##";
        } catch (BRiException e){
            messageClient = e.getMessage();
        }
    }

    private void modifierAdresseFTP(BufferedReader in , PrintWriter out, Programmeur programmeur) throws IOException {
        out.println("Entrez la nouvelle adresse de votre serveur ftp");
        String newAdresseFtp = lireLigne(in, out);
        programmeur.setAdresseFtp(newAdresseFtp);
        messageClient = "Adresse modifiée, nouvelle adresse : " + newAdresseFtp + "##";
    }

    public Programmeur authentifierProgrammeur(BufferedReader in, PrintWriter out) {
        out.println(messageClient + "Entrez 1 pour vous inscrire ou 2 pour vous connecter :##" +
                "1- S'inscrire##" +
                "2- Se connecter");

        try {
            int choix = Integer.parseInt(lireLigne(in, out));
            if (choix == 1)
                return inscrireProgrammeur(in, out);
            else if (choix == 2)
                return connecterProgrammeur(in, out);
            else
                messageClient = "numéro de choix invalide##";
        } catch (NumberFormatException | IOException e) {
            messageClient = "numéro de choix invalide##";
        }
        return null;
    }

    public Programmeur inscrireProgrammeur(BufferedReader in , PrintWriter out) throws IOException {
        out.println("Entrez votre identifiant");
        String identifiant = lireLigne(in, out);
        if (programmeurExist(identifiant)) {
            messageClient = "Identifiant déjà utilisé##";
            return null;
        }
        out.println("Choisissez un mot de passe: ");
        String mdp = lireLigne(in, out);
        out.println("Entrez l'adresse de votre serveur ftp");
        String adresseServeur = lireLigne(in, out);
        messageClient = "Inscription réussie##";
        return addProgrammeur(identifiant, mdp, adresseServeur);
    }

    public Programmeur connecterProgrammeur(BufferedReader in , PrintWriter out) throws IOException {
        out.println("Entrez votre identifiant");
        String identifiant = lireLigne(in, out);
        out.println("Entrez votre mot de passe");
        String mdp = lireLigne(in, out);
        for (Programmeur p : programmeurs) {
            if (p.verifier(identifiant, mdp)) {
                messageClient = "Connexion réussie##";
                return p;
            }
        }
        messageClient = "Identifiant ou mot de passe incorrect##";
        return null;
    }

    public static Boolean programmeurExist(String identifiant){
        for (Programmeur p : programmeurs)
            if(p.getIdentifiant().equals(identifiant))
                return true;
        return false;
    }

    public static Programmeur addProgrammeur(String identifiant, String mdp, String adresseServeur) {
        Programmeur newp = new Programmeur(identifiant, mdp, adresseServeur);
        programmeurs.add(newp);
        return newp;
    }

    public URLClassLoader getURLClassLoader(Programmeur programmeur) throws MalformedURLException {
        URLClassLoader urlcl = null;
        urlcl = URLClassLoader.newInstance(new URL[] {new URL(programmeur.getAdresseFtp())});
        return urlcl;
    }

    protected void finalize() throws Throwable {
        client.close();
    }
}
