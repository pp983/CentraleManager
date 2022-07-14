

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ApplicationStarter {
    public static void main(String[] args) {

        String from = args[0];
        String to = args[1];
        String format = args[2];

        if(from == null || to == null || format == null) {
            System.err.println("ApplicationStarter - erreur - paramètre entrée manquant");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date dateFrom = null;
        try {
            dateFrom = sdf.parse(from);
        } catch (Exception e) {
            System.err.println("ApplicationStarter - erreur format date entrée from="+from);
            e.printStackTrace();
        }
        Date dateTo = null;
        try {
            dateTo = sdf.parse(to);
        } catch (Exception e) {
            System.err.println("ApplicationStarter - erreur format date entrée to="+to);
            e.printStackTrace();
        }

        ProductionCentraleCompteur productionCentraleCompteur = new ProductionCentraleCompteur(dateFrom, dateTo, format);
        productionCentraleCompteur.execute();
    }
}
