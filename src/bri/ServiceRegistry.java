package bri;

import java.util.ArrayList;
import java.util.List;

public class ServiceRegistry {
	// cette classe est un registre de services
	// partag�e en concurrence par les clients et les "ajouteurs" de services,
	// un Vector pour cette gestion est pratique

	static {
		servicesClasses = null;
	}
	private static List<Class<?>> servicesClasses;

// ajoute une classe de service apr�s contr�le de la norme BLTi
	public static void addService(Class <? extends Service> serviceClasse) {
		// v�rifier la conformit� par introspection
		// si non conforme --> exception avec message clair
		// si conforme, ajout au vector
		if(servicesClasses == null) {
			servicesClasses = new ArrayList<Class<?>>();
		}
		servicesClasses.add(serviceClasse);
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
