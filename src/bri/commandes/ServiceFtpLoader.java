package bri.commandes;

import bri.Service;
import programmeurs.Programmeur;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;


public class ServiceFtpLoader {
    private ServiceFtpLoader() {}

    public static Class<? extends Service> loadServiceClass(String className, Programmeur programmeur) throws ClassNotFoundException, MalformedURLException {
        URLClassLoader urlcl = new URLClassLoader(new URL[]{ new URL(programmeur.getAdresseFtp()) });
        return urlcl.loadClass(className).asSubclass(Service.class);
    }
}
