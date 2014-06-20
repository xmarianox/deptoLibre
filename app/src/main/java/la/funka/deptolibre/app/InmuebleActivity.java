package la.funka.deptolibre.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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

public class InmuebleActivity extends FragmentActivity implements GoogleMap.OnMapClickListener {

    private TextView title_inmueble;
    private ImageView img_inmueble;
    private TextView price_inmueble;
    private TextView mercado_pago;
    private TextView direccion_inmueble;
    private WebView web_description;
    private ViewFlipper flippy;
    private String meli_id;

    // Swipper
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private Context mContext;
    private final GestureDetector detector = new GestureDetector(new SwipeGestureDetector());

    // Maps
    private GoogleMap mapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inmueble);

        mapa = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

        // Traemos los datos del otro activity.
        meli_id = getIntent().getStringExtra("inmueble_id");

        mContext = this;
        flippy = (ViewFlipper) this.findViewById(R.id.view_flipper);

        if (isNetworkAvailable() == true) {
            try {
                new BuscarMeli().execute("https://api.mercadolibre.com/items/" + URLEncoder.encode(meli_id, "utf-8"));

                flippy.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        detector.onTouchEvent(event);
                        return true;
                    }
                });

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

    @Override
    public void onMapClick(LatLng latLng) {

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

                for (int i = 0; i< jsonArray.length(); i++){
                    JSONObject jsonList = jsonArray.getJSONObject(i);
                    String img_url = jsonList.getString("url");
                    img_inmueble = new ImageView(getApplicationContext());
                    new DownloadImageTask(img_inmueble).execute(img_url);
                    flippy.addView(img_inmueble);
                }

                String price = String.valueOf(json.getInt("price"));
                price_inmueble = (TextView) findViewById(R.id.price_inmueble);
                price_inmueble.setText("$"+price);

                boolean mercadoPago = json.getBoolean("accepts_mercadopago");
                mercado_pago = (TextView) findViewById(R.id.mercado_pago);
                if ( mercadoPago == true ) {
                    mercado_pago.setText("Se acepta MercadoPago.");
                    mercado_pago.setTextColor(getResources().getColor(R.color.green_ok));
                } else {
                    mercado_pago.setText("No se acepta MercadoPago.");
                    mercado_pago.setTextColor(getResources().getColor(R.color.red_price));
                }

                JSONObject jsonLocation = json.getJSONObject("location");
                String direccion = jsonLocation.getString("address_line");
                direccion_inmueble = (TextView) findViewById(R.id.direccion_inmueble);
                direccion_inmueble.setText(direccion);

                // Mapas
                Double latitude = jsonLocation.getDouble("latitude");
                Double longitude = jsonLocation.getDouble("longitude");
                LatLng meli_inmueble = new LatLng(latitude, longitude);

                mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(meli_inmueble, 15));
                mapa.setMyLocationEnabled(true);
                mapa.getUiSettings().setZoomControlsEnabled(false);
                mapa.getUiSettings().setCompassEnabled(true);
                mapa.addMarker(new MarkerOptions()
                        .position(meli_inmueble)
                        .title(title)
                        .snippet("Depto Libre.")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin))
                        .anchor(0.5f, 0.5f));
                mapa.setOnMapClickListener(InmuebleActivity.this);

                JSONArray jsonDescription = json.getJSONArray("descriptions");
                JSONObject jsonID = jsonDescription.getJSONObject(0);
                String id_description = jsonID.getString("id");

                // Descripcion del inmueble
                try {
                    new BuscarMeliDescription().execute("https://api.mercadolibre.com/items/" + URLEncoder.encode(meli_id, "utf-8") + "/descriptions/" + URLEncoder.encode(id_description, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(InmuebleActivity.this, "Ocurrio un error al buscar el inmueble...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class BuscarMeliDescription extends AsyncTask<String, Void, String> {
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
                String text_data = json.getString("text");
                web_description = (WebView) findViewById(R.id.web_description);
                web_description.loadData(text_data, "text/html; charset=UTF-8", null);
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

    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    flippy.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_in));
                    flippy.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_out));
                    flippy.showNext();
                    return true;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    flippy.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.right_in));
                    flippy.setOutAnimation(AnimationUtils.loadAnimation(mContext,R.anim.right_out));
                    flippy.showPrevious();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
