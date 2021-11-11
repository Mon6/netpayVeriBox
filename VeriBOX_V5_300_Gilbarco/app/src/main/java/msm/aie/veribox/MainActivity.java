package msm.aie.veribox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.BitSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.view.View.OnKeyListener;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/*
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.android.Contents;
*/
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Miguel Santiago.
 * Clase Principal
 *
 * Version 1.8-0	02/Oct/2017
 * Ing. Miguel Santiago Moreno
 *  - Cabios para que Realice un reset de Clave de Gerente y Servicio
 *  - Cambios para para el mejor control de la Impresora ePOS_AIE
 *  - Permite ingresar Tickets a Cobro Bancario.
 *
 * Version 2.0-0  MSM 02/Oct/2017
 * Ing. Miguel Santiago Moreno
 *  -Uso de Cerficado para Actualizaciones
 *  -Uso de Multiples Lectores por Posicion o por Usuario
 *  -Cobro Bancario Multiples Ticket
 *  -Quita uso de datos Fiscales a Factura.
 *  -Cambios para uso de FCDI 3.3
 *  -Acumulado de Tickets
 *
 *  Version 3.0-0  MSM /21Dic/2018
 *  Ing. Miguel Santiago Moreno
 *   -Ajuste para el uso de pantalla touch
 *   -Se activa la funcion de Boton multimedia.
 */


public class MainActivity extends Activity implements OnKeyListener{

	// Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final UUID CLIENT_CHARACTERISTIC_CONFIG_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final String UUID_RESPONSE = "00001101-0000-1000-8000-00805F9B34FB";


	OnClickListener myClickListener;
	ProgressDialog myProgressDialog;
	private Toast failToast;
	private Handler mHandler;
	private boolean connectStat = false;
	
	private Dialog dialog;
	
	// Bluetooth Stuff
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket btSocket = null;
    private static OutputStream outStream = null;
    private ConnectThread mConnectThread = null;
    private String deviceAddress = null;
        
    // Well known SPP UUID (will *probably* map to RFCOMM channel 1 (default) if not in use); 
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	int tyf=0; //Define que sera Tickets inicial.
	int acti = 0, poscarga= 0;
	String d1,d2,d3, pide_tck="0";
	String met_pago_f, digitos_f ,enti_banco_f ;
	//String[] res;
	private Button bt3,bt4,bt5,bt6,btv1,btv2,btv3,btv4,btv5,btv6,btv7, b_bk, b_fr;
	//private EditText edit1,edit2,edit3;
	private TextView textView1, textView30, textView36;
	SQLiteDatabase bd; 
	//DEFINE POR DONDE SALDRA LA IMPRESION
	//1 = Impresora de Tabled
	//0 = Impresora TPV
	int imp_ext,num_tabled, intentos;
	String mac_serial, mac, version, nomad, Enviando, Datos, DatosTransa, RM1, RM2, RM3,clave_esp;
	private String address;
	int pend, con_pet=0, t_busca;
	
	private final String NAMESPACE = "urn:veriboxwsdl";
	//private final String URL = "http://www.sigma-aie.com.mx/veribox/Veribox.php";
	private String URL;// = "http://192.168.1.38/Veribox/Veribox.php";
	private final String SOAPACTION = "urn:veriboxwsdl#veribox";
	private final String METHOD = "veribox";
	private String serv_cb; 

	
	private static String LOG_TAG = "GenerateQRCode";
	static int smallerDimension;
	
	private Bitmap bitmap, bitmap1, bitmap2;
	private BitSet dots;
	private EditText myTextBox;
	private String capturo;
	private String em_rfc, re_rfc, folio_fis, total;
	private int pend_pau=0;
	private String cbInputText, qrInputText;
	private int MSJ=10, vista, rango_pos_ini, rango_pos_fin, pag, cont_pag;
	private String val_boton1,val_boton2,val_boton3,val_boton4;
	private boolean pausa_men=true;
	//Captura de XY
	private TextView textView_xy, TextView001;
	StringBuilder stringBuilder = new StringBuilder();
	private Coor_xy cox_coy;
	//
	private Bitacora bitacora;
	//
	boolean esp_con;
	int con_time;
	String user_sol;
	boolean permite_salir, activaAcumulado;
	boolean lanza_user_sol, servidor_ok;
	int visOperador, visTienda, visTickets, visMepagoT, visOrdenTP, visTckCobro, visAcumula;
	boolean inicia0, busca_imp, revMsjEpos, revComm, msImpNoRs;
	int treloj;

	Timer timer = new Timer();	//Tiempo de Mensajes de Impresora INICIAL Conectada o NO.
	Timer timer2 = new Timer();	//Tiempo del Reloj
	Timer timer3 = new Timer();	//Tiempo del Mensaje de Estado de Impresora al No encontrarce.
	Timer timer4 = new Timer(); //Tiempo de espera de Cobro Bancario
	/*
	//Creamos el handler puente para mostrar
		//el mensaje recibido de peticiones
		private Handler puente = new Handler() {
		 @Override
		 public void handleMessage(Message msg) {
			 if (((String)msg.obj).equals("")){
				 //t_busca=10000;
				 //textView1.setText("Problemas COM");
				 msj(">Problemas COM>2>3>4>5>6>7>");
				 pend_user(2); 
			 }
			 else {
				 String muestra = (String)msg.obj;
				 //textView1.setText("Respondio: "+muestra);
				mensajes(muestra);
				//proc_pend(muestra);
			 }
			 apa_bot(true);
			 pausa_men = true;
		 }
		};	
		*/
		
		
	//Creamos el handler puente para mostrar
	//el mensaje recibido de peticiones
	private Handler puente2 = new Handler() {
	 @Override
	 public void handleMessage(Message msg) {
		 if (((String)msg.obj).equals("")){
			 //Tiempo terminado o contestacion sin datos
			 msj(">Problemas COM>2>3>4>5>6>7>");
			 gen_xml_test3();
			 pend_user(2); 
		 }
		 else {
			 String muestra = (String)msg.obj;
			 res_ser(muestra);
			 //msj(">RESPUESTA>2>3>4>5>6>7>");
		 }
		 apa_bot(true);
		 pausa_men = true;
	 }
	};

	/*
	//Creamos el handler puente para mostrar
	//el mensaje recibido de peticiones
	private Handler puente = new Handler() {
	 @Override
	 public void handleMessage(Message msg) {
		//3.-Pendientes Valida respuesta de la consulta a DB
		 if (((String)msg.obj).equals("0")){
			//3.1.-Pendientes NO hay pendientes continua preguntando
			 t_busca=5000;
			 textView1.setText("NADA pendiente ");
			 myTextBox.setText("");//Borra Captura
			 pendientes(); 
		 }
		 else {
			//3.2.-Pendientes Encuentra pendiente
			textView1.setText("Consulta Servidor...");
			myTextBox.setText("");//Borra Captura
			t_busca=5000;
			//4.-Pendientes Envia mensaje de pendiente a servidor
			mensaje_M3();
		 }
	 }
	};	
	*/
	/*
	//Creamos el handler puente2 para mostrar
	//el mensaje recibido de peticiones
	private Handler puente2 = new Handler() {
	 @Override
	 public void handleMessage(Message msg) {
		//8.-Pendientes Valida respuesta tipo de pendiente
		 if (((String)msg.obj).equals("")){
			//8.1.-Pendientes Valida respuesta tipo de pendiente False
			 t_busca=10000;
			 textView1.setText("Problemas COM");
			 pendientes(); 
		 }
		 else {
			 String muestra = (String)msg.obj;
			textView1.setText("Respondio");
			//8.2.-Pendientes Envia a procesar respuesta de tipo de Respuesta
			proc_pend(muestra);
		 }
	 }
	};
	*/
	/*
	//Creamos el handler puente para mostrar
	//el mensaje recibido de peticiones
	private Handler puente3 = new Handler() {
	 @Override
	 public void handleMessage(Message msg) {
		 //rev_cap();
	 }
	};
	*/
 
@Override
protected void onCreate(Bundle savedInstanceState) {
 super.onCreate(savedInstanceState); 
 requestWindowFeature(Window.FEATURE_NO_TITLE);
 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
 setContentView(R.layout.activity_main); 
 
 /*
 getWindow().getDecorView().setSystemUiVisibility(
         View.SYSTEM_UI_FLAG_LAYOUT_STABLE
         | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
         | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
         | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
         );
*/
 /*
 //Saber que version de Android se esta usando
 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
     hideVirtualButtons();
 }
 */

	//Permisos para uso de Kiosco
	provisionOwner();
 
 
 btv1 = (Button)findViewById(R.id.Button1); //1 vista
 btv2 = (Button)findViewById(R.id.Button2);	//2 vistas 
 btv3 = (Button)findViewById(R.id.Button3);	//2 vistas
 btv4 = (Button)findViewById(R.id.Button4);	//4 vistas
 btv5 = (Button)findViewById(R.id.Button5);	//4 vistas
 btv6 = (Button)findViewById(R.id.Button6);	//4 vistas
 btv7 = (Button)findViewById(R.id.Button7);	//4 vistas
 
 btv1.setVisibility(View.INVISIBLE);//1 vista
 btv2.setVisibility(View.INVISIBLE);
 btv3.setVisibility(View.INVISIBLE);
 btv4.setVisibility(View.INVISIBLE);
 btv5.setVisibility(View.INVISIBLE);
 btv6.setVisibility(View.INVISIBLE);
 btv7.setVisibility(View.INVISIBLE);
 
 //bt1 = (Button)findViewById(R.id.button1);
 //bt2 = (Button)findViewById(R.id.button2);
 bt3 = (Button)findViewById(R.id.button3);
 bt4 = (Button)findViewById(R.id.button4);
 bt5 = (Button)findViewById(R.id.button5);
 bt6 = (Button)findViewById(R.id.button6);
 b_bk = (Button)findViewById(R.id.button_bk);
 b_fr = (Button)findViewById(R.id.button_fr);
 
 bt3.setVisibility(View.INVISIBLE); //Boton Ingresa tickets
 bt4.setVisibility(View.INVISIBLE); //Boton Home
 bt6.setVisibility(View.VISIBLE);	//Boton Gerente
 
 //edit2 = (EditText)findViewById(R.id.editText2);
 //edit3 = (EditText)findViewById(R.id.editText3);
textView1 = (TextView)findViewById(R.id.textView1);
textView36= (TextView)findViewById(R.id.textView36);
textView36.setVisibility(View.INVISIBLE);
 //textView30 = (TextView)findViewById(R.id.textView30);
 //textView2.setVisibility(View.VISIBLE);


 servidor_ok = false;
 AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"tablet", null, 1);
 bd = admin.getWritableDatabase();
 
 cont_pag=1;
 
 //Revisara que se introduce desde el LECTOR DE CODIGO DE BARRAS
 myTextBox = (EditText) findViewById(R.id.editText1);
 //Para poder capturar lo que se ha precinado en teclado multimedia
	myTextBox.setOnKeyListener(this);
 myTextBox.requestFocus();
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
	   textView1.setText(num);
	   if (con==5){
		   capturo = s.toString();
		   String p1 = capturo.substring(0,1);
		   if (p1.equals("3")){
			   //myTextBox.setText("");
			   textView1.setText("POSICION " + p1);
			   encuentra_tck(capturo);
		    }
		   //espera_cap();
	   }
	   /*if (con==7){
		   capturo = s.toString();
		   String p1 = capturo.substring(0,1);
		   if (p1.equals("4")||p1.equals("5")){
			   //myTextBox.setText("");
			   textView1.setText("Precarga" + p1);
			   encuentra_preset(capturo);
 		    }
	   }
	   if (con==12){
		   capturo = s.toString();
		   String p1 = capturo.substring(0,1);
		   if (p1.equals("9")){
			   //myTextBox.setText("");
			   textView1.setText("Vehículo" + p1);
			   encuentra_vehi(capturo);
		    }
	   }
	   if (con==16){
		   capturo = s.toString();
		   String p1 = capturo.substring(0,1);
		   if (p1.equals("2")){
			   //myTextBox.setText("");
			   textView1.setText("Tarjeta:" + p1);
			   encuentra_tarjeta(capturo);
		    }
	   }*/
	   if (con>5){
		   myTextBox.setText("");//Borra Captura
	   }
	   //else  myTextBox.setText("");
   }
  });
//FIN - Revisara que se introduce desde el LECTOR DE CODIGO DE BARRAS  
 
//Lineas para ocultar el teclado virtual (Hide keyboard) DESACTIVA TECLADO
InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
imm.hideSoftInputFromWindow(myTextBox.getWindowToken(), 0);
myTextBox.setInputType(InputType.TYPE_NULL);
 
  
 Leedb();
 vista();
 if (visOrdenTP==1){
	 tyf=2;
 }
 cambiatf(null); //Opcion Inicial
 
 //guarda_conf();

 
//Check whether bluetooth adapter exists
 mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); 
 if (mBluetoothAdapter == null) { 
      Toast.makeText(this, "Equipo Sin Bluetooth", Toast.LENGTH_LONG).show(); 
      //finish(); 
      return;
 }
 
//Si el BT no esta activado pide activar.
	if (!mBluetoothAdapter.isEnabled()) {
		mBluetoothAdapter.enable();
		msj(">Activando BLUETOOTH ...>2>3>4>5>6>7>");
		/*Pedia a usuario activacion.
		Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		*/
	}
 
 //Lee si existe una peticion al temino del Cobro Bancario
 //lee_CBreturn();
 
 //Envia a solicitar USUARIO Y CONTRASEÑA
 lanza_user_sol = true;
 permite_salir = false;
	activaAcumulado = false;
 user_sol="000";
 revMsjEpos = true;
 revComm = true;
 msImpNoRs = false;
 lee_user();

// if (!servidor_ok){
//	 obtiene_mac();
//	 msj(">La terminal VeriBOX no esta\nconfigurada, consulte a su\nCentro de Servicio.\nMAC: "+address+">0>3>4>5>6>7>");
// }else{
//	 msj(">Arrancando VeriBOX...>2>3>4>5>6>7>");
// }
	obtiene_mac();
 
 inicia0 = false;
 try
 {
	 Bundle bundle=getIntent().getExtras();
	 int inicia = bundle.getInt("inicia");
	 if (inicia==1){
		//Toast.makeText(this, "Continua ejecucion", Toast.LENGTH_SHORT).show();
	 }else{
		 //Toast.makeText(this, "Solicita TEST", Toast.LENGTH_SHORT).show();
		 //provisionOwner();
		 //bloquea();
		 //if (servidor_ok)
		 //gen_xml_test3();
		 inicia0= true;
	 }
 }
 catch (Exception ex)
 {
 	//Toast.makeText(this, "INICIA de 0 Solictara Test",Toast.LENGTH_SHORT).show();
	//provisionOwner();
	//bloquea();
 	//if (servidor_ok)
 	//gen_xml_test3();
 	inicia0= true;
 }

 //Uso de bitacora para revicion
	// Bitacora bitacora;
	bitacora = new Bitacora();

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
		//si la acci�n que se recibe es de movimiento
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

	lee_CBreturn(0); //Borra mensaje resagado de Cobro
	timer4.scheduleAtFixedRate(new TimerTask() {
		@Override
		public void run() {
			//Ejecuta
			hiloPago();
		}
	}, 1000, 1000);
 
 //Si la Opcion de imprecion de tickets es por Impresora Externa
	if(servidor_ok &&imp_ext==1 && inicia0){
		//inicia_epos();
	}

	if(imp_ext==5){
		textView36.setVisibility(View.INVISIBLE);
		busca_imp = false;
		timer.cancel();
		timer2.cancel();

		if (servidor_ok){
			gen_xml_test3();
		}

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				// acciones que se ejecutan tras los milisegundos
				bloquea();
				msj(">IMPRESORA SIN USO.>2>3>4>5>6>7>");
			}
		}, 1000);
	}else{
		if(imp_ext==1){
			busca_imp = true;
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					//Ejecuta Hilo para mostrar Informacion de Impresora desde el inicio
					hiloMmsjepos();
				}
			}, 9000, 9000);

			//Da 30 segundo para iniciar impresora
			/*treloj = 30;
			timer2.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					//Ejecuta
					hiloReloj();
				}
			}, 1000, 1000);
			*/
			bloquea();
			textView36.setVisibility(View.INVISIBLE); // Quita vista de contador.
			//msj(">La impresora no responde, verifique que esté conectada correctamente y reinicie el equipo>2>3>4>5>6>7>");
			timer2.cancel(); //Detiene ejecucion cada segundo del Reloj
			//timer.cancel(); //Detiene ejecucion Muestra mensajes desde Impresora.

		}else{
			textView36.setVisibility(View.INVISIBLE);
			busca_imp = false;
			timer.cancel();
			timer2.cancel();

			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					// acciones que se ejecutan tras los milisegundos
					bloquea();
					msj(">IMPRESORA SIN USO.>2>3>4>5>6>7>");
				}
			}, 1000);
		}
	}

	/*

	if(imp_ext==1){
		//Ya no funciona ---tiempo_msj_imp();
		busca_imp = true;
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				//Ejecuta
				hiloMmsjepos();
			}
		}, 9000, 9000);


		treloj = 30;
		timer2.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				//Ejecuta
				hiloReloj();
			}
		}, 1000, 1000);

	}

 if(imp_ext==1){
	 //pend_pau=1;
	 myProgressDialog = new ProgressDialog(this);
     failToast = Toast.makeText(this, R.string.failedToConnect, Toast.LENGTH_SHORT);
	 
     mHandler = new Handler() {
         @Override
         public void handleMessage(Message msg) {
        	 if (myProgressDialog.isShowing()) {
             	myProgressDialog.dismiss();
             }
        	 
        	 // Check if bluetooth connection was made to selected device
             if (msg.what == 1) {
             	// Set button to display current status
                 connectStat = true;
                 pend_pau=0;
                 //connect_button.setText(R.string.connected);
                 
     	 		// Reset the BluCar
     	 		//AttinyOut = 0;
     	 		//ledStat = false;
     	 		//write(AttinyOut);
             }else {
             	// Connection failed
            	 pend_pau=0;
            	 failToast.show();
             }
         }
     };
     
   //Check whether bluetooth adapter exists
    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); 
    if (mBluetoothAdapter == null) { 
         Toast.makeText(this, R.string.no_bt_device, Toast.LENGTH_LONG).show(); 
         finish(); 
         return; 
    }
    
   //Si el BT no esta activado pide activar.
    if (!mBluetoothAdapter.isEnabled()) {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }
    connect();
    
 }// FIN - Si la Opcion de imprecion de tickets es por Impresora Externa - Conecta a Bluethoot
 */
 //t_busca=5000;
 //textView1.setText("INICIA-" +t_busca+" *"+con_pet);
 //pend_0();
 //pendientes();

	/*
	Intent i = new Intent(this, Fac_rapida1_msj.class);
	i.putExtra("texto","Cliente sin CORREO:" );
	i.putExtra("opcX", "REGRESAR");
	i.putExtra("opcY", "CONTINUAR");
	//startActivityForResult(i, 0);
	*/

}

/*
public void espera_cap(){
	//Espero por si llegan mas datos
	//int tiempo=t_busca*1000;
		//espera tiempo o si no lanza abortar
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}
				
				Message sms = new Message();
			    sms.obj = "5";
			    puente3.sendMessage(sms);
			}
		}).start();
}
*/



public void revisar (String rev_coor) throws IOException{
	//Boton a HOME
	if (rev_coor.equals("H1")){
		if (servidor_ok){
			home(null);
		}
		
    }
	//Cambia de Opcion a procesar
	if (rev_coor.equals("H3") || rev_coor.equals("H4") || rev_coor.equals("H5") || rev_coor.equals("H6") || rev_coor.equals("G3") || rev_coor.equals("G4") || rev_coor.equals("G5") || rev_coor.equals("G6")){
		if (servidor_ok){
			con_time = 4;		
			cambiatf(null); //Cambia de Opciones
		}
    }
	//Tickets de FACTURA - Tickets de Pago Banco - Acumulado
	if (rev_coor.equals("H8") || rev_coor.equals("G8")){
		//La VeriBOX ya esta configurada
		if(servidor_ok){
			//Opcion para tickets en Factura.
			if (visTickets==0){
				if (tyf == 2 && visOrdenTP == 0) { //Inicia con Ticket
					con_time = 4;            //Tiempo espera para bloqueo
					esp_con = false;        //Sigue o no esperando
					permite_salir = true;    //Permite o no salir de la interfaz actual
					lanza_ventana("T");
				}else
				if (tyf == 3 && visOrdenTP == 1) { //Inicia con Cobro Bancario
					con_time = 4;            //Tiempo espera para bloqueo
					esp_con = false;        //Sigue o no esperando
					permite_salir = true;    //Permite o no salir de la interfaz actual
					lanza_ventana("T");
				}
			}

			//Se aumenta la Opcion para Tickets en Cobro Bancario
			//Version 2.0-0 MSM 02/Oct/2017
			if (visTckCobro==0 ){
				if (tyf==3 && visOrdenTP==0){ //Inicia con Ticket
					con_time = 4;			//Tiempo espera para bloqueo
					esp_con = false;		//Sigue o no esperando
					permite_salir = true;	//Permite o no salir de la interfaz actual
					lanza_ventana("B");
					//Toast.makeText(this, "Solicita TCKs a PB \nTCK", Toast.LENGTH_LONG).show();
				}else
				if (tyf==0 && visOrdenTP==1){ //Inicia con Cobro Bancario
					con_time = 4;			//Tiempo espera para bloqueo
					esp_con = false;		//Sigue o no esperando
					permite_salir = true;	//Permite o no salir de la interfaz actual
					lanza_ventana("B");
					//Toast.makeText(this, "Solicita TCKs a PB \nPB", Toast.LENGTH_LONG).show();
				}
			}


			//Se aumenta la opcion para Acumulado de Venta.
			//Version 2.0-0 MSM 13/Mar/2018
			//MSM 03/Abr/2018
			if(visAcumula == 0){
				if (tyf==1 && visOrdenTP==0){ //Inicia con Ticket
					con_time = 4;			//Tiempo espera para bloqueo
					esp_con = false;		//Sigue o no esperando
					permite_salir = true;	//Permite o no salir de la interfaz actual
					activaAcumulado = true;
					bt5.setText("ACUMULADO");
					//Quita demas opciones
					bt3.setVisibility(View.INVISIBLE);	//Boton Ingresar TCK
					bt6.setVisibility(View.INVISIBLE);	//Boton Gerente
					bt4.setVisibility(View.VISIBLE);	//Boton Home
				}
				/*
				else
				if (tyf==2 && visOrdenTP==1){ //Inicia con Cobro Bancario
					con_time = 4;			//Tiempo espera para bloqueo
					esp_con = false;		//Sigue o no esperando
					permite_salir = true;	//Permite o no salir de la interfaz actual
					activaAcumulado = true;
					//Toast.makeText(this, "Solicita TCKs a PB \nPB", Toast.LENGTH_LONG).show();
				}*/
			}

		}
    }
	//Ejecuta Boton1
	if (rev_coor.equals("F2") || rev_coor.equals("F3") || rev_coor.equals("F4") || rev_coor.equals("E2") || rev_coor.equals("E3") || rev_coor.equals("E4") || rev_coor.equals("D2") || rev_coor.equals("D3") || rev_coor.equals("D4") || rev_coor.equals("C2") || rev_coor.equals("C3") || rev_coor.equals("C4") || rev_coor.equals("B2") || rev_coor.equals("B3") || rev_coor.equals("B4")){
		if (servidor_ok){
			try{
				bitacora.guarda_b("Boton 1");
			}catch (Exception e){e.getStackTrace();}

			t1(null);
		}
    }
	//Ejecuta Boton2
	if (rev_coor.equals("F5") || rev_coor.equals("F6") || rev_coor.equals("F7") || rev_coor.equals("E5") || rev_coor.equals("E6") || rev_coor.equals("E7") || rev_coor.equals("D5") || rev_coor.equals("D6") || rev_coor.equals("D7") || rev_coor.equals("C5") || rev_coor.equals("C6") || rev_coor.equals("C7") || rev_coor.equals("B5") || rev_coor.equals("B6") || rev_coor.equals("B7")){
		if (servidor_ok){
			t2(null);	
		}
    }
	//Boton adelante
	if (rev_coor.equals("E8") || rev_coor.equals("D8") ){
		if (servidor_ok){
			mas(null);
		}
    }
	//Boton atras
	if (rev_coor.equals("E1") || rev_coor.equals("D1")){
		if (servidor_ok){
			menos(null);
		}
    }
	//Boton Gerente en HOME
	if (rev_coor.equals("A8")){
		if (!activaAcumulado){
			if ((tyf==1 && visOrdenTP==0) || (tyf==0 && visOrdenTP==1)){
				conf(null);
			}
			/*if (tyf==0 && visOrdenTP==1){
				conf(null);
			}*/
		}

    }
}



public void encuentra_tck(String cap){
	
	d1 = cap.substring(1,3);
	//Toast.makeText(this, "IMPRIME TICKET",	Toast.LENGTH_SHORT).show();
	//msj(">IMPRIME TICKET>2>3>4>5>6>7>");
	d3="TI";
	//ksoap(d3,d1);
	//MSJ=0;
	//mensajes("");
	gen_xml();
	textView1.setText("Encontro TCK:"+ d1);
	myTextBox.setText("");//Borra Captura
}

public void encuentra_preset(String cap){
	String p1 = cap.substring(0,1);
	if (p1.equals("4")){
		//Toast.makeText(this, "RESET por $",	Toast.LENGTH_SHORT).show();
		msj(">PRESET de $>2>3>4>5>6>7>");
		p1 = cap.substring(1,5);
		textView1.setText("PRESET $:"+ p1);
		myTextBox.setText("");//Borra Captura
	}
	if (p1.equals("5")){
		//Toast.makeText(this, "RESET por LTS",	Toast.LENGTH_SHORT).show();
		msj(">PRESET por LTS>2>3>4>5>6>7>");
		p1 = cap.substring(1,5);
		textView1.setText("PRESET LTS:"+ p1);
		myTextBox.setText("");//Borra Captura
	}
}

public void encuentra_vehi(String cap){
	//Toast.makeText(this, "VEHICULO",	Toast.LENGTH_SHORT).show();
	msj(">VEHICULO>2>3>4>5>6>7>");
	String p1 = "Us:" + cap.substring(3,4) + cap.substring(8,12)+" Es:" + cap.substring(2,3) + cap.substring(4,8);
	textView1.setText(""+ p1);
	myTextBox.setText("");//Borra Captura
	Intent i = new Intent(this, Preset.class );
	i.putExtra("cap", cap);
    i.putExtra("tipo", "VH");
	//startActivity(i);
	//finish();
}

public void encuentra_tarjeta(String cap){
	msj(">TARJETA>2>3>4>5>6>7>");
	String p1 = "Us:" + cap.substring(3,4) + cap.substring(8,12)+" Es:" + cap.substring(2,3) + cap.substring(4,8);
	textView1.setText(""+ p1);
	myTextBox.setText("");//Borra Captura
}


public void lee_user(){
	//Lanza panatalla de Bloquero solicitando usuario.
	//Toast.makeText(this, "Envia USER.", Toast.LENGTH_LONG).show();
	if (lanza_user_sol && visOperador==0){
		Intent intent = null;
	    intent = new Intent(this, User_pass.class);
	    startActivityForResult(intent, 0);
	}
}

@Override
protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
	//EditText mEdtTarget = (EditText)findViewById(R.id.edtTarget);
    if (requestCode == 0){
    	//Toast.makeText(this, "REGRESA USER.", Toast.LENGTH_LONG).show();
    	if (data != null && resultCode == RESULT_OK) {
        	user_sol = data.getStringExtra("user");
        	String pass = data.getStringExtra("pass");
            if (user_sol != null) {
            	//Tiempo de espera para Bloquear Pantalla.
            	con_time = 10;
            	esp_con=true;
            	lanza_user_sol= true;
                esp();

            }
        }
    }
    if (requestCode == 1){
    	
    	if (data != null && resultCode == RESULT_OK) {
    		String opcion = data.getStringExtra("opcion");
        	String pos_reg = data.getStringExtra("pos");
    		
    		//Toast.makeText(this, "REGRESA COBRO BANCARIO -- OK", Toast.LENGTH_LONG).show();

    		// Creamos una carpeta "Tickets" dentro del directorio "/"
            // Con el m�todo "mkdirs()" creamos el directorio si es necesario
            File path = new File(Environment.getExternalStorageDirectory(), "Tickets");
            path.mkdirs();

            //Una vez creado disponemos de un archivo para guardar datos
            try
            {
                File ruta_sd = Environment.getExternalStorageDirectory();

                File f = new File(ruta_sd.getAbsolutePath(), "Tickets/PBDATA.txt");

                OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(f));

                String conf_cb = pos_reg+"\n";
                conf_cb += serv_cb+"\n";
                conf_cb += num_tabled+"\n";
                conf_cb += user_sol+"\n";
                conf_cb += opcion+"\n";
                
                fout.write(conf_cb);
                fout.close();
                //Toast.makeText(this, "Texto de prueba.3", Toast.LENGTH_SHORT).show();
            }
            catch (Exception ex)
            {
                //Log.e("Ficheros", "Error al escribir fichero a tarjeta SD");
            	//Toast.makeText(this, "Error al escribir fichero a tarjeta SD", Toast.LENGTH_LONG).show();
            }

    		Intent launchIntent = getPackageManager().getLaunchIntentForPackage("mx.qpay.testsdk");
    		startActivity(launchIntent);
    	    //finish();

            
        }else{
        	//Toast.makeText(this, "REGRESA COBRO BANCARIO -- CANCEL", Toast.LENGTH_LONG).show();
        }
    }
    if (requestCode == 3){
    	//Respuesta de Continuar son Correo, enviara Factura Completa
    	if (resultCode == RESULT_OK) {
    		//Toast.makeText(this, "PIDE FACTURA", Toast.LENGTH_LONG).show();
        	
        	//SOLICITA FACTURA
        	/*
			Intent i = new Intent(this, Fac_rapida.class );
		    i.putExtra("pos", pide_tck);
		    i.putExtra("tipofac", ""); 
		    i.putExtra("tickets", "");
		    i.putExtra("met_pago_f", met_pago_f);
		    i.putExtra("digitos_f", digitos_f);
		    i.putExtra("enti_banco_f", enti_banco_f);
		    i.putExtra("envia_fac", "FA");
		    i.putExtra("user_sol", user_sol);
		    //startActivity(i);
			*/
			gen_xml_st("FA");
		    textView1.setText("Espera Solicitud");
		    //finish();
        }else{
        //Respuesta de regresar, enviara a Editar al Cliente, foco en CORREO.
        	//Toast.makeText(this, "FIN FACTURA", Toast.LENGTH_LONG).show();
        }
    }
	
}

public void gen_xml_test3(){
	if (revComm){
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				gen_xml_test();
			}
		}, 5000);
	}

}

//CAMBIAR POR UN TIMER PARA OPTIMIZAR HILOS DE EJECUCION.
public void esp(){
    if (esp_con){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                rev_esp();
            }
        }, 1000);
    }
}

public void rev_esp(){
	if (con_time == 0){
		//Envia a solicitar USUARIO Y CONTRASE�A
		esp_con = false;
		lee_user();
	}else{
		//Un segundo menos para Bloquear pantalla
		con_time--;
		esp();
	}
}

public void pend_user(int esp){
	esp = esp * 1000;
	Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
        public void run() {
        	esp_con = false;
    		lee_user();
        }
    }, esp);
}

/*
public void pendientes()
{
	//1.-Pendientes Inicia revicion de pendientes mientras no este en Pausa
	if (pend_pau==0){
		//espera tiempo y ejecuta
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(t_busca);
				} catch (InterruptedException e) {
				}
				String res_pen = "0";
				//2.-Pendientes Consulta si existe pendientes en DB
				Cursor fila = bd.rawQuery("select * from config where num=1"
							+ "", null);
				 if (fila.moveToFirst()) {
					 res_pen=fila.getString(9);						
					}
				 
			    Message sms = new Message();
			    sms.obj = res_pen;
			    puente.sendMessage(sms);
			    con_pet++;
			}
		}).start();
	}
	
}
*/
/*
public void proc_pend(String procesara){
	//9.-Pendientes Revisa el tipo de Pendiente y ejecuta
	
	//Dividir una cadena en partes por |
	String[] res0 = procesara.split("\\|");
	//0>1>1>
	String ress0 = res0[1];
	if (ress0.equals("ES")){
		textView1.setText("Procesando . . .");
	}else{
		if (ress0.equals("FA")){
			//9.1-Pendientes Encontro pendiente de Factura
				/*disconnect();
				try {
				    Thread.sleep(2000);
				} catch (InterruptedException e) {
				    e.printStackTrace();
				}
				Intent j = new Intent(this, Fac_rapida_t.class );
				Toast.makeText(this, "FA",	Toast.LENGTH_SHORT).show();
				//j.putExtra("msjcon", muestra);
			    //j.putExtra("tiempo", tiempo);    
			    startActivity(j);
			    finish();
			    *
				//Pausa los pendientes
				pend_pau=1;
				M2FA("FA");
			}else{
				if (ress0.equals("PR")){
					//9.2-Pendientes Encontro pendiente de Preset
						//Pausa pendientes
						pend_pau=1;
						
						M2PR("PR");
						//Toast.makeText(this, "Ticket PRESET",	Toast.LENGTH_SHORT).show();
					}
			}
		
	}	
	//else //Toast.makeText(this, "NO Entendio",	Toast.LENGTH_SHORT).show();
	pendientes();
}

public void M2PR(String ree){
	//Toast.makeText(this, "Process:"+ ree,	Toast.LENGTH_SHORT).show();
	
	SoapObject request = new SoapObject(NAMESPACE, METHOD);
	request.addProperty( "d0" , "M2");
    request.addProperty( "d1" , Enviando);
    request.addProperty( "d2" , "");
    request.addProperty( "d3", "");

    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    envelope.dotNet = true;
    envelope.setOutputSoapObject(request);
    HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
    try
    {
    	androidHttpTransport.call(SOAPACTION, envelope);
    	String respuesta = envelope.getResponse().toString();
    	//RM1=respuesta;
    	//textView1.setText("R:" + respuesta);
    	//Toast.makeText(this, "R:"+ respuesta,	Toast.LENGTH_SHORT).show();
    	tck_pres(respuesta);
    }catch(Exception e)
    {
    	e.printStackTrace();
    	Toast.makeText(this, "NO hay Conexion M2"+ URL,	Toast.LENGTH_SHORT).show();
    }
}

public void M2FA(String ree){
	Toast.makeText(this, "Process:"+ ree,	Toast.LENGTH_SHORT).show();
	
	SoapObject request = new SoapObject(NAMESPACE, METHOD);
	request.addProperty( "d0" , "M2");
    request.addProperty( "d1" , Enviando);
    request.addProperty( "d2" , "");
    request.addProperty( "d3", "");

    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    envelope.dotNet = true;
    envelope.setOutputSoapObject(request);
    HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
    try
    {
    	androidHttpTransport.call(SOAPACTION, envelope);
    	String respuesta = envelope.getResponse().toString();
    	//RM1=respuesta;
    	//textView1.setText("R:" + respuesta);
    	Toast.makeText(this, "R:"+ respuesta,	Toast.LENGTH_SHORT).show();
    	tck_fac(respuesta);
    }catch(Exception e)
    {
    	e.printStackTrace();
    	Toast.makeText(this, "NO hay Conexion M2"+ URL,	Toast.LENGTH_SHORT).show();
    }
}

/*
public void tck_pres(String cad_pr) throws IOException{
	String tck_ori, tck_copia;
	//Dividir una cadena en partes por >
	String[] parte4 = cad_pr.split("\\>");
	cad_pr = parte4[1];
	if (cad_pr.equals("true")){
		cad_pr = parte4[3];
		//Dividir una cadena en partes por |
		res = cad_pr.split("\\|");
		
		String num_ref = res[1];
		String nom_flot = res[2];
		String calle = res[3];
		String no_ext = res[4];
		String no_int = res[5];
		String col = res[6];
		String muni = res[7];
		String estado = res[8];
		String cp = res[9];
		String rfc = res[10];
		String nom_user = res[11];
		String matricula = res[12];
		String odometro = res[13];
		String rendimiento = res[14];
		String comprobante = res[15];
		String FH_compra = res[16];
		String clie_pemex = res[17];
		String posicion = res[18];
		String desc_corta = res[19];
		String cantidad = res[20];
		String precio = res[21];
		String monto = res[22];
		String clav_produc = res[23];
		String transacc = res[24];
		String subtotal = res[25];
		String iva = res[26];
		String total = res[27];
		String letra = res[28];
				
		String text1 = "|NLEncabezado de TCK"+
			"|NS"+
			"|NRReferencia : "+num_ref+
			"|NS"+
			"|NLCliente:"+
			"|NL"+nom_flot+
			"|NS"+
			"|NLDireccion:"+						
			"|NL"+calle+
			"|NL"+no_ext+
			"|NL"+no_int+
			"|NL"+col+
			"|NL"+muni+", "+estado+" C.P. "+cp+
			"|NS"+
			"|NLR.F.C.: "+rfc+
			"|NS"+
			"|NLUsuario:"+
			"|NL"+nom_user+
			"|NLMatricula: "+matricula+
			"|NLOdometro: "+odometro+
			"|NLRendimiento (km/lt):  "+rendimiento+
			"|NS"+
			"|NRCOMPROBANTE : "+comprobante+
			"|NLHora y Fecha de compra: "+FH_compra+
			"|NS"+
			"|NLCOMPROBANTE SIN VALIDEZ FISCAL"+
			"|NS"+
			"|NLCliente PEMEX : "+clie_pemex+
			"|NS";
		String original = "|NL******** O R I G I N A L ********";
		String copia = "|NL********    C O P I A    ********";
		String text2="|NS"+
			"|NLMETODO DE PAGO"+
			"|NS"+
			"|NLEfectivo      Cheque      Tarjeta"+
			"|N-"+
			"|NLDescripcion      Cantidad  Precio  Monto"+
			"|N-"+
			"|NLPosic.: "+posicion+
			"|NL"+desc_corta+"       "+cantidad+"Lt "+precio+"     "+monto+
			"|NLClave Producto : "+clav_produc+
			"|NLTransaccion: "+transacc+
			"|NRSubtotal:"+subtotal+
			"|NRIVA:"+iva+
			"|NRTotal:"+total+
			"|NL"+letra+
			"|NS";
		
		tck_ori = text1+original+text2+"|NS"+"|NS"+"|NS";
		tck_copia = text1+copia+text2+"|NC";
		imprimir( tck_ori );
		imprimir( tck_copia );
		
    }else{
    	tck_ori = "|NL"+parte4[4];
    	imprimir( tck_ori );
    }	
	pendiente_fin();
}


public void tck_fac(String cad_tck) throws IOException{
	String text;
	int imp_qr=0;
	//Dividir una cadena en partes por >
		String[] parte4 = cad_tck.split("\\>");
		cad_tck = parte4[1];
		if (cad_tck.equals("true")){
			cad_tck = parte4[4];
			//Dividir una cadena en partes por |
			res = cad_tck.split("\\|");
			
			String ref = res[1];
			String tipo_com= res[2];
			String tipo_fac = res[3];
			folio_fis = res[4];
			String no_cer = res[5];
			String fh_cer = res[6];
			em_rfc = res[7];
			String em_nombre = res[8];
			String em_calle = res[9];
			String em_noint_ext = res[10];
			String em_col = res[11];
			String em_loc = res[12];
			String em_ref = res[13];
			String em_mun = res[14];
			String em_estado = res[15];
			String em_pais = res[16];
			String em_cp = res[17];
			String em_lug_emi = res[18];
			String em_regimen = res[19];
			String ex_loc = res[20];
			String ex_mun = res[21];
			String ex_estado = res[22];
			String ex_pais = res[23];
			String ex_cp = res[24];
			String ex_fecha = res[25];
			String no_cer2 = res[26];
			String forma_pago = res[27];
			re_rfc = res[28];
			String re_nom = res[29];
			String re_calle = res[30];
			String re_noint_ext = res[31];
			String re_col = res[32];
			String re_loc = res[33];
			String re_ref = res[34];
			String re_mun = res[35];
			String re_estado = res[36];
			String re_pais = res[37];
			String re_cp = res[38];
			String met_pago = res[39];
			String digi = res[40];
			String cantidad = res[41];
			String unidad = res[42];
			String precio = res[43];
			String importe = res[44];
			String desc = res[45];
			String ref_ori = res[46];
			String subtotal = res[47];
			String ieps = res[48];
			String impuesto = res[49];
			total = res[50];
			String letra = res[51];
			String moneda = res[52];
			String cadena = res[53];
			String sat_sello_cfdi = res[54];
			String sat_sello_sat = res[55];
			String tar = res[56];
			String leyenda = res[57];
			
		
			text = "|NLEncabezado de FACTURA"+
						"|NS"+
						"|NRREFERENCIA:"+ref+
						"|NS"+
						"|NLTIPO DE COMPROBANTE: "+tipo_com+
						"|NS"+
						"|NL"+tipo_fac+
						"|NS"+
						"|NLFOLIO FISCAL:"+
						"|NL"+folio_fis+
						"|NS"+
						"|NLNO SERIE DEL CERTIFICADO DEL SAT:"+
						"|NL"+no_cer+
						"|NS"+
						"|NLFECHA Y HORA DE CERTIFICACION:"+
						"|NL"+fh_cer+
						"|NS"+
						"|NLEMISOR:"+
						"|NL"+em_rfc+
						"|NL"+em_nombre+
						"|NL"+em_calle+
						"|NL"+em_noint_ext+
						"|NL"+em_col+
						"|NL"+em_loc+
						"|NL"+em_ref+
						"|NL"+em_mun+
						"|NL"+em_estado+
						"|NL"+em_pais+
						"|NLC.P.:"+em_cp+
						"|NS"+
						"|NLLUGAR EXPEDICION:"+
						"|NL"+em_lug_emi+
						"|NS"+
						"|NLREGIMEN FISCAL:"+
						"|NL"+em_regimen+
						"|NS"+
						"|NLEXPEDIDO EN:"+
						"|NL"+ex_loc+
						"|NL"+ex_mun+
						"|NL"+ex_estado+
						"|NL"+ex_pais+
						"|NLC.P.:"+ex_cp+
						"|NS"+
						"|NLFECHA EXPEDICION: "+ex_fecha+
						"|NS"+
						"|NLCertificado: "+no_cer2+
						"|NS"+
						"|NLForma de pago:"+
						"|NL"+forma_pago+
						"|NS"+
						"|NLR.F.C.:"+re_rfc+
						"|NL"+re_nom+
						"|NL"+re_calle+
						"|NL"+re_noint_ext+
						"|NL"+re_col+
						"|NL"+re_loc+
						"|NL"+re_ref+
						"|NL"+re_mun+
						"|NL"+re_estado+
						"|NL"+re_pais+
						"|NL"+"C.P.:"+re_cp+
						"|NS"+
						"|NL"+met_pago+
						"|NS"+
						"|NLCANTIDAD U.MEDIDA  PRECIO       IMPORTE"+
						"|N-"+
						"|NL"+cantidad+unidad+precio+importe+
						"|NL"+desc+
						"|NLReferencia Original: "+ref_ori+
						"|N-"+
						"|NLSUBTOTAL:"+subtotal+
						"|NLIVA"+impuesto+
						"|NLTOTAL:"+total+
						"|NS"+
						"|NL"+letra+moneda+
						"|NS"+
						"|NLCADENA ORIGINAL DEL COMPLEMENTO DE CERTIFICACION DIGITAL DEL SAT"+
						"|NL"+cadena+
						"|NS"+
						"|NLSELLO DIGITAL DEL CFDI:"+
						"|NL"+sat_sello_cfdi+
						"|NS"+
						"|NLSELLO DEL SAT:"+
						"|NL"+sat_sello_sat+
						"|NL"+tar+
						"|NS"+
						"|NL"+leyenda+
						"|NS";
			imp_qr=1;
	    }else{
	    	text = "|NL"+parte4[4];
	    	imp_qr=0;
	    }
		
    //textView1.setText(text);
    imp_fac(text, imp_qr);
}

/*
public void imp_fac(String cadena1, int imp_qr) throws IOException {
	 if (acti == 1){
	//textView1.setText("Imprime1");

	 int max,conta, conta2;
	 byte[] envia;
	 String opti;
	 conta=0;
	 String compara="";
	 String pipe="|";
	     
	 max=cadena1.trim().length();
	 while (conta < max){
		//textView1.setText("Imprime2");
		opti = cadena1.substring(conta);
		conta2 = conta + 1;
		compara = cadena1.substring(conta, conta2);

		if (compara.equals(pipe))
		{
			//textView1.setText("Imprime3");
		   //Alinear Impresion
	       	 outStream.write(27); //esc
	       	 outStream.write('a'); //a
	       	 outStream.write(0); //0=Izquierda, 1=centrado, 2=derecha 
	       //FIN Alinear Impresion
			conta=conta+2;
			conta2 = conta + 1;
			compara = cadena1.substring(conta, conta2);
			if (compara.equals("L"))
			{
				outStream.write(10);//Nueva de Linea
				//Alinear Impresion
				outStream.write(27); //esc
				outStream.write('a'); //a
				outStream.write(0); //0=Izquierda, 1=centrado, 2=derecha 
				//FIN Alinear Impresion
			}else{
				if (compara.equals("S"))
   			{
			    	//Salto de Linea
			      	 outStream.write(27);
			      	 outStream.write('d');
			      	 outStream.write(1); //Numero se saltos
			      	//FIN Salto linea
   			}else{
   				if (compara.equals("R"))
       			{
   					outStream.write(10);//Nueva de Linea
   			       	//Alinear Impresion
   			       	 outStream.write(27); //esc
   			       	 outStream.write('a'); //a
   			       	 outStream.write(2); //0=Izquierda, 1=centrado, 2=derecha 
   			       //FIN Alinear Impresion
   				}else{
   					if (compara.equals("-"))
   					{
   						outStream.write(10);//Nueva de Linea
   				    	 conta2=0;
   			    	  	 while (conta2 < 42){
   			    	  		opti = "-";
   			    	  		 envia = opti.getBytes();
   			         		 outStream.write(envia[0]);
   			    	 		 conta2++;
   			    	 		}
   					}else{
   						Toast.makeText(this, "Ticket con ERROR",
   		     					Toast.LENGTH_SHORT).show();
   					}
   				}
				}
			}
			conta++;
		}else{
  		 envia = opti.getBytes();
   		 outStream.write(envia[0]);
   		 conta++;
		}
		

	 }
	 if (imp_qr==1){
		generaqr_fac();
	 }else{
		//Salto de Linea
	 	 outStream.write(27);
	 	 outStream.write('d');
	 	 outStream.write(5); //Numero se saltos
	 	//FIN Salto linea 
		 
	 }
	 
	//Salto de Linea
	 outStream.write(27);
	 outStream.write('d');
	 outStream.write(8); //Numero se saltos
	//FIN Salto linea     	 

	 //Cortar papel
	outStream.write(29);
	outStream.write(86);
	outStream.write(0);
	//FIN Cortar papel
	//disconnect();
	 }

}
*/

/*
public void guarda_conf(){
	
	File path = new File(Environment.getExternalStorageDirectory(), "Tickets");
    path.mkdirs();

    //Una vez creado disponemos de un archivo para guardar datos
    try
    {
        File ruta_sd = Environment.getExternalStorageDirectory();

        File f = new File(ruta_sd.getAbsolutePath(), "Tickets/ePOS_conf.txt");

        OutputStreamWriter fout =
                new OutputStreamWriter(
                        new FileOutputStream(f));

        //String conf_cb = servidor+"\n";
        fout.write(conf_cb);
        fout.close();
        //Toast.makeText(this, "Texto de prueba.3", Toast.LENGTH_SHORT).show();
    }
    catch (Exception ex)
    {
        Log.e("Ficheros", "Error al escribir fichero a tarjeta SD");
    }
    
}//
*/
public void pendiente_fin(){
	pend_pau=0;
	ContentValues registro = new ContentValues();
	registro.put("pend", "0");
		
	int cant = bd.update("config", registro, "num=1", null);
	if (cant == 1){
		//Toast.makeText(this, "Ticket a Pendientes", Toast.LENGTH_SHORT).show();
	}
	else{
		//Toast.makeText(this, "NO", Toast.LENGTH_SHORT).show();
	}

}


/*
public void generaqr_fac() throws IOException{
	 //Find screen size
	 WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
	 Display display = manager.getDefaultDisplay();
	 Point point = new Point();
	 display.getSize(point);
	 int width = point.x;
	 int height = point.y;
	 smallerDimension = width < height ? width : height;
	 smallerDimension = smallerDimension * 3/4;
	 String qrInputText = "?re="+em_rfc+"&rr="+re_rfc+"="+total+"&id="+folio_fis;
	 Log.v(LOG_TAG, qrInputText);

	 //Encode with a QR Code image
	 QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrInputText, 
	           null, 
	           Contents.Type.TEXT,  
	           BarcodeFormat.QR_CODE.toString(), 
	           smallerDimension);
	 try {
	  bitmap1 = qrCodeEncoder.encodeAsBitmap();
	  bitmap2 = Bitmap.createScaledBitmap(bitmap1,(int)(bitmap1.getWidth()*0.6), (int)(bitmap1.getHeight()*0.6), true);
	  //bitmap = Bitmap.createScaledBitmap(bitmap2,(int)(bitmap2.getWidth()*0.5), (int)(bitmap2.getHeight()*0.5), true);
	  bitmap = bitmap2;

	  imprimeqr();
	 } catch (WriterException e) {
	  e.printStackTrace();
	 }
}
	*/
/*
public void mensaje_M3()
{	
	//5.-Pendientes Segundo plano de Solicitud M3
	if (URL.length()>11){
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				String respuesta = "";
				SoapObject request = new SoapObject(NAMESPACE, METHOD);
			    request.addProperty( "d0" , "M3");
			    request.addProperty( "d1" , Enviando);
			    request.addProperty( "d2" , "");
			    request.addProperty( "d3", "");

			    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			    envelope.dotNet = true;
			    envelope.setOutputSoapObject(request);
			    HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
			    try
			    {
			    	androidHttpTransport.call(SOAPACTION, envelope);
			    	//6.-Pendientes Solo obtiene que tipo de pendiente existe
			    	respuesta = envelope.getResponse().toString();
			    	//t_busca=5*1000;
			    	//busca_servidor(1);
			    	//Toast.makeText(this, mensaje+ " Conexion lista"+ respuesta,	Toast.LENGTH_SHORT).show();    	
			    }catch(Exception e)
			    {
			    	e.printStackTrace();
			    	//t_busca=10*1000;
			    	//busca_servidor(0);
			    	//Toast.makeText(this, mensaje+" NO hay Conexion Disponible" ,Toast.LENGTH_SHORT).show();
			    }
			    Message sms = new Message();
			    sms.obj = respuesta;
			  //7.-Pendientes Envia respuesta tipo de pendiente
			    puente2.sendMessage(sms);	
			}
		}).start();
		
	}else{
		textView1.setText("No se tiene servidor");
		t_busca=10000;
	}
}
*/

public void Leedb(){
	//Actualiza la version de VeriBox
	ContentValues registro = new ContentValues();
	version = getResources().getString(R.string.app_ver);
	registro.put("version", version);
	bd.update("config", registro, "num=1", null);

	//Recupera los datos de configuracion.
	Cursor fila = bd.rawQuery("select * from config where num=1"
			+ "", null);
	if (fila.moveToFirst()) {
		URL=fila.getString(5);
		serv_cb = URL;
		mac_serial=fila.getString(7);
		if (URL.length()>11){
			URL = "http://" + URL + "/Veribox/Veribox.php";
			num_tabled=fila.getInt(2);
			mac=fila.getString(3);
			version=fila.getString(4);
			imp_ext=fila.getInt(6);
			//mac_serial=fila.getString(7);
			nomad=fila.getString(8);
			vista=fila.getInt(10);
			rango_pos_ini=fila.getInt(11);
			rango_pos_fin=fila.getInt(12);

			visAcumula=fila.getInt(15);
			visTienda=fila.getInt(21);
			visTickets=fila.getInt(22);
			visOperador=fila.getInt(23);
			visMepagoT=fila.getInt(24);
			visOrdenTP=fila.getInt(26);
			visTckCobro=fila.getInt(32);
			
			String extra1=fila.getString(27);
			if (extra1.equals("FFFFFFFF"))
			servidor_ok = true;

			intentos=1;
			Enviando=">"+num_tabled+">"+mac+">"+version+">"+mac_serial+">"+nomad+">"+intentos+">";
			int par1 = rango_pos_fin-rango_pos_ini+1;
			float par2 = (float) par1 / vista;
			par2 = (float) (par2 + 0.26);
			pag = Math.round(par2);
			String pag_s = Integer.toString(pag);	
			if (pag == 1){
				b_bk.setVisibility(View.INVISIBLE); //Boton Pag atras
				b_fr.setVisibility(View.INVISIBLE); //Boton Pag adelante
			}else{
				b_bk.setVisibility(View.INVISIBLE); //Boton Pag atras
			}

			//Toast.makeText(this, "MAC_SERIAL " + mac_serial + "-" + imp_ext ,Toast.LENGTH_SHORT).show();	
		}else{
			//Toast.makeText(this, "NO se tiene servidor CONFIGURADO",Toast.LENGTH_SHORT).show();
			msj(">NO se tiene servidor\nCONFIGURADO>2>3>4>5>6>7>");
		}
			
	} else{
		//Toast.makeText(this, "NO lee DB", Toast.LENGTH_SHORT).show();
	}
}

public void vista(){
	if (pag != 1){
		//Esto restringue a solo una pagina, en algun momento se pidio asi.
		/*
		if (cont_pag == 1){
			b_bk.setVisibility(View.INVISIBLE); //Boton Pag atras
			b_fr.setVisibility(View.VISIBLE); //Boton Pag adelante
		}else{
			b_bk.setVisibility(View.VISIBLE); //Boton Pag atras
			b_fr.setVisibility(View.INVISIBLE); //Boton Pag adelante
		}
		*/
		if (cont_pag<pag){
			b_fr.setVisibility(View.VISIBLE); //Boton Pag adelante
		}else{
			b_fr.setVisibility(View.INVISIBLE); //Boton Pag adelante
		}
		if (cont_pag == 1){
			b_bk.setVisibility(View.INVISIBLE); //Boton Pag adelante
		}else{
			b_bk.setVisibility(View.VISIBLE); //Boton Pag adelante
		}
	}
	//Maximo de paginas 
	textView1.setText("Pagina:"+cont_pag+" de "+ pag);
	String rango_pos_ini_s1= Integer.toString(rango_pos_ini);
	String rango_pos_ini_s2= Integer.toString(rango_pos_ini+1);
	String rango_pos_ini_s3= Integer.toString(rango_pos_ini+2);
	String rango_pos_ini_s4= Integer.toString(rango_pos_ini+3);
	//Toast.makeText(this, "Mostrara: "+ vista,
		//	Toast.LENGTH_SHORT).show();	
	switch (vista) {
	case 1:
		btv1.setVisibility(View.VISIBLE);//1 vista
		btv1.setText(rango_pos_ini_s1);
		val_boton1=rango_pos_ini_s1;
		break;
    case 2:
    	btv2.setVisibility(View.VISIBLE);//2 vista
    	btv2.setText(rango_pos_ini_s1);
    	val_boton1=rango_pos_ini_s1;
    	if((rango_pos_ini+1)<=rango_pos_fin){
    		btv3.setVisibility(View.VISIBLE);//2 vista
        	btv3.setText(rango_pos_ini_s2);
        	val_boton2=rango_pos_ini_s2;	
    	}else
    		btv3.setVisibility(View.INVISIBLE);//Oculta Boton
    	break;
    case 4:
    	btv4.setVisibility(View.VISIBLE);//1 vista
    	btv4.setText(rango_pos_ini_s1);
    	val_boton1=rango_pos_ini_s1;
    	if((rango_pos_ini+1)<=rango_pos_fin){
    		btv5.setVisibility(View.VISIBLE);//1 vista
        	btv5.setText(rango_pos_ini_s2);
        	val_boton2=rango_pos_ini_s2;
    	}else
    		btv5.setVisibility(View.INVISIBLE);//Oculta Boton
    	
    	if((rango_pos_ini+2)<=rango_pos_fin){
    		btv6.setVisibility(View.VISIBLE);//1 vista
        	btv6.setText(rango_pos_ini_s3);
        	val_boton3=rango_pos_ini_s3;
    	}else
    		btv6.setVisibility(View.INVISIBLE);//Oculta Boton
    	
    	if((rango_pos_ini+3)<=rango_pos_fin){
    		btv7.setVisibility(View.VISIBLE);//1 vista
        	btv7.setText(rango_pos_ini_s4);
        	val_boton4=rango_pos_ini_s4;
    	}else
    		btv7.setVisibility(View.INVISIBLE);//Oculta Boton    	
    	break;
	//default:
	}	
}

public void mas (View view){
	
	if (cont_pag<pag){
		rango_pos_ini+=vista;		
		cont_pag++;
		vista();
		//textView1.setText("Pagina:"+cont_pag+" de "+ pag);
	}
}

public void menos (View view){
	if (cont_pag>1){
		rango_pos_ini-=vista;
		cont_pag--;
		vista();
		//textView1.setText("Pagina:"+cont_pag+" de "+ pag);
	}
}


/*
public void mensajes(String cadena){
	
		//Envia mensajes a servidor secuencialmente	
		if (MSJ==3){
		}
		//Mensaje DATOS-Tranzaccion esperamos la respuesta con los datos
		if (MSJ==2){
			textView1.setText("Solicitud Correcta.");
			msj(cadena);
			pend_user(2);
		}
		//Mensaje DATOS-Datos a procesar
		if (MSJ==1){
			String[] res0 = cadena.split("\\>");
			//false>NO DISPONIBLE. M0>3>3>4>5>6>7>
			String res_val = res0[0];
			if (res_val.equals("false")){
				msj(">NO DISPONIBLE>2>3>4>5>6>7>");
				pend_user(2);
			}else{
				int res_num = 0;
				try {
					res0 = cadena.split("\\>");
					//01>E0:76:D0:41:69:BA>1
					String ress0 = res0[2];
					res_num = NumberFormat.getInstance().parse(ress0).intValue();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (res_num == 1){
					MSJ=2;
					//String Datos = "";
					String DatosTransa = "";
					envia("M1",Datos,DatosTransa);
				}
			}
		}
		//Mensaje INICIAL-Quien solicita la infomacion
		if (MSJ==0){
			//Con esta valida si hay una respuesta aun pendiente de M0
			if(pausa_men){
				pausa_men = false;
				apa_bot(false);
				
				textView1.setText("Enviando solicitud...");
				MSJ=1;
				Datos = ">"+d3+">"+d1+">";
				DatosTransa = ">"+imp_ext+">"+"0900"+">"+"0000"+">";
				envia("M0",Datos,DatosTransa);
			}
		}
}
*/

	public void res_ser(String xml){
		//Toast.makeText(this, xml,Toast.LENGTH_SHORT).show();

		String busca1 = "mensaje-tipo";
		String busca2 = "tipo";
		String dato=regresa_xml(xml, busca1,busca2);

		switch (dato) {
			case "TS":
					busca1 = "com";
					busca2 = "res-test";
					dato=regresa_xml(xml, busca1,busca2);
					if (dato.equals("true")) {
						busca2 = "tester";
						dato = regresa_xml(xml, busca1, busca2);
						msj(">Comunicacion con Servidor Correcta,\nsi no imprime comprobante verifique\nla configuracion de la impresora>4>1>4>5>6>7>");
						//msj(">\nComunicacion con Servidor es Correcta,\nsi no imprime comprobante verfique la\nconfiguracion de la impresora>3>3>4>5>6>7>");
						pend_user(2);
					}
				break;
			case "TI":
					busca1 = "ticket";
					busca2 = "res";
					dato=regresa_xml(xml, busca1,busca2);
					if (dato.equals("true")){
						//Toast.makeText(this, "R=TRUE",Toast.LENGTH_SHORT).show();
						busca1 = "display";
						busca2 = "dato-impresiond";
						dato=regresa_xml(xml, busca1,busca2);
						msj(">"+dato+">2>3>4>5>6>7>");
						pend_user(2);
					}else{
						//msj(">Peticion incorrecta.>2>3>4>5>6>7>");
						busca1 = "display";
						busca2 = "dato-impresiond";
						dato=regresa_xml(xml, busca1,busca2);
						msj(">"+dato+">2>3>4>5>6>7>");
						pend_user(2);
					}
				break;
			case "FA":
				//msj(">Respuesta FACTURA>2>3>4>5>6>7>");
				busca1 = "factura";
				busca2 = "resf";
				dato=regresa_xml(xml, busca1,busca2);
				if (dato.equals("true")){
					//Toast.makeText(this, "R=TRUE",Toast.LENGTH_SHORT).show();
					//busca1 = "display";
					//busca2 = "dato-impresiond";
					//dato=regresa_xml(xml, busca1,busca2);
					//msj(">Envia a Facturar>2>3>4>5>6>7>");
					factura_sig("FA");
					//pend_user(2);
				}else{
					//msj(">Peticion incorrecta.>2>3>4>5>6>7>");
					busca1 = "display";
					busca2 = "dato-impresiond";
					dato=regresa_xml(xml, busca1,busca2);
					msj(">"+dato+">2>3>4>5>6>7>");
					pend_user(2);
				}

				break;
			case "PB":

				busca1 = "pago-bancario";
				busca2 = "respg";
				dato=regresa_xml(xml, busca1,busca2);
				if (dato.equals("true")){
					//Toast.makeText(this, "R=TRUE",Toast.LENGTH_SHORT).show();
					//busca1 = "display";
					//busca2 = "dato-impresiond";
					//dato=regresa_xml(xml, busca1,busca2);
					//msj(">Envia a PAGO BANCARIO>2>3>4>5>6>7>");
					pago_sig();
					//pend_user(2);
				}else{
					//msj(">Peticion incorrecta.>2>3>4>5>6>7>");
					busca1 = "display";
					busca2 = "dato-impresiond";
					dato=regresa_xml(xml, busca1,busca2);
					msj(">"+dato+">2>3>4>5>6>7>");
					pend_user(2);
				}
				break;

			case "PR":
				busca1 = "preset";
				busca2 = "respr";
				dato=regresa_xml(xml, busca1,busca2);
				if (dato.equals("true")){
					//Toast.makeText(this, "R=TRUE",Toast.LENGTH_SHORT).show();
					//busca1 = "display";
					//busca2 = "dato-impresiond";
					//dato=regresa_xml(xml, busca1,busca2);
					//msj(">Envia a PRESET>2>3>4>5>6>7>");
					flotilla_sig();
					//pend_user(2);
				}else{
					//msj(">Peticion incorrecta.>2>3>4>5>6>7>");
					busca1 = "display";
					busca2 = "dato-impresiond";
					dato=regresa_xml(xml, busca1,busca2);
					msj(">"+dato+">2>3>4>5>6>7>");
					pend_user(2);
				}
				break;

			//Version 2.0-0 MSM 13/Mar/2018
			//Cambio para opcion de Acumulado de Venta
			case "AC":
				busca1 = "acumulado";
				busca2 = "resac";
				dato=regresa_xml(xml, busca1,busca2);
				if (dato.equals("true")){
					home(null);
					factura_sig("AC");
				}else{
					busca1 = "display";
					busca2 = "dato-impresiond";
					dato=regresa_xml(xml, busca1,busca2);
					msj(">"+dato+">2>3>4>5>6>7>");
					pend_user(2);
				}
				break;
		}

		if (servidor_ok){
			//Regresa a pantalla Principal (HOME)
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					// acciones que se ejecutan tras los milisegundos
					home(null);
				}
			}, 1000);
		}
	}



	public void factura_sig(String tipo){
		Intent i = new Intent(this, Fac_rapida.class );
		i.putExtra("pos", pide_tck);
		i.putExtra("tipofac", poscarga);
		i.putExtra("tickets", "");
		i.putExtra("envia_fac", tipo);
		i.putExtra("user_sol", user_sol);
		//Toast.makeText(this, "1 "+user_sol, Toast.LENGTH_LONG).show();
		startActivity(i);
	}

	public void pago_sig(){
		Intent i = new Intent(this, Fac_rapida.class );
		i.putExtra("pos", pide_tck);
		i.putExtra("envia_fac", "PB");
		i.putExtra("tickets", "");
		i.putExtra("met_pago_f", met_pago_f);
		i.putExtra("digitos_f", digitos_f);
		i.putExtra("met_pago_f", met_pago_f);
		i.putExtra("enti_banco_f", enti_banco_f);
		i.putExtra("user_sol", user_sol);
		startActivity(i);
	}

	public void flotilla_sig(){
		Intent i = new Intent(this, Flotilla1.class );
		i.putExtra("pos", pide_tck);
		i.putExtra("user_sol", user_sol);
		startActivity(i);
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
	            envi_retun="NO Encontrado";
	        }
	    }else{
	    	envi_retun="NO Encontrado";
	    }
	    return envi_retun;
	}

	//Funcion que solicita el estado de la posicion para Factura, Pago Bancario y Flotilla.
	public void gen_xml_st(String tipo){

		if(pausa_men){
			pausa_men = false;
			apa_bot(false);

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
					"   <posicion pos=\""+pide_tck+"\" ></posicion>\n"+
					"   <usuario user_sol=\""+user_sol+"\"></usuario>\n";//+

			switch (tipo) {
				case "FA":
					text +=  "   <factura tipo_solicitu=\"ST\" tipo_clien=\"\" cliente=\"\" tipo_compro=\"\" tipo_pago=\"\" enti_banco_f=\"\" dig_tarjeta=\"\"  nombre=\"\" calle=\"\" num_ex=\"\" num_int=\"\" colonia=\"\" localidad=\"\" referencia=\"\" municipio=\"\" estado=\"\""+
							"   pais=\"\" cp=\"\" telefono=\"\" correos=\"\" num_ref_f=\"\" >\n"+
							"   </factura>\n";
					break;
				case "PB":
					text += "<pago-bancario solicito=\"true\" processed=\"\" tranzaccion=\"ST\" guardar=\"-\">\n"+
							"<comprobante tipo_compro=\"T\" tipo_cliepb=\"\" clientepb=\"\" >\n"+
							"</comprobante>\n"+
							"<registra bancoEmisor=\"\" cardNumber=\"\" hora_fecha=\"\" serieTDS=\"\" aid=\"\" error_description=\"\" transacc=\"\" monto=\"\" arqc=\"\" codigoAprobacion=\"\" marcaTarjeta=\"\" vigencia=\"\" terminalID=\"\" numeroControl=\"\" referenciaBanco=\"\" tvr=\"\" tsi=\"\" apn=\"\" afiliacion=\"\" ></registra>\n"+
							"</pago-bancario>\n";
					break;

				case "PR":
					text += "	<preset sol=\"ST\" usuario=\"\" nip=\"\" monto=\"\" monto_preset=\"\" odome_reg=\"\" tipo_venta=\"\" usuario_trj=\"\" ></preset>\n";
					break;

				case "AC":
					text += "   <acumulado sol=\"ST\" usuario_flo=\"\" ></acumulado>\n";
					break;
			}
			text +=	"   </datos>\n"+
					"</peticion>";

			envia(text);
		}
	}

	public void gen_xml(){
		
		if(pausa_men){
			pausa_men = false;
			apa_bot(false);
			
			String[] partes = Enviando.split("\\>");
		    String num_tabled = partes[1];
		    String mac = partes[2];
		    String version = partes[3];
		    String mac_serial = partes[4];
		    String nomad = partes[5];
		    String intentos = partes[6];
		    
			final String text = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"+
		            "<peticion>\n"+
		            "   <mensaje-tipo tipo=\""+d3+"\"></mensaje-tipo>\n"+
		            "	<envio tds=\""+num_tabled+"\" mac=\""+ mac+"\" version=\""+version+"\" mac_serial=\""+mac_serial+"\" nomad=\""+nomad+"\" intentos=\""+intentos+"\"></envio>\n"+
		            "   <datos>\n"+
		            "   <posicion pos=\""+d1+"\"></posicion>\n"+
		            "   <usuario user_sol=\""+user_sol+"\"></usuario>\n"+
					"   <ticket tipo=\""+imp_ext+"\" tipo_pago=\"01\" enti_banco_t=\"\" dig_tarjeta=\"\" > </ticket>\n"+
		            "   </datos>\n"+
		            "</peticion>";
			
			envia(text);
		}
	}

	public void envia(final String salida){
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
	
	public void gen_xml_test(){
		lanza_user_sol = false;
		if(pausa_men){
			pausa_men = false;
			apa_bot(false);
			
			String[] partes = Enviando.split("\\>");
		    String num_tabled = partes[1];
		    String mac = partes[2];
		    String version = partes[3];
		    String mac_serial = partes[4];
		    String nomad = partes[5];
		    String intentos = partes[6];
		    
			final String text = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"+
		            "<peticion>\n"+
		            "   <mensaje-tipo tipo=\"TS\"></mensaje-tipo>\n"+
		            "	<envio tds=\""+num_tabled+"\" mac=\""+ mac+"\" version=\""+version+"\" mac_serial=\""+mac_serial+"\" nomad=\""+nomad+"\" intentos=\""+intentos+"\"></envio>\n"+
		            "   <datos>\n"+
		            "   <posicion pos=\"99\"></posicion>\n"+
					"   <com res-test=\"true\" tester=\"VeriBOX"+num_tabled+"\"></com>\n"+
		            "   </datos>\n"+
		            "</peticion>";
			
			envia(text);
		}
		
	}

public void apa_bot(boolean act){
	btv1.setEnabled(act);
	btv2.setEnabled(act);
	btv3.setEnabled(act);
	btv4.setEnabled(act);
	btv5.setEnabled(act);
	btv6.setEnabled(act);
	btv7.setEnabled(act);
	
}

/*
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
*/

/*
public void ksoap(String d1, String d2){
	pend_pau=1;
	Datos = ">"+d1+">"+d2+">";
	DatosTransa = ">"+imp_ext+">"+"0900"+">"+"0000"+">";
    //Inicio de envios de Datos para procesar peticion
    SoapObject request = new SoapObject(NAMESPACE, METHOD);
    
    request.addProperty( "d0" , "M0");
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
    	String respuesta = envelope.getResponse().toString();
    	//Toast.makeText(this, "Conexion lista.1"+ respuesta,	Toast.LENGTH_SHORT).show();
    	//textView1.setText("R:" + respuesta);
    	M1(respuesta);
    }catch(Exception e)
    {
    	e.printStackTrace();
    	//msj(">>>>>>Sin Contacto del Servidor");
    	textView1.setText("Sin Respuesta m0");
    	//Toast.makeText(this, "NO hay Conexion Disponible M0",Toast.LENGTH_SHORT).show();
    }
}
*/

public void M1(String res) throws ParseException{
	//Dividir una cadena en partes por |
	String[] res0 = res.split("\\>");
	//0>1>1>
	String ress0 = res0[2];
	int res_num = NumberFormat.getInstance().parse(ress0).intValue();
	if (res_num == 1){
		
		SoapObject request = new SoapObject(NAMESPACE, METHOD);
		request.addProperty( "d0" , "M1");
	    request.addProperty( "d1" , Enviando);
	    request.addProperty( "d2" , "");
	    request.addProperty( "d3", "");

	    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    envelope.dotNet = true;
	    envelope.setOutputSoapObject(request);
	    HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
	    try
	    {
	    	androidHttpTransport.call(SOAPACTION, envelope);
	    	String respuesta = envelope.getResponse().toString();
	    	RM1=respuesta;
	    	//Envia a mostrar el mensaje en PANTALLA
	    	//msj(">TCK>3>3>4>5>6>7>");
	    	msj(RM1);
	    	//Toast.makeText(this, "Conexion lista 1"+ respuesta,Toast.LENGTH_SHORT).show();
	    	textView1.setText("");
	    	//M2();
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    	//textView1.setText("Sin Respuesta M1");
	    	//Toast.makeText(this, "NO hay Conexion M1",	Toast.LENGTH_SHORT).show();
	    }
		
	}
}

public void M2(){
	
SoapObject request = new SoapObject(NAMESPACE, METHOD);
    
    request.addProperty( "d0" , "M2");
    request.addProperty( "d1" , Enviando);
    request.addProperty( "d2" , "");
    request.addProperty( "d3", "");

    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    envelope.dotNet = true;
    envelope.setOutputSoapObject(request);
    HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
    try
    {
    	androidHttpTransport.call(SOAPACTION, envelope);
    	String respuesta = envelope.getResponse().toString();
    	//Toast.makeText(this, "Conexion lista.2"+ respuesta,	Toast.LENGTH_SHORT).show();
    	textView1.setText("R:" + respuesta);
    	//tck(respuesta);
    }catch(Exception e)
    {
    	e.printStackTrace();
    	//msj(">>>>>>Sin Contacto del Servidor");
    	//textView1.setText("Sin Respuesta m0");
    	//Toast.makeText(this, "NO hay Conexion DisponibleM2",Toast.LENGTH_SHORT).show();
    }
}


public void tck(String cad_tck) throws IOException{
	if(imp_ext==1){
		textView1.setText("Imp a  Externa");
		
		String original, Venta, Transacc, FechaFmt, ClientePemex, Clavepemex, posicion, Desc_corta, Volumen, Precio_siva, Monto, MontoIVA, Dinero, letra;
		textView1.setText("entro a TKC");
		//Dividir una cadena en partes por >
			String[] res = cad_tck.split("\\>");
			cad_tck = res[3];
			//Dividir una cadena en partes por |
			res = cad_tck.split("\\|");
			original = res[0];
			Venta = res[1];
			Transacc = res[2];
			FechaFmt = res[3];
			ClientePemex = res[4];
			Clavepemex = res[5];
			posicion = res[6];
			Desc_corta = res[7];
			Volumen = res[8];
			Precio_siva = res[9];
			Monto = res[10];
			MontoIVA = res[11];
			Dinero = res[12];
			letra = res[13];
			
		
	    String text = "|NLReferencia : "+ Venta+ 
						"|NS"+
						"|NRTransaccion : "+ Transacc+
						"|NS"+
						"|NLHora y Fecha de compra: "+ FechaFmt+
						"|NS"+
						"|NLCliente PEMEX : "+ ClientePemex+
						"|NLClave Producto : "+ Clavepemex+
						"|NS";
	    
	    if (original.equals("1")){
	    	text+="|NL******** O R I G I N A L ********";
	    	clave_esp = res[14];
	    }else{
	    	text+="|NL********    C O P I A    ********";
	    	clave_esp = "";
	    }
	    		text+="|NS"+
						"|NLMETODO DE PAGO"+
						"|NS"+
						"|NLEfectivo      Cheque      Tarjeta"+
						"|NS"+
						"|N-"+
						"|NLDescripcion      Cantidad  Precio  Monto"+
						"|N-"+
						"|NLPosic+: "+ posicion+
						"|NL"+ Desc_corta + "      "+ Volumen+ "Lt  "+ Precio_siva+ "  "+ Monto+
						"|NS"+
						"|NS"+
						"|NS"+
						"|NRSubtotal: "+ Monto+
						"|NRIVA: "+ MontoIVA+
						"|NRTotal: "+ Dinero+
						"|NS"+
						"|NL("+ letra+ ")"+
						"|NS";
			if (original.equals("1")){
		    	text+="|NLFACTURA EN LINEA"+
						"|NLwww.erfc.com.mx"+
						"|NLIDW: "+clave_esp;
		    	clave_esp = res[14];
		    }			
	    
	    textView1.setText(text);
	    imp(text, clave_esp, original);
	    pend_pau=0;
		
	}else{
		textView1.setText("Impresion por TPV");
	}
	
	

}
	public void rev_metT(){
		if (visMepagoT==1){
			try{
				bitacora.guarda_b("Sin metodo de Pago");
			}catch (Exception e){e.getStackTrace();}

			gen_xml();
		}else{
			try{
				bitacora.guarda_b("Con metodo de pago");
			}catch (Exception e){e.getStackTrace();}

			String xml_pago1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"+
		            "<peticion>\n"+
		            "   <mensaje-tipo tipo=\""+d3+"\"></mensaje-tipo>\n"+
		            "	<envio tds=\""+num_tabled+"\" mac=\""+ mac+"\" version=\""+version+"\" mac_serial=\""+mac_serial+"\" nomad=\""+nomad+"\" intentos=\""+intentos+"\"></envio>\n"+
		            "   <datos>\n"+
		            "   <posicion pos=\""+d1+"\"></posicion>\n"+
		            "   <usuario user_sol=\""+user_sol+"\"></usuario>\n"+
					"   <ticket tipo=\""+imp_ext +"\" ";
					
					//"\" tipo_pago=\"0900\" prov_pago=\"000\"  >\n"+
					
			String xml_pago2 ="> </ticket>\n"+
		            "   </datos>\n"+
		            "</peticion>";

			//String envdgt=null;
			Intent i = new Intent(this, Metodo_pago.class );
			i.putExtra("xmp_pago1", xml_pago1);
		    i.putExtra("xmp_pago2", xml_pago2);
			i.putExtra("met_pago_f", "");
			i.putExtra("digitos_f", "");
		    i.putExtra("enti_banco_f", "");
		    i.putExtra("tipo_compro", "T");
		    i.putExtra("m_ticket", "1");
			i.putExtra("usocfdi", "");
		    startActivity(i);

			/*
			//Validar si se tiene metodo de Pago, si vacio solicita el dato y digitos.
			Intent i = new Intent(this, Metodo_pago.class );
			i.putExtra("xmp_pago1", xml_pago1);
			i.putExtra("xmp_pago2", xml_pago2);
			i.putExtra("met_pago_f", met_pago_f);
			i.putExtra("digitos_f", digitos_f);
			i.putExtra("enti_banco_f", enti_banco_f);
			i.putExtra("tipo_compro", "");
			i.putExtra("m_ticket", "0");
			i.putExtra("usocfdi", usocfdi);
			startActivity(i);
			*/
		}
		
	}
	
	public void pro_botones(){
		if(tyf==1){
			try{
				bitacora.guarda_b("Solicita Ticket");
			}catch (Exception e){e.getStackTrace();}

			//SOLICITA TICKET
			
			//Detiene tiempo de inactividad y pide usuario
			esp_con = false;
			
			d2="0";
			if(imp_ext==1){
				d2="1";	
			}
			 d1=pide_tck;
			 d3="TI";
			 //ksoap(d3,d1);
			 //MSJ=0;
			 //mensajes("");
			 //gen_xml();
			if (activaAcumulado){
				//Version 2.0-0 MSM 13/Mar/2018
				//Cambio para opcion de Acumulado de Venta
				try{
					bitacora.guarda_b("gen_xml AC");
				}catch (Exception e){e.getStackTrace();}

				gen_xml_st("AC");

			}else{
				try{
					bitacora.guarda_b("rev_merT");
				}catch (Exception e){e.getStackTrace();}

				rev_metT();
			}
		}
		if (tyf==2){
			if (visTienda==0){
				if (visOrdenTP==0){
					//Toast.makeText(this, "FACTURA 1", Toast.LENGTH_LONG).show();
					//SOLICITA FACTURA

			/*disconnect();
			 try {
				    Thread.sleep(2000);
				} catch (InterruptedException e) {
				    e.printStackTrace();
				}
			 */
					con_time = 4;			//Tiempo espera para bloqueo
					esp_con = false;		//Sigue o no esperando
					permite_salir = true;	//Permite o no salir de la interfaz actual
					/*
					Intent i = new Intent(this, Fac_rapida.class );
					i.putExtra("pos", pide_tck);
					i.putExtra("tipofac", poscarga);
					i.putExtra("tickets", "");
					i.putExtra("envia_fac", "FA");
					i.putExtra("user_sol", user_sol);
					//Toast.makeText(this, "1 "+user_sol, Toast.LENGTH_LONG).show();
					//startActivity(i);
					*/
					gen_xml_st("FA");
					textView1.setText("Espera Solicitud");

					/*
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						// acciones que se ejecutan tras los milisegundos
						home(null);
					}
				}, 2000);
				*/
					//finish();
				}

			}else{
				if (visOrdenTP==0){
					//Toast.makeText(this, "FACTURA 2", Toast.LENGTH_LONG).show();
					//SOLICITA FACTURA

			/*disconnect();
			 try {
				    Thread.sleep(2000);
				} catch (InterruptedException e) {
				    e.printStackTrace();
				}
			 */
					con_time = 4;			//Tiempo espera para bloqueo
					esp_con = false;		//Sigue o no esperando
					permite_salir = true;	//Permite o no salir de la interfaz actual
					/*
					Intent i = new Intent(this, Fac_rapida.class );
					i.putExtra("pos", pide_tck);
					i.putExtra("tipofac", poscarga);
					i.putExtra("tickets", "");
					i.putExtra("envia_fac", "FA");
					i.putExtra("user_sol", user_sol);
					//Toast.makeText(this, "1 "+user_sol, Toast.LENGTH_LONG).show();
					//startActivity(i);
					*/
					gen_xml_st("FA");
					textView1.setText("Espera Solicitud");

					/*
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						// acciones que se ejecutan tras los milisegundos
						home(null);
					}
				}, 2000);
				*/
					//finish();
				}else{
					//Toast.makeText(this, "FLOTILLA 3", Toast.LENGTH_LONG).show();
					//SOLICITA FLOTILLA
					disconnect();
					con_time = 4;			//Tiempo espera para bloqueo
					esp_con = false;		//Sigue o no esperando
					permite_salir = true;	//Permite o no salir de la interfaz actual

					/*
					Intent i = new Intent(this, Flotilla1.class );
					//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					i.putExtra("pos", pide_tck);
					i.putExtra("user_sol", user_sol);
					//startActivity(i);
					*/
					gen_xml_st("PR");
					//textView1.setText("Espera Solicitud");

					/*
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						// acciones que se ejecutan tras los milisegundos
						home(null);
					}
				}, 2000);
				*/
					//finish();
				}
			}
		}else{
		if (tyf==3){

			if (visOrdenTP==0){
				//SOLICITA PAGO BANCARIO
				/*disconnect();
				 try {
					    Thread.sleep(2500);
					} catch (InterruptedException e) {
					    e.printStackTrace();
					}
				 */
				/*
				Intent i = new Intent(this, Fac_rapida.class );
			    i.putExtra("pos", pide_tck);
			    i.putExtra("envia_fac", "PB");
			    i.putExtra("tickets", "");
			    i.putExtra("met_pago_f", met_pago_f);
			    i.putExtra("digitos_f", digitos_f);
			    i.putExtra("met_pago_f", met_pago_f);
			    i.putExtra("enti_banco_f", enti_banco_f);
			    i.putExtra("user_sol", user_sol);
			    //startActivity(i);
				*/
				gen_xml_st("PB");
			    //finish();

				con_time = 4;			//Tiempo espera para bloqueo
				esp_con = false;		//Sigue o no esperando
				permite_salir = true;	//Permite o no salir de la interfaz actual

				/*
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						// acciones que se ejecutan tras los milisegundos
						home(null);
					}
				}, 2000);
				*/
			    //pago_banca(pide_tck);
			}else{
				//SOLICITA FACTURA

				/*disconnect();
				 try {
					    Thread.sleep(2000);
					} catch (InterruptedException e) {
					    e.printStackTrace();
					}
				 */
				con_time = 4;			//Tiempo espera para bloqueo
			    esp_con = false;		//Sigue o no esperando
			    permite_salir = true;	//Permite o no salir de la interfaz actual
				/*
				Intent i = new Intent(this, Fac_rapida.class );
			    i.putExtra("pos", pide_tck);
			    i.putExtra("tipofac", poscarga);
			    i.putExtra("tickets", "");
			    i.putExtra("envia_fac", "FA");
			    i.putExtra("user_sol", user_sol);
			    //Toast.makeText(this, "1 "+user_sol, Toast.LENGTH_LONG).show();
			    //startActivity(i);
				*/
				gen_xml_st("FA");
			    textView1.setText("Espera Solicitud");

				/*
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						// acciones que se ejecutan tras los milisegundos
						home(null);
					}
				}, 2000);
				*/
			    //finish();
			}
			
			
		}else {
			if(tyf==4){
				//SOLICITA FLOTILLA
				disconnect();
				con_time = 4;			//Tiempo espera para bloqueo
			    esp_con = false;		//Sigue o no esperando 
			    permite_salir = true;	//Permite o no salir de la interfaz actual
			    
				/*
				Intent i = new Intent(this, Flotilla1.class );
				//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    i.putExtra("pos", pide_tck);
			    i.putExtra("user_sol", user_sol);    
			    //startActivity(i);
				*/
				gen_xml_st("PR");
			    //textView1.setText("Espera Solicitud");

				/*
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						// acciones que se ejecutan tras los milisegundos
						home(null);
					}
				}, 2000);
				*/
				//finish();
			}
			if(tyf==0){
				if (visTienda==0){
					if (visOrdenTP==0){
						//Toast.makeText(this, "NADA 4 ", Toast.LENGTH_LONG).show();
					}else {
						//Toast.makeText(this, "PAGO BANCARIO 5", Toast.LENGTH_LONG).show();
						con_time = 4;			//Tiempo espera para bloqueo
						esp_con = false;		//Sigue o no esperando
						permite_salir = true;	//Permite o no salir de la interfaz actual
						/*
						Intent i = new Intent(this, Fac_rapida.class );
						i.putExtra("pos", pide_tck);
						i.putExtra("envia_fac", "PB");
						i.putExtra("tickets", "");
						i.putExtra("met_pago_f", met_pago_f);
						i.putExtra("digitos_f", digitos_f);
						i.putExtra("met_pago_f", met_pago_f);
						i.putExtra("enti_banco_f", enti_banco_f);
						i.putExtra("user_sol", user_sol);
						//startActivity(i);
						*/
						gen_xml_st("PB");
						/*
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						// acciones que se ejecutan tras los milisegundos
						home(null);
					}
				}, 2000);
				*/
						//finish();
						//pago_banca(pide_tck);
					}

				}else{
					if (visOrdenTP==0){
						//Toast.makeText(this, "FLOTILLA 6", Toast.LENGTH_LONG).show();
						//Toast.makeText(this, "FLOTILLA", Toast.LENGTH_LONG).show();
						//SOLICITA FLOTILLA
						disconnect();
						con_time = 4;			//Tiempo espera para bloqueo
						esp_con = false;		//Sigue o no esperando
						permite_salir = true;	//Permite o no salir de la interfaz actual

						/*
						Intent i = new Intent(this, Flotilla1.class );
						//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						i.putExtra("pos", pide_tck);
						i.putExtra("user_sol", user_sol);
						//startActivity(i);
						*/
						gen_xml_st("PR");
						//textView1.setText("Espera Solicitud");

						/*
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						// acciones que se ejecutan tras los milisegundos
						home(null);
					}
				}, 2000);
				*/
						//finish();
					}else{
						//Toast.makeText(this, "PAGO BANCAIO 7", Toast.LENGTH_LONG).show();
						con_time = 4;			//Tiempo espera para bloqueo
						esp_con = false;		//Sigue o no esperando
						permite_salir = true;	//Permite o no salir de la interfaz actual
						/*
						Intent i = new Intent(this, Fac_rapida.class );
						i.putExtra("pos", pide_tck);
						i.putExtra("envia_fac", "PB");
						i.putExtra("tickets", "");
						i.putExtra("met_pago_f", met_pago_f);
						i.putExtra("digitos_f", digitos_f);
						i.putExtra("met_pago_f", met_pago_f);
						i.putExtra("enti_banco_f", enti_banco_f);
						i.putExtra("user_sol", user_sol);
						//startActivity(i);
						*/
						gen_xml_st("PB");
						/*
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						// acciones que se ejecutan tras los milisegundos
						home(null);
					}
				}, 2000);
				*/
						//finish();
						//pago_banca(pide_tck);
					}
				}
			}
		}
			
		}
	}

public void t1(View view) throws IOException {
	//Detiene el bloqueo por tiempo.
	esp_con=false;
	pide_tck = val_boton1;//"1";
	textView1.setText("Enviando solicitud...");
	//IGUAL A TODOS LOS bOTONES
	try{
		bitacora.guarda_b("Antes de pro_botones");
	}catch (Exception e){e.getStackTrace();}

	pro_botones();
}

public void t2(View view) {
	//Detiene el bloqueo por tiempo.
	esp_con=false;
	
	pide_tck = val_boton2;//"1";	
	textView1.setText("Enviando solicitud...");
	//Se AJustara para que apunte a la posicion de TICKET
	pro_botones();
	/*
	if(tyf==1){
		//SOLICITA TICKET
		d2="0";
		if(imp_ext==1){
			d2="1";	
		}
		 d1=pide_tck;
		 d3="TI";
		 //ksoap(d3,d1);
		 //MSJ=0;
		 //mensajes("");
		 //gen_xml();
		 rev_metT();
	}
	//Se AJustara para que apunte a la posicion de FACTURA
	if (tyf==2){
		//SOLICITA FACTURA
			Intent i = new Intent(this, fac_rapida.class );
			/*disconnect();
			 try {
				    Thread.sleep(2000);
				} catch (InterruptedException e) {
				    e.printStackTrace();
				}
			 * /
			
			con_time = 4;			//Tiempo espera para bloqueo
		    esp_con = false;		//Sigue o no esperando 
		    permite_salir = true;	//Permite o no salir de la interfaz actual
		    
		    i.putExtra("pos", pide_tck);
		    i.putExtra("tipofac", poscarga); 
		    i.putExtra("tickets", "");
		    i.putExtra("envia_fac", "FA");
		    i.putExtra("user_sol", user_sol);
		    startActivity(i);
		    textView1.setText("Espera Solicitud");
		    finish();
			
    
	}else{
		//Se AJustara para que apunte a la posicion de PAGO BANCARIO
	if (tyf==3){
		//SOLICITA PAGO BANCARIO
		/*disconnect();
		 try {
			    Thread.sleep(2500);
			} catch (InterruptedException e) {
			    e.printStackTrace();
			}
		 * /
		//Intent i = new Intent(this, Pago_ban2.class );
	    //i.putExtra("pos", pide_tck);
	    //i.putExtra("tipofac", poscarga);    
	    //startActivity(i);
		con_time = 4;			//Tiempo espera para bloqueo
		esp_con = false;		//Sigue o no esperando 
		permite_salir = true;	//Permite o no salir de la interfaz actual
		
	    pago_banca(pide_tck);
		
	}else {
		//Se AJustara para que apunte a la posicion de FLOTILLA
		//if(tyf==0){ //Sin Almacen
		if(tyf==4){
			//SOLICITA FLOTILLA
			disconnect();
			
			con_time = 4;			//Tiempo espera para bloqueo
		    esp_con = false;		//Sigue o no esperando 
		    permite_salir = true;	//Permite o no salir de la interfaz actual
		    
			Intent i = new Intent(this, Flotilla1.class );
		    i.putExtra("pos", pide_tck);
		    i.putExtra("user_sol", user_sol);
		    startActivity(i);
		    textView1.setText("Espera Solicitud");
		    finish();
		}
	}
		
	}
	*/
}



public void t3(View view) {
	//Detiene el bloqueo por tiempo.
	esp_con=false;
	pide_tck = val_boton3;//"1";	
	textView1.setText("Enviando solicitud...");
	if(tyf==1){
		d2="0";
		if(imp_ext==1){
			d2="1";	
		}
		 d1=pide_tck;
		 d3="TI";
		 //ksoap(d3,d1);
		 //MSJ=0;
		 //mensajes("");
		 gen_xml();
	}
	if (tyf==2){

		/*disconnect();
		 try {
			    Thread.sleep(2500);
			} catch (InterruptedException e) {
			    e.printStackTrace();
			}
		 */
		con_time = 4;			//Tiempo espera para bloqueo
	    esp_con = false;		//Sigue o no esperando 
	    permite_salir = true;	//Permite o no salir de la interfaz actual
		/*
		Intent i = new Intent(this, Fac_rapida.class );
	    i.putExtra("pos", pide_tck);
	    i.putExtra("tipofac", poscarga);  
	    i.putExtra("tickets", "");
	    i.putExtra("envia_fac", "FA");
	    i.putExtra("user_sol", user_sol);
	    //startActivity(i);
		*/
		gen_xml_st("FA");
	    //textView1.setText("Espera Solicitud");
	    //finish();
			
    
	}else{
	if (tyf==3){
		/*disconnect();
		 try {
			    Thread.sleep(2500);
			} catch (InterruptedException e) {
			    e.printStackTrace();
			}
			*
		Intent i = new Intent(this, Pago_ban2.class );
	    i.putExtra("pos", pide_tck);
	    //i.putExtra("tipofac", poscarga);    
	    startActivity(i);
	    textView1.setText("Espera Solicitud");
	    finish();
	    */
		con_time = 4;			//Tiempo espera para bloqueo
		esp_con = false;		//Sigue o no esperando 
		permite_salir = true;	//Permite o no salir de la interfaz actual
		
	    pago_banca(pide_tck);
		
	}else {
		//if(tyf==0){ //Sin Almacen
		if(tyf==4){
			/*disconnect();try {
			    Thread.sleep(2500);
			} catch (InterruptedException e) {
			    e.printStackTrace();
			}
			*/
			con_time = 4;			//Tiempo espera para bloqueo
		    esp_con = false;		//Sigue o no esperando 
		    permite_salir = true;	//Permite o no salir de la interfaz actual

			/*
			Intent i = new Intent(this, Flotilla1.class );
			//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    i.putExtra("pos", pide_tck);
		    i.putExtra("user_sol", user_sol);
		    //startActivity(i);
			*/
			gen_xml_st("PR");
		    textView1.setText("Espera Solicitud");
		    //finish();
		}
	}
		
	}	
}

public void t4(View view) {
	//Detiene el bloqueo por tiempo.
	esp_con=false;
	pide_tck = val_boton4;//"1";	
	textView1.setText("Enviando solicitud...");
	if(tyf==1){
		d2="0";
		if(imp_ext==1){
			d2="1";	
		}
		 d1=pide_tck;
		 d3="TI";
		 //ksoap(d3,d1);
		 //MSJ=0;
		 //mensajes("");
		 gen_xml();
	}
	if (tyf==2){

		/*disconnect();
		 try {
			    Thread.sleep(2500);
			} catch (InterruptedException e) {
			    e.printStackTrace();
			}
		 */
		con_time = 4;			//Tiempo espera para bloqueo
	    esp_con = false;		//Sigue o no esperando 
	    permite_salir = true;	//Permite o no salir de la interfaz actual
		/*
		Intent i = new Intent(this, Fac_rapida.class );
	    i.putExtra("pos", pide_tck);
	    i.putExtra("tipofac", poscarga);  
	    i.putExtra("tickets", "");
	    i.putExtra("envia_fac", "FA");
	    i.putExtra("user_sol", user_sol);
	    //startActivity(i);
		*/
		gen_xml_st("FA");
	    //textView1.setText("Espera Solicitud");
	    //finish();
			
    
	}else{
	if (tyf==3){
		/*disconnect();
		 try {
			    Thread.sleep(2500);
			} catch (InterruptedException e) {
			    e.printStackTrace();
			}
			*
		Intent i = new Intent(this, Pago_ban2.class );
	    i.putExtra("pos", pide_tck);
	    //i.putExtra("tipofac", poscarga);    
	    startActivity(i);
	    textView1.setText("Espera Solicitud");
	    finish();
	    */
		con_time = 4;			//Tiempo espera para bloqueo
		esp_con = false;		//Sigue o no esperando 
		permite_salir = true;	//Permite o no salir de la interfaz actual
		
	    pago_banca(pide_tck);
		
	}else {
		//if(tyf==0){ //Sin Almacen
		if(tyf==4){
			/*disconnect();try {
			    Thread.sleep(2500);
			} catch (InterruptedException e) {
			    e.printStackTrace();
			}
			*/
			con_time = 4;			//Tiempo espera para bloqueo
		    esp_con = false;		//Sigue o no esperando 
		    permite_salir = true;	//Permite o no salir de la interfaz actual

			/*
			Intent i = new Intent(this, Flotilla1.class );
			//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    i.putExtra("pos", pide_tck);
		    i.putExtra("user_sol", user_sol);
		    //startActivity(i);
			*/
			gen_xml_st("PR");
		    textView1.setText("Espera Solicitud");
		    //finish();
		}
	}
		
	}	
}

	public void pago_banca(String pos_cobro){
		
		/*
		Intent i = null;
	    i = new Intent(this, Pago_ban_msj.class);
	    i.putExtra("pos", pos_cobro);
        i.putExtra("metodoPago", "");
        i.putExtra("tar_digi", "");
        i.putExtra("enti_banco_f", "");
	    startActivityForResult(i, 1);
	    */
		
		// Creamos una carpeta "Tickets" dentro del directorio "/"
        // Con el m�todo "mkdirs()" creamos el directorio si es necesario
        File path = new File(Environment.getExternalStorageDirectory(), "Tickets");
        path.mkdirs();

        //Una vez creado disponemos de un archivo para guardar datos
        try
        {
            File ruta_sd = Environment.getExternalStorageDirectory();

            File f = new File(ruta_sd.getAbsolutePath(), "Tickets/PBDATA.txt");

            OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(f));
            
            String conf_cb = pos_cobro+"\n";
            conf_cb += serv_cb+"\n";
            conf_cb += num_tabled+"\n";
            conf_cb += user_sol+"\n";
            conf_cb += "1"+"\n";
            
            fout.write(conf_cb);
            fout.close();
            
        }
        catch (Exception ex)
        {
            //Log.e("Ficheros", "Error al escribir fichero a tarjeta SD");
        	//Toast.makeText(this, "Error al escribir fichero a tarjeta SD", Toast.LENGTH_LONG).show();
        }

		Intent launchIntent = getPackageManager().getLaunchIntentForPackage("mx.qpay.testsdk");
		startActivity(launchIntent);
	    //textView1.setText("Espera Solicitud");
	    //finish();
	    
	}

public void msj(String muestra){
	Intent j = new Intent(this, Msj.class );
	j.putExtra("msjcon", muestra);
    startActivity(j);
}

	/*
public void generaqr() throws IOException{
	 //Find screen size
	 WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
	 Display display = manager.getDefaultDisplay();
	 Point point = new Point();
	 display.getSize(point);
	 int width = point.x;
	 int height = point.y;
	 smallerDimension = width < height ? width : height;
	 smallerDimension = smallerDimension * 3/4;
	 
	 String qrInputText = "idw="+clave_esp;
	 Log.v(LOG_TAG, qrInputText);

	 //Encode with a QR Code image
	 QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrInputText, 
	           null, 
	           Contents.Type.TEXT,  
	           BarcodeFormat.QR_CODE.toString(), 
	           smallerDimension);
	 try {
	  bitmap1 = qrCodeEncoder.encodeAsBitmap();
	  bitmap2 = Bitmap.createScaledBitmap(bitmap1,(int)(bitmap1.getWidth()*0.6), (int)(bitmap1.getHeight()*0.6), true);
	  //bitmap = Bitmap.createScaledBitmap(bitmap2,(int)(bitmap2.getWidth()*0.5), (int)(bitmap2.getHeight()*0.5), true);
	  bitmap = bitmap2;

	  imprimeqr();

	 } catch (WriterException e) {
	  e.printStackTrace();
	 }
}
	*/

public void imprimeqr() throws IOException{
	
	outStream.write(10);
	
    Bitmap bmp = bitmap;
    convertBitmap(bmp);
    //mService.write(PrinterCommands.SET_LINE_SPACING_24);
    //SET_LINE_SPACING_24 = {0x1B, 0x33, 24};
    outStream.write(0x1B);
    outStream.write(0x33);
    outStream.write(24);

    int offset = 0;
    while (offset < bmp.getHeight()) {
        //mService.write(PrinterCommands.SELECT_BIT_IMAGE_MODE);
    	//public static byte[] SELECT_BIT_IMAGE_MODE = {0x1B, 0x2A, 33, 255, 3}; 210
    	outStream.write(0x1B); 
    	outStream.write(0x2A);
    	outStream.write(33);
    	//outStream.write(300);
    	outStream.write(bmp.getWidth());	        	
    	outStream.write(0);
    	
        for (int x = 0; x < bmp.getWidth(); ++x) {
        //for (int x = 0; x < 300; ++x) {	

            for (int k = 0; k < 3; ++k) {

                byte slice = 0;
                for (int b = 0; b < 8; ++b) {
                    int y = (((offset / 8) + k) * 8) + b;
                    int i = (y * bmp.getWidth()) + x;
                    boolean v = false;
                    if (i < dots.length()) {
                        v = dots.get(i);
                    }
                    slice |= (byte) ((v ? 1 : 0) << (7 - b));
                }
                outStream.write(slice);
            }
        }
        offset += 24;
      //Salto de Linea
       	 outStream.write(10);
    }
    //mService.write(PrinterCommands.SET_LINE_SPACING_30);
    //SET_LINE_SPACING_30 = {0x1B, 0x33, 30};
    //Select default line spacing ESC 2 --> 1B 32
    outStream.write(0x1B);
    outStream.write(0x32);
    
    //Salto de Linea
   	 outStream.write(27);
   	 outStream.write('d');
   	 outStream.write(1); //Numero se saltos
   	//FIN Salto linea     	 
}

public String convertBitmap(Bitmap inputBitmap) {

    int mWidth = inputBitmap.getWidth();
    int mHeight = inputBitmap.getHeight();

    convertArgbToGrayscale(inputBitmap, mWidth, mHeight);
    String mStatus = "ok";
    return mStatus;

}

private void convertArgbToGrayscale(Bitmap bmpOriginal, int width, int height) {
    int pixel;
    int k = 0;
    int B = 0, G = 0, R = 0;
    dots = new BitSet();
    try {

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                // get one pixel color
                pixel = bmpOriginal.getPixel(y, x);

                // retrieve color of all channels
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                // take conversion up to one single value by calculating
                // pixel intensity.
                R = G = B = (int) (0.299 * R + 0.587 * G + 0.114 * B);
                // set bit into bitset, by calculating the pixel's luma
                if (R < 55) {                       
                    dots.set(k);//this is the bitset that i'm printing
                }
                k++;
            }
        }
    } catch (Exception e) {
        // TODO: handle exception
    	e.printStackTrace();
    }
}


public void cambiatf(View view){
	// Posiones del BOTON PRINCIPAL
	//Reincia el contador de espera actividad o bloquea 5 seg
	if (tyf==0){
		bt5.setText("TICKET");
		tyf=1;
		if (visOrdenTP==0){
			//Version 2.0-0 MSM 13/Mar/2018
			//Opcion para mostrar "Acumulado" de venta a cliente.
			if (visAcumula == 0){
				bt3.setText("Acumular\nTickets");
				bt3.setVisibility(View.VISIBLE);	//Boton Ingresar TCK
			}else{
				bt3.setVisibility(View.INVISIBLE);	//Boton Ingresar TCK
			}

			bt4.setVisibility(View.INVISIBLE);	//Boton HOME	C
			bt6.setVisibility(View.VISIBLE);	//Boton Gerente	C
			//bt3.setText("Introducir TICKETS");
			//poscarga=1;
		}else{
			bt3.setVisibility(View.INVISIBLE);	//Boton Ingresar TCK
			bt4.setVisibility(View.VISIBLE);	//Boton HOME	C
			bt6.setVisibility(View.INVISIBLE);	//Boton Gerente	C
		}
		
		
	}else{
		if (tyf==1){
			bt5.setText("FACTURA");
			if (visTienda==0){
				if (visOrdenTP==0){
					tyf=2;
				}else{
					tyf=3;
				}

			}else{
				if (visOrdenTP==0){
					tyf=2;

				}else{
					tyf=3;
				}
			}

			bt3.setText("Ingresar\nTickets");
			if (visTickets==0){
					//bt3.setVisibility(View.INVISIBLE);	//Boton Ingresar TCK
				bt3.setVisibility(View.VISIBLE);	//Boton Ingresar TCK
			}else{
				bt3.setVisibility(View.INVISIBLE);	//Boton Ingresar TCK}
			}
			bt4.setVisibility(View.VISIBLE);	//Boton HOME
			bt6.setVisibility(View.INVISIBLE);	//Boton Gerente
			//bt1.setEnabled(false);
			//bt2.setEnabled(false);
		}else{
			if (tyf==2){
				bt5.setText("PAGO BANCARIO");
				//tyf=3;
				if (visOrdenTP==0){
					tyf=3;
					//Cambio para Permirir Tickets a Cobro Bancario
					//Version 2.0-0 MSM 20/Feb/2018
					if (visTckCobro==0){
						bt3.setVisibility(View.VISIBLE);	//Boton Ingresar TCKs //Adecuacion CB tickets
					}else{
						bt3.setVisibility(View.INVISIBLE);	//Boton Ingresar TCKs //Adecuacion CB tickets
					}
					bt4.setVisibility(View.VISIBLE);	//Boton HOME	C
					bt6.setVisibility(View.INVISIBLE);	//Boton Gerente	C
				}else{
					tyf=0;
					//Cambio para Permirir Tickets a Cobro Bancario
					//Version 2.0-0 MSM 20/Feb/2018
					if (visTckCobro==0){
						bt3.setVisibility(View.VISIBLE);	//Boton Ingresar TCKs //Adecuacion CB tickets
					}else{
						bt3.setVisibility(View.INVISIBLE);	//Boton Ingresar TCKs //Adecuacion CB tickets
					}
					bt4.setVisibility(View.INVISIBLE);	//Boton HOME	C
					bt6.setVisibility(View.VISIBLE);	//Boton Gerente	C
				}
				
			}else{
				if (tyf==3){
					bt5.setText("VENTA FLOTILLA");
					if (visTienda==0){
						tyf=4;
					}else{
						if (visOrdenTP==0){
							tyf=0;
						}else{
							tyf=2;
						}
					}

					bt3.setVisibility(View.INVISIBLE);	//Boton Ingresar TCK
					bt4.setVisibility(View.VISIBLE);	//Boton HOME
					bt6.setVisibility(View.INVISIBLE);	//Boton Gerente
					//Se cambia a 4 para que vea a TIENDA, se quita por el momento.

				}else{
					if (tyf==4){
						tyf=0;
						//Manda a la ventana de TIENDA --
						Intent i = new Intent(this, Tienda.class );
						i.putExtra("envia_dat", "TC");
					    i.putExtra("dispo_f", "");
					    i.putExtra("flotilla", "");
					    i.putExtra("tarjeta", "");
					    i.putExtra("envia_dat", "TC");
					    i.putExtra("user_sol", user_sol);
					    startActivity(i);
					    
					    con_time = 4;
					    esp_con=false;
					    permite_salir = true;

						Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							public void run() {
								// acciones que se ejecutan tras los milisegundos
								home(null);
							}
						}, 1000);

					    //finish();
					}else{
						//Si se requiren mas Opciones
						//Toast.makeText(this, "Opcion Incorrecta",Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	}
}

//Revisa que opcion esta activa 2 opcion principal
public void tcks_acl(View view){
	if(servidor_ok){
		//Opcion para tickets en Factura.
		if (visTickets==0){
			if (tyf == 2 && visOrdenTP == 0) { //Inicia con Ticket
				con_time = 4;            //Tiempo espera para bloqueo
				esp_con = false;        //Sigue o no esperando
				permite_salir = true;    //Permite o no salir de la interfaz actual
				lanza_ventana("T");
			}else
			if (tyf == 3 && visOrdenTP == 1) { //Inicia con Cobro Bancario
				con_time = 4;            //Tiempo espera para bloqueo
				esp_con = false;        //Sigue o no esperando
				permite_salir = true;    //Permite o no salir de la interfaz actual
				lanza_ventana("T");
			}
		}

		//Se aumenta la Opcion para Tickets en Cobro Bancario
		//Version 2.0-0 MSM 02/Oct/2017
		if (visTckCobro==0 ){
			if (tyf==3 && visOrdenTP==0){ //Inicia con Ticket
				con_time = 4;			//Tiempo espera para bloqueo
				esp_con = false;		//Sigue o no esperando
				permite_salir = true;	//Permite o no salir de la interfaz actual
				lanza_ventana("B");
				//Toast.makeText(this, "Solicita TCKs a PB \nTCK", Toast.LENGTH_LONG).show();
			}else
			if (tyf==0 && visOrdenTP==1){ //Inicia con Cobro Bancario
				con_time = 4;			//Tiempo espera para bloqueo
				esp_con = false;		//Sigue o no esperando
				permite_salir = true;	//Permite o no salir de la interfaz actual
				lanza_ventana("B");
				//Toast.makeText(this, "Solicita TCKs a PB \nPB", Toast.LENGTH_LONG).show();
			}
		}


		//Se aumenta la opcion para Acumulado de Venta.
		//Version 2.0-0 MSM 13/Mar/2018
		//MSM 03/Abr/2018
		if(visAcumula == 0){
			if (tyf==1 && visOrdenTP==0){ //Inicia con Ticket
				con_time = 4;			//Tiempo espera para bloqueo
				esp_con = false;		//Sigue o no esperando
				permite_salir = true;	//Permite o no salir de la interfaz actual
				activaAcumulado = true;
				bt5.setText("ACUMULADO");
				//Quita demas opciones
				bt3.setVisibility(View.INVISIBLE);	//Boton Ingresar TCK
				bt6.setVisibility(View.INVISIBLE);	//Boton Gerente
				bt4.setVisibility(View.VISIBLE);	//Boton Home
			}
				/*
				else
				if (tyf==2 && visOrdenTP==1){ //Inicia con Cobro Bancario
					con_time = 4;			//Tiempo espera para bloqueo
					esp_con = false;		//Sigue o no esperando
					permite_salir = true;	//Permite o no salir de la interfaz actual
					activaAcumulado = true;
					//Toast.makeText(this, "Solicita TCKs a PB \nPB", Toast.LENGTH_LONG).show();
				}*/
		}

	}
}


public void home(View view){
	//TOMARA EL VALOR DEL HOME
	activaAcumulado = false;
	if (visOrdenTP==0){
		tyf=0;
	}else{
		tyf=2;
	}

	cambiatf(null);
	
	//Reincia el contador de espera actividad o bloquea 5 seg
	con_time = 4;
	/*
	esp_con=false;
	Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
        public void run() {
        	con_time = 4;
        	esp_con=true;
        	esp();
        }
    }, 1000);
    */
	/*
	bt5.setText("TICKET");
	bt3.setVisibility(View.INVISIBLE);
	bt4.setVisibility(View.INVISIBLE);
	bt6.setVisibility(View.VISIBLE);	//Boton Gerente
	tyf=1;
	//bt3.setText("POSICION DE CARGA");
	poscarga=1;
	*/
	//bt1.setEnabled(true);
	//bt2.setEnabled(true);
	//Intent i = new Intent(this, Pago_ban.class );
    //startActivity(i);
}

	//Version 1.8-0 MSM 02/Oct/2017
public void lanza_ventana(String sol_tcks){
	//disconnect();
    Intent i = new Intent(this, Main_ticket.class );
	i.putExtra("user_sol", user_sol);
	//Version 1.8-0 MSM 02/Oct/2017
	//Parametro par saber quien solicita los Miltiples Tickets
	i.putExtra("sol_tcks", sol_tcks);
    startActivity(i);
    //Regresa a pantalla Principal (HOME)
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
        public void run() {
            // acciones que se ejecutan tras los milisegundos
            home(null);
        }
    }, 1500);
}

public void dismissDialog() {
	if(dialog != null) {
		dialog.dismiss();
		dialog = null;
	}
}


	/*
public void kiosco(View view) {
	disconnect();
    Intent i = new Intent(this, kiosco.class );
    startActivity(i);
    finish();
}
*/

	public void conf(View view) {	
	con_time = 4;			//Tiempo espera para bloqueo
	esp_con = false;		//Sigue o no esperando
	revMsjEpos = false;		//Pausa mensaje de estatus de Impresora.
	revComm = false;		//Pone en pausa Revicion de Comunicacion
	permite_salir = true;	//Permite o no salir de la interfaz actual
	msImpNoRs = true;		//Detiene por completo el mensaje de  Impresora.
	
	disconnect();
	//Intent i = new Intent(this, Configura_clv.class );
	Intent i = new Intent(this, Gerente_clv.class );
	startActivity(i);
	//finish();
	}

    public void prueba(View view) {
		setContentView(R.layout.leeqr);
    }

//FUNCIONES DE BLUETOOTH
/** Thread used to connect to a specified Bluetooth Device */
public class ConnectThread extends Thread {
	private String address;
	private boolean connectionStatus;
	
	ConnectThread(String MACaddress) {
		address = MACaddress;
		connectionStatus = true;
	}
	
	public void run() {
		// When this returns, it will 'know' about the server, 
       // via it's MAC address. 
		try {
			BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
			
			// We need two things before we can successfully connect 
           // (authentication issues aside): a MAC address, which we 
           // already have, and an RFCOMM channel. 
           // Because RFCOMM channels (aka ports) are limited in 
           // number, Android doesn't allow you to use them directly; 
           // instead you request a RFCOMM mapping based on a service 
           // ID. In our case, we will use the well-known SPP Service 
           // ID. This ID is in UUID (GUID to you Microsofties) 
           // format. Given the UUID, Android will handle the 
           // mapping for you. Generally, this will return RFCOMM 1, 
           // but not always; it depends what other BlueTooth services 
           // are in use on your Android device. 
           try { 
                btSocket = device.createRfcommSocketToServiceRecord(SPP_UUID); 
           } catch (IOException e) { 
           	connectionStatus = false;
           } 
		}catch (IllegalArgumentException e) {
			connectionStatus = false;
		}
       
       // Discovery may be going on, e.g., if you're running a 
       // 'scan for devices' search from your handset's Bluetooth 
       // settings, so we call cancelDiscovery(). It doesn't hurt 
       // to call it, but it might hurt not to... discovery is a 
       // heavyweight process; you don't want it in progress when 
       // a connection attempt is made. 
       mBluetoothAdapter.cancelDiscovery(); 
       
       // Blocking connect, for a simple client nothing else can 
       // happen until a successful connection is made, so we 
       // don't care if it blocks. 
       try {
            btSocket.connect(); 
       } catch (IOException e1) {
            try {
                 btSocket.close(); 
            } catch (IOException e2) {
            }
       }
       
       // Create a data stream so we can talk to server. 
       try { 
       	outStream = btSocket.getOutputStream(); 
       } catch (IOException e2) {
       	connectionStatus = false;
       }
       
       // Send final result
       if (connectionStatus) {
       	mHandler.sendEmptyMessage(1);
       }else {
       	mHandler.sendEmptyMessage(0);
       }
	}
}

public void write(byte data) {
	 if (outStream != null) {
        try {
       	 outStream.write(data);
        } catch (IOException e) {
        }
    }
}

public void emptyOutStream() {
	 if (outStream != null) {
      try {
     	 outStream.flush();
      } catch (IOException e) {
      }
  }
}

public void connect() {
	 // Launch the DeviceListActivity to see devices and do scan
    //Intent serverIntent = new Intent(this, DeviceListActivity.class);
    //startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    acti = 1;
    coneimpre();
}

public void disconnect() {
	 if (outStream != null) {
		 try {
	 			outStream.close();
	 			connectStat = false;
				//connect_button.setText(R.string.disconnected);
				acti = 0;
	 		} catch (IOException e) {
	 		}
	 } 
}

public void coneimpre(){
	//Toast.makeText(this, "Inicia Conexion con Impresora:",Toast.LENGTH_SHORT).show();
		myProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.pleaseWait), getResources().getString(R.string.makingConnectionString), true);
			
    	// Get the device MAC address
		//deviceAddress = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		// Connect to device with specified MAC address
//Imprecion de la mac usada 
			deviceAddress = mac_serial;
	//Toast.makeText(this, "MAC BLUE: " + deviceAddress,Toast.LENGTH_SHORT).show();
		//et1.setText(deviceAddress);
		
        mConnectThread = new ConnectThread(deviceAddress);
        mConnectThread.start();
	 
 }

public void imp(String cadena1, String clave, String original) throws IOException{
	 if (acti == 1){
		 textView1.setText("entro a imp");
	 int max,conta, conta2;
	 byte[] envia;
	 String opti;
	 conta=0;
	 String compara="";
	 String pipe="|";
	 
	 String cadena2 = "012345678912"; //Codigo de BARRAS DEL Ticket
	     
	 max=cadena1.trim().length();
	 while (conta < max){
		//Toast.makeText(this, "INICIA:",Toast.LENGTH_SHORT).show();	     		 
		opti = cadena1.substring(conta);
		conta2 = conta + 1;
		
		compara = cadena1.substring(conta, conta2);
		//Toast.makeText(this, "SUB: " + compara,
			//	Toast.LENGTH_SHORT).show();
		if (compara.equals(pipe))
		{
			//Toast.makeText(this, "Encuentra |",
				//	Toast.LENGTH_SHORT).show();
		   //Alinear Impresion
	       	 outStream.write(27); //esc
	       	 outStream.write('a'); //a
	       	 outStream.write(0); //0=Izquierda, 1=centrado, 2=derecha 
	       //FIN Alinear Impresion
			conta=conta+2;
			conta2 = conta + 1;
			compara = cadena1.substring(conta, conta2);
			if (compara.equals("L"))
			{
				//Toast.makeText(this, "Encuentra L",
   				//	Toast.LENGTH_SHORT).show();
				outStream.write(10);//Nueva de Linea
				//Alinear Impresion
				outStream.write(27); //esc
				outStream.write('a'); //a
				outStream.write(0); //0=Izquierda, 1=centrado, 2=derecha 
				//FIN Alinear Impresion
			}else{
				if (compara.equals("S"))
   			{
					//Toast.makeText(this, "Encuentra S",
       				//	Toast.LENGTH_SHORT).show();
			    	//Salto de Linea
			      	 outStream.write(27);
			      	 outStream.write('d');
			      	 outStream.write(1); //Numero se saltos
			      	//FIN Salto linea
   			}else{
   				if (compara.equals("R"))
       			{
   					//Toast.makeText(this, "Encuentra R",
           				//	Toast.LENGTH_SHORT).show();
   					outStream.write(10);//Nueva de Linea
   			       	//Alinear Impresion
   			       	 outStream.write(27); //esc
   			       	 outStream.write('a'); //a
   			       	 outStream.write(2); //0=Izquierda, 1=centrado, 2=derecha 
   			       //FIN Alinear Impresion
   				}else{
   					if (compara.equals("-"))
   					{
   						//Toast.makeText(this, "Encuentra -",
   	         				//	Toast.LENGTH_SHORT).show();
   						outStream.write(10);//Nueva de Linea
   				    	 conta2=0;
   			    	  	 while (conta2 < 42){
   			    	  		opti = "-";
   			    	  		 envia = opti.getBytes();
   			         		 outStream.write(envia[0]);
   			    	 		 conta2++;
   			    	 		}
   					}else{
   						if (compara.equals("Q"))
       					{
   							//Toast.makeText(this, "GENERA --> QR",Toast.LENGTH_SHORT).show();
   							//generaqr();
       					}//else
   						//msj("Ticket con ERROR");
   					}
   				}
				}
			}
			conta++;
		}else{
			//Toast.makeText(this, "NO ES | resulto: " + compara,
   		//		Toast.LENGTH_SHORT).show();
  		 envia = opti.getBytes();
   		 outStream.write(envia[0]);
   		 conta++;
		}
		

	 }
	
	 //Genera e Imprime codigo QR 
	if (original.equals("1")){
		//generaqr();
   }

	//+++++++++++++++++++++++++
	 //Codigo de Barras.
	 outStream.write(10);//Nueva de Linea
	 
	//Alinear Impresion
	 outStream.write(27); //esc
	 outStream.write('a'); //a
	 outStream.write(0); //0=Izquierda, 1=centrado, 2=derecha 
	 
	 //CODIGO de BARRAS
	 outStream.write(29); //1D
	 outStream.write('h'); //h
	 outStream.write(80); //tama�o de 80
	 
	 outStream.write(29); //1D
	 outStream.write(119); //w
	 outStream.write(5); //5
	 
	 outStream.write(29); //1D
	 outStream.write(107); //k
	 outStream.write(2); //2
	 
	 conta=0;
	 max=cadena2.trim().length();
	 while (conta < max){
		 opti = cadena2.substring(conta);
		 envia = opti.getBytes();
		 outStream.write(envia[0]);
		 conta++;
    } 
	 outStream.write(0);
	//FIN CODIGO de BARRAS
	 
	 
	//Salto de Linea
	 outStream.write(27);
	 outStream.write('d');
	 outStream.write(11); //Numero se saltos
	//FIN Salto linea     	 

	 //Cortar papel
	outStream.write(29);
	outStream.write(86);
	outStream.write(0);
	
	 //FIN Cortar papel
	
	 }
	 //textView1.setText("corto el papel");

}




public void imprimir(String cadena) throws IOException{
	 if (acti == 1){
		 textView1.setText("entro a imp");
		 int max,conta, conta2;
		 byte[] envia;
		 String opti;
		 conta=0;
		 String compara="";
		 String pipe="|";
		 		     
		 max=cadena.trim().length();
	 	 while (conta < max){
			opti = cadena.substring(conta);
			conta2 = conta + 1;
			compara = cadena.substring(conta, conta2);

			if (compara.equals(pipe))
			{
			   //Alinear Impresion
		       	 outStream.write(27); //esc
		       	 outStream.write('a'); //a
		       	 outStream.write(0); //0=Izquierda, 1=centrado, 2=derecha 
		       //FIN Alinear Impresion
				conta=conta+2;
				conta2 = conta + 1;
				compara = cadena.substring(conta, conta2);
				if (compara.equals("L"))
				{
					outStream.write(10);//Nueva de Linea
					//Alinear Impresion
					outStream.write(27); //esc
					outStream.write('a'); //a
					outStream.write(0); //0=Izquierda, 1=centrado, 2=derecha 
					//FIN Alinear Impresion
				}else{
					if (compara.equals("S"))
	    			{
				    	//Salto de Linea
				      	 outStream.write(27);
				      	 outStream.write('d');
				      	 outStream.write(1); //Numero se saltos
				      	//FIN Salto linea
	    			}else{
	    				if (compara.equals("R"))
	        			{
	    					outStream.write(10);//Nueva de Linea
	    			       	//Alinear Impresion
	    			       	 outStream.write(27); //esc
	    			       	 outStream.write('a'); //a
	    			       	 outStream.write(2); //0=Izquierda, 1=centrado, 2=derecha 
	    			       //FIN Alinear Impresion
	    				}else{
	    					if (compara.equals("-"))
	    					{
	    						outStream.write(10);//Nueva de Linea
	    				    	 conta2=0;
	    			    	  	 while (conta2 < 42){
	    			    	  		opti = "-";
	    			    	  		 envia = opti.getBytes();
	    			         		 outStream.write(envia[0]);
	    			    	 		 conta2++;
	    			    	 		}
	    					}else{
	    						if (compara.equals("Q"))
	        					{
									//Toast.makeText(this, "GENERA --> QR",Toast.LENGTH_SHORT).show();
	    							//generaqr();
	        					}else{
	        						if (compara.equals("B"))
		        					{
										//Toast.makeText(this, "GENERA --> CB",Toast.LENGTH_SHORT).show();
		    							//generaCb();
		        					}else{
		        						if (compara.equals("C"))
			        					{
											//Toast.makeText(this, "Corta papel ----------",Toast.LENGTH_SHORT).show();
			    							//Salto de Linea
			    						 	 outStream.write(27);
			    						 	 outStream.write('d');
			    						 	 outStream.write(6); //Numero se saltos
			    						 	//FIN Salto linea     	 
			    						
			    							 //Cortar papel
			    							outStream.write(29);
			    							outStream.write(86);
			    							outStream.write(0);
			        					}
		        						else{
		        							//msj("Ticket con ERROR");
		        						}
		        					}
	        					}
	    					}
	    				}
					}
				}
				conta++;
			}else{
				//Toast.makeText(this, "NO ES | resulto: " + compara,
	    		//		Toast.LENGTH_SHORT).show();
	   		 envia = opti.getBytes();
	    		 outStream.write(envia[0]);
	    		 conta++;
			}
		 }
	 }	 
 }


	
	public void lee_CBreturn(int uso){
		File f1 = new File(Environment.getExternalStorageDirectory() + "/Tickets");
		// Comprobamos si la carpeta est� ya creada
		// Si la carpeta no est� creada, la creamos.
		if(!f1.isDirectory()) {
		 String newFolder = "/Tickets"; //cualquierCarpeta es el nombre de la Carpeta que vamos a crear
		 String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
		 File myNewFolder = new File(extStorageDirectory + newFolder);
		 myNewFolder.mkdir(); //creamos la carpeta
		 //Toast.makeText(this, "Crea Tickets", Toast.LENGTH_LONG).show();
		}
		
		String contenido="";
        //Defino la ruta donde busco los ficheros
        File f = new File(Environment.getExternalStorageDirectory() + "/Tickets/");
        //Creo el array de tipo File con el contenido de la carpeta
        File[] files = f.listFiles();
		boolean buscaArc = true;
        //Hacemos un Loop por cada fichero para extraer el nombre de cada uno
        for (int i = 0; i < files.length; i++)
        {
            //Sacamos del array files un fichero
            File file = files[i];
            //Si es directorio...
            if (file.isDirectory())
                contenido=contenido+(file.getName() + "/ carpeta\n");
                //Si es fichero...
            else{

                if(file.getName().equals("PBVR.txt")){
                	//Toast.makeText(this, "Encontro PBVR.txt ", Toast.LENGTH_LONG).show();
					if (buscaArc){
						buscaArc = false;
						//, "Encontro ENTRA", Toast.LENGTH_LONG).show();
						lee_doc_cb(uso);
					}
                    i = files.length;
                }
				if(file.getName().equals("VBCONF.txt")){
					//Toast.makeText(this, "Busca doc 2 Encuentra", Toast.LENGTH_LONG).show();
					//contenido=contenido+(file.getName() + " doc\n");
					leeConf();
					i = files.length;
				}
            }
        }
	}


	public void leeConf(){
		String resconf = "";

		if(isExternalStorageReadable()) {

			File rFileE = new File(Environment.getExternalStorageDirectory(), "Tickets/VBCONF.txt");
			try {

				BufferedReader bReader = new BufferedReader(new FileReader(rFileE));
				resconf = bReader.readLine();

				//Cierra lectura de archivo
				bReader.close();
				//Borra archivo.
				rFileE.delete();

				if (resconf.equals("C")){
					cont_pag=1;
					Leedb();
					lanza_user_sol = true;
					lee_user();
					vista();
					if (visOrdenTP==1){
						tyf=2;
					}else{
						tyf=0;
					}
					cambiatf(null);
					if (servidor_ok){
						//Reactiva la Busqueda de Comunicion
						revComm = true;
						//Revisar Comunicaciones.
						gen_xml_test3();
					}
					if(imp_ext==1) {
						revMsjEpos = true;
					}

				}else{
					lanza_user_sol = true;
					lee_user();
				}



			} catch (IOException e) {
				//Toast.makeText(this, "Error leyendo fichero de memoria externa 1", Toast.LENGTH_SHORT).show();
			}
		}else{
			//Toast.makeText(this, "Error leyendo memoria externa 2", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void lee_doc_cb(int br){

        if(isExternalStorageReadable()) {
            File rFileE = new File(Environment.getExternalStorageDirectory(), "Tickets/PBVR.txt");
            try {

				BufferedReader bReader = new BufferedReader(new FileReader(rFileE));

                //Cierra lectura de archivo
                bReader.close();
                //Borra archivo.
                rFileE.delete();
				if (br==1){
					bloquea();
					lee_user();
				}

            } catch (IOException e) {
                Toast.makeText(this, "Error leyendo fichero de memoria externa 1", Toast.LENGTH_SHORT).show();
            }
        }else{
        	Toast.makeText(this, "Error leyendo memoria externa 2", Toast.LENGTH_SHORT).show();
        }
    }
	
	public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

	/*
	public void tiempo_msj_imp(){
		new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(9000);
                } catch (InterruptedException e) {
                }
                msj_epos();
            }
        }).start();
	}
	*/
	private void hiloPago()
	{
		//Aun dentro del mismo Hilo
		this.runOnUiThread(Accion4);
	}

	private Runnable Accion4 = new Runnable() {
		public void run() {
			//Funcion a ejecutar
			lee_CBreturn(1);
			//Toast.makeText(getApplicationContext(), "Tiempo!", Toast.LENGTH_LONG).show();
		}
	};



	private void hiloReloj()
	{
		//Aun dentro del mismo Hilo
		this.runOnUiThread(Accion2);
	}

	private Runnable Accion2 = new Runnable() {
		public void run() {
			//Funcion a ejecutarar
			tiempoReloj();
			//Toast.makeText(getApplicationContext(), "Tiempo!", Toast.LENGTH_LONG).show();
		}
	};


	private void tiempoReloj(){
		treloj--;
		if (treloj==0){

			bloquea();
			//msj(">La impresora no responde, verifique que esté conectada correctamente y reinicie el equipo>2>3>4>5>6>7>");
			timer2.cancel();
			timer.cancel();

			timer3.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					//Ejecuta
					hiloMsjImp();
				}
			}, 0, 9000);
		}
		revMsjepos();
		String treloj_s = String.format("%02d", treloj);
		textView36.setText(treloj_s);
	}



	private void hiloMsjImp()
	{
		//Aun dentro del mismo Hilo
		this.runOnUiThread(Accion3);
	}

	private Runnable Accion3 = new Runnable() {
		public void run() {
			if (!msImpNoRs)
				msj(">La impresora no responde, verifique que esté conectada correctamente y reinicie la VeriBOX>4>3>4>5>6>7>");
		}
	};


	private void hiloMmsjepos()
	{
		//Aun dentro del mismo Hilo
		this.runOnUiThread(Accion);
	}

	private Runnable Accion = new Runnable() {
		public void run() {
			if (revMsjEpos){
				revMsjepos();
			}
		}
	};

	public void revMsjepos(){
		//Toast.makeText(this, "Busca doc 1", Toast.LENGTH_LONG).show();
		String contenido="";
        //Defino la ruta donde busco los ficheros
        File f = new File(Environment.getExternalStorageDirectory() + "/Tickets/");
        //Creo el array de tipo File con el contenido de la carpeta
        File[] files = f.listFiles();
        //Hacemos un Loop por cada fichero para extraer el nombre de cada uno
        for (int i = 0; i < files.length; i++)
        {
            //Sacamos del array files un fichero
            File file = files[i];
            //Si es directorio...
            if (file.isDirectory())
                contenido=contenido+(file.getName() + "/ carpeta\n");
			//Si es archivo...
            else{
				//Busca archivo de configuracion a DB
				if(file.getName().equals("VBX.txt")){
					//Toast.makeText(this, "CONFIGURACION 1", Toast.LENGTH_LONG).show();
					configMod();
					i = files.length;
				}
                //Busca archivo con Informacion desde la Impresora cada x tiempo
				if(file.getName().equals("msj_epos.txt")){
                    //contenido=contenido+(file.getName() + " doc\n");
					//Toast.makeText(this, "MSJ ePOS 1", Toast.LENGTH_LONG).show();
                    lee_doc_imp();
                    //i = files.length;
                }
            }
        }
	}
	
	public void lee_doc_imp(){
		permite_salir = true;
		String ms1 = "";
        String ms2 = "";
        String ms3 = "";
		if(isExternalStorageReadable()) {
			File rFileE = new File(Environment.getExternalStorageDirectory(), "Tickets/msj_epos.txt");
            try {
                BufferedReader bReader = new BufferedReader(new FileReader(rFileE));

                ms1 = bReader.readLine();
				ms2 = bReader.readLine();

                /*
                while((ms1 = bReader.readLine()).equals(""))
                {
                	ms1=ms1+"\n";
                }                
                */
                if (ms1.equals("<<")){
					//Toast.makeText(this, "Muere a Solcitu de la Impresora", Toast.LENGTH_LONG).show();
                	//Muere a Solcitu de la Impresora
                	//finish();
                	//regresa_msj();
                }
				if (ms1.equals(">") && busca_imp){
					textView36.setVisibility(View.INVISIBLE);
					busca_imp = false;
					timer2.cancel();

					if (servidor_ok){
						//Envia a revisar comunicacion.
						gen_xml_test3();
					}

					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						public void run() {
							// acciones que se ejecutan tras los milisegundos

							bloquea();
							msj(">Impresora lista,\ncontinua el arranque>2>3>4>5>6>7>");
						}
					}, 2000);

				}
                if (!ms1.equals(">")){
                	msj(">"+ms1+">3>3>4>5>6>7>");
					//Toast.makeText(this, ms1, Toast.LENGTH_LONG).show();
                	//regresa_msj();
					/*
					if (ms2.equals("00")){
						//Borra archivo.
						//rFileE.delete();
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
							stopLockTask();
							Toast.makeText(this, "Pide DESBLOQUEO", Toast.LENGTH_SHORT).show();
						}

						Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.epson.epos2_printer");
						startActivity(launchIntent);
						finish();

						//Toast.makeText(this, "MS2: 00", Toast.LENGTH_LONG).show();
					}
					*/



					/*
					if (ms2.length()>0  && ms2!=null ){
						Toast.makeText(this, "Lee linea2", Toast.LENGTH_LONG).show();
					}

					if (ms2.equals("00")){
						Toast.makeText(this, "Lanzar a EPOS", Toast.LENGTH_LONG).show();

						//inicia_epos();
					}
					*/
                }

              //Borra archivo.
                //rFileE.delete();
            } catch (IOException e) {
                Toast.makeText(this, "Error leyendo fichero de memoria externa", Toast.LENGTH_SHORT).show();
            }
		}else{
        	Toast.makeText(this, "Error leyendo memoria externa", Toast.LENGTH_SHORT).show();
        }
	}


	public void obtiene_mac() {

		try {
			address= "";
			String getMacetho = getMacAddress();
			if (getMacetho.length() > 0 ){
				address = getMacetho;
			}else{
				try {
					//VeriBox 1.8-0 Para uso en Android "N"
					//WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
					WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
					WifiInfo info = manager.getConnectionInfo();
					address = info.getMacAddress();
					address = address.toUpperCase();
					Toast.makeText(this, "MAC INALAMBRICA" + address, Toast.LENGTH_SHORT).show();
				}catch (Exception e1) {
					address = "Error MAC.";
				}
			}
		}
		catch (Exception e) {
			address = "Error MAC2.";
		}
	}

	public String getMacAddress(){
		try {
			return loadFileAsString("/sys/class/net/eth0/address").toUpperCase().substring(0, 17);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String loadFileAsString(String filePath) throws java.io.IOException{
		StringBuffer data = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead=0;
		while((numRead=reader.read(buf)) != -1){
			String readData = String.valueOf(buf, 0, numRead);
			data.append(readData);
		}
		reader.close();
		return data.toString();
	}

	
	public void regresa_msj(){
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
           // acciones que se ejecutan tras los milisegundos
				permite_salir = false;
			}
		}, 2000);
	}


	/*
	@Override
	protected void onResume() {
		super.onResume();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			startLockTask();
		}
	}
	*/
	private void bloquea(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			startLockTask();
		}
	}


	private void provisionOwner() {
		DevicePolicyManager manager =
				(DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName componentName = BasicDeviceAdminReceiver.getComponentName(this);

		if(!manager.isAdminActive(componentName)) {
			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
			startActivityForResult(intent, 0);
			return;
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			if (manager.isDeviceOwnerApp(getPackageName()))
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					manager.setLockTaskPackages(componentName, new String [] {getPackageName()});
				}
		}
	}



	//Deshabilitar BOTON atras
	@Override
	public void onBackPressed() {
	}



	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		timer.cancel();
		timer2.cancel();
		timer3.cancel();
		timer4.cancel();
	}


	//A partir de encontrar un archivo de Configuracion se procesa
	public void configMod(){

		//Toast.makeText(this, "CONFIGURACION 2", Toast.LENGTH_LONG).show();
		//Verifica que se Pueda leer memoria
		if(isExternalStorageReadable()) {
			File rFileE = new File(Environment.getExternalStorageDirectory(), "Tickets/VBX.txt");

			//Toast.makeText(this, "CONFIGURACION 3", Toast.LENGTH_LONG).show();
			try {
				BufferedReader bReader = new BufferedReader(new FileReader(rFileE));

				//lee linea de archivo
				String leelinea = bReader.readLine();

				//Cierra lectura de Archivo
				bReader.close();
				//Borra archivo
				rFileE.delete();

				//Obtiene Mac a comparar
				//Toast.makeText(this, leelinea+"\nCONFIGURACION 4", Toast.LENGTH_LONG).show();
				obtiene_mac();

				String[] parte = leelinea.split("\\|");
				String ops1 = parte[0]; // Valor de la MAC
				String ops2 = parte[1]; // Valor para reset CLV
				String ops3 = parte[2]; // Valor para reset CLV_GERENTE

				//Toast.makeText(this, ops1+"\n"+address, Toast.LENGTH_LONG).show();

				if (ops1.equals(address)) {
					//Toast.makeText(this, "CONFIGURACION 5", Toast.LENGTH_LONG).show();

					//Contenedor de Registros
					ContentValues registro = new ContentValues();

					if (ops2.equals("1")) {
						//Regresa a valor por defecto la Clave de Servicio
						registro.put("clv", "111111");
					}

					if (ops3.equals("1")) {
						//Regresa a valor por defecto la Clave de Gerente
						registro.put("clv_gerente", "000000");
					}

					//Actualiza los datos en la DB
					int cant = bd.update("config", registro, "num=1", null);
					bd.close();


					if (cant == 1) {
						msj(">Se Realizaron Cambios en VeriBox>2>3>4>5>6>7>");
					}
				} else {
					msj(">NO Corresponde el archivo\na VeriBox >2>3>4>5>6>7>");
				}


			} catch (IOException e) {
				Toast.makeText(this, "NO Lee parametros de entrada", Toast.LENGTH_SHORT).show();
			}
		}
		//Toast.makeText(this, "CONFIGURACION 6", Toast.LENGTH_LONG).show();
	}
	
	/*onPause(): Indica que la actividad est� a punto de ser lanzada a segundo plano, 
	 * Es el lugar adecuado para No permir que se salga de la aplicacion y regresarla de forma forzada*/
	/*
	@Override 
	protected void onPause() {
	   super.onPause();
	   if (permite_salir){
		   //Toast.makeText(this, "PERMITE", Toast.LENGTH_SHORT).show();
		   permite_salir = false;
	   }else{
		   Intent i = new Intent(this, MainActivity.class );
		   i.putExtra("inicia", 1);
		   startActivity(i);
		   finish();
		   //Toast.makeText(this, "DEBE REGRESAR", Toast.LENGTH_SHORT).show();   
	   }
	}
	*/

	//Obtiene el dato del teclado multimedia.
	@Override
	public boolean onKey(View view, int keyCode, KeyEvent event) {

		/*
        BluetoothGattCharacteristic characteristic = null;
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID);
        descriptor.setValue(
                BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        */

		if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
				keyCode == EditorInfo.IME_ACTION_DONE ||
				event.getAction() == KeyEvent.ACTION_DOWN &&
						event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			Toast.makeText(this, "ENTER",Toast.LENGTH_SHORT).show();
		}
		if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
				keyCode == EditorInfo.IME_ACTION_DONE ||
				event.getAction() == KeyEvent.ACTION_DOWN &&
						event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
			Toast.makeText(this, "VOL ABAJO",Toast.LENGTH_SHORT).show();
		}
		if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
				keyCode == EditorInfo.IME_ACTION_DONE ||
				event.getAction() == KeyEvent.ACTION_DOWN &&
						event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
			Toast.makeText(this, "VOL ARRIBA",Toast.LENGTH_SHORT).show();
		}
		if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
				keyCode == EditorInfo.IME_ACTION_DONE ||
				event.getAction() == KeyEvent.ACTION_DOWN &&
						event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_NEXT) {
			Toast.makeText(this, "SIGUIENTE",Toast.LENGTH_SHORT).show();
		}
		if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
				keyCode == EditorInfo.IME_ACTION_DONE ||
				event.getAction() == KeyEvent.ACTION_DOWN &&
						event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
			Toast.makeText(this, "ATRAS",Toast.LENGTH_SHORT).show();
		}

		if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
				keyCode == EditorInfo.IME_ACTION_DONE ||
				event.getAction() == KeyEvent.ACTION_DOWN &&
						event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
			Toast.makeText(this, "AMBOS",Toast.LENGTH_SHORT).show();
		}

		BluetoothGattService serv = null;
		BluetoothGattCharacteristic charac = null;
		//serv = mUdooBluService.getService(address, UDOOBLE.UUID_LED_SERV);
		displayGattServices((List<BluetoothGattService>) serv);

		//Toast.makeText(this, "return 3",Toast.LENGTH_SHORT).show();
		return false; // pass on to other listeners.
	}

	//
	private void displayGattServices(List<BluetoothGattService> gattServices) {
		//if (gattServices == null) return;

		for (BluetoothGattService gattService : gattServices) {
			//-----Service-----//
			int type = gattService.getType();
			//Log.e(TAG,"-->service type:"+Utils.getServiceType(type));
			//Log.e(TAG,"-->includedServices size:"+gattService.getIncludedServices().size());
			//Log.e(TAG,"-->service uuid:"+gattService.getUuid());

			//-----Characteristic-----//
			List<BluetoothGattCharacteristic> gattCharacteristics =gattService.getCharacteristics();
			for (final BluetoothGattCharacteristic  gattCharacteristic: gattCharacteristics) {
				//Log.e(TAG,"---->char uuid:"+gattCharacteristic.getUuid());

				int permission = gattCharacteristic.getPermissions();
				//Log.e(TAG,"---->char permission:"+Utils.getCharPermission(permission));

				int property = gattCharacteristic.getProperties();
				//Log.e(TAG,"---->char property:"+Utils.getCharPropertie(property));

				byte[] data = gattCharacteristic.getValue();
				if (data != null && data.length > 0) {
					//Log.e(TAG,"---->char value:"+new String(data));
				}

				if(gattCharacteristic.getUuid().toString().equals(UUID_RESPONSE)) {
					//ResponseCharac = gattCharacteristic;
				}


				/*
				//UUID_KEY_DATA Characteristic
				if(gattCharacteristic.getUuid().toString().equals(UUID_REQUEST)){
					//测试读取当前Characteristic数据，会触发mOnDataAvailable.onCharacteristicRead()
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							//mBLE.readCharacteristic(gattCharacteristic);
							mHandler.postDelayed(this, 500);

							//mBLE.readRemoteRssi();

							//BluetoothGattDescriptor descriptor = gattCharacteristic.getDescriptor(HD_Profile.UUID_CHAR_NOTIFY_DIS)
						}
					}, 500);

					//接受Characteristic被写的通知,收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
					//mBLE.setCharacteristicNotification(gattCharacteristic, true);

					//设置数据内容
					//gattCharacteristic.setValue("hi");
					//往蓝牙模块写入数据
					// mBLE.writeCharacteristic(gattCharacteristic);
				}
				*/




				//-----Descriptors-----//
				List<BluetoothGattDescriptor> gattDescriptors = gattCharacteristic.getDescriptors();
				for (BluetoothGattDescriptor gattDescriptor : gattDescriptors) {
					//Log.e(TAG, "-------->desc uuid:" + gattDescriptor.getUuid());
					int descPermission = gattDescriptor.getPermissions();
					//Log.e(TAG,"-------->desc permission:"+ Utils.getDescPermission(descPermission));

					byte[] desData = gattDescriptor.getValue();
					if (desData != null && desData.length > 0) {
						//Log.e(TAG, "-------->desc value:"+ new String(desData));
					}
				}
			}
		}//

	}
	//
}