package fr.univpau.fueltoday;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ServiceAdapter extends ArrayAdapter<Service> {
    private Context context;
    public ServiceAdapter(Context context, List<Service> services) {
        super(context, 0, services);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.service_item, parent, false);
        }

        Service service = getItem(position);

        TextView tv_name = convertView.findViewById(R.id.tv_name);
        CheckBox checkBox = convertView.findViewById(R.id.checkBox);

        tv_name.setText(service.getServiceName());
        checkBox.setChecked(service.isChecked());

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service.clicked();
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}
