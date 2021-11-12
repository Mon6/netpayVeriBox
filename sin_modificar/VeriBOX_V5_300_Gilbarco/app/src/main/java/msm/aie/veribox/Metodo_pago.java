package msm.aie.veribox;

/**
 * Created by Ing Miguel Santiago on 10/11/2016.
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

//Se desactiva "OnKeyListener" por que no se usara numeros de tarjeta
//public class Metodo_pago extends Activity implements OnKeyListener{
public class Metodo_pago extends Activity{
	
	//private EditText  editText01;
	String metodo_pago, tipo_compro, m_ticket, uso_cfdi;
	boolean uso_cfdi_act;
	String xmp_pago1, xmp_pago2, met_pago_f, digitos_f, enti_banco_f;
	SQLiteDatabase bd;
	private final String NAMESPACE = "urn:veriboxwsdl";
	//private final String URL = "http://www.sigma-aie.com.mx/veribox/Veribox.php";
	private String URL = "";//"http://192.168.1.38/Veribox/Veribox.php";
	private final String SOAPACTION = "urn:veriboxwsdl#veribox";
	private final String METHOD = "veribox";
	String tds;
	RadioButton radio1;
	RadioButton radio2;
	RadioButton radio3;
	RadioButton radio4;
	RadioButton radio5;
	RadioButton radio6;
	private Button button24, button23, button1, button2;
	private TextView textView34, textView29;//, textView1;
	//Captura de XY
	private TextView textView_xy;
	StringBuilder stringBuilder = new StringBuilder();
	private Coor_xy cox_coy;
	//Tipo letra
	Typeface normal, marcado;	
	int sig_estado;
	boolean envio_dato;
	private int visMepagoF, visUsoDCFI;
	
	
	//Creamos el handler, puente para mostrar
	//el mensaje recibido de peticiones
	private Handler puente = new Handler() {
	 @Override
	 public void handleMessage(Message msg) {
		 if (((String)msg.obj).equals("")){
			 //t_busca=10000;
			 //textView5.setText("Problemas COM");
			 //pendientes(); 
			 msj("Problemas COM.");
			 Handler handler = new Handler();
		        handler.postDelayed(new Runnable() {
		            public void run() {
		            	if (tipo_compro.equals("T")){
		            		fin2s();
		            	}else{
		            		fin_fac();
		            	}
		            }
		        }, 3000);
		 }
		 else {
			 String muestra = (String)msg.obj;
			 busca_cadena1(muestra);
		 }
	 }
	};	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.metodo_pago);


        Bundle bundle=getIntent().getExtras();
        xmp_pago1 = bundle.getString("xmp_pago1");
        xmp_pago2 = bundle.getString("xmp_pago2");
        met_pago_f = bundle.getString("met_pago_f");
        digitos_f = bundle.getString("digitos_f");
        enti_banco_f = bundle.getString("enti_banco_f");
        tipo_compro = bundle.getString("tipo_compro");
        m_ticket = bundle.getString("m_ticket");
		uso_cfdi = bundle.getString("usocfdi");

		uso_cfdi_act = false;
		if (uso_cfdi.length()>0) {
			uso_cfdi_act = true;
			//msj(uso_cfdi);
		}
        normal = Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");
    	marcado = Typeface.createFromAsset(getAssets(), "fonts/arialbold.ttf");
        
    	//textView1 = (TextView) findViewById( R.id.textView1 );
    	textView34 = (TextView) findViewById( R.id.textView34 );
    	textView29 = (TextView) findViewById( R.id.textView29 );
    	button2 = (Button)findViewById(R.id.button2);
    	button1 = (Button)findViewById(R.id.button1);
		button1.requestFocus();
    	button24 = (Button)findViewById(R.id.button24);
    	button23 = (Button)findViewById(R.id.button23);
    	
        //editText01 = (EditText)findViewById(R.id.editText01);
        //editText01.setOnKeyListener(this);

        radio1 = (RadioButton) findViewById(R.id.metodo1);
        radio2 = (RadioButton) findViewById(R.id.metodo2);
        radio3 = (RadioButton) findViewById(R.id.metodo3);
        radio4 = (RadioButton) findViewById(R.id.metodo4);
        radio5 = (RadioButton) findViewById(R.id.metodo5);
        radio6 = (RadioButton) findViewById(R.id.metodo6);
        
        radio1.setTypeface(marcado);
        radio2.setTypeface(normal);
        radio3.setTypeface(normal);
        radio4.setTypeface(normal);
        radio5.setTypeface(normal);
        radio6.setTypeface(normal);
        
        button24.setVisibility(View.INVISIBLE);
        button23.setVisibility(View.INVISIBLE);
        //textView1.setVisibility(View.INVISIBLE);
        textView34.setVisibility(View.INVISIBLE);
        
        metodo_pago = met_pago_f;  
        if (metodo_pago==null){
        	//metodo_pago = "01";
			String usoPago = getString(R.string.metodo1);
			String[] usoPagoP = usoPago.split("\\s");
			metodo_pago = usoPagoP[0];
			String usoCFDI = getString(R.string.uso_cfdi1);
			String[] usoCFDIp = usoCFDI.split("-");
			if (!uso_cfdi_act)
				uso_cfdi = usoCFDIp[0];
		}
        if (enti_banco_f==null){
        	//metodo_pago = "000";
			String usoPago = getString(R.string.metodo1);
			String[] usoPagoP = usoPago.split("\\s");
			metodo_pago = usoPagoP[0];
			String usoCFDI = getString(R.string.uso_cfdi1);
			String[] usoCFDIp = usoCFDI.split("-");
			if (!uso_cfdi_act)
				uso_cfdi = usoCFDIp[0];
		}
        
        envio_dato=true;
		sig_estado = 0;
        
        radio1.setChecked(true);
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "tablet", null, 1);
    	bd = admin.getWritableDatabase();
        Leedb();
        //Toast.makeText(this, "***** INICIO *****", Toast.LENGTH_SHORT).show();

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
	     
	     if ((digitos_f != null) && (!tipo_compro.equals("T"))){
	        	//editText01.setText(digitos_f);
	        	ok(null);
	     }
	     
	     if (!tipo_compro.equals("T")){
	    	 veri_visMepagoF(); 
	     }
    }
    
    
  //Funcion Acciones
  	public void revisar (String rev_coor) throws IOException{
  		 		
  		//Boton a Efectivo  "metodo1">Efectivo
  		if (rev_coor.equals("F1") || rev_coor.equals("F2") || rev_coor.equals("F3")){
			if (sig_estado < 2 ){
				met1();
			}
  	    }
  		
  		//"metodo2">
  		if (rev_coor.equals("F5") || rev_coor.equals("F6") || rev_coor.equals("F7") || rev_coor.equals("F7")){
			if (sig_estado < 2 ){
				met2();
			}
  	    }
  		
  		//"metodo3">
  		if (rev_coor.equals("E5") || rev_coor.equals("E6") || rev_coor.equals("E7") || rev_coor.equals("E7")){
			if (sig_estado < 2 ){
				met3();
			}
  	    }
  		
  		//"metodo4">
  		if (rev_coor.equals("E1") || rev_coor.equals("E2") || rev_coor.equals("E3") || rev_coor.equals("E4")){
			if (sig_estado < 2 ){
				met4();
			}
  	    }
  		
  		//"metodo5">Tarjeta de Debito
  		if (rev_coor.equals("D1") || rev_coor.equals("D2") || rev_coor.equals("D3") || rev_coor.equals("D4")){
			if (sig_estado < 2 ){
				met5();
			}
  	    }
  		
  		//"metodo6">NA
  		if (rev_coor.equals("D5") || rev_coor.equals("D6") || rev_coor.equals("D7")){
			if (sig_estado < 2 ){
				met6();
			}
  	    }
  		
  		//ATRAS	
  		if (rev_coor.equals("A3") || rev_coor.equals("B3")){
			if (sig_estado < 2 ){
  				atras(null);
  			}
  			//Toast.makeText(this, "ATRAS", Toast.LENGTH_SHORT).show();
  	    }
  		
  		//OK
  		if (rev_coor.equals("A6") || rev_coor.equals("B6")){
  			//Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
  			//if (sig_estado < 3 ){
				//button1.setVisibility(View.INVISIBLE);
				//button2.setVisibility(View.INVISIBLE);
  				ok(null);
  			//}
  	    }
  		
  		//PIDE FACTURA
  		if (rev_coor.equals("E1") || rev_coor.equals("E2") || rev_coor.equals("E3") || rev_coor.equals("E4") || rev_coor.equals("D1") || rev_coor.equals("D2") || rev_coor.equals("D3") || rev_coor.equals("D4")){
  			//Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
  			if (sig_estado > 1){
  				tipo_compro = "F";
  				ok_fin();
  				//Toast.makeText(this, "FACTURA F", Toast.LENGTH_SHORT).show();
  			}
  	    }
  		
  		//PIDE CIMPROBANTE SIMPLIFICADO
  		if (rev_coor.equals("E5") || rev_coor.equals("E6") || rev_coor.equals("E7") || rev_coor.equals("E8") || rev_coor.equals("D5") || rev_coor.equals("D6") || rev_coor.equals("D7") || rev_coor.equals("D8")){
  			//Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
  			if (sig_estado > 1){
  				tipo_compro = "S";
  				ok_fin();
  				//Toast.makeText(this, "COMPROBANTE S", Toast.LENGTH_SHORT).show();
  			}
  	    }
  	}

  	public void completa(View view){
		if (sig_estado > 1){
			tipo_compro = "F";
			ok_fin();
			//Toast.makeText(this, "FACTURA F", Toast.LENGTH_SHORT).show();
		}
	}

	public void simpli(View view){
		if (sig_estado > 1){
			tipo_compro = "S";
			ok_fin();
			//Toast.makeText(this, "COMPROBANTE S", Toast.LENGTH_SHORT).show();
		}
	}

  	/*
	@Override
	public boolean onKey(View view, int keyCode, KeyEvent event) {
	    
	 if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
	  keyCode == EditorInfo.IME_ACTION_DONE ||
	  event.getAction() == KeyEvent.ACTION_DOWN &&
	  event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
	   
		  if (!event.isShiftPressed()) {
			   Log.v("AndroidEnterKeyActivity","Enter Key Pressed!");
			   switch (view.getId()) {
				   case R.id.editText01:
					   if (sig_estado==0){
			  				ok(null);
			  			}
					   //Toast.makeText(this, "ENTER EN 1",Toast.LENGTH_SHORT).show();
				    break;
			   }
			   //Toast.makeText(this, "return 2",Toast.LENGTH_SHORT).show();
			   return true; 
		  }                
	  
	 }
	 //Toast.makeText(this, "return 3",Toast.LENGTH_SHORT).show();
	 return false; // pass on to other listeners. 
	
	}
	*/

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.metodo1:
                if (checked){
					met1();
                }
                break;
            case R.id.metodo2:
            	if (checked){
					met2();
                }
                    
                break;
            case R.id.metodo3:
            	if (checked){
					met3();
                }
                    
                break;
            case R.id.metodo4:
                if (checked){
					met4();
                }
                break;
            case R.id.metodo5:
                if (checked){
					met5();
                }
                break;
            case R.id.metodo6:
                if (checked){
					met6();
                }
                break;
        }
    }
    
    public void met1(){
    	radio1.setChecked(true);
    	radio2.setChecked(false);
    	radio3.setChecked(false);
    	radio4.setChecked(false);
    	radio5.setChecked(false);
    	radio6.setChecked(false);
    	radio1.setTypeface(marcado);
        radio2.setTypeface(normal);
        radio3.setTypeface(normal);
        radio4.setTypeface(normal);
        radio5.setTypeface(normal);
        radio6.setTypeface(normal);
		//Obtenemos el valor del Metodo de Pago
		if (sig_estado == 0){
			String usoPago = getString(R.string.metodo1);
			String[] usoPagoP = usoPago.split("\\s");
			metodo_pago = usoPagoP[0];
		}else if (sig_estado == 1){
			//Obtenemos el valor del USO del CFDI.
			String usoCFDI = getString(R.string.uso_cfdi1);
			String[] usoCFDIp = usoCFDI.split("-");
			uso_cfdi = usoCFDIp[0];
		}


    }
    
    public void met2(){
    	radio1.setChecked(false);
    	radio2.setChecked(true);
    	radio3.setChecked(false);
    	radio4.setChecked(false);
    	radio5.setChecked(false);
    	radio6.setChecked(false);
    	radio1.setTypeface(normal);
        radio2.setTypeface(marcado);
        radio3.setTypeface(normal);
        radio4.setTypeface(normal);
        radio5.setTypeface(normal);
        radio6.setTypeface(normal);
		//Obtenemos el valor del Metodo de Pago
		if (sig_estado == 0){
			String usoPago = getString(R.string.metodo2);
			String[] usoPagoP = usoPago.split("\\s");
			metodo_pago = usoPagoP[0];
			//Toast.makeText(this, "METODO DE PAGO: "+metodo_pago, Toast.LENGTH_SHORT).show();
		}else if (sig_estado == 1){
			//Obtenemos el valor del USO del CFDI.
			String usoCFDI = getString(R.string.uso_cfdi2);
			String[] usoCFDIp = usoCFDI.split("-");
			uso_cfdi = usoCFDIp[0];
			//Toast.makeText(this, "USO CFDI: "+uso_cfdi, Toast.LENGTH_SHORT).show();
		}

    }
    
	public void met3(){
		radio1.setChecked(false);
    	radio2.setChecked(false);
    	radio3.setChecked(true);
    	radio4.setChecked(false);
    	radio5.setChecked(false);
    	radio6.setChecked(false);
    	radio1.setTypeface(normal);
        radio2.setTypeface(normal);
        radio3.setTypeface(marcado);
        radio4.setTypeface(normal);
        radio5.setTypeface(normal);
        radio6.setTypeface(normal);
		//Obtenemos el valor del Metodo de Pago
		if (sig_estado == 0){
			String usoPago = getString(R.string.metodo3);
			String[] usoPagoP = usoPago.split("\\s");
			metodo_pago = usoPagoP[0];
		}else if (sig_estado == 1){
			//Obtenemos el valor del USO del CFDI.
			String usoCFDI = getString(R.string.uso_cfdi3);
			String[] usoCFDIp = usoCFDI.split("-");
			uso_cfdi = usoCFDIp[0];
		}
	}
	
	public void met4(){
		radio1.setChecked(false);
    	radio2.setChecked(false);
    	radio3.setChecked(false);
    	radio4.setChecked(true);
    	radio5.setChecked(false);
    	radio6.setChecked(false);
    	radio1.setTypeface(normal);
        radio2.setTypeface(normal);
        radio3.setTypeface(normal);
        radio4.setTypeface(marcado);
        radio5.setTypeface(normal);
        radio6.setTypeface(normal);
		//Obtenemos el valor del Metodo de Pago
		if (sig_estado == 0){
			String usoPago = getString(R.string.metodo4);
			String[] usoPagoP = usoPago.split("\\s");
			metodo_pago = usoPagoP[0];
		}else if (sig_estado == 1){
			//Obtenemos el valor del USO del CFDI.
			String usoCFDI = getString(R.string.uso_cfdi4);
			String[] usoCFDIp = usoCFDI.split("-");
			uso_cfdi = usoCFDIp[0];
		}
	}
	
	public void met5(){
		radio1.setChecked(false);
    	radio2.setChecked(false);
    	radio3.setChecked(false);
    	radio4.setChecked(false);
    	radio5.setChecked(true);
    	radio6.setChecked(false);
    	radio1.setTypeface(normal);
        radio2.setTypeface(normal);
        radio3.setTypeface(normal);
        radio4.setTypeface(normal);
        radio5.setTypeface(marcado);
        radio6.setTypeface(normal);
		//Obtenemos el valor del Metodo de Pago
		if (sig_estado == 0){
			String usoPago = getString(R.string.metodo5);
			String[] usoPagoP = usoPago.split("\\s");
			metodo_pago = usoPagoP[0];
		}else if (sig_estado == 1){
			//Obtenemos el valor del USO del CFDI.
			String usoCFDI = getString(R.string.uso_cfdi5);
			String[] usoCFDIp = usoCFDI.split("-");
			uso_cfdi = usoCFDIp[0];
		}
	}
	
	public void met6(){
		radio1.setChecked(false);
    	radio2.setChecked(false);
    	radio3.setChecked(false);
    	radio4.setChecked(false);
    	radio5.setChecked(false);
    	radio6.setChecked(true);
    	radio1.setTypeface(normal);
        radio2.setTypeface(normal);
        radio3.setTypeface(normal);
        radio4.setTypeface(normal);
        radio5.setTypeface(normal);
        radio6.setTypeface(marcado);
		//Obtenemos el valor del Metodo de Pago
		if (sig_estado == 0){
			String usoPago = getString(R.string.metodo6);
			String[] usoPagoP = usoPago.split("\\s");
			metodo_pago = usoPagoP[0];
		}else if (sig_estado == 1){
			//Obtenemos el valor del USO del CFDI.
			String usoCFDI = getString(R.string.uso_cfdi6);
			String[] usoCFDIp = usoCFDI.split("-");
			uso_cfdi = usoCFDIp[0];
		}
	}
    
    public void ok(View view){
     	if (envio_dato){
			// Se desactiva por la version 3.3
			//String digitos_pago = editText01.getText().toString();
        	//Toast.makeText(this, digitos_pago, Toast.LENGTH_SHORT).show();
        	
        	//if (digitos_pago.length()==0 || digitos_pago.length()==4){
			switch(sig_estado) {
				case 0:
					if (visUsoDCFI == 0){
						textView29.setText("Uso de CFDI");

						button1.setVisibility(View.VISIBLE);
						button2.setVisibility(View.VISIBLE);

						String usoCFDI = getString(R.string.uso_cfdi1);
						String[] usoCFDIp = usoCFDI.split("-");
						radio1.setText(usoCFDIp[1]);
						radio1.setVisibility(View.VISIBLE);

						usoCFDI = getString(R.string.uso_cfdi2);
						usoCFDIp = usoCFDI.split("-");
						radio2.setText(usoCFDIp[1]);
						radio2.setVisibility(View.VISIBLE);

						usoCFDI = getString(R.string.uso_cfdi3);
						usoCFDIp = usoCFDI.split("-");
						radio3.setText(usoCFDIp[1]);
						radio3.setVisibility(View.VISIBLE);

						usoCFDI = getString(R.string.uso_cfdi4);
						usoCFDIp = usoCFDI.split("-");
						radio4.setText(usoCFDIp[1]);
						radio4.setVisibility(View.VISIBLE);

						usoCFDI = getString(R.string.uso_cfdi5);
						usoCFDIp = usoCFDI.split("-");
						radio5.setText(usoCFDIp[1]);
						radio5.setVisibility(View.VISIBLE);

						usoCFDI = getString(R.string.uso_cfdi6);
						usoCFDIp = usoCFDI.split("-");
						radio6.setText(usoCFDIp[1]);
						radio6.setVisibility(View.VISIBLE);

						sig_estado = 1;
						met1();

					}else{
						if (tipo_compro.equals("T")){
							envio_dato = false; //Para que no envien mas de una peticion al Servidor.
							ok_fin();
						}else {
							sig_estado = 2;
							textView29.setVisibility(View.INVISIBLE);
							radio1.setVisibility(View.INVISIBLE);
							radio2.setVisibility(View.INVISIBLE);
							radio3.setVisibility(View.INVISIBLE);
							radio4.setVisibility(View.INVISIBLE);
							radio5.setVisibility(View.INVISIBLE);
							radio6.setVisibility(View.INVISIBLE);
							button1.setVisibility(View.INVISIBLE);
							button2.setVisibility(View.INVISIBLE);
							//editText01.setVisibility(View.INVISIBLE);

							button24.setVisibility(View.VISIBLE);
							button23.setVisibility(View.VISIBLE);
							textView34.setVisibility(View.VISIBLE);
						}
					}
					break;
				case 1:
					//Toast.makeText(this, "TIPO COMPROBANTE", Toast.LENGTH_SHORT).show();
					sig_estado = 2;
					//textView1.setText("");

					textView29.setVisibility(View.INVISIBLE);
					radio1.setVisibility(View.INVISIBLE);
					radio2.setVisibility(View.INVISIBLE);
					radio3.setVisibility(View.INVISIBLE);
					radio4.setVisibility(View.INVISIBLE);
					radio5.setVisibility(View.INVISIBLE);
					radio6.setVisibility(View.INVISIBLE);
					button1.setVisibility(View.INVISIBLE);
					button2.setVisibility(View.INVISIBLE);
					//editText01.setVisibility(View.INVISIBLE);

					button24.setVisibility(View.VISIBLE);
					button23.setVisibility(View.VISIBLE);
					textView34.setVisibility(View.VISIBLE);
					break;

				case 2:
					//Toast.makeText(this, tipo_compro, Toast.LENGTH_SHORT).show();
					if (tipo_compro.equals("F") || tipo_compro.equals("T")){
						envio_dato = false; //Para que no envien mas de una peticion al Servidor.
						ok_fin();
					}else{

					}
					break;
				default:
					Toast.makeText(this, "NINGUNO", Toast.LENGTH_SHORT).show();
					break;
			}
        	/* Se desactiva por la version 3.3
        	}else{
        		msj("DIGITOS INCORRECTOS");
        	}
        	*/
     	}
    }
    
    public void ok_fin(){
    	//Desactiva los Botones de envio en vista
		button1.setVisibility(View.INVISIBLE);
		button2.setVisibility(View.INVISIBLE);
    	String pago ="";
    	String digitos_pago = "";//editText01.getText().toString();
    	if (tipo_compro.equals("T")){
    		//"\" tipo_pago=\"0900\" prov_pago=\"000\"  >\n"+
			msj3("PROCESANDO PETICIÓN.");
    		pago = "tipo_pago=\""+metodo_pago+"\" enti_banco_t=\""+enti_banco_f+"\" dig_tarjeta=\""+digitos_pago+"\" ";
    	}else{
    		//Si es Solicitud de Factura
    		msj3("PROCESANDO PETICIÓN:\nSE IMPRIMIRÁ SU COMPROBANTE");
        	//"tipo_pago=\""+tipo_pago+"\" enti_banco_f=\""+enti_banco_f+"\" dig_tarjeta=\""+ultimos_num+"\" ";
    		pago = "tipo_compro=\""+tipo_compro+"\" tipo_pago=\""+metodo_pago+"\" uso_cfdi=\""+uso_cfdi+"\" enti_banco_f=\""+enti_banco_f+"\" dig_tarjeta=\""+digitos_pago+"\" ";
    		
    	}
    	String xml_fin= xmp_pago1+pago+xmp_pago2;
		envia(xml_fin);
    }
    
    public void envia(final String salida){
    	//Toast.makeText(this, "ENVIA", Toast.LENGTH_SHORT).show();
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
    
    public void fin2s(){
    	Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
           // acciones que se ejecutan tras los milisegundos
				fin_tck();
			}
		}, 2000);	
    }
    
    public void fin_tck(){
		/*
    	Intent i = new Intent(this, MainActivity.class );
		i.putExtra("inicia", 1);
		startActivity(i);
		*/
		//MSM 05Mar2018 - Revisar el uso de bloqueo por usuario
		env_main();
    	finish();
    }
    
	public void busca_cadena1(String xml){
		if (tipo_compro.equals("T")){

			String busca1 = "display";
			String busca2 = "dato-impresiond";
			String dato=regresa_xml(xml, busca1,busca2);
			msj(dato);
			fin2s();
			/*
			String busca1 = "ticket";
			String busca2 = "res";
			String dato=regresa_xml(xml, busca1,busca2);
		    if (dato.equals("true")){
		    	//Toast.makeText(this, "R=TRUE",Toast.LENGTH_SHORT).show();
		    	busca1 = "display";
		    	busca2 = "dato-impresiond";
			    dato=regresa_xml(xml, busca1,busca2);	   
			    msj(dato);
			    fin2s();
		    }else{
				busca1 = "display";
				busca2 = "dato-impresiond";
				dato=regresa_xml(xml, busca1,busca2);
				msj(dato);
		    	fin2s();
		    }
		    */
		    
		}else
		{
			//Toast.makeText(this, "RESPUESTA", Toast.LENGTH_SHORT).show();
		    String busca1 = "factura";
		    String busca2 = "resf";
		    String dato=regresa_xml(xml, busca1,busca2);
		    if (dato.equals("true")){
		    	//Toast.makeText(this, "R = CORRECTO", Toast.LENGTH_SHORT).show();
			    busca2 = "tipo_solicitu";
			    dato=regresa_xml(xml, busca1,busca2);	
			    if (dato.equals("F1")){
			    	//Toast.makeText(this, "F1", Toast.LENGTH_SHORT).show();
		        	busca1 = "display";    	    
		    	    busca2 = "dato-impresiond";
		    	    String resdis = regresa_xml(xml, busca1,busca2);
		    	    //Toast.makeText(this, resdis, Toast.LENGTH_SHORT).show();
		    		msj(resdis);
			    }	
		    }else{
		    	//Toast.makeText(this, "R = ERROR", Toast.LENGTH_SHORT).show();
		    	busca2 = "tipo_solicitu";
			    dato=regresa_xml(xml, busca1,busca2);
		    	if (dato.equals("F1")){
		    		busca1 = "display";    	    
		    	    busca2 = "dato-impresiond";
		    	    String resdis = regresa_xml(xml, busca1,busca2);
		    		msj(resdis);	    		
		    	}	    	
		    }
		  //espera tiempo o si no lanza abortara
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
	           // acciones que se ejecutan tras los milisegundos
					fin_fac();
				}
			}, 3000);
		}
		
	}
	
	public void fin_fac(){
		 ContentValues registro = new ContentValues();
		 registro.put("msj_esp", "0");
			int cant = bd.update("config", registro, "num=1", null);
			//bd.close();
			if (cant == 1){
				//Toast.makeText(this, "FIN 0", Toast.LENGTH_SHORT).show();
			}
			else
				Toast.makeText(this, "NO Graba en DB",Toast.LENGTH_SHORT).show();
	    finish();
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
	            envi_retun="Sin Datos";
	        }
	    }else{
			envi_retun="Sin Datos";
	    }
	    return envi_retun;
	}
    
    public void msj(String muestra){
    	muestra = ">"+muestra+">2>3>4>5>6>7>";
    	Intent j = new Intent(this, Msj.class );
    	j.putExtra("msjcon", muestra);
        startActivity(j);
    }
    
    public void msj3(String muestra){
    	muestra = ">"+muestra+">3>3>4>5>6>7>";
    	Intent j = new Intent(this, Msj.class );
    	j.putExtra("msjcon", muestra);
        startActivity(j);
    }
    
    public void atras(View view){
    	if (tipo_compro.equals("T")){
    		fin_tck();
    	}else{
    		ContentValues registro = new ContentValues();
    		registro.put("msj_esp", "2");
   			int cant = bd.update("config", registro, "num=1", null);
   			//bd.close();
   			if (cant == 1){
   				//Toast.makeText(this, "FIN 2", Toast.LENGTH_SHORT).show();
   			}
   			else
   				Toast.makeText(this, "NO Graba en DB",Toast.LENGTH_SHORT).show();
       	finish();
    	}
    	
    }
    
    public void Leedb(){	
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
			
			visMepagoF=fila.getInt(25);

			if (tipo_compro.equals("T")){
				visUsoDCFI = 1;
				//sig_estado = 2;
				//Toast.makeText(this, "visUsoDCFI DESACTIVADO", Toast.LENGTH_SHORT).show();
			}else{
				if (!uso_cfdi_act)
					visUsoDCFI=fila.getInt(31);
				else
					visUsoDCFI=1;
			}

			int intentos=1;
			//Enviando=">"+num_tabled+">"+mac+">"+version+">"+mac_serial+">"+nomad+">"+intentos+">";
		}else{
			Toast.makeText(this, "NO se tiene servidor CONFIGURADO", Toast.LENGTH_SHORT).show();
		}
	} else{
		Toast.makeText(this, "NO lee DB",
				Toast.LENGTH_SHORT).show();	
	}
	//*****************************************************************************		
    }
    
    public void veri_visMepagoF(){
    	if (visMepagoF==1){
    		//Borrar pantalla de metodo de Pago.
    		textView29.setVisibility(View.VISIBLE);
    		textView29.setText("PROCESANDO PETICION: . . .");
        	radio1.setVisibility(View.INVISIBLE);
        	radio2.setVisibility(View.INVISIBLE);
        	radio3.setVisibility(View.INVISIBLE);
        	radio4.setVisibility(View.INVISIBLE);
        	radio5.setVisibility(View.INVISIBLE);
        	radio6.setVisibility(View.INVISIBLE);
        	button1.setVisibility(View.INVISIBLE);
        	button2.setVisibility(View.INVISIBLE);
        	//editText01.setVisibility(View.INVISIBLE);
    		ok(null);
    	}
    }
    
  //Deshabilitar BOTON atras: se usa para regresar.
    @Override
    public void onBackPressed() {
		atras(null);
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