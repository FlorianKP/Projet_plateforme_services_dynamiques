package programmeurs;

public class Programmeur {
    private String identifiant;
    private String mdp;

    private String adresseFtp;

    public String getAdresseFtp() {
        return adresseFtp;
    }

    public void setAdresseFtp(String adresseFtp) {
        this.adresseFtp = adresseFtp;
    }

    public Programmeur(String mdp, String identifiant, String adresseFtp) {
        this.mdp = mdp;
        this.identifiant = identifiant;
        this.adresseFtp = adresseFtp;
    }

    public boolean verifier(String identifiant, String mdp){
        return this.identifiant.equals(identifiant) && this.mdp.equals(mdp);
    }
}
