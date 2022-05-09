package ortiz.burgos.marc.m06json;

import ortiz.burgos.marc.m06json.models.Estacio;
import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.result.UpdateResult;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.JOptionPane;
import org.bson.Document;
import ortiz.burgos.marc.m06json.mappings.EstacioMapping;

/**
 *
 * @author marco
 */
public class InicioApp {

    public static void main(String[] args) {
        Properties defaultProps = new Properties();
        InputStream in = InicioApp.class.getClassLoader()
                .getResourceAsStream("defaultProperties.txt");
        try {
            defaultProps.load(in);
            downloadFile(defaultProps.getProperty("url1"), "station_information.json");
            downloadFile(defaultProps.getProperty("url2"), "station_status.json");

            insertJSON(readJson("station_information"), readJson("station_status"));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private static void downloadFile(String url, String json) {
        try {
            InputStream is = new URL(url).openStream();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            String line;
            FileWriter flux = new FileWriter(json);
            BufferedWriter file = new BufferedWriter(flux);
            while ((line = reader.readLine()) != null) {
                file.write(line);
                file.newLine();
            }
            file.close();
            is.close();
        } catch (MalformedURLException ex) {
            System.err.println(ex.getMessage());
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private static Estacio readJson(String valor) {
        //Creació del parser
        Gson gson = new Gson();
        Estacio est = new Estacio();
        try {
            //Des-serialització d'un array estàtic
            est = gson.fromJson(
                    new FileReader("src/main/resources/" + valor + ".json"),
                    Estacio.class
            );

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return est;
    }

    private static void insertJSON(Estacio est_info, Estacio est_status) {

        MongoClientURI connectionString = new MongoClientURI(
                "mongodb://localhost:27017"
        );

        MongoClient mongoClient = new MongoClient(connectionString);

        MongoDatabase bbdd = mongoClient.getDatabase("bicing");

        //Agafem la coleccio amb la que volem veure els seus documents
        MongoCollection<Document> coleccio_info = bbdd.getCollection("station_information");
        MongoCollection<Document> coleccio_status = bbdd.getCollection("station_status");

        List<Document> documents_info = new ArrayList<>();
        List<Document> documents_status = new ArrayList<>();

        try {
            if (coleccio_info.countDocuments() == 0 || coleccio_status.countDocuments() == 0) {
                for (int i = 0; i < est_info.getData().getStations().size(); i++) {
                    documents_info.add(new EstacioMapping().setStationInfoToDocument(est_info, i));
                }
                for (int i = 0; i < est_status.getData().getStations().size(); i++) {
                    documents_status.add(new EstacioMapping().setStationStatusToDocument(est_status, i));
                }

                coleccio_info.insertMany(documents_info);
                coleccio_status.insertMany(documents_status);
            } else {
                updateStatusCollection(coleccio_status, est_status);
                updateInformationCollection(coleccio_info, est_info);
            }

            optionPanel(coleccio_info, coleccio_status);
        } catch (MongoException ex) {
            System.err.println("Excepció: " + ex.toString());
        }
    }

    private static void updateStatusCollection(MongoCollection<Document> collection, Estacio est_status) {
        UpdateResult uResult = null;
        for (int i = 0; i < est_status.getData().getStations().size(); i++) {
            uResult = collection.updateMany(
                    eq("station_id", est_status.getData().getStations().get(i).getStation_id()),
                    new Document("$set", new Document("station_id", est_status.getData().getStations().get(i).getStation_id())
                    .append("num_bikes_available", est_status.getData().getStations().get(i).getNum_bikes_available())
                    .append("num_docks_available", est_status.getData().getStations().get(i).getNum_docks_available())));
        }
        System.out.println("Documents status actualizats: " + uResult.getModifiedCount());
    }

    private static void updateInformationCollection(MongoCollection<Document> collection, Estacio est_info) {
        UpdateResult uResult = null;
        for (int i = 0; i < est_info.getData().getStations().size(); i++) {
            uResult = collection.updateMany(
                    eq("station_id", est_info.getData().getStations().get(i).getStation_id()),
                    new Document("$set", new Document("station_id", est_info.getData().getStations().get(i).getStation_id())
                    .append("name", est_info.getData().getStations().get(i).getName())
                    .append("lat", est_info.getData().getStations().get(i).getLat())
                    .append("lon", est_info.getData().getStations().get(i).getLon())));
        }
        System.out.println("Documents info actualizats: " + uResult.getModifiedCount());
    }

    private static void optionPanel(MongoCollection<Document> coleccio_info, MongoCollection<Document> coleccio_status) {
        int select = -1;
        while (select != 0) {
            try {
                String lectura = JOptionPane.showInputDialog(null,
                        "************************\n"
                        + "Selecciona una opció:\n"
                        + "1.- Quantes bicis hi han disponibles en una estació?\n"
                        + "2.- Quants llocs hi han disponibles per aparcar a una estació?\n"
                        + "3.- Quina és l’estació més propera a mi amb bicis lliures?\n"
                        + "4.- Quina és l’estació més propera a mi amb llocs disponibles?\n"
                        + "0.- Salir\n"
                        + "************************");

                select = Integer.parseInt(lectura);

                switch (select) {
                    case 1:
                        numBikes(coleccio_info, coleccio_status);
                        break;
                    case 2:
                        numDocks(coleccio_info, coleccio_status);
                        break;
                    case 3:
                        JOptionPane.showMessageDialog(null, "No esta implementat");
                        break;
                    case 4:
                        JOptionPane.showMessageDialog(null, "No esta implementat");
                        break;
                    case 0:
                        JOptionPane.showMessageDialog(null, "Fins aviat!");
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Selecciona un numero disponible");
                        break;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error! Selecciona una de les opcions!");
            }
        }
    }

    private static void numBikes(MongoCollection<Document> coleccio_info, MongoCollection<Document> coleccio_status) {
        String question;
        question = JOptionPane.showInputDialog(null, "Escriu el nom de l'estació: ");

        Document doc1 = coleccio_info.find(eq("name", question)).first();
        Document doc2 = coleccio_status.find(eq("station_id", doc1.get("station_id"))).first();

        JOptionPane.showMessageDialog(null, "El numero de bicis disponibles es:\n" + doc2.get("num_bikes_available"));
    }

    private static void numDocks(MongoCollection<Document> coleccio_info, MongoCollection<Document> coleccio_status) {
        String question;
        question = JOptionPane.showInputDialog(null, "Escriu el nom de l'estació: ");

        Document doc1 = coleccio_info.find(eq("name", question)).first();
        Document doc2 = coleccio_status.find(eq("station_id", doc1.get("station_id"))).first();

        JOptionPane.showMessageDialog(null, "El numero de llocs disponibles es:\n" + doc2.get("num_docks_available"));
    }
}
