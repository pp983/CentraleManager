package model;

import java.util.Date;

public class CentraleReleve {
    private String nomCentrale;
    private Date dateFrom;
    private Date dateTo;
    private Integer puissance;
    private Integer intervalMinute;

    public String getNomCentrale() {
        return nomCentrale;
    }

    public void setNomCentrale(String nomCentrale) {
        this.nomCentrale = nomCentrale;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Integer getPuissance() {
        return puissance;
    }

    public void setPuissance(Integer puissance) {
        this.puissance = puissance;
    }

    public Integer getIntervalMinute() {
        return intervalMinute;
    }

    public void setIntervalMinute(Integer intervalMinute) {
        this.intervalMinute = intervalMinute;
    }
}
