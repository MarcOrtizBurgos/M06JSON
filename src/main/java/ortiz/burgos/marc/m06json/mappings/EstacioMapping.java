package ortiz.burgos.marc.m06json.mappings;

import org.bson.Document;
import ortiz.burgos.marc.m06json.models.Estacio;

/**
 *
 * @author marco
 */
public class EstacioMapping {
    
    public static Document setStationInfoToDocument(Estacio est_info, int i) {
        return new Document("station_id", est_info.getData().getStations().get(i).getStation_id())
                            .append("name", est_info.getData().getStations().get(i).getName())
                            .append("lat", est_info.getData().getStations().get(i).getLat())
                            .append("lon", est_info.getData().getStations().get(i).getLon());
    }
    
    public static Document setStationStatusToDocument(Estacio est_status, int i) {
        return new Document("station_id", est_status.getData().getStations().get(i).getStation_id())
                            .append("num_bikes_available", est_status.getData().getStations().get(i).getNum_bikes_available())
                            .append("num_docks_available", est_status.getData().getStations().get(i).getNum_docks_available());
    }
}
