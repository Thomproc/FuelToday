package fr.univpau.fueltoday;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LocationTracker extends Service implements LocationListener {
    private static LocationTracker singleton = null;
    private LocationManager locationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        singleton = this;
    }

    public static LocationTracker getInstance() {
        return LocationTracker.singleton;
    }
    public static boolean isInstanceCreated() {
        return singleton != null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public Location getCurrentLocation() {
        try {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (lastKnownLocation != null) {
                return lastKnownLocation;
            } else {
                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
                return null;
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    public boolean GPSIsOn(){
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(this);
    }


}
