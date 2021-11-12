package msm.aie.veribox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

/**
 * Created by Miguel Santiago.
 * Clase Principal para verificar los Numero de referencia en el Servidor
 * regresa si estan disponinles y el monto.
 *
 * Version 1.8-0	02/Oct/2017
 * Ing Miguel Santiago Moreno
 *  - Permite ingresar Tickets a Cobro Bancario diferenciando de Factura o Cobro Bancario
 */

public class Main_ticket extends Activity implements OnKeyListener{
	
	private EditText editText1;
	private SQLiteDatabase bd; 
	private TextView textView1, textView3, textView40, textView41;
	private String pide_tck, res, tickets = "", tck_envia, d1, d2, revisar, revisarQR;
	private int poscarga, dispo, MSJ, max_tck, cont_tck;
	private String Enviando;
	private String [] tickets_alm;//, qr_alm;
	private final String NAMESPACE = "urn:veriboxwsdl";
	//private final String URL = "http://www.sigma-aie.com.mx/veribox/Veribox.php";
	private String URL = "";//"http://192.168.1.38/Veribox/Veribox.php";
	private final String SOAPACTION = "urn:veriboxwsdl#veribox";
	private final String METHOD = "veribox";
	private Button bt1, bt2, button15;
	
	//Captura de XY
	private TextView textView_xy, TextView001;
	StringBuilder stringBuilder = new StringBuilder();
	private Coor_xy cox_coy;
	//Captura Codigo Barras
	String capturo;
	private String user_sol, sol_tcks, total_PB_S, lector_cone;
	private double total_PB = 0.00;

	//Uso de la camara lector de QR
	private CameraSource cameraSource;
	private SurfaceView cameraView;
	private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
	private String token = "";
	private String tokenanterior = "";
	private TextView leevista;
	
	/*
	//Creamos el handler puente para mostrar
	//el mensaje recibido de peticiones
	private Handler puente = new Handler() {
	 @Override
	 public void handleMessage(Message msg) {
		 if (((String)msg.obj).equals("")){
			 //t_busca=10000;
			 //textView1.setText("Problemas COM");
			 msj("Problemas COM.");
			 Handler handler = new Handler();
		        handler.postDelayed(new Runnable() {
		            public void run() {
		            	fin();
		            }
		        }, 2000);
		 }
		 else {
			 String muestra = (String)msg.obj;
			 //textView1.setText("Respondio: "+muestra);
			mensajes(muestra);
			//proc_pend(muestra);
		 }
	 }
	};
	*/

	//Creamos el handler puente para mostrar
	//el mensaje recibido de peticiones
	private Handler puente2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (((String)msg.obj).equals("")){
				msj("Problemas COM.");
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						fin();
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
    setContentView(R.layout.main_ticket);
    
    this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	//Asigna el uso de la vista a camara.
	cameraView = (SurfaceView) findViewById(R.id.camera_view);
    editText1 = (EditText)findViewById(R.id.editText1);
    textView1 = (TextView)findViewById(R.id.textView1);
    textView3 = (TextView)findViewById(R.id.textView3);
	textView40  = (TextView)findViewById(R.id.textView40);
	textView41  = (TextView)findViewById(R.id.textView41);
    
    bt1 = (Button)findViewById(R.id.button1);
    bt2 = (Button)findViewById(R.id.button2);
	button15 = (Button)findViewById(R.id.button15);
    textView3.setVisibility(View.INVISIBLE);
	textView40.setVisibility(View.INVISIBLE);
	textView41.setVisibility(View.INVISIBLE);
    bt2.setVisibility(View.INVISIBLE);

    editText1.setOnKeyListener(this);
    
    tck_envia="";

    textView1.setText("");
    cont_tck=0;
    tickets_alm = new String [5];
	//qr_alm = new String [5];
	//recive datos del accion anterior
	Bundle bundle=getIntent().getExtras();
	user_sol = bundle.getString("user_sol");
	sol_tcks = bundle.getString("sol_tcks");

	if(sol_tcks.equals("B")){
		button15.setText("PAGO BANCARIO");
		textView40.setVisibility(View.VISIBLE);
		textView41.setVisibility(View.VISIBLE);
	}

    Leedb();
    
    //Revisara que se introduce desde el LECTOR DE CODIGO DE BARRAS
  	editText1 = (EditText)findViewById(R.id.editText1);
      editText1.addTextChangedListener(new TextWatcher() {

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
	     	   //editText1.setText(num);
			if (con==10){
				mas(null);
			  //capturo = s.toString();
			  //editText1.setText("");//Borra Captura   
			}
			if (con>10){
				editText1.setText("");//Borra Captura
			}
        }
       });
     //FIN - Revisara que se introduce desde el LECTOR DE CODIGO DE BARRAS  
    
    /*
  //Inicia captura de Coordenadas XY	
    //Coor_xy
    cox_coy= new Coor_xy();
   this.textView_xy = (TextView) findViewById( R.id.strXY );
   //this.textView_xy.setText("X: ,Y: ");//texto inicial

   //Evento Touch
   this.textView_xy.setOnTouchListener( new OnTouchListener()
   {
   	@Override
   	public boolean onTouch( View arg0, MotionEvent arg1 ) {
   		
   		stringBuilder.setLength(0);
   		//si la accion que se recibe es de movimiento
   		if( arg1.getAction() == MotionEvent.ACTION_UP )
   		{
   			//stringBuilder.append("Moviendo, X:" + arg1.getX() + ", Y:" + arg1.getY() );
   			//stringBuilder.append( "Detenido, X:" + arg1.getX() + ", Y:" + arg1.getY() );
   			float co_X = arg1.getX();
   			float co_Y = arg1.getY();
   			//mr_co_re(co_X, co_Y);
   			String rev=cox_coy.co_xy(co_X, co_Y);
   			try {
   				revisar (rev);
   			} catch (IOException e) {
   				// TODO Auto-generated catch block
   				e.printStackTrace();
   			}
   			//accion( co_X, co_Y);			
   		}								
   		//Se muestra en pantalla
   		//textView.setText( stringBuilder.toString() );
   		return true;
   	}			
   });
   //FIN Cptura de Coordenadas XY
	*/
    //Se quita para pruebas en DIF
    //initQR();
    }

//Funcion Acciones
public void revisar (String rev_coor) throws IOException{
	//Boton a HOME
	if (rev_coor.equals("H1") || rev_coor.equals("G1")){
		fin(null);
    }
	//Cambia de Opcion a procesar
	if (rev_coor.equals("H3") || rev_coor.equals("H4") || rev_coor.equals("H5") || rev_coor.equals("H6") || rev_coor.equals("G3") || rev_coor.equals("G4") || rev_coor.equals("G5") || rev_coor.equals("G6")){
		fin(null);
    }
	//Boton a MAS
	if (rev_coor.equals("E6") || rev_coor.equals("F6")){
		mas(null);
		editText1.requestFocus();
    }
	//Continuar a la Sig Pagina
	if (rev_coor.equals("C6")){
		ok(null);
		editText1.requestFocus();
    }

	/*switch (rev_coor) {                                    
	case "addLogo":
		//Codigo de algo
		break;
  case "TextFont":
  	//Codigo de algo2
		break;
	default:
	}
  */	
}

	@Override
	public boolean onKey(View view, int keyCode, KeyEvent event) {

	 if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
	  keyCode == EditorInfo.IME_ACTION_DONE ||
	  event.getAction() == KeyEvent.ACTION_DOWN &&
	  event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
	   
		  if (!event.isShiftPressed()) {
			   Log.v("AndroidEnterKeyActivity","Enter Key Pressed!");
			   switch (view.getId()) {
				   case R.id.editText1:
					   //Toast.makeText(this, "ENTER EN 1", Toast.LENGTH_SHORT).show();
					   mas(null);
				    break;
			   }
			   //Toast.makeText(this, "return 2",Toast.LENGTH_SHORT).show();
			   return true; 
		  }
	 }
	 //Toast.makeText(this, "return 3",Toast.LENGTH_SHORT).show();
	 return false; // pass on to other listeners.
	}

public void fin(){
	/*
	Intent j = new Intent(this, MainActivity.class );
	j.putExtra("inicia", 1);
	startActivity(j);
	*/
	env_main();
	finish();	
}

public void Leedb(){
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
			//imp_ext=fila.getInt(6);
			String mac_serial=fila.getString(7);
			String nomad=fila.getString(8);
			max_tck=fila.getInt(17);
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
}

public void mas(View view){
	//Oculta el teclado Virtual.
	InputMethodManager imm =
				(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText1.getWindowToken(), 0);
		
	if(editText1.length()>0){
		revisar = editText1.getText().toString();
		editText1.setText("");
		
		int h=Integer.parseInt(revisar.replaceAll("[\\D]", ""));
		String rev_string = String.format("%010d", h);
		boolean tck_rp = false; 
		for (int i = 0; i < 5; i++) {
			String compara = tickets_alm[i];
			//Toast.makeText(this,i+": " + compara,	Toast.LENGTH_SHORT).show();			
			try
			{
				if (compara.equals(rev_string)){
					tck_rp = true;
				}
			}
			catch (Exception ex)
			{
			    Log.e("Compara", "Error COMPARA");
			}		 
		}
		if (tck_rp){
			msj("Ticket Duplicado");
		}else{
			//textView1.setText("Consultando Servidor");
			msj("CONSULTANDO DATOS...");

			gen_xml();
			//MSJ=0;
		    //mensajes("");
			//envia();
			
			//tickets += revisar + "\n";
			//textView1.setText(tickets);
		}		
	}
}

	public void gen_xml(){

		String tipo = "FA";
		String tipo_solicitu = "FT";
		if(sol_tcks.equals("B")){
			tipo = "PB";
			tipo_solicitu = "FT";
		}

		String[] partes = Enviando.split("\\>");
		String num_tabled = partes[1];
		String mac = partes[2];
		String version = partes[3];
		String mac_serial = partes[4];
		String nomad = partes[5];
		String intentos = partes[6];

		String text = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"+
				"<peticion>\n"+
				"   <mensaje-tipo tipo=\""+tipo+"\"></mensaje-tipo>\n"+
				"	<envio tds=\""+num_tabled+"\" mac=\""+ mac+"\" version=\""+version+"\" mac_serial=\""+mac_serial+"\" nomad=\""+nomad+"\" intentos=\""+intentos+"\"></envio>\n"+
				"   <datos>\n"+
				"       <posicion pos=\"99\"></posicion>\n"+
				"       <usuario user_sol=\""+user_sol+"\"></usuario>\n";
		if(sol_tcks.equals("B")){

		text+=	"       <pago-bancario solicito=\"true\" processed=\"\" tranzaccion=\"revisatck\" guardar=\"TK\">\n"+
				"   	<comprobante tipo_compro=\"T\" tipo_cliepb=\"\" clientepb=\"\" >\n"+ "</comprobante>\n"+
				"       <registra bancoEmisor=\"\" cardNumber=\"\" tipotarjeta=\"\" hora_fecha=\"\" serieTDS=\"\" aid=\"\" error_description=\"\" transacc=\"\" monto=\"\" arqc=\"\" codigoAprobacion=\"\" marcaTarjeta=\"\""+
				"       vigencia=\"\" titular=\"\" terminalID=\"\" numeroControl=\"\" referenciaBanco=\"\" tvr=\"\" tsi=\"\" apn=\"\" afiliacion=\"\" tckspb=\"" + revisar + "\" ></registra>\n"+
				"       </pago-bancario>\n";

		}else{
		text+=	"       <factura tipo_solicitu=\""+tipo_solicitu+"\" tipo_clien=\"\" cliente=\""+
				"\" tipo_pago=\"\" enti_banco_f=\"\" dig_tarjeta=\"\" nombre=\"\"\n"+
				"       	calle=\"\" num_ex=\"\" num_int=\"\" colonia=\"\" localidad=\"\" referencia=\"\" municipio=\"\" estado=\"\"\n"+
				"       pais=\"\" cp=\"\" telefono=\"\" correos=\"\" num_ref_f=\""+revisar+"\">\n"+
				"       </factura>\n";
		}

		text+=	"   </datos>\n"+
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
				puente2.sendMessage(sms);
			}
		}).start();
	}

	public void res_ser(String xml){
		boolean res_tck = false;
		String monto = "";
		String busca1 = "factura";
		String busca2 = "resf";
		String dato=regresa_xml(xml, busca1,busca2);
		if (dato.equals("true")){
			//Toast toast = Toast.makeText(this,"FACTURA", Toast.LENGTH_LONG); toast.setGravity(Gravity.CENTER, 0, 0);	toast.show();
			busca2 = "montotck";
			dato=regresa_xml(xml, busca1,busca2);
			monto = dato;
			res_tck = true;
		}else{
			busca1 = "pago-bancario";
			busca2 = "respg";
			dato=regresa_xml(xml, busca1,busca2);
			if (dato.equals("true")){
				//Toast toast = Toast.makeText(this,"PAGO BANCARIO", Toast.LENGTH_LONG); toast.setGravity(Gravity.CENTER, 0, 0);	toast.show();
				busca2 = "montopg";
				dato=regresa_xml(xml, busca1,busca2);
				monto = dato;
				res_tck = true;
			}
		}

		if (res_tck){
		    //Revisa el numero de Ticket si ya esta almacenado.
            busca2 = "ticket";
            dato=regresa_xml(xml, busca1,busca2);

            boolean tck_rp = false;
            for (int i = 0; i < 5; i++) {
                String compara = tickets_alm[i];
                //Toast.makeText(this,i+": " + compara,	Toast.LENGTH_SHORT).show();
                try
                {
                    if (compara.equals(dato)){
                        tck_rp = true;
                    }
                }
                catch (Exception ex)
                {
                    Log.e("Compara", "Error COMPARA");
                }
            }

            if (tck_rp){
                msj("Ticket Duplicado");
            }else{
                //Continua
                Float montof= Float.parseFloat(monto);
                //Monto Total del Cobro Bancario
                total_PB += montof;
                //De Flotante a String
                monto = String.format("%.2f", montof);
                total_PB_S = String.format("%.2f", total_PB);

                int i=Integer.parseInt(dato.replaceAll("[\\D]", ""));
                String rev_string = String.format("%010d", i);
                //revisar = String.format("0{0,10}", revisar);

                tickets_alm[cont_tck]=rev_string;
                tickets += rev_string +"   $" +monto+"\n";
                tck_envia += rev_string+"\n";
                textView1.setText(tickets);
                textView41.setText(total_PB_S);
                bt2.setVisibility(View.VISIBLE);
                textView3.setVisibility(View.VISIBLE);

                cont_tck++;
                if (cont_tck == 5){
                    editText1.setVisibility(View.INVISIBLE);
                    bt1.setVisibility(View.INVISIBLE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            ok(null);
                        }
                    }, 2500);
                }
            }
		}else{
			msj("TICKET:\nNO DISPONIBLE.");
			//textView1.setText(tickets);
		}

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

public void fin(View view) {
    /*
	Intent i = new Intent(this, MainActivity.class );
	i.putExtra("inicia", 1);
    startActivity(i);
	*/
	env_main();
	finish();
	}

public void ok (View view){
	if(sol_tcks.equals("B")){
		//MSM 05/Oct/2017 Ver:1.8-0
		//rev_lectores();
		//pago_banco();
		pide_tck="99";
		poscarga=1;
		if (tickets.length()>0){
			Intent i = new Intent(this, Fac_rapida.class );
			i.putExtra("pos", pide_tck);
			i.putExtra("tipofac", poscarga);
			i.putExtra("tickets", tck_envia);
			i.putExtra("cont_tck", cont_tck);
			i.putExtra("envia_fac", "PB");
			i.putExtra("total_PB_S", total_PB_S);
			startActivity(i);
			finish();
		}
	}else{
		pide_tck="KA";
		poscarga=1;
		if (tickets.length()>0){
			Intent i = new Intent(this, Fac_rapida.class );
			i.putExtra("pos", pide_tck);
			i.putExtra("tipofac", poscarga);
			i.putExtra("tickets", tck_envia);
			i.putExtra("cont_tck", cont_tck);
			i.putExtra("envia_fac", "FA");
			startActivity(i);
			finish();
		}else{
			msj("Ingrese Ticket");
		}
	}
}


	/*
	public void pago_banco(){

		AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "tablet", null, 1);
		bd = admin.getWritableDatabase();

		String URL ="", tds = "", Enviando = "";
		//*****************************************************************************
		Cursor fila = bd.rawQuery("select * from config where num=1"
				+ "", null);
		if (fila.moveToFirst()) {
			URL=fila.getString(5);
			if (URL.length()>11){
				//URL = "http://" + URL + "/Veribox/Veribox.php";
				int num_tabled=fila.getInt(2);
				tds=fila.getString(2);
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

		File path = new File(Environment.getExternalStorageDirectory(), "Tickets");
		path.mkdirs();

		//Una vez creado disponemos de un archivo para guardar datos
		try
		{
			File ruta_sd = Environment.getExternalStorageDirectory();

			File f = new File(ruta_sd.getAbsolutePath(), "Tickets/PBDATA.txt");

			OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(f));

			tck_envia = tck_envia.replace("\n", "|");

			String conf_cb = "99\n";  		//Pocicion de la venta, 99=Multiples tickets
			conf_cb += lector_cone+"\n";	//Dispositivo a Conectar.
			conf_cb += URL+"\n";			//Servidor SIGMA
			conf_cb += tds+"\n";			//Terinal VeriBox que Solicita
			conf_cb += user_sol+"\n";		//Usuario que Realiza la Transaccion
			conf_cb += "T\n";				//Tipo de Comprobante que se entrega T=Ticket, F=Factura
			conf_cb += "\n";				//Tipo de Factura que se Realizara C=Cliente Registrado, R=RFC
			conf_cb += "\n";				//Cliente a quien se Factura: RFC o No Cliente
			conf_cb += total_CB_S+"\n";		//Monto a Cobrar ya que son Multiples tickets
			conf_cb += tck_envia+"\n";		//numeros de referencias para Cobro Bancario

			fout.write(conf_cb);
			fout.close();

			//Liberar del Modo Kisco
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				stopLockTask();
			}

			//Toast.makeText(this, "Texto de prueba.3", Toast.LENGTH_SHORT).show();
		}
		catch (Exception ex)
		{
			//Log.e("Ficheros", "Error al escribir fichero a tarjeta SD");
			Toast.makeText(this, "Error de Fichero o Desbloquear", Toast.LENGTH_LONG).show();
		}

		Intent launchIntent = getPackageManager().getLaunchIntentForPackage("mx.qpay.testsdk");
		startActivity(launchIntent);
		finish();
	}
	*/

public void msj(String msjcon){
	//int index = 1;
	msjcon =">"+ msjcon+ ">2>3>4>5>6>7>"; 	    
	Intent i = new Intent(this, Msj.class );
    i.putExtra("msjcon", msjcon);
    //i.putExtra("index", index);
    startActivity(i);
}

	public void teclado(View view){
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	//Deshabilitar BOTON atras
	@Override
	public void onBackPressed() {
	}


	public void env_main(){
		File path = new File(Environment.getExternalStorageDirectory(), "Tickets");
		path.mkdirs();
		try
		{
			File ruta_sd = Environment.getExternalStorageDirectory();
			File f2 = new File(ruta_sd.getAbsolutePath(), "Tickets/VBCONF.txt");
			OutputStreamWriter fout2 = new OutputStreamWriter(new FileOutputStream(f2));
			fout2.write("U");
			fout2.close();
		}
		catch (Exception ex)
		{
			Log.e("Ficheros", "Error al escribir fichero a tarjeta SD");
		}
	}

	public void initQR() {
		BarcodeDetector barcodeDetector =
				new BarcodeDetector.Builder(this)
						.setBarcodeFormats(Barcode.QR_CODE)
						.build();

		cameraSource = new CameraSource
				.Builder(this, barcodeDetector)
				.setRequestedPreviewSize(100, 100)
				.setFacing(CameraSource.CAMERA_FACING_FRONT)
				.setAutoFocusEnabled(true) //you should add this feature
				.build();
		cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// verifico si el usuario dio los permisos para la camara
				if (ActivityCompat.checkSelfPermission(Main_ticket.this, Manifest.permission.CAMERA)
						!= PackageManager.PERMISSION_GRANTED) {
				} else {
					try {
						cameraSource.start(cameraView.getHolder());
					} catch (IOException ie) {
						Log.e("CAMERA SOURCE", ie.getMessage());
					}
				}
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				cameraSource.stop();
			}
		});

		// preparo el detector de QR
		barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
			@Override
			public void release() {
			}


			@Override
			public void receiveDetections(Detector.Detections<Barcode> detections) {
				final SparseArray<Barcode> barcodes = detections.getDetectedItems();
				//Se encontro un codigo de barras
				if (barcodes.size() > 0) {
					// obtenemos el token
					token = barcodes.valueAt(0).displayValue.toString();

					// verificamos que el token anterior no se igual al actual
					// esto es util para evitar multiples llamadas empleando el mismo token
					if (!token.equals(tokenanterior)) {
						// guardamos el ultimo token proceado
						tokenanterior = token;

						//Revisa el dato que se encuentra en el
						rev_QR(token);

						//Despues del tiempo determinado se borra el token para evitar
						//el mismo token en la lectura
						new Thread(new Runnable() {
							public void run() {
								try {
									synchronized (this) {
										wait(5000);
										// limpiamos el token
										tokenanterior = "";
									}
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									Log.e("Error", "Waiting didnt work!!");
									e.printStackTrace();
								}
							}
						}).start();
					}
				}
			}
		});
	}

	public void rev_QR(String data){
		int j=0;
		try{
			String[] partes = data.split("=");
			//Valida si tiene los datos requeridos
			//	idw=0049C344VNZEJLRDF1A
			if ((partes[0].equals("idw")) && (partes[1].length() > 0)){
				revisar = partes[1];
				//int h=Integer.parseInt(revisar.replaceAll("[\\D]", ""));
				//String rev_string = String.format("%010d", h);
				boolean tck_rp = false;

				/*
				for (int i = 0; i < 5; i++) {
					String compara = qr_alm[i];
					try {
						if (compara.equals(revisar)){
							tck_rp = true;
						}
					}
					catch (Exception ex)
					{
						Log.e("Compara", "Error COMPARA");
						qr_alm[i] = revisar;
					}
					//j++;
				}

				if (tck_rp){
					msj("QR Revisado");
				}else{
					//qr_alm[j] = revisar;
					msj("CONSULTANDO DATOS QR...");
					gen_xml();
				}
				*/
                msj("CONSULTANDO DATOS QR...");
                gen_xml();
			}else{
				msj("QR INCOMPLETA");
			}
		}catch(Exception e){
			msj("QR invalido para esta Aplicación,\nConsulte a su Estación de Servicio");
		}
	}

	/*
	//MSM 05/Oct/2017 Ver:1.8-0
	//Revisa lectores, si son mas de 1 envia a seleccinar Lector.
	public void rev_lectores(){
		String [] lectores;
		lectores = new String[5];
		int dis = 0;
		Object[] pairedObjects = BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray();
		final BluetoothDevice[] pairedDevices = new BluetoothDevice[pairedObjects.length];
		for(int i = 0; i < pairedObjects.length; ++i) {
			pairedDevices[i] = (BluetoothDevice)pairedObjects[i];
		}
		for (int i = 0; i < pairedDevices.length; ++i) {
			String nom_blue = pairedDevices[i].getName();
			String compara = nom_blue.substring(0,2);
			if (compara.equals("WP")){
				//Regsitra los dispositivos encontrados
				lectores[dis] = nom_blue;
				dis++;
			}
		}

		if(dis == 0){
			msj("Lector NO\nVinculado.");
			finish();
		}else{
			lector_cone=lectores[0];
			if (dis == 1) {
				pago_banco();
			}else{
				Intent i = new Intent(this, Lectores.class);
				i.putExtra("lectores", lectores);
				startActivityForResult(i, 0);
			}
		}

	}

	//MSM 05/Oct/2017 Ver:1.8-0
	//Regresa de selecion del Lector a conectar
	protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
		if (requestCode == 0){
			if (data != null && resultCode == RESULT_OK) {
				lector_cone = data.getStringExtra("lecBusca");
				pago_banco();
			}
		}
	}
	*/



}