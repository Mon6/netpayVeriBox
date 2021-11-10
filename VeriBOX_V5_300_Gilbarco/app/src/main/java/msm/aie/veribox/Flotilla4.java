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

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

	public class Flotilla4 extends Activity{

		private String res, pos, tarjeta,odo_s, tarjeta_cb,user_sol, d1, d2 ,d3,d4;
		private float preset_num;
		private int tipo_venta;
		private TextView textView16;
		SQLiteDatabase bd;
		private final String NAMESPACE = "urn:veriboxwsdl";
		//private final String URL = "http://www.sigma-aie.com.mx/veribox/Veribox.php";
		private String URL = "";//"http://192.168.1.38/Veribox/Veribox.php";
		private final String SOAPACTION = "urn:veriboxwsdl#veribox";
		private final String METHOD = "veribox";
		String mac_serial; // = "00:06:66:60:B8:01";
		int imp_ext=1;
		String Enviando;
		int MSJ=10;
		ImageView imageView20;

		/*
		//Creamos el handler puente para mostrar
		//el mensaje recibido del servidor
		private Handler puente = new Handler() {
		 @Override
		 public void handleMessage(Message msg) {
		 //Mostramos el mensage recibido del servido en pantalla
		 //Toast.makeText(getApplicationContext(), (String)msg.obj,
		   //Toast.LENGTH_LONG).show();
			 if ((String)msg.obj == ""){
				 msj("Problemas COM");
			 	fin(null);
				 //textView1.setText("No se tiene contacto con el servidor");
			 }
			 else {
				 String respuesta = (String)msg.obj;
				 mensajes(respuesta);
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
					msj("Problemas COM");
					fin(null);
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
	    setContentView(R.layout.flotilla4);


	    textView16 = (TextView)findViewById(R.id.textView16);

	    Bundle bundle=getIntent().getExtras();
	    pos = bundle.getString("pos");
	    preset_num = bundle.getFloat("envia2");
	    tipo_venta = bundle.getInt("envia3");
	    tarjeta = bundle.getString("tarjeta");
	    odo_s = bundle.getString("odo_s");
	    tarjeta_cb = bundle.getString("tarjeta_cb");
	    user_sol = bundle.getString("user_sol");
	    //textView1.setText(pos+"-"+preset_num+"-"+tipo_venta);
	    imageView20 = (ImageView) findViewById(R.id.imageView20);
	    imageView20.setVisibility(View.INVISIBLE);
	    Leedb();
	    //M0();
	    msj("SOLICITANDO\nAUTORIZACIÓN... ");
	    textView16.setText("SOLICITANDO AUTORIZACIÓN... ");
	    MSJ=0;
		gen_xml();
	    //mensajes("");
	    //Revisa Valores
	    /*
	    String Datos = "";
	    Datos = Datos +  pos + " - ";
	    Datos = Datos +  Float.toString(preset_num) + " - ";
	    Datos = Datos +  String.valueOf(tipo_venta) + " - ";
	    Datos = Datos +  tarjeta + " - ";
	    Datos = Datos +  odo_s + " - ";
	    textView16.setText(Datos);
	    */

	    }

	public void Leedb(){
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
						imp_ext=fila.getInt(6);
						mac_serial=fila.getString(7);
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

			final String text = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"+
					"<peticion>\n"+
					"   <mensaje-tipo tipo=\"PR\"></mensaje-tipo>\n"+
					"	<envio tds=\""+num_tabled+"\" mac=\""+ mac+"\" version=\""+version+"\" mac_serial=\""+mac_serial+"\" nomad=\""+nomad+"\" intentos=\""+intentos+"\"></envio>\n"+
					"   <datos>\n"+
					"       <posicion pos=\""+pos+"\"></posicion>\n"+
					"       <usuario user_sol=\""+user_sol+"\"></usuario>\n"+
					"		<preset sol=\"2\" usuario=\""+tarjeta+"\" nip=\"----\" monto=\"\" monto_preset=\""+preset_num+"\" odome_reg=\""+odo_s+"\" tipo_venta=\""+tipo_venta+"\" usuario_trj=\""+tarjeta_cb+"\" />"+
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
			/*
			//Dividir cadena
			String autoriza = partes[1];

			imageView20.setVisibility(View.VISIBLE);
			if (autoriza.equals("TRUE")){
				imageView20.setImageResource(R.drawable.like);
			}else{
				imageView20.setImageResource(R.drawable.alerta);
			}

			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					// acciones que se ejecutan tras los milisegundos
					fin(textView16);
				}
			}, 3000);
			*/

			String busca1 = "preset";
			String busca2 = "respr";
			String dato=regresa_xml(xml, busca1,busca2);
			imageView20.setVisibility(View.VISIBLE);

			if (dato.equals("TRUE")){
				imageView20.setImageResource(R.drawable.like);
			}else{
				imageView20.setImageResource(R.drawable.alerta);
			}
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					// acciones que se ejecutan tras los milisegundos
					fin(textView16);
				}
			}, 3000);
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
	public void mensajes(String cadena1){
		//Envia mensajes a servidor secuencialmente	
			if (MSJ==3){
			    res_pres(cadena1);		    
			}
			//Mensaje DATOS-Tranzaccion esperamos la respuesta con los datos
			if (MSJ==2){
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
				MSJ=2;
				String Datos = ">"+"PR"+">"+pos+">";
				String DatosTransa = ">"+"2"+">"+preset_num+">"+tipo_venta+">"+tarjeta+">"+odo_s+">"+tarjeta_cb+">"+user_sol+">";
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
	
	public void res_pres(String dat1){
		
		//Dividir cadena
		String[] partes = dat1.split("\\>");
	    String autoriza = partes[1];
	    String imp = partes[2];
	    String mensa_res = partes[3];
	    textView16.setText(mensa_res);
	    imageView20.setVisibility(View.VISIBLE);
	    if (autoriza.equals("TRUE")){
	    	imageView20.setImageResource(R.drawable.like);
	    }else{
	    	imageView20.setImageResource(R.drawable.alerta);
	    }
	    
	    Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // acciones que se ejecutan tras los milisegundos
            	fin(textView16);
            }
        }, 3000);
        
	}
		*/
	
	//Deshabilitar BOTON atras
	@Override
	public void onBackPressed() {	
		}
/*
	public void M0(){
		//Dividir una cadena en partes por |
		//String[] res0 = res.split("\\>");
		//String ress0 = res0[2];
		//string a numero
		String Datos = ">"+"PR"+">"+pos+">";
		String DatosTransa = ">"+"2"+">"+preset_num+">"+tipo_venta+">"+tarjeta+">"+odo_s+">";
		
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
	    	//RM1=respuesta;
	    	//Envia a mostrar el mensaje en PANTALLA
	    	//msj(respuesta);
	    	
	    	Toast.makeText(this, "Conexion lista2."+ respuesta,Toast.LENGTH_SHORT).show();
	    	//textView1.setText("R:" + respuesta);
	    	msj("VENTA AUTORIZADA");
	    	
	    	//M1();
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
	    	//RM1=respuesta;
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
	    	//saldo(respuesta);
	    	
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    	//textView1.setText("Sin Respuesta M1");
	    	Toast.makeText(this, "NO hay Conexion M2"+ URL,	Toast.LENGTH_SHORT).show();
	    }
	}
	
	*/

	public void fin(View view){
		/*
		Intent j = new Intent(this, MainActivity.class );
		j.putExtra("inicia", 1);
		startActivity(j);
		*/
		env_main();
		finish();
	}
	
	public void msj(String msjcon){
		//int index = 1;
		//Intent j = new Intent(this, MainActivity.class );
		//startActivity(j);
	    
		msjcon =">"+ msjcon+ ">2>3>4>5>6>7>"; 	    
		Intent i = new Intent(this, Msj.class );
	    i.putExtra("msjcon", msjcon);
	    startActivity(i);

	    //finish();
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
	
}
