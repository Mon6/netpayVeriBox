package msm.aie.veribox;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;



public class Msj_esp extends Activity{
	
	private TextView tv01, tv02, tv03, tv04, tv05;
	Typeface sutil, normal, marcado;
	private int msj_esp;
	SQLiteDatabase bd;
	//int conta=0;
	
	//Creamos el handler puente para mostrar
	private Handler puente = new Handler() {
	 @Override
	 public void handleMessage(Message msg) {
		 if ((String)msg.obj == ""){
			 finish(); 
		 }			 
		 else {	
			 //conta++;
			 //String cadena = String.valueOf(conta); 
			 //tv01.setText(cadena);
			 Cursor fila = bd.rawQuery("select * from config where num=1"
						+ "", null);
			 if (fila.moveToFirst()) {
				 msj_esp=fila.getInt(16);
				 //msj_esp=0;
					if (msj_esp==1){
						espera();
					}else
						finish();
				}
		 }
	 }
	};	
	
@Override
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState); 
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.msj);
    
    //DATOS PARA LLAMAR A MENSAJES EMERGENTES
    /*
    msj(">Conectando>2>3>4>5>6>7>");
    //
    public void msj(String muestra){
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
	tv02.setTextSize(60);
	tv03.setTextSize(60);
	tv04.setTextSize(60);
	tv05.setTextSize(60);
	
	//Cambio de fondo en los Textos
	tv01.setBackgroundColor(Color.TRANSPARENT);
	tv02.setBackgroundColor(Color.TRANSPARENT);
	tv03.setBackgroundColor(Color.TRANSPARENT);
	tv04.setBackgroundColor(Color.TRANSPARENT);
	tv05.setBackgroundColor(Color.TRANSPARENT);
	
	tv01.setTextColor(Color.YELLOW);
	tv02.setTextColor(Color.YELLOW);
	tv03.setTextColor(Color.YELLOW);
	tv04.setTextColor(Color.YELLOW);
	tv05.setTextColor(Color.YELLOW);
	
	//Oculta los TextView que no se usan
	tv02.setVisibility(View.INVISIBLE);
	tv03.setVisibility(View.INVISIBLE);
	tv04.setVisibility(View.INVISIBLE);
	tv05.setVisibility(View.INVISIBLE);
	
	
	//recive datos del accion anterior
    Bundle bundle = getIntent().getExtras();
    String msjcon = bundle.getString("msjcon");        
    int index = bundle.getInt("index");
    
    //Dividir una cadena en partes por |
    String[] partes = msjcon.split("\\>");
    String texto = partes[1];
    String tiempo_s = partes[2];
    String pos = partes[3];
    String tamano = partes[4];
    String tipo = partes[5];
    String estilo = partes[6];
    String fuente = partes[7];
    
    
    tv01.setText(texto);
    tv02.setText(texto);
    tv03.setText(texto);
    tv04.setText(texto);
    tv05.setText(texto);
    
    AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
			"tablet", null, 1);
	bd = admin.getWritableDatabase();
	
    //msj_esp=1;
    espera();
    //revisa();
 
    }

public void espera()
{
	final int t_busca=1000;//tiempo=t_busca*1000;
	//espera tiempo o si no lanza abortar
	new Thread(new Runnable() {
		@Override
		public void run() {
			try {
				Thread.sleep(t_busca);
			} catch (InterruptedException e) {
			}
			
			Message sms = new Message();
		    sms.obj = "revisa";
		    puente.sendMessage(sms);
		}
	}).start();
}

public void revisa(){
	finish();
}

}
