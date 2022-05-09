package ortiz.burgos.marc.m06json.models;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author marco
 */
public class Estacio implements Serializable {

    private int lastUpdated;
    private int ttl;
    private Data data;

    public Estacio(int lastUpdated, int ttl, Data data) {
        this.lastUpdated = lastUpdated;
        this.ttl = ttl;
        this.data = data;
    }

    public Estacio() {
    }

    public int getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(int input) {
        this.lastUpdated = input;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int input) {
        this.ttl = input;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data input) {
        this.data = input;
    }

    public class Data implements Serializable {

        private List<Stations> stations;

        public List<Stations> getStations() {
            return stations;
        }

        public void setStations(List<Stations> input) {
            this.stations = input;
        }

        public class Stations implements Serializable {

            private int station_id;
            private String name;
            private double lat;
            private double lon;

            private int num_bikes_available;
            private int num_docks_available;

            public int getStation_id() {
                return station_id;
            }

            public void setStation_id(int input) {
                this.station_id = input;
            }

            public String getName() {
                return name;
            }

            public void setName(String input) {
                this.name = input;
            }

            public double getLat() {
                return lat;
            }

            public void setLat(double input) {
                this.lat = input;
            }

            public double getLon() {
                return lon;
            }

            public int getNum_bikes_available() {
                return num_bikes_available;
            }

            public void setNum_bikes_available(int num_bikes_available) {
                this.num_bikes_available = num_bikes_available;
            }

            public int getNum_docks_available() {
                return num_docks_available;
            }

            public void setNum_docks_available(int num_docks_available) {
                this.num_docks_available = num_docks_available;
            }
        }
    }
}
