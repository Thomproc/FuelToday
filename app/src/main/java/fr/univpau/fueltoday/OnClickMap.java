package fr.univpau.fueltoday;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

public class OnClickMap implements View.OnClickListener {
    private Context context;
    private Station station;

    OnClickMap(Context context, Station station) {
        this.context = context;
        this.station = station;
    }

    @Override
    public void onClick(View view) {
        String packageName = "com.google.android.apps.maps";
        String geoUri = "geo:" + station.getLatitude() + "," + station.getLongitude() + "?q=" + station.getAddress();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        intent.setPackage(packageName);

        // Vérifier si l'application Google Maps est installée !!
        context.startActivity(intent);
    }
}
