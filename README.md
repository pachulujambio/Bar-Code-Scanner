<center>

# Escaner Android

</center>

Aplicación móvil, desarrollada en Java mediante Android studio, en la cual creamos un escaner que lee códigos de tipo:

+ EAN, UPC
+ ITF
+ CODE39
+ CODE93
+ NW-7
+ CODE128
+ PDF417
+ QR
  
 y con el mismo realizamos una consulta a un webService determinado.

## Dependencias Necesarias
Dentro del archivo ```build.gradle```, el cual encontramos en ```Grandle Scripts``` es necesario incluir las dependencia de Volley Android, zxing y sincronizar la aplicación.

*Como implementar las dependencias*
```java 
implementation 'com.android.volley:volley:1.2.1'
implementation 'com.journeyapps:zxing-android-embedded:4.1.0'
```
La dependencia ```volley``` la usaremos para las consultas a realizar y la dependencia ```zxing``` es la encargada de importar las herramientas necesarias para utilizar el lector de código.

## Permitirle a la aplicación conectarse a internet
Para que la aplicación pueda realizar consultar mediante internet es necesario habilitarlo, esto lo hacemos desde el archivo ```AndroidManifest.xml```.

*Como permitir la conexión a internet:*
```java
<uses-permission android:name="android.permission.INTERNET" />
```

## Creación del layout
Para que la aplicación pueda funcionar debemos crear un ```Button``` y un ```TextView``` en nuestro ```activity_main.xml```. *El resto de los elementos utilizados son unicamente para su visualización, no cumplen una función dentro de la aplicación.*
El id de los elementos deberá ser:

<center> 

| Elemento             | ID    | 
|-------------------|-------------|
| Button   | btnScan    |
| TextView        | txtResult       |

</center>

Luego se debe relacionar el ```EditText``` con el ```Button```.

## Variables globales necesarias
Utilizaremos tres variables globales, dos correspondientes a los elementos creados en el layout y una para mostrar un resultado.

<center>

| Variable             | Tipo    | 
|-------------------|-------------|
| btnScan   | Button    |
| EditText        | txtResult       |
| String        | scanResult       |

</center>

*Creación de variables:*
```java
Button btnScan;//BOTON DEL SCANNER
EditText txtResult;//TEXTO PARA MOSTRAR RESULTADO DEL SCANNER
String scanResult;//GUARDAMOS EL RESULTADO DEL SCANNER
```

## Métodos utilizados
### Método ```protected void onCreate(Bundle savedInstanceState)``` 
Es el método donde ejecutas la lógica de arranque básica de la aplicación que debe ocurrir una sola vez en toda la vida de la actividad. Dentro del mismo le asignamos un valor a la variables previamente creadas y le indicamos a la aplicación que acciones realizar al presionar el botón creado.

```java
@RequiresApi(api = Build.VERSION_CODES.O)
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    //Variables Button y EditText
    btnScan = findViewById(R.id.btnScan);
    txtResult = findViewById(R.id.txtResult);

    //Creación del escaner al presionar el botón 
    btnScan.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
            //Indica que tipo de código lee el escaner
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.setPrompt("Lector - COP");
            //Indica la cámara a utilizar por la app
            integrator.setCameraId(0);
            integrator.setBeepEnabled(true);
            integrator.setBarcodeImageEnabled(true);
            integrator.initiateScan();
        }
    });
}        
```
### Funcion ```btnScan.setOnClickListener(new View.OnClickListener()```
Con esta función le indicamos a la aplicación que al presionar el botón debe crear el escaner con las propiedades que le pasemos a continuación. 

[Documentación de la clase IntentIntegratos](https://zxing.github.io/zxing/apidocs/com/google/zxing/integration/android/IntentIntegrator.html "Documentación")

### Método ```protected void onActivityResult(int requestCode, int resultCode, Intent data)```
Método que permite la lectura del código, utilizando ```IntentResult``` generamos un elemento producto de la función ```parseActivityResult``` de ```IntentIntegrator```, el cual pasamos por un ```IF``` para validar que no este vacío y podamos ejecutar la aplicación sin problemas. En caso  que el resultado no sea *null* la aplicación guarda el mismo en una variable (```scanResult```) y lo muestra en un ```Toast```. 

[Documentación  IntentResult](https://zxing.github.io/zxing/apidocs/com/google/zxing/integration/android/IntentResult.html "IntentResult")

[Documentación Toast](https://developer.android.com/guide/topics/ui/notifiers/toasts?hl=es-419 "Documentación Toast")


``` java
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
```   
## Realizar la consulta al web service con volley

## Metodo ```getData(String scanResult)```:

 El siguiente metodo recibe el resultado del codigo escaneado con el que vamos a realizar la consulta al web service mediante la libreria "Volley" de Andorid Studio.

 En primer lugar vamos a instanciar tanto las credenciales del usuario como la url que vamos a consultar, la cual armamos con el resultado del escaner.



```java

private void getData(String scanResult){
    String user = "User";
    String pass = "Pass";
    String url = "https://test + scanResult ;
}

```
 En este caso el web service necesitara una autorizacion, para esto se pondra el header "Authorization", en donde las credenciales se codifican en Base64, en la llamada HTTP.
```java

String authString = Base64.getEncoder()

    .encodeToString(String.format("%s:%s", user,pass)

        .getBytes());

```

## StringRequest

 La clase StringRequest de Volley especifica el metodo que vamos a realizar, la URL que vamos a consultar y recibe un string sin procesar como respuesta.

 Para procesar la respuesta, Volley nos brinda dos interfaces, la interfaz `Response.Listener` la cual nos devuelve un metodo para ejecutar en caso de una respuesta correcta ( `onResponse(String response)` ), y  la interfaz `Response.ErrorListener` que nos devuelve un metodo para ejecutar en caso de una respuesta incorrecta.( `onErrorResponse(VolleyError error)` ):

```java

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
})

```

 En este caso como necesitamos la autorizacion crearemos el metodo `getHeaders()` el cual va a armar el header Authorization con las credenciales codificadas en Base64, las cuales codificamos anteriormente: 

```java
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
```

 Por ultimo para poder enviar la peticion travez del metodo `newRequestQueue` indicaremos el contexto y a travez del metodo `add` la peticion que creamos anteriormente

```java
Volley.newRequestQueue(this).add(postRequest);
```

## Mantener escaner en posición portrair al ejecutarlo (pantalla en vertical)
Por defecto el escaner al abrirse lo hace en landscape (pantalla en horizontal), si deseamos evitar que el usuario tenga que rotar el teléfono al utilizar el mismo debemos realizar algunas configuraciones que veremos a continuación.

### Creación de la clase CaptureActivityPortrait
Clase que extiende de `CaptureActivity` y vamos a utilizar para setear las configuraciones en el método que se ejecute el escaner. 

La clase quedaría así:

```java
import com.journeyapps.barcodescanner.CaptureActivity;

public class CaptureActivityPortrait extends CaptureActivity {
}
```
*Se le puede asignar el nombre que uno desee.*

### Setear los atributos necesarios
Dentro del `IntentIntegrator` que creamos para configurar el escaner es necesario añadir dos líneas de código.

```java
integrator.setCaptureActivity(CaptureActivityPortrait.class);
integrator.setBarcodeImageEnabled(true);
```

### Cambiar las configuraciones del AndroidManifest.xml
Dentro del archivo referido tenemos que agregar una actividad que contiene las configuraciones necesarias para que la aplicación deje por defecto el escaner en posición vertical.
```java
<activity android:name=".CaptureActivityPortrait"
    android:screenOrientation="portrait"
    android:stateNotNeeded="true"
    android:theme="@style/zxing_CaptureTheme"
    android:windowSoftInputMode="stateAlwaysHidden">
</activity>
```




