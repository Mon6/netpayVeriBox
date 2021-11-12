package msm.aie.veribox;


import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;

public class Msj_geren extends Activity{
	int MSJ=10;
	private final String NAMESPACE = "urn:veriboxwsdl";
	private String URL;// = "http://192.168.1.38/Veribox/Veribox.php";
	private final String SOAPACTION = "urn:veriboxwsdl#veribox";
	private final String METHOD = "veribox";
	String Enviando, envia1, isla, envia3, mensaje;
	SQLiteDatabase bd;
	private TextView tv01;
	//Captura de XY
	private TextView textView_xy;
	StringBuilder stringBuilder = new StringBuilder();
	private Coor_xy cox_coy;

	/*
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
	*/

	//Creamos el handler puente para mostrar
	//el mensaje recibido de peticiones
	private Handler puente2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (((String)msg.obj).equals("")){
				//msj1("Problemas COM");
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
    setContentView(R.layout.msj_geren);
    
    Leedb();
    
	tv01 = (TextView)findViewById(R.id.textView1);
	tv01.setTextSize(60);
	tv01.setBackgroundColor(Color.TRANSPARENT);
	//tv01.setTextColor(Color.YELLOW);
    
    //recive datos del accion anterior
    Bundle bundle=getIntent().getExtras();
    envia1 = bundle.getString("envia1");        
    isla = bundle.getString("isla");
    envia3 = bundle.getString("envia3");
    mensaje = bundle.getString("mensaje");
    
    tv01.setText(mensaje);
    
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
 
    }


	//	Funcion Acciones
	public void revisar (String rev_coor) throws IOException{
		 		
		//Boton a Continuar
		if (rev_coor.equals("B3") || rev_coor.equals("B4") || rev_coor.equals("C3") || rev_coor.equals("C4") || rev_coor.equals("D3") || rev_coor.equals("D4")){
			ok(null);
	    }
		
		//Cancelar
		if (rev_coor.equals("B5") || rev_coor.equals("B6") ||rev_coor.equals("C5") || rev_coor.equals("C6") || rev_coor.equals("D5") || rev_coor.equals("D6")){
			atras(null);
	    }				
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

/*
	public void mensajes(String cadena1, String cadena2, String cadena3){
	//Envia mensajes a servidor secuencialmente	
		if (MSJ==3){
			finish();   
		}
		//Mensaje DATOS-Tranzaccion esperamos la respuesta con los datos
		if (MSJ==2){
			MSJ=3;
			String Datos = ">"+"GR"+">"+"99"+">";
			String DatosTransa = "";
			envia("M2",Datos,DatosTransa);
		}
		//Mensaje DATOS-Datos a procesar
		if (MSJ==1){
			msj("Envia mensaje a Servidor");
			MSJ=2;
			String Datos = ">"+"GR"+">"+"99"+">";
			String DatosTransa = "";
			//envia("M1",Datos,DatosTransa);
			finish();   
		}
		//Mensaje INICIAL-Quien solicita la infomacion
		if (MSJ==0){
			MSJ=1;
			String Datos = ">"+"GR"+">"+"99"+">";
			String DatosTransa = ">"+cadena1+">"+cadena2+">"+cadena3+">";
			envia("M0",Datos,DatosTransa);
		}
	}

	/ *
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
	
	public void ok(View view){
		
		//MSJ=0;
		//mensajes(envia1,isla,envia3);
		gen_xml(envia1,isla,envia3);
	}
	
	public void atras(View view){
		finish();
	}
	
	public void msj(String msjcon){
		msjcon =">"+ msjcon+ ">2>3>4>5>6>7>"; 	    
		Intent i = new Intent(this, Msj.class );
	    i.putExtra("msjcon", msjcon);
	    startActivity(i);
	}

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
				"   <mensaje-tipo tipo=\"GR\"></mensaje-tipo>\n"+
				"	<envio tds=\""+num_tabled+"\" mac=\""+ mac+"\" version=\""+version+"\" mac_serial=\""+mac_serial+"\" nomad=\""+nomad+"\" intentos=\""+intentos+"\"></envio>\n"+
				"   <datos>\n"+
				"       <posicion pos=\"99\"></posicion>\n"+
				"       <usuario user_sol=\"999\"></usuario>\n"+
				"		<gerente soli_g=\""+cadena1+"\" isla=\""+cadena2+"\" turno=\""+cadena3+"\" ></gerente>"+
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

		String busca1 = "gerente";
		String busca2 = "resg";
		String dato=regresa_xml(xml, busca1,busca2);

		if (dato.equals("true")){
			msj ("SOLICITUD: CORRECTA");

			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					// acciones que se ejecutan tras los milisegundos
					finish();
				}
			}, 2000);
		}else{
			//msj ("SOLICITUD: ERROR");
			busca1 = "display";
			busca2 = "dato-impresiond";
			dato=regresa_xml(xml, busca1,busca2);
			msj ( dato );
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					// acciones que se ejecutan tras los milisegundos
					finish();
				}
			}, 2000);
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




}
