package com.example.sebasmaddox.cardiocheck;

import android.content.SharedPreferences;
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
import android.widget.Spinner;
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

public class ActualizarActivity extends AppCompatActivity {
    EditText edt1,edt2,edt3,edt4,edt5, edt6, edt7,edt8;
    Button btn1;
    ImageView imagen;
    /*private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;*/
    private String url = "http://192.168.0.100:81/android/paciente.php";

    private String url2 = "http://192.168.0.100:81/android/newpac.php";
    ArrayAdapter<String> adaptador;

    private ArrayList<String> alumnos;
    private JSONArray users;
    JSONArray vectorJSON;
    Bitmap bitmap;
    private static final int PICK_IMAGE = 100;
    String con,nom,ap,am,tel1,tel2,corr, idPac;
    int i =1;
    String login_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar);

        edt1= findViewById(R.id.usuario);
        edt2= findViewById(R.id.contra);
        edt3 = findViewById(R.id.nombre);
        edt4 = findViewById(R.id.app);
        edt5 = findViewById(R.id.apm);
        edt6 = findViewById(R.id.telefono);
        edt7 = findViewById(R.id.telefono2);
        edt8 = findViewById(R.id.correo);

        btn1 = findViewById(R.id.guardar);
        TraerID();
        sendAndRequestResponse();
        SharedPreferences preferences = getSharedPreferences("temp", getApplicationContext().MODE_PRIVATE);
        login_name = preferences.getString("username", "");

        //Initializing the ArrayList
        alumnos= new ArrayList<String>();

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idPac = edt1.getText().toString();
                con = edt2.getText().toString();
                nom = edt3.getText().toString();
                ap = edt4.getText().toString();
                am = edt5.getText().toString();
                tel1 = edt6.getText().toString();
                tel2 = edt7.getText().toString();
                corr = edt8.getText().toString();
                enviar();
                TraerID();
            }
        });


    }

    private void sendAndRequestResponse(){
        RequestQueue mRequestQueue;
        StringRequest mStringRequest;
        mRequestQueue = Volley.newRequestQueue(ActualizarActivity.this);

        mStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    vectorJSON = jsonObject.getJSONArray("datos");



                    edt1.setText(vectorJSON.getJSONObject(0).getString("id").toString());
                    edt2.setText(vectorJSON.getJSONObject(0).getString("contra").toString());
                    edt3.setText(vectorJSON.getJSONObject(0).getString("nom").toString());
                    edt4.setText(vectorJSON.getJSONObject(0).getString("ap").toString());
                    edt5.setText(vectorJSON.getJSONObject(0).getString("am").toString());
                    edt6.setText(vectorJSON.getJSONObject(0).getString("tel1").toString());
                    edt7.setText(vectorJSON.getJSONObject(0).getString("tel2").toString());
                    edt8.setText(vectorJSON.getJSONObject(0).getString("corr").toString());
                    Toast.makeText(ActualizarActivity.this, "Conexion exitosa", Toast.LENGTH_LONG).show();

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TAG","Error: "+error.toString());
                Toast.makeText(ActualizarActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                sendAndRequestResponse();
            }
        }) {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("IdPaciente", login_name );
                return map;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private void TraerID() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, datosUsuario.URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject json = null;
                        try {
                            json = new JSONObject(response);
                            users = json.getJSONArray(datosUsuario.JSON_ARRAY);
                            getID(users);

                            Toast.makeText(ActualizarActivity.this, "Conexion exitosa", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(ActualizarActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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
                alumnos.add(jsonObject.getString(datosUsuario.TAG_ID));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //spinner.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, alumnos));
    }

    private void enviar(){
        RequestQueue mRequestQueue;
         StringRequest mStringRequest;
        mRequestQueue = Volley.newRequestQueue(ActualizarActivity.this);

        mStringRequest = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    vectorJSON = jsonObject.getJSONArray("datos");


                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TAG","Error: "+error.toString());
                enviar();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("id", idPac);
                params.put("IdPaciente", login_name );
                params.put("contra", con);
                params.put("nom", nom);
                params.put("ap", ap);
                params.put("am", am);
                params.put("tel1", tel1);
                params.put("tel2", tel2);
                params.put("corr", corr);

                //contra,nom,ap,am,tel1,tel2,corr;


                return params;

            }
        };
        mRequestQueue.add(mStringRequest);
    }
}
