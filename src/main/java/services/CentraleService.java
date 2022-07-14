package services;

import model.CentraleConfig;
import model.CentraleReleve;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class CentraleService {
    private ApiCentraleService apiCentraleService;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public CentraleService() {
        this.apiCentraleService = new ApiCentraleService();
    }

    /**
     *
     * @param centraleConfigList liste des centrales
     * @param dateFrom date à partir de laquelle on veut les relevés
     * @param dateTo date jusqu'a laquelle on veut les revelés
     * @return liste totale des relevés de la période
     */
    public List<CentraleReleve> getAllReleveCentrales(List<CentraleConfig> centraleConfigList, Date dateFrom, Date dateTo) {
        List<CentraleReleve> centraleReleveList = new ArrayList<>();
        centraleConfigList.forEach(centraleConfig -> {
            List<Map<String, Object>> resultatsCentrale = apiCentraleService.getCentraleRevele(centraleConfig, dateFrom, dateTo);

            if(resultatsCentrale != null) {
                // on récupère les données brutes et on va en faire une liste regroupant toutes les centrales
                int index = 0;
                for(Map<String, Object> resultatCentrale : resultatsCentrale) {
                    // on check si il n'y pas un trou dans les données
                    if(index > 0 && index < resultatsCentrale.size()) {
                        Map<String, Object> previousResult = resultatsCentrale.get(index - 1);
                        // si le pas entre l'element et celui d'avant est supérieur au pas normal on fait la moyenne des n-1 et n+1
                        long intervalDuree = (Integer) resultatCentrale.get(centraleConfig.getFromProperty()) - (Integer) previousResult.get(centraleConfig.getToProperty());
                        long minutesDifference = TimeUnit.MILLISECONDS.toMinutes(intervalDuree);
                        if(minutesDifference > 0) {
                            // il y a un element manquant
                            CentraleReleve centraleRelevePrevious = parseResultatCentrale(previousResult, centraleConfig);

                            Map<String, Object> nextResult = resultatsCentrale.get(index + 1);
                            CentraleReleve centraleReleveNext = parseResultatCentrale(nextResult, centraleConfig);

                            CentraleReleve centraleReleveAAjouter = new CentraleReleve();
                            centraleReleveAAjouter.setNomCentrale(centraleConfig.getNom());
                            centraleReleveAAjouter.setDateFrom(centraleRelevePrevious.getDateTo());
                            centraleReleveAAjouter.setDateTo(centraleReleveNext.getDateFrom());
                            centraleReleveAAjouter.setPuissance((centraleRelevePrevious.getPuissance() + centraleReleveNext.getPuissance()) / 2);
                            centraleReleveList.add(centraleReleveAAjouter);
                        }
                    }

                    centraleReleveList.add(this.parseResultatCentrale(resultatCentrale, centraleConfig));
                    index++;
                }
            } else {
                System.err.println("CentraleService - aucun résultat pour la centrale : " + centraleConfig.getNom());
            }
        });
        return centraleReleveList;
    }

    /**
     *
     * @param resultatCentrale objet à parser
     * @param centraleConfig config de la centrale
     * @return objet CentralReleve propre
     */
    private CentraleReleve parseResultatCentrale(Map<String, Object> resultatCentrale, CentraleConfig centraleConfig) {
        CentraleReleve centraleReleve = new CentraleReleve();
        centraleReleve.setNomCentrale(centraleConfig.getNom());
        if(resultatCentrale.get(centraleConfig.getFromProperty()) instanceof Integer) {
            Integer value = (Integer) resultatCentrale.get(centraleConfig.getFromProperty());
            if(value != null) {
                centraleReleve.setDateFrom(new Date(value.longValue() * 1000));
            }
        }
        if(resultatCentrale.get(centraleConfig.getToProperty()) instanceof Integer) {
            Integer value = (Integer) resultatCentrale.get(centraleConfig.getToProperty());
            if(value != null) {
                centraleReleve.setDateTo(new Date(value.longValue() * 1000));
            }
        }
        if(resultatCentrale.get(centraleConfig.getPuissanceProperty()) instanceof Integer) {
            centraleReleve.setPuissance((Integer) resultatCentrale.get(centraleConfig.getPuissanceProperty()));
        }
        centraleReleve.setIntervalMinute(centraleConfig.getNbMinutesEnregistrement());
        return centraleReleve;
    }

    /**
     * agrège les relevés avec l'interval de temps donné
     * @param centraleReleveList liste des relevés
     * @param dateFrom date début
     * @param dateTo date fin
     * @param interval interval en minute sur lequel on veut la somme des puissances 
     * @return
     */
    public List<Map<String, Object>> computeRelevesAggregate(List<CentraleReleve> centraleReleveList, Date dateFrom, Date dateTo, int interval) {
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDateTime localDateFrom = LocalDateTime.from(LocalDateTime.from(dateFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
        LocalDateTime localDateTo = LocalDateTime.from(dateTo.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

        for (LocalDateTime date = localDateFrom; date.isBefore(localDateTo); date = date.plus(Duration.of(interval, ChronoUnit.MINUTES))) {
            LocalDateTime dateCompare = date.plus(Duration.of(interval, ChronoUnit.MINUTES));
            // pas les memes type...
            Date dateDebutPeriode = Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
            Date dateFinPeriode = Date.from(dateCompare.atZone(ZoneId.systemDefault()).toInstant());
            List<CentraleReleve> listeRelevePeriode = centraleReleveList.stream().filter((centraleReleve -> {
                // on regarde si il y a une intersection de date
                return centraleReleve.getDateFrom().before(dateFinPeriode) && dateDebutPeriode.before(centraleReleve.getDateTo());
            })).toList();

            Map<String, Object> itemResultat = new HashMap<>();
            itemResultat.put("dateFrom", formatter.format(date));
            itemResultat.put("dateTo", formatter.format(dateCompare));
            double totalPuissancePeriode = 0;
            for (CentraleReleve centraleReleve : listeRelevePeriode) {
                totalPuissancePeriode += centraleReleve.getPuissance() / (centraleReleve.getIntervalMinute() / interval);
            }
            itemResultat.put("somme", totalPuissancePeriode);
            result.add(itemResultat);
        }
        return result;
    }
}
