package fr.univpau.fueltoday;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Station {
    private double latitude;
    private double longitude;
    private double distance;
    private String city;
    private String address;

    private JSONArray fuelsAvailables = null;
    private JSONArray fuelsUnavailables = null;
    private Map<String, Double> pricePerFuel = new HashMap<>();

    private JSONArray services = null;

    Station(JSONObject stationJSON, double userLatitude, double userLongitude) {
        try {
            this.latitude = stationJSON.getDouble("latitude");
            this.longitude = stationJSON.getDouble("longitude");
            this.distance = this.calculateDistance(userLatitude, userLongitude);


            this.address = stationJSON.getString("adresse");
            this.city = stationJSON.getString("ville");

            if(!stationJSON.isNull("carburants_disponibles")) {
                this.fuelsAvailables = stationJSON.getJSONArray("carburants_disponibles");
                for (int i = 0; i < fuelsAvailables.length(); i++) {
                    String fuel = fuelsAvailables.getString(i);
                    double price = Double.parseDouble(stationJSON.getString(fuel.toLowerCase() + "_prix"));
                    pricePerFuel.put(fuel, price);
                }
            }

            if(!stationJSON.isNull("carburants_indisponibles")) {
                this.fuelsUnavailables = stationJSON.getJSONArray("carburants_indisponibles");
            }

            if(!stationJSON.isNull("services_service")) {
                this.services = stationJSON.getJSONArray("services_service");
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public double calculateDistance(double userLatitude, double userLongitude) {
        double R = 6371; // Rayon de la Terre en km
        double convertedLatitude = this.latitude / 100000;
        double convertedLongitude = this.longitude / 100000;
        double dLat = Math.toRadians(convertedLatitude - userLatitude);
        double dLon = Math.toRadians(convertedLongitude - userLongitude);
        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(Math.toRadians(userLatitude)) * Math.cos(Math.toRadians(convertedLatitude)) *
                                Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance en km
    }
    public double getLatitude() { return this.latitude; }

    public double getLongitude() {
        return this.longitude;
    }

    public double getDistance() {
        return this.distance;
    }

    public Map<String, Double> getPricePerFuel() {
        return this.pricePerFuel;
    }

    public double getPrice(String fuel) {
        return this.pricePerFuel.get(fuel);
    }
    public JSONArray getServices() {
        return services;
    }
    public String getCity() {
        return city;
    }
    public String getAddress() {
        return address;
    }
}
