package com.example.sebasmaddox.cardiocheck;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HorasActivity extends AppCompatActivity {

    EditText edt1,edt2,edt3,edt4,edt5;
    Button btn1,btn2;
    Spinner spinner;
    ListView lista;
    private String url = "http://192.168.0.100:81/android/ultimopulso.php";

    private String url2 = "http://192.168.0.100:81/android/ultimopulso.php";
    ArrayAdapter<String> adaptador1,adaptador2;

    ArrayList<String> alumnos, fecha;
    private JSONArray users;
    JSONArray vectorJSON;
    String img,path, ctr, carr, nom;
    int i =1;
    TextView ultimo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horas);

        lista = findViewById(R.id.listHora);
        ultimo = findViewById(R.id.txtActualHora);
        adaptador1= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        //adaptador1.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);
        lista.setAdapter(adaptador1);
        adaptador2= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2);
        //adaptador2.setDropDownViewResource(android.R.layout.simple_expandable_list_item_2);
        lista.setAdapter(adaptador2);



        //Listener();
        //Initializing the ArrayList
        alumnos= new ArrayList<String>();
        fecha = new ArrayList<String>();
        sendAndRequestResponse();
        //TraerID();

    }

    private void sendAndRequestResponse(){
        RequestQueue mRequestQueue;
        StringRequest mStringRequest;
        mRequestQueue = Volley.newRequestQueue(HorasActivity.this);

        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    vectorJSON = jsonObject.getJSONArray("datos");

                    ultimo.setText(vectorJSON.getJSONObject(0).getString("pulso").toString()+"BPM");

                    for (int i = 0; i < vectorJSON.length(); i++) {
                        try {
                            //JSONObject jsonObject = worldpopulation.getJSONObject(i);
                            //vectorJSON = jsonObject.getJSONArray("datos");
                            fecha.add(vectorJSON.getJSONObject(i).getString("fecha"));
                            alumnos.add(vectorJSON.getJSONObject(i).getString("pulso").toString());





                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    lista.setAdapter(new ArrayAdapter<String>(HorasActivity.this, android.R.layout.simple_list_item_1, alumnos));
                    //lista.setAdapter(new ArrayAdapter<String>(HorasActivity.this, android.R.layout.simple_list_item_2, fecha));
                    Toast.makeText(HorasActivity.this, "Conexion exitosa", Toast.LENGTH_LONG).show();

                    // uri =

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TAG","Error: "+error.toString());
                Toast.makeText(HorasActivity.this, "Error ultimo", Toast.LENGTH_LONG).show();
                //sendAndRequestResponse();
            }
        });
        mRequestQueue.add(mStringRequest);
    }
    private void TraerID() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, datosUsuario.URL2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject json = null;
                        try {

                            json = new JSONObject(response);


                            users = json.getJSONArray(datosUsuario.JSON_ARRAY);


                            getID(users);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HorasActivity.this, "todo Fallo", Toast.LENGTH_LONG).show();
                        TraerID();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getID(JSONArray worldpopulation) {
        for (int i = 0; i < worldpopulation.length(); i++) {
            try {
                JSONObject jsonObject = worldpopulation.getJSONObject(i);
                alumnos.add(jsonObject.getString(datosUsuario.TAG_IDpulso));
                fecha.add(jsonObject.getString(datosUsuario.TAG_IDfecha));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        lista.setAdapter(new ArrayAdapter<String>(HorasActivity.this, android.R.layout.simple_list_item_1, alumnos));
        lista.setAdapter(new ArrayAdapter<String>(HorasActivity.this, android.R.layout.simple_list_item_2, fecha));
    }



}
