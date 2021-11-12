package msm.aie.veribox;

/*
 * Ing Miguel Santiago Moreno
 * 03/Oct/2017 Ver:1.8-0
 * Se agrega la opcion para que muestre mas de un Lector Disponible
 */

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

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


public class Flotilla1 extends Activity implements OnKeyListener{
	private String txt_log = "";
	private boolean detener = true, rein_con, salir_flotilla;
    private boolean tagNFC = false;
	private String val_nfc;
	int conta_reco;
	//BBPOS
	protected static BBDeviceController bbDeviceController;
	protected static MyBBDeviceControllerListener listener;
	private static CheckCardMode checkCardMode;
	//FIN BBPOS


	private String pos, user_sol;
	private EditText editText1 ,editText2, editText3;
	private String monto_disponible, monto_produc, capturo,sol_odo, sol_trj;
	private String tarjeta_vehi, tarjeta_cb;
	private String tarjeta_valor;
	SQLiteDatabase bd;
	String Enviando;
	int MSJ=10;

	private BluetoothAdapter mBluetoothAdapter = null;
	private static final int REQUEST_ENABLE_BT = 2;

	private static final int PRODUCT_UNKNOWN = 0;
	private static final int PRODUCT_WISEPAD = 1;
	private static final int PRODUCT_WISEPAD_PLUS = 2;

	protected static String encryptedPinSessionKey = "";
	protected static String pinKcv = "";
	protected static String dataKcv = "";

	protected static String encryptedDataSessionKey = "";

	protected static String encryptedTrackSessionKey = "";
	protected static String trackKcv = "";

	protected static String encryptedMacSessionKey = "";
	protected static String macKcv = "";

	//private Button startButton;
	//private EditText amountEditText;
	//private static EditText statusEditText;
	private Dialog dialog;
	protected ListView listView;
	private ListView appListView;

	private String cashbackAmount = "";

	//private MyWisePadControllerListener listener;
	//private WisePadController wisePadController;
	private boolean isSwipeOnly = false;
	private String amount = "";

	private boolean isPinCanceled = false;
	private boolean isStarting = false;
	private boolean isPrintingSample = false;
	private int product = PRODUCT_UNKNOWN;

	protected List<BluetoothDevice> foundDevices;
	protected ArrayAdapter<String> arrayAdapter;

	private ArrayList<byte[]> receipts;
	private String cardholderName;
	private String cardNumber;
	//private String expiryDate;
	private String aid;
	private String appLabel;
	private String tc;
	private String batchNum;
	private String tid;
	private String mid;
	private String transactionDateTime;
	private boolean signatureRequired;
	//private TransactionResult result;
	private TextView textView1, textView2, textView42, textView43;

	private final String NAMESPACE = "urn:veriboxwsdl";
	private String URL;// = "http://192.168.1.38/Veribox/Veribox.php";
	private final String SOAPACTION = "urn:veriboxwsdl#veribox";
	private final String METHOD = "veribox";
	private boolean descone_nomad=true, cancelCard = false;
	private boolean vehiculo_es= true, tarjeta_es= false, desde_vei=false;
	//Captura de XY
	private TextView textView_xy, TextView001;
	StringBuilder stringBuilder = new StringBuilder();
	private Coor_xy cox_coy;
	int conta_reconex = 0;//Contador de Reintentos


	//Creamos el handler puente para mostrar
	//el mensaje recibido de peticiones
	private Handler puente2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (((String)msg.obj).equals("")){
				msj1("Problemas COM");
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
		setContentView(R.layout.flotilla1);

		//Oculta teclado virtual
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
				"tablet", null, 1);
		bd = admin.getWritableDatabase();

		//recive datos del accion anterior
		Bundle bundle=getIntent().getExtras();
		pos = bundle.getString("pos");
		user_sol = bundle.getString("user_sol");

		val_nfc = "";
		conta_reco = 0;
		rein_con = true;

		editText1 = (EditText)findViewById(R.id.editText1);
		editText2 = (EditText)findViewById(R.id.editText2);
		editText3 = (EditText)findViewById(R.id.editText3);
		textView1 = (TextView)findViewById(R.id.textView1);
		textView2 = (TextView)findViewById(R.id.strXYmsj);
		textView42 = (TextView)findViewById(R.id.textView42);
		textView43 = (TextView)findViewById(R.id.textView43);

		editText1.setOnKeyListener(this);
		editText2.setOnKeyListener(this);
		editText3.setOnKeyListener(this);

		int i=Integer.parseInt(pos.replaceAll("[\\D]", ""));
		String pos_v = String.format("%02d", i);
		textView2.setText(pos_v);
		//listener = new MyWisePadControllerListener();
		//wisePadController = new WisePadController(this, listener);
		//statusEditText = (EditText)findViewById(R.id.statusEditText);

		txt_log = "INICA "+pos+"\n";
		txt_log += "INICA "+val_nfc+"\n";
		//statusEditText.setText(txt_log);

		//Inicializa datos para Conectar lector de Banda Magnetico
		if (bbDeviceController == null) {
			listener = new MyBBDeviceControllerListener();
			bbDeviceController = BBDeviceController.getInstance(this, listener);
			BBDeviceController.setDebugLogEnabled(true);
			bbDeviceController.setDetectAudioDevicePlugged(true);
		}

		//textView3.setVisibility(View.INVISIBLE);
		editText1.setVisibility(View.VISIBLE);
		editText2.setVisibility(View.INVISIBLE);
		editText3.setVisibility(View.INVISIBLE);
		//Flotilla - paso 1 - Solicita Conecta WisePad
		// msj("Conectando. . .");
		//BBPOS PASO 1

		//ACTIVA Bluetooth de manera Forzada
		 mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!mBluetoothAdapter.isEnabled()) {
			mBluetoothAdapter.enable();
		 }

		//Envia a Conectar Lector de Banda Magnetica.
		promptForConnection();

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
				if (descone_nomad){
					//Toast.makeText(Flotilla1.this, "***TECLADO Detiene Conexion***", Toast.LENGTH_SHORT).show();
					rein_con = false;
					descone_nomad = false;
					cancelCard = true;
					bbDeviceController.cancelCheckCard();
					//stopConnection();
					textView42.setVisibility(View.INVISIBLE);
					textView43.setVisibility(View.INVISIBLE);

				}
				int con = s.length();
				String num = Integer.toString(con);
				//myOutputBox.setText(num);
				//editText1.setText(num);
				if (con==12){
					capturo = s.toString();
					String p1 = capturo.substring(0,1);
					if (p1.equals("9")){
						envi_vehi();
						//myTextBox.setText("");
						//textView1.setText("Vehiculo" + p1);
					}
				}
				if (con==17){
					capturo = s.toString();
					String p1 = capturo.substring(0,1);
					if (p1.equals("2")){
						//myTextBox.setText("");
						//textView1.setText("Tarjeta:" + p1);
						cap_tarjeta(capturo);
					}
				}
				if (con>17){
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
					revisar (rev);
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
	public void revisar (String rev_coor){
		//Boton a HOME
		if (rev_coor.equals("H1") || rev_coor.equals("G1")){
			//fin(null);
			//Toast.makeText(Flotilla1.this, "***HOME Fin flotilla y Salir***", Toast.LENGTH_SHORT).show();
			rein_con = false;
			salir_flotilla= true;
			bbDeviceController.cancelCheckCard();
			//stopConnection();
			if (salir_flotilla){
				//Toast.makeText(Flotilla1.this, "***FIN Flotilla***", Toast.LENGTH_SHORT).show();
				//cancelCard = true;
				//bbDeviceController.cancelCheckCard();
				finish();
				//System.exit(1);
			}
		}
		//Cambia de Opcion a procesar SALIR
		if (rev_coor.equals("H3") || rev_coor.equals("H4") || rev_coor.equals("H5") || rev_coor.equals("H6")){
			rein_con = false;
			salir_flotilla= true;
			//MSM 23Oct2017
			bbDeviceController.cancelCheckCard();
			//stopConnection();
			if (salir_flotilla){
				//Toast.makeText(Flotilla1.this, "***FIN Flotilla***", Toast.LENGTH_SHORT).show();
				finish();
				//System.exit(1);
			}
		}
		//
		if (rev_coor.equals("E6") || rev_coor.equals("D6")){
			ok(null);
		}

		if (rev_coor.equals("A1") || rev_coor.equals("A2")){
			//promptForConnection();
		}
	}

	@Override
	public boolean onKey(View view, int keyCode, KeyEvent event) {

		if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
				keyCode == EditorInfo.IME_ACTION_DONE ||
				event.getAction() == KeyEvent.ACTION_DOWN &&
						event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

			if (!event.isShiftPressed()) {
				Log.v("AndroidEnterKeyActivity","Enter Key Pressed!");
                //Toast.makeText(this, "ENTER EN CAJA", Toast.LENGTH_SHORT).show();
				switch (view.getId()) {
					case R.id.editText1:
						ok(null);
						//Toast.makeText(this, "ENTER EN 1", Toast.LENGTH_SHORT).show();
						break;
					case R.id.editText2:
						ok(null);
						//Toast.makeText(this, "ENTER EN 2",Toast.LENGTH_SHORT).show();
						break;
					case R.id.editText3:
						//Toast.makeText(this, "ENTER EN 3",Toast.LENGTH_SHORT).show();
						ok(null);
						break;
				}
				//Toast.makeText(this, "return 2",Toast.LENGTH_SHORT).show();
				return true;
			}

		}
		//Toast.makeText(this, "return 3",Toast.LENGTH_SHORT).show();
		return false; // pass on to other listeners.

	}


	//***************************************************************************************************************

	public void promptForStartNfcDetection() {
		Hashtable<String, Object> data = new Hashtable<String, Object>();
		String nfcCardDetectionTimeout = "15"; //((EditText) (dialog.findViewById(R.id.general1EditText))).getText().toString();
		if ((nfcCardDetectionTimeout != null) && (!nfcCardDetectionTimeout.equalsIgnoreCase(""))) {
			data.put("nfcCardDetectionTimeout", nfcCardDetectionTimeout);
		}
		String nfcOperationMode = "1"; //((EditText) (dialog.findViewById(R.id.general2EditText))).getText().toString();
		if ((nfcOperationMode != null) && (!nfcOperationMode.equalsIgnoreCase(""))) {
			data.put("nfcOperationMode", nfcOperationMode);
		}
		txt_log += "Funcion Busca NFC"+"\n";
		//statusEditText.setText(txt_log);
		bbDeviceController.startNfcDetection(data);
	}

	public void promptForConnection() {
        //MSM 23/Oct/2017 Ver:1.8-0
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

		//msj1(lector_busca);

		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		String compara = "";
		String nom_blue = "";
		String nom_blue_mues = "";
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
			nom_blue_mues = pairedDevices[i].getName();

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
			else msj1("Lector NO\nVinculado.");
			//finish();
		}else{
			textView43.setText(nom_blue_mues);
			bbDeviceController.connectBT(pairedDevices[position_con]);
		}

		/*
        //Se asigna el primer lector que se encuentra.
        String [] lectores;
        lectores = new String[5];
        int dis = 0, position_con = 0;
        Object[] pairedObjects = BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray();
        final BluetoothDevice[] pairedDevices = new BluetoothDevice[pairedObjects.length];
        for(int i = 0; i < pairedObjects.length; ++i) {
            pairedDevices[i] = (BluetoothDevice)pairedObjects[i];
        }
        for (int i = 0; i < pairedDevices.length; ++i) {
            String nom_blue = pairedDevices[i].getName();
            String compara = nom_blue.substring(0,2);
            if (compara.equals("WP")){
                //Registra los dispositivos encontrados
                //lector_cone = lectores[dis] = nom_blue;
                dis++;
                position_con = i;
                //Al encontrar por lo menos un lector lo asigna
                i = pairedDevices.length;
            }
        }


        */

	}


	/*
	Se Desactiva la Funcion de selecion de Lector

	//MSM 03/Oct/2017 Ver:1.8-0
	//Envia a pantalla la selecion del Lector a conectar
	private void espera_lector(String [] lectores){
		//Toast.makeText(this, "Envia USER.", Toast.LENGTH_LONG).show();
			Intent i = new Intent(this, Lectores.class);
			i.putExtra("lectores", lectores);
			startActivityForResult(i, 0);
	}

	//MSM 03/Oct/2017 Ver:1.8-0
	//Regresa de selecion del Lector a conectar
	protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
		if (requestCode == 0){
			if (data != null && resultCode == RESULT_OK) {
				String lecBusca = data.getStringExtra("lecBusca");
				//Toast.makeText(this, lecBusca, Toast.LENGTH_LONG).show();
				//Obtiene los Dispositivos Bluetooth
				int position_con = 50;
				Object[] pairedObjects = BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray();
				final BluetoothDevice[] pairedDevices = new BluetoothDevice[pairedObjects.length];
				for(int i = 0; i < pairedObjects.length; ++i) {
					pairedDevices[i] = (BluetoothDevice)pairedObjects[i];
				}
				for (int i = 0; i < pairedDevices.length; ++i) {
					String nom_blue = pairedDevices[i].getName();
					if (nom_blue.equals(lecBusca)){
						//Encontro el Lector
						position_con = i;
						//Toast.makeText(getApplicationContext(), nom_blue, Toast.LENGTH_LONG).show();
					}
				}
				if (position_con!=50){
					bbDeviceController.connectBT(pairedDevices[position_con]);
				}else{
					msj1("Lector\nNO Encontrado");
					rein_con = false;
					descone_nomad = false;
				}
			}
		}
	}

//***************************************************************************************************************
    */

	public void cap_tarjeta(String cap){
		//msj1(">TARJETA>2>3>4>5>6>7>");
		if(tarjeta_es){
			tarjeta_cb = editText1.getText().toString();
			//msj1("encontro TARJETA");
			obtiene_odo();
		}else{
			editText1.setText("");//Borra Captura
			msj1("IDENTIFIQUE VEHICULO.");
			//msj1(">IDENTIFIQUE VEHICULO.>2>3>4>5>6>7>");
		}
	}

	public void envi_vehi(){
		if (vehiculo_es){
			vehiculo_es = false;
			desde_vei = true;
		/*Intent i = new Intent(this, Flotilla2.class );
		String tarjeta = editText1.getText().toString();
		String desde = "f";
		i.putExtra("desde", desde);
	    i.putExtra("pos", pos);
	    i.putExtra("tarjeta", tarjeta);
	    i.putExtra("nip", "");
	    startActivity(i);
	    */
			//Envia a servidor preguntar por los datos.
			//Flotilla - paso 8.1 - Envia a servidor DATOS
			msj1("CONSULTANDO DATOS...");
			Leedb();
			MSJ=0;
			//mensajes("");
			gen_xml();

		}else{
			msj1("Ingrese TARJETA");
		}

	}


	public void dismissDialog() {
		if(dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}


	public static String randomNumber() {
		String s = "";
		for (int i = 0; i < 6; i++) {
			s += new Random().nextInt(10);
		}
		return s;
	}

	public void printReceipt() {
		//CAMBIOS AIE - MIGUEL SANTIAGO
		//Aqui se pondra la Imprecin pesonalizada de los datos EXPO
	}


	public void promptForAmount() {
		dismissDialog();
		//CAMBIOS AIE - MIGUEL SANTIAGO
		//Cambio para enviar directo el Monto de la VENTA
		//String amount = ((EditText)(dialog.findViewById(R.id.amountEditText))).getText().toString();
		String amount = monto_disponible;
		String cashbackAmount = "";//((EditText)(dialog.findViewById(R.id.cashbackAmountEditText))).getText().toString();
		String transactionTypeString = "GOODS";//(String)((Spinner)dialog.findViewById(R.id.transactionTypeSpinner)).getSelectedItem();
		String symbolString = "DOLLAR";//(String)((Spinner)dialog.findViewById(R.id.symbolSpinner)).getSelectedItem();
	/*
	TransactionType transactionType = TransactionType.GOODS;
	if(transactionTypeString.equals("GOODS")) {
		transactionType = TransactionType.GOODS;
	} else if(transactionTypeString.equals("SERVICES")) {
		transactionType = TransactionType.SERVICES;
	} else if(transactionTypeString.equals("CASHBACK")) {
		transactionType = TransactionType.CASHBACK;
	} else if(transactionTypeString.equals("INQUIRY")) {
		transactionType = TransactionType.INQUIRY;
	} else if(transactionTypeString.equals("TRANSFER")) {
		transactionType = TransactionType.TRANSFER;
	} else if(transactionTypeString.equals("PAYMENT")) {
		transactionType = TransactionType.PAYMENT;
	} else if(transactionTypeString.equals("REFUND")) {
		transactionType = TransactionType.REFUND;
	}

	CurrencyCharacter[] currencyCharacters = new CurrencyCharacter[] {CurrencyCharacter.A, CurrencyCharacter.B, CurrencyCharacter.C};
	if(symbolString.equals("DOLLAR")) {
		currencyCharacters = new CurrencyCharacter[] {CurrencyCharacter.DOLLAR};
	} else if(symbolString.equals("RUPEE")) {
		currencyCharacters = new CurrencyCharacter[] {CurrencyCharacter.RUPEE};
	} else if(symbolString.equals("YEN")) {
		currencyCharacters = new CurrencyCharacter[] {CurrencyCharacter.YEN};
	} else if(symbolString.equals("POUND")) {
		currencyCharacters = new CurrencyCharacter[] {CurrencyCharacter.POUND};
	} else if(symbolString.equals("EURO")) {
		currencyCharacters = new CurrencyCharacter[] {CurrencyCharacter.EURO};
	} else if(symbolString.equals("WON")) {
		currencyCharacters = new CurrencyCharacter[] {CurrencyCharacter.WON};
	} else if(symbolString.equals("DIRHAM")) {
		currencyCharacters = new CurrencyCharacter[] {CurrencyCharacter.DIRHAM};
	} else if(symbolString.equals("RIYAL")) {
		currencyCharacters = new CurrencyCharacter[] {CurrencyCharacter.RIYAL, CurrencyCharacter.RIYAL_2};
	} else if(symbolString.equals("AED")) {
		currencyCharacters = new CurrencyCharacter[] {CurrencyCharacter.A, CurrencyCharacter.E, CurrencyCharacter.D};
	} else if(symbolString.equals("BS.")) {
		currencyCharacters = new CurrencyCharacter[] {CurrencyCharacter.B, CurrencyCharacter.S, CurrencyCharacter.DOT};
	} else if(symbolString.equals("NULL")) {
		currencyCharacters = null;
	}

	if(wisePadController.setAmount(amount, cashbackAmount, "840", transactionType, currencyCharacters)) {
		//amountEditText.setText("$" + amount);
		Flotilla1.this.amount = amount;
		Flotilla1.this.cashbackAmount = cashbackAmount;
		//statusEditText.setText(getString(R.string.please_confirm_amount));
		dismissDialog();
	} else {
		promptForAmount();
	}
	*/
		//FIN - Cambio para enviar directo el Monto de la VENTA

	}

	private static byte[] hexToByteArray(String s) {
		if(s == null) {
			s = "";
		}
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		for(int i = 0; i < s.length() - 1; i += 2) {
			String data = s.substring(i, i + 2);
			bout.write(Integer.parseInt(data, 16));
		}
		return bout.toByteArray();
	}

	public void injectNextSessionKey() {
		if(!encryptedPinSessionKey.equals("")) {
			Hashtable<String, String> data = new Hashtable<String, String>();
			data.put("index", "1");
			data.put("encSK", encryptedPinSessionKey);
			data.put("kcv", pinKcv);
			//statusEditText.setText(getString(R.string.sending_encrypted_pin_session_key));
			encryptedPinSessionKey = "";
			//wisePadController.injectSessionKey(data);
			return;
		}

		if(!encryptedDataSessionKey.equals("")) {
			Hashtable<String, String> data = new Hashtable<String, String>();
			data.put("index", "2");
			data.put("encSK", encryptedDataSessionKey);
			data.put("kcv", dataKcv);
			//statusEditText.setText(getString(R.string.sending_encrypted_data_session_key));
			encryptedDataSessionKey = "";
			//wisePadController.injectSessionKey(data);
			return;
		}

		if(!encryptedTrackSessionKey.equals("")) {
			Hashtable<String, String> data = new Hashtable<String, String>();
			data.put("index", "3");
			data.put("encSK", encryptedTrackSessionKey);
			data.put("kcv", trackKcv);
			//statusEditText.setText(getString(R.string.sending_encrypted_track_session_key));
			encryptedTrackSessionKey = "";
			//wisePadController.injectSessionKey(data);
			return;
		}

		if(!encryptedMacSessionKey.equals("")) {
			Hashtable<String, String> data = new Hashtable<String, String>();
			data.put("index", "4");
			data.put("encSK", encryptedMacSessionKey);
			data.put("kcv", macKcv);
			//statusEditText.setText(getString(R.string.sending_encrypted_mac_session_key));
			encryptedMacSessionKey = "";
			//wisePadController.injectSessionKey(data);
			return;
		}
	}

/*
public void lee(View view){
	isPinCanceled = false;
	//amountEditText.setText("");

	if(product == PRODUCT_UNKNOWN) {
		isStarting = true;
		isPrintingSample = false;
		//statusEditText.setText(R.string.getting_info);
		//wisePadController.getDeviceInfo();
	} else {
		//statusEditText.setText(R.string.starting);
		isSwipeOnly = false;
		Hashtable<String, Object> data = new Hashtable<String, Object>();
		//data.put("checkCardMode", CheckCardMode.SWIPE_OR_INSERT);
		//wisePadController.checkCard(data);
	}
}
*/

	public void lee2(){
		//BBPOS PASO 5
		Hashtable<String, Object> data = new Hashtable<String, Object>();
		checkCardMode = CheckCardMode.SWIPE_OR_TAP;
		if(checkCardMode != null) {
			data.put("checkCardMode", checkCardMode);
		}
		data.put("checkCardMode", checkCardMode);
		data.put("checkCardTimeout", "120");

		//BBPOS PASO 6 Envia a Revisar la tarejta
		txt_log += "Inicia checkCard"+"\n";
		txt_log = "VUELTA:  "+pos+"\n";
		bbDeviceController.checkCard(data);

	}


	public void msj1(String msjcon){
		//int index = 1;
		msjcon =">"+ msjcon+ ">2>3>4>5>6>7>";
		Intent i = new Intent(this, Msj.class );
		i.putExtra("msjcon", msjcon);
		//i.putExtra("index", index);
		startActivity(i);
	}

	public void msj(String muestra){
		//Toast.makeText(this, "MSJ",	Toast.LENGTH_SHORT).show();
		//textView1.setText("");
		//int tiempo = 3;
		muestra =">"+ muestra+ ">2>3>4>5>6>7>";
		//----------------------------------------------------
		ContentValues registro = new ContentValues();
		registro.put("msj_esp", "1");
		int cant = bd.update("config", registro, "num=1", null);
		//bd.close();
		if (cant == 1){
			//Toast.makeText(this, "Modificacion Terminada", Toast.LENGTH_SHORT).show();
		}
		else
			Toast.makeText(this, "NO Graba en DB",Toast.LENGTH_SHORT).show();
		//----------------------------------------------------
		Intent j = new Intent(this, Msj_esp.class );
		j.putExtra("msjcon", muestra);
		//j.putExtra("tiempo", tiempo);
		startActivity(j);
	}

	public void msj_fin(){
		//----------------------------------------------------
		ContentValues registro = new ContentValues();
		registro.put("msj_esp", "0");
		int cant = bd.update("config", registro, "num=1", null);
		//bd.close();
		if (cant == 1){
			//Toast.makeText(this, "Modificacion Terminada", Toast.LENGTH_SHORT).show();
		}
		else
			Toast.makeText(this, "NO Graba en DB",
					Toast.LENGTH_SHORT).show();
		//----------------------------------------------------
	}

	public static void setStatus(String message) {
		////statusEditText.setText(message + "\n" + //statusEditText.getText().toString());
		////statusEditText.setText(message);
	}

	public void revisa_tarjeta(){
		//Flotilla - paso 5 - Revisa contenido de Tarjeta
		//Tarjeta   8888001000000025 = 188880000000 0025
		//String usuario = tarjeta_vehi.substring(12,16);
		//String estacion = tarjeta_vehi.substring(6,7)+tarjeta_vehi.substring(0,4);
		//descone_nomad=false;
		//editText1.setText("****"+usuario);
		//editText1.setText("****"+tarjeta_vehi);

		//if (tarjeta){
		//Flotilla - paso 6 - Solita Numero de Telefono (NIP)

		msj("CONTRASEÑA");
		//wisePadController.startGetPhoneNumber();
		txt_log += "Pide Contrasena:"+"\n";
		//statusEditText.setText(txt_log);
		bbDeviceController.startGetPhoneNumber();
		//}
	}

	class MyBBDeviceControllerListener implements BBDeviceControllerListener {

		@Override
		public void onWaitingForCard(
				com.bbpos.bbdevice.BBDeviceController.CheckCardMode arg0) {
			// TODO Auto-generated method stub
			//Toast.makeText(Flotilla1.this, "onWaitingForCard", Toast.LENGTH_SHORT).show();

		}

		@Override
		public void onBatteryLow(BBDeviceController.BatteryStatus batteryStatus) {
			//Infica el nivel de la bateia baja y emite una alerta.
			if(batteryStatus == BBDeviceController.BatteryStatus.LOW) {
				//statusEditText.setText(getString(R.string.battery_low));
			} else if(batteryStatus == BBDeviceController.BatteryStatus.CRITICALLY_LOW) {
				////statusEditText.setText(getString(R.string.battery_critically_low));
			}
		}


		@Override
		public void onError(Error errorState, String errorMessage) {

			String content = "";
			if (errorState == Error.CMD_NOT_AVAILABLE) {
				content = getString(R.string.command_not_available);
			} else if (errorState == Error.TIMEOUT) {
				content = getString(R.string.device_no_response);
				msj1 ("Reiniciar Lector");
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
				//Fuerza el NO Reintentar
				//rein_con = false;
				//textView42.setVisibility(View.INVISIBLE);
				//textView43.setVisibility(View.INVISIBLE);
				//Fuerza el NO Reintentar

				if (rein_con){
					reintento_manual();
					/*
					conta_reconex++;
					if (conta_reconex == 3){
						conta_reconex = 0;
						content = ("ENCIENDA O REINICIE\nLECTOR DE USUARIOS");
						msj1 ("ENCIENDA O REINICIE\nLECTOR DE USUARIOS");
					}
					content = "REINTENTA";
					conta_reco++;
					String conta_reco_s = String.format("%02d", conta_reco);
					txt_log += "REINTENTA..."+conta_reco_s+"\n";
					//statusEditText.setText(txt_log);
					//Toast.makeText(Flotilla1.this, "***REINTENTA***", Toast.LENGTH_SHORT).show();
					promptForConnection();
					*/
				}else{
					//Fuerza el NO Reintentar
					//if (!salir_flotilla)
					//	msj1 ("NO ENCONTRO\nLECTOR DE USUARIOS");
					//Toast.makeText(Flotilla1.this, "***CACNELA REINTENTOS***", Toast.LENGTH_SHORT).show();
					content = "DETIENE REINTENTA...";
					txt_log += "DETIENE REINTENTA..."+"\n";
					//statusEditText.setText(txt_log);
				}

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
			////statusEditText.setText(content);
		}

		@Override
		public void onBTConnected(BluetoothDevice bluetoothDevice) {
			//Cuando se logra conectar pasa a esta Clase.
			// TODO Auto-generated method stub
			//BBPOS PASO 3
			////statusEditText.setText("Conectado" + ": " + bluetoothDevice.getAddress());
			textView42.setText("CONECTADO.");
			//BBPOS PASO 4
			lee2();
			//Toast.makeText(Flotilla1.this, "***Conectado***", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onBTDisconnected() {
			//txt_log += "DESCONECTADO..."+"\n";
			////statusEditText.setText(txt_log);
			//fin2s();
		}

		@Override
		public void onReturnCancelCheckCardResult(boolean isSuccess) {
			if(isSuccess) {
				////statusEditText.setText(R.string.cancel_check_card_success);

				//MSM VerBox 1.8-0 Cambio para cancelar lectura de tarjeta
				if (cancelCard){
					cancelCard = false;
					//statusEditText.setText(R.string.cancel_check_card_fail);
					//Toast.makeText(Flotilla1.this, "DETIENE TARJETA", Toast.LENGTH_SHORT).show();
					stopConnection();
				}else{
					//Toast.makeText(Flotilla1.this, "CIERRA FLOTILLA", Toast.LENGTH_SHORT).show();
					finish();
				}
			}
		}

		@Override
		public void onReturnCheckCardResult(CheckCardResult checkCardResult, Hashtable<String, String> decodeData) {
			dismissDialog();
			if(checkCardResult == CheckCardResult.NO_CARD) {
				//statusEditText.setText(getString(R.string.no_card_detected));
			} else if(checkCardResult == CheckCardResult.ICC) {
				//statusEditText.setText(getString(R.string.icc_card_inserted));
				msj1("USAR BANDA MAGNETICA");
				isPinCanceled = false;

				//statusEditText.setText(R.string.starting);
				isSwipeOnly = false;
				Hashtable<String, Object> data = new Hashtable<String, Object>();
				data.put("checkCardMode", CheckCardMode.SWIPE_OR_TAP);
				bbDeviceController.checkCard(data);
			} else if(checkCardResult == CheckCardResult.NOT_ICC) {
				//statusEditText.setText(getString(R.string.card_inserted));
				msj1("USAR BANDA MAGNETICA");
				isPinCanceled = false;

				//statusEditText.setText(R.string.starting);
				isSwipeOnly = false;
				Hashtable<String, Object> data = new Hashtable<String, Object>();
				data.put("checkCardMode", CheckCardMode.SWIPE_OR_TAP);
				bbDeviceController.checkCard(data);
			} else if(checkCardResult == CheckCardResult.BAD_SWIPE) {
				//statusEditText.setText(getString(R.string.bad_swipe));
				txt_log += "BAD_SWIPE"+"\n";
				//Reintenta automaticamente.
				msj1("Intente \nNuevamente");
				isPinCanceled = false;

				//statusEditText.setText(R.string.starting);
				isSwipeOnly = false;
				Hashtable<String, Object> data = new Hashtable<String, Object>();
				data.put("checkCardMode", CheckCardMode.SWIPE_OR_TAP);
				bbDeviceController.checkCard(data);

			} else if(checkCardResult == CheckCardResult.MSR) {
				txt_log += "MSR"+"\n";
				String formatID = decodeData.get("formatID");
				final String maskedPAN = decodeData.get("maskedPAN");
				String PAN = decodeData.get("PAN");
				final String expiryDate = decodeData.get("expiryDate");
				final String cardHolderName = decodeData.get("cardholderName");
				String ksn = decodeData.get("ksn");
				String serviceCode = decodeData.get("serviceCode");
				String track1Length = decodeData.get("track1Length");
				String track2Length = decodeData.get("track2Length");
				String track3Length = decodeData.get("track3Length");
				String encTracks = decodeData.get("encTracks");
				String encTrack1 = decodeData.get("encTrack1");
				String encTrack2 = decodeData.get("encTrack2");
				String encTrack3 = decodeData.get("encTrack3");
				String track1Status = decodeData.get("track1Status");
				String track2Status = decodeData.get("track2Status");
				String track3Status = decodeData.get("track3Status");
				String partialTrack = decodeData.get("partialTrack");
				String productType = decodeData.get("productType");
				String trackEncoding = decodeData.get("trackEncoding");
				String randomNumber = decodeData.get("randomNumber");
				String finalMessage = decodeData.get("finalMessage");
				String encWorkingKey = decodeData.get("encWorkingKey");
				String mac = decodeData.get("mac");
				String serialNumber = decodeData.get("serialNumber");
				String bID = decodeData.get("bID");

				String content = getString(R.string.card_swiped) + "\n";
				content += getString(R.string.format_id) + " " + formatID + "\n";
				content += getString(R.string.masked_pan) + " " + maskedPAN + "\n";
				content += getString(R.string.pan) + " " + PAN + "\n";
				content += getString(R.string.expiry_date) + " " + expiryDate + "\n";
				content += getString(R.string.cardholder_name) + " " + cardHolderName + "\n";
				content += getString(R.string.ksn) + " " + ksn + "\n";
				content += getString(R.string.service_code) + " " + serviceCode + "\n";
				content += getString(R.string.track_1_length) + " " + track1Length + "\n";
				content += getString(R.string.track_2_length) + " " + track2Length + "\n";
				content += getString(R.string.track_3_length) + " " + track3Length + "\n";
				content += getString(R.string.encrypted_tracks) + " " + encTracks + "\n";
				content += getString(R.string.encrypted_track_1) + " " + encTrack1 + "\n";
				content += getString(R.string.encrypted_track_2) + " " + encTrack2 + "\n";
				content += getString(R.string.encrypted_track_3) + " " + encTrack3 + "\n";
				content += getString(R.string.track_1_status) + " " + track1Status + "\n";
				content += getString(R.string.track_2_status) + " " + track2Status + "\n";
				content += getString(R.string.track_3_status) + " " + track3Status + "\n";
				content += getString(R.string.partial_track) + " " + partialTrack + "\n";
				content += getString(R.string.product_type) + " " + productType + "\n";
				/*
				content += getString(R.string.track_encoding) + " " + trackEncoding + "\n";
				content += getString(R.string.random_number) + " " + randomNumber + "\n";
				content += getString(R.string.final_message) + " " + finalMessage + "\n";
				content += getString(R.string.encrypted_working_key) + " " + encWorkingKey + "\n";
				content += getString(R.string.mac) + " " + mac + "\n";
				*/
				if ((decodeData != null) && (decodeData.containsKey("data"))) {
					//content += getString(R.string.data) + decodeData.get("data");
				}

				if ((serialNumber != null) && (!serialNumber.equals(""))) {
					//content += getString(R.string.serial_number) + serialNumber + "\n";
				}

				if ((bID != null) && (!bID.equals(""))) {
					//content += getString(R.string.b_id) + "  :" + bID + "\n";
				}

				tarjeta_vehi = cardHolderName.substring(9,25);
				txt_log += "Tarjeta encontrada ="+tarjeta_vehi+"\n";
				//statusEditText.setText(txt_log);
				revisa_tarjeta();
			} else if(checkCardResult == CheckCardResult.MAG_HEAD_FAIL) {
				//statusEditText.setText(getString(R.string.mag_head_fail));
			} else if(checkCardResult == CheckCardResult.USE_ICC_CARD) {
				String content = "ICC **** \n";//getString(R.string.use_icc_card) + "\n";
				if(decodeData != null) {
					/*
					String formatID = decodeData.get("formatID");
					final String maskedPAN = decodeData.get("maskedPAN");
					String PAN = decodeData.get("PAN");
					final String expiryDate = decodeData.get("expiryDate");
					final String cardHolderName = decodeData.get("cardholderName");
					String ksn = decodeData.get("ksn");
					String serviceCode = decodeData.get("serviceCode");
					String track1Length = decodeData.get("track1Length");
					String track2Length = decodeData.get("track2Length");
					String track3Length = decodeData.get("track3Length");
					String encTracks = decodeData.get("encTracks");
					String encTrack1 = decodeData.get("encTrack1");
					String encTrack2 = decodeData.get("encTrack2");
					String encTrack3 = decodeData.get("encTrack3");
					String track1Status = decodeData.get("track1Status");
					String track2Status = decodeData.get("track2Status");
					String track3Status = decodeData.get("track3Status");
					String partialTrack = decodeData.get("partialTrack");
					String productType = decodeData.get("productType");
					String trackEncoding = decodeData.get("trackEncoding");
					String randomNumber = decodeData.get("randomNumber");
					String encWorkingKey = decodeData.get("encWorkingKey");
					String mac = decodeData.get("mac");

					content += getString(R.string.format_id) + " " + formatID + "\n";
					content += getString(R.string.masked_pan) + " " + maskedPAN + "\n";
					content += getString(R.string.pan) + " " + PAN + "\n";
					content += getString(R.string.expiry_date) + " " + expiryDate + "\n";
					content += getString(R.string.cardholder_name) + " " + cardHolderName + "\n";
					content += getString(R.string.ksn) + " " + ksn + "\n";
					content += getString(R.string.service_code) + " " + serviceCode + "\n";
					content += getString(R.string.track_1_length) + " " + track1Length + "\n";
					content += getString(R.string.track_2_length) + " " + track2Length + "\n";
					content += getString(R.string.track_3_length) + " " + track3Length + "\n";
					content += getString(R.string.encrypted_tracks) + " " + encTracks + "\n";
					content += getString(R.string.encrypted_track_1) + " " + encTrack1 + "\n";
					content += getString(R.string.encrypted_track_2) + " " + encTrack2 + "\n";
					content += getString(R.string.encrypted_track_3) + " " + encTrack3 + "\n";
					content += getString(R.string.track_1_status) + " " + track1Status + "\n";
					content += getString(R.string.track_2_status) + " " + track2Status + "\n";
					content += getString(R.string.track_3_status) + " " + track3Status + "\n";
					content += getString(R.string.partial_track) + " " + partialTrack + "\n";
					content += getString(R.string.product_type) + " " + productType + "\n";

					content += getString(R.string.track_encoding) + " " + trackEncoding + "\n";
					content += getString(R.string.random_number) + " " + randomNumber + "\n";
					content += getString(R.string.encrypted_working_key) + " " + encWorkingKey + "\n";
					content += getString(R.string.mac) + " " + mac + "\n";
					*/

				}
				//statusEditText.setText(content);
			} else if(checkCardResult == CheckCardResult.TAP_CARD_DETECTED) {
				txt_log += "NFC Encontrado="+"\n";
				txt_log += "Envia a Detectar NFC por otro medio"+"\n";
				txt_log += "CANCELO NFC"+"\n";
				//statusEditText.setText(txt_log);
				promptForStartNfcDetection();
			}
		}

		@Override
		public void onReturnNfcDetectCardResult(NfcDetectCardResult nfcDetectCardResult, Hashtable<String, Object> data) {

			String text = "";
			//text += getString(R.string.nfc_card_detection_result) + nfcDetectCardResult;
			//text += "\n" + getString(R.string.nfc_tag_information) + data.get("nfcTagInfo");
			text = (String) data.get("nfcCardUID");
			if (data.containsKey("errorMessage")) {
				text += "\n" + getString(R.string.error_message) + data.get("errorMessage");
			}
			//setStatus(text);
			//tarjeta_vehi = "DETENIDO:"+text;
			////statusEditText.setText(tarjeta_vehi);
			if (detener){
				if (text.length()>0){
					detener = false;
					val_nfc = text;
					txt_log += "DATOS>0 :"+val_nfc+"\n";

					txt_log += "DETECTO NFC:"+val_nfc+", a solicitud"+"\n";
					//statusEditText.setText(txt_log);

					Hashtable<String, Object> data2 = new Hashtable<String, Object>();
					String nfcCardRemovalTimeout = "15";
					if ((nfcCardRemovalTimeout != null) && (!nfcCardRemovalTimeout.equalsIgnoreCase(""))) {
						data2.put("nfcCardRemovalTimeout", nfcCardRemovalTimeout);
					}
					txt_log += "DETENR NFC:"+"\n";
					//statusEditText.setText(txt_log);
					bbDeviceController.stopNfcDetection(data2);
				}
			}else{
				editText1.setText(val_nfc);
				tarjeta_vehi = val_nfc;
				txt_log += "Respuesta DETENR NFC:"+"\n";
				txt_log += "ENVIA:"+tarjeta_vehi+"\n";
				//statusEditText.setText(txt_log);
				stopConnection();
				vehiculo_es = true;
				tagNFC=true;
				envi_vehi();
			}
		}

		@Override
		public void onReturnPhoneNumber(PhoneEntryResult phoneEntryResult, String phoneNumber) {
			//public void onReturnPhoneNumber(com.bbpos.bbdevice.BBDeviceController.PhoneEntryResult arg0,String arg1) {
			if(phoneEntryResult == PhoneEntryResult.ENTERED) {
				//Flotilla - paso 7 - Obtiene Numero de Telefono (NIP)

				//statusEditText.setText(getString(R.string.phone_number) + " " + phoneNumber);
				String tel = phoneNumber;
				if (tel.length() > 3){
					txt_log += "TELEFONO: onReturnPhoneNumber()"+ tel+"\n";
					//statusEditText.setText(txt_log);
					msj_fin();
					disponible(tel);//Envia a solicitar Disponible
				}
				else{
					msj1("Dato: \nIncorrecto");
					//wisePadController.startGetPhoneNumber();
					bbDeviceController.startGetPhoneNumber();
				}
			} else if(phoneEntryResult == PhoneEntryResult.TIMEOUT) {
				//statusEditText.setText(getString(R.string.timeout));
				msj_fin();
				fin(null);
			} else if(phoneEntryResult == PhoneEntryResult.CANCEL) {
				//statusEditText.setText(getString(R.string.canceled));
				//Toast.makeText(Flotilla1.this, "phoneEntryResult", Toast.LENGTH_SHORT).show();
				msj_fin();
				fin(null);
			} else if(phoneEntryResult == PhoneEntryResult.WRONG_LENGTH) {
				//statusEditText.setText(getString(R.string.wrong_length));
				msj_fin();
				fin(null);
			} else if(phoneEntryResult == PhoneEntryResult.BYPASS) {
				//statusEditText.setText(getString(R.string.bypass));
				msj_fin();
				fin(null);
			}

		}

		@Override
		public void onAudioAutoConfigCompleted(boolean arg0, String arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAudioAutoConfigError(AudioAutoConfigError arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAudioAutoConfigProgressUpdate(double arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAudioDevicePlugged() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAudioDeviceUnplugged() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onBTReturnScanResults(List<BluetoothDevice> arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onBTScanStopped() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onBTScanTimeout() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onBarcodeReaderConnected() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onBarcodeReaderDisconnected() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDeviceHere(boolean arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onNoAudioDeviceDetected() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPrintDataCancelled() {
			Toast.makeText(Flotilla1.this, "onPrintDataCancelled", Toast.LENGTH_SHORT).show();
			// TODO Auto-generated method stub

		}

		@Override
		public void onPrintDataEnd() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestClearDisplay() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestDisplayAsterisk(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestDisplayLEDIndicator(ContactlessStatus arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestDisplayText(
				com.bbpos.bbdevice.BBDeviceController.DisplayText arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestFinalConfirm() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestOnlineProcess(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestPinEntry(PinEntrySource arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestPrintData(int arg0, boolean arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestProduceAudioTone(ContactlessStatusTone arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestSelectApplication(ArrayList<String> arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestSetAmount() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestTerminalTime() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnAmount(Hashtable<String, String> arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnAmountConfirmResult(boolean arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnApduResult(boolean arg0,
									   Hashtable<String, Object> arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnBarcode(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnBatchData(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnCAPKDetail(CAPK arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnCAPKList(List<CAPK> arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnCAPKLocation(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnDeviceInfo(Hashtable<String, String> arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnDisableInputAmountResult(boolean arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnEmvCardDataResult(boolean arg0, String arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnEmvCardNumber(boolean arg0, String arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnEmvReport(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnEmvReportList(Hashtable<String, String> arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnEnableInputAmountResult(boolean arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnEncryptDataResult(boolean arg0,
											  Hashtable<String, String> arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnEncryptPinResult(boolean arg0,
											 Hashtable<String, String> arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnInjectSessionKeyResult(boolean arg0,
												   Hashtable<String, String> arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnNfcDataExchangeResult(boolean arg0,
												  Hashtable<String, String> arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnPinEntryResult(
				com.bbpos.bbdevice.BBDeviceController.PinEntryResult arg0,
				Hashtable<String, String> arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnPowerOffIccResult(boolean arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnPowerOnIccResult(boolean arg0, String arg1,
											 String arg2, int arg3) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnPrintResult(PrintResult arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnReadGprsSettingsResult(boolean arg0,
												   Hashtable<String, Object> arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnReadTerminalSettingResult(
				com.bbpos.bbdevice.BBDeviceController.TerminalSettingStatus arg0,
				String arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnReadWiFiSettingsResult(boolean arg0,
												   Hashtable<String, Object> arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnReversalData(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnTransactionResult(
				com.bbpos.bbdevice.BBDeviceController.TransactionResult arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnUpdateCAPKResult(boolean arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnUpdateGprsSettingsResult(
				boolean arg0,
				Hashtable<String, com.bbpos.bbdevice.BBDeviceController.TerminalSettingStatus> arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnUpdateTerminalSettingResult(
				com.bbpos.bbdevice.BBDeviceController.TerminalSettingStatus arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReturnUpdateWiFiSettingsResult(
				boolean arg0,
				Hashtable<String, com.bbpos.bbdevice.BBDeviceController.TerminalSettingStatus> arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSerialConnected() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSerialDisconnected() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSessionError(SessionError arg0, String arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSessionInitialized() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUsbConnected() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUsbDisconnected() {
			// TODO Auto-generated method stub

		}



		@Override
		public void onWaitingReprintOrPrintNext() {
			// TODO Auto-generated method stub

		}

	}


	public void fin_pres(String mues){
		//stopConnection();
		//Intent i = new Intent(this, MainActivity.class );
		//i.putExtra("inicia", 1);
		//startActivity(i);
		msj1(mues);
		txt_log += "fin_pres finish"+"\n";
		//statusEditText.setText(txt_log);
		env_main();
		//Toast.makeText(Flotilla1.this, "fin_pres", Toast.LENGTH_SHORT).show();
		fin2s();
	}

	public void fin2s (){
		rein_con = false;
		//Toast.makeText(Flotilla1.this, "***FIN 2 SEGUNDOS***", Toast.LENGTH_SHORT).show();
		//finish();

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				// acciones que se ejecutan tras los milisegundos
				//Toast.makeText(Flotilla1.this, "***FIN 3 SEGUNDOS***", Toast.LENGTH_SHORT).show();
				finish();
			}
		}, 3000);

	}

	//Envia a Flotilla2 los datos de la TARJETA y el Telefono (NIP).
	public void disponible(String nip){
		//Flotilla - paso 8 - Envia DATOS a siguiente Interfaz.
		Intent i = new Intent(this, Flotilla2.class );
		String desde = "f";
		i.putExtra("desde", desde);
		i.putExtra("pos", pos);
		i.putExtra("tarjeta", tarjeta_vehi);
		i.putExtra("nip", nip);
		i.putExtra("user_sol", user_sol);
		startActivity(i);
		stopConnection();
		//Toast.makeText(Flotilla1.this, "DISPONIBLE  POS:"+pos , Toast.LENGTH_SHORT).show();
		finish();
		//fin2s();
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

	public void gen_xml(){

		String[] partes = Enviando.split("\\>");
		String num_tabled = partes[1];
		String mac = partes[2];
		String version = partes[3];
		String mac_serial = partes[4];
		String nomad = partes[5];
		String intentos = partes[6];

		tarjeta_vehi = editText1.getText().toString();
		editText1.setVisibility(View.INVISIBLE);

		final String text = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"+
				"<peticion>\n"+
				"   <mensaje-tipo tipo=\"PR\"></mensaje-tipo>\n"+
				"	<envio tds=\""+num_tabled+"\" mac=\""+ mac+"\" version=\""+version+"\" mac_serial=\""+mac_serial+"\" nomad=\""+nomad+"\" intentos=\""+intentos+"\"></envio>\n"+
				"   <datos>\n"+
				"       <posicion pos=\""+pos+"\"></posicion>\n"+
				"       <usuario user_sol=\""+user_sol+"\"></usuario>\n"+
				"		<preset sol=\"1\" usuario=\""+tarjeta_vehi+"\" nip=\"----\" monto=\"\" monto_preset=\"\" odome_reg=\"\" tipo_venta=\"\" usuario_trj=\"\" />"+
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
				puente2.sendMessage(sms);
			}
		}).start();
	}

	public void res_ser(String xml){
		String busca1 = "preset";
		String busca2 = "respr";
		String dato=regresa_xml(xml, busca1,busca2);
		//Respuesta Correcta de mesaje M2
		//Flotilla - paso 9 - Respuesta de SERVIDOR
		//Dividir una cadena en partes por |
		//String[] partes = cadena.split("\\>");
		//String res_pross = partes[0];

		if (dato.equals("TRUE")){
			busca2 = "sol";
			dato=regresa_xml(xml, busca1,busca2);
			String sol = dato;

			busca2 = "monto";
			dato=regresa_xml(xml, busca1,busca2);
			monto_disponible = dato;

			busca2 = "producto_max";
			dato=regresa_xml(xml, busca1,busca2);
			monto_produc = dato;

			busca2 = "pide_odom";
			dato=regresa_xml(xml, busca1,busca2);
			sol_odo = dato;

			busca2 = "pide_tarjeta";
			dato=regresa_xml(xml, busca1,busca2);
			sol_trj = dato;

			rev_trj();

			//msj(dato );
			/*
			String sol = partes[1];
			monto_disponible = partes[2];
			monto_produc = partes[4];
			sol_odo = partes[5];
			sol_trj = partes[6];
			rev_trj();
			//obtiene_odo();
			*/
		}else{
			//Flotilla - paso 20.1 - No es correcto algo, mostrara que sea revisado en administracion.
			fin_pres("Consulte en:\nADMINISTRACION:");
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


	/*
public void mensajes(String cadena){
//Envia mensajes a servidor secuencialmente	
	if (MSJ==3){
		MSJ=10;
		//Respuesta Correcta de mesaje M2
		//Flotilla - paso 9 - Respuesta de SERVIDOR
		//Dividir una cadena en partes por |
	    String[] partes = cadena.split("\\>");
	    String res_pross = partes[0];
	   
	    if (res_pross.equals("TRUE")){
		    String sol = partes[1];
		    monto_disponible = partes[2];
		    monto_produc = partes[4];
		    sol_odo = partes[5];
		    sol_trj = partes[6];
		    rev_trj();
		 	//obtiene_odo();
	    }else{
	    	//Flotilla - paso 20.1 - No es correcto algo, mostrara que sea revisado en administracion.
	    	fin_pres("Consulte en:\nADMINISTRACION:");
	    }
	}
	//Mensaje DATOS-Tranzaccion esperamos la respuesta con los datos
	if (MSJ==2){
		MSJ=3;
		
		//String[] partes = cadena.split("\\>");
		//String muestra = partes[1];
		//msj1(muestra);
	    
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
		MSJ=2;
		tarjeta_vehi = editText1.getText().toString();
		editText1.setVisibility(View.INVISIBLE);
		cadena = "----";
		String Datos = ">"+"PR"+">"+pos+">";
		String DatosTransa = ">"+"1"+">"+tarjeta_vehi+">"+cadena+">"+"f";
		
		txt_log += "MSJ0:"+Datos+"\n";
		txt_log += "MSJ0:"+DatosTransa+"\n";
		//statusEditText.setText(txt_log);
		
		envia("M0",Datos,DatosTransa);
	}
}
	* /

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

	public void rev_trj(){
		if (sol_trj.equals("1")){
			//Toast.makeText(this, "PIDE TARJETA",Toast.LENGTH_SHORT).show();
			tarjeta_es = true;
			editText1.setVisibility(View.VISIBLE);
			editText1.requestFocus();
			editText1.setText("");
			textView1.setText("\nIngrese TARJETA");
			msj1("Ingrese TARJETA");

		}else{
			tarjeta_cb = "----";
			obtiene_odo();
		}
	}

	public void obtiene_odo(){
		if (sol_odo.equals("1")){
			//Toast.makeText(this, "PIDE ODOMETRO 1",Toast.LENGTH_SHORT).show();

			editText1.setVisibility(View.INVISIBLE);
			editText3.setVisibility(View.VISIBLE);


			textView1.setText("\nIngrese ODOMETRO");
			msj1("Ingrese ODOMETRO");
			editText3.requestFocus();

		}else{
			//Toast.makeText(this, " NO ODOMETRO",Toast.LENGTH_SHORT).show();
			String odo_s = "--";
			Float monto_disponible_f = Float.parseFloat(monto_disponible);
			Intent j;
			if (desde_vei){
				j = new Intent(this, Flotilla4.class );
			}else
				j = new Intent(this, Flotilla2.class );
			j.putExtra("pos", pos);
			j.putExtra("envia2", monto_disponible_f);
			j.putExtra("envia3", 2); //Es por pesos el maximo del saldo entre Flotilla y Usuario
			j.putExtra("tarjeta", tarjeta_vehi);
			j.putExtra("odo_s", odo_s);
			j.putExtra("tarjeta_cb", tarjeta_cb);
			j.putExtra("user_sol", user_sol);

			//msj("Procesando Solicitud..." + pos+preset_num+tipo_venta);
			//textView4.setText("Procesando Solicitud..." + pos+preset_num+tipo_venta );

			txt_log += "obtiene_odo finish"+"\n";
			txt_log += pos+"\n";
			//statusEditText.setText(txt_log);
			startActivity(j);
			msj1("DATOS ENVIA");
			//Toast.makeText(Flotilla1.this, "obtiene_odo", Toast.LENGTH_SHORT).show();
			fin2s();
		}
	}


	public void fin(View view){
		rein_con = false;
		//Intent i = new Intent(this, MainActivity.class );
		//i.putExtra("inicia", 1);
		txt_log += "fin finish"+"\n";
		//statusEditText.setText(txt_log);
		stopConnection();
		//startActivity(i);
		env_main();
		//Toast.makeText(Flotilla1.this, "fin", Toast.LENGTH_SHORT).show();
		fin2s();

	}

	public void ok(View view){
        //Toast.makeText(Flotilla1.this, "Boton OK", Toast.LENGTH_SHORT).show();
		//stopConnection();
		editText1.requestFocus();
		Intent i = new Intent(this, Flotilla2.class );
		if(vehiculo_es){
			tarjeta_vehi = editText1.getText().toString();
		}
		String nip = editText2.getText().toString();

		if ((tarjeta_vehi.length()>11 && tarjeta_vehi.length()<13) || tagNFC){

			//Valida si es un codigo de Barras Vehiculo o si es lectura NFC.
			if (tarjeta_vehi.substring(0,2).equals("90") || tagNFC)
			{
				if (editText3.length()>0){
					//if Validar que mayor a 0
					String odo_s = editText3.getText().toString();
					Float monto_disponible_f = Float.parseFloat(monto_disponible);
					Intent j = new Intent(this, Flotilla4.class );
					j.putExtra("pos", pos);
					j.putExtra("envia2", monto_disponible_f);
					j.putExtra("envia3", 2); //Es por pesos el maximo del saldo entre Flotilla y Usuario
					j.putExtra("tarjeta", tarjeta_vehi);
					j.putExtra("odo_s", odo_s);
					j.putExtra("tarjeta_cb", tarjeta_cb);
					j.putExtra("user_sol", user_sol);


					txt_log += "ok finish"+"\n";
					//statusEditText.setText(txt_log);
					startActivity(j);
					//Toast.makeText(Flotilla1.this, "OK", Toast.LENGTH_SHORT).show();
					fin2s();
					msj1("DATOS ENVIA");
				}else{
					editText1.setVisibility(View.INVISIBLE);
					//editText3.setVisibility(View.VISIBLE);

					//textView1.setText("Ingrese ODOMETRO");
					msj1("Ingrese ODOMETRO");
				}

			}
		}else{
			if (tarjeta_vehi.length()>15){
				if (editText2.length()>3){
					//int cont = tarjeta.length();
					//if Validar que mayor a 0
					String desde = "f";
					i.putExtra("desde", desde);
					i.putExtra("pos", pos);
					i.putExtra("tarjeta", tarjeta_vehi);
					i.putExtra("nip", nip);
					i.putExtra("user_sol", user_sol);

					txt_log += "ok2 finish"+"\n";
					//statusEditText.setText(txt_log);
					startActivity(i);
					finish();
				}else{
					editText1.setVisibility(View.INVISIBLE);
					editText2.setVisibility(View.VISIBLE);
					editText2.requestFocus();
					textView1.setText("Ingrese NIP");
					msj1("Ingrese NIP");
				}
			}
		}


	}


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


	public void stopConnection() {
		ConnectionMode connectionMode = bbDeviceController.getConnectionMode();
		if (connectionMode == ConnectionMode.BLUETOOTH) {
			bbDeviceController.disconnectBT();
		} else if (connectionMode == ConnectionMode.AUDIO) {
			bbDeviceController.stopAudio();
		} else if (connectionMode == ConnectionMode.SERIAL) {
			bbDeviceController.stopSerial();
		} else if (connectionMode == ConnectionMode.USB) {
			bbDeviceController.stopUsb();
		}
	}



	//Deshabilitar BOTON atras
	@Override
	public void onBackPressed() {
	}




	@Override
	public void onDestroy() {
		super.onDestroy();

		stopConnection();

		ConnectionMode connectionMode = bbDeviceController.getConnectionMode();
		if (connectionMode == ConnectionMode.BLUETOOTH) {
			bbDeviceController.disconnectBT();
		}
		bbDeviceController.releaseBBDeviceController();
		bbDeviceController = null;
		listener = null;
		//rein_con = true;
	}


	/*
	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
*/

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

	public void reintento_manual(){
		Intent i = new Intent(this, Flotilla_rein.class);
		i.putExtra("msj", "NO SE ENCONTRO\nLECTOR DE USUARIOS");
		i.putExtra("ops1", "CANCELAR");
		i.putExtra("ops2", "REINTENTAR");
		startActivityForResult(i, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
		//EditText mEdtTarget = (EditText)findViewById(R.id.edtTarget);
		if (requestCode == 0){
			if (resultCode == RESULT_OK) {
				//vis_conec.setVisibility(View.VISIBLE);
				//estado_con = 0; //INICIA, Lanza la conexion del Lector
				//rev_lectores(false, "");
				promptForConnection();
			}else{
				//vis_conec.setVisibility(View.INVISIBLE);
				textView42.setVisibility(View.INVISIBLE);
				textView43.setVisibility(View.INVISIBLE);
			}
			//Toast.makeText(this, "regresa REINTENTA", Toast.LENGTH_LONG).show();
			/*
			if (data != null && resultCode == RESULT_OK) {
				user_sol = data.getStringExtra("user");
				String pass = data.getStringExtra("pass");
				if (user_sol != null) {

				}
			}
			*/
		}
	}

}