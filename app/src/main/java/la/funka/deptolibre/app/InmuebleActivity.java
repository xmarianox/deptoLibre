package la.funka.deptolibre.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
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

public class InmuebleActivity extends Activity {

    TextView title_inmueble;
    ImageView img_inmueble;
    TextView price_inmueble;
    TextView mercado_pago;
    TextView garantia;
    TextView direccion_inmueble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inmueble);

        // Traemos los datos del otro activity.
        String meli_id = getIntent().getStringExtra("inmueble_id");

        if (isNetworkAvailable() == true) {
            try {
                new BuscarMeli().execute("https://api.mercadolibre.com/items/" + URLEncoder.encode(meli_id, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Ha ocurrido un error, estas seguro de que tienes internet??", Toast.LENGTH_LONG).show();
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public class BuscarMeli extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(InmuebleActivity.this, "Por favor espere...", "Buscando inmuebles...", true);
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

                String title = json.getString("title");
                title_inmueble = (TextView) findViewById(R.id.title_inmueble);
                title_inmueble.setText(title);

                JSONArray jsonArray = json.getJSONArray("pictures");
                JSONObject jsonList = jsonArray.getJSONObject(0);
                String img_url = jsonList.getString("url");
                img_inmueble = (ImageView) findViewById(R.id.img_inmueble);
                new DownloadImageTask(img_inmueble).execute(img_url);

                String price = String.valueOf(json.getInt("price"));
                price_inmueble = (TextView) findViewById(R.id.price_inmueble);
                price_inmueble.setText(price);

                boolean mercadoPago = json.getBoolean("accepts_mercadopago");
                mercado_pago = (TextView) findViewById(R.id.mercado_pago);
                if ( mercadoPago == true ) {
                    mercado_pago.setText("Se acepta MercadoPago.");
                    //mercado_pago.setTextColor(Color.parseColor("#4b2"));
                } else {
                    mercado_pago.setText("No se acepta MercadoPago.");
                    //mercado_pago.setTextColor(Color.parseColor("#900"));
                }

                JSONObject jsonLocation = json.getJSONObject("location");
                String direccion = jsonLocation.getString("address_line");
                direccion_inmueble = (TextView) findViewById(R.id.direccion_inmueble);
                direccion_inmueble.setText(direccion);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(InmuebleActivity.this, "Ocurrio un error al buscar el inmueble...", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView miImageView;

        public DownloadImageTask(ImageView bmImage) {
            this.miImageView = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            miImageView.setImageBitmap(result);
        }
    }
}
