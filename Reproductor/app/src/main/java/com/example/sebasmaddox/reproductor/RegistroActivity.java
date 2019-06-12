package com.example.sebasmaddox.reproductor;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;

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

public class RegistroActivity extends AppCompatActivity {
    Button btnSave, btnBuscar;
    ImageView imgNueva;
    EditText cajaNom, cajaArti, cajaId;
    Spinner spinnerAlbum;
    TextView txtid;

    private String url = "http://192.168.1.117:81/android/rep/new.php";
    private String url2 = "http://192.168.1.117:81/android/rep/selectone.php";
    ArrayAdapter<String> adaptador;

    private ArrayList<String> cancion;
    private ArrayList<String> albums;
    private JSONArray users;
    JSONArray vectorJSON;
    Bitmap bitmap;
    private static final int PICK_IMAGE = 100;
    private static final int PICK_AUDIO = 101;
    String nom,art,path1,path2,album,id1;
    int i =1;
    MediaPlayer mp = new MediaPlayer();
    String login_name;
    int triger = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        btnSave = findViewById(R.id.btnGuardar);
        imgNueva = findViewById(R.id.imageView);
        cajaNom = findViewById(R.id.cajaNombre);
        cajaArti = findViewById(R.id.cajaArtista);
        spinnerAlbum = findViewById(R.id.spinner);
        btnBuscar = findViewById(R.id.btnBuscar);
        cajaId = findViewById(R.id.cajaId);
        txtid = findViewById(R.id.txtId);

        cancion= new ArrayList<String>();
        albums = new ArrayList<String>();
        cajaId.setVisibility(View.INVISIBLE);
        txtid.setVisibility(View.INVISIBLE);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nom = cajaNom.getText().toString();
                art = cajaArti.getText().toString();
                album = spinnerAlbum.getSelectedItem().toString();

                if (nom.equals(""))
                {
                    Toast toast1 = Toast.makeText(getApplicationContext(), "Falta nombre", Toast.LENGTH_SHORT);
                    toast1.show();
                }
                else
                {
                    if (art.equals(""))
                    {
                        Toast toast1 = Toast.makeText(getApplicationContext(), "Falta Artista", Toast.LENGTH_SHORT);
                        toast1.show();
                    }
                    else
                    {
                        if (path1 == null)
                        {
                            Toast toast1 = Toast.makeText(getApplicationContext(), "Falta Audio", Toast.LENGTH_SHORT);
                            toast1.show();
                        }
                        else
                        {
                            if(path2 == null)
                            {
                                Toast toast1 = Toast.makeText(getApplicationContext(), "Falta Audio", Toast.LENGTH_SHORT);
                                toast1.show();
                            }
                            else
                            {
                                enviar();
                                cajaNom.setText("");
                                cajaArti.setText("");
                                imgNueva.setImageResource(R.drawable.imgview);
                                mp.stop();
                            }
                        }
                    }
                }
            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (triger==0)
                {
                    triger=1;
                    cajaId.setVisibility(View.VISIBLE);
                    txtid.setVisibility(View.VISIBLE);
                }
                else
                {
                    id1 = cajaId.getText().toString();

                    sendAndRequestResponse();
                }

            }
        });

        imgNueva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                audio();
                img();

            }
        });
    }

    private void enviar(){
        RequestQueue mRequestQueue;
        StringRequest mStringRequest;
        mRequestQueue = Volley.newRequestQueue(RegistroActivity.this);

        mStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
                params.put("nom", nom);
                params.put("art", art);
                params.put("alb", album);
                params.put("img", path2);
                params.put("aud", path1);
                return params;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private  void audio()
    {
        Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galeria, PICK_AUDIO);
    }

    private  void img()
    {
        Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galeria, PICK_IMAGE);
    }

    private void sendAndRequestResponse(){
        RequestQueue mRequestQueue;
        StringRequest mStringRequest;
        mRequestQueue = Volley.newRequestQueue(RegistroActivity.this);

        mStringRequest = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    vectorJSON = jsonObject.getJSONArray("datos");



                    cajaNom.setText(vectorJSON.getJSONObject(0).getString("nom").toString());
                    cajaArti.setText(vectorJSON.getJSONObject(0).getString("art").toString());
                    //spinnerAlbum.setItem(vectorJSON.getJSONObject(0).getString("alb").toString());
                    busqueda1(vectorJSON.getJSONObject(0).getString("img").toString());
                    busqueda2(vectorJSON.getJSONObject(0).getString("aud").toString());


                    Toast.makeText(RegistroActivity.this, "Conexion exitosa", Toast.LENGTH_LONG).show();

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TAG","Error: "+error.toString());
                Toast.makeText(RegistroActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                sendAndRequestResponse();
            }
        }) {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("Id", id1 );
                return map;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private void busqueda1(String d)
    {

        bitmap = BitmapFactory.decodeFile(d);
        imgNueva.setImageBitmap(bitmap);
    }

    private void busqueda2(String d)
    {
        try {

            if (mp.isPlaying())
            {
                mp.reset();
            }
            //path1 = lista3.get(position);
            mp.setDataSource(d);
            //mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.prepare(); //salta exception
            mp.seekTo(0);                             //seek to starting of song means time=0 ms
            mp.start();                               //start media player
        } catch (Exception e) {
            Toast.makeText(getApplication(), "error exception", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode ==RESULT_OK){
            switch (requestCode)
            {
                case PICK_AUDIO:

                    Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                    cursor.moveToFirst();
                    path1 = cursor.getString(1);
                    cursor.close();

                    try {

                        if (mp.isPlaying())
                        {
                            mp.reset();
                        }
                        //path1 = lista3.get(position);
                        mp.setDataSource(path1);
                        //mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mp.prepare(); //salta exception
                        mp.seekTo(0);                             //seek to starting of song means time=0 ms
                        mp.start();                               //start media player
                    } catch (Exception e) {
                        Toast.makeText(getApplication(), "error exception", Toast.LENGTH_SHORT).show();
                    }

                    break;

                case PICK_IMAGE:
                    Cursor c = getContentResolver().query(data.getData(), null, null, null, null);
                    c.moveToFirst();
                    path2 = c.getString(1);
                    c.close();
                    bitmap = redimensionarImagen(path2);
                    imgNueva.setImageBitmap(bitmap);

                    break;
                default:
                    path1 = null;
                    path2 = null;
            }
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        mp.stop();
    }

    public static Bitmap redimensionarImagen(String absolutePath){

        BitmapFactory.Options options = new BitmapFactory.Options();//se crea el bitmap
        options.inJustDecodeBounds = true;//Carga la imagen pero no en memoria
        BitmapFactory.decodeFile(absolutePath, options);//

        int imageWidth = options.outWidth;
        int imageHeigth = options.outHeight;

        int ratio;
        options.inJustDecodeBounds = false;//carga la imagen en memoria

        if(imageWidth > imageHeigth)//redimencion la imagen
            ratio = imageWidth / 250;
        else
            ratio = imageHeigth / 250;

        options.inSampleSize = ratio;//asigna el tamaño a la imagen

        return BitmapFactory.decodeFile(absolutePath, options);
    }
}
