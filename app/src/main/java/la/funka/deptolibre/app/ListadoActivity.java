package la.funka.deptolibre.app;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

        // Enviamos la consulta a la api.
        try {
            new BuscarMeli().execute("http://www.deptolibre.com/api/v1/realestate/search?State="+ URLEncoder.encode(lugar_id, "utf-8") +"&From="+ URLEncoder.encode(desde, "utf-8") +"&Until="+ URLEncoder.encode(hasta, "utf-8") +"&Guests="+ URLEncoder.encode(huespedes, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Instanciamos el adaptador.
        meliAdapter = new MeliAdapter(this, R.layout.meli_list_item, lista_meli);
        setListAdapter(meliAdapter);
    }

    /**
     *   BuscarMeli:
     *   - - - - - -
     *   http://www.deptolibre.com/api/v1/realestate/search?State=TUxBUENBUGw3M2E1&From=20/10/2014&Until=20/11/2014&Guests=2
     *
     *   Modelo JSON:
     *   - - - - - -
     *   {
     *      Operacion: "Alquiler Temporario en Capital Federal",
     *      available_filters: [],
     *      available_sorts: [],
     *      filters: [],
     *      Paging: {},
     *      query: "?",
     *      related_results: [ ],
     *      results: [],
     *      secondary_results: [ ],
     *      site_id: "MLA",
     *      Sort: {}
     *   }
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
                    int price = listadoJson.getInt("price");

                    //Log.i("Datos Traidos por el jason: Title: ", title);
                    //Log.i("Datos Traidos por el jason: SubTitle: ", subtitle);

                    //public MeliListItem (int image, int price, String title, String description)
                    lista_meli.add(new MeliListItem(thumbnail, price, title, subtitle));
                }
                meliAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(ListadoActivity.this, "Ocurrio un error al buscar el inmueble...", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
