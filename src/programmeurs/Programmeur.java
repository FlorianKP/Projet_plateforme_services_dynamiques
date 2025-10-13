package programmeurs;

public class Programmeur {
    private String identifiant;
    private String mdp;


    public Programmeur(String mdp, String identifiant) {
        this.mdp = mdp;
        this.identifiant = identifiant;
    }

    public boolean verifier(String identifiant, String mdp){
        return this.identifiant.equals(identifiant) && this.mdp.equals(mdp);
    }
}
