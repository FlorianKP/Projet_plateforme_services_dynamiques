package serveur.bri;

import serveur.programmeurs.Programmeur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public interface Commande {
    public String executer(BufferedReader in, PrintWriter out, Programmeur programmeur) throws IOException;
}
