package fr.univpau.fueltoday;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class RequestAPI {
    public interface ApiCallback<T> {
        void onSuccess(T data);
        void onFailure(String errorMessage);
    }

    private static Context context;
    private static final String BASE_URL = "https://data.economie.gouv.fr/api/explore/v2.1/catalog/datasets/prix-des-carburants-en-france-flux-instantane-v2/records?where=within_distance(geom%2C%20GEOM%27POINT(";
    private static final String END_URL = "&limit=-1";
    private static RequestAPI instance;
    private RequestQueue requestQueue;

    private RequestAPI(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        this.context = context;
    }

    public static synchronized RequestAPI getInstance(Context context) {
        if (instance == null) {
            instance = new RequestAPI(context);
        }
        return instance;
    }

    public void getJson(double  latitude, double longitude, double rayon, final ApiCallback<JSONObject> callback) {
        // url pour les stations selon la position et rayon
        String url = BASE_URL + longitude +  "%20" + latitude + ")%27%2C%20" + rayon + "km)";
        SharedPreferences preferences = context.getSharedPreferences("preferences", MODE_PRIVATE);

        String fuel = preferences.getString("fuelType", "SP98");
        // url qui conserve les résultats avec le fuel qui nous intéresse
        url = url + "%20AND%20%22" + fuel + "%22%20in%20carburants_disponibles";

        // url avec filtre des services qui nous intéresse
        DataManager dataManager = DataManager.getInstance(context);
        List<Service> services = dataManager.getServices();
        for (Service service : services) {
            if(service.isChecked()){
                url = url + " %20AND%20%22";
                String serviceName = service.getServiceName();
                serviceName = serviceName.replace(" ", "%20");

                url = url + serviceName + "%22%20in%20services_service";
            }
        }
        // url qui trie
        int orderBy = preferences.getInt("orderBy", 0);
        if(orderBy == 0) {
            url = url + "&order_by=" + fuel.toLowerCase() + "_prix%20ASC";
        }

        // url qui enlève la limite des résultats
        url = url + END_URL;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onFailure("Error response from the server");
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

}
