package fr.univpau.fueltoday;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import androidx.annotation.NonNull;

import static android.content.Context.MODE_PRIVATE;

public class StationAdapter extends ArrayAdapter<Station> {
    private final Context context;
    public StationAdapter(Context context, List<Station> stations) {
        super(context, 0, stations);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.list_item_station, null);
        }

        Station station = getItem(position);

        TextView tv_city = convertView.findViewById(R.id.tv_city);
        TextView tv_address = convertView.findViewById(R.id.tv_address);
        TextView tv_fuel_price_dist = convertView.findViewById(R.id.tv_fuel_price_dist);
        Button btn_go = convertView.findViewById(R.id.btn_go);

        tv_city.setText(station.getCity());
        tv_address.setText(station.getAddress());

        SharedPreferences preferences = context.getSharedPreferences("preferences", MODE_PRIVATE);
        DecimalFormat df = new DecimalFormat("#0.00");
        String dist = df.format(station.getDistance()) + " Km" + "     ";
        String fuel = preferences.getString("fuelType", "SP98");
        String fuelAndPrice = fuel + " : " + station.getPrice(fuel) + " €/L";
        tv_fuel_price_dist.setText(dist + fuelAndPrice);
        btn_go.setOnClickListener(new OnClickMap(context, station));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, StationDetails.class);
                intent.putExtra("stationIndex", position);
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
