/**
 *      NuevaCiudad.java
 *  Actividad que funciona de interfaz para agregar y modificar ciudades a la aplicación.
 *
 *  Autor: Juan Cristian Miguel
 */
package com.example.jcmiguel.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class NuevaCiudad extends AppCompatActivity {

    private int modo;
    private int idCiudadElegida;

    private Integer ADD_CITY_MODE = 138;
    private Integer EDIT_CITY_MODE = 579;
    private Integer LOAD_APPID_MODE = 791;

    /**
     *  Método que se ejecuta al inicio de la actividad. Aquí se determina si la actividad
     *  funcionará en modo "editar" o "añadir", según el estado de la variable modoEditar. En
     *  modo de edición, se utiliza el campo "ciudadElegida" para establecer un valor por defecto
     *  al EditText y se le cambia la etiqueta al botón.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_ciudad);

        modo = getIntent().getExtras().getInt("modo");
        if(modo == EDIT_CITY_MODE) {
            EditText editText = findViewById(R.id.editCiudad);

            editText.setText(getIntent().getExtras().getString("ciudadElegida"));
            idCiudadElegida = getIntent().getExtras().getInt("idCiudadElegida");

            Button boton = findViewById(R.id.botonAgregarCiudad);
            boton.setText(R.string.botonEditarCiudad);
        }
        else if(modo == LOAD_APPID_MODE) {
            Button boton = findViewById(R.id.botonAgregarCiudad);
            boton.setText(R.string.botonCargarAPPID);

            TextView textView = findViewById(R.id.textView);
            textView.setText(R.string.msgCargarAPPID);

        }
    }


    /**
     *  Método que se ejecuta cuando se pulsa el botón "Agregar ciudad"/"Editar ciudad".
     *  Se devuelve a MainActivity el nombre de la ciudad a agregar. Si està el modo de edición
     *  habilitado, se envía además el id de la zona a modificar para actualizar la ListView y la
     *  base de preferencias.
     */
    public void botonAgregarCiudad (View view) {
        EditText editText = findViewById(R.id.editCiudad);

        String campoIngresado = editText.getText().toString();

        if(campoIngresado.length() != 0) {
            Intent datos = new Intent();
            if(modo == ADD_CITY_MODE) {
                datos.putExtra("NombreCiudad", campoIngresado);
            }
            else if(modo == EDIT_CITY_MODE) {
                datos.putExtra("NombreCiudad", campoIngresado);
                datos.putExtra("idCiudadElegida", idCiudadElegida);
            }
            else if(modo == LOAD_APPID_MODE) {
                datos.putExtra("APPID", campoIngresado);
            }

            setResult(RESULT_OK, datos);
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(), "Nombre de ciudad vacio", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     *  Método que permite volver a la MainActivity sin efectuar cambios.
     */
    public void botonVolver (View view) {
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }
}
