package la.funka.deptolibre.app;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.parse.Parse;
import com.parse.ParseAnalytics;

import java.util.Calendar;

public class MainActivity extends Activity {

    private ArrayAdapter<String> autocomplete_adapter;

    private int year;
    private int month;
    private int day;

    private EditText desde;
    private EditText hasta;
    private EditText huesped;
    private Button btn_buscar;

    static final int DATE_DIALOG_DESDE = 100;
    static final int DATE_DIALOG_HASTA = 200;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Libreria de Parse agregada.
        Parse.initialize(this, "6AB3RUUYwMaX034QApFKA0HQ0m88D6WHpu9XSeCT", "qStZKK3QhvZpUZC5GBdoX38J9PVaLssksIFGPdvl");
        // Trackeamos la cantidad de request.
        ParseAnalytics.trackAppOpened(getIntent());

        // Autocompletado de los lugares para buscar...
        final AutoCompleteTextView lugar = (AutoCompleteTextView) findViewById(R.id.input_buscar);

        // Traemos el array con los lugares para llenar el autocomplete.
        String[] lugares = getResources().getStringArray(R.array.lugares_array);

        // Adaptador para el autocomplete.
        autocomplete_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lugares);
        lugar.setAdapter(autocomplete_adapter);

        // Probamos el date picker en el click de input desde.
        desde = (EditText) findViewById(R.id.input_desde);
        desde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_DESDE);
                setDateOnViewDesde();
            }
        });

        // Probamos el date picker en el click de input hasta.
        hasta = (EditText) findViewById(R.id.input_hasta);
        hasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_HASTA);
                setDateOnViewHasta();
            }
        });

        // Traemos el input huesped
        huesped = (EditText) findViewById(R.id.input_huesped);

        btn_buscar = (Button) findViewById(R.id.btn_buscar);
        btn_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String result_lugat = lugar.getText().toString();
                // Traemos el id de ML para enviar a la busqueda.
                String query_id = getMeliID(result_lugat);
                // fechas de busqueda.
                String fecha_desde = desde.getText().toString();
                String fecha_hasta = hasta.getText().toString();
                //int cant_huesped = Integer.parseInt(huesped.getText().toString());
                String cant_huesped = huesped.getText().toString();

                // Pasamos todos los datos al activity que trae los resultados.
                Intent resultado = new Intent(MainActivity.this, ListadoActivity.class);
                resultado.putExtra("lugar_id", query_id);
                resultado.putExtra("desde", fecha_desde);
                resultado.putExtra("hasta", fecha_hasta);
                resultado.putExtra("huespedes", cant_huesped);
                startActivity(resultado);
            }
        });

    }

    public String getMeliID(String lugar){
        String id = null;

        if (lugar.equals("Brasil")) {
            id = "TUxBUEJSQWwyMzA1";
        } else {
            if (lugar.equals("Bs.As. Costa Atlántica")) {
                id = "TUxBUENPU2ExMmFkMw";
            } else {
                if (lugar.equals("Bs.As. G.B.A. Norte")) {
                    id = "TUxBUEdSQWU4ZDkz";
                } else {
                    if (lugar.equals("Bs.As. G.B.A. Oeste")) {
                        id = "TUxBUEdSQWVmNTVm";
                    } else {
                        if (lugar.equals("Bs.As. G.B.A. Sur")) {
                            id = "TUxBUEdSQXJlMDNm";
                        } else {
                            if (lugar.equals("Buenos Aires Interior")) {
                                id = "TUxBUFpPTmFpbnRl";
                            } else {
                                if (lugar.equals("Capital Federal")) {
                                    id = "TUxBUENBUGw3M2E1";
                                } else {
                                    if (lugar.equals("Catamarca")) {
                                        id = "TUxBUENBVGFiY2Fm";
                                    } else {
                                        if (lugar.equals("Chaco")) {
                                            id = "TUxBUENIQW8xMTNhOA";
                                        } else {
                                            if (lugar.equals("Chubut")) {
                                                id = "TUxBUENIVXQxNDM1MQ";
                                            } else {
                                                if (lugar.equals("Corrientes")) {
                                                    id = "TUxBUENPUnM5MjI0";
                                                } else {
                                                    if (lugar.equals("Córdoba")) {
                                                        id = "TUxBUENPUmFkZGIw";
                                                    } else {
                                                        if (lugar.equals("Entre Ríos")) {
                                                            id = "TUxBUEVOVHMzNTdm";
                                                        } else {
                                                            if (lugar.equals("Formosa")) {
                                                                id = "TUxBUEZPUmE1OTk5";
                                                            } else {
                                                                if (lugar.equals("Jujuy")) {
                                                                    id = "TUxBUEpVSnk3YmUz";
                                                                } else {
                                                                    if (lugar.equals("La Pampa")) {
                                                                        id = "TUxBUExBWmE1OWMy";
                                                                    } else {
                                                                        if (lugar.equals("La Rioja")) {
                                                                            id = "TUxBUExBWmEyNzY0";
                                                                        } else {
                                                                            if (lugar.equals("Mendoza")) {
                                                                                id = "TUxBUE1FTmE5OWQ4";
                                                                            } else {
                                                                                if (lugar.equals("Misiones")) {
                                                                                    id = "TUxBUE1JU3MzNjIx";
                                                                                } else {
                                                                                    if (lugar.equals("Neuquén")) {
                                                                                        id = "TUxBUE5FVW4xMzMzNQ";
                                                                                    } else {
                                                                                        if (lugar.equals("Paraguay")) {
                                                                                            id = "TUxBUFBBUjYxNzczMg";
                                                                                        } else {
                                                                                            if (lugar.equals("República Dominicana")) {
                                                                                                id = "TUxBUFJFUDQyMjQ4Ng";
                                                                                            } else {
                                                                                                if (lugar.equals("Río Negro")) {
                                                                                                    id = "TUxBUFLNT29iZmZm";
                                                                                                } else {
                                                                                                    if (lugar.equals("Salta")) {
                                                                                                        id = "TUxBUFNBTGFjMTJi";
                                                                                                    } else {
                                                                                                        if (lugar.equals("San Juan")) {
                                                                                                            id = "TUxBUFNBTm5lYjU4";
                                                                                                        } else {
                                                                                                            if (lugar.equals("San Luis")) {
                                                                                                                id = "TUxBUFNBTnM0ZTcz";
                                                                                                            } else {
                                                                                                                if (lugar.equals("Santa Cruz")) {
                                                                                                                    id = "TUxBUFNBTno3ZmY5";
                                                                                                                } else {
                                                                                                                    if (lugar.equals("Santa Fe")) {
                                                                                                                        id = "TUxBUFNBTmU5Nzk2";
                                                                                                                    } else {
                                                                                                                        if (lugar.equals("Santiago del Estero")) {
                                                                                                                            id = "TUxBUFNBTm9lOTlk";
                                                                                                                        } else {
                                                                                                                            if (lugar.equals("Tierra del Fuego")) {
                                                                                                                                id = "TUxBUFRJRVoxM2M5YQ";
                                                                                                                            } else {
                                                                                                                                if (lugar.equals("Tucumán")) {
                                                                                                                                    id = "TUxBUFRVQ244NmM3";
                                                                                                                                } else {
                                                                                                                                    if (lugar.equals("USA")) {
                                                                                                                                        id = "TUxBUFVTQWl1cXdlMg";
                                                                                                                                    } else {
                                                                                                                                        // Uruguay
                                                                                                                                        id = "TUxBUFVSVXllZDVl";
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            }
                                                                                                                        }
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return id;
    }

    // Cargamos la fecha en el input desde
    public void setDateOnViewDesde() {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        desde.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year));
    }

    // Cargamos la fecha en el input hasta
    public void setDateOnViewHasta() {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        hasta.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year));
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 100:
                return new DatePickerDialog(this, datePickerListenerDesde, year, month,day);
            case 200 :
                return new DatePickerDialog(this, datePickerListenerHasta, year, month,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListenerDesde = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            desde.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year));
        }
    };

    private DatePickerDialog.OnDateSetListener datePickerListenerHasta = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            hasta.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year));
        }
    };
}
