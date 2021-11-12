package msm.aie.veribox;


import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;


public class User_edit extends Activity implements OnKeyListener{
	
	 private LinearLayout layoutAlta, layoutUsu;
	
	private TextView textView4, textView001, textView002, textView003, textView004, textView005;
	private EditText editText1;//, editText2, editText3;
	
	SQLiteDatabase bd;
	RadioButton radio1;
	RadioButton radio2;
	RadioButton radio3;
	RadioButton radio4;
	RadioButton radio5;
	private Button button2, button3;// button22, button23;
	String user_edit;
	String[] lst_user, lst_nombre, ls_lector ;
	String u1, u2, u3, u4, u5;
	//Tipo letra
	Typeface normal, marcado;
	int vista, incre;
	boolean alta, ultimo;
	String usua_alta, lector_cone;
	
	private String Enviando;
	private String tds;
	private boolean t1, t2, t3, t4, t5, btnmas, btnmenos;
	private final String NAMESPACE = "urn:veriboxwsdl";
	//private final String URL = "http://www.sigma-aie.com.mx/veribox/Veribox.php";
	private String URL = "";//"http://192.168.1.38/Veribox/Veribox.php";
	private final String SOAPACTION = "urn:veriboxwsdl#veribox";
	private final String METHOD = "veribox";
	//Captura de XY
	private TextView textView_xy;
	StringBuilder stringBuilder = new StringBuilder();
	private Coor_xy cox_coy;
	int sig_estado;
	
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
    setContentView(R.layout.user_edit);
	
	//Oculta teclado virtual
    this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    
    normal = Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");
	marcado = Typeface.createFromAsset(getAssets(), "fonts/arialbold.ttf");
	
	layoutUsu = (LinearLayout) findViewById(R.id.vis_user);
	layoutAlta = (LinearLayout) findViewById(R.id.vis_alta);
	layoutAlta.setVisibility(View.INVISIBLE);
	
	editText1 = (EditText)findViewById(R.id.editText1);
	editText1.setOnKeyListener(this);

	textView4 = (TextView)findViewById(R.id.textView4);
	textView001 = (TextView)findViewById(R.id.textView001);
	textView002 = (TextView)findViewById(R.id.textView002);
	textView003 = (TextView)findViewById(R.id.textView003);
	textView004 = (TextView)findViewById(R.id.textView004);
	textView005 = (TextView)findViewById(R.id.textView005);
	
    radio1 = (RadioButton) findViewById(R.id.user_vis1);
    radio2 = (RadioButton) findViewById(R.id.user_vis2);
    radio3 = (RadioButton) findViewById(R.id.user_vis3);
    radio4 = (RadioButton) findViewById(R.id.user_vis4);
    radio5 = (RadioButton) findViewById(R.id.user_vis5);
    
	radio1.setVisibility(View.INVISIBLE);
	radio2.setVisibility(View.INVISIBLE);
	radio3.setVisibility(View.INVISIBLE);
	radio4.setVisibility(View.INVISIBLE);
	radio5.setVisibility(View.INVISIBLE);
    
    button2 = (Button)findViewById(R.id.button2);
	button3 = (Button)findViewById(R.id.button3);
	//button22 = (Button)findViewById(R.id.button22);
	//button23 = (Button)findViewById(R.id.button23);
    
    radio1.setTypeface(marcado);
    radio2.setTypeface(normal);
    radio3.setTypeface(normal);
    radio4.setTypeface(normal);
    radio5.setTypeface(normal);
    
    button2.setVisibility(View.INVISIBLE);
    button3.setVisibility(View.INVISIBLE);
    
    //button22.setVisibility(View.INVISIBLE);
    //button23.setVisibility(View.INVISIBLE);
    
    /*
    editText01.setVisibility(View.INVISIBLE);
    editText02.setVisibility(View.INVISIBLE);
    textView01.setVisibility(View.INVISIBLE);
    textView02.setVisibility(View.INVISIBLE);
    */
    //textView4.setVisibility(View.INVISIBLE);
    alta = false;
    ultimo = false;
    user_edit = "01";  
    
    radio1.setChecked(true);
    t1=t2=t3=t4=t5=btnmas=btnmenos=false;
    //lst_user = new String[20];
    vista = 0;
    incre = 0;
    AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "tablet", null, 1);
	bd = admin.getWritableDatabase();
	Leedb_ini();
    Leedb();

    /*
  //Inicia captura de Coordenadas XY	
    //Coor_xy
    sig_estado= 0;
    	cox_coy= new Coor_xy();
	   this.textView_xy = (TextView) findViewById( R.id.strXY );
	   //this.textView_xy.setText("X: ,Y: ");//texto inicial
	
	   //Evento Touch
	   this.textView_xy.setOnTouchListener( new OnTouchListener()
	   {
	   	@Override
	   	public boolean onTouch( View arg0, MotionEvent arg1 ) {
	   		
	   		stringBuilder.setLength(0);
	   		//si la acción que se recibe es de movimiento
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
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			 Toast.makeText(this, "HOME",	Toast.LENGTH_SHORT).show();
	    }
	 if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
	  keyCode == EditorInfo.IME_ACTION_DONE ||
	  event.getAction() == KeyEvent.ACTION_DOWN &&
	  event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
	   
	  if (!event.isShiftPressed()) {
	   //Log.v("AndroidEnterKeyActivity","Enter Key Pressed!");
	   switch (view.getId()) {
	   case R.id.editText1:
		   if (alta)
		   	guarda(null);
		   else
		   	Toast.makeText(this, "NO alta",Toast.LENGTH_SHORT).show();
	    break;
	   }
	   return true; 
	  }                
	  
	 }
	 return false; // pass on to other listeners. 
	
	}

	/*
	//Revisa que se a selecionado en la caja de Radios
	public void onRadioButtonClicked(View view) {
		// Is the button now checked?
		boolean checked = ((RadioButton) view).isChecked();
		// Check which radio button was clicked
		switch(view.getId()) {
			case R.id.metodo1:
				if (checked){ met1(); }
				break;
			case R.id.metodo2:
				if (checked){ met2(); }
				break;
			case R.id.metodo3:
				if (checked){ met3(); }
				break;
			case R.id.metodo4:
				if (checked){ met4(); }
				break;
			case R.id.metodo5:
				if (checked){ met5(); }
				break;
		}
	}
	* /
	
	  //Funcion Acciones
  	public void revisar (String rev_coor) throws IOException{

        switch (rev_coor) {
            case "B7": case"B8":
                //Se asignara Lector a usuario
                if (sig_estado==0){
                    rev_lectores(null);
                }
                break;
        }
  		 		
  		//Boton a Efectivo  "metodo1">Efectivo
  		if (rev_coor.equals("F3") || rev_coor.equals("F4") || rev_coor.equals("F5")|| rev_coor.equals("F6")){  			
  			if (sig_estado==0 && t1){
  				met1();
  			    //Toast.makeText(this, "111", Toast.LENGTH_SHORT).show();
  			}
  	    }
  		
  		//"metodo2">Cheque nominativo
  		if (rev_coor.equals("E3") || rev_coor.equals("E4") || rev_coor.equals("E5")|| rev_coor.equals("E6")){ 
  			if (sig_estado==0 && t2){
  				//Toast.makeText(this, "222", Toast.LENGTH_SHORT).show();
  				met2();
  			}
  			
  	    }
  		
  		//"metodo3">Transferencia electrónica de fondos
  		if (rev_coor.equals("D3") || rev_coor.equals("D4") || rev_coor.equals("D5")|| rev_coor.equals("D6")){
  			if (sig_estado==0 && t3){
  				//Toast.makeText(this, "333", Toast.LENGTH_SHORT).show();
  				met3();
  			}
  			
  	    }
  		
  		//"metodo4">Tarjeta de Crédito
  		if (rev_coor.equals("C3") || rev_coor.equals("C4") || rev_coor.equals("C5")|| rev_coor.equals("C6")){
  			if (sig_estado==0 && t4){
  				//Toast.makeText(this, "444", Toast.LENGTH_SHORT).show();
  				met4();
  			}
  	    }
  		
  		//"metodo5">Tarjeta de Débito
  		if (rev_coor.equals("B3") || rev_coor.equals("B4") || rev_coor.equals("B5")|| rev_coor.equals("B6")){
  			if (sig_estado==0 && t5){
  				//Toast.makeText(this, "555", Toast.LENGTH_SHORT).show();
  				met5();
  			}
  	    }
  		
  		
  		//ATRAS	
  		if (rev_coor.equals("H1") || rev_coor.equals("G1")){
  			if (sig_estado==0){
  				fin(null);
  			}
  	    }
  		
  		//Agrega usuario
  		if (rev_coor.equals("F7") || rev_coor.equals("F8")){
  			//Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
  			if (sig_estado==0){
  				mas(null);
  				//Toast.makeText(this, "MAS", Toast.LENGTH_SHORT).show();
  			}
  	    }
  		
  		//Elimina usuario
  		if (rev_coor.equals("D7") || rev_coor.equals("D8")){
  			//Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
  			if (sig_estado==0){
  				elimina(null);
  				//Toast.makeText(this, "MENOS", Toast.LENGTH_SHORT).show();
  			}
  	    }

  		
  	//Envia alta
  		if (rev_coor.equals("D5") || rev_coor.equals("D6")|| rev_coor.equals("C5")|| rev_coor.equals("C6")){
  			//Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
  			if (sig_estado==1){;
  				guarda(null);
  				//Toast.makeText(this, "Envia alta", Toast.LENGTH_SHORT).show();
  			}
  	    }
  		
  		//Regresa 2
  		if (rev_coor.equals("D3") || rev_coor.equals("D4")|| rev_coor.equals("C3")|| rev_coor.equals("C4")){  		
  			//Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
  			if (sig_estado==1){
  				atras2(null);
  				//Toast.makeText(this, "Atras", Toast.LENGTH_SHORT).show();
  			}
  	    }
  		
  		//Mas de 5
  		if (rev_coor.equals("C2") || rev_coor.equals("B2")|| rev_coor.equals("C2")|| rev_coor.equals("B2")){  		
  			if (sig_estado==0 && btnmas){
  				mas5(null);
  				//Toast.makeText(this, "Mas 5", Toast.LENGTH_SHORT).show();
  			}
  	    }
  		
  		//Menos de 5
  		if (rev_coor.equals("F2") || rev_coor.equals("E2")|| rev_coor.equals("F2")|| rev_coor.equals("E2")){  		
  			//Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
  			if (sig_estado==0 && btnmenos){
  				menos5(null);
  				Toast.makeText(this, "Menos 5", Toast.LENGTH_SHORT).show();
  			}
  	    }
  	}
  	*/
  	
  	public void mas5(View view){
  		incre = incre + 5;
  		Leedb();
  	}
  	
  	public void menos5(View view){
  		incre = incre - 5;
  		Leedb();
  	}

	public void Leedb(){
		int count = 0;
		int pos = 0;
		int con = 0;
		
		Cursor fila = bd.rawQuery("SELECT COUNT(*) FROM users", null);
		if(fila.moveToFirst()){
			count= fila.getInt(0);
		}
		
		lst_user = new String[count];
		lst_nombre = new String[count];
		ls_lector = new String[count];
		radio1.setText("");
		radio2.setText("");
		radio3.setText("");
		radio4.setText("");
		radio5.setText("");

		textView001.setText("");
		textView002.setText("");
		textView003.setText("");
		textView004.setText("");
		textView005.setText("");
		
		radio1.setVisibility(View.INVISIBLE);
		radio2.setVisibility(View.INVISIBLE);
		radio3.setVisibility(View.INVISIBLE);
		radio4.setVisibility(View.INVISIBLE);
		radio5.setVisibility(View.INVISIBLE);

		textView001.setVisibility(View.INVISIBLE);
		textView002.setVisibility(View.INVISIBLE);
		textView003.setVisibility(View.INVISIBLE);
		textView004.setVisibility(View.INVISIBLE);
		textView005.setVisibility(View.INVISIBLE);

		t1=t2=t3=t4=t5=false;
		
		fila = bd.rawQuery("select * from users", null);
		
		if(fila.moveToFirst()){
			do{
				lst_user[pos] = fila.getString(1);
				ls_lector[pos] = fila.getString(3);
				lst_nombre[pos] = fila.getString(4);
				pos++;
			}while(fila.moveToNext());
		}
		
		if (pos == 1){
			ultimo = true;
		}else{
			ultimo = false;
		}
		
		if (incre > 0 ){
			 button2.setVisibility(View.VISIBLE);
			 //button3.setVisibility(View.VISIBLE);
			 btnmenos= true;
		}else{
			 button2.setVisibility(View.INVISIBLE);
			 btnmenos= false;
			 //incre = 0;
		}
			
		int vis_mas = pos-incre;
		if (vis_mas >5){
			 //button2.setVisibility(View.VISIBLE);
			 button3.setVisibility(View.VISIBLE);
			 btnmas= true;
		}else{
			 button3.setVisibility(View.INVISIBLE);
			 btnmas= false;
			 //incre = 0;
		}
		
		try
		{
			String muestra_lecto = "";
			while (con<5){
				switch (con) {                                    
				case 0:
					u1 = lst_user[con+incre];
					radio1.setText(lst_user[con+incre]+" - "+ lst_nombre[con+incre]);
					radio1.setVisibility(View.VISIBLE);
					radio1.setChecked(true);
					muestra_lecto = ls_lector[con+incre];
                    if (muestra_lecto.length()>2){
                        muestra_lecto = muestra_lecto.substring(muestra_lecto.length()-3);
                        textView001.setText(muestra_lecto);
                        textView001.setVisibility(View.VISIBLE);
                    }
					t1 = true;
					con++;
					met1(null);
					break;
	            case 1:
	            	u2 = lst_user[con+incre];
	            	radio2.setText(lst_user[con+incre]+" - "+ lst_nombre[con+incre]);
	            	radio2.setVisibility(View.VISIBLE);
					muestra_lecto = ls_lector[con+incre];
					if (muestra_lecto.length()>2){
						muestra_lecto = muestra_lecto.substring(muestra_lecto.length()-3);
						textView002.setText(muestra_lecto);
						textView002.setVisibility(View.VISIBLE);
					}
	            	t2 = true;
	            	con++;
	            	break;
	            case 2:
	            	u3 = lst_user[con+incre];
					radio3.setText(lst_user[con+incre]+" - "+ lst_nombre[con+incre]);
					radio3.setVisibility(View.VISIBLE);
					muestra_lecto = ls_lector[con+incre];
					if (muestra_lecto.length()>2){
						muestra_lecto = muestra_lecto.substring(muestra_lecto.length()-3);
						textView003.setText(muestra_lecto);
						textView003.setVisibility(View.VISIBLE);
					}
					t3 = true;
					con++;
					break;
	            case 3:
	            	u4 = lst_user[con+incre];
					radio4.setText(lst_user[con+incre]+" - "+ lst_nombre[con+incre]);
					radio4.setVisibility(View.VISIBLE);
					muestra_lecto = ls_lector[con+incre];
					if (muestra_lecto.length()>2){
						muestra_lecto = muestra_lecto.substring(muestra_lecto.length()-3);
						textView004.setText(muestra_lecto);
						textView004.setVisibility(View.VISIBLE);
					}
					t4 = true;
					con++;
					break;
	            case 4:
	            	u5 = lst_user[con+incre];
					radio5.setText(lst_user[con+incre]+" - "+ lst_nombre[con+incre]);
					radio5.setVisibility(View.VISIBLE);
					muestra_lecto = ls_lector[con+incre];
					if (muestra_lecto.length()>2){
						muestra_lecto = muestra_lecto.substring(muestra_lecto.length()-3);
						textView005.setText(muestra_lecto);
						textView005.setVisibility(View.VISIBLE);
					}
					t5 = true;
					con++;
					break;
				default:
					con++;
					Toast.makeText(this, "MAYOR 5",Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}
		catch (Exception ex)
		{
			//Toast.makeText(this, "TOTAL DE USUARIOS",Toast.LENGTH_SHORT).show();
		}
		
	}
	
	
	public void mas(View view){
		layoutAlta.setVisibility(View.VISIBLE);
		layoutUsu.setVisibility(View.INVISIBLE);
		textView4.setText("ALTA DE OPERADOR");
		editText1.setText("");
		alta = true;
		sig_estado = 1;
		//editText2.setText("");
		//editText3.setText("");
	}
	
	public void editar1(View view){
		layoutAlta.setVisibility(View.VISIBLE);
		layoutUsu.setVisibility(View.INVISIBLE);
		textView4.setText("EDICION DE OPERADOR");
		editText1.setText("");
		//editText2.setText("");
		//editText3.setText("");
		//edit = true;
		Cursor fila = bd.rawQuery("select * from users where user = '"+ user_edit +"'", null);
		
		if(fila.moveToFirst()){
			editText1.setText(fila.getString(1));
		}
	}
	
	public void atras2(View view){
		layoutAlta.setVisibility(View.INVISIBLE);
		layoutUsu.setVisibility(View.VISIBLE);
		sig_estado = 0;
		alta = false;
	}
	
	
	public void guarda(View view){
		String usuario;
		String usuario_c = editText1.getText().toString();
		//String pass1 = editText2.getText().toString();
		//String pass2 = editText3.getText().toString();
				
		if (usuario_c.length()>0){
			int i=Integer.parseInt(usuario_c.replaceAll("[^\\d]", ""));
			usuario = String.format("%03d", i);
			msj("Consulta Servidor");
			usua_alta = usuario;
			gen_xml(usuario);

		}else{
			Toast.makeText(this, "Ingrese:\n Usuario", Toast.LENGTH_SHORT).show();
			//editText3.setText("");
			//editText2.setText("");
			editText1.requestFocus();
		}
	}
	
	
	public void elimina (View view){
		if (ultimo){
			Toast.makeText(this, "Ultimo Operador\nNO SE BORRA", Toast.LENGTH_SHORT).show();
		}else{
			int cant = bd.delete("users", "user='"+user_edit+"'", null);
			if (cant == 1)
				Toast.makeText(this, "Se borro Operador", Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(this, "No hay Operador a borrar.", Toast.LENGTH_SHORT).show();		
			Leedb();
		}		
	}


	//MSM 05/Oct/2017 Ver:1.8-0
	//Revisa lectores, si son mas de 1 envia a seleccinar Lector.
	public void rev_lectores(View view){
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
				asignaLector();
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
				asignaLector();
			}
		}
	}

	//MSM 06/Oct/2017 Ver:1.8-0
	//Teniendo el lector se el asigna al Usuario.
	public void asignaLector(){
        //Toast.makeText(this, lector_cone, Toast.LENGTH_SHORT).show();

        ContentValues registro = new ContentValues();
        registro.put("atributos", lector_cone);
        bd.update("users", registro, "user='"+user_edit+"'", null);
		Leedb();
	}
	
	public void gen_xml(String user){
		
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
	            "   <gerente resg=\"true\" soli_g=\"0\"  isla=\"0\" turno=\"0\" usuario=\""+user+"\" pass=\"\" >\n"+
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
	
	public void Leedb_ini(){
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
				//Toast.makeText(this, "NO se tiene servidor CONFIGURADO", Toast.LENGTH_SHORT).show();
				msj("NO se tiene servidor\nCONFIGURADO");
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
		           // acciones que se ejecutan tras los milisegundos
						finish();
					}
				}, 2000);
			}
		} else{
			Toast.makeText(this, "NO lee DB",
					Toast.LENGTH_SHORT).show();	
		}
		//*****************************************************************************		
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
	        	
	        	busca2 = "usuario";
			    String usuario = regresa_xml(xml, busca1,busca2);
			    
			    busca2 = "pass";
			    String pass_res = regresa_xml(xml, busca1,busca2);
			    
			    //Toast.makeText(this, "Usuario:"+ usuario + "\nPASS:" + pass_res,Toast.LENGTH_SHORT).show();
			    
				Cursor fila = bd.rawQuery("select * from users where user = '"+ usua_alta +"'", null);
				if(fila.moveToFirst()){
					editText1.setText("");
					msj("Operador Repetido");
				}else{
					//msj("Alta de Operador");
					ContentValues registro = new ContentValues();
					registro.put("pass", pass_res);
					registro.put("user", usua_alta);
					registro.put("atributos", "");
					registro.put("ext1", usuario);
					registro.put("ext2", "");
					
					bd.insert("users", null, registro);//Almacena la Informacion en la Base de Datos
					
					Leedb();
					layoutAlta.setVisibility(View.INVISIBLE);
					layoutUsu.setVisibility(View.VISIBLE);
					alta = false;
					sig_estado=0;
					//editText1.setText("");
				}
				
				
	        }else{
	        	msj("Solicitud Incorrecta");
	        }
	        	
	    }else{
	    	msj("Operador: No Valido");
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
	
	public void msj(String msjcon){
		msjcon =">"+ msjcon+ ">2>3>4>5>6>7>"; 	    
		Intent i = new Intent(this, Msj.class );
	    i.putExtra("msjcon", msjcon);
	    startActivity(i);
	}
	
	public void fin(View view){
	    finish();
	}
	
	
	//Deshabilitar BOTON atras
	@Override
	public void onBackPressed() {		
		}

	
    public void met1(View view){
    	radio1.setChecked(true);
    	radio2.setChecked(false);
    	radio3.setChecked(false);
    	radio4.setChecked(false);
    	radio5.setChecked(false);
    	radio1.setTypeface(marcado);
        radio2.setTypeface(normal);
        radio3.setTypeface(normal);
        radio4.setTypeface(normal);
        radio5.setTypeface(normal);
        textView001.setTypeface(marcado);
		textView002.setTypeface(normal);
		textView003.setTypeface(normal);
		textView004.setTypeface(normal);
		textView005.setTypeface(normal);
    	user_edit = u1;
    }
    
    public void met2(View view){
    	radio1.setChecked(false);
    	radio2.setChecked(true);
    	radio3.setChecked(false);
    	radio4.setChecked(false);
    	radio5.setChecked(false);
    	radio1.setTypeface(normal);
        radio2.setTypeface(marcado);
        radio3.setTypeface(normal);
        radio4.setTypeface(normal);
        radio5.setTypeface(normal);
		textView001.setTypeface(normal);
		textView002.setTypeface(marcado);
		textView003.setTypeface(normal);
		textView004.setTypeface(normal);
		textView005.setTypeface(normal);
        user_edit = u2;
    }
    
	public void met3(View view){
		radio1.setChecked(false);
    	radio2.setChecked(false);
    	radio3.setChecked(true);
    	radio4.setChecked(false);
    	radio5.setChecked(false);
    	radio1.setTypeface(normal);
        radio2.setTypeface(normal);
        radio3.setTypeface(marcado);
        radio4.setTypeface(normal);
        radio5.setTypeface(normal);
		textView001.setTypeface(normal);
		textView002.setTypeface(normal);
		textView003.setTypeface(marcado);
		textView004.setTypeface(normal);
		textView005.setTypeface(normal);
        user_edit = u3;
	}
	
	public void met4(View view){
		radio1.setChecked(false);
    	radio2.setChecked(false);
    	radio3.setChecked(false);
    	radio4.setChecked(true);
    	radio5.setChecked(false);
    	radio1.setTypeface(normal);
        radio2.setTypeface(normal);
        radio3.setTypeface(normal);
        radio4.setTypeface(marcado);
        radio5.setTypeface(normal);
		textView001.setTypeface(normal);
		textView002.setTypeface(normal);
		textView003.setTypeface(normal);
		textView004.setTypeface(marcado);
		textView005.setTypeface(normal);
        user_edit = u4;
	}
	
	public void met5(View view){
		radio1.setChecked(false);
    	radio2.setChecked(false);
    	radio3.setChecked(false);
    	radio4.setChecked(false);
    	radio5.setChecked(true);
    	radio1.setTypeface(normal);
        radio2.setTypeface(normal);
        radio3.setTypeface(normal);
        radio4.setTypeface(normal);
        radio5.setTypeface(marcado);
		textView001.setTypeface(normal);
		textView002.setTypeface(normal);
		textView003.setTypeface(normal);
		textView004.setTypeface(normal);
		textView005.setTypeface(marcado);
        user_edit = u5;
	}

}