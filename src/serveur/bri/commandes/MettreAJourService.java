package serveur.bri.commandes;

import serveur.bri.Commande;
import serveur.bri.Service;
import serveur.bri.ServiceRegistry;
import serveur.exceptions.BRiException;
import serveur.programmeurs.Programmeur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;


public class MettreAJourService implements Commande {
    @Override
    public String executer(BufferedReader in, PrintWriter out, Programmeur programmeur) throws IOException {
        out.println("Entrez le nom du service à modifier");
        String nomClasse = in.readLine();
        try {
            ServiceRegistry.updateService(ServiceFtpLoader.loadServiceClass(nomClasse, programmeur), programmeur.getIdentifiant());
            return "Service modifié " + nomClasse + "##";
        } catch (ClassNotFoundException e) {
            return "Le service demandé est introuvable sur votre serveur FTP##";
        } catch (MalformedURLException e) {
            return "L'adresse FTP fournie est invalide##";
        } catch (BRiException e){
            return e.getMessage() + "##";
        }
    }

    @Override
    public String toString() {
        return "Mettre à jour un service";
    }
}
