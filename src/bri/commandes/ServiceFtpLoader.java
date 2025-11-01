package bri.commandes;

import bri.Service;
import exceptions.BRiException;
import programmeurs.Programmeur;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;


public class ServiceFtpLoader {
    private ServiceFtpLoader() {}

    public static Class<? extends Service> loadServiceClass(String className, Programmeur programmeur) throws ClassNotFoundException, MalformedURLException, BRiException {
        URLClassLoader urlcl = new URLClassLoader(new URL[]{ new URL(programmeur.getAdresseFtp()) });
        if (!className.startsWith(programmeur.getIdentifiant() +"."))
            throw new BRiException("Le nom du package ne correspond pas à votre nom de login");
        return urlcl.loadClass(className).asSubclass(Service.class);
    }
}
