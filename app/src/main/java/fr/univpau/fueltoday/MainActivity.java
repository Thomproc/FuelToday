package fr.univpau.fueltoday;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_CODE = 1;
    private static final String[] permissions = {android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private RequestAPI requestAPI;

    private StationAdapter stationAdapter;
    private final List<Station> stations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.requestAPI = RequestAPI.getInstance(this);

        ListView lv_stations = findViewById(R.id.lv_stations);

        this.stationAdapter = new StationAdapter(this, stations);

        lv_stations.setAdapter(stationAdapter);

        Button btn_searching = (Button) findViewById(R.id.search_station);
        btn_searching.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stations.clear();
                        searchStations();
                    }
                }
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(checkPermissions() && !LocationTracker.isInstanceCreated()) {
            Intent intent = new Intent(getApplicationContext(), LocationTracker.class);
            startService(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
        return true;
    }

    private void searchStations() {
        if (this.isNetworkAvailable() && this.checkGPSOn()) {
            LocationTracker locationTracker = LocationTracker.getInstance();
            Location currentLocation = locationTracker.getCurrentLocation();

            if (currentLocation != null) {
                double latitude = currentLocation.getLatitude();
                double longitude = currentLocation.getLongitude();

                SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
                double rayon = Double.parseDouble(preferences.getString("radius", "1"));

                requestAPI.getJson(latitude, longitude, rayon, new RequestAPI.ApiCallback<JSONObject>() {
                            @Override
                            public void onSuccess(JSONObject data) {
                                try {
                                    JSONArray allStationsJSON = data.getJSONArray("results");
                                    for (int i = 0; i < allStationsJSON.length(); i++) {
                                        JSONObject stationJSON = allStationsJSON.getJSONObject(i);
                                        Station station = new Station(stationJSON, latitude, longitude);
                                        stations.add(station);
                                    }

                                    SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
                                    int orderBy = preferences.getInt("orderBy", 0);
                                    if(orderBy == 1) {
                                        stations.sort(new Comparator<Station>() {
                                            @Override
                                            public int compare(Station s1, Station s2) {
                                                Log.i("info", String.valueOf(s1.getDistance()));
                                                return Double.compare(s1.getDistance(), s2.getDistance());
                                            }
                                        });
                                    }
                                    DataManager.getInstance(MainActivity.this).setStations(stations);

                                    if(allStationsJSON.length() == 0) {
                                        Toast.makeText(MainActivity.this, "Aucune station trouvée...", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                stationAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Toast.makeText(MainActivity.this, "Problème de réseau...", Toast.LENGTH_SHORT).show();
                            }
                        });
                Toast.makeText(MainActivity.this, "Recherche en cours...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "La position n'est pas encore disponible", Toast.LENGTH_SHORT).show();
            }
        } else {
            stationAdapter.notifyDataSetChanged();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if(activeNetworkInfo == null) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Aucun signal internet")
                        .setMessage("Veuillez vous connecter à internet afin de rechercher les stations essence aux alentours")
                        .setPositiveButton("Ok", null)
                        .setCancelable(true)
                        .create()
                        .show();
            }
            return activeNetworkInfo != null;
        }
        return false;
    }


    private boolean checkGPSOn(){
        LocationTracker locationTracker = LocationTracker.getInstance();
        boolean GPSIsOn = locationTracker.GPSIsOn();
        if(!GPSIsOn) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Activation de la localisation")
                    .setMessage("Souhaitez-vous être redirigé afin d'activer la localisation ?")
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Non", null)
                    .setCancelable(true)
                    .create()
                    .show();
        }
        return GPSIsOn;
    }

    private boolean checkPermissions() {
        boolean permissionsGranted = permissionsGranted();
        if (!permissionsGranted) {
            requestPermissions();
        }
        return permissionsGranted;
    }

    private boolean permissionsGranted() {
        for (String permission : this.permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, this.permissions, this.LOCATION_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == this.LOCATION_PERMISSION_CODE) {
            // Si la permission n'est pas accordée : on ferme l'application
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder dialog =  new AlertDialog.Builder(this);
                dialog.setTitle("Autorisation manquante")
                        .setMessage("Autorisez l'application à utiliser la localisation pour trouver les stations essence les plus proches ! \n\nParamètres -> Applications -> Fuel Today -> Autorisations")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }
            // Sinon la permission est accordée
            else {
                if(!LocationTracker.isInstanceCreated()) {
                    Intent intent = new Intent(getApplicationContext(), LocationTracker.class);
                    startService(intent);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(MainActivity.this, LocationTracker.class);
        stopService(intent);
    }
}