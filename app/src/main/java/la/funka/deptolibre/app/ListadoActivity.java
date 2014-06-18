package la.funka.deptolibre.app;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class ListadoActivity extends ListActivity {

    MeliAdapter meliAdapter;
    ArrayList<MeliListItem> lista_meli;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);

        lista_meli = new ArrayList<MeliListItem>();

        // Traemos los datos del otro activity.
        String lugar_id = getIntent().getStringExtra("lugar_id");
        String desde = getIntent().getStringExtra("desde");
        String hasta = getIntent().getStringExtra("hasta");
        String huespedes = getIntent().getStringExtra("huespedes");

        if (isNetworkAvailable() == true) {
            // Enviamos la consulta a la api.
            try {
                new BuscarMeli().execute("http://www.deptolibre.com/api/v1/realestate/search?State=" + URLEncoder.encode(lugar_id, "utf-8") + "&From=" + URLEncoder.encode(desde, "utf-8") + "&Until=" + URLEncoder.encode(hasta, "utf-8") + "&Guests=" + URLEncoder.encode(huespedes, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            // Instanciamos el adaptador.
            meliAdapter = new MeliAdapter(this, R.layout.meli_list_item, lista_meli);
            setListAdapter(meliAdapter);
        } else {
            Toast.makeText(this, "Ha ocurrido un error, estas seguro de que tienes internet??", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String meli_id = lista_meli.get(position).getId();
        String nombre = lista_meli.get(position).getTitle();
        //Toast.makeText(this, "Item seleccionado: "+ nombre + " id: "+ meli_id, Toast.LENGTH_SHORT).show();

        Intent intent_inmueble = new Intent(ListadoActivity.this, InmuebleActivity.class);
        intent_inmueble.putExtra("inmueble_id", meli_id);
        startActivity(intent_inmueble);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     *   BuscarMeli:
     *   - - - - - -
     *   http://www.deptolibre.com/api/v1/realestate/search?State=TUxBUENBUGw3M2E1&From=20/10/2014&Until=20/11/2014&Guests=2
     */
    public class BuscarMeli extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(ListadoActivity.this, "Por favor espere...", "Buscando inmuebles...", true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            InputStream inputStream = null;
            String result = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(urls[0]));
                inputStream = httpResponse.getEntity().getContent();
                if(inputStream != null) {
                    BufferedReader buffer = new BufferedReader( new InputStreamReader(inputStream));
                    String line = "";
                    while ((line = buffer.readLine()) != null)
                        result += line;
                    inputStream.close();
                } else {
                    // ERROR;
                }
            } catch (Exception e) {
                // ERROR;
                Log.d("InputStream", e.getLocalizedMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String resultado) {
            // En result está el texto que viene de Internet
            dialog.dismiss();

            try {
                JSONObject json = new JSONObject(resultado);
                JSONArray jsonArrayResults = json.getJSONArray("results");
                for (int i = 0; i < jsonArrayResults.length(); i++) {

                    JSONObject listadoJson = jsonArrayResults.getJSONObject(i);

                    String title = listadoJson.getString("title");
                    String subtitle = listadoJson.getString("subtitle");
                    String thumbnail = listadoJson.getString("thumbnail");
                    String id = listadoJson.getString("id");
                    int price = listadoJson.getInt("price");

                    //public MeliListItem (int image, int price, String title, String description)
                    lista_meli.add(new MeliListItem(thumbnail, id, price, title, subtitle));
                }
                meliAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(ListadoActivity.this, "Ocurrio un error al buscar el inmueble...", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
