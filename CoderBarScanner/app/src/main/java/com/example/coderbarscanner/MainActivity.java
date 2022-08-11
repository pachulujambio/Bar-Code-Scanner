package com.example.coderbarscanner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button btnScan;//BOTON DEL SCANNER
    EditText txtResult;//TEXTO PARA MOSTRAR RESULTADO DEL SCANNER
    String scanResult;//GUARDAMOS EL RESULTADO DEL SCANNER

    /**
     * Crea el escaner
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //VARIABLES DEL SCANNER
        btnScan = findViewById(R.id.btnScan);
        txtResult = findViewById(R.id.txtResult);
        //SCANNER
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                //Indica que tipo de código lee el escaner
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Lector - COP");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
            }
        });
    }

    /**
     * Lectura y resultado del escaner
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Lectura cancelada", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                txtResult.setText(result.getContents());
                scanResult = result.getContents();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

        getData(scanResult);
    };

    /**
     * Consulta a la API y retorno de la misma al textView
     * @param scanResult
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getData(String scanResult){
        //Credenciales de acceso
        String user = "USER-TEST";
        String pass = "PASS-TEST";
        //Api connection
        String url = "https://URL-TEST.com/barcode_info?barcode=" + scanResult ;
        //Autorizamos
        String authString = Base64.getEncoder().encodeToString(
                String.format("%s:%s", user,pass)
                        .getBytes()
        );
        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                txtResult.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                txtResult.setText("Error de autorizacion");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Basic " + authString);
                return headers;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
    }
}