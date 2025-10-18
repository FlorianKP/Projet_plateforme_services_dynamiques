package serveur.bri.commandes;

import serveur.bri.Service;
import serveur.programmeurs.Programmeur;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ServiceFtpLoader {
    private ServiceFtpLoader() {}

    public static Class<? extends Service> loadServiceClass(String className, Programmeur programmeur) throws ClassNotFoundException, MalformedURLException {
        URLClassLoader urlcl = URLClassLoader.newInstance(new URL[]{ new URL(programmeur.getAdresseFtp()) });
        return urlcl.loadClass(className).asSubclass(Service.class);
    }
}
