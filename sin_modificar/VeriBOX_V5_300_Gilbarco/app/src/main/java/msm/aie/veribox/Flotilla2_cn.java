package msm.aie.veribox;

/**
 * Created by Miquetl on 14/06/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;

public class Flotilla2_cn extends Activity{

    private final String NAMESPACE = "urn:veriboxwsdl";
    //private final String URL = "http://www.sigma-aie.com.mx/veribox/Veribox.php";
    private String URL = "";//"http://192.168.1.38/Veribox/Veribox.php";
    private final String SOAPACTION = "urn:veriboxwsdl#veribox";
    private final String METHOD = "veribox";
    private Coor_xy cox_coy;
    private TextView textView_xy;
    private EditText editText18 ,editText19;
    StringBuilder stringBuilder = new StringBuilder();
    private String nvnip1, nvnip2, flotilla, tarjeta, user_sol;
    SQLiteDatabase bd;
    String Enviando;

    //Creamos el puente para obtener
    //el mensaje recibido de peticiones
    private Handler puente = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (((String)msg.obj).equals("")){
                msj("Problemas COM.");

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        finish();
                    }
                }, 2000);
            }
            else {
                String muestra = (String)msg.obj;
                res_ser(muestra);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.flotilla2_cn);

        //Oculta teclado virtual
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Recupera datos enciados
        Bundle bundle=getIntent().getExtras();
        flotilla = bundle.getString("flotilla");
        tarjeta = bundle.getString("tarjeta");
        user_sol = bundle.getString("user_sol");


        //Inicia los componentes
        editText18 = (EditText)findViewById(R.id.editText18);
        editText19 = (EditText)findViewById(R.id.editText19);

        Leedb();

        /*
        //Inicia captura de Coordenadas XY
        //Coor_xy
        cox_coy = new Coor_xy();
        this.textView_xy = (TextView) findViewById( R.id.strXY );

        //Evento Touch
        this.textView_xy.setOnTouchListener( new View.OnTouchListener()
        {
            @Override
            public boolean onTouch( View arg0, MotionEvent arg1 ) {

                stringBuilder.setLength(0);
                //si la accion que se recibe es de levantar
                if( arg1.getAction() == MotionEvent.ACTION_UP )
                {
                    float co_X = arg1.getX();
                    float co_Y = arg1.getY();
                    String rev=cox_coy.co_xy(co_X, co_Y);
                    try {
                        revisar (rev);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                //Se muestra en pantalla
                //textView.setText( stringBuilder.toString() );
                return true;
            }
        });
        //FIN Cptura de Coordenadas XY
        */
    }


    //Funcion Acciones
    public void revisar (String rev_coor) throws IOException {

        //Boton a Continuar.
        if (rev_coor.equals("C2") || rev_coor.equals("C3") || rev_coor.equals("C4") || rev_coor.equals("D2") || rev_coor.equals("D3") || rev_coor.equals("D4")) {
            continua(null);
        }

        //Boton cancelar
        if (rev_coor.equals("C5") || rev_coor.equals("C6") || rev_coor.equals("C7") || rev_coor.equals("D5") || rev_coor.equals("D6") || rev_coor.equals("D7")) {
            atras(null);
        }
    }

    public void atras(View view){
        finish();
    }

    public void continua(View view){
        nvnip1 = editText18.getText().toString();
        nvnip2 = editText19.getText().toString();
        if (nvnip1.length()>3 && nvnip2.length()>3){
            if (nvnip1.equals(nvnip2)){
                //Toast.makeText(this, "enviaXml",Toast.LENGTH_SHORT).show();
                enviaXml();
            }else{
                msj("Digitos NO coinciden");
                editText18.setText("");
                editText19.setText("");
            }
        }else{
            msj("Por lo menos 4 digitos\nen ambos campos");
            editText18.setText("");
            editText19.setText("");
        }
    }

    public void Leedb(){
        //Lee DB de la aplicacion
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

    public void enviaXml(){
        String[] partes = Enviando.split("\\>");
        String num_tabled = partes[1];
        String mac = partes[2];
        String version = partes[3];
        String mac_serial = partes[4];
        String nomad = partes[5];
        String intentos = partes[6];

        final String text = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"+
                "<peticion>\n"+
                "   <mensaje-tipo tipo=\"PR\"></mensaje-tipo>\n"+
                "	<envio tds=\""+num_tabled+"\" mac=\""+ mac+"\" version=\""+version+"\" mac_serial=\""+mac_serial+"\" nomad=\""+nomad+"\" intentos=\""+intentos+"\"></envio>\n"+
                "   <datos>\n"+
                "       <posicion pos=\"99\"></posicion>\n"+
                "       <usuario user_sol=\""+user_sol+"\"></usuario>\n"+
                "		<preset sol=\"CN\" usuario=\""+tarjeta+"\" nip=\""+nvnip1+"\" monto=\"\" monto_preset=\"\" odome_reg=\"\" tipo_venta=\"\" usuario_trj=\"\" >"+
                "   </datos>\n"+
                "</peticion>";

        envia(text, num_tabled);

    }

    public void envia(final String salida, final String num_tabled){
        new Thread(new Runnable() {
            @Override
            public void run() {

                String respuesta = "";
                SoapObject request = new SoapObject(NAMESPACE, METHOD);
                request.addProperty( "d0" , num_tabled);
                request.addProperty( "d1" , salida);
                request.addProperty( "d2" , "");
                request.addProperty( "d3", "");

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

    public void res_ser(String xml){
        //msj("RESPUESTA");
        String busca1 = "preset";
        String busca2 = "respr";
        String dato=regresa_xml(xml, busca1,busca2);

        if (dato.equals("TRUE")){
            busca1 = "display";
            busca2 = "dato-impresiond";
            dato=regresa_xml(xml, busca1,busca2);
            String msjcon =">"+ dato+ ">2>3>4>5>6>7>";
            Intent i = new Intent(this, Msj.class );
            i.putExtra("msjcon", msjcon);
            startActivity(i);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, 2000);
    }

    public String regresa_xml(String xml, String b1, String b2){
        String envi_retun="";
        String cadena = xml;
        //Busca cadena1 y regresa donde inicia
        int resultado = cadena.indexOf(b1);
        if(resultado != -1) {
            //Desde encuentra hasta el final
            cadena=cadena.substring(resultado);
            //Busca cadena2 y regresa donde inicia
            resultado = cadena.indexOf(b2);
            if(resultado != -1) {
                //Desde encuentra hasta el final
                cadena=cadena.substring(resultado);
                //Dividir la respuesta, obtiene resultado
                String[] partes = cadena.split("\"");
                //Resultado Final
                String respg = partes[1];
                envi_retun=respg;
            }else{
                envi_retun="NO";
            }
        }else{
            envi_retun="NO";
        }
        return envi_retun;
    }






    public void msj(String msjcon){
        msjcon =">"+ msjcon+ ">2>3>4>5>6>7>";
        Intent i = new Intent(this, Msj.class );
        i.putExtra("msjcon", msjcon);
        startActivity(i);
    }


}