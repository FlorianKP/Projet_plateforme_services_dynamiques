package bri;

import exceptions.BRiException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Socket;

public class CertificationBRi {
    protected static void estCertifieeBRi(Class<? extends Service> serviceClasse, String identifiant) throws BRiException {
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
        Field[] fields = serviceClasse.getDeclaredFields();
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
}
