package msm.aie.veribox;


import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

public class User_pass extends Activity implements OnKeyListener{
	
	private EditText  editText17, editText018;
	SQLiteDatabase bd; 
	boolean permite_salir; 
	private String Enviando;
	private String tds;
	private String usr, passw, lector_db;
	private final String NAMESPACE = "urn:veriboxwsdl";
	//private final String URL = "http://www.sigma-aie.com.mx/veribox/Veribox.php";
	private String URL = "";//"http://192.168.1.38/Veribox/Veribox.php";
	private final String SOAPACTION = "urn:veriboxwsdl#veribox";
	private final String METHOD = "veribox";
	
	//Creamos el handler puente para mostrar
	//el mensaje recibido de peticiones
	private Handler puente = new Handler() {
	 @Override
	 public void handleMessage(Message msg) {
		 if (((String)msg.obj).equals("")){
			 msj_error("Problemas COM.");
		 }
		 else {
			 String respuesta = (String)msg.obj;
			 res_ser(respuesta);
			 //msj2("RESPUESTA");
		 }
	 }
	};	
	
@Override
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState); 
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.user_pass);
    
    getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            );
    
    
  //Oculta teclado virtual
    this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    
    editText17 = (EditText)findViewById(R.id.editText17);
    editText018 = (EditText)findViewById(R.id.editText018);
    //editText17.setFocusable(true);
    //editText018.setFocusable(false);
    editText17.requestFocus();
    editText018.setOnKeyListener(this);
    
    permite_salir = false;
    
    Leedb();
 
    }



	public void ok(View view){
		
	}
	
	/*
	@Override
	public void onAttachedToWindow() {
	    super.onAttachedToWindow();
	    this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
	}
	*/
	
	@Override
	public boolean onKey(View view, int keyCode, KeyEvent event) {
	    
	 if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
	  keyCode == EditorInfo.IME_ACTION_DONE ||
	  event.getAction() == KeyEvent.ACTION_DOWN &&
	  event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
	   
	  if (!event.isShiftPressed()) {
	   Log.v("AndroidEnterKeyActivity","Enter Key Pressed!");
	   switch (view.getId()) {
		   /*case R.id.editText17:
			   //editText18.requestFocus();
			   //rev_pass();
			   //Toast.makeText(this, "ENTER EN 2",Toast.LENGTH_SHORT).show();
		    break;
		    */
		   case R.id.editText018:
			   //editText17.requestFocus();
			   rev_pass();
			   //Toast.makeText(this, "ENTER EN 2",Toast.LENGTH_SHORT).show();
		    break;
	   }
	   return true; 
	  }                
	  
	 }
	 return false; // pass on to other listeners. 
	
	}
	
	public void rev_pass(){
		
		String usuario = editText17.getText().toString();
		String pass_rev = editText018.getText().toString();
		
		if (pass_rev.length()>3 && usuario.length()>0){
			
			int i=Integer.parseInt(usuario.replaceAll("[\\D]", ""));
			usuario = String.format("%03d", i);
			
			editText17.setText("");
			editText018.setText("");
			//editText17.requestFocus();	
			
			//Toast.makeText(this, usuario+ "--" + pass_rev,Toast.LENGTH_SHORT).show();
			
			AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"tablet", null, 1);
		    bd = admin.getWritableDatabase();
		    
			if (usuario.equals("999"))
			{
				Cursor fila = bd.rawQuery("select * from config where num=1"
						+ "", null);
				if (fila.moveToFirst()) {
					String clv=fila.getString(13);
					if (pass_rev.equals(clv)){
						Intent intent = new Intent();
					    intent.putExtra("user", usuario);
					    intent.putExtra("pass", pass_rev);
					    setResult(RESULT_OK, intent);
					    permite_salir = true;
					    finish();
					}else {
						msj_error("Operador o Contraseña:\nINCORRECTA");
					}

				} else{
					Toast.makeText(this, "NO lee DB",
							Toast.LENGTH_SHORT).show();
				}
			}else{
				Cursor fila = bd.rawQuery("select * from users where user = \""+ usuario + "\"", null);
				
				if (fila.moveToFirst()) {
					String pass_db = fila.getString(2);
					lector_db = fila.getString(3);
					usr = usuario;
					passw = pass_rev;
					envia_ver();
					/*
					if (pass_db.equals(pass_rev)){
						Intent intent = new Intent();
					    intent.putExtra("user", usuario);
					    intent.putExtra("pass", pass_rev);
					    setResult(RESULT_OK, intent);
					    permite_salir = true;
					    finish();
					}else{
						msj_error("Operador o Contraseña:\nINCORRECTA");
					}
					*/
					//Toast.makeText(this, "PASS DB*:"+usuario ,Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(this, "Operador: \nNO REGISTRADO",Toast.LENGTH_SHORT).show();
					msj_error("Operador: \nNO REGISTRADO");
				}
			}
		}else{
			msj_error("Contraseña:\nMinimo 4 Digitos");
		}
	}
	
	public void envia_ver (){
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
	            "   <posicion pos=\"99\"></posicion>\n"+
	            "   <gerente resg=\"true\" soli_g=\"0\"  isla=\"0\" turno=\"0\" usuario=\""+usr+"\" pass=\"\" >\n"+
	            "   </gerente>\n"+
	            "   </datos>\n"+
	            "</peticion>";
		
		envia(text);
	}
	
	public void envia(final String salida){
	    new Thread(new Runnable() {
	        @Override
	        public void run() {

	            String respuesta = "";
	            SoapObject request = new SoapObject(NAMESPACE, METHOD);
	            request.addProperty( "d0" , tds);
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
		//Toast.makeText(this, xml,Toast.LENGTH_SHORT).show();
		String busca1 = "gerente";
	    String busca2 = "resg";
	    String dato=regresa_xml(xml, busca1,busca2);
	    if (dato.equals("true")){
	    	//Toast.makeText(this, "R=TRUE",Toast.LENGTH_SHORT).show();
	    	busca2 = "soli_g";
		    dato=regresa_xml(xml, busca1,busca2);	    		
	        if (dato.equals("0")){
	        	
	        	//Toast.makeText(this, "R=P0",Toast.LENGTH_SHORT).show();
	        	
	        	//busca2 = "usuario";
			    //String usuario = regresa_xml(xml, busca1,busca2);
			    
			    busca2 = "pass";
			    String pass_res = regresa_xml(xml, busca1,busca2);
			    
			    if (pass_res.equals(passw)){
			    	Intent intent = new Intent();
				    intent.putExtra("user", usr);
				    intent.putExtra("pass", "");
					//intent.putExtra("pass", lector_db);
				    setResult(RESULT_OK, intent);
				    permite_salir = true;
				    finish();
			    }else{
			    	ContentValues registro = new ContentValues();
					registro.put("pass", pass_res);
					//bd.insert("users", null, registro);//Almacena la Informacion en la Base de Datos
					//----------------------------------------------------
					//Cursor fila = bd.rawQuery("select * from users where user = '"+ usua_alta +"'", null);
					int cant = bd.update("users", registro, "user='"+usr +"'", null);
					bd.close();
					if (cant == 1){
						//Toast.makeText(this, "Modificacion Terminada", Toast.LENGTH_SHORT).show();
						msj_error("Contraseña Incorrecta.");
					}
					//else
						//Toast.makeText(this, "No existe Operador registrado", Toast.LENGTH_SHORT).show();
			//----------------------------------------------------
			    }
	        }else{
	        	msj_error("Solicitud Incorrecta");
	        }
	    }else{
	    	msj_error("OPERADOR NO AUTORIZADO");
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
	
	public void msj_error(String muestra){
		//editText17.requestFocus();
		muestra = ">"+muestra+">2>3>4>5>6>7>";
    	Intent j = new Intent(this, Msj.class );
    	j.putExtra("msjcon", muestra);
        startActivity(j);
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
	}
	

	//Deshabilitar BOTON atras
	@Override
	public void onBackPressed() {
	}

	
	/*onPause(): Indica que la actividad está a punto de ser lanzada a segundo plano,
	 * Es el lugar adecuado para No permir que se salga de la aplicacion y regresarla de forma forzada*/
	/*
	@Override 
		protected void onPause() {
		   super.onPause();
		   if (permite_salir){
			   Toast.makeText(this, "PERMITE", Toast.LENGTH_SHORT).show();
			   permite_salir = false;
		   }else{
			   Intent i = new Intent(this, MainActivity.class );
			   i.putExtra("inicia", 1);
			   startActivity(i);
			   finish();

			   Toast.makeText(this, "DEBE REGRESAR", Toast.LENGTH_SHORT).show();   
		   }
		}
		*/

	

}