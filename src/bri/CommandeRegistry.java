package bri;

import java.util.ArrayList;
import java.util.List;

public class CommandeRegistry {
    private static List<Commande> commandes;

    static {
        commandes = new ArrayList<>();
    }


    public void addCommande(Commande cmd) {
        commandes.add(cmd);
    }

    public Commande getCommande(int id) {
        return commandes.get(id-1);
    }

    public String toStringue() {
        StringBuilder sb = new StringBuilder();
        sb.append("Entrez le numéro de la fonctionnalité désirée :");
        int i = 0;
        for (Commande cmd : commandes)
            sb.append("##").append(++i).append(" - ").append(cmd.toString());

        return sb.toString();
    }
}
