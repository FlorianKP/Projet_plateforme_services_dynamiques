package bri;

import programmeurs.Programmeur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.Socket;
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

            addProgrammeur(identifiant, mdp);

            out.println("Tapez le numéro de la fonctionnalité désirée");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void addProgrammeur(String identifiant, String mdp) {
        for (Programmeur p : programmeurs) {
            if(p.verifier(identifiant, mdp))
                return;
        }
        programmeurs.add(new Programmeur(identifiant, mdp));
    }
}
