package msm.aie.veribox;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class Fac_rapida1 extends Activity{

	private LinearLayout datos_cliente, botones;

	/*
	private TextView textView1, textView2, textView4, textView5, textView7,textView9, textView8, textView10, textView17,
						textView18,textView19,textView20,textView21,textView22,textView23,textView24,textView25,textView26,textView27, solo_rfc;
	private EditText  editText1, editText2, editText3, editText4, editText7, editText8, editText9, editText10, editText11,
						editText12, editText13, editText14, editText15, editText16, editText_ref;
	*/
	private TextView textView2, textView22, textView4, textView9, textView10, textView1, textView17, solo_rfc;

	private EditText editText_ref,editText1, editText2, editText3,editText4;

	private Button bt, button4, button5;
	private boolean editados, edit_rfc;
	SQLiteDatabase bd;
	int imp_ext;
	String Enviando;
	String pos, tipoFactura, rfc_cli, rfc_cli_pb, tickets="", met_pago_f, digitos_f, enti_banco_f, envia_fac, user_sol, total_PB_S, qr;
	ImageView myImage;
	String correo_fin;
	String tds;
	String Datos;
	String DatosTransa;
	
	//private RadioButton r0,r1,r2,r3,r4;
	String d1,d2,d3,d4,d5,d6,d7,d8;
	
	private final String NAMESPACE = "urn:veriboxwsdl";
	//private final String URL = "http://www.sigma-aie.com.mx/veribox/Veribox.php";
	private String URL = "";//"http://192.168.1.38/Veribox/Veribox.php";
	private final String SOAPACTION = "urn:veriboxwsdl#veribox";
	private final String METHOD = "veribox";
	
	private String RM1, RM2, RM3;
	private int MSJ=10;
	boolean ok_st;
	int control;	
	String xml_pago1, xml_pago2;
	int con_esp=0;
	//Datos cliente
	String rfc_rec, nom_rec, calle, no_ex, no_in, colonia, cp, email, usocfdi;
	String servodorPB, datoPB, lector_cone = "";
	Timer timer = new Timer();
	
	//Captura de XY
	private TextView textView_xy, TextView001;
	StringBuilder stringBuilder = new StringBuilder();
	private Coor_xy cox_coy;
	
	//Creamos el handler puente para mostrar
	//el mensaje recibido de peticiones
	private Handler puente = new Handler() {
	 @Override
	 public void handleMessage(Message msg) {
		 if (((String)msg.obj).equals("")){
			 //t_busca=10000;
			 //textView5.setText("Problemas COM");
			 //pendientes(); 
			 msj2("Problemas COM.");
			 Handler handler = new Handler();
		        handler.postDelayed(new Runnable() {
		            public void run() {
		            	fin();
		            }
		        }, 2000);
		 }
		 else {
			 String muestra = (String)msg.obj;
			 //textView5.setText("HAY RESPUESTA");
			 busca_cadena1(muestra);
			 //solo_nom(muestra);
			 //mensajes("HAY RESPUESTA");
		 }
	 }
	};	
	
@Override
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState); 
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.fac_rapida1);
    
    this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


    ok_st = true;
    control = 0;

	botones = (LinearLayout) findViewById(R.id.botones);
	datos_cliente = (LinearLayout) findViewById(R.id.datos_cliente);

    myImage = (ImageView) findViewById(R.id.imageView1);
    tickets = "";
    met_pago_f = "";
    digitos_f = "";
    enti_banco_f = "";
	usocfdi = "";
  //recive datos del accion anterior
    Bundle bundle=getIntent().getExtras();
    pos = bundle.getString("envia1");
    tipoFactura = bundle.getString("envia2");
    rfc_cli = bundle.getString("envia3");
    tickets = bundle.getString("tickets");
    met_pago_f = bundle.getString("met_pago_f");
    digitos_f = bundle.getString("digitos_f");
    enti_banco_f = bundle.getString("enti_banco_f");
    envia_fac = bundle.getString("envia_fac");
    user_sol = bundle.getString("user_sol");
	total_PB_S = bundle.getString("total_PB_S");
	qr = bundle.getString("qr");

	d2 = tipoFactura + ">" + rfc_cli;//edit2.getText().toString();
	d3 = met_pago_f;//Metodo de Pago.
	d4 = enti_banco_f;//Proveedor de PAGO.
	d5 = digitos_f;//Digitos
    //d6 = rfc_cli;//edit2.getText().toString();//"02";//Posicion.
    
    bt = (Button)findViewById(R.id.button1);
    button4 = (Button)findViewById(R.id.button4);
    button5 = (Button)findViewById(R.id.button5);

	solo_rfc = (TextView)findViewById(R.id.solo_rfc);
	solo_rfc.setVisibility(View.INVISIBLE);

    textView1 = (TextView)findViewById(R.id.textView1);
	textView4 = (TextView)findViewById(R.id.textView4);
	textView9 = (TextView)findViewById(R.id.textView9);
	textView10 = (TextView)findViewById(R.id.textView10);
	textView17 = (TextView)findViewById(R.id.textView17);
	textView2 = (TextView)findViewById(R.id.textView2);
	textView22 = (TextView)findViewById(R.id.textView22);
	/*
    textView5 = (TextView)findViewById(R.id.textView5);
    textView7 = (TextView)findViewById(R.id.textView7);
    textView8 = (TextView)findViewById(R.id.textView8);
    textView18 = (TextView)findViewById(R.id.textView18);
    textView19 = (TextView)findViewById(R.id.textView19);
    textView20 = (TextView)findViewById(R.id.textView20);
    textView21 = (TextView)findViewById(R.id.textView21);
    textView22 = (TextView)findViewById(R.id.textView22);
    textView23 = (TextView)findViewById(R.id.textView23);
    textView24 = (TextView)findViewById(R.id.textView24);
    textView25 = (TextView)findViewById(R.id.textView25);
    textView26 = (TextView)findViewById(R.id.textView26);
    textView27 = (TextView)findViewById(R.id.textView27);
    */
    
    editText1 = (EditText)findViewById(R.id.editText1);
    editText1.requestFocus();
    editText2 = (EditText)findViewById(R.id.editText2);
    editText3 = (EditText)findViewById(R.id.editText3);
	editText4 = (EditText)findViewById(R.id.editText4);
    editText_ref = (EditText)findViewById(R.id.editText_ref);
	/*

    editText7 = (EditText)findViewById(R.id.editText7);
    editText8 = (EditText)findViewById(R.id.editText8);
    editText9 = (EditText)findViewById(R.id.editText9);
    editText10 = (EditText)findViewById(R.id.editText10);
    editText11 = (EditText)findViewById(R.id.editText11);
    editText12 = (EditText)findViewById(R.id.editText12);
    editText13 = (EditText)findViewById(R.id.editText13);
    editText14 = (EditText)findViewById(R.id.editText14);
    editText15 = (EditText)findViewById(R.id.editText15);
    editText16 = (EditText)findViewById(R.id.editText16);
    */

    
    bt.setEnabled(false);
	editText1.setVisibility(View.INVISIBLE);
	editText2.setVisibility(View.INVISIBLE);
    editText3.setVisibility(View.INVISIBLE);
	textView17.setVisibility(View.INVISIBLE);
	editText4.setVisibility(View.INVISIBLE);
	/*
	editText3.setVisibility(View.INVISIBLE);
	textView18.setVisibility(View.INVISIBLE);
	editText7.setVisibility(View.INVISIBLE);
	textView19.setVisibility(View.INVISIBLE);
	editText8.setVisibility(View.INVISIBLE);
	textView20.setVisibility(View.INVISIBLE);
	editText9.setVisibility(View.INVISIBLE);
	textView21.setVisibility(View.INVISIBLE);
	editText10.setVisibility(View.INVISIBLE);
	textView22.setVisibility(View.INVISIBLE);
	editText11.setVisibility(View.INVISIBLE);
	textView23.setVisibility(View.INVISIBLE);
	editText12.setVisibility(View.INVISIBLE);
	textView24.setVisibility(View.INVISIBLE);
	editText13.setVisibility(View.INVISIBLE);
	textView25.setVisibility(View.INVISIBLE);
	editText14.setVisibility(View.INVISIBLE);
	textView26.setVisibility(View.INVISIBLE);
	editText15.setVisibility(View.INVISIBLE);
	textView27.setVisibility(View.INVISIBLE);
	editText16.setVisibility(View.INVISIBLE);
	*/
	
	editados = false;
	edit_rfc = true;
	correo_fin = "";
	AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "tablet", null, 1);
	bd = admin.getWritableDatabase();
	
    Leedb();
    //msj2("CONSULTANDO\n DATOS...");
    
    //textView1.setText(rfc_cli);
    //int i=Integer.parseInt(pos.replaceAll("[\\D]", ""));
    //String pos_v = String.format("%02d", i);
    //textView2.setText(pos_v);
    textView4.setTextColor(Color.RED);

    if (qr.equals("1")){
        //textView17.setText("LECTUAR DE QR");
        String[] partes1 = rfc_cli.split(">");

        //Obtiene el valor de  >CORREO	>RFC	>Razón Social	>CP
        textView10.setText( partes1[0]);
        editText4.setText( partes1[0]);
        correo_fin = email = partes1[0];

        textView4.setText( partes1[1]);
        editText1.setText( partes1[1]);

        textView1.setText(partes1[2]);
        editText2.setText(partes1[2]);

        textView22.setText(partes1[3]);
        editText3.setText(partes1[3]);

        rfc_cli = rfc_cli_pb = partes1[1];

		d1="F2";
        d2 = tipoFactura + ">" + rfc_cli;
        editados = true;
        bt.setEnabled(true);
    }else{
		switch (tipoFactura){
			case "C":
				//textView4.setText("Flotilla:");
				d1="F1";
				MSJ=3;
				mensajes("");
				break;
			case "R":
				//textView4.setText("R.F.C.:");
				d1="F2";
				MSJ=3;
				mensajes("");
				break;
		}
	}



	switch (envia_fac){
		case "PB":
			button5.setText("PAGO BANCARIO");
			button4.setVisibility(View.INVISIBLE);
			rfc_cli_pb = rfc_cli;
			break;
		case "AL":
			String[] partes = tickets.split("-");
			String total_ven_s = partes[0];
			textView17.setText("VENTA TIENDA\n$"+ total_ven_s);
			textView17.setTextSize(35);
			textView17.setVisibility(View.VISIBLE);
			myImage.setVisibility(View.INVISIBLE);
			textView2.setText("");
			break;
		default:
			if(tickets.length()==0){
				int i=Integer.parseInt(pos.replaceAll("[\\D]", ""));
				String pos_v = String.format("%02d", i);
				textView2.setText(pos_v);
			}else{
				myImage.setVisibility(View.INVISIBLE);
				textView2.setText("");
				textView17.setText(tickets);
				textView17.setVisibility(View.VISIBLE);
			}
			break;

	}

    /*BORRAR
	if (envia_fac.equals("PB")){
    	button5.setText("PAGO BANCARIO");
    	button4.setVisibility(View.INVISIBLE);
    	rfc_cli_pb = rfc_cli;
	}
    
    if(envia_fac.equals("AL")){
    	String[] partes = tickets.split("-");
	    String total_ven_s = partes[0];
	    textView17.setText("VENTA TIENDA\n$"+ total_ven_s);
	    textView17.setTextSize(35);
	    textView17.setVisibility(View.VISIBLE);
	    myImage.setVisibility(View.INVISIBLE);
	    textView2.setText("");
	    
    }else{
    	if(tickets.length()==0){
        	int i=Integer.parseInt(pos.replaceAll("[\\D]", ""));
            String pos_v = String.format("%02d", i);
            textView2.setText(pos_v);
        }else{
        	myImage.setVisibility(View.INVISIBLE);
        	textView2.setText("");
        	textView17.setText(tickets);
        	textView17.setVisibility(View.VISIBLE);
        }
    }
    */
    
     /*
    //Revisara que se introduce
    //myTextBox = (EditText) findViewById(R.id.editText1);
    //myTextBox.requestFocus();
    editText1.addTextChangedListener(new TextWatcher() {

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
   	   if (con > 0 && control != con){
   		   control = con;
   		   //Combierte a MAYUSCULAS
   		   //textView2.setText(num);
   		   rfc_cli = editText1.getText().toString();
   		   rfc_cli = rfc_cli.toUpperCase();
   		   //String input = algo.toUpperCase(); 
   		   editText1.setText(rfc_cli);
   		   editText1.setSelection(con);
   	   }
      }
     });
   //FIN - Revisara que se introduce
	*/

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

//Funcion Acciones
	public void revisar (String rev_coor) throws IOException{
		//Boton a HOME
		if (rev_coor.equals("H1") || rev_coor.equals("G1")){
			fin(null);
	    }
		//Cambia de Opcion a procesar
		if (rev_coor.equals("H3") || rev_coor.equals("H4") || rev_coor.equals("H5") || rev_coor.equals("H6")){
			fin(null);
	    }
		//Boton a Atras
		if (rev_coor.equals("B3") || rev_coor.equals("A3")){
			atras(null);
	    }
		//Boton Editar
		if (rev_coor.equals("B4") || rev_coor.equals("B5") || rev_coor.equals("A4") || rev_coor.equals("A5")){
			if (envia_fac.equals("PB")){
				//Directo a PAGO BANCARIO
			}else{
				editar(null);
			}
			
	    }
		//Boton a OK
		if (rev_coor.equals("B6") || rev_coor.equals("A7")){
			if (envia_fac.equals("PB")){
				//MSM 05/Oct/2017 Ver:1.8-0
				rev_lectores("F");
				//envia_PB("F");
			}else{
				ok(null);
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
			
			//*****************************************************************************		
			Cursor fila = bd.rawQuery("select * from config where num=1"
					+ "", null);
			if (fila.moveToFirst()) {
				URL=fila.getString(5);
				servodorPB = URL;
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

public void mensajes(String cadena){
	//Envia mensajes a servidor secuencialmente	
	if (MSJ==5){
		//Dividir cadena
		String[] partes = cadena.split("\\>");
	   	String res = partes[1];
	   	if (res.equals("true")){
	   		String dispoS = partes[4];
			//textView1.setText(rfc_cli);
			partes = cadena.split("\\|");
		   	rfc_rec = partes[1];
		   	nom_rec = partes[2];
		   	calle = partes[3];
		   	no_ex = partes[4];
		   	no_in = partes[5];
		   	colonia = partes[6];
		   	cp = partes[7];		   	
		   	email = partes[8];
		   	
		   	textView4.setText( rfc_rec );
		   	editText1.setText( rfc_rec );

		   	textView1.setText(nom_rec);
		   	editText2.setText(nom_rec);

			textView22.setText(cp);
			editText3.setText(cp);
		   	/*
		   	String direc = calle + " " + no_ex + " " + no_in + " " + colonia + " C.P.:" + cp;
		   	textView8.setText( direc );
		   	editText3.setText( calle );
		   	editText4.setText( no_ex );
		   	editText7.setText( no_in );
		   	editText8.setText( colonia );
		   	editText14.setText( cp );
			editText16.setText( email );
			*/
		   	textView10.setText( email );
			editText4.setText( email );

		   	
		   	bt.setEnabled(true);
			bt.requestFocus();
	    }else{
	    	if (tipoFactura.equals("R")){
	    		bt.setEnabled(true);
				bt.requestFocus();
	        	textView4.setText( rfc_cli );
	        	editText1.setText( rfc_cli );
	        }else{
	        	textView4.setText("Flotilla NO Encontrada: "+rfc_cli);
		    	//msjcon =">"+ msjcon+ ">3>3>4>5>6>7>";
		    	msj("FLOTILLA:"+rfc_cli+"\nNO VALIDA");
		    	//msj2("Flotilla:7NO VALIDA");
	        }
	    }
	}	
	if (MSJ==4){
		String[] partes = cadena.split("\\>");
	   	String res = partes[0];
	   	if (res.equals("false")){
	   		if (tipoFactura.equals("R")){
	   			/*MSJ=5;			
				 Datos = ">"+"FA"+">"+pos+">";
				 DatosTransa = "";
				envia("M2",Datos,DatosTransa);
				*/
				textView4.setText("RFC: "+rfc_cli);
	        }else{
	        	MSJ=10;
		   		String res1 = partes[1];
		   		bt.setEnabled(true);
				bt.requestFocus();
		   		//res1 = "AQUI NO SE VE";//">"+ res1+ ">2>3>4>5>6>7>";
		   		//textView4.setText("Flotilla -->"+rfc_cli);
		   		//msj(res1);
	        }	   		
	   	}else{
	   		MSJ=5;			
			 Datos = ">"+"FA"+">"+pos+">";
			 DatosTransa = "";
			envia("M2");
	   	}
		
	}
	if (MSJ==3){
		MSJ=10;
		//textView5.setText("Busca Flotilla");
		 Datos = ">"+"FA"+">"+pos+">";
		 DatosTransa = ">"+"F0"+">"+d2+">"+d3+">"+d4+">"+d5+">->->->";//"+d6+">";
		 //Toast.makeText(this, "Envia M3",Toast.LENGTH_SHORT).show();	
		 gen_xml();
		 //envia("M0");
	}
	//Mensaje DATOS-Tranzaccion esperamos la respuesta con los datos
	if (MSJ==2){
		//textView5.setText("Respondio:"+cadena);
		msj(cadena);
	}
	//Mensaje DATOS-Datos a procesar
	if (MSJ==1){
		MSJ=2;
		 Datos = "";
		 DatosTransa = "";
		envia("M1");
	}
	//Mensaje INICIAL-Quien solicita la infomacion
	if (MSJ==0){
		//textView5.setText("Enviando solicitud...");
		MSJ=10;
		//Datos;
		 //DatosTransa;
		revisa_envia();
		/*if (editados){
			cap_edit();
			d2 = "R" + ">" + rfc_cli;//edit2.getText().toString();
			Datos = ">"+"FA"+">"+"KA"+">";
			DatosTransa = ">"+d1+">"+d2+">"+d3+">"+d4+">"+d5+">"+"P"+pos;//"+d6+">";	
			textView5.setText( Datos+"**"+DatosTransa);
		}else {
			Datos = ">"+"FA"+">"+pos+">";
			tickets = tickets.replace("\n", "|");
			DatosTransa = ">"+d1+">"+d2+">"+d3+">"+d4+">"+d5+">"+tickets;//"+d6+">";
			textView5.setText( Datos+"**"+DatosTransa);
		}
		*/		
		//envia("M0");
		//Toast.makeText(this, "Envia M0",Toast.LENGTH_SHORT).show();	
		gen_xml();
	}
}

/*
public void gen_xml(String tds, 
		String mac, 
		String version, 
		String mac_serial, 
		String nomad_serie, 
		String intentos, 
		String tipo_solicitud, 
		String tipo_cliente, 
		String cliente, 
		String tipo_pago, 
		String prov_pago, 
		String ultimos_num,
		String nombre,
		boolean res_pago, 
		String monto_venta_s){
    /
	if (digitos_tar.length()>15){
        digitos_tar = digitos_tar.substring(15);
    }
    monto_venta_s = monto_venta_s.replace(" ", "");
    tar_digi = digitos_tar;
    enti_banco_f = banco;
    */
public void gen_xml(){
	
	//request.addProperty( "d0" , M);
    //request.addProperty( "d1" , Enviando);
    //request.addProperty( "d2" , Datos);
    //request.addProperty( "d3", DatosTransa);
	//Toast.makeText(this, "GEN_XML",Toast.LENGTH_SHORT).show();
    
    //Enviando=">"+num_tabled+">"+mac+">"+version+">"+mac_serial+">"+nomad+">"+intentos+">";
	String[] partes;
	String num_tabled ="", mac ="",version="" ,mac_serial="" ,nomad="" ,intentos="" ;
	try
	{
		partes = Enviando.split("\\>");
	     num_tabled = partes[1];
	     mac = partes[2];
	     version = partes[3];
	     mac_serial = partes[4];
	     nomad = partes[5];
	     intentos = partes[6];
	}
	catch (Exception ex)
	{
		Toast.makeText(this, "TRONO DIVIDE Enviando",Toast.LENGTH_SHORT).show();
	}
    
	String tipo = "", pos = "";
	try
	{
		partes = Datos.split("\\>");
	    tipo = partes[1];
	    pos = partes[2];
	}
	catch (Exception ex)
	{
		Toast.makeText(this, "TRONO DIVIDE DATOS",Toast.LENGTH_SHORT).show();
	}   
    
    
    //DatosTransa = ">"+d1+">"+d2+">"+d3+">"+d4+">"+d5+">";//"+d6+">"; FACTURA RAPIDA-FLOTILLA  ** >F0>C>1>900>0001>2222>
    //DatosTransa = ">"+d1+">"+d2>d2.5+">"+d3+">"+d4+">"+d5+">"+"P"+pos+"> >";
    //DatosTransa = ">"+d1+">"+d2>d2.5+">"+d3+">"+d4+">"+d5+">>"+tickets;
    //Datos = ">"+"FA"+">"+pos+">";
	//DatosTransa = ">"+"F0"+">"+d2+">"+d3+">"+d4+">"+d5+">>>>";//"+d6+">";
	String tipo_solicitud = "" , tipo_cliente = "",cliente = "", tipo_pago ="" , enti_banco_f ="", ultimos_num = "", num_ref_f="" ;
	try
	{
		//textView5.setText(DatosTransa);
	    partes = DatosTransa.split("\\>");
	     tipo_solicitud = partes[1];
	     tipo_cliente = partes[2];
	     cliente = partes[3];
	     tipo_pago = partes[4];
	     enti_banco_f = partes[5];
	     ultimos_num = partes[6];
	     num_ref_f = partes[7];
	    //String num_ref_f = partes[8];
	}
	catch (Exception ex)
	{
		Toast.makeText(this, "TRONO DIVIDE DatosTransa",Toast.LENGTH_SHORT).show();
	}
	
	
    
    if (tipo_pago.equals("null")){
    	tipo_pago = "01";
    }
    tipo_pago = "900";
    if (enti_banco_f.equals("null")){
    	enti_banco_f = "000";
    }
    if (ultimos_num.equals("null")){
    	ultimos_num = "0000";
    }
    /*if (pos2.equals("")){
    	pos = pos2;
    }
    */
    
    String nombre = "";
    String calle = "";
    String num_ex = "";
    String num_int = "";
    String colonia = "";
    String localidad = "";
    String referencia = "";
    String municipio = "";
    String estado = "";
    String pais = "";
    String cp = "";
    String telefono = "";
    String correos = "";
    
    
    String sTexto = cliente;
    int contador = 0;
    String sTextoBuscado = "|";
    while (sTexto.indexOf(sTextoBuscado) > -1) {
        sTexto = sTexto.substring(sTexto.indexOf(sTextoBuscado)+sTextoBuscado.length(),sTexto.length());
        contador++; 
    }   
    
    if (contador>12){
    	partes = cliente.split("\\|");
        cliente = partes[0];
        nombre = partes[1];
        calle = partes[2];
        num_ex = partes[3];
        num_int = partes[4];
        colonia = partes[5];
        localidad = partes[6];
        referencia = partes[7];
        municipio = partes[8];
        estado = partes[9];
        pais = partes[10];
        cp = partes[11];
        telefono = partes[12];
        correos = partes[13];
    }
        

    final String text = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"+
            "<peticion>\n"+
            "   <mensaje-tipo tipo=\""+tipo+"\"></mensaje-tipo>\n"+
            "	<envio tds=\""+num_tabled+"\" mac=\""+ mac+"\" version=\""+version+"\" mac_serial=\""+mac_serial+"\" nomad=\""+nomad+"\" intentos=\""+intentos+"\"></envio>\n"+
            "   <datos>\n"+
            "       <posicion pos=\""+pos+"\"></posicion>\n"+
			"       <factura tipo_solicitu=\""+tipo_solicitud+"\" tipo_clien=\""+tipo_cliente+"\" cliente=\""+cliente+
			
			"\" tipo_pago=\""+tipo_pago+"\" enti_banco_f=\""+enti_banco_f+"\" dig_tarjeta=\""+ultimos_num+"\" nombre=\""+nombre+"\"\n"+
			"       	calle=\""+calle+"\" num_ex=\""+num_ex+"\" num_int=\""+num_int+"\" colonia=\""+colonia+"\" localidad=\""+localidad+"\" referencia=\""+referencia+"\" municipio=\""+municipio+"\" estado=\""+estado+"\"\n"+
			"       pais=\""+pais+"\" cp=\""+cp+"\" telefono=\""+telefono+"\" correos=\""+correos+"\" num_ref_f=\""+num_ref_f+"\">\n"+
			"       </factura>\n"+
            "   </datos>\n"+
            "</peticion>";

    /*
    byte[] data = null;
    try {
        data = text.getBytes("UTF-8");
    }catch (UnsupportedEncodingException e1){
        System.out.println(e1.getMessage());
    }
    String base64 = "";
    
    try {
        base64 = Base64.encodeBytes(data);      // Receiving side
    }catch (Exception err){
        System.out.println(err.toString());
    }
    */
    //Toast.makeText(this, "GEN_XML_FIN",Toast.LENGTH_SHORT).show();
   envia(text);
}


//*
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

			/*
            try
            {
                androidHttpTransport.call(SOAPACTION, envelope);
				byte[] data = null;
				data = (byte[]) envelope.getResponse();
				respuesta = new String(data, "US-ASCII");

            }catch(Exception e)
            {
                e.printStackTrace();
            }
            */

			String decoded = "ÑÑ";
			try {
				decoded = new String(respuesta.getBytes("ISO-8859-1"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}


			/*
			String deco= "ÑÑ";
			try {
				//deco = decodeString(respuesta);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			*/

			Message sms = new Message();
            sms.obj = decoded;
            puente.sendMessage(sms);
        }
    }).start();
}

	public String decodeString(String encodedString) throws UnsupportedEncodingException {
		return new String(encodedString.getBytes(), "UTF-8");

	}

//*/
	
	public void revisa_envia(){
		//Flotilla correcta
		//d1="F1  Cliente Flotilla ; o F2  RFC
		//d2 = tipoFactura + ">" + rfc_cli;//edit2.getText().toString();
		//d3 = "900";//Tipo de Pago.
		//d4 = "0001";//Proveedor de PAGO.
		//d5 = "2222";//Digitos
		
		//Factura de CLIENTE CON FLOTILLA
		if (tipoFactura.equals("C")){
	    	//SE A EDITADO LA INFORMACION
			if (editados){
				String mail = editText4.getText().toString();
				correo_fin = mail;
				//16_e
				//if (mail.length()>5){
					//Toast.makeText(this, "MAIL MAYOR A 5",Toast.LENGTH_SHORT).show();
					if(tickets.length()==0){
						//ES POR POCICION no hay tickets
						cap_edit();
						d1="F2";
						tipoFactura="R";
						d2 = tipoFactura + ">" + rfc_cli;//edit2.getText().toString();
						Datos = ">"+"FA"+">"+"KA"+">";
						DatosTransa = ">"+d1+">"+d2+">"+d3+">"+d4+">"+d5+">"+"P"+pos+">";
					}else{
						//ES POR tickets
						cap_edit();
						d1="F2";
						tipoFactura="R";
						d2 = tipoFactura + ">" + rfc_cli;//edit2.getText().toString();
						Datos = ">"+"FA"+">"+"KA"+">";
						tickets = tickets.replace("\n", "|");
						DatosTransa = ">"+d1+">"+d2+">"+d3+">"+d4+">"+d5+">"+tickets;
					}
				//}
				//else{
					//Toast.makeText(this, "-----------",	Toast.LENGTH_SHORT).show();
					//msj2("Requiere CORREO:");
					//editText4.requestFocus();
					//16_e
				//}
				
			}else{
				//NO SE A EDITADO LA INFORMACION
				if(tickets.length()==0){
					//ES POR POCICION no hay tickets
					Datos = ">"+"FA"+">"+pos+">";
					
					DatosTransa = ">"+d1+">"+d2+">"+d3+">"+d4+">"+d5+">->";
				}else{
					//ES POR tickets
					Datos = ">"+"FA"+">"+"KA"+">";
					tickets = tickets.replace("\n", "|");
					DatosTransa = ">"+d1+">"+d2+">"+d3+">"+d4+">"+d5+">"+tickets;
				}
			}
	    }else{
	    	//Es FACTRUA POR RFC
	    	//SE A EDITADO LA INFORMACION
			if (editados){				
				if(tickets.length()==0){
					//ES POR POCICION no hay tickets
					cap_edit();
					d1="F2";
					tipoFactura="R";
					d2 = tipoFactura + ">" + rfc_cli;// +"|"+ correo_fin;
					Datos = ">"+"FA"+">"+"KA"+">";
					DatosTransa = ">"+d1+">"+d2+">"+d3+">"+d4+">"+d5+">"+"P"+pos+">";
				}else{
					//ES POR tickets
					cap_edit();
					d1="F2";
					tipoFactura="R";
					d2 = tipoFactura + ">" + rfc_cli;// +"|"+ correo_fin;
					Datos = ">"+"FA"+">"+"KA"+">";
					tickets = tickets.replace("\n", "|");
					DatosTransa = ">"+d1+">"+d2+">"+d3+">"+d4+">"+d5+">"+tickets;
				}
				
			}else{
				//NO SE A EDITADO LA INFORMACION
				if(tickets.length()==0){
					//ES POR POCICION no hay tickets
					Datos = ">"+"FA"+">"+pos+">";					
					DatosTransa = ">"+d1+">"+d2+">"+d3+">"+d4+">"+d5+">->";
				}else{
					//HAY TICKETS para FACTURA
					Datos = ">"+"FA"+">"+"KA"+">";
					tickets = tickets.replace("\n", "|");
					DatosTransa = ">"+d1+">"+d2+">"+d3+">"+d4+">"+d5+">"+tickets;
				}
			}
	    }
	}
	

public void msj(String msjcon){
	
    msjcon =">"+ msjcon+ ">2>3>4>5>6>7>";
	Intent i = new Intent(this, Msj.class );
    i.putExtra("msjcon", msjcon);
    startActivity(i);
    
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
        public void run() {
            esp_fin();
        }
    }, 2000);
}

public void esp_fin(){
	/*
	Intent j = new Intent(this, MainActivity.class );
	j.putExtra("inicia", "1");
    startActivity(j);
    */
	env_main();
	finish();
}

public void msj2(String msjcon){
	//int index = 1;
	msjcon =">"+ msjcon+ ">2>3>4>5>6>7>"; 	    
	Intent i = new Intent(this, Msj.class );
    i.putExtra("msjcon", msjcon);
    startActivity(i);
}

public void atras(View view){
	if ( editados ){
		 if (ok_st) {
			 Intent i = new Intent(this, Fac_rapida.class );
			 i.putExtra("pos", pos);
			 i.putExtra("tipofac", tipoFactura);
			 i.putExtra("tickets", tickets);
			 i.putExtra("envia_fac", envia_fac);
			 i.putExtra("user_sol", user_sol);
			 startActivity(i);
			 finish();
		 }else{
			 Toast.makeText(this, "EDITADOS",Toast.LENGTH_SHORT).show();
			 editText_ref.setVisibility(View.VISIBLE);
			 button4.setVisibility(View.VISIBLE);
			 editados = false;
			 textView4.setVisibility(View.VISIBLE);
			 textView1.setVisibility(View.VISIBLE);
			 textView22.setVisibility(View.VISIBLE);
			 textView10.setVisibility(View.VISIBLE);

			 editText1.setVisibility(View.INVISIBLE);
			 editText2.setVisibility(View.INVISIBLE);
			 editText3.setVisibility(View.INVISIBLE);
			 editText4.setVisibility(View.INVISIBLE);

			 editText_ref.requestFocus();
		 }
	}else{
		Intent i = new Intent(this, Fac_rapida.class );
		i.putExtra("pos", pos);
		i.putExtra("tipofac", tipoFactura);
		i.putExtra("tickets", tickets);
		i.putExtra("envia_fac", envia_fac);
		i.putExtra("user_sol", user_sol);
	    startActivity(i);
		finish();
	}
}

public void fin(View view){
	/*
	Intent j = new Intent(this, MainActivity.class );
	j.putExtra("inicia", "1");
    startActivity(j);
	*/
	env_main();
	finish();
}


public void salir(String msj_fin){
	/*
    Intent i = new Intent(this, MainActivity.class );
    i.putExtra("inicia", "1");
    startActivity(i);
    */
	env_main();
	finish();	
	
}

public void editar(View view){
	editText4.requestFocus();
	ok_st = false;
	button4.setVisibility(View.INVISIBLE);
	editados = true;
	if (tipoFactura.equals("R") && edit_rfc){
		textView4.setVisibility(View.INVISIBLE);
		//textView4.setText("");
	}
	textView1.setVisibility(View.INVISIBLE);
	//textView1.setText("");
	textView22.setVisibility(View.INVISIBLE);
	//textView2.setText("");
	textView10.setVisibility(View.INVISIBLE);
	//textView10.setText("");
	editText_ref.setVisibility(View.INVISIBLE);
	
	//textView7.setText("Calle:");
	textView9.setText("Correo:");
	
	if (tipoFactura.equals("R")&& edit_rfc){
		editText1.setVisibility(View.VISIBLE);	
	}
	editText2.setVisibility(View.VISIBLE);
	editText3.setVisibility(View.VISIBLE);
	editText4.setVisibility(View.VISIBLE);
}

public void  cap_edit(){
	String rfc_e = editText1.getText().toString();
	if (rfc_e.length()==0){rfc_e="-";}
	
	String nombre_e = editText2.getText().toString();
	if (nombre_e.length()==0){nombre_e="-";}

	String cp = editText3.getText().toString();
	if (cp.length()==0){cp="-";}
	
	String mail = editText4.getText().toString();
	if (mail.length()==0){mail="-";}

	rfc_cli = rfc_e +"|"+ nombre_e +"|"+ "-" +"|"+ "-" +"|"+ "-" +"|"+ "-" +"|"+
			"-" +"|"+ "-" +"|"+ "-" +"|"+ "-" +"|"+ "-" +"|"+ cp +"|"+ "-" +"|"+ mail ;
}

public void fin(){
	/*
	//Toast.makeText(this, "A HOME",Toast.LENGTH_SHORT).show();
	Intent j = new Intent(this, MainActivity.class );
	j.putExtra("inicia", "1");
	startActivity(j);
	*/
	env_main();
	finish();	
}

public void ok(View view){
	if (ok_st){
		//Toast.makeText(this, "OK 1",Toast.LENGTH_SHORT).show();
		/*
		//textView5.setText("ENVIA INFOMACION A SERVIDOR");
		bt.setEnabled(false);
	    MSJ=0;
		mensajes("");
		revisa_envia();
		gen_xml();
		*/
		revisa_correo();
				
	}else{
		//Toast.makeText(this, "NO 1",Toast.LENGTH_SHORT).show();
		String mail = editText4.getText().toString();
		//if (mail.length()>5){
			ok_st=true;
			
			String rfc_e = editText1.getText().toString();
			String nombre_e = editText2.getText().toString();
			String cp = editText3.getText().toString();
			String email = editText4.getText().toString();
			correo_fin = email;

			editText_ref.setVisibility(View.VISIBLE);
			button4.setVisibility(View.VISIBLE);
			textView4.setVisibility(View.VISIBLE);
			textView1.setVisibility(View.VISIBLE);
			textView22.setVisibility(View.VISIBLE);
			textView10.setVisibility(View.VISIBLE);

			editText1.setVisibility(View.INVISIBLE);
			editText2.setVisibility(View.INVISIBLE);
			editText3.setVisibility(View.INVISIBLE);
			editText4.setVisibility(View.INVISIBLE);
			
			textView4.setText( rfc_e );
			textView10.setText( email );
		   	textView1.setText(nombre_e);
			textView22.setText( cp );
		   	editText_ref.requestFocus();
		//}else{
			//msj2("Requiere CORREO:");
			//editText4.requestFocus();
			//16
		//}
		
	}
		
}


	
	public void revisa_correo(){
		//Toast.makeText(this, "Revisa correo 2",Toast.LENGTH_SHORT).show();

		if (correo_fin.length()>0){
			//Toast.makeText(this, "Revisa correo 3",Toast.LENGTH_SHORT).show();
			revisa_envia();
			gen_xml_pago();

			//Toast.makeText(this, "Revisa correo 4",Toast.LENGTH_SHORT).show();
			ContentValues registro = new ContentValues();
			registro.put("msj_esp", "1");
			int cant = bd.update("config", registro, "num=1", null);
			//bd.close();
			if (cant == 1){
				//Toast.makeText(this, "ESPERA CERRAR", Toast.LENGTH_SHORT).show();
			}
			else
				Toast.makeText(this, "NO Graba en DB",Toast.LENGTH_SHORT).show();

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
		    
		    //Toast.makeText(this, "Inicia ESPERA",Toast.LENGTH_SHORT).show();
		    espera_fin();
		}else{
			//Envia a Confirmacion sin Correo.
			Intent i = new Intent(this, Fac_rapida1_msj.class);
		    i.putExtra("texto","Cliente sin CORREO:" );
		    i.putExtra("opcX", "REGRESAR");
		    i.putExtra("opcY", "CONTINUAR");
		    startActivityForResult(i, 0);
		}

	}
	
	@Override
	protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
		//EditText mEdtTarget = (EditText)findViewById(R.id.edtTarget);
	    if (requestCode == 0){
	    	//Respuesta de Continuar son Correo, enviara Factura Completa
	    	if (resultCode == RESULT_OK) {
	    		revisa_envia();
				gen_xml_pago();
				
				ContentValues registro = new ContentValues();
				registro.put("msj_esp", "1");
				int cant = bd.update("config", registro, "num=1", null);
				//bd.close();
				if (cant == 1){
					//Toast.makeText(this, "ESPERA CERRAR", Toast.LENGTH_SHORT).show();
				}
				else
					Toast.makeText(this, "NO Graba en DB",Toast.LENGTH_SHORT).show();

				//Validar si se tiene metodo de Pago, si vacio solicita el dato y digitos.
				Intent i = new Intent(this, Metodo_pago.class );
				i.putExtra("xmp_pago1", xml_pago1);
			    i.putExtra("xmp_pago2", xml_pago2);
			    i.putExtra("met_pago_f", met_pago_f);
			    i.putExtra("digitos_f", digitos_f);
			    i.putExtra("enti_banco_f", enti_banco_f);
			    i.putExtra("tipo_compro", "F");
			    i.putExtra("m_ticket", "0");
				i.putExtra("usocfdi", usocfdi);
			    startActivity(i);

			    //Toast.makeText(this, "Inicia ESPERA",Toast.LENGTH_SHORT).show();
			    espera_fin();
	        }else{
	        //Respuesta de regresar, enviara a Editar al Cliente, foco en CORREO.
	        	editar(null);
	        }
	    }else{
			/*
			//MSM 05/Oct/2017 Ver:1.8-0
			//Regresa de selecion del Lector a conectar
			if (requestCode == 1){
				if (data != null && resultCode == RESULT_OK) {
					lector_cone = data.getStringExtra("lecBusca");
					envia_PB();
				}
			}
			*/
		}
	}


	public void espera_fin()
	{
		/*
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				//Ejecuta
				revisa_db();
			}
		}, 0, 500);
		*/

		//Toast.makeText(this, "ESPERA 3",Toast.LENGTH_SHORT).show();
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
           // acciones que se ejecutan tras los milisegundos
				revisa_db();
			}
		}, 500);

	}
	
	public void revisa_db(){
		int msj_esp;
		Cursor fila = bd.rawQuery("select * from config where num=1"
				+ "", null);
		if (fila.moveToFirst()) {
			msj_esp=fila.getInt(16);
			if (msj_esp==1){
				espera_fin();
				//Toast.makeText(this, "Sigue esperando en DB=1 **3 seg +**",Toast.LENGTH_SHORT).show();
			}
			if (msj_esp==2){
				ContentValues registro = new ContentValues();
				 registro.put("msj_esp", "0");
					int cant = bd.update("config", registro, "num=1", null);
					//bd.close();
					if (cant == 1){
						//Toast.makeText(this, "DEJA ESPERARA", Toast.LENGTH_SHORT).show();
					}
					else{
						Toast.makeText(this, "NO Graba en DB",Toast.LENGTH_SHORT).show();
					}
			}
			if (msj_esp==0){
				//Toast.makeText(this, "FIN FAC=0",Toast.LENGTH_SHORT).show();
				fin();
			}
		}
		/*
	 if (fila.moveToFirst()) {
		 msj_esp=fila.getInt(16);
		 //msj_esp=0;
			if (msj_esp==1){
				espera_fin();
				//Toast.makeText(this, "Sigue esperando en DB=1",Toast.LENGTH_SHORT).show();
			}
			if (msj_esp==2){
				ContentValues registro = new ContentValues();
				 registro.put("msj_esp", "0");
					int cant = bd.update("config", registro, "num=1", null);
					//bd.close();
					if (cant == 1){
						//Toast.makeText(this, "FIN 0", Toast.LENGTH_SHORT).show();
					}
					else{
						//Toast.makeText(this, "NO Graba en DB",Toast.LENGTH_SHORT).show();
					}
			}
			if (msj_esp==0){
				//Toast.makeText(this, "FIN FAC=0",Toast.LENGTH_SHORT).show();
				fin();	
			}
		}
		*/
	}


	public void gen_xml_pago(){
		
	    String[] partes = Enviando.split("\\>");
	    String num_tabled = partes[1];
	    String mac = partes[2];
	    String version = partes[3];
	    String mac_serial = partes[4];
	    String nomad = partes[5];
	    String intentos = partes[6];
	       
	    partes = Datos.split("\\>");
	    String tipo = partes[1];
	    String pos = partes[2];
	    
	    //textView5.setText(DatosTransa);
	    partes = DatosTransa.split("\\>");
	    String tipo_solicitud = partes[1];
	    String tipo_cliente = partes[2];
	    String cliente = partes[3];
	    String tipo_pago = partes[4];
	    String enti_banco_f = partes[5];
	    String ultimos_num = partes[6];
	    String num_ref_f = partes[7];
	    //String num_ref_f = partes[8];
	    
	    if (tipo_pago.equals("null")){
	    	tipo_pago = "01";
	    }
	    tipo_pago = "900";
	    if (enti_banco_f.equals("null")){
	    	enti_banco_f = "000";
	    }
	    if (ultimos_num.equals("null")){
	    	ultimos_num = "0000";
	    }
	    /*if (pos2.equals("")){
	    	pos = pos2;
	    }
	    */
	    
	    String nombre = "";
	    String calle = "";
	    String num_ex = "";
	    String num_int = "";
	    String colonia = "";
	    String localidad = "";
	    String referencia = "";
	    String municipio = "";
	    String estado = "";
	    String pais = "";
	    String cp = "";
	    String telefono = "";
	    String correos = "";
	    
	    
	    String sTexto = cliente;
	    int contador = 0;
	    String sTextoBuscado = "|";
	    while (sTexto.indexOf(sTextoBuscado) > -1) {
	        sTexto = sTexto.substring(sTexto.indexOf(sTextoBuscado)+sTextoBuscado.length(),sTexto.length());
	        contador++; 
	    }   
	    
	    if (contador>12){
	    	partes = cliente.split("\\|");
	        cliente = partes[0];
	        nombre = partes[1];
	        calle = partes[2];
	        num_ex = partes[3];
	        num_int = partes[4];
	        colonia = partes[5];
	        localidad = partes[6];
	        referencia = partes[7];
	        municipio = partes[8];
	        estado = partes[9];
	        pais = partes[10];
	        cp = partes[11];
	        telefono = partes[12];
	        correos = partes[13];
	    }
	     
	    String cantidad = "", cod_barras = "";
	    if(envia_fac.equals("AL")){
	    	tipo = "AL";
	    	pos = "KA";
	    	partes = tickets.split("-");
		    cantidad = partes[1];
		    cod_barras = partes[2];
		    num_ref_f = "";
	    }

	    xml_pago1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"+
	            "<peticion>\n"+
	            "   <mensaje-tipo tipo=\""+tipo+"\"></mensaje-tipo>\n"+
	            "	<envio tds=\""+num_tabled+"\" mac=\""+ mac+"\" version=\""+version+"\" mac_serial=\""+mac_serial+"\" nomad=\""+nomad+"\" intentos=\""+intentos+"\"></envio>\n"+
	            "   <datos>\n"+
	            "       <posicion pos=\""+pos+"\"></posicion>\n"+
	            "   	<usuario user_sol=\""+user_sol+"\"></usuario>\n"+
				"       <factura tipo_solicitu=\""+tipo_solicitud+"\" tipo_clien=\""+tipo_cliente+"\" cliente=\""+cliente+"\" ";
	    
	    xml_pago2 = " nombre=\""+nombre+"\" "+
				"calle=\""+calle+"\" num_ex=\""+num_ex+"\" num_int=\""+num_int+"\" colonia=\""+colonia+"\" localidad=\""+localidad+"\" referencia=\""+referencia+"\" municipio=\""+municipio+"\" estado=\""+estado+"\"\n"+
				"pais=\""+pais+"\" cp=\""+cp+"\" telefono=\""+telefono+"\" correos=\""+correos+"\" num_ref_f=\""+num_ref_f+"\" >\n"+
				"       </factura>\n";
	    
	    if(envia_fac.equals("AL")){
	    	xml_pago2 += "       <almacen peticiont=\"FA\" cod_barrat=\""+cod_barras+"\" catidadt=\""+cantidad+"\">\n"+
	    				"       </almacen>\n";
	    }
	    xml_pago2 += "   </datos>\n"+
	            	 "</peticion>";
	}

	/*
	public void pide_metodo(){
		//Busca en la DB si tiene datos y se los asigna a la variable
		Cursor fila = bd.rawQuery("select * from config where num=1"
				+ "", null);
		if (fila.moveToFirst()) {
			met_pago_f=fila.getString(18);
			if (met_pago_f.equals("")) {
				Intent j = new Intent(this, Msj_fin.class );
				startActivity(j);
			}
		}	
	}
	*/

	//Deshabilitar BOTON atras
	@Override
	public void onBackPressed() {	
		}


	/*
	public void solo_nom(String xx){

		textView1.setText("LLEGO solo nom");
		try {
			/*
			String utf ="";
			utf = URLEncoder.de(xx, "UTF-8");
			* /
			String busca1 = "factura";
			String busca2;
			busca2 = "nombre";
			nom_rec = regresa_xml(xx, busca1,busca2);
			textView1.setText(nom_rec);
		}catch (Exception err){
			textView1.setText("MAL");
			System.out.println(err.toString());
		}
	}
	*/

	
	public void busca_cadena1(String xml){

	    String busca1 = "factura";
	    String busca2 = "resf";
	    String dato=regresa_xml(xml, busca1,busca2);
	    //textView_con.setText(dato);
	    if (dato.equals("true")){
		    busca2 = "tipo_solicitu";
		    dato=regresa_xml(xml, busca1,busca2);
	    		
	        if (dato.equals("F0")){
				busca2 = "rfc";
				rfc_rec = regresa_xml(xml, busca1,busca2);

				//MSM 06/Nov/2017 Ver:1.8-0
				if (envia_fac.equals("PB")){

					botones.setVisibility(View.INVISIBLE);
					datos_cliente.setVisibility(View.INVISIBLE);
					solo_rfc.setVisibility(View.VISIBLE);
					solo_rfc.setText( rfc_rec );

					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						public void run() {
							//Espera 3 seg para mostrar RFC de Cliente.
							rev_lectores("F");
						}
					}, 4000);

				}else{
					busca2 = "nombre";
					nom_rec = regresa_xml(xml, busca1,busca2);
					//Toast.makeText(this, "Encontro 3: "+ nom_rec, Toast.LENGTH_LONG).show();
					busca2 = "calle";
					calle = regresa_xml(xml, busca1,busca2);
					busca2 = "num_ex";
					no_ex = regresa_xml(xml, busca1,busca2);
					busca2 = "num_int";
					no_in = regresa_xml(xml, busca1,busca2);
					busca2 = "colonia";
					colonia = regresa_xml(xml, busca1,busca2);
					busca2 = "cp";
					cp = regresa_xml(xml, busca1,busca2);
					busca2 = "correos";
					email = regresa_xml(xml, busca1,busca2);
					busca2 = "usocfdi";
					usocfdi = regresa_xml(xml, busca1,busca2);

					textView4.setText( rfc_rec );
					editText1.setText( rfc_rec );

					textView10.setText( email );
					editText4.setText( email );
					correo_fin = email;

					nom_rec = nom_rec.replace("Ã\u0091","Ñ");
					nom_rec = nom_rec.replace("Ã\u0081","Á");
					nom_rec = nom_rec.replace("Ã\u0089","É");
					nom_rec = nom_rec.replace("Ã\u008D","Í");
					nom_rec = nom_rec.replace("Ã\u0093","Ó");
					nom_rec = nom_rec.replace("Ã\u009A","Ú");

					textView1.setText(nom_rec);
					editText2.setText(cp);

					/*
					String direc = calle + " " + no_ex + " " + no_in + " " + colonia + " C.P.:" + cp;
					textView8.setText( direc );

					editText3.setText( calle );
					//editText4.setText( no_ex );
					editText8.setText( no_ex );
					editText7.setText( no_in );
					editText9.setText( colonia );
					//editText14.setText( cp );
					*/

					bt.setEnabled(true);
					bt.requestFocus();

					edit_rfc = false;
				}
	        }else{
	        	//Toast.makeText(this, "F1", Toast.LENGTH_SHORT).show();
	        	busca1 = "display";    	    
	    	    busca2 = "dato-impresiond";
	    	    String resdis = regresa_xml(xml, busca1,busca2);
				//textView5.setText("Respondio:"+resdis);
	    		//msj(resdis);
	        }
	    }else{
	    	busca2 = "tipo_solicitu";
		    dato=regresa_xml(xml, busca1,busca2);
	    	if (dato.equals("F1")){
	    		busca1 = "display";    	    
	    	    busca2 = "dato-impresiond";
	    	    String resdis = regresa_xml(xml, busca1,busca2);
	        	//textView5.setText("Respondio:"+resdis);
	    		msj(resdis);
	    		
	    	}else{
	    		if (tipoFactura.equals("R")){
	    			if (envia_fac.equals("PB")){
	    				msj2("RFC:\nNO ESTA REGISTRADO\nCONTINUA CON LA OPERACION");
						Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							public void run() {
				           // acciones que se ejecutan tras los milisegundos
								tipoFactura = "";
								rfc_cli_pb = "";
								//MSM 05/Oct/2017 Ver:1.8-0
								rev_lectores("T");
								//envia_PB("T");
							}
						}, 2000);       		
					}else{
						bt.setEnabled(true);
						bt.requestFocus();
			        	textView4.setText(rfc_cli);
			        	editText1.setText( rfc_cli );
			        	msj2("RFC:\nNO ESTA REGISTRADO\nCONTINUA CON LA OPERACION");
					}
		    		
		        	
		        }else{
		        	if (envia_fac.equals("PB")){
		        		msj2("CLIENTE:\nNO ESTA REGISTRADO\nCONTINUA CON LA OPERACION");
						Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							public void run() {
				           // acciones que se ejecutan tras los milisegundos
								tipoFactura = "";
								rfc_cli_pb = "";
								//MSM 05/Oct/2017 Ver:1.8-0
								rev_lectores("T");
								//envia_PB("T");
							}
						}, 2000);       		
					}else{
						//textView4.setText("Flotilla NO Encontrada: "+rfc_cli);
				    	//msjcon =">"+ msjcon+ ">3>3>4>5>6>7>";
				    	msj("CLIENTE:\nNO ESTA REGISTRADO\nCONTINUA CON LA OPERACION");
				    	//msj2("Flotilla:\nNO VALIDA");
					}		        	
		        }	
	    	}
	    }
	}
	
	
	public void envia_PB (){
		
		File path = new File(Environment.getExternalStorageDirectory(), "Tickets");
        path.mkdirs();

        //Una vez creado disponemos de un archivo para guardar datos
        try
        {
            File ruta_sd = Environment.getExternalStorageDirectory();

            File f = new File(ruta_sd.getAbsolutePath(), "Tickets/PBDATA.txt");

            OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(f));
            
            String conf_cb = pos+"\n";		//Pocicion de la venta, 99=Multiples tickets
			conf_cb += lector_cone+"\n";	//Dispositivo a Conectar.
            conf_cb += servodorPB+"\n";		//Servidor SIGMA
            conf_cb += tds+"\n";			//Terinal VeriBox que Solicita
            conf_cb += user_sol+"\n";		//Usuario que Realiza la Transaccion
            conf_cb += "F" +"\n";			//Tipo de Comprobante que se entrega T=Ticket, F=Factura
            conf_cb += tipoFactura+"\n";	//Tipo de Factura que se Realizara C=Cliente Registrado, R=RFC
            conf_cb += rfc_cli_pb+"\n";		//Cliente a quien se Factura: RFC o No Cliente
			conf_cb += total_PB_S +"\n";	//Monto a Cobrar ya que son Multiples tickets
			conf_cb += tickets.replace("\n", "|") + "\n";//numeros de referencias para Cobro Bancario

            fout.write(conf_cb);
            fout.close();

			//Liberar del Modo Kisco
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				stopLockTask();
			}
        }
        catch (Exception ex)
        {
            //Log.e("Ficheros", "Error al escribir fichero a tarjeta SD");
        	Toast.makeText(this, "Error al escribir fichero a tarjeta SD", Toast.LENGTH_LONG).show();
        }

		Intent launchIntent = getPackageManager().getLaunchIntentForPackage("mx.qpay.testsdk");
		startActivity(launchIntent);
	    finish();
	}

	//MSM 05/Oct/2017 Ver:1.8-0
	//Revisa lectores, si son mas de 1 envia a seleccinar Lector.
	public void rev_lectores(String dato_enviaPB){
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
		int dis = 0;//, position_con = 0;
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
					lector_cone = pairedDevices[i].getName();
					dis++;
					//position_con = i;
					//Al encontrar por lo menos un lector lo asigna
					i = pairedDevices.length;
				}
			}else{
				//Registra los dispositivos encontrados
				lector_cone = pairedDevices[i].getName();
				dis++;
				//position_con = i;
				//Al encontrar por lo menos un lector lo asigna
				i = pairedDevices.length;
			}
			/*
			if (usoLector == 0){compara = nom_blue.substring(0,2);}
			else {compara = nom_blue;}

			if (compara.equals(lector_busca)){
				//Registra los dispositivos encontrados
				lector_cone = pairedDevices[i].getName();
				dis++;
				//position_con = i;
				//Al encontrar por lo menos un lector lo asigna
				i = pairedDevices.length;
			}
			*/
		}

		if(dis == 0){
			if (lector_busca.length()>2)msj("Lector ASIGNADO,\nPero NO Emparejado");
			else msj("Lector NO\nVinculado.");
			//finish();
		}else{
			envia_PB();
			//textView43.setText(nom_blue_mues);
			//bbDeviceController.connectBT(pairedDevices[position_con]);
		}

		/*
        if(lector_cone.length()>0){
			//Toast.makeText(this, lector_cone, Toast.LENGTH_LONG).show();
			envia_PB();
        }else{
			if (lector_busca.length()>2)msj("AUN Lector ASIGNADO,\nPero NO Emparejado");
			else msj("AUN Lector NO\nVinculado.");
			//finish();
        }
        */

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
				//Toast.makeText(this, "Encontro 1: "+ respg, Toast.LENGTH_LONG).show();
	            envi_retun=respg;

	        }else{
	            envi_retun="Respuesta INCOMPLETA";
	        }
	    }else{
	        envi_retun="Respuesta INCOMPLETA";
	    }
		//Toast.makeText(this, "Encontro 2: "+ envi_retun, Toast.LENGTH_LONG).show();
		return envi_retun;

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

	public void teclado(View view){
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}


}	