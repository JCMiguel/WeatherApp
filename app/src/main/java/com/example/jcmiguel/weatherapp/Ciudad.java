/**
 *      Ciudad.java
 *  Clase diseñada para manipular la base de preferencias y la ArrayList vinculada a la ListView de
 *  la MainActivity.
 *
 *  Autor: Juan Cristian Miguel
 */
package com.example.jcmiguel.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

public class Ciudad {
    private Context context;
    private int cantCiudades;
    private ArrayList<String> ciudades;
    private SharedPreferences preferences;

    /**
     *  Constructor de la clase. Configura la base de preferencias y el ArrayList con los datos
     *  persistidos. Este ArrayList será el que se le pase a la ListView de MainActivity para
     *  mostrar las ciudades ingresadas.
     */
    public Ciudad(Context context) {
        this.context = context;
        ciudades = new ArrayList<String>();
        preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        cantCiudades = preferences.getInt("CantCiudades", 0);
        for (int i = 0; i<cantCiudades; i++)
        {
            ciudades.add(preferences.getString("Ciudad" + String.valueOf(i), "") );
        }
        //APPID = preferences.getString("APPID","");
    }


    /**
     *  Establece el APPID para acceder a los servicios de la API de OpenWeather.
     */
    public void setAPPID (Intent data) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("APPID",data.getStringExtra("APPID"));
        editor.commit();
    }


    /**
     *  Devuelve el APPID para acceder a los servicios de la API de OpenWeather.
     */
    public String getAPPID () {
        return preferences.getString("APPID", "");
    }


    /**
     *  Añade una ciudad al ArrayList y la persiste en las preferencias.
     */
    public void addCiudad (Intent data) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Ciudad" + String.valueOf(cantCiudades), data.getStringExtra("NombreCiudad"));
        cantCiudades += 1;
        editor.putInt("CantCiudades", cantCiudades);
        editor.commit();

        ciudades.add(data.getStringExtra("NombreCiudad"));
    }


    /**
     *  Edita una ciudad existente. Aplica los cambios en el ArrayList y las preferencias.
     */
    public void editCiudad (Intent data) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Ciudad" + data.getExtras().getInt("idCiudadElegida"), data.getStringExtra("NombreCiudad"));
        editor.commit();

        ciudades.set(data.getExtras().getInt("idCiudadElegida"), data.getStringExtra("NombreCiudad"));
    }


    /**
     *  Elimina una ciudad existente. El cambio se asienta en el ArrayList y las preferencias.
     */
    public void removeCiudad (int id) {
        SharedPreferences.Editor editor = preferences.edit();

        ciudades.remove(id);
        editor.remove("Ciudad" + String.valueOf(id));
        cantCiudades -= 1;
        editor.putInt("CantCiudades", cantCiudades);

        for (int i = 0; i<cantCiudades; i++)
        {
            editor.putString("Ciudad" + String.valueOf(i), ciudades.get(i));
        }
        editor.commit();

    }


    /**
     *  Devuelve la lista completa de ciudades ingresadas. Se utiliza principalmente para asignar
     *  el ArrayList en el ArrayAdapter de la ListView en MainActivity
     */
    public ArrayList<String> getArrayListCiudades () {
        return ciudades;
    }


    /**
     *  Devuelve la cantidad de ciudades registradas hasta el momento.
     */
    public Integer getCantCiudades () {
        return cantCiudades;
    }


    /**
     *  Devuelve el nombre de una ciudad, utilizand
     */
    public String getCiudadPorID (int id) {
        return preferences.getString("Ciudad" + String.valueOf(id), "");
    }

}
