package msm.aie.veribox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.text.ParseException;
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

import android.app.Activity;
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
import android.view.inputmethod.EditorInfo;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class Flotilla2 extends Activity implements OnKeyListener{
	
	private String pos, tarjeta, d1, d2, d3, res, preset, pide_odo,valida_odo, ult_odo, precio_uni, capturo ,flotilla, res_display;
	private TextView textView, textView1, textView4, textView2, textView3, textView5, textView6, textView7;
	private Button button, button1, button2, teclado5, teclado6;
	private RadioButton radio1, radio2;
	private EditText editText1 ,editTextOD;
	private float dispo_f, preset_num;
	int tipo_venta;
	SQLiteDatabase bd; 
	private final String NAMESPACE = "urn:veriboxwsdl";
	//private final String URL = "http://www.sigma-aie.com.mx/veribox/Veribox.php";
	private String URL = "";//"http://192.168.1.38/Veribox/Veribox.php";
	private final String SOAPACTION = "urn:veriboxwsdl#veribox";
	private final String METHOD = "veribox";
	String mac_serial; // = "00:06:66:60:B8:01";
	int imp_ext=1;
	int MSJ=10;
	String Enviando,RM1, RM2, RM3, desde, nip, user_sol;
	//Captura de XY
	private TextView textView_xy, TextView001;
	StringBuilder stringBuilder = new StringBuilder();
	private Coor_xy cox_coy;
	int con_sig_pag, visTienda;


	//Creamos el handler puente para mostrar
		//el mensaje recibido de peticiones
		private Handler puente = new Handler() {
		 @Override
		 public void handleMessage(Message msg) {
			 if (((String)msg.obj).equals("")){
				 msj("Problemas COM.");
				 Handler handler = new Handler();
				 handler.postDelayed(new Runnable() {
				 	public void run() {
				 		fin(null);
				 	}
				 }, 2000);
			 }
			 else {
				 String muestra = (String)msg.obj;
				//statusEditText.setText("Respondio: "+muestra);
				 //if (MSJ==3)
					 RM2= muestra;
				//mensajes("","","");
				//proc_pend(muestra);
			 }
		 }
		};



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
						fin(null);
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
    setContentView(R.layout.flotilla2);
    
    //Oculta teclado virtual
    this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	editTextOD = (EditText)findViewById(R.id.editTextOD);
    
    textView = (TextView)findViewById(R.id.textView);
    textView1 = (TextView)findViewById(R.id.textView1);
    textView4 = (TextView)findViewById(R.id.textView4);
    textView2 = (TextView)findViewById(R.id.strXYmsj);
    textView3 = (TextView)findViewById(R.id.textView3);
    textView5 = (TextView)findViewById(R.id.textView5);
    textView6 = (TextView)findViewById(R.id.textView6);
    textView7 = (TextView)findViewById(R.id.textView7);
    
    button = (Button)findViewById(R.id.button);
    button1 = (Button)findViewById(R.id.button1);
	button2 = (Button)findViewById(R.id.button2);
		teclado6 = (Button)findViewById(R.id.teclado6);
		teclado5 = (Button)findViewById(R.id.teclado5);
    
    radio1= (RadioButton)findViewById(R.id.radio1);
    radio2= (RadioButton)findViewById(R.id.radio2);
	editTextOD.setVisibility(View.INVISIBLE);
    textView.setVisibility(View.INVISIBLE);
    button.setVisibility(View.INVISIBLE);
    teclado6.setVisibility(View.INVISIBLE);
    button1.setVisibility(View.INVISIBLE);
    teclado5.setVisibility(View.INVISIBLE);

    Bundle bundle=getIntent().getExtras();
    desde = bundle.getString("desde");
    pos = bundle.getString("pos");
    tarjeta = bundle.getString("tarjeta");
    nip = bundle.getString("nip");
    user_sol = bundle.getString("user_sol");
    
    //textView6.setText("***"+tarjeta.substring(12,15));
    
    /*if (tarjeta.length()>11 && tarjeta.length()<13 ){
    	if (tarjeta.substring(0,2).equals("90"))
        {
        	if (tarjeta.length()>0){
        		String tar_tempo = tarjeta.substring(4,8)+tarjeta.substring(0,3)+"0000"+tarjeta.substring(3,4)+tarjeta.substring(8,12);
            	tarjeta=tar_tempo;
            	textView6.setText("***"+tarjeta.substring(12,16));
        	}
        }
    }
      */  	
    
    //tarjeta ="1";
    
    textView2.setText("$ ");
	textView3.setText("PESOS");
	int i=Integer.parseInt(pos.replaceAll("[\\D]", ""));
    String pos_v = String.format("%02d", i);
	textView5.setText(pos_v);
	tipo_venta = 2;
	radio1.setChecked(false);
	radio2.setChecked(true);
    
	Leedb();

	if (visTienda ==1){
		button2.setVisibility(View.INVISIBLE);
	}

	msj("CONSULTANDO\nDATOS...");
	MSJ=0;        	
	//mensajes("1",tarjeta,nip);
		gen_xml("1",tarjeta,nip);
	//String DatosTransa = ">"+"1"+">"+tarjeta+">"+desde;
    //M0();
	
	//Revisara que se introduce desde el LECTOR DE CODIGO DE BARRAS
	editText1 = (EditText)findViewById(R.id.editText1);
	editText1.setOnKeyListener(this);
    editText1.addTextChangedListener(new TextWatcher() {

      public void afterTextChanged(Editable s) {}

      public void beforeTextChanged(CharSequence s, int start, 
        int count, int after) {}

      public void onTextChanged(CharSequence s, int start, 
        int before, int count) {
   	   //myOutputBox = (TextView) findViewById(R.id.myOutputBox);
   	   int con = s.length();
   	   String num = Integer.toString(con);
   	   //myOutputBox.setText(num);
   	   //editText1.setText(num);
		/*
		  if (con==7){
   		   capturo = s.toString();
   		   String p1 = capturo.substring(0,1);
   		   if (p1.equals("4")){
	   			p1 = capturo.substring(1,5);
	   			editText1.setText(p1);
	   			flotilla4(textView1);
   		   }
   	   }
   	   */
   	   if (con>7){
   		editText1.setText("");
   		   //capturo = s.toString();
   	   }
      }
     });
   //FIN - Revisara que se introduce desde el LECTOR DE CODIGO DE BARRAS  

		/*
  //Inicia captura de Coordenadas XY	
    //Coor_xy
    con_sig_pag = 0;
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
    
    //Pruebas de Vista
    /*String dispoS = "1.1";
    dispo_f= Float.parseFloat(dispoS);
    dispoS = String.format("%.2f", dispo_f);
	//dispoS = Float.toString(dispo_f);	
	textView7.setText("DISP.: $ "+dispoS);
	*/
 
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
	//Boton a ok1
	if (rev_coor.equals("D8") || rev_coor.equals("C8")){
		flotilla4(null);
    }
	//Boton a ok2
	if (rev_coor.equals("C7") || rev_coor.equals("B7")){
		if(con_sig_pag == 1){
			sig_pag(null);
		}
    }
	//Boton a Litros
	if (rev_coor.equals("E1") || rev_coor.equals("E2")){
			litros(null);
    }
	//Boton a Pesos
	if (rev_coor.equals("D1") || rev_coor.equals("D2")){
			dinero(null);
    }
	//Activado almacen entra.
	if (rev_coor.equals("B4") || rev_coor.equals("B5")){
		if (visTienda ==0){
			almacen(null);
		}
	}
	//Boton Cambio de NIP
	if (rev_coor.equals("A1") || rev_coor.equals("A2") || rev_coor.equals("B1") || rev_coor.equals("B2")){
		cambio_nip(null);
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
			   switch (view.getId()) {
				   case R.id.editText1:
					   //Toast.makeText(this, "ENTER EN 1", Toast.LENGTH_SHORT).show();
					   flotilla4(null);
				    break;
			   }
			   //Toast.makeText(this, "return 2",Toast.LENGTH_SHORT).show();
			   return true; 
		  }                
	  
	 }
	 //Toast.makeText(this, "return 3",Toast.LENGTH_SHORT).show();
	 return false; // pass on to other listeners. 
	
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
			visTienda=fila.getInt(21);
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

 //	/*
	public void gen_xml(String cadena1, String cadena2, String cadena3){

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
				"       <posicion pos=\""+pos+"\"></posicion>\n"+
				"       <usuario user_sol=\""+user_sol+"\"></usuario>\n"+
				"		<preset sol=\""+cadena1+"\" usuario=\""+cadena2+"\" nip=\""+cadena3+"\" monto=\"\" monto_preset=\"\" odome_reg=\"\" tipo_venta=\"\" usuario_trj=\"\" >"+
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

		//msj("RESPUESTA");

		String busca1 = "preset";
		String busca2 = "respr";
		String dato=regresa_xml(xml, busca1,busca2);

		if (dato.equals("TRUE")){

			busca2 = "monto";
			dato=regresa_xml(xml, busca1,busca2);
			String dispoS = dato;

			busca2 = "flotilla";
			dato=regresa_xml(xml, busca1,busca2);
			flotilla = dato;

			busca2 = "producto_max";
			dato=regresa_xml(xml, busca1,busca2);
			precio_uni = dato;

			busca2 = "pide_odom";
			dato=regresa_xml(xml, busca1,busca2);
			pide_odo = dato;

			busca2 = "valida_odom";
			dato=regresa_xml(xml, busca1,busca2);
			valida_odo = dato;

			busca2 = "ult_odom";
			dato=regresa_xml(xml, busca1,busca2);
			ult_odo = dato;


			dispo_f= Float.parseFloat(dispoS);
			dispoS = String.format("%.2f", dispo_f);

			//DATOS A MOSTRAR
			textView1.setText("FLOTILLA:"+flotilla);
			String tarjeta_v = tarjeta.substring(12,16);
			textView6.setText("USUARIO:"+tarjeta_v);
			textView7.setText("DISP.: $ "+dispoS);
			button1.setVisibility(View.VISIBLE);
			teclado5.setVisibility(View.VISIBLE);

		}else{
			busca1 = "display";
			busca2 = "dato-impresiond";
			dato=regresa_xml(xml, busca1,busca2);
			String msjcon =">"+ dato+ ">2>3>4>5>6>7>";
			Intent i = new Intent(this, Msj.class );
			i.putExtra("msjcon", msjcon);
			startActivity(i);

			env_main();
			finish();

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



public void mensajes(String cadena1, String cadena2, String cadena3){
	//Envia mensajes a servidor secuencialmente	
		if (MSJ==3){
		    saldo(RM2);		    
		}
		//Mensaje DATOS-Tranzaccion esperamos la respuesta con los datos
		if (MSJ==2){
			
			//Toast.makeText(this, RM2, Toast.LENGTH_SHORT).show();
			String[] partes = RM2.split("\\>");
		   	res_display = partes[1];
	   		MSJ=3;
	   		String Datos = ">"+"PR"+">"+pos+">";
			String DatosTransa = "";
			envia("M2",Datos,DatosTransa);
			
		}
		//Mensaje DATOS-Datos a procesar
		if (MSJ==1){
			
			MSJ=2;
			String Datos = ">"+"PR"+">"+pos+">";
			String DatosTransa = "";
			envia("M1",Datos,DatosTransa);
			
		}
		//Mensaje INICIAL-Quien solicita la infomacion
		if (MSJ==0){
			MSJ=1;
			String Datos = ">"+"PR"+">"+pos+">";
			String DatosTransa = ">"+cadena1+">"+cadena2+">"+cadena3+">"+user_sol+">";
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



/*
 * public void M0(){
	//Dividir una cadena en partes por |
	//String[] res0 = res.split("\\>");
	//String ress0 = res0[2];
	//string a numero
	String Datos = ">"+"PR"+">"+pos+">";
	String DatosTransa = ">"+"1"+">"+tarjeta+">"+desde;
	
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
    	RM1=respuesta;
    	//Envia a mostrar el mensaje en PANTALLA
    	//msj(respuesta);
    	
    	Toast.makeText(this, "Conexion lista2."+ respuesta,Toast.LENGTH_SHORT).show();
    	//textView1.setText("R:" + respuesta);
    	
    	M1();
    }catch(Exception e)
    {
    	e.printStackTrace();
    	//textView1.setText("Sin Respuesta M1");
    	Toast.makeText(this, "NO hay Conexion M1"+ URL,	Toast.LENGTH_SHORT).show();
    }
}

public void M1(){
	//Dividir una cadena en partes por |
	//String[] res0 = res.split("\\>");
	//String ress0 = res0[2];
	//string a numero
	
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
    	//msj(respuesta);
    	
    	Toast.makeText(this, "Conexion lista2."+ respuesta,Toast.LENGTH_SHORT).show();
    	//textView1.setText("R:" + respuesta);
    	
    	M2();
    }catch(Exception e)
    {
    	e.printStackTrace();
    	//textView1.setText("Sin Respuesta M1");
    	Toast.makeText(this, "NO hay Conexion M1"+ URL,	Toast.LENGTH_SHORT).show();
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
    	//RM1=respuesta;
    	//Toast.makeText(this, "FIN FIN:"+ respuesta,Toast.LENGTH_SHORT).show();
    	//textView1.setText("R:" + respuesta);
    	saldo(respuesta);
    }catch(Exception e)
    {
    	e.printStackTrace();
    	//textView1.setText("Sin Respuesta M1");
    	Toast.makeText(this, "NO hay Conexion M2"+ URL,	Toast.LENGTH_SHORT).show();
    }
}
*/

public void saldo(String cadena){
	//Dividir una cadena en partes por |
    String[] partes = cadena.split("\\>");
    String sol = partes[0];
    if (sol.equals("TRUE")){
    	String dispoS = partes[2];
    	flotilla = partes[3];
        precio_uni = partes[4];
        pide_odo = partes[5];
        valida_odo = partes[6];
        ult_odo = partes[7];
        
        dispo_f= Float.parseFloat(dispoS);
        dispoS = String.format("%.2f", dispo_f);
        
        //DATOS A MOSTRAR
        textView1.setText("FLOTILLA:"+flotilla);
        String tarjeta_v = tarjeta.substring(12,16);
        textView6.setText("USUARIO:"+tarjeta_v);
        textView7.setText("DISP.: $ "+dispoS);
        button1.setVisibility(View.VISIBLE);
		teclado5.setVisibility(View.VISIBLE);
    }else{
		/*
		Intent j = new Intent(this, MainActivity.class );
		j.putExtra("inicia", 1);
		startActivity(j);
	    */


		String msjcon =">"+ res_display+ ">2>3>4>5>6>7>";
		Intent i = new Intent(this, Msj.class );
	    i.putExtra("msjcon", msjcon);
	    startActivity(i);

		env_main();
		finish();
    }
}



public void flotilla4(View view){
	
	if (tipo_venta == 3){
		Intent i = new Intent(this, Tienda.class );
		String dispoS = String.format("%.2f", dispo_f);
	    i.putExtra("dispo_f", dispoS);
	    i.putExtra("flotilla", flotilla);
	    i.putExtra("tarjeta", tarjeta);
	    i.putExtra("envia_dat", "FL");
	    i.putExtra("user_sol", user_sol);
	    startActivity(i);
		finish();
	}else{
		preset = editText1.getText().toString();
		if (preset.length()>0){
			preset_num= Float.parseFloat(preset);
			Float precio= Float.parseFloat(precio_uni);
			float compara = 0;
			compara = preset_num;
			if(tipo_venta ==1){
				Float litros = preset_num * precio;
				compara = litros;
			}
			if (compara <= dispo_f){
				//int pide_odo = 0;
				int pide_odo_num=Integer.parseInt(pide_odo);
				if (pide_odo_num==1){
					pideOdometro(preset_num);
				}else{
					Intent i = new Intent(this, Flotilla4.class );
				    i.putExtra("pos", pos);
				    i.putExtra("envia2", preset_num);
				    i.putExtra("envia3", tipo_venta);
				    i.putExtra("tarjeta", tarjeta);
				    i.putExtra("user_sol", user_sol);
				    startActivity(i);
					finish();
				}
				//textView4.setText("Procesando Solicitud...");
			}else{
				msj("Solicitud mayor a Disponible");
				editText1.setText("");
				//textView4.setText("Solicitud mayor a Disponible");
			}
		}else{
			msj("Ingrese un VALOR");
			//Toast.makeText(this, "Ingrese un VALOR"+ preset,Toast.LENGTH_SHORT).show();
		}
		
	}
	
		
}

public void pideOdometro(Float cantidad){
	//en_odo = true;
	con_sig_pag = 1;
	editTextOD.setVisibility(View.VISIBLE);
	editTextOD.requestFocus();
    textView.setVisibility(View.VISIBLE);
    button.setVisibility(View.VISIBLE);
	teclado6.setVisibility(View.VISIBLE);
    radio1.setVisibility(View.INVISIBLE);
    radio2.setVisibility(View.INVISIBLE);
    editText1.setVisibility(View.INVISIBLE);
    textView2.setVisibility(View.INVISIBLE);
    textView3.setVisibility(View.INVISIBLE);
    button1.setVisibility(View.INVISIBLE);
	teclado5.setVisibility(View.INVISIBLE);
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



public void sig_pag(View view){
	String odo_s = editTextOD.getText().toString();
	//boolean continua = false;
	if (odo_s.length()>0){	
		//Codigo que valida el Odometro
		/*if (valida_odo.equals("1")){
			int odo_num = Integer.parseInt(odo_s);
			int ult_odo_num = Integer.parseInt(ult_odo);
			if (odo_num>ult_odo_num){
				//msj("Mayor valida Sig Pagina");
				continua = true;
			}else{
				//msj("Menor valida Cierra");
			}
	    }else{
	    	//msj("No Valida Sig PAGINA");
	    	continua = true;
	    }
		*/
		Intent i = new Intent(this, Flotilla4.class );
	    i.putExtra("pos", pos);
	    i.putExtra("envia2", preset_num);
	    i.putExtra("envia3", tipo_venta);
	    i.putExtra("tarjeta", tarjeta);
	    i.putExtra("odo_s", odo_s);
	    i.putExtra("user_sol", user_sol);
	    //msj("Procesando Solicitud..." + pos+preset_num+tipo_venta);
	    //textView4.setText("Procesando Solicitud..." + pos+preset_num+tipo_venta );
	    startActivity(i);
		finish();
	}
	
	/*if (continua){
		Intent i = new Intent(this, Flotilla4.class );
	    i.putExtra("pos", pos);
	    i.putExtra("envia2", preset_num);
	    i.putExtra("envia3", tipo_venta);
	    i.putExtra("tarjeta", tarjeta);
	    
	    //msj("Procesando Solicitud..." + pos+preset_num+tipo_venta);
	    //textView4.setText("Procesando Solicitud..." + pos+preset_num+tipo_venta );
	    startActivity(i);
		finish();
	}
	*/
}


	public void onRadioButtonClicked(View view) {
		// Is the button now checked?
		boolean checked = ((RadioButton) view).isChecked();
		// Check which radio button was clicked
		switch(view.getId()) {
			case R.id.radio1:
				if (checked){
					litros(null);
				}
				break;
			case R.id.radio2:
				if (checked){
					dinero(null);
				}

				break;
		}
	}




public void litros(View view){
	radio1.setChecked(true);
	radio2.setChecked(false);
	editText1.setVisibility(View.VISIBLE);
	textView2.setVisibility(View.VISIBLE);
	textView3.setVisibility(View.VISIBLE);
	textView.setVisibility(View.INVISIBLE);
	editText1.setText("");
	textView2.setText("");
	textView3.setText("LITROS");
	tipo_venta = 1;
}

public void dinero(View view){
	radio1.setChecked(false);
	radio2.setChecked(true);
	editText1.setVisibility(View.VISIBLE);
	textView2.setVisibility(View.VISIBLE);
	textView3.setVisibility(View.VISIBLE);
	textView.setVisibility(View.INVISIBLE);
	editText1.setText("");
	textView2.setText("$");
	textView3.setText("PESOS");
	tipo_venta = 2;
}

public void almacen(View view){
	tipo_venta = 3;
	finish();
	Intent i = new Intent(this, Tienda.class );
	String dispoS = String.format("%.2f", dispo_f);
    i.putExtra("dispo_f", dispoS);
    i.putExtra("flotilla", flotilla);
    i.putExtra("tarjeta", tarjeta);
    i.putExtra("envia_dat", "FL");
    i.putExtra("user_sol", user_sol);
    startActivity(i);

}

public void cambio_nip(View view){
	//Toast.makeText(this, "CAMBIA NIP",Toast.LENGTH_SHORT).show();
	//finish();
	Intent i = new Intent(this, Flotilla2_cn.class );
	//String dispoS = String.format("%.2f", dispo_f);
	//i.putExtra("dispo_f", dispoS);
	i.putExtra("flotilla", flotilla);
	i.putExtra("tarjeta", tarjeta);
	//i.putExtra("envia_dat", "FL");
	i.putExtra("user_sol", user_sol);
	startActivity(i);
}

public void fin(View view){
	/*
	Intent i = new Intent(this, MainActivity.class );
	i.putExtra("inicia", 1);
    startActivity(i);
    */
	env_main();
	finish();	
	
}

public void msj(String msjcon){
	//int index = 1;
	msjcon =">"+ msjcon+ ">2>3>4>5>6>7>"; 	    
	Intent i = new Intent(this, Msj.class );
    i.putExtra("msjcon", msjcon);
    //i.putExtra("index", index);
    startActivity(i);
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

//Deshabilitar BOTON atras
//@Override
//public void onBackPressed() {	
//	}


/*
public void t1(View view) {
	String add = TextView5.getText().toString();
	add += "1";
	TextView5.setText(add);
	}


public void t2(View view) {
	String add = TextView5.getText().toString();
	add += "2";
	TextView5.setText(add);
	}

public void t3(View view) {
	String add = TextView5.getText().toString();
	add += "3";
	TextView5.setText(add);
	}

public void t4(View view) {
	String add = TextView5.getText().toString();
	add += "4";
	TextView5.setText(add);
	}

public void t5(View view) {
	String add = TextView5.getText().toString();
	add += "5";
	TextView5.setText(add);
	}

public void t6(View view) {
	String add = TextView5.getText().toString();
	add += "6";
	TextView5.setText(add);
	}

public void t7(View view) {
	String add = TextView5.getText().toString();
	add += "7";
	TextView5.setText(add);
	}

public void t8(View view) {
	String add = TextView5.getText().toString();
	add += "8";
	TextView5.setText(add);
	}

public void t9(View view) {
	String add = TextView5.getText().toString();
	add += "9";
	TextView5.setText(add);
	}

public void t0(View view) {
	String add = TextView5.getText().toString();
	add += "0";
	TextView5.setText(add);
	}

public void punto(View view) {
	String add = TextView5.getText().toString();
	add += ".";
	TextView5.setText(add);
	}

public void l(View view) {
	//String add = textView4.getText().toString();
	String add = "";
	TextView5.setText(add);
	}
*/

}