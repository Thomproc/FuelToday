package fr.univpau.fueltoday;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager instance;
    private List<Station> stations;
    private List<Service> services;

    private DataManager(Context context) {
        stations = new ArrayList<>();
        services = new ArrayList<>();
        String[] serviceArray = context.getResources().getStringArray(R.array.services);

        for (int i = 0; i < serviceArray.length; i++) {
            Service service = new Service(serviceArray[i]);
            services.add(service);
        }
    }

    public static DataManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataManager(context);
        }
        return instance;
    }

    public Station getStationAt(int index) {
        return this.stations.get(index);
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }
    public List<Service> getServices() {
        return this.services;
    }

    public Service getServiceAt(int position) {
        return this.services.get(position);
    }
}

