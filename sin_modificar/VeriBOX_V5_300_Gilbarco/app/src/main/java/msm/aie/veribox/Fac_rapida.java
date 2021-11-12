package msm.aie.veribox;

/*
 * Ing. Miguel Santiago
 * 03/Oct/2017 Ver:1.8-0
 * Se busca el numero de Lectores
 * si es mayor a uno envia a leccionar el dispocitivo a conectar.
 * MSM Ver:2.0-0 14 de Marzo 2018
 * Cambio para solicitar usuario de Acumulado de Venta
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Hashtable;
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
import android.annotation.SuppressLint;
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
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.bbpos.bbdevice.BBDeviceController;
import com.bbpos.bbdevice.BBDeviceController.AudioAutoConfigError;
import com.bbpos.bbdevice.BBDeviceController.BBDeviceControllerListener;
import com.bbpos.bbdevice.BBDeviceController.CheckCardMode;
import com.bbpos.bbdevice.BBDeviceController.CheckCardResult;
import com.bbpos.bbdevice.BBDeviceController.ConnectionMode;
import com.bbpos.bbdevice.BBDeviceController.ContactlessStatus;
import com.bbpos.bbdevice.BBDeviceController.ContactlessStatusTone;
import com.bbpos.bbdevice.BBDeviceController.EmvOption;
import com.bbpos.bbdevice.BBDeviceController.Error;
import com.bbpos.bbdevice.BBDeviceController.NfcDetectCardResult;
import com.bbpos.bbdevice.BBDeviceController.PhoneEntryResult;
import com.bbpos.bbdevice.BBDeviceController.PinEntrySource;
import com.bbpos.bbdevice.BBDeviceController.PrintResult;
import com.bbpos.bbdevice.BBDeviceController.SessionError;
import com.bbpos.bbdevice.CAPK;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;


public class Fac_rapida extends Activity implements OnKeyListener{

	private LinearLayout vis_conec, muestra2, muestra4;
	private FrameLayout muestra1, muestra3;

	private TextView textView1, textView2, textView3, textView31, textView43, textView42, textView4, textView34, textView11;
	private EditText editText1,editText2;
	private Button bt, button4, button22;
	String pos, tipoFactura, envia_fac;
	int cambia = 1, impext;
	String rfc_cli, tickets="", met_pago_f, digitos_f, enti_banco_f, user_sol, total_PB_S;
	ImageView myImage;
	//Captura de XY
	private TextView textView_xy, TextView001;
	StringBuilder stringBuilder = new StringBuilder();
	private Coor_xy cox_coy;
	int control;
	boolean pgnull, bandera_tck, usoNFC_TRJ;
	String nfc_imp;
	SQLiteDatabase bd;
	//Variable para el Lector selecionado
	int estado_con; //Estado de la conexion con el lector
	int conta_reconex = 0;//Contador de Reintentos
	String lector_cone="";
	private boolean rein_con = true, salir = false, sig_acumula = false, env_ok = true;

	//BBPOS
	protected static BBDeviceController bbDeviceController;
	protected static MyBBDeviceControllerListener listener;
	private static CheckCardMode checkCardMode;
	//FIN BBPOS

	private final String NAMESPACE = "urn:veriboxwsdl";
	private String URL;// = "http://192.168.1.38/Veribox/Veribox.php";
	private final String SOAPACTION = "urn:veriboxwsdl#veribox";
	private final String METHOD = "veribox";

	//Uso de la camara lector de QR
	private CameraSource cameraSource;
	private SurfaceView cameraView;
	private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
	private String token = "";
	private String tokenanterior = "";
	private TextView leevista;


	//Creamos el puente para regresar
	//el mensaje recibido de peticiones
	private Handler puente = new Handler() {
		@Override
		public void handleMessage(Message msg) {
		if (((String)msg.obj).equals("")){
			msj("Problemas COM");
			finish();
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
        setContentView(R.layout.fac_rapida);

		AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "tablet", null, 1);
		bd = admin.getWritableDatabase();

        control = 0;
        pgnull = false;
		usoNFC_TRJ = false;
		nfc_imp = "";

		//Asigna el uso de la vista a camara.
		cameraView = (SurfaceView) findViewById(R.id.camera_view);
		vis_conec = (LinearLayout) findViewById(R.id.vis_conec);

		muestra1 = (FrameLayout) findViewById (R.id.muestra1);
		muestra2 = (LinearLayout) findViewById(R.id.muestra2);
		muestra3 = (FrameLayout) findViewById (R.id.muestra3);
		muestra4 = (LinearLayout) findViewById(R.id.muestra4);
		muestra4.setVisibility(View.INVISIBLE);

        bt = (Button)findViewById(R.id.button1);
        button4 = (Button)findViewById(R.id.button4);
        button22 = (Button)findViewById(R.id.button5);
        editText1 = (EditText)findViewById(R.id.editText1);
        editText2 = (EditText)findViewById(R.id.editText2);
        editText2.requestFocus();
        
        //editText2 = (EditText)findViewById(R.id.editText2);
        textView1 = (TextView)findViewById(R.id.textView1);
        textView2 = (TextView)findViewById(R.id.strXYmsj);
        textView31 = (TextView)findViewById(R.id.textView31);
        //textView36 = (TextView)findViewById(R.id.textView36);
		textView43 = (TextView)findViewById(R.id.textView43);
		textView42 = (TextView)findViewById(R.id.textView42);

		textView4  = (TextView)findViewById(R.id.textView4);
		textView34  = (TextView)findViewById(R.id.textView34);
		textView11  = (TextView)findViewById(R.id.textView11);

        myImage = (ImageView) findViewById(R.id.imageView1);

        editText1.setOnKeyListener(this);
        editText2.setOnKeyListener(this);
        
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        //recive datos del accion anterior
        Bundle bundle=getIntent().getExtras();
        pos = bundle.getString("pos");        
        envia_fac = bundle.getString("envia_fac");
        //Contendra datos si viene de Manin_tickets, ya que envia los ticketes a FACTURAR.
        tickets = bundle.getString("tickets");
        met_pago_f = bundle.getString("met_pago_f");
        digitos_f = bundle.getString("digitos_f");
        enti_banco_f = bundle.getString("enti_banco_f");
        user_sol = bundle.getString("user_sol");
		total_PB_S = bundle.getString("total_PB_S");
        
        bt.setText("Cliente");
        tipoFactura="C";
        editText1.setVisibility(View.INVISIBLE);
		editText2.setVisibility(View.VISIBLE);
		button22.setVisibility(View.INVISIBLE);
		//textView36.setVisibility(View.INVISIBLE);
		bandera_tck = false;

		if(tickets.length()==0){
			int i=Integer.parseInt(pos.replaceAll("[\\D]", ""));
			String pos_v = String.format("%02d", i);
			textView1.setText(pos_v);
			textView2.setText("");
		}else{
			myImage.setVisibility(View.INVISIBLE);
			textView1.setText("");
			textView2.setText(tickets);
		}

		switch(envia_fac) {
			case "PB":
				button4.setText("PAGO BANCARIO");
				button22.setVisibility(View.VISIBLE);
				textView31.setVisibility(View.VISIBLE);
				textView31.setText("FACTURACIÓN");
				bandera_tck = true;
				break;
			case "AL":
				String[] partes = tickets.split("-");
				String total_ven_s = partes[0];
				//String cantidad = partes[1];
				//String cod_barras = partes[2];
				textView31.setVisibility(View.VISIBLE);
				textView31.setText("VENTA TIENDA\n$"+ total_ven_s);
				textView2.setText("");
				myImage.setVisibility(View.INVISIBLE);
				textView1.setText("");
				break;
			//Version 2.0-0 MSM 13/Mar/2018
			case "AC":
				button4.setText("ACUMULADO");
				break;
		}



		/*
		if (envia_fac.equals("PB")){
			button4.setText("PAGO BANCARIO");
			button22.setVisibility(View.VISIBLE);
			textView36.setVisibility(View.VISIBLE);
			bandera_tck = true;
		}else{
			button22.setVisibility(View.INVISIBLE);
			textView36.setVisibility(View.INVISIBLE);
			bandera_tck = false;
		}
		
		if(envia_fac.equals("AL")){
			String[] partes = tickets.split("-");
		    String total_ven_s = partes[0];
		    //String cantidad = partes[1];
		    //String cod_barras = partes[2];
		    textView31.setVisibility(View.VISIBLE);
		    textView31.setText("VENTA TIENDA\n$"+ total_ven_s);
		    textView2.setText("");
		    myImage.setVisibility(View.INVISIBLE);
        	textView1.setText("");
		}else{
			if(tickets.length()==0){
	        	int i=Integer.parseInt(pos.replaceAll("[\\D]", ""));
	            String pos_v = String.format("%02d", i);
	            textView1.setText(pos_v);
	            textView2.setText("");
	        }else{
	        	myImage.setVisibility(View.INVISIBLE);
	        	textView1.setText("");
	            textView2.setText(tickets);
	        }
		}
		*/



	//MSM 05/Oct/2017 Ver:1.8-0
	//Inicializa datos para Conectar lector de Banda Magnetico
	//Identificar la Flotilla por Banda Magnatica o NFC
	if (bbDeviceController == null) {
		listener = new MyBBDeviceControllerListener();
		bbDeviceController = BBDeviceController.getInstance(this, listener);
		BBDeviceController.setDebugLogEnabled(true);
		bbDeviceController.setDetectAudioDevicePlugged(true);
	}
	estado_con = 0; //INICIA, Lanza la conexion del Lector
	rev_lectores(false, "");
        
        
    //Revisara que se introduce y lo cambia a mayusculas
    editText1.addTextChangedListener(new TextWatcher() {
      public void afterTextChanged(Editable s) {
      }
      public void beforeTextChanged(CharSequence s, int start,
        int count, int after) {
      }
      public void onTextChanged(CharSequence s, int start,
        int before, int count) {
       int con = s.length();
       String num = Integer.toString(con);
       if (con > 0 && control != con){
           control = con;
           //Pasa a MAYUSCULAS
           rfc_cli = editText1.getText().toString();
           rfc_cli = rfc_cli.toUpperCase();
           editText1.setText(rfc_cli);
           editText1.setSelection(con);
		   /*switch (estado_con) {
			   case 0://No esta conectado, Solo cancela reintentos, estado 0 (NO conectado):
				   rein_con = false;
				   vis_conec.setVisibility(View.INVISIBLE);
				   break;
			   case 2://Lector en espera de Tarjeta es Solo Cancelado
				   salir = false;
				   bbDeviceController.cancelCheckCard();
				   break;
		   }*/
           if (envia_fac.equals("PB")){
               solotck(null); //Desactiva la opcion de "Solo tickets"
           }
       }
      }
     });
    //FIN - Revisara que se introduce cambia a mayusculas

	//Revisara que se introduce datos en campo Flotilla para desactivar "SOLO TICKET"
	editText2.addTextChangedListener(new TextWatcher() {
		public void afterTextChanged(Editable s) {
		}
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			int con = s.length();
			//String num = Integer.toString(con);
			switch (estado_con) {
				case 0://No esta conectado, Solo cancela reintentos, estado 0 (NO conectado):
					rein_con = false;
					vis_conec.setVisibility(View.INVISIBLE);
					break;
				case 2://Lector en espera de Tarjeta es Solo Cancelado
					salir = false;
					bbDeviceController.cancelCheckCard();
					break;
			}

			if (con > 0 && control != con && envia_fac.equals("PB")){
				control = con;
				//Desactiva la opcion de "Solo tickets"
				solotck(null);
			}
		}
	});
	//FIN - Desactiva "SOLO TICKETS"

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
	//	initQR();
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
						ok( "F","");
						break;
					case R.id.editText2:
						ok("F", "");
						break;
				}
				return true;
			}
		}
		return false; // pass on to other listeners.
	}

	public void solotck(View view){
		String rvs = editText2.getText().toString();
		if (rvs.length()>0 && envia_fac.equals("PB")){
			button22.setVisibility(View.INVISIBLE);
			bandera_tck = false;

		}else{
			//button22.setVisibility(View.VISIBLE);
			//bandera_tck = true;
			rvs = editText1.getText().toString();
			if (rvs.length()>0 && envia_fac.equals("PB")){
				button22.setVisibility(View.INVISIBLE);
				bandera_tck = false;
			}else{
				button22.setVisibility(View.VISIBLE);
				bandera_tck = true;
			}
		}
	}

	public void cambia_fac(View view){
		//4 Posiones del BOTON PRINCIPAL
		if (cambia==0){
			bt.setText("Cliente");
			cambia=1;
			tipoFactura="C";
			editText1.setVisibility(View.INVISIBLE);
			editText2.setVisibility(View.VISIBLE);
			editText2.requestFocus();
			editText2.setText("");
		}else{
			if (cambia==1){
				bt.setText("RFC");
				cambia=0;
				tipoFactura="R";
				editText2.setVisibility(View.INVISIBLE);
				editText1.setVisibility(View.VISIBLE);
				editText1.requestFocus();
				editText1.setText("");
			}else{
					//Si se requiren mas Opciones
					Toast.makeText(this, "Opcion Incorrecta", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/*
	//Funcion Acciones
	public void revisar (String rev_coor) throws IOException{
		//Boton a HOME
		if (rev_coor.equals("H1") || rev_coor.equals("G1")){
			//bbDeviceController.disconnectBT();
			//finish();
			home(null);
	    }
		//Cambia de Opcion a procesar (HOME)
		if (rev_coor.equals("H3") || rev_coor.equals("H4") || rev_coor.equals("H5") || rev_coor.equals("H6")){
			home(null);
	    }
		//Boton a Cliente-RFC
		if (rev_coor.equals("F4") || rev_coor.equals("F5")){
			cambia_fac(null);
			salir = false;
			switch (estado_con) {
				case 0://No esta conectado, Solo cancela reintentos, estado 0 (NO conectado):
					rein_con = false;
					vis_conec.setVisibility(View.INVISIBLE);
					break;
				case 2://Lector en espera de Tarjeta es Solo Cancelado
					salir = false;
					bbDeviceController.cancelCheckCard();
					break;
			}
	    }
		//Boton a ok
		if (rev_coor.equals("D6")){
			if (envia_fac.equals("PB")){
				pgnull = true;
			}
			ok("F", "");
	    }
		
		//Boton NO a factuacion Solo TICKET
		if (rev_coor.equals("B3") || rev_coor.equals("B4") || rev_coor.equals("B5") || rev_coor.equals("B6") || rev_coor.equals("A3") || rev_coor.equals("A4") || rev_coor.equals("A5") || rev_coor.equals("A6") ){
			if (envia_fac.equals("PB") && bandera_tck){
				//MSM 05/Oct/2017 Ver:1.8-0
				rev_lectores(true, "T");
				//pago_banco();
			}
	    }

		//ACUMULA
		if (rev_coor.equals("C4") || rev_coor.equals("C5") || rev_coor.equals("B4") || rev_coor.equals("B5") ){
			if (envia_fac.equals("AC") && sig_acumula && env_ok){
				//MSM 05/Oct/2017 Ver:2.0-0 Acepta el usuario para acumulado
				env_ok = false;
				msj("Enviando...");
				ok("", "AC");
			}
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
	    * /
	}
	*/

	//Boton NO a factuacion, Solo TICKET
	public void ticketDirecto(View view){
		if (envia_fac.equals("PB") && bandera_tck){
			//MSM 05/Oct/2017 Ver:1.8-0
			rev_lectores(true, "T");
			//pago_banco();
		}
	}

	//Boton a ok, Revisa que sigue.
	public void ok_p1(View view){
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		if (envia_fac.equals("PB")){
			pgnull = true;
		}
		ok("F", "");
	}

	//ACUMULAR
	public void acumula_fin(View view){
		if (envia_fac.equals("AC") && sig_acumula && env_ok){
			//MSM 05/Oct/2017 Ver:2.0-0 Acepta el usuario para acumulado
			env_ok = false;
			msj("Enviando...");
			ok("", "AC");
		}
	}

	public void ok(String Busca_Flot, String ac){

			boolean sig = false;

			if (tipoFactura.equals("R")){
				rfc_cli = editText1.getText().toString();
				int tama = rfc_cli.length();
				if (tama>11){
					sig = true;
				}
			}else{
				rfc_cli = editText2.getText().toString();
				int tama = rfc_cli.length();
				if (tama>0 ){
					sig = true;
				}
			}
			if (sig){
				//Version 2.0-0 MSM 13/Mar/2018
				//Uso de Acumulado de Venta
				if (envia_fac.equals("AC")){
					//msj("ESPERAAAAAAA");
					//sol_flot("PR", Busca_Flot);
					sol_flot(rfc_cli, Busca_Flot, ac);
				}else {
					Intent i = new Intent(this, Fac_rapida1.class );
					i.putExtra("envia1", pos);
					i.putExtra("envia2", tipoFactura);
					i.putExtra("envia3", rfc_cli);
					i.putExtra("tickets", tickets);
					i.putExtra("met_pago_f", met_pago_f);
					i.putExtra("digitos_f", digitos_f);
					i.putExtra("enti_banco_f", enti_banco_f);
					i.putExtra("envia_fac", envia_fac);
					i.putExtra("user_sol", user_sol);
					i.putExtra("total_PB_S", total_PB_S);
					i.putExtra("qr", "0");
					startActivity(i);
					stopConnection();
					finish();
				}
			}else{
				if (envia_fac.equals("PB") && pgnull){
					//MSM 05/Oct/2017 Ver:1.8-0
					rev_lectores(true, "F");
					//pago_banco();
				}else{
					msj("NO VALIDO");
				}
			}
	}
	


	public void pago_banco(String compro){
		
		String tds = "";//, Enviando = "", URL ="";
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
				//Enviando=">"+num_tabled+">"+mac+">"+version+">"+mac_serial+">"+nomad+">"+intentos+">";
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
            //Datoas para la Aplicacion
            String conf_cb = pos+"\n";		//Pocicion de la venta, 99=Multiples tickets
			conf_cb += lector_cone+"\n";	//Dispositivo a Conectar.
            conf_cb += URL+"\n";			//Servidor SIGMA
            conf_cb += tds+"\n";			//Terinal VeriBox que Solicita
            conf_cb += user_sol +"\n";		//Usuario que Realiza la Transaccion
            conf_cb += compro +"\n";		//Tipo de Comprobante que se entrega T=Ticket, F=Factura
            conf_cb += "" +"\n";			//Tipo de Factura que se Realizara C=Cliente Registrado, R=RFC
            conf_cb += ""+"\n";				//Cliente a quien se Factura: RFC o No Cliente
			conf_cb += total_PB_S +"\n";				//Monto a Cobrar ya que son Multiples tickets
			conf_cb += tickets.replace("\n", "|") +"\n";//numeros de referencias para Cobro Bancario
                  
            fout.write(conf_cb);
            fout.close();

            //Toast.makeText(this, "Texto de prueba.3", Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex)
        {
            //Log.e("Ficheros", "Error al escribir fichero a tarjeta SD");
        	Toast.makeText(this, "Error de Fichero o Desbloquear", Toast.LENGTH_LONG).show();
        }

		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				stopLockTask();
			}
		}catch (Exception ex){}

		Intent launchIntent = getPackageManager().getLaunchIntentForPackage("mx.qpay.testsdk");
		startActivity(launchIntent);

		switch (estado_con) {
			case 0://No esta conectado cancela reintentos
				salir = true;
				rein_con = false;
				Toast.makeText(this, "S: SIN CONECTAR", Toast.LENGTH_LONG).show();
				break;
			case 2://Lector en espera de Tarjeta es Cancelado
				salir = true;
				bbDeviceController.cancelCheckCard();
				Toast.makeText(this, "S: CONECTARDO", Toast.LENGTH_LONG).show();
				break;
		}
	}
	
	
	public void msj(String msjcon){
		//int index = 1;
		msjcon =">"+ msjcon+ ">2>3>4>5>6>7>"; 	    
		Intent i = new Intent(this, Msj.class );
	    i.putExtra("msjcon", msjcon);
	    //i.putExtra("index", index);
	    startActivity(i);
	}

	/*
	public void msj0(String msjcon){
		//int index = 1;
		msjcon =">"+ msjcon+ ">0>3>4>5>6>7>";
		Intent i = new Intent(this, Msj.class );
		i.putExtra("msjcon", msjcon);
		//i.putExtra("index", index);
		startActivity(i);
	}
	*/
	
	public void home(View view){
		switch (estado_con) {
			case 0://No esta conectado aun asi Fuerza la salida
				salir = true;
				rein_con = false;
				msj("CERRANDO...");
				break;
			case 1: //Lector en espera de Tarjeta es Cancelado
				msj("CASO 1");
				break;
			case 2://Lector en espera de Tarjeta es Cancelado
				salir = true;
				bbDeviceController.cancelCheckCard();
				break;
			case 3://Lector desconectado sale de la interfaz
				salir = true;
				finish();
				break;
		}
		env_main();
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

	//Soliciata la FLOTILLA a la que pertenece el Codigo de Barras o el TAG-NFC
	public void sol_flot(String tarjeta_vehi, String Busca_Flot, String ac){

		//*****************************************************************************
		String Enviando = "";//URL = "";
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

		String[] partes = Enviando.split("\\>");
		String num_tabled = partes[1];
		String mac = partes[2];
		String version = partes[3];
		String mac_serial = partes[4];
		String nomad = partes[5];
		String intentos = partes[6];
		String text = "", text2 = "", tipo = "PR";

		//Version 2.0-0 MSM 13/Mar/2018
		//Uso de Acumulado de Venta
		String preset_sol = "3";
		if (Busca_Flot.equals("F")){
			preset_sol = "4";
		}

		if (ac.equals("AC")) {
			tipo = "AC";
			text2 += "	<acumulado sol=\"1\" usuario_flo=\"" + rfc_cli + "\" tipo_cliente=\"" + tipoFactura + "\"></acumulado>\n ";
		}else
			text2 += "	<preset sol=\""+preset_sol+"\" usuario=\""+tarjeta_vehi+"\" nip=\"****\" monto=\"\" monto_preset=\"\" odome_reg=\"\" tipo_venta=\"\" usuario_trj=\"\"></preset>\n";

		text = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"+
				"<peticion>\n"+
				"   <mensaje-tipo tipo=\""+tipo+"\"></mensaje-tipo>\n"+
				"	<envio tds=\""+num_tabled+"\" mac=\""+ mac+"\" version=\""+version+"\" mac_serial=\""+mac_serial+"\" nomad=\""+nomad+"\" intentos=\""+intentos+"\"></envio>\n"+
				"   <datos>\n"+
				"       <posicion pos=\""+pos+"\"></posicion>\n"+
				"       <usuario user_sol=\""+user_sol+"\"></usuario>\n";
		text += text2;
		text +="   </datos>\n"+
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
		String busca1 = "mensaje-tipo";
		String busca2 = "tipo";
		String dato=regresa_xml(xml, busca1,busca2);

		switch (dato) {
			case "PR":
				busca1 = "preset";
				busca2 = "respr";
				dato=regresa_xml(xml, busca1,busca2);
				if (dato.equals("TRUE")){
					busca2 = "flotilla";
					dato=regresa_xml(xml, busca1,busca2);
					//msj(dato);
					tipoFactura="C";
					editText2.setText(dato);
					textView34.setText(dato);
					rfc_cli = dato;
					if (usoNFC_TRJ){
						ok("" ,"AC");
					}else{
						muestra_flotilla (xml);
					}
				}else{
					msj("USUARIO:\nNO REGISTRADO");
					if (nfc_imp.length()>0)
						imprime_nfc();
					finish();
				}
				break;
			//Version 2.0-0 MSM 13/Mar/2018
			//Uso de Acumulado de Venta
			case "AC":
				busca1 = "acumulado";
				busca2 = "resac";
				dato=regresa_xml(xml, busca1,busca2);
				if (dato.equals("true")){
					msj("ACUMULADO:\nREALIZADO");
					finish();
				}else{
					msj("OCURRIO ALGO\nINESPERADO");
					finish();
				}
				break;
			default:
				msj("PETICION:\nSIN RESPUESTA");
				finish();
				break;
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

	//MSM 05/Oct/2017 Ver:1.8-0
	//Revisa lectores, si son mas de 1 envia a seleccinar Lector.
	public void rev_lectores(boolean cobra, String comprobante){
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		int usoLector = 0;
		Cursor fila = bd.rawQuery("select usoLector from config where num=1" + "", null);
		//Cursor fila = bd.rawQuery("select * from config where num=1"+ "", null);
		if (fila.moveToFirst()) {
			usoLector = fila.getInt(0);
		}


		String lector_busca="";
		switch (usoLector) {
			case 0://Se conecta al primer lector que encuentra en lista.
				//Se quita restriccion ya que los Dispositivos a partir de Abri 2018 no contiene esa identificacion
				//lector_busca = "WP";
				break;
			case 1://Busca Lector asociado a la Posicion.
				//String pos_busca = String.format("%02d", pos);¿
				fila = bd.rawQuery("select lec from lectores where posc like '%"+pos+"%'"
						+ "", null);
				if (fila.moveToFirst()) {
					lector_busca = fila.getString(0);

				}else{
					usoLector = 0;
					//lector_busca = "WP";
				}

				break;
			case 2://Busca Lector asociado al Usuario.
				fila = bd.rawQuery("select atributos from users where user = '"+user_sol+"'"
						+ "", null);
				if (fila.moveToFirst()) {
					lector_busca = fila.getString(0);
					if(lector_busca.length()==0){
						usoLector = 0;
						//lector_busca = "WP";
					}
				}else{
					usoLector = 0;
					//lector_busca = "WP";
				}
				break;
		}

		//msj(lector_busca);

		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		String compara = "";
		String nom_blue = "";
		//String [] lectores;
		//lectores = new String[5];
		int dis = 0, position_con = 0;
		Object[] pairedObjects = BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray();
		final BluetoothDevice[] pairedDevices = new BluetoothDevice[pairedObjects.length];
		for(int i = 0; i < pairedObjects.length; ++i) {
			pairedDevices[i] = (BluetoothDevice)pairedObjects[i];
		}
		for (int i = 0; i < pairedDevices.length; ++i) {
			nom_blue = pairedDevices[i].getName();

			if (usoLector != 0){
				compara = nom_blue;
				if (compara.equals(lector_busca)){
					//Registra los dispositivos encontrados
					//lector_cone = lectores[dis] = nom_blue;
					dis++;
					position_con = i;
					//Al encontrar por lo menos un lector lo asigna
					i = pairedDevices.length;
				}
			}else{
				dis++;
				position_con = i;
				//Al encontrar por lo menos un lector lo asigna
				i = pairedDevices.length;
			}


			/*
			if (usoLector == 0){compara = nom_blue.substring(0,2);}
			else {compara = nom_blue;}

			if (compara.equals(lector_busca)){
				//Registra los dispositivos encontrados
				//lector_cone = lectores[dis] = nom_blue;
				dis++;
				position_con = i;
				//Al encontrar por lo menos un lector lo asigna
				i = pairedDevices.length;
			}
			*/

		}

		if(dis == 0){
			if (lector_busca.length()>2)msj("Lector ASIGNADO,\nPero NO Emparejado");
			else msj("Lector NO\nVinculado.");
			textView42.setVisibility(View.INVISIBLE);
			textView43.setVisibility(View.INVISIBLE);
			estado_con = 3;
		}else{
			if(cobra){
				//stopConnection();
				salir = false;
				bbDeviceController.cancelCheckCard();
				pago_banco(comprobante);
			}else{
				lector_cone = pairedDevices[position_con].getName();
				//msj(lector_cone);
				vis_conec.setVisibility(View.VISIBLE);
				textView43.setText(lector_cone);
				bbDeviceController.connectBT(pairedDevices[position_con]);
			}
		}
	}

	//Se descarta la opcion de elegir Lector de Banda Magentica y se asigna el primero por defecto
	/*
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

	public void esperLector(){
		//BBPOS PASO 5
		Hashtable<String, Object> data = new Hashtable<String, Object>();
		checkCardMode = BBDeviceController.CheckCardMode.SWIPE_OR_TAP;
		data.put("checkCardMode", checkCardMode);
		data.put("checkCardTimeout", "120");
		//BBPOS PASO 6 Envia a Revisar la tarjeta
		bbDeviceController.checkCard(data);
		estado_con = 2;	//Lector en espera de Tarjeta
	}

	public void promptForStartNfcDetection() {
		Hashtable<String, Object> data = new Hashtable<String, Object>();
		String nfcCardDetectionTimeout = "15";
		if ((nfcCardDetectionTimeout != null) && (!nfcCardDetectionTimeout.equalsIgnoreCase(""))) {
			data.put("nfcCardDetectionTimeout", nfcCardDetectionTimeout);
		}
		//Tipo de lectura
		String nfcOperationMode = "1";
		if ((nfcOperationMode != null) && (!nfcOperationMode.equalsIgnoreCase(""))) {
			data.put("nfcOperationMode", nfcOperationMode);
		}
		bbDeviceController.startNfcDetection(data);
	}

	public void stopConnection() {
		vis_conec.setVisibility(View.INVISIBLE);
		estado_con = 3;	//Lector Desconectado
		ConnectionMode connectionMode = bbDeviceController.getConnectionMode();
		if (connectionMode == ConnectionMode.BLUETOOTH) {
			bbDeviceController.disconnectBT();
			//msj("Desconecta BLUE");
		} else if (connectionMode == ConnectionMode.AUDIO) {
			bbDeviceController.stopAudio();
		} else if (connectionMode == ConnectionMode.SERIAL) {
			bbDeviceController.stopSerial();
		} else if (connectionMode == ConnectionMode.USB) {
			bbDeviceController.stopUsb();
		}
		/*
		if (salir){
			finish();
		}
		*/
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		try{
			estado_con = 3;	//Lector Desconectado
			ConnectionMode connectionMode = bbDeviceController.getConnectionMode();
			if (connectionMode == ConnectionMode.BLUETOOTH) {
				bbDeviceController.disconnectBT();
			}
			bbDeviceController.releaseBBDeviceController();
			bbDeviceController = null;
			listener = null;
		}catch (Exception ex){}

	}

	public void reintento_manual(){
		Intent i = new Intent(this, Flotilla_rein.class);
		i.putExtra("msj", "NO SE ENCONTRO\nLECTOR DE USUARIOS");
		i.putExtra("ops1", "CANCELAR");
		i.putExtra("ops2", "REINTENTAR");
		startActivityForResult(i, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
		switch(requestCode) {
			case 0:
				if (resultCode == RESULT_OK) {
					//vis_conec.setVisibility(View.VISIBLE);
					estado_con = 0; //INICIA, Lanza la conexion del Lector
					rev_lectores(false, "");
				}else{
					vis_conec.setVisibility(View.INVISIBLE);
				}
				break;
			case 1:
				if (data != null && resultCode == RESULT_OK) {
					rev_QR(data.getStringExtra("token"));
				}
				/*
				if (resultCode == RESULT_OK) {
					String contents = data.getStringExtra("SCAN_RESULT");
					String format = data.getStringExtra("SCAN_RESULT_FORMAT");
					rev_QR(contents);
				} else if (resultCode == RESULT_CANCELED) {
					// Handle cancel
				}
				*/
				break;
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
				if (ActivityCompat.checkSelfPermission(Fac_rapida.this, Manifest.permission.CAMERA)
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

						//
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
		try{
			String[] res = data.split("\\|");
			//Valida si tiene los datos requeridos
			//	|CORREO	|RFC	|Razón Social	|CP
			if ((res[1].length() > 0) && (res[2].length() > 0) && (res[3].length() > 0) && (res[4].length() > 0)){
				//Detiene los reintentos en segundo plano de Lectores de Banda Magnetica
				rein_con = false;
				//Revisa que hacer con la informacion
				switch(envia_fac) {
					case "FA": case "PB": case"AL":
						//Envia datos de Cliente QR a Solicitar Factura
						Intent i = new Intent(this, Fac_rapida1.class );
						i.putExtra("envia1", pos);
						i.putExtra("envia2", "R");
						i.putExtra("envia3", res[1]+">"+res[2]+">"+res[3]+">"+res[4]);
						i.putExtra("tickets", tickets);
						i.putExtra("met_pago_f", met_pago_f);
						i.putExtra("digitos_f", digitos_f);
						i.putExtra("enti_banco_f", enti_banco_f);
						//i.putExtra("envia_fac", "QR");
						i.putExtra("envia_fac", envia_fac);
						i.putExtra("user_sol", user_sol);
						i.putExtra("total_PB_S", total_PB_S);
						i.putExtra("qr", "1");
						startActivity(i);
						finish();
                        break;
					case "AC":
						tipoFactura = "R";
						sol_flot(res[2], "F", "");
						break;
				}
			}else{
				//Toast.makeText(this, "QR INCOMPLETA",Toast.LENGTH_SHORT).show();
				msj("QR INCOMPLETA");
			}
		}catch(Exception e){
			//e.printStackTrace();
			msj("QR invalido para esta Aplicación,\nConsulte a su Estación de Servicio");
			//Toast.makeText(this, "No se Reconoce como un Código QR valido para esta Aplicación,\nConsulte a su Estación de Servicio",Toast.LENGTH_SHORT).show();
			//textView16.setText("No se Reconoce como un Código QR valido para esta Aplicación,\nConsulte a su Estación de Servicio");
		}
	}

	public void muestra_flotilla(String xml){

		sig_acumula = true;
		String busca1 = "preset";

		//Muestra RFC
		String busca2 = "rfc";
		String dato=regresa_xml(xml, busca1,busca2);

		textView4.setText(dato);
		busca2 = "ra_social";
		dato=regresa_xml(xml, busca1,busca2);
		//Muestra Flotilla
		textView11.setText(dato);
		//busca2 = "correo";
		//dato=regresa_xml(xml, busca1,busca2);

		muestra1.setVisibility(View.INVISIBLE);
		muestra2.setVisibility(View.INVISIBLE);
		muestra3.setVisibility(View.INVISIBLE);
		muestra4.setVisibility(View.VISIBLE);
		Toast.makeText(this, "OCULTA CAMARA",Toast.LENGTH_SHORT).show();
		//Oculta la lectura de QR
		cameraView.setVisibility(View.INVISIBLE);
		//cameraSource.stop();


	}

	public void imprime_nfc(){
		File path = new File(Environment.getExternalStorageDirectory(), "Tickets");
		path.mkdirs();

		//Una vez creado disponemos de un archivo para guardar datos
		try
		{
			File ruta_sd = Environment.getExternalStorageDirectory();

			File f = new File(ruta_sd.getAbsolutePath(), "Tickets/REFNFC.txt");

			OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(f));
			String conf_epos = "\n\n\n\n\n\n\nUsuario NFC no registrado\n\nRegistre en ADMINISTRACION\n\npara su uso como identificador.\n\n\n";
			conf_epos += "$TextSize$2\nNFC ID:"+nfc_imp+"\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";

			fout.write(conf_epos);
			fout.close();
		}
		catch (Exception ex)
		{
			Log.e("Ficheros", "Error al escribir fichero a tarjeta SD");
		}
	}

	/*
	public void oculta(View view){
		// Ocultar teclado virtual
		InputMethodManager imm_2 = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm_2.hideSoftInputFromWindow(editText1.getWindowToken(), 0);
	}
	*/

	public void teclado(View view){
		//Toast.makeText(this, "TECLADO 1", Toast.LENGTH_LONG).show();
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		/*if (cambia==0)
			editText2.requestFocus();
		else
			editText1.requestFocus();
		onBackPressed();
		*/
	}

	/*
	public void qr_busca(View view){
		Toast toast = Toast.makeText(this,"INICIANDO CAMARA...", Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

		salir = false;
		switch (estado_con) {
			case 0://No esta conectado, Solo cancela reintentos, estado 0 (NO conectado):
				rein_con = false;
				vis_conec.setVisibility(View.INVISIBLE);
				break;
			case 2://Lector en espera de Tarjeta es Solo Cancelado
				bbDeviceController.cancelCheckCard();
				break;
		}

		Intent i = new Intent(this, LeeQR.class);
		startActivityForResult(i, 1);


		/* POR SI SE REQUIRE PERMISOS
		//Verifica si el permiso de la cámara no está concedido
		if (ActivityCompat.checkSelfPermission(Fac_tem.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
			//Si el permiso no se encuentra concedido se solicita
			ActivityCompat.requestPermissions(Fac_tem.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
		} else {
			//Si el permiso esá concedico prosigue con el flujo normal
			Intent i = new Intent(this, LeeQR.class);
			startActivityForResult(i, 0);
		}
		*/
		/*
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		intent.putExtra("SCAN_MODE", "QR_CODE_MODE","FRONT_CAMERA");
		//intent.putExtra("SCAN_MODE", "QR_CODE_MODE","FRONT_CAMERA");
		startActivityForResult(intent, 1);
		* /

	}
	*/

	/*
	//Deshabilitar BOTON atras
	@Override
	public void onBackPressed() {
	}
	*/


	class MyBBDeviceControllerListener implements BBDeviceControllerListener {

		@Override
		public void onBTConnected(BluetoothDevice bluetoothDevice) {
			//Cuando se logra conectar pasa a esta Clase.
			// TODO Auto-generated method stub
			//BBPOS PASO 3
			//statusEditText.setText("Conectado" + ": " + bluetoothDevice.getAddress());
			//BBPOS PASO 4
			estado_con = 1;	//Lector Conectado
			textView42.setText("CONECTADO");
			esperLector();
			//Toast.makeText(Flotilla1.this, "***Conectado***", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onBatteryLow(BBDeviceController.BatteryStatus batteryStatus) {
			//Infica el nivel de la bateia baja y emite una alerta.
			if(batteryStatus == BBDeviceController.BatteryStatus.LOW) {
				//statusEditText.setText(getString(R.string.battery_low));
			} else if(batteryStatus == BBDeviceController.BatteryStatus.CRITICALLY_LOW) {
				//statusEditText.setText(getString(R.string.battery_critically_low));
			}
		}

		@Override
		public void onError(Error errorState, String errorMessage) {

			String content = "";
			if (errorState == Error.CMD_NOT_AVAILABLE) {
				content = getString(R.string.command_not_available);
			} else if (errorState == Error.TIMEOUT) {
				//content = getString(R.string.device_no_response);
				content = "Reiniciar Lector";
				msj ("Reiniciar Lector");
				finish();
			} else if (errorState == Error.UNKNOWN) {
				content = getString(R.string.unknown_error);
			} else if (errorState == Error.DEVICE_BUSY) {
				content = getString(R.string.device_busy);
			} else if (errorState == Error.INPUT_OUT_OF_RANGE) {
				content = getString(R.string.out_of_range);
			} else if (errorState == Error.INPUT_INVALID_FORMAT) {
				content = getString(R.string.invalid_format);
			} else if (errorState == Error.INPUT_INVALID) {
				content = getString(R.string.input_invalid);
			} else if (errorState == Error.CASHBACK_NOT_SUPPORTED) {
				content = getString(R.string.cashback_not_supported);
			} else if (errorState == Error.CRC_ERROR) {
				content = getString(R.string.crc_error);
			} else if (errorState == Error.COMM_ERROR) {
				content = getString(R.string.comm_error);
			} else if (errorState == Error.FAIL_TO_START_BT) {
				content = "BUSCA LECTOR...";
				//Reintenta la conexion al Lector
				//Fuerza el NO Reintentar
				//rein_con = false;
				//textView42.setVisibility(View.INVISIBLE);
				//textView43.setVisibility(View.INVISIBLE);
				//Fuerza el NO Reintentar
				estado_con = 3;
				if (rein_con){
					reintento_manual();
				}else{

					if(salir){
						content = ("Reconectando FIN");
						finish();
					}/*else{
						//Fuerza el NO Reintentar
						//msj ("NO ENCONTRO\nLECTOR DE USUARIOS");
						content = ("Reconectando 3");
						estado_con = 3;
						reintento_manual();
					}
					*/
				}

				/*
				if (rein_con){
					conta_reconex++;
					if (conta_reconex == 3){
						conta_reconex = 0;
						content = ("ENCIENDA O REINICIE\nLECTOR DE USUARIOS");
						msj ("ENCIENDA O REINICIE\nLECTOR DE USUARIOS");
					}
					rev_lectores(false, "");
				}else{
					if(salir){
						content = ("Reconectando FIN");
						finish();
					}else{
						//Fuerza el NO Reintentar
						//msj ("NO ENCONTRO\nLECTOR DE USUARIOS");
						content = ("Reconectando 3");
						estado_con = 3;
						reintento_manual();
					}
				}
				*/
			} else if (errorState == Error.FAIL_TO_START_AUDIO) {
				content = getString(R.string.fail_to_start_audio);
			} else if (errorState == Error.INVALID_FUNCTION_IN_CURRENT_CONNECTION_MODE) {
				content = getString(R.string.invalid_function);
			} else if (errorState == Error.COMM_LINK_UNINITIALIZED) {
				content = getString(R.string.comm_link_uninitialized);
				stopConnection();
			} else if (errorState == Error.BTV4_NOT_SUPPORTED) {
				content = getString(R.string.bluetooth_4_not_supported);
			} else if (errorState == Error.CHANNEL_BUFFER_FULL) {
				content = getString(R.string.channel_buffer_full);
			} else if (errorState == Error.BLUETOOTH_PERMISSION_DENIED) {
				content = getString(R.string.bluetooth_permission_denied);
			} else if (errorState == Error.VOLUME_WARNING_NOT_ACCEPTED) {
				content = getString(R.string.volume_warning_not_accepted);
			} else if (errorState == Error.FAIL_TO_START_SERIAL) {
				content = getString(R.string.fail_to_start_serial);
			} else if (errorState == Error.USB_DEVICE_NOT_FOUND) {
				content = getString(R.string.usb_device_not_found);
			} else if (errorState == Error.USB_DEVICE_PERMISSION_DENIED) {
				content = getString(R.string.usb_device_permission_denied);
			} else if (errorState == Error.USB_NOT_SUPPORTED) {
				content = getString(R.string.usb_not_supported);
			}

			/*
			if (errorMessage != null && !errorMessage.equals("")) {
				content += "\n" + getString(R.string.error_message) + errorMessage;
			}
			*/

			//msj(content);
		}

		@Override
		public void onReturnCancelCheckCardResult(boolean isSuccess) {
            if(isSuccess) {
				vis_conec.setVisibility(View.INVISIBLE);
				if(salir){
					finish();
				}else{
					//salir = true;
					stopConnection();
				}
				/*
                //MSM VerBox 1.8-0 Cambio para cancelar lectura de tarjeta
                if (cancelChkCard){
					cancelChkCard = false;
                    stopConnection();
                }else{
                    finish();
                }
                */
            }
		}

		@Override
		public void onReturnCheckCardResult(CheckCardResult checkCardResult, Hashtable<String, String> decodeData) {

			//BBPOS PASO 7 Regresa de la Lectura de tarjeta

			if(checkCardResult == CheckCardResult.NO_CARD) {
				//statusEditText.setText(getString(R.string.no_card_detected));
			} else if(checkCardResult == CheckCardResult.ICC) {
				//Reintenta nuevamente.
				msj("USAR BANDA MAGNETICA");
				Hashtable<String, Object> data = new Hashtable<String, Object>();
				data.put("checkCardMode", CheckCardMode.SWIPE_OR_TAP);
				bbDeviceController.checkCard(data);
			} else if(checkCardResult == CheckCardResult.NOT_ICC) {
				//Reintenta nuevamente.
				msj("USAR BANDA MAGNETICA");
				Hashtable<String, Object> data = new Hashtable<String, Object>();
				data.put("checkCardMode", CheckCardMode.SWIPE_OR_TAP);
				bbDeviceController.checkCard(data);
			} else if(checkCardResult == CheckCardResult.BAD_SWIPE) {
				//Reintenta nuevamente.
				msj("Intente \nNuevamente");

				Hashtable<String, Object> data = new Hashtable<String, Object>();
				data.put("checkCardMode", CheckCardMode.SWIPE_OR_TAP);
				bbDeviceController.checkCard(data);

			} else if(checkCardResult == CheckCardResult.MSR) {
				final String cardHolderName = decodeData.get("cardholderName");

				String content = getString(R.string.cardholder_name) + " " + cardHolderName + "\n";
				String tarjeta_vehi = cardHolderName.substring(9,25);
				stopConnection();
				//Toast.makeText(fac_rapida.this, tarjeta_vehi, Toast.LENGTH_SHORT).show();
				usoNFC_TRJ = true;
				sol_flot(tarjeta_vehi, "", "");
				//revisa_tarjeta(tarjeta_vehi);
			} else if(checkCardResult == CheckCardResult.TAP_CARD_DETECTED) {
				//Detecto Uso de NFC, inicia lectura de NFC
				promptForStartNfcDetection();
				//Toast.makeText(fac_rapida.this, "***BUSCA NFC***", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onReturnNfcDetectCardResult(NfcDetectCardResult nfcDetectCardResult, Hashtable<String, Object> data) {

			String val_nfc = (String) data.get("nfcCardUID");
			//if (detener){
				if (val_nfc.length()>0){
					//detener = false;
					Hashtable<String, Object> data2 = new Hashtable<String, Object>();
					String nfcCardRemovalTimeout = "15";
					if ((nfcCardRemovalTimeout != null) && (!nfcCardRemovalTimeout.equalsIgnoreCase(""))) {
						data2.put("nfcCardRemovalTimeout", nfcCardRemovalTimeout);
					}
                    //Toast.makeText(fac_rapida.this, val_nfc, Toast.LENGTH_SHORT).show();
					bbDeviceController.stopNfcDetection(data2);
					stopConnection();
					usoNFC_TRJ = true;
					nfc_imp = val_nfc;
					sol_flot(val_nfc, "", "");
				}
			/*
			}else{
				editText1.setText(val_nfc);
				tarjeta_vehi = val_nfc;
				txt_log += "Respuesta DETENR NFC:"+"\n";
				txt_log += "ENVIA:"+tarjeta_vehi+"\n";
				statusEditText.setText(txt_log);
				stopConnection();
				vehiculo_es = true;
				tagNFC=true;
				envi_vehi();
			}
			*/
		}

		@Override
		public void onReturnPhoneNumber(PhoneEntryResult phoneEntryResult, String phoneNumber) {
			//public void onReturnPhoneNumber(com.bbpos.bbdevice.BBDeviceController.PhoneEntryResult arg0,String arg1) {
			/*
			if(phoneEntryResult == PhoneEntryResult.ENTERED) {
				//Flotilla - paso 7 - Obtiene Numero de Telefono (NIP)

				statusEditText.setText(getString(R.string.phone_number) + " " + phoneNumber);
				String tel = phoneNumber;
				if (tel.length() > 3){
					txt_log += "TELEFONO: onReturnPhoneNumber()"+ tel+"\n";
					statusEditText.setText(txt_log);
					msj_fin();
					disponible(tel);//Envia a solicitar Disponible
				}
				else{
					msj1("Dato: \nIncorrecto");
					//wisePadController.startGetPhoneNumber();
					bbDeviceController.startGetPhoneNumber();
				}
			} else if(phoneEntryResult == PhoneEntryResult.TIMEOUT) {
				statusEditText.setText(getString(R.string.timeout));
				msj_fin();
				fin(null);
			} else if(phoneEntryResult == PhoneEntryResult.CANCEL) {
				statusEditText.setText(getString(R.string.canceled));
				msj_fin();
				fin(null);
			} else if(phoneEntryResult == PhoneEntryResult.WRONG_LENGTH) {
				statusEditText.setText(getString(R.string.wrong_length));
				msj_fin();
				fin(null);
			} else if(phoneEntryResult == PhoneEntryResult.BYPASS) {
				statusEditText.setText(getString(R.string.bypass));
				msj_fin();
				fin(null);
			}
			*/

		}

		@Override
		public void onWaitingForCard(BBDeviceController.CheckCardMode checkCardMode) {

		}

		@Override
		public void onWaitingReprintOrPrintNext() {

		}

		@Override
		public void onBTReturnScanResults(List<BluetoothDevice> list) {

		}

		@Override
		public void onBTScanTimeout() {
		}

		@Override
		public void onBTScanStopped() {

		}

		@Override
		public void onBTDisconnected() {

		}

		@Override
		public void onUsbConnected() {

		}

		@Override
		public void onUsbDisconnected() {

		}

		@Override
		public void onSerialConnected() {

		}

		@Override
		public void onSerialDisconnected() {

		}

		@Override
		public void onReturnDeviceInfo(Hashtable<String, String> hashtable) {

		}

		@Override
		public void onReturnTransactionResult(BBDeviceController.TransactionResult transactionResult) {

		}

		@Override
		public void onReturnBatchData(String s) {

		}

		@Override
		public void onReturnReversalData(String s) {

		}

		@Override
		public void onReturnAmountConfirmResult(boolean b) {

		}

		@Override
		public void onReturnPinEntryResult(BBDeviceController.PinEntryResult pinEntryResult, Hashtable<String, String> hashtable) {

		}

		@Override
		public void onReturnPrintResult(BBDeviceController.PrintResult printResult) {

		}

		@Override
		public void onReturnAmount(Hashtable<String, String> hashtable) {

		}

		@Override
		public void onReturnUpdateGprsSettingsResult(boolean b, Hashtable<String, BBDeviceController.TerminalSettingStatus> hashtable) {

		}

		@Override
		public void onReturnUpdateTerminalSettingResult(BBDeviceController.TerminalSettingStatus terminalSettingStatus) {

		}

		@Override
		public void onReturnUpdateWiFiSettingsResult(boolean b, Hashtable<String, BBDeviceController.TerminalSettingStatus> hashtable) {

		}

		@Override
		public void onReturnReadGprsSettingsResult(boolean b, Hashtable<String, Object> hashtable) {

		}

		@Override
		public void onReturnReadTerminalSettingResult(BBDeviceController.TerminalSettingStatus terminalSettingStatus, String s) {

		}

		@Override
		public void onReturnReadWiFiSettingsResult(boolean b, Hashtable<String, Object> hashtable) {

		}

		@Override
		public void onReturnEnableInputAmountResult(boolean b) {

		}

		@Override
		public void onReturnCAPKList(List<CAPK> list) {

		}

		@Override
		public void onReturnCAPKDetail(CAPK capk) {

		}

		@Override
		public void onReturnCAPKLocation(String s) {

		}

		@Override
		public void onReturnUpdateCAPKResult(boolean b) {

		}

		@Override
		public void onReturnEmvReportList(Hashtable<String, String> hashtable) {

		}

		@Override
		public void onReturnEmvReport(String s) {

		}

		@Override
		public void onReturnDisableInputAmountResult(boolean b) {

		}

		@Override
		public void onReturnEmvCardDataResult(boolean b, String s) {

		}

		@Override
		public void onReturnEmvCardNumber(boolean b, String s) {

		}

		@Override
		public void onReturnEncryptPinResult(boolean b, Hashtable<String, String> hashtable) {

		}

		@Override
		public void onReturnEncryptDataResult(boolean b, Hashtable<String, String> hashtable) {

		}

		@Override
		public void onReturnInjectSessionKeyResult(boolean b, Hashtable<String, String> hashtable) {

		}

		@Override
		public void onReturnPowerOnIccResult(boolean b, String s, String s1, int i) {

		}

		@Override
		public void onReturnPowerOffIccResult(boolean b) {

		}

		@Override
		public void onReturnApduResult(boolean b, Hashtable<String, Object> hashtable) {

		}

		@Override
		public void onRequestSelectApplication(ArrayList<String> arrayList) {

		}

		@Override
		public void onRequestSetAmount() {

		}

		@Override
		public void onRequestPinEntry(BBDeviceController.PinEntrySource pinEntrySource) {

		}

		@Override
		public void onRequestOnlineProcess(String s) {

		}

		@Override
		public void onRequestTerminalTime() {

		}

		@Override
		public void onRequestDisplayText(BBDeviceController.DisplayText displayText) {

		}

		@Override
		public void onRequestDisplayAsterisk(int i) {

		}

		@Override
		public void onRequestDisplayLEDIndicator(BBDeviceController.ContactlessStatus contactlessStatus) {

		}

		@Override
		public void onRequestProduceAudioTone(BBDeviceController.ContactlessStatusTone contactlessStatusTone) {

		}

		@Override
		public void onRequestClearDisplay() {

		}

		@Override
		public void onRequestFinalConfirm() {

		}

		@Override
		public void onRequestPrintData(int i, boolean b) {

		}

		@Override
		public void onPrintDataCancelled() {

		}

		@Override
		public void onPrintDataEnd() {

		}

		@Override
		public void onAudioDevicePlugged() {

		}

		@Override
		public void onAudioDeviceUnplugged() {

		}

		@Override
		public void onSessionInitialized() {

		}

		@Override
		public void onSessionError(BBDeviceController.SessionError sessionError, String s) {

		}

		@Override
		public void onAudioAutoConfigProgressUpdate(double v) {

		}

		@Override
		public void onAudioAutoConfigCompleted(boolean b, String s) {

		}

		@Override
		public void onAudioAutoConfigError(BBDeviceController.AudioAutoConfigError audioAutoConfigError) {

		}

		@Override
		public void onNoAudioDeviceDetected() {

		}

		@Override
		public void onDeviceHere(boolean b) {

		}

		@Override
		public void onReturnNfcDataExchangeResult(boolean b, Hashtable<String, String> hashtable) {

		}

		@Override
		public void onBarcodeReaderConnected() {

		}

		@Override
		public void onBarcodeReaderDisconnected() {

		}

		@Override
		public void onReturnBarcode(String s) {

		}
	}
	

}