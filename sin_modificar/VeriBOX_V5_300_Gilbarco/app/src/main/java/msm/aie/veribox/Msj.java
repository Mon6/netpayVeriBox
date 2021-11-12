package msm.aie.veribox;

import java.text.NumberFormat;
import java.text.ParseException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class Msj extends Activity{
	
	private TextView tv01, tv02, tv03, tv04, tv05;
	Typeface sutil, normal, marcado;
	
@Override
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState); 
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.msj);
    
    //DATOS PARA LLAMAR A MENSAJES EMERGENTES
    /*
    msj("MENSAJE A MOSTRAR");
    //
    public void msj(String muestra){
		muestra = ">"+muestra+">2>3>4>5>6>7>";
		Intent j = new Intent(this, msj.class );
		j.putExtra("msjcon", muestra);
	    startActivity(j);
	}
    */
    
    //Tipo de Fuentes
    sutil = Typeface.createFromAsset(getAssets(), "fonts/sutil.ttf");
	normal = Typeface.createFromAsset(getAssets(), "fonts/normal.ttf");
	marcado = Typeface.createFromAsset(getAssets(), "fonts/marcado.ttf");
	//Asignacion de TextView
	tv01 = (TextView)findViewById(R.id.textView1);
	tv02 = (TextView)findViewById(R.id.strXYmsj);
	tv03 = (TextView)findViewById(R.id.textView3);
	tv04 = (TextView)findViewById(R.id.textView4);
	tv05 = (TextView)findViewById(R.id.textView5);
	
	//Asigna Fuentes
	//tv01.setTypeface(normal);
	//tv02.setTypeface(sutil);
	//tv03.setTypeface(marcado);
	//tv04.setTypeface(marcado);
	//tv05.setTypeface(marcado);
	
	//Asigna Tamaños de Texto
	tv01.setTextSize(60);

	/*
	tv02.setTextSize(40);
	tv03.setTextSize(40);
	tv04.setTextSize(40);
	tv05.setTextSize(40);
	*/

	//Cambio de fondo en los Textos
	tv01.setBackgroundColor(Color.TRANSPARENT);
	/*
	tv02.setBackgroundColor(Color.TRANSPARENT);
	tv03.setBackgroundColor(Color.TRANSPARENT);
	tv04.setBackgroundColor(Color.TRANSPARENT);
	tv05.setBackgroundColor(Color.TRANSPARENT);
	*/

	tv01.setTextColor(Color.YELLOW);
	/*
	tv02.setTextColor(Color.YELLOW);
	tv03.setTextColor(Color.YELLOW);
	tv04.setTextColor(Color.YELLOW);
	tv05.setTextColor(Color.YELLOW);
	*/

	//Oculta los TextView que no se usan
	tv02.setVisibility(View.INVISIBLE);
	tv03.setVisibility(View.INVISIBLE);
	tv04.setVisibility(View.INVISIBLE);
	tv05.setVisibility(View.INVISIBLE);
	
	
	//recive datos del accion anterior
    Bundle bundle = getIntent().getExtras();
    String msjcon = bundle.getString("msjcon");        
    int index = bundle.getInt("index");


	//msj(">Comunicacion con Servidor Correcta>0>3>1>5>6>7>");
    //Dividir una cadena en partes por |
    String[] partes = msjcon.split("\\>");
    String texto = partes[1];
    String tiempo_s = partes[2];
    String tamano = partes[3];
    String pos = partes[4];
    String tipo = partes[5];
    String estilo = partes[6];
    String fuente = partes[7];
    

    tv01.setText(texto);
	/*
    tv02.setText(texto);
    tv03.setText(texto);
    tv04.setText(texto);
    tv05.setText(texto);
    */

	//Revisa si usara tamaño menor
	if (tamano.equals("1")){
		tv01.setTextSize(40);
	}

    timepo(tiempo_s, index);
    

 }

public void timepo(String timepo_s, final int index){
    int tiempo_num = 0;
	try {
		tiempo_num = NumberFormat.getInstance().parse(timepo_s).intValue();
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	if (tiempo_num>0){
		tiempo_num = tiempo_num * 1000;
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
           // acciones que se ejecutan tras los milisegundos
				if (index==1){
					finmain();
				}else{
					finish();
				}
			}
		}, tiempo_num);
	}
}

public void finmain(){
	//Intent i = new Intent(this, MainActivity.class );
    //startActivity(i);
	finish();
}

public void l1(View view){
	//tv01 = (TextView)findViewById(R.id.TextView01);
	tv01.setTypeface(marcado);
}

public void l2(View view){
	//tv01 = (TextView)findViewById(R.id.TextView01);
	tv01.setTypeface(normal);
}

public void letra_mas(View view){
	float size = tv01.getTextSize() + 1;
	tv01.setTextSize(size);
}

public void letra_menos(View view){
	float size = tv01.getTextSize();
	size= 10;
	tv01.setTextSize(size);
}



public void m1(View view){
	Toast toast = Toast.makeText(this, "MENSAJE CENTRADO QUE DESAPARECE", Toast.LENGTH_SHORT);
    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
    toast.show();  
}

public void m2(View view){
	AlertDialog.Builder alerta2 = new AlertDialog.Builder(this);
	alerta2.setMessage("SOLOS NOTIFICA UN MENSAJE")
	        .setTitle("¡Atención!")
	        .setCancelable(false)
	        .setNeutralButton("ENTERADO",
	                new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int id) {
	                        dialog.cancel();
	                    }
	                });
	AlertDialog alert = alerta2.create();
	alert.show();
}

public void m3(View view){
	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	builder.setMessage("¿MENSAJE de opciones para decidir?")
	        .setTitle("¡Atencion!")
	        .setCancelable(false)
	        .setNegativeButton("Cancelar",
	                new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int id) {
	                        dialog.cancel();
	                    }
	                })
	        .setPositiveButton("Confirmar",
	                new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int id) {
	                        //TransferirDinero(); // metodo que se debe implementar
	                    }
	                });
	AlertDialog alert = builder.create();
	alert.show();  
}

public void m4(View view){
	final CharSequence[] items = {"Opcion 1", "Opcion 2", "Opcion 3", "Opcion 4"};
	 
	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	builder.setTitle("Opciones escojer");
	builder.setItems(items, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int item) {
	        Toast toast = Toast.makeText(getApplicationContext(), "Haz elegido la opcion: " + items[item] , Toast.LENGTH_SHORT);
	        toast.show();
	        dialog.cancel();
	    }
	});
	AlertDialog alert = builder.create();
	alert.show();
}

}