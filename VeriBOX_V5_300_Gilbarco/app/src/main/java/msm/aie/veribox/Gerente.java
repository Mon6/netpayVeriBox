package msm.aie.veribox;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class Gerente extends Activity{

	private LinearLayout vis_ser1, vis_ser2, visTurno, visTan, visRec;
	SQLiteDatabase bd;
	private final String NAMESPACE = "urn:veriboxwsdl";
	private String URL;// = "http://192.168.1.38/Veribox/Veribox.php";
	private final String SOAPACTION = "urn:veriboxwsdl#veribox";
	private final String METHOD = "veribox";
	Spinner sp1, sp2, sp3, sp4, sp5, sp6;
	int MSJ=10;
	private EditText editText1,editText2,editText3, editText4;
	//Captura de XY
	private TextView textView_xy, TextView001;
	StringBuilder stringBuilder = new StringBuilder();
	private Coor_xy cox_coy;
	int cam1, cam2, cam3, cam4, cam5, visFueraServ, visCamTurno, visInvTanques, visRecTurno;;
	private Button bt16,bt17,bt18,bt19, bt20;
			
@Override
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState); 
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.gerente);

    this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    
    cam1=0;
    cam2=0;
    cam3=1;
    cam4=0;
    cam5=0;
    
    bt16 = (Button)findViewById(R.id.button16);
    bt17 = (Button)findViewById(R.id.button17);
    bt18 = (Button)findViewById(R.id.button18);
    bt19 = (Button)findViewById(R.id.button19);
	bt20 = (Button)findViewById(R.id.button20);
    
    editText1 = (EditText) findViewById(R.id.editText1);
    editText2 = (EditText) findViewById(R.id.editText2);
    editText3 = (EditText) findViewById(R.id.editText3);
	editText4 = (EditText) findViewById(R.id.editText4);
    
    editText1.setVisibility(View.INVISIBLE);
    editText2.setVisibility(View.INVISIBLE);
    editText3.setVisibility(View.INVISIBLE);
	editText4.setVisibility(View.INVISIBLE);

	vis_ser1 = (LinearLayout) findViewById(R.id.vis_ser1);
	vis_ser2 = (LinearLayout) findViewById(R.id.vis_ser2);
	visTurno = (LinearLayout) findViewById(R.id.visTurno);
	visTan = (LinearLayout) findViewById(R.id.visTan);
	visRec = (LinearLayout) findViewById(R.id.visRec);

	//lee las opciones a mostrar y procesar.
	Leedb();
    
  /*
	//---------------------------------------------------------------------
    sp1 = (Spinner) findViewById(R.id.spinner1);
    ArrayAdapter adapter1 = ArrayAdapter.createFromResource(
            this, R.array.servi_turno, android.R.layout.simple_spinner_item);
    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    sp1.setAdapter(adapter1);    
//---------------------------------------------------------------------   
    sp2 = (Spinner) findViewById(R.id.spinner2);
    sp2.setAdapter(adapter1);    
  //---------------------------------------------------------------------   
    sp3 = (Spinner) findViewById(R.id.spinner3);    
    sp3.setAdapter(adapter1);    
  //---------------------------------------------------------------------   
    sp4 = (Spinner) findViewById(R.id.spinner4);
    ArrayAdapter adapter3 = ArrayAdapter.createFromResource(
            this, R.array.corte_turno, android.R.layout.simple_spinner_item);
    adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    sp4.setAdapter(adapter3);       
//---------------------------------------------------------------------
    sp5 = (Spinner) findViewById(R.id.spinner5);
    ArrayAdapter adapter5 = ArrayAdapter.createFromResource(
            this, R.array.ops_sn, android.R.layout.simple_spinner_item);
    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    sp5.setAdapter(adapter5);         
//---------------------------------------------------------------------
    sp6 = (Spinner) findViewById(R.id.spinner6);
    sp6.setAdapter(adapter5);
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
   				revisar (rev);			
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
public void revisar (String rev_coor) {
	//Boton Atras
	if (rev_coor.equals("H1") || rev_coor.equals("G1")){
		fin(null);
  }
	//Cambia Conf
	if (rev_coor.equals("A8")){
		conf(null);
  }
	//Fuera Servicio
	if (rev_coor.equals("F7") && visFueraServ==0){
			fuera_ser(null);
  }
	//En Servicio
	if (rev_coor.equals("E7") && visFueraServ==0){
			en_ser(null);
  }
	//Corte Turno
	if (rev_coor.equals("D7") && visCamTurno == 0){
		cambio_turno(null);
  }
	//Corte Tanques
	if (rev_coor.equals("C7") && visInvTanques == 0){
		corte_tanque(null);
  }
	//Corte Turno
	if (rev_coor.equals("B7") && visRecTurno == 0){
		corte_turno(null);
	}
	
	//Camb1 (Fuera servicio).
	if (rev_coor.equals("F4") || (rev_coor.equals("F5"))){
		cam1(null);
  }
	//Camb2 (En servicio)
	if (rev_coor.equals("E4")|| (rev_coor.equals("E5"))){
		cam2(null);
  }
	//Cambi3 (cambio Turno 1)
	if (rev_coor.equals("D4")){
		cam3(null);
  }
	//camb4 (cambio Turno 2)
	if (rev_coor.equals("D5")){
		cam4(null);
  }
	//Camb5 (Colecta)
	if (rev_coor.equals("B4") || (rev_coor.equals("B5"))){
		cam5(null);
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
	//Lee DB de la aplicacion
//---------------------------------------------------------------------
	AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
			"tablet", null, 1);
	bd = admin.getWritableDatabase();
	//*****************************************************************************		
	Cursor fila = bd.rawQuery("select * from config where num=1"
			+ "", null);
	if (fila.moveToFirst()) {
		String config = fila.getString(27);

		if (!config.equals("00AA00AA")){
			visFueraServ = fila.getInt(33);	//extra4 -> 0 activo, 1 desactivado
			visCamTurno = fila.getInt(34);	//extra5 -> 0 activo, 1 desactivado
			visInvTanques = fila.getInt(35);	//extra6 -> 0 activo, 1 desactivado
			visRecTurno = fila.getInt(36);	//extra7 -> 0 activo, 1 desactivado
			if (visFueraServ == 1){vis_ser1.setVisibility(View.INVISIBLE);
				vis_ser2.setVisibility(View.INVISIBLE);}
			if (visCamTurno == 1){visTurno.setVisibility(View.INVISIBLE);}
			if (visInvTanques == 1){visTan.setVisibility(View.INVISIBLE);}
			if (visRecTurno == 1){visRec.setVisibility(View.INVISIBLE);}
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

public void cam1(View view){
	editText1.requestFocus();
	if (cam1==0){
		cam1=1;
		bt16.setText("ISLA");
		editText1.setVisibility(View.VISIBLE);
		editText2.setVisibility(View.INVISIBLE);
		editText3.setVisibility(View.INVISIBLE);
		editText1.setText("");
	}else{
		cam1=0;
		bt16.setText("TODAS");
		editText1.setVisibility(View.INVISIBLE);
		editText2.setVisibility(View.INVISIBLE);
		editText3.setVisibility(View.INVISIBLE);
		editText1.setText("");
	}
}

public void cam2(View view){
	editText2.requestFocus();
	if (cam2==0){
		cam2=1;
		bt17.setText("ISLA");
		editText2.setVisibility(View.VISIBLE);
		editText1.setVisibility(View.INVISIBLE);
		editText3.setVisibility(View.INVISIBLE);
		editText2.setText("");
	}else{
		cam2=0;
		bt17.setText("TODAS");
		editText2.setVisibility(View.INVISIBLE);
		editText2.setVisibility(View.INVISIBLE);
		editText3.setVisibility(View.INVISIBLE);
		editText2.setText("");
	}
}

public void cam3(View view){
	editText3.requestFocus();
	if (cam3==1){
		bt18.setText("T2");
		cam3=2;
	}else{
		if (cam3==2){
			cam3=3;
			bt18.setText("T3");
		}else{
			cam3=1;
			bt18.setText("T1");
		}
	}
}

public void cam4(View view){
	editText3.requestFocus();
	if (cam4==0){
		bt19.setText("ISLA");
		editText3.setVisibility(View.VISIBLE);
		editText1.setVisibility(View.INVISIBLE);
		editText2.setVisibility(View.INVISIBLE);
		editText3.setText("");
		cam4=1;
	}else{
		cam4=0;
		bt19.setText("TODAS");
		editText3.setVisibility(View.INVISIBLE);
		editText2.setVisibility(View.INVISIBLE);
		editText3.setVisibility(View.INVISIBLE);
		editText3.setText("");
	}
}

	public void cam5(View view){
		editText4.requestFocus();
		if (cam5==0){
			cam5=1;
			bt20.setText("ISLA");
			editText1.setVisibility(View.INVISIBLE);
			editText2.setVisibility(View.INVISIBLE);
			editText3.setVisibility(View.INVISIBLE);
			editText4.setVisibility(View.VISIBLE);
			editText4.setText("");
		}else{
			cam5=0;
			bt20.setText("TODAS");
			editText1.setVisibility(View.INVISIBLE);
			editText2.setVisibility(View.INVISIBLE);
			editText3.setVisibility(View.INVISIBLE);
			editText4.setVisibility(View.INVISIBLE);
			editText4.setText("");
		}
	}

public void fuera_ser(View view){
	String isla;
	String ms_exe = "";
	if(cam1 == 0){
		isla = "00";
		ms_exe = "TODAS LAS ISLAS"; 
	}else{
		isla = editText1.getText().toString();		
		if (isla.length()>0){
			ms_exe = "ISLA: " + isla;
			//msj("Mayor a 0");
		}else{
			msj("NO Valido");
			return;
		}
	}
	
	Intent i = new Intent(this, Msj_geren.class );
    i.putExtra("envia1", "1");
    i.putExtra("isla", isla);
    i.putExtra("envia3", "-");
    i.putExtra("mensaje", "SOLICITUD DE:\nFUERA DE SERVICIO\n"+ ms_exe);
    startActivity(i);
	
	//////////////////////////////
	//msj("Envia mensaje a Servidor");
	//MSJ=0;        	
	//mensajes("1",isla,"-");
	
	
	/*
	AlertDialog.Builder adb = new AlertDialog.Builder(this);
    adb.setMessage("Solicitud: FUERA DE SERVICIO");
    adb.setCancelable(false);
    adb.setPositiveButton("Continuar", new DialogInterface.OnClickListener() { //Boton Positivio 
        
        public void onClick(DialogInterface dialog, int which) {
            // TODO Auto-generated method stub
        	String isla="";
        	if((sp1.getSelectedItem().toString()).equals("TODAS")){
        		isla = "00";
        	}else{
        		isla = editText1.getText().toString();		
        		if (isla.length()>0){
        			//msj("Mayor a 0");
        		}else{
        			msj("NO Valido");
        			return;
        		}
        	}
        	msj("Envia mensaje a Servidor");
        	MSJ=0;        	
        	mensajes("1",isla,"-");
        }
    });
    adb.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {//Boton negativo
        
        public void onClick(DialogInterface dialog, int which) {
            // TODO Auto-generated method stub
            dialog.cancel();//Se cancela el AlertDialog
            return;
        }
    });
    adb.show();//Se muestra el AlertDialog 
    
    */
  
}

public void en_ser(View view){
	String ms_exe;
	String isla="";
	if(cam2 == 0){
		isla = "00";
		ms_exe = "TODAS LAS ISLAS"; 
	}else{
		isla = editText2.getText().toString();
		if (isla.length()>0){
			ms_exe = "ISLA: " + isla;
		}else{
			msj("NO Valido");
			return;
		}
	}
	
	Intent i = new Intent(this, Msj_geren.class );
    i.putExtra("envia1", "2");
    i.putExtra("isla", isla);
    i.putExtra("envia3", "-");
    i.putExtra("mensaje", "SOLICITUD DE:\nEN SERVICIO\n"+ ms_exe);
    startActivity(i);
    
	//msj("Envia mensaje a Servidor");
	//MSJ=0;
	//mensajes("2",isla,"-");
	
	/*
	
	AlertDialog.Builder adb = new AlertDialog.Builder(this);
	adb.setMessage("Solicitud: EN SERVICIO");
	adb.setCancelable(false);
    adb.setPositiveButton("Continuar", new DialogInterface.OnClickListener() { //Boton Positivio 
		
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			String isla="";
			if((sp2.getSelectedItem().toString()).equals("TODAS")){
				isla = "00";
			}else{
				isla = editText2.getText().toString();
				if (isla.length()>0){
					//msj("Mayor a 0");
				}else{
					msj("NO Valido");
					return;
				}
			}
			msj("Envia mensaje a Servidor");
			MSJ=0;
			mensajes("2",isla,"-");
		}
	});
	adb.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {//Boton negativo
		
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			dialog.cancel();//Se cancela el AlertDialog
		}
	});
	adb.show();//Se muestra el AlertDialog
	
	*/

}


public void cambio_turno(View view){
	String isla="";
	String turno="";
	String ms_exe;
	
	if(cam4 == 0){
		isla = "00";
		ms_exe = "TODAS LAS ISLAS"; 
	}else{
		isla = editText3.getText().toString();
		if (isla.length()>0){
			ms_exe = "ISLA: " + isla;
		}else{
			msj("NO Valido");
			return;
		}
		/*
		isla = editText3.getText().toString();
		if (isla.length()<1){
			msj("NO Valido");
			return;
		}
		*/
	}
	
	turno = String.valueOf(cam3);
	
	Intent i = new Intent(this, Msj_geren.class );
    i.putExtra("envia1", "3");
    i.putExtra("isla", isla);
    i.putExtra("envia3", turno);
    i.putExtra("mensaje", "SOLICITUD DE:\nCAMBIO DE TURNO\n"+ ms_exe +"\nTurno:"+ turno);
    startActivity(i);
	

}

	public void corte_turno(View view){
		String isla="";
		String ms_exe;

		if(cam5 == 0){
			isla = "00";
			ms_exe = "TODAS LAS ISLAS";
		}else{
			isla = editText4.getText().toString();
			if (isla.length()>0){
				ms_exe = "ISLA: " + isla;
			}else{
				msj("NO Valido");
				return;
			}
		}

		Intent i = new Intent(this, Msj_geren.class );
		i.putExtra("envia1", "5");
		i.putExtra("isla", isla);
		i.putExtra("envia3", "0");
		i.putExtra("mensaje", "SOLICITUD DE:\nCOLECTA PARCIAL\n"+ ms_exe +"\nTurno actual");
		startActivity(i);
	}

public void corte_tanque(View view){
	
	Intent i = new Intent(this, Msj_geren.class );
    i.putExtra("envia1", "4");
    i.putExtra("isla", "");
    i.putExtra("envia3", "");
    i.putExtra("mensaje", "SOLICITUD DE:\nINVENTARIO DE TANQUES");
    startActivity(i);
	
	//msj("Envia mensaje a Servidor");
	//MSJ=0;
	//mensajes("4","","");
	
	/*
	AlertDialog.Builder adb = new AlertDialog.Builder(this);
	adb.setMessage("Solicitud: CORTE DE TANQUES");
	adb.setCancelable(false);
	adb.setPositiveButton("Continuar", new DialogInterface.OnClickListener() { //Boton Positivio 
		
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			msj("Envia mensaje a Servidor");
			MSJ=0;
			mensajes("4","","");
		}
	});
	adb.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {//Boton negativo
		
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			dialog.cancel();//Se cancela el AlertDialog
		}
	});
	adb.show();//Se muestra el AlertDialog
	*/	
}

/*
public void mensajes(String cadena1, String cadena2, String cadena3){
	//Envia mensajes a servidor secuencialmente	
		if (MSJ==3){
					    
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
			MSJ=2;
			String Datos = ">"+"GR"+">"+"99"+">";
			String DatosTransa = "";
			envia("M1",Datos,DatosTransa);
		}
		//Mensaje INICIAL-Quien solicita la infomacion
		if (MSJ==0){
			MSJ=1;
			String Datos = ">"+"GR"+">"+"99"+">";
			String DatosTransa = ">"+cadena1+">"+cadena2+">"+cadena3+">";
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
*/

public void conf(View view) {
	Intent i = new Intent(this, Configura_clv.class );
	startActivity(i);
	finish();
}

public void fin(View view){
	env_main();
	finish();
	//Intent i = new Intent(this, MainActivity.class );
	//startActivity(i);
}

public void oculta(View view){
	// Ocultar teclado virtual
	InputMethodManager imm =
	        (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
	imm.hideSoftInputFromWindow(editText1.getWindowToken(), 0);
}

public void msj(String msjcon){
	msjcon =">"+ msjcon+ ">2>3>4>5>6>7>"; 	    
	Intent i = new Intent(this, Msj.class );
    i.putExtra("msjcon", msjcon);
    //i.putExtra("index", index);
    startActivity(i);
}

public void msj_sig(String msjcon){

	Intent i = new Intent(this, Msj_geren.class );
    i.putExtra("msjcon", msjcon);
    //i.putExtra("index", index);
    startActivity(i);
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