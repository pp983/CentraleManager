import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import model.CentraleConfig;
import model.CentraleReleve;
import services.CentraleService;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ProductionCentraleCompteur {
    private Date dateFrom;
    private Date dateTo;
    private String format;
    private CentraleService centraleService;

    private static int INTERVAL = 15;
    public ProductionCentraleCompteur(Date dateFrom, Date dateTo, String format) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.format = format;
    }

    public void execute() {
        centraleService = new CentraleService();
        ObjectMapper objectMapper = new ObjectMapper();
        List<CentraleConfig> centrales = null;
        try {
            // on récupère la config des centrales
            centrales = objectMapper.readValue(new File("src/main/ressources/ConfigCentrales.json"), new TypeReference<List<CentraleConfig>>(){});
        } catch (Exception e) {
            System.err.println("ProductionCentraleCompteur - erreur lecture config centrale");
            e.printStackTrace();
        }
        if(centrales != null && !centrales.isEmpty()) {
             // le but ici est de récupérer les datas propres donc par interval de 15min
            List<CentraleReleve> centraleRelevesList = centraleService.getAllReleveCentrales(centrales, dateFrom, dateTo);
            List<Map<String, Object>> centraleRelevesListAgregee = centraleService.computeRelevesAggregate(centraleRelevesList, dateFrom, dateTo, INTERVAL);

            if("json".equals(this.format)) {
                try {
                    System.out.println(new ObjectMapper().writeValueAsString(centraleRelevesListAgregee));
                } catch (JsonProcessingException e) {
                    System.err.println("ProductionCentraleCompteur - erreur format de résultat");
                    e.printStackTrace();
                }
            } else if("csv".equals(this.format)) {
                // todo
            }
        }
    }
}
