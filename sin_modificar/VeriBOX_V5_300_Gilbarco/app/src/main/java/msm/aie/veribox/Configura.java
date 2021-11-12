package msm.aie.veribox;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Created by Ing. Miguel Santiago
 * Clase para mostrar y Guardar cambios en la configuracion.
 */

public class Configura extends Activity{

	private EditText et1, et2, et3, et4, et5, et6;
	private TextView tv1, tv2, tv3, tv4, tv7;
	private Spinner sp1, sp2, sp3, sp4, sp5, sp6, sp7, sp8, sp9, sp10, sp11, sp12, sp13, sp14, sp15, sp16, sp17,sp18,sp19, sp20, sp21, sp22;
	private Button button27;
	private boolean vista_boton27;
	private SQLiteDatabase bd;
	private String address, mac, mac_serial, nomad,servidor, clv, clv_ger;// version;
	int num, selec_inter, imp_ext;
	//Coordenadas
	StringBuilder stringBuilder = new StringBuilder();
	private Coor_xy cox_coy;
	private TextView textView_xy;
	int visTienda, visTickets, visOperador, visMepagoT, visMepagoF, visOrdenTP, estadopago, usoLector, visUsoCFDI, visTckCobro, visAcumulado, visFueraServ,
	visCamTurno, visInvTanques, visRecTurno;
	
@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.configura);
        
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        //String mac, servidor, num_tabled, clv, version;
        int num_tabled = 0, vista = 0, pos_ini = 0, pos_fin = 0, pos_car = 0, imp_let_max=0, mul_pos = 0;
        visTienda = visTickets= visOperador= visMepagoT= visMepagoF= visOrdenTP=0;
        
        et1 = (EditText) findViewById(R.id.editText1);//Muestra Servidor.
		et2 = (EditText) findViewById(R.id.editText2);//Muestra Terminal. 
		et3 = (EditText) findViewById(R.id.editText3);//Muestra Clv gerente
		et4 = (EditText) findViewById(R.id.editText4);//Muestra Clv Configuracion
		et5 = (EditText) findViewById(R.id.editText5);
		et6 = (EditText) findViewById(R.id.editText6);

		button27 = (Button)findViewById(R.id.button27);
		vista_boton27 = true;
		
		et1.requestFocus();
		
		tv3 = (TextView)findViewById(R.id.textView3);//Muestra MAC.
		tv7 = (TextView)findViewById(R.id.textView7);//Muestra Version
		

		AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
				"tablet", null, 1);
		bd = admin.getWritableDatabase();
		
		obtiene_mac();
		guarda_mac();
		obtieneIP();
		
		//*****************************************************************************		
		
		Cursor fila = bd.rawQuery("select * from config where num=1"
				+ "", null);
		if (fila.moveToFirst()) {
			num=fila.getInt(0);
			clv=fila.getString(1);
			num_tabled=fila.getInt(2);
			mac=fila.getString(3);
			//version=fila.getString(4);
			servidor=fila.getString(5);
			imp_ext=fila.getInt(6);
			mac_serial=fila.getString(7);
			nomad=fila.getString(8);
			vista=fila.getInt(10)-1;
			if(vista==3)
				vista-=1;
			pos_ini=fila.getInt(11)-1;
			pos_fin=fila.getInt(12)-1;
			clv_ger=fila.getString(13);

			visAcumulado = fila.getInt(15);

			pos_car = fila.getInt(20)-1;

			visTienda = fila.getInt(21);
			visTickets = fila.getInt(22);
			visOperador = fila.getInt(23);
			visMepagoT = fila.getInt(24);
			visMepagoF = fila.getInt(25);
			visOrdenTP = fila.getInt(26);
			estadopago = fila.getInt(28);
			usoLector = fila.getInt(29);
			visUsoCFDI = fila.getInt(31);
			visTckCobro = fila.getInt(32);
			visFueraServ = fila.getInt(33);	//extra4
			visCamTurno = fila.getInt(34);	//extra5
			visInvTanques = fila.getInt(35);	//extra6
			visRecTurno = fila.getInt(36);	//extra7
			imp_let_max = fila.getInt(37);	//extra8
            mul_pos = fila.getInt(38);	    //extra9
						
			tv3.setText(mac);
			et1.setText(servidor);
			//et2.setText(num_tabled);
			//tv7.setText(version);
			et3.setText(clv_ger);
			et4.setText(clv);
			//String imp_ext_str = String.valueOf(imp_ext);
			//et4.setText(imp_ext_str);
			et5.setText(mac_serial);
			et6.setText(nomad);
			
			//Toast.makeText(this, "LEE DB: "+ clv,
				//Toast.LENGTH_SHORT).show();	
		} else{
			Toast.makeText(this, "NO lee DB",
					Toast.LENGTH_SHORT).show();	
		}
				
		//*****************************************************************************
		
		//Seleccion que se leera de la DB que mostrara la eleccion almacenada de Tipo de Interfaz. 
		//sp.setSelection(selec_inter);		
//---------------------------------------------------------------------
		//Muestra Opciones de Numero de Terminal
		sp1 = (Spinner) findViewById(R.id.spinner1);
	    ArrayAdapter adapter1 = ArrayAdapter.createFromResource(
	            this, R.array.terminal, android.R.layout.simple_spinner_item);
	    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    sp1.setAdapter(adapter1);  
	    sp1.setSelection(num_tabled-1);
//---------------------------------------------------------------------	
	    //Muestra la salida de Impresora
	    sp2 = (Spinner) findViewById(R.id.spinner2);
	    adapter1 = ArrayAdapter.createFromResource(
	            this, R.array.impre, android.R.layout.simple_spinner_item);
	    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    sp2.setAdapter(adapter1);
	    sp2.setSelection(imp_ext);
//---------------------------------------------------------------------
	    //Uso del Lector para identificar Usuario
	    sp3 = (Spinner) findViewById(R.id.spinner3);
	    adapter1 = ArrayAdapter.createFromResource(
	            this, R.array.asocia_lec, android.R.layout.simple_spinner_item);
	    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    sp3.setAdapter(adapter1);
	    sp3.setSelection(usoLector);
//---------------------------------------------------------------------
	    //Muestra rango de posicion inicial
	    sp4 = (Spinner) findViewById(R.id.spinner4);
	    adapter1 = ArrayAdapter.createFromResource(
	            this, R.array.terminal, android.R.layout.simple_spinner_item);
	    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    sp4.setAdapter(adapter1);  
	    sp4.setSelection(pos_ini);
//---------------------------------------------------------------------
	  //Muestra rango de posicion final
	    sp5 = (Spinner) findViewById(R.id.spinner5);
	    sp5.setAdapter(adapter1);  
	    sp5.setSelection(pos_fin);
//---------------------------------------------------------------------
	  //Muestra Carpeta de Impresora
	    sp6 = (Spinner) findViewById(R.id.spinner6);
	    ArrayAdapter adapter6 = ArrayAdapter.createFromResource(
	            this, R.array.carpeta, android.R.layout.simple_spinner_item);
	    adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    sp6.setAdapter(adapter6);  
	    sp6.setSelection(pos_car);
	     
//---------------------------------------------------------------------
	  //Muestrar Tienda SI - NO.
	    sp7 = (Spinner) findViewById(R.id.spinner7);
	    ArrayAdapter adapter7 = ArrayAdapter.createFromResource(
	            this, R.array.ops_sn, android.R.layout.simple_spinner_item);
	    adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    sp7.setAdapter(adapter7);  
	    sp7.setSelection(visTienda);
//---------------------------------------------------------------------
	    //Muestrar Factura Tickets  SI - NO.
	    sp8 = (Spinner) findViewById(R.id.spinner8);
	    sp8.setAdapter(adapter7);  
	    sp8.setSelection(visTickets);
//---------------------------------------------------------------------
	    //Muestrar Uso de Operador  SI - NO.
	    sp9 = (Spinner) findViewById(R.id.spinner9);
	    sp9.setAdapter(adapter7);  
	    sp9.setSelection(visOperador);
//---------------------------------------------------------------------
	    //Muestrar Uso de Metodo de pago en Ticket  SI - NO.
	    sp10 = (Spinner) findViewById(R.id.spinner10);
	    sp10.setAdapter(adapter7);  
	    sp10.setSelection(visMepagoT);
//---------------------------------------------------------------------
	  //Muestrar Uso de Metodo de pago en Factura  SI - NO.
	    sp11 = (Spinner) findViewById(R.id.spinner11);
	    sp11.setAdapter(adapter7);  
	    sp11.setSelection(visMepagoF);
	//---------------------------------------------------------------------
	  //Muestrar Orden en patantalla principal
	    sp12 = (Spinner) findViewById(R.id.spinner12);
	    ArrayAdapter adapter12 = ArrayAdapter.createFromResource(
	            this, R.array.orden_prin, android.R.layout.simple_spinner_item);
	    adapter12.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    sp12.setAdapter(adapter12);  
	    sp12.setSelection(visOrdenTP);
//---------------------------------------------------------------------
		//Muestrar Estado de Cobro Bancario T = Pruebas, P = Produccion.
		sp13 = (Spinner) findViewById(R.id.spinner13);
		ArrayAdapter adapter13 = ArrayAdapter.createFromResource(
				this, R.array.pagoestado, android.R.layout.simple_spinner_item);
		adapter13.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp13.setAdapter(adapter13);
		sp13.setSelection(estadopago);

//---------------------------------------------------------------------
		//Muestrar Uso de FCDI 3.3 Factura  SI - NO.
		sp14 = (Spinner) findViewById(R.id.spinner14);
		//ArrayAdapter adapter11 = ArrayAdapter.createFromResource(
		//        this, R.array.ops_sn, android.R.layout.simple_spinner_item);
		//adapter10.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp14.setAdapter(adapter7);
		sp14.setSelection(visUsoCFDI);
//---------------------------------------------------------------------
		//Habilita INGRESAR TICKETS (COBRO)  SI - NO.
		sp15 = (Spinner) findViewById(R.id.spinner15);
		sp15.setAdapter(adapter7);
		sp15.setSelection(visTckCobro);
//---------------------------------------------------------------------
		//Habilitar ACUMULADO DE VENTA  SI - NO.
		sp16 = (Spinner) findViewById(R.id.spinner16);
		sp16.setAdapter(adapter7);
		sp16.setSelection(visAcumulado);
//---------------------------------------------------------------------
		//Habilitar vista FUERA DE SERVICIO  SI - NO.
		sp17 = (Spinner) findViewById(R.id.spinner17);
		sp17.setAdapter(adapter7);
		sp17.setSelection(visFueraServ);
//---------------------------------------------------------------------
		//Habilitar vista CAMBIO DE TURNO  SI - NO.
		sp18 = (Spinner) findViewById(R.id.spinner18);
		sp18.setAdapter(adapter7);
		sp18.setSelection(visCamTurno);
//---------------------------------------------------------------------
		//Habilitar vista INVENTARIO DE TANQUES  SI - NO.
		sp19 = (Spinner) findViewById(R.id.spinner19);
		sp19.setAdapter(adapter7);
		sp19.setSelection(visInvTanques);
//---------------------------------------------------------------------
		//Habilitar vista RECOLECTA DE TURNO  SI - NO.
		sp20 = (Spinner) findViewById(R.id.spinner20);
		sp20.setAdapter(adapter7);
		sp20.setSelection(visRecTurno);
//---------------------------------------------------------------------
		//Habilitar LETRA GRANDE  SI - NO.
		sp21 = (Spinner) findViewById(R.id.spinner21);
		sp21.setAdapter(adapter7);
		sp21.setSelection(imp_let_max);
//---------------------------------------------------------------------
		//Habilitar Uso de Multiples posiciones  SI - NO.
		sp22 = (Spinner) findViewById(R.id.spinner22);
		sp22.setAdapter(adapter7);
		sp22.setSelection(mul_pos);
//---------------------------------------------------------------------

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
	   //FIN Captura de Coordenadas XY
	*/

	//MSM Junio 2018
	//Muetra-Oculta
		sp3.setOnItemSelectedListener(
			new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> spn, android.view.View v, int posicion, long id) {
					//Toast.makeText(spn.getContext(), "Has seleccionado " + spn.getItemAtPosition(posicion).toString(), Toast.LENGTH_LONG).show();
					if (posicion == 1){
						button27.setVisibility(View.VISIBLE);
						vista_boton27 = true;
					}else {
						button27.setVisibility(View.INVISIBLE);
						vista_boton27 = false;
					}
				}
				public void onNothingSelected(AdapterView<?> spn) {
				}
			});


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
					case R.id.editText1:
						ok(null);
						break;
					case R.id.editText2:
						ok(null);
						break;
				}
				return true;
			}
		}
		return false; // pass on to other listeners.
	}
	*/

	//Funcion Acciones
    /*
	public void revisar (String rev_coor){
		//Boton a HOME
		if (rev_coor.equals("H1")){
			fin(null);
		}
		//Guarda los datos a DB
		if (rev_coor.equals("H8")){
			guardar(null);
		}
		//Usuarios
		if (rev_coor.equals("F7")|| rev_coor.equals("F8")){
			user();
			//Toast.makeText(this, "USER", Toast.LENGTH_SHORT).show();
		}
    }
    */

    //Funcion Acciones
    public void revisar (String rev_coor){
        switch (rev_coor) {
            //Boton a HOME
            case "H1":
                fin(null);
                break;
            //Guarda los datos a DB
            case "H8":
                guardar(null);
                break;
            case "F7": case"F8":
            	//Muestra a los usuarios
                user(null);
                break;
            case "D7": case"D8":
            	//Muestra la opcion de Asignacion Posiciones y Lectores
				if (vista_boton27)
                	pos_lec(null);
                break;
        }
    }

public void obtiene_mac() {
	
	try {
		address= "";
    	String getMacetho = getMacAddress();
    	if (getMacetho.length() > 0 ){
    		address = getMacetho;
    	}else{
			/*
    		try {
        		WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        		WifiInfo info = manager.getConnectionInfo();
        		address = info.getMacAddress();
        		address = address.toUpperCase();
        		Toast.makeText(this, "MAC INALAMBRICA" + address, Toast.LENGTH_SHORT).show();
			}catch (Exception e1) {
        		address = "Error MAC.";
        	}
        	*/
    	}
    }catch (Exception e) {
    	
    	address = "Error MAC2.";
    	/*
    	try {
			String macetho2 = loadFileAsString("/sys/class/net/eth0/address");
			if (macetho2.length() > 0 ){
	    		address = address + macetho2 + "=";
	    	}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			address = address + "Exception"+ "]";
			e1.printStackTrace();
		}
    	*/
    }
	
	}

	public void obtieneIP(){

        //VeriBox 1.8-0 Para uso en Android "N"
		//WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();
		String ipAddress = Formatter.formatIpAddress(ip);

		/*
		WifiManager manager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();
		int ip = info.getIpAddress();
		String sip;
		sip = String.valueOf(ip);
		*/

		if (ipAddress.equals("0.0.0.0") ){
			Toast.makeText(this, "IP: NO DISPONIBLE", Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(this, "IP:" + ipAddress, Toast.LENGTH_SHORT).show();
		}

	}

public static String loadFileAsString(String filePath) throws java.io.IOException{
    StringBuffer data = new StringBuffer(1000);
    BufferedReader reader = new BufferedReader(new FileReader(filePath));
    char[] buf = new char[1024];
    int numRead=0;
    while((numRead=reader.read(buf)) != -1){
        String readData = String.valueOf(buf, 0, numRead);
        data.append(readData);
    }
    reader.close();
    return data.toString();
}

public String getMacAddress(){
    try {
        return loadFileAsString("/sys/class/net/eth0/address").toUpperCase().substring(0, 17);
    } catch (IOException e) {
        e.printStackTrace();
        return null;
    }
}

public void guarda_mac() {
	ContentValues registro = new ContentValues();
	registro.put("mac", address);
		
	int cant = bd.update("config", registro, "num=1", null);
	/*
	if (cant == 1)
		Toast.makeText(this, "Actualizo MAC:" + address, Toast.LENGTH_SHORT)
				.show();
	else
		Toast.makeText(this, "NO",
				Toast.LENGTH_SHORT).show();
	*/

}



	public void msj(String muestra){
		muestra = ">"+muestra+">2>3>4>5>6>7>";
		Intent j = new Intent(this, Msj.class );
		j.putExtra("msjcon", muestra);
	    startActivity(j);
	}

public void guardar(View view) {
	//A0:1E:0B:		04:96:B8
	//123456789		01234567
	
	//192.168.0		00.054
	//123456789		012345
	servidor = et1.getText().toString();
	if (servidor.length()==15 && (servidor.substring(3,4)).equals(".") && (servidor.substring(7,8)).equals(".") && (servidor.substring(11,12)).equals(".") ){
		int correcto=1;
		clv_ger = et3.getText().toString();
		if (clv_ger.length() >5){
			clv = et4.getText().toString();
			if (clv.length() >5){
				servidor = et1.getText().toString();
				String num_tabled_s = sp1.getSelectedItem().toString();
				String carpeta = sp1.getSelectedItem().toString();

				int i=Integer.parseInt(carpeta.replaceAll("[\\D]", ""));
				carpeta = String.format("%02d", i);

				mac_serial = et5.getText().toString();

				String imp_ext_str = sp2.getSelectedItem().toString();
				imp_ext_str=imp_ext_str.substring(0,1);

				String dato_usoLector = sp3.getSelectedItem().toString();
				if (dato_usoLector.equals("- - - -")) dato_usoLector="0";
				else if (dato_usoLector.equals("POR POSICIÓN")) dato_usoLector="1";
				else dato_usoLector="2";

				String dato_visT = sp7.getSelectedItem().toString();
				if (dato_visT.equals("SI"))dato_visT="0";
				else dato_visT="1";
				
				String dato_visIT = sp8.getSelectedItem().toString();
				if (dato_visIT.equals("SI"))dato_visIT="0";
				else dato_visIT="1";
				
				String dato_visOC = sp9.getSelectedItem().toString();
				if (dato_visOC.equals("SI"))dato_visOC="0";
				else dato_visOC="1";
				
				String dato_visMPT = sp10.getSelectedItem().toString();
				if (dato_visMPT.equals("SI"))dato_visMPT="0";
				else dato_visMPT="1";
				
				String dato_visMPF = sp11.getSelectedItem().toString();
				if (dato_visMPF.equals("SI"))dato_visMPF="0";
				else dato_visMPF="1";
				
				String dato_visOP = sp12.getSelectedItem().toString();
				if (dato_visOP.equals("TICKETS"))dato_visOP="0";
				else dato_visOP="1";

				String dato_estapago = sp13.getSelectedItem().toString();
				if (dato_estapago.equals("PRUEBAS"))dato_estapago="0";
				else dato_estapago="1";

				String dato_usoCfdi = sp14.getSelectedItem().toString();
				if (dato_usoCfdi.equals("SI"))dato_usoCfdi="0";
				else dato_usoCfdi="1";

				String dato_TckCobro = sp15.getSelectedItem().toString();
				if (dato_TckCobro.equals("SI"))dato_TckCobro="0";
				else dato_TckCobro="1";

				String dato_Acumulado = sp16.getSelectedItem().toString();
				if (dato_Acumulado.equals("SI"))dato_Acumulado="0";
				else dato_Acumulado="1";

				String dato_visFueraServ = sp17.getSelectedItem().toString();
				if (dato_visFueraServ.equals("SI"))dato_visFueraServ="0";
				else dato_visFueraServ="1";

				String dato_visCambioTur = sp18.getSelectedItem().toString();
				if (dato_visCambioTur.equals("SI"))dato_visCambioTur="0";
				else dato_visCambioTur="1";

				String dato_visInvTanque = sp19.getSelectedItem().toString();
				if (dato_visInvTanque.equals("SI"))dato_visInvTanque="0";
				else dato_visInvTanque="1";

				String dato_visRecTurno = sp20.getSelectedItem().toString();
				if (dato_visRecTurno.equals("SI"))dato_visRecTurno="0";
				else dato_visRecTurno="1";

				//Obtiene el dato de Letra grande
				String dato_imp_let_max = sp21.getSelectedItem().toString();
				if (dato_imp_let_max.equals("SI"))dato_imp_let_max="0";
				else dato_imp_let_max="1";

				String dato_mul_pos = sp22.getSelectedItem().toString();
				if (dato_mul_pos.equals("SI"))dato_mul_pos="0";
				else dato_mul_pos="1";
						
				ContentValues registro = new ContentValues();
				registro.put("servidor", servidor);
				registro.put("num_tablet", num_tabled_s);
				registro.put("clv", clv);
				registro.put("imp_ext", imp_ext_str);
				//registro.put("vista", sp3.getSelectedItem().toString());
				registro.put("vista", "2");
				registro.put("rango_pos_ini", sp4.getSelectedItem().toString());
				registro.put("rango_pos_fin", sp5.getSelectedItem().toString());
				registro.put("carpeta", carpeta);
				registro.put("visTienda", dato_visT);
				
				registro.put("visTickets", dato_visIT);
				registro.put("visOperador", dato_visOC);
				registro.put("visMepagoT", dato_visMPT);
				registro.put("visMepagoF", dato_visMPF);
				registro.put("visOrdenTP", dato_visOP);
				registro.put("estadopago", dato_estapago);
				registro.put("extra2", dato_usoCfdi);
				registro.put("extra3", dato_TckCobro);
				registro.put("acu_venta", dato_Acumulado);
				registro.put("usoLector", dato_usoLector);

				registro.put("extra4", dato_visFueraServ);
				registro.put("extra5", dato_visCambioTur);
				registro.put("extra6", dato_visInvTanque);
				registro.put("extra7", dato_visRecTurno);
				registro.put("extra8", dato_imp_let_max);
				registro.put("extra9", dato_mul_pos);

				//registro.put("usoLector", usoLector);
				//registro.put("acumulado", acumulado);

				registro.put("clv_gerente", clv_ger);
				
				registro.put("extra1", "FFFFFFFF");

				if (dato_usoLector.equals("2") && (dato_visOC.equals("1"))){
					msj("Se requiere habilite:\n'INGRESO OPERADORES'\npara Asociar Lectores : POR USUARIOS");
					sp9.requestFocus();
					correcto=0;
				}
				
				if(imp_ext_str.equals("3")){
					if (mac_serial.length()==17 && (mac_serial.substring(2,3)).equals(":") && (mac_serial.substring(5,6)).equals(":") && (mac_serial.substring(8,9)).equals(":")&& (mac_serial.substring(11,12)).equals(":") && (mac_serial.substring(14,15)).equals(":")){
						registro.put("mac_serial", mac_serial);	
					}
					else{
						//Toast.makeText(this, "MAC Bluetooth: INVALIDA", Toast.LENGTH_SHORT).show();
						correcto=0;
					}
				}

				if (dato_visOC.equals("0")){
					Cursor fila = bd.rawQuery("SELECT COUNT(*) FROM users", null);
					if(fila.moveToFirst()){
						if (fila.getInt(0)>0){
							correcto=1;
						}else{
							correcto=0;
							msj("Operadores:\nNo Registrados");
						}
					}else{
						correcto=0;
						msj("Operadores:\nNo Registrados");
					}

				}
				
				if(correcto==1){
					//----------------------------------------------------
					int cant = bd.update("config", registro, "num=1", null);
					//msj("GUARDO");
					bd.close();
					/*
					if (cant == 1)
						Toast.makeText(this, "Modificacion Terminada", Toast.LENGTH_SHORT)
								.show();
					else
						Toast.makeText(this, "NO Graba en DB",
								Toast.LENGTH_SHORT).show();
					*/
					//----------------------------------------------------		
					//et1.setText("");
					env_epos(num_tabled_s, imp_ext_str, mac_serial, carpeta, dato_imp_let_max);
					env_pb(dato_estapago);
				    //Intent i = new Intent(this, MainActivity.class );
				    //i.putExtra("inicia", "0");
				    //startActivity(i);
					finish();
				}
			}else{
				msj("Contraseña Servicio:\nUsar 6 Digitos");
			}
		}else{
			msj("Contraseña Gerente:\nUsar 6 Digitos");
		}
	}else{
		msj("IP Servidor: INVALIDA");
	}
	
	
		
	}

	public void env_epos(String num_tablet, String imp_ext_str, String imp_ext_mac, String carpeta, String imp_let_max){
		File path = new File(Environment.getExternalStorageDirectory(), "Tickets");
	    path.mkdirs();

	    //Una vez creado disponemos de un archivo para guardar datos
	    try
	    {
	        File ruta_sd = Environment.getExternalStorageDirectory();

	        File f = new File(ruta_sd.getAbsolutePath(), "Tickets/conpos.txt");

	        OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(f));	        
	        String conf_epos = address +"\n";
	        conf_epos += servidor+"\n";
	        conf_epos += carpeta+"\n";
	        conf_epos += num_tablet+"\n";
	        conf_epos += imp_ext_str+"\n";
	        conf_epos += imp_ext_mac+"\n";
			conf_epos += imp_let_max+"\n";
	        //conf_epos += "0Mac 1Servidor 2Carpeta 3NumVeri 4Imp_ext 5Mac_blue";
            fout.write(conf_epos);
	        fout.close();
	    }
	    catch (Exception ex)
	    {
	        Log.e("Ficheros", "Error al escribir fichero a tarjeta SD");
	    }
	}

	public void env_pb(String dato_estapago){
		File path = new File(Environment.getExternalStorageDirectory(), "Tickets");
		path.mkdirs();

		//Una vez creado disponemos de un archivo para guardar datos
		try
		{
			File ruta_sd = Environment.getExternalStorageDirectory();

			File f = new File(ruta_sd.getAbsolutePath(), "Tickets/PBCONF.txt");

			OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(f));
			fout.write(dato_estapago);
			fout.close();

			File f2 = new File(ruta_sd.getAbsolutePath(), "Tickets/VBCONF.txt");

			OutputStreamWriter fout2 = new OutputStreamWriter(new FileOutputStream(f2));
			fout2.write("C");
			fout2.close();
		}
		catch (Exception ex)
		{
			Log.e("Ficheros", "Error al escribir fichero a tarjeta SD");
		}
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
	imm.hideSoftInputFromWindow(et1.getWindowToken(), 0);
}

public void apagar(View view){
	try {
		//System.exit(0);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			stopLockTask();
		}
		msj("ACCESO ANDROID:\nDISPONIBLE");
	}catch (Exception ex){

	}
}



/*
void shutdown_sys()
{
    Process chperm;
    try {
        chperm=Runtime.getRuntime().exec("su");
          DataOutputStream os = 
              new DataOutputStream(chperm.getOutputStream());

              os.writeBytes("shutdown\n");
              os.flush();

              chperm.waitFor();

    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
}
*/

/*
public void epos(View view){
	// Creamos una carpeta "Tickets" dentro del directorio "/"
    // Con el metodo "mkdirs()" creamos el directorio si es necesario
	inicia_ePOS("1001", false);
    new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
            }
            inicia_ePOS("1010", true);;
        }
    }).start();
	
}
*/

/*
public void inicia_ePOS(String grb, boolean inicia){
	File path = new File(Environment.getExternalStorageDirectory(), "Tickets");
    path.mkdirs();

    //Una vez creado disponemos de un archivo para guardar datos
    try
    {
        File ruta_sd = Environment.getExternalStorageDirectory();

        File f = new File(ruta_sd.getAbsolutePath(), "Tickets/ePOS_AIE.txt");

        OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(f));
        fout.write(grb);
        fout.close();
        //Toast.makeText(this, "Texto de prueba.3", Toast.LENGTH_SHORT).show();
    }
    catch (Exception ex)
    {
        Log.e("Ficheros", "Error al escribir fichero a tarjeta SD");
    }
    if (inicia){
    	Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.epson.epos2_printer");
    	startActivity(launchIntent);
        finish();    	
    }	
}
*/


    public void user(View view){
        Intent i = new Intent(this, User_edit.class );
        startActivity(i);
    }

    public void pos_lec(View view){
        Intent i = new Intent(this, Configura_pos_lector.class );
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

