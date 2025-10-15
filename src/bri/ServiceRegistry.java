package bri;

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
	public static void addService(Class <? extends Service> serviceClasse) {
		// v�rifier la conformit� par introspection
		// si non conforme --> exception avec message clair
		// si conforme, ajout au vector
		servicesClasses.add(serviceClasse);
	}

	// ajoute une classe de service apr�s contr�le de la norme BLTi
	public static void removeService(Class <? extends Service> serviceClasse) {
		servicesClasses.remove(serviceClasse);
	}

	public static void updateService(Class <? extends Service> serviceClasse) {
		removeService(serviceClasse);
		addService(serviceClasse);
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
