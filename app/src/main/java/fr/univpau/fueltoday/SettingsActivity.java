package fr.univpau.fueltoday;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText editTextRadius;
    private Spinner spinnerFuelType;
    private Spinner spinnerOrderBy;
    private ServiceAdapter serviceAdapter;
    private  List<Service> services = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editTextRadius = findViewById(R.id.input_radius);
        editTextRadius.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                String radius = editTextRadius.getText().toString();
                if(!radius.isEmpty() && Double.parseDouble(radius) > 50) {
                    editTextRadius.setText("50");
                }
                return false;
            }
        });
        spinnerFuelType = findViewById(R.id.spinnerFuelType);
        spinnerOrderBy = findViewById(R.id.spinnerOrderBy);

        Button buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePreferences();
            }
        });

        List<Service> oldServices = DataManager.getInstance(this).getServices();
        for (Service oldService : oldServices) {
            this.services.add(new Service(oldService.getServiceName(), oldService.isChecked()));
        }

        this.serviceAdapter = new ServiceAdapter(this, this.services);

        ListView lv_services = findViewById(R.id.lv_services);
        lv_services.setAdapter(serviceAdapter);

        loadPreferences();
    }

    private void savePreferences() {
        String radius = editTextRadius.getText().toString();
        if(radius.isEmpty() || Double.parseDouble(radius) == 0) {
            Toast.makeText(SettingsActivity.this, "Renseignez un rayon de recherche", Toast.LENGTH_SHORT).show();
            return;
        }
        DataManager.getInstance(this).setServices(this.services);

        String fuelType = spinnerFuelType.getSelectedItem().toString();
        int orderBy = getSpinnerSelection(spinnerOrderBy);

        SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("radius", radius);
        editor.putString("fuelType", fuelType);
        editor.putInt("orderBy", orderBy);
        editor.apply();

        Toast.makeText(SettingsActivity.this, "Préférences sauvegardées !", Toast.LENGTH_SHORT).show();
    }

    private void loadPreferences() {
        SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);

        editTextRadius.setText(preferences.getString("radius", "1"));
        setSpinnerSelection(spinnerFuelType, preferences.getString("fuelType", ""));

        int position = preferences.getInt("orderBy", 0);
        spinnerOrderBy.setSelection(position);
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        int position = adapter.getPosition(value);
        spinner.setSelection(position);
    }

    private int getSpinnerSelection(Spinner spinner) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        String value = spinner.getSelectedItem().toString();
        return adapter.getPosition(value);
    }

}
