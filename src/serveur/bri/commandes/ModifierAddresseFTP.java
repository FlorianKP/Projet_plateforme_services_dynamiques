package serveur.bri.commandes;

import serveur.bri.Commande;
import serveur.programmeurs.Programmeur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ModifierAddresseFTP implements Commande {

    @Override
    public String executer(BufferedReader in, PrintWriter out, Programmeur programmeur) throws IOException {
        out.println("Entrez la nouvelle adresse de votre serveur ftp");
        String newAdresseFtp = in.readLine();
        programmeur.setAdresseFtp(newAdresseFtp);
        return "Adresse modifiée, nouvelle adresse : " + newAdresseFtp + "##";
    }

    @Override
    public String toString() {
        return "Modifier votre addresse FTP";
    }
}