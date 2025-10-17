package serveur.bri;

import serveur.programmeurs.Programmeur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServiceProg implements Service {
    private static List<Programmeur> programmeurs = new ArrayList<Programmeur>();

    private Socket client;

    private String messageClient = "";

    private static CommandeRegistry commandeRegistry;

    public ServiceProg(Socket socket) {
        client = socket;
    }

    public static void setLesCommandes(CommandeRegistry commandeRegistry) {
        ServiceProg.commandeRegistry = commandeRegistry;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            Programmeur programmeur = authentifierProgrammeur(in, out);
            while (programmeur == null) {
                programmeur = authentifierProgrammeur(in, out);
            }

            while (true){
                int choix = demanderChoix(in, out);

                if (choix > 0)
                    traiterChoix(choix, in, out, programmeur);
            }

        } catch (IOException e) {}

        System.out.println("*********Connexion au service programmeur terminée");
        try {client.close();} catch (IOException e2) {}

    }

    private int demanderChoix(BufferedReader in, PrintWriter out) throws IOException {
        out.println(messageClient + commandeRegistry.toStringue());
        try {
            return Integer.parseInt(lireLigne(in));
        } catch (NumberFormatException e) {
            messageClient = "Choix invalide. Opération annulée##";
            return -1;
        }
    }

    private void traiterChoix(int choix, BufferedReader in, PrintWriter out, Programmeur programmeur) throws IOException {
        Commande commande = null;
        try {
            commande = commandeRegistry.getCommande(choix);
            messageClient = commande.executer(in, out, programmeur);
        } catch (ArrayIndexOutOfBoundsException e) {
            messageClient = "Numéro de commande invalide##";
        }
    }

    public Programmeur authentifierProgrammeur(BufferedReader in, PrintWriter out) {
        out.println(messageClient + "Entrez 1 pour vous inscrire ou 2 pour vous connecter :##" +
                "1- S'inscrire##" +
                "2- Se connecter");

        try {
            int choix = Integer.parseInt(lireLigne(in));
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
        String identifiant = lireLigne(in);
        if (programmeurExist(identifiant)) {
            messageClient = "Identifiant déjà utilisé##";
            return null;
        }
        out.println("Choisissez un mot de passe: ");
        String mdp = lireLigne(in);
        out.println("Entrez l'adresse de votre serveur ftp");
        String adresseServeur = lireLigne(in);
        messageClient = "Inscription réussie##";
        return addProgrammeur(identifiant, mdp, adresseServeur);
    }

    public Programmeur connecterProgrammeur(BufferedReader in , PrintWriter out) throws IOException {
        out.println("Entrez votre identifiant");
        String identifiant = lireLigne(in);
        out.println("Entrez votre mot de passe");
        String mdp = lireLigne(in);
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

    public static String lireLigne(BufferedReader in) throws IOException {
        String input = in.readLine();
        if (input == null || input.equalsIgnoreCase("exit")) {
            throw new IOException("Client déconnecté");
        }
        return input;
    }

    protected void finalize() throws Throwable {
        client.close();
    }
}
