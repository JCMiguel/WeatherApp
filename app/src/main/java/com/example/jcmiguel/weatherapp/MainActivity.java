/**
 *      MainActivity.java
 *  Contiene el detalle de la actividad principal de la aplicación. La aplicación permite consultar
 *  la temperatura de sendas ciudades almacenadas en una base de preferencias por medio de la API de
 *  OpenWeather.
 *
 *  Autor: Juan Cristian Miguel
 */
package com.example.jcmiguel.weatherapp;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends ListActivity  {

    private ArrayAdapter<String> arrayAdapter;
    private Ciudad ciudad;
    private Integer REQ_CODE_ADDCITY = 91675;
    private Integer REQ_CODE_EDITCITY = 75943;
    private Integer REQ_CODE_APPID = 34851;

    private Integer ADD_CITY_MODE = 138;
    private Integer EDIT_CITY_MODE = 579;
    private Integer LOAD_APPID_MODE = 791;


    /**
     *  Método que se ejecuta al inicio de la actividad. En él se cargan los datos almacenados en
     *  la base de preferencias (si los hubiere) y se inicializa el menú contextual de la ListView.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ciudad = new Ciudad(getBaseContext());

        if(ciudad.getCantCiudades() == 0 ) {
            Toast.makeText(this, "Agrege ciudades por medio del menu desplegable " +
                    "para consultar su temperatura.", Toast.LENGTH_LONG).show();
        }

        arrayAdapter = new ArrayAdapter<String>( this,
                android.R.layout.simple_list_item_1, ciudad.getArrayListCiudades());

        setListAdapter(arrayAdapter);
        registerForContextMenu(getListView());
    }


    /**
     *  Método que se ejecuta cuando se crea el menú contextual de la ListView.
     */
    @Override
    public void onCreateContextMenu (ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }


    /**
     *  Método que se ejecuta cuando se selecciona una opción dentro del menú contextual vinculado
     *  a la ListView. Muestra dos opciones: "Editar ciudad" y "Eliminar ciudad".
     *   1) Editar ciudad:
     *      Lanza la actividad NuevaCiudad con requestCode de edición (REQ_CODE_EDITCITY)
     *      Se le pasa como parámetros las variables "editar" en TRUE e "idCiudadElegida" queç
     *      permite referenciar a la ciudad suceptible de moficiaciones. El Extra "ciudadElegida"
     *      permite inicializar el EditText de la actividad siguiente, teniendo como valor inicial
     *      el nombre de la ciudad.
     *   2) Eliminar ciudad:
     *      Borra la ciudad seleccionada de la base de preferencias y del ArrayList de la lista.
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.opcionContextualEditar:
                Intent intent = new Intent(this,NuevaCiudad.class);
                intent.putExtra("modo", EDIT_CITY_MODE);
                intent.putExtra("ciudadElegida", ciudad.getCiudadPorID((int)info.id));
                intent.putExtra("idCiudadElegida", (int)info.id);
                startActivityForResult(intent, REQ_CODE_EDITCITY);
                return true;
            case R.id.opcionContextualEliminar:
                ciudad.removeCiudad((int)info.id);
                arrayAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Ciudad borrada con exito", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    /**
     *  Método que crea el menú desplegable ubicado en la esquina superior derecha de la pantalla.
     *  Muestra las opciones "Agregar ciudad" y "Ayuda".
     */
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    /**
     *  Se ejecuta este método cuando se selecciona una opción del menú desplegable, ubicado
     *  en la esquina superior derecha de la pantalla.
     */
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        //Aqui proceso lo que hace cada opción del menú
        switch(item.getItemId()) {
            case R.id.opcionMenuAgregarCiudad:
                Intent intentCity = new Intent(this,NuevaCiudad.class);
                intentCity.putExtra("modo", ADD_CITY_MODE);
                startActivityForResult(intentCity, REQ_CODE_ADDCITY);
                break;

            case R.id.opcionMenuCargarAPPID:
                Intent intentAPPID = new Intent(this,NuevaCiudad.class);
                intentAPPID.putExtra("modo", LOAD_APPID_MODE);
                startActivityForResult(intentAPPID, REQ_CODE_APPID);
                break;

            case R.id.opcionMenuAyuda:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.contenidoDialogoAyuda);
                builder.setTitle(R.string.tituloDialogoAyuda);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Se ejecuta este método cuando se pulsa sobre una opción de la lista.
     * Lanza la AsyncTask con la URL de openweather y la ciudad seleccionada.
     */
    @Override
    public void onListItemClick(ListView l, View view, int position, long id) {
        String ciudadStr = (String) l.getItemAtPosition(position);
        String APPID = new String();

        APPID = ciudad.getAPPID();

        if(APPID.length() != 0) {
            new GetJSONOpenWeather().execute("http://api.openweathermap.org/data/2.5/weather?q="
                    + ciudadStr + "&units=metric&appid=" + APPID);
        }
        else {
            Toast.makeText(this, "Necesita crear una cuenta en OpeanWeather y " +
                    "configurar su APPID para realizar la consulta", Toast.LENGTH_LONG).show();
        }


    }


    /**
     * Evalúo el resultado cuando la actividad NuevaCiudad concluye.
     * @param requestCode   código de requerimiento con el que inició la actividad.
     *                      REQ_CODE_ADDCITY, indica que la zona es nueva.
     *                      REQ_CODE_EDITCITY, indica que la ciudad existe y debe editarse.
     * @param resultCode    código de resultado de la actividad.
     * @param data          datos devueltos por la actividad. Se distinguen dos casos:
     *                      1) Se añade la ciudad:
     *                          devuelve "NombreCiudad".
     *                      2) Se edita una ciudad existente:
     *                          devuelve "NombreCiudad" y "idCiudadElegida".
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == REQ_CODE_ADDCITY) && (resultCode == RESULT_OK)) {
            ciudad.addCiudad(data);
            Toast.makeText(this, "Se ha agregado la ciudad " +
                    data.getStringExtra("NombreCiudad") + " con exito.", Toast.LENGTH_LONG).show();

            arrayAdapter.notifyDataSetChanged();
        }
        if ((requestCode == REQ_CODE_EDITCITY) && (resultCode == RESULT_OK)) {
            ciudad.editCiudad(data);
            Toast.makeText(this, "Se ha editado la ciudad " +
                    data.getStringExtra("NombreCiudad") + " correctamente.", Toast.LENGTH_LONG).show();

            arrayAdapter.notifyDataSetChanged();
        }
        if ((requestCode == REQ_CODE_APPID) && (resultCode == RESULT_OK)) {
            ciudad.setAPPID(data);
            Toast.makeText(this, "Se ha cargado el APPID " +
                    data.getStringExtra("APPID") + " correctamente.", Toast.LENGTH_LONG).show();

            //arrayAdapter.notifyDataSetChanged();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     *  AsyncTask para obtener datos de la API de OpenWeather.
     *  Crea un diálogo de proceso mientras se accede al contenido del JSON.
     */
    public class GetJSONOpenWeather extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(MainActivity.this,"Obteniendo temperatura", "Por favor espere...");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground (String... urls) {
            InputStream inputStream;
            String result = ""; // Da un resultado distinto si se inicializa con null!

            try {
                inputStream = new URL(urls[0]).openStream();

                if(inputStream != null) {
                    BufferedReader buffer = new BufferedReader( new InputStreamReader(inputStream));
                    String line;

                    while( (line = buffer.readLine()) != null) {
                        result += line;
                    }
                    inputStream.close();
                }
                else {
                    //Toast.makeText(MainActivity.this, "Ocurrio un error al intentar obtener datos meteorologicos", Toast.LENGTH_SHORT).show();
                }
            }
            catch (MalformedURLException e) {
                Log.d("MalformedURL", e.getLocalizedMessage());
            }
            catch (IOException e) {
                Log.d("IOException", e.getLocalizedMessage());
                e.printStackTrace();
            }
            catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());

            }

            return result;
        }

        @Override
        protected void onPostExecute (String text) {
            dialog.dismiss(); //Es similar a dialog.cancel();

            try {
                JSONObject json = new JSONObject(text);

                //Pregunto si existe el nodo "main". Si existe, el JSON posee el nodo "temp".
                if(json.has("main")) {
                    JSONObject jsonMain = json.getJSONObject("main");
                    double tempC = jsonMain.getDouble("temp");

                    Toast.makeText(MainActivity.this, "Temperatura actual: " + String.valueOf(tempC)
                            + "ºC", Toast.LENGTH_SHORT).show();
                }
                else if(json.has("cod")) {  //Verifico si hay errores.
                    int errorCode = json.getInt("cod");
                    String message = json.getString("message");
                    Toast.makeText(MainActivity.this, "Error " + String.valueOf(errorCode)
                            + ": " + message, Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e) {
                Toast.makeText(MainActivity.this, "Datos de ciudad no encontrados", Toast.LENGTH_LONG).show();
                Log.d("JSONObjetc", e.getLocalizedMessage());
            }

        }
    }
}
