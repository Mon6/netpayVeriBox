package msm.aie.veribox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.KeyEvent;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;

public class Tienda extends Activity implements OnKeyListener{
	
	private EditText editText1, editText2;
	private TextView textView1, textView2, textView3, textView4,textView5,textView6,textView7,textView8, textView28, textView32, textView33;
	private int cont_cod, MSJ=10;
	private SQLiteDatabase bd;
	private String tds;
	private String [] cod_prod;
	
	private final String NAMESPACE = "urn:veriboxwsdl";
	//private final String URL = "http://www.sigma-aie.com.mx/veribox/Veribox.php";
	private String URL = "";//"http://192.168.1.38/Veribox/Veribox.php";
	private final String SOAPACTION = "urn:veriboxwsdl#veribox";
	private final String METHOD = "veribox";
	
	private String total_ven_s, Enviando, cant_tck, cod_tck, cantidad, cod_barras, cant_total, codigo_total, des_total, pre_uni_total, monto_total;
	private double total_ven;
	Float dispo_f;
	private String dispo_s, flotilla, tarjeta, envia_dat, user_sol;
	private int total_reg;
	private Button Button01, Button02, button2, button1;
	
	//Captura de XY
	private TextView textView_xy, TextView001;
	StringBuilder stringBuilder = new StringBuilder();
	private Coor_xy cox_coy;
	int con_sig_pag;
	
	//Creamos el handler puente para mostrar
	//el mensaje recibido de peticiones
	private Handler puente = new Handler() {
	 @Override
	 public void handleMessage(Message msg) {
		 if (((String)msg.obj).equals("")){
			 msj("Problemas COM.");
		 }
		 else {
			 String muestra = (String)msg.obj;
			 res_ser(muestra);
			 //msj2("RESPUESTA");
		 }
	 }
	};	
	
@Override
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState); 
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.tienda);
    
    Bundle bundle=getIntent().getExtras();
    dispo_s = bundle.getString("dispo_f");
    flotilla = bundle.getString("flotilla");
    tarjeta = bundle.getString("tarjeta");
    envia_dat = bundle.getString("envia_dat");
    user_sol = bundle.getString("user_sol");
    
               
    cod_prod = new String [5];
    cont_cod = 0;
    cod_tck = "";
    cant_tck = "";
    total_reg = 0;
    
    cant_total = "Cant:\n";
    codigo_total = "Código:\n";
    des_total = "Descripción:\n";
    pre_uni_total = "P. Unitario:\n";
    monto_total = "Monto\n";
    total_ven = 0.00;
    
    editText1 = (EditText)findViewById(R.id.editText1);
    editText2 = (EditText)findViewById(R.id.editText2);
    editText2.setOnKeyListener(this);
    textView1 = (TextView)findViewById(R.id.textView1);
    textView2 = (TextView)findViewById(R.id.strXYmsj);
    textView3 = (TextView)findViewById(R.id.textView3);
    textView4 = (TextView)findViewById(R.id.textView4);
    textView5 = (TextView)findViewById(R.id.textView5);
    textView6 = (TextView)findViewById(R.id.textView6);
    textView7 = (TextView)findViewById(R.id.textView7);
    textView8 = (TextView)findViewById(R.id.textView8);
    textView28 = (TextView)findViewById(R.id.textView28);
    textView32 = (TextView)findViewById(R.id.textView32);
    textView33= (TextView)findViewById(R.id.textView33);
    textView2.setText("TOTAL:\n0.00");
    textView4.setText(cant_total);
    textView5.setText(codigo_total);
    textView6.setText(des_total);
    textView7.setText(pre_uni_total);
    textView8.setText(monto_total);
    
    textView4.setVisibility(View.INVISIBLE);
    textView5.setVisibility(View.INVISIBLE);
    textView6.setVisibility(View.INVISIBLE);
    textView7.setVisibility(View.INVISIBLE);
    textView8.setVisibility(View.INVISIBLE);
    
    
    button2 = (Button)findViewById(R.id.button2);
    button1 = (Button)findViewById(R.id.button1);
    Button01 = (Button)findViewById(R.id.Button01);
    Button02 = (Button)findViewById(R.id.Button02);
    
    textView32.setVisibility(View.INVISIBLE);
    textView33.setVisibility(View.INVISIBLE);
    textView28.setVisibility(View.INVISIBLE);
    Button01.setVisibility(View.INVISIBLE);
    Button02.setVisibility(View.INVISIBLE);

    
    Leedb();
    
    if (envia_dat.equals("FL")){
    	dispo_f= Float.parseFloat(dispo_s);
    	
    	textView32.setVisibility(View.VISIBLE);
        textView33.setVisibility(View.VISIBLE);
        
        textView32.setText("Flotilla:" + flotilla + "\nUsuario:" + tarjeta.substring(12,16));
        textView33.setText("Disponible: $" + dispo_s);
    }

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
				    break;
				   case R.id.editText2:
					   
					   mas(null);
					   //Toast.makeText(this, "ENTER EN PRODUCTO",Toast.LENGTH_SHORT).show();
				    break;
			   }
			   //Toast.makeText(this, "return 2",Toast.LENGTH_SHORT).show();
			   return true; 
		  }                
	  
	 }
	 //Toast.makeText(this, "return 3",Toast.LENGTH_SHORT).show();
	 return false; // pass on to other listeners. 
	
	}
	
	//Funcion Acciones
	public void revisar (String rev_coor) throws IOException{
		//Boton a HOME
		if (rev_coor.equals("H1") || rev_coor.equals("G1")){
			fin(null);
	    }
		//Regresa a Tickets
		if (rev_coor.equals("H3") || rev_coor.equals("H4") || rev_coor.equals("H5") || rev_coor.equals("H6") || rev_coor.equals("G3") || rev_coor.equals("G4") || rev_coor.equals("G5") || rev_coor.equals("G6")){
			fin(null);
	    }
		//Boton a ok1
		if (rev_coor.equals("F6") || rev_coor.equals("E6")){
			if(con_sig_pag == 0){
				mas(null);
			}
	    }
		//Boton a ok2
		if (rev_coor.equals("E7") || rev_coor.equals("D7")){							
			ok(null);
	    }
		//Boton a Litros
		if (rev_coor.equals("B2") || rev_coor.equals("B3") || rev_coor.equals("B4")){
			if(con_sig_pag == 1 && (!envia_dat.equals("FL"))){
				tck(null);
			}
	    }
		//Boton a Pesos
		if (rev_coor.equals("B5") || rev_coor.equals("B6") || rev_coor.equals("B7")){
			if(con_sig_pag == 1 && (!envia_dat.equals("FL"))){
				factura(null);
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

	public void fin(View view){
		/*
		Intent i = new Intent(this, MainActivity.class );
		i.putExtra("inicia", 1);
	    startActivity(i);
	    */
		env_main();
		finish();
	}
	
	
	public void mas(View view){
		editText1.requestFocus();
		if(editText1.length()>0 && editText2.length()>0){
			cantidad = editText1.getText().toString();
			cod_barras = editText2.getText().toString();
			
			editText1.setText("");
			editText2.setText("");
			editText1.requestFocus();
						
			//int h=Integer.parseInt(cod_barras.replaceAll("[\\D]", ""));
			//String rev_string = String.format("%013s", cod_barras);
			String rev_string = cod_barras;//String.format("%013d", h);
			
			boolean cb_rev = false; 
			
			if (total_reg != 5){
				for (int i = 0; i < 5; i++) {
					String compara = cod_prod[i];
					//Toast.makeText(this,i+": " + compara,	Toast.LENGTH_SHORT).show();			
					try
					{
						if (compara.equals(rev_string)){
							cb_rev = true;
						}
					}
					catch (Exception ex)
					{
					    Log.e("Compara", "Error compara");
					}		 
				}
				
				if (cb_rev){
					msj("Codigo Duplicado");
				}else{
					MSJ=3;
					msj("Consulta Servidor");
					gen_xml("P0");					
				}
			}else{
				msj("5 Productos\nMAXIMO");
			}
			
			
			
			
		}else{
			msj("Ingrese CANTIDAD e ID Producto");
		}
		
	}
	
	public void gen_xml(String pet){
		
		String[] partes = Enviando.split("\\>");
	    String num_tabled = partes[1];
	    String mac = partes[2];
	    String version = partes[3];
	    String mac_serial = partes[4];
	    String nomad = partes[5];
	    String intentos = partes[6];
	    
		final String text = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"+
	            "<peticion>\n"+
	            "   <mensaje-tipo tipo=\"AL\"></mensaje-tipo>\n"+
	            "	<envio tds=\""+num_tabled+"\" mac=\""+ mac+"\" version=\""+version+"\" mac_serial=\""+mac_serial+"\" nomad=\""+nomad+"\" intentos=\""+intentos+"\"></envio>\n"+
	            "   <datos>\n"+
	            "   <posicion pos=\"99\"></posicion>\n"+
	            "   <usuario user_sol=\""+user_sol+"\"></usuario>\n"+
	            "   <almacen peticiont=\""+pet+"\" cod_barrat=\""+cod_barras+"\" catidadt=\""+cantidad+"\"  clientet=\""+tarjeta+"\" >\n"+
	            "   </almacen>\n"+
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
		String busca1 = "almacen";
	    String busca2 = "rest";
	    String dato=regresa_xml(xml, busca1,busca2);
	    if (dato.equals("true")){
	    	//Toast.makeText(this, "R=TRUE",Toast.LENGTH_SHORT).show();
	    	busca2 = "peticiont";
		    dato=regresa_xml(xml, busca1,busca2);	    		
	        if (dato.equals("P0")){
	        	total_reg ++;
	        	//Toast.makeText(this, "R=P0",Toast.LENGTH_SHORT).show();
	        	busca2 = "cod_barrat";
			    String cod_barrat = regresa_xml(xml, busca1,busca2);
			    busca2 = "des_corta";
			    String des_corta = regresa_xml(xml, busca1,busca2);
			    busca2 = "pre_uni";
			    String pre_uni = regresa_xml(xml, busca1,busca2);
			    
	    		Float f= Float.parseFloat(pre_uni);
	    		//De Flotante a String
	    		pre_uni = String.format("%.2f", f);
	    		
			    busca2 = "unidad";
			    String unidad = regresa_xml(xml, busca1,busca2);
			    //Inserte registro en lista de compras
			    registro_mas(cod_barrat,des_corta,pre_uni,unidad);
	        }else{
	        	if (dato.equals("P1")){
	        		msj("Procesando TICKET...");
	        		espera2();
	        	}
	        	if (dato.equals("P2")){
	        		msj("Procesando TICKET\nUSUARIO...");
	        		espera2();
	        	}
	        }
	        	
	    }else{
	    	msj("Solicitud Incorrecta");
	    }
	}
	
	private void registro_mas(String cod_barrat, String des_corta, String pre_uni, String unidad) {
		
		textView4.setVisibility(View.VISIBLE);
	    textView5.setVisibility(View.VISIBLE);
	    textView6.setVisibility(View.VISIBLE);
	    textView7.setVisibility(View.VISIBLE);
	    textView8.setVisibility(View.VISIBLE);
		
		float cantidad_f = Float.parseFloat(cantidad);
		float pre_uni_f = Float.parseFloat(pre_uni);
		float monto_f = cantidad_f * pre_uni_f;
		String monto_s = String.format("%.2f", monto_f);
		
		//Calculo del USUARIO
		if (envia_dat.equals("FL")){	
			//float tmp_total_ven = (float) (total_ven + monto_f);
			if (monto_f < dispo_f){
				dispo_f -= monto_f;
				dispo_s = String.format("%.2f", dispo_f);
				textView33.setText("Disp.: $" + dispo_s);
				if (cod_tck.length()==0){
					cod_tck += cod_barrat;
				}else{
					cod_tck += "|"+cod_barrat;
				}

				if (cant_tck.length()==0){
					cant_tck += cantidad;
				}else{
					cant_tck += "|"+cantidad;
				}
			}else{
				msj("Disponible:\nInsuficiente");
				return;
			}			
		}else{
			if (cod_tck.length()==0){
				cod_tck += cod_barrat;
			}else{
				cod_tck += "|"+cod_barrat;
			}

			if (cant_tck.length()==0){
				cant_tck += cantidad;
			}else{
				cant_tck += "|"+cantidad;
			}

		}
		
		//Cantidad por productos
		cant_total += cantidad + "\n";
		textView4.setText(cant_total);
		//Codigo por producto
		codigo_total += cod_barrat + "\n";
		textView5.setText(codigo_total);
		//Descripcion por producto
		des_total += des_corta + "\n";
		textView6.setText(des_total);
		//Precio unitario por producto
		
		pre_uni_total += "$" + pre_uni + "\n";
		textView7.setText(pre_uni_total);
		
		//Calculando el total por productos
		monto_total += "$" + monto_s + "\n";
		textView8.setText(monto_total);
		
		total_ven += monto_f;
		total_ven_s = String.format("%.2f", total_ven);
		textView2.setText("TOTAL:\n$"+total_ven_s);
		textView28.setText("TOTAL: $"+total_ven_s);
		//Toast.makeText(this, "Registro_Mas",Toast.LENGTH_SHORT).show();
		cod_prod[cont_cod]=cod_barrat;
		cont_cod++;
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
	


	public void ok(View view){
		if (cod_tck.length()==0){
			msj("Sin Productos");
		}else{
			cantidad = cant_tck;
			cod_barras = cod_tck;
			con_sig_pag = 1;
			if (envia_dat.equals("FL")){
				Button01.setEnabled(false);
				Button02.setEnabled(false);
				msj("Envia a Servidor.");
				gen_xml("P2");
			}else{
				textView28.setVisibility(View.VISIBLE);
				Button01.setVisibility(View.VISIBLE);
				Button01.requestFocus();
				Button02.setVisibility(View.VISIBLE);
				
				button2.setVisibility(View.INVISIBLE);
				button1.setVisibility(View.INVISIBLE);
				editText1.setVisibility(View.INVISIBLE);
				editText2.setVisibility(View.INVISIBLE);
				textView1.setVisibility(View.INVISIBLE);
				textView3.setVisibility(View.INVISIBLE);
				textView2.setVisibility(View.INVISIBLE);
			}
		}			
	}

	public void tck(View view){
		Button01.setEnabled(false);
		Button02.setEnabled(false);
		msj("Envia a Servidor.");
		gen_xml("P1");
	}
	
	public void factura(View view){
		
		Intent i = new Intent(this, Fac_rapida.class );
	    i.putExtra("pos", "99");
	    i.putExtra("envia_fac", "AL");
	    i.putExtra("tickets", total_ven_s+"-"+cantidad+"-"+ cod_barras);
	    //i.putExtra("met_pago_f", "");
	    //i.putExtra("digitos_f", "");
	    //i.putExtra("enti_banco_f", "");
	    i.putExtra("user_sol", user_sol);
	    startActivity(i);
		finish();
	}
	
	public void msj(String msjcon){
		msjcon =">"+ msjcon+ ">2>3>4>5>6>7>"; 	    
		Intent i = new Intent(this, Msj.class );
	    i.putExtra("msjcon", msjcon);
	    startActivity(i);
	}
	
	public void espera2(){
		new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                fin(null);
            }
        }).start();
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
	
}