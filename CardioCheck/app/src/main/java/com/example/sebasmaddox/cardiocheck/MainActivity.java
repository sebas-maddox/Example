    package com.example.sebasmaddox.cardiocheck;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

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

    public class MainActivity extends AppCompatActivity {
    public static final String LOGIN_URL = "http://192.168.0.100:81/android/login.php";
    public static final String KEY_USERNAME="username";
    public static final String KEY_PASSWORD="password";


    private EditText editTextUsername;
    private EditText editTextPassword;
    Button buttonLogin;
    String Acces = "Concedido";
    private Button buttonRegister;

        /*private RequestQueue mRequestQueue;
        private StringRequest mStringRequest;*/

    String cel;
    public  int cel2;
    String password;
    private ArrayList<String> alumnos;
        private JSONArray users;
        JSONArray vectorJSON, vectorJSON2;
        String con, usu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextUsername  = (EditText) findViewById(R.id.usuario);
        editTextPassword  = (EditText) findViewById(R.id.contra);
        buttonLogin  = (Button) findViewById(R.id.Entrar);
        alumnos= new ArrayList<String>();



        SharedPreferences preferences = getSharedPreferences("temp", getApplicationContext().MODE_PRIVATE);
        String login_name = preferences.getString("username", "");
        //String login_organizacion = preferences.getString("organizacion","");
        if(login_name != ""){
            Intent i = new Intent(this, MenuActivity.class);
            //datosUsuario.setNombre_usuario(login_name);
            //datosUsuario.setOrganizacion(login_organizacion);
            startActivity(i);
            finish();
        }

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TraerID();
                sendAndRequestResponse();
                SharedPreferences preferences = getSharedPreferences("temp", getApplicationContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("username", editTextUsername.getText().toString());
                //editor.putString("organizacion", datosUsuario.getOrganizacion());
                editor.commit();
            }
        });
    }


        private void sendAndRequestResponse(){
            RequestQueue mRequestQueue;
            StringRequest mStringRequest;
            mRequestQueue = Volley.newRequestQueue(MainActivity.this);

            mStringRequest = new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        vectorJSON = jsonObject.getJSONArray("datos");

                        String Acceso = new String(vectorJSON.getJSONObject(0).getString("Acceso").toString());

                        if (Acces.equals(Acceso)){
                            Intent log = new Intent (MainActivity.this,MenuActivity.class);

                            startActivity(log);
                        }else {
                            Toast.makeText(MainActivity.this,"Datos Incorrectos",Toast.LENGTH_LONG);
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("TAG","Error: "+error.toString());
                    Toast toast = Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG);
                    toast.show();
                }
            }) {

                @Override
                protected HashMap<String, String> getParams() {
                    HashMap<String, String> map = new HashMap<>();

                    map.put("IdPaciente", editTextUsername.getText().toString());
                    map.put("Contra", editTextPassword.getText().toString());
                    return map;
                }
            };
            mRequestQueue.add(mStringRequest);
        }


}
