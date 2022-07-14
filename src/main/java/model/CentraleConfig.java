package model;

public class CentraleConfig {
    private String nom;
    private Integer nbMinutesEnregistrement;
    private String formatAPI;
    private String fromProperty;
    private String toProperty;
    private String puissanceProperty;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getNbMinutesEnregistrement() {
        return nbMinutesEnregistrement;
    }

    public void setNbMinutesEnregistrement(Integer nbMinutesEnregistrement) {
        this.nbMinutesEnregistrement = nbMinutesEnregistrement;
    }

    public String getFormatAPI() {
        return formatAPI;
    }

    public void setFormatAPI(String formatAPI) {
        this.formatAPI = formatAPI;
    }

    public String getFromProperty() {
        return fromProperty;
    }

    public void setFromProperty(String fromProperty) {
        this.fromProperty = fromProperty;
    }

    public String getToProperty() {
        return toProperty;
    }

    public void setToProperty(String toProperty) {
        this.toProperty = toProperty;
    }

    public String getPuissanceProperty() {
        return puissanceProperty;
    }

    public void setPuissanceProperty(String puissanceProperty) {
        this.puissanceProperty = puissanceProperty;
    }
}
