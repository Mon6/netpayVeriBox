package msm.aie.veribox;

/**
 * Created by aie on 28/02/18.
 */

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Preset extends Activity{

    private TextView textView1;
    int espera=0, MSJ=10, paso=0;
    private EditText myTextBox;
    String capturo, Enviando, pos, tarjeta, dispoS;
    SQLiteDatabase bd;
    private final String NAMESPACE = "urn:veriboxwsdl";
    private String URL;// = "http://192.168.1.38/Veribox/Veribox.php";
    private final String SOAPACTION = "urn:veriboxwsdl#veribox";
    private final String METHOD = "veribox";

    //Creamos el handler puente para mostrar
    //el mensaje recibido de peticiones
    private Handler puente = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (((String)msg.obj).equals("")){
                //t_busca=10000;
                //statusEditText.setText("Problemas COM");
                //pendientes();
            }
            else {
                String muestra = (String)msg.obj;
                //statusEditText.setText("Respondio: "+muestra);
                mensajes(muestra,"","");
                //proc_pend(muestra);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.preset);

        textView1 = (TextView)findViewById(R.id.textView1);

        myTextBox = (EditText) findViewById(R.id.editText1);

        //Lineas para ocultar el teclado virtual (Hide keyboard) DESACTIVA TECLADO
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(myTextBox.getWindowToken(), 0);
        myTextBox.setInputType(InputType.TYPE_NULL);

        //recive datos del accion anterior
        Bundle bundle=getIntent().getExtras();
        tarjeta = bundle.getString("cap");
        String Pres_tipo = bundle.getString("tipo");

        //String p1 = "Us:" + cap.substring(3,4) + cap.substring(8,12)+" Es:" + cap.substring(2,3) + cap.substring(4,8);

        if (Pres_tipo.equals("VH")){
            textView1.setText("Espera Posicion...");
            espera=1;

        }

        paso = 1;
        Leedb();
        String nip = "----";
        MSJ=0;
        mensajes(tarjeta, nip,"b");
        //mensajes(String 					cadena1, String cadena2, String cadena3){
        //String DatosTransa = ">"+"1"+">"+	tarjeta+"	>"+	nip+"		>"+"	f"+">";

        //Revisara que se introduce desde el LECTOR DE CODIGO DE BARRAS
        myTextBox.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                //myOutputBox = (TextView) findViewById(R.id.myOutputBox);
                int con = s.length();
                String num = Integer.toString(con);
                //myOutputBox.setText(num);
                //textView1.setText(num);
                if (con==5){
                    capturo = s.toString();
                    pos = capturo.substring(0,1);
                    if (pos.equals("3")){
                        pos = capturo.substring(1,3);
                        //myTextBox.setText("");
                        textView1.setText(" Preset Pos: " + pos);
                        myTextBox.setText("");//Borra Captura
                        preset(capturo);
                    }
                    //espera_cap();
                }
                if (con==7){
                    capturo = s.toString();
                    String p1 = capturo.substring(0,1);
                    if (p1.equals("4")||p1.equals("5")){
                        //myTextBox.setText("");
                        textView1.setText("Precarga " + p1);
                        myTextBox.setText("");//Borra Captura
                        //encuentra_preset(capturo);
                    }
                }
                if (con==12){
                    capturo = s.toString();
                    String p1 = capturo.substring(0,1);
                    if (p1.equals("9")){
                        //myTextBox.setText("");
                        textView1.setText("Vehículo " + p1);
                        myTextBox.setText("");//Borra Captura
                        //encuentra_vehi(capturo);
                    }
                    //espera_cap();
                }
                if (con>12){
                    myTextBox.setText("");//Borra Captura
                }
                //else  myTextBox.setText("");
            }
        });
        //FIN - Revisara que se introduce desde el LECTOR DE CODIGO DE BARRAS

    }


    public void Leedb(){
        //Lee DB de la aplicacion
//---------------------------------------------------------------------
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "tablet", null, 1);
        bd = admin.getWritableDatabase();
        //*****************************************************************************
        Cursor fila = bd.rawQuery("select * from config where num=1"
                + "", null);
        if (fila.moveToFirst()) {
            URL=fila.getString(5);
            if (URL.length()>11){
                URL = "http://" + URL + "/Veribox/Veribox.php";
                int num_tabled=fila.getInt(2);
                String mac=fila.getString(3);
                String version=fila.getString(4);
                int imp_ext=fila.getInt(6);
                String mac_serial=fila.getString(7);
                String nomad=fila.getString(8);
                int intentos=1;
                Enviando=">"+num_tabled+">"+mac+">"+version+">"+mac_serial+">"+nomad+">"+intentos+">";
            }else{
                Toast.makeText(this, "NO se tiene servidor CONFIGURADO",
                        Toast.LENGTH_SHORT).show();
            }
        } else{
            Toast.makeText(this, "NO lee DB",
                    Toast.LENGTH_SHORT).show();
        }
        //*****************************************************************************
    }

    public void preset(String cadena){
        MSJ=0;
        mensajes(dispoS, "2" , tarjeta);
        //mensajes(String 					cadena1, String cadena2, String cadena3){
        //String DatosTransa = ">"+"2"+">"+preset_num+"	>"+tipo_venta+"	>"+tarjeta+">";
    }

    public void mensajes(String cadena1, String cadena2, String cadena3){
        //Envia mensajes a servidor secuencialmente
        if (MSJ==3){
            if (paso == 1){
                paso=2;
                //Despuesta Correcta de mesaje M2
                //Dividir una cadena en partes por |
                String[] partes = cadena1.split("\\>");
                String sol = partes[0];
                dispoS = partes[1];
                //precio_uni = partes[2];
                //ult_odo = partes[3];
                //pide_odo = "0";
                //dispo= Float.parseFloat(dispoS);
                //monto_venta=dispoS;
                //statusEditText.setText(dispoS);
            }else{
                finish();
            }

        }
        //Mensaje DATOS-Tranzaccion esperamos la respuesta con los datos
        if (MSJ==2){
            MSJ=3;
            String Datos = "";
            String DatosTransa = "";
            envia("M2",Datos,DatosTransa);
        }
        //Mensaje DATOS-Datos a procesar
        if (MSJ==1){
            MSJ=2;
            String Datos = "";
            String DatosTransa = "";
            envia("M1",Datos,DatosTransa);
        }
        //Mensaje INICIAL-Quien solicita la infomacion
        if (MSJ==0){
            MSJ=1;
            String Datos = ">"+"PR"+">"+pos+">";
            String DatosTransa = ">"+paso+">"+cadena1+">"+cadena2+">"+cadena3+">";
            //String DatosTransa = ">"+"1"+">"+tarjeta+"	>"+nip+"		>"+"f"+">";
            //String DatosTransa = ">"+"2"+">"+preset_num+"	>"+tipo_venta+"	>"+tarjeta+">";
            envia("M0",Datos,DatosTransa);
        }
    }

    public void envia(final String M, final String Datos, final String DatosTransa )
    {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String respuesta = "";
                SoapObject request = new SoapObject(NAMESPACE, METHOD);
                request.addProperty( "d0" , M);
                request.addProperty( "d1" , Enviando);
                request.addProperty( "d2" , Datos);
                request.addProperty( "d3", DatosTransa);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                try
                {
                    androidHttpTransport.call(SOAPACTION, envelope);
                    respuesta = envelope.getResponse().toString();
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
                Message sms = new Message();
                sms.obj = respuesta;
                puente.sendMessage(sms);
            }
        }).start();
    }

}