package fr.univpau.fueltoday;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Locale;
import java.util.Map;

public class StationDetails extends AppCompatActivity {
    private LinearLayout fuelsContainer;
    private LinearLayout servicesContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_details);

        int stationIndex = getIntent().getIntExtra("stationIndex", -1);

        if(stationIndex == -1){
            this.finish();
        }
        Station station = DataManager.getInstance(this).getStationAt(stationIndex);

        TextView tv_city = findViewById(R.id.detail_city);
        TextView tv_address = findViewById(R.id.detail_address);
        Button btn_go = findViewById(R.id.detail_btn_go);

        tv_city.setText(station.getCity());
        tv_address.setText(station.getAddress());
        btn_go.setOnClickListener(
                new OnClickMap(this, station)
        );

        this.fuelsContainer = findViewById(R.id.fuelsContainer);

        Map<String, Double> fuels = station.getPricePerFuel();
        for (Map.Entry<String, Double> entry : fuels.entrySet()) {
            String fuelName = entry.getKey();
            Double fuelPrice = entry.getValue();
            addFuelTextView(fuelName, fuelPrice);
        }

        this.servicesContainer = findViewById(R.id.servicesContainer);
        JSONArray services = station.getServices();

        if(services == null) {
            this.addServiceTextView("Aucun service proposé");
        }
        else {
            for(int i = 0; i < services.length(); i++){
                try {
                    String service = services.getString(i);
                    this.addServiceTextView(service);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    private void addFuelTextView(String fuelName, Double fuelPrice) {
        TextView textViewFuel = new TextView(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(0, 0, 0, 16);

        textViewFuel.setLayoutParams(params);
        textViewFuel.setText(String.format(Locale.getDefault(), "%s : %.2f €/L", fuelName, fuelPrice));
        this.fuelsContainer.addView(textViewFuel);
    }

    private void addServiceTextView(String service) {
        TextView textViewService = new TextView(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(0, 0, 0, 16);

        textViewService.setLayoutParams(params);
        textViewService.setText(service);
        this.servicesContainer.addView(textViewService);
    }

}