package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.CentraleConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ApiCentraleService {
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    private BufferedReader reader;
    public List<Map<String, Object>> getCentraleRevele(CentraleConfig centraleConfig, Date dateFrom, Date dateTo) {
        List<Map<String, Object>> response = new ArrayList<>();
        StringBuilder responseContent = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder().append("https://interview.beta.bcmenergy.fr/")
                    .append(centraleConfig.getNom())
                    .append("?from=")
                    .append(sdf.format(dateFrom))
                    .append("&to=")
                    .append(sdf.format(dateTo));
            URL url = new URL(sb.toString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            int responseCode = con.getResponseCode();
            String line = null;
            if(responseCode > 299) {
                System.err.println("ApiCentraleService - erreur - code réponse=" + responseCode + " pour la centrale " + centraleConfig.getNom());
            } else {
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            }
            if("json".equals(centraleConfig.getFormatAPI())) {
                ObjectMapper mapper = new ObjectMapper();
                response = mapper.readValue(responseContent.toString(), List.class);
            } else if("csv".equals(centraleConfig.getFormatAPI())) {
                // todo
                response = null;
            }
        } catch (Exception e) {
            System.err.println("ApiCentraleService - erreur globale - pour la centrale " + centraleConfig.getNom());
            e.printStackTrace();
        }
        return response;
    }
}
