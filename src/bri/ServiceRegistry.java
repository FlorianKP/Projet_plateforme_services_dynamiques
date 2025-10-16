package bri;

import exceptions.BRiException;
import programmeurs.Programmeur;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServiceRegistry {
	// cette classe est un registre de services
	// partag�e en concurrence par les clients et les "ajouteurs" de services,
	// un Vector pour cette gestion est pratique

	static {
		servicesClasses = new ArrayList<Class<?>>();
	}
	private static List<Class<?>> servicesClasses;

// ajoute une classe de service apr�s contr�le de la norme BLTi
	public static void addService(Class <? extends Service> serviceClasse, String identifiant) throws BRiException {
		// v�rifier la conformit� par introspection
		// si non conforme --> exception avec message clair
		// si conforme, ajout au vector
		estCertifieeBRi(serviceClasse, identifiant);
		servicesClasses.add(serviceClasse);
	}

	private static void estCertifieeBRi(Class<? extends Service> serviceClasse, String identifiant) throws BRiException {
		if(!serviceClasse.getPackageName().equals(identifiant)){
			throw new BRiException("Le package de votre classe n'a pas le même nom que votre identifiant");
		}
		if(!implementeService(serviceClasse)){
			throw new BRiException("La classe n'implémente pas l'interface bri.Service");
		}
		if(Modifier.isAbstract(serviceClasse.getModifiers())){
			throw new BRiException("La classe ne peut pas être abstract");
		}
		if(!Modifier.isPublic(serviceClasse.getModifiers())){
			throw new BRiException("La classe doit être public");
		}
		if(!aConstructeurPublicSansException((serviceClasse))){
			throw new BRiException("La classe n'a pas de constructeur public (Socket) sans exception");
		}
		if(!aAttributSocket(serviceClasse)){
			throw new BRiException("La classe n'a pas d'attribut Socket private final");
		}
		if(!aMethodeToStringue(serviceClasse)) {
			throw new BRiException("La classe n'a pas de méthode public static String toStringue() sans exception");
		}
	}

	private static boolean aMethodeToStringue(Class<? extends Service> serviceClasse) {
		Method[] methods = serviceClasse.getMethods();
		for(Method method : methods){
			if(Modifier.isPublic(method.getModifiers())){
				if(method.getExceptionTypes().length == 0){
					if(Modifier.isStatic(method.getModifiers())){
						if(method.getReturnType().equals(String.class)){
							if(method.getName().equals("toStringue")){
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	private static boolean aAttributSocket(Class<? extends Service> serviceClasse){
		Field[] fields = serviceClasse.getFields();
		for(Field field : fields){
			if(Modifier.isPrivate(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && field.getType().equals(java.net.Socket.class)){
				return true;
			}
		}
		return false;
	}


	private static boolean aConstructeurPublicSansException(Class<? extends Service> serviceClasse){
		try{
			Class<?>[] exceptions = serviceClasse.getConstructor(Socket.class).getExceptionTypes();
			if(exceptions.length == 0){
				return true;
			}
		}catch(NoSuchMethodException e){
			return false;
		}
		return false;
	}

	private static boolean implementeService(Class<? extends Service> serviceClasse) {
		Class<?>[] interfaces = serviceClasse.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            if (anInterface.getName().equals("bri.Service")) {
                return true;
            }
        }
		return false;
	}


	public static void removeService(Class <? extends Service> serviceClasse) {
		servicesClasses.remove(serviceClasse);
	}

	public static void updateService(Class <? extends Service> serviceClasse, String identifiant) throws BRiException {
		removeService(serviceClasse);
		addService(serviceClasse, identifiant);
	}

	
// renvoie la classe de service (numService -1)	
	public static Class<?> getServiceClass(int numService) {
		return servicesClasses.get(numService-1);
	}
	
// liste les activit�s pr�sentes
	public static String toStringue() {
		String result = "Activit�s pr�sentes :##";
		int i = 0;
		for(Class<?> classe : servicesClasses) {
			result+= ++i + " - " + classe.getSimpleName();
		}
		return result; 
	}

}
