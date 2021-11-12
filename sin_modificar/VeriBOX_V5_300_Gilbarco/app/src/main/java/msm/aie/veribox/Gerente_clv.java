package msm.aie.veribox;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class Gerente_clv extends Activity implements OnKeyListener{
	
	SQLiteDatabase bd; 
	private String clv;
	private EditText editText1;
	private TextView tv3;
	//Captura de XY
	private TextView textView_xy, TextView001;
	StringBuilder stringBuilder = new StringBuilder();
	private Coor_xy cox_coy;
	
@Override
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState); 
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.gerente_clv);

    this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    
    editText1 = (EditText) findViewById(R.id.editText1);
    tv3 = (TextView)findViewById(R.id.textView3);
    editText1.setOnKeyListener(this);
    
    Leedb();

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
 
    }

//Funcion Acciones
public void revisar (String rev_coor){
	//Boton a HOME
	if (rev_coor.equals("F2") || rev_coor.equals("F3") || rev_coor.equals("E2") || rev_coor.equals("E3")){
		fin(null);
  }
	//Cambia de Opcion a procesar
	if (rev_coor.equals("F6") || rev_coor.equals("F7") || rev_coor.equals("E6") || rev_coor.equals("E7")){
		enter(null);
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
					   enter(null);
				    break;
			   }
			   //Toast.makeText(this, "return 2",Toast.LENGTH_SHORT).show();
			   return true; 
		  }                
	  
	 }
	 //Toast.makeText(this, "return 3",Toast.LENGTH_SHORT).show();
	 return false; // pass on to other listeners. 
	
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
		clv=fila.getString(13);

	} else{
		Toast.makeText(this, "NO lee DB",
				Toast.LENGTH_SHORT).show();
	}
	//*****************************************************************************		
}

public void enter(View view){
	tv3.setText("");
	String compara = editText1.getText().toString();
	if (compara.equals(clv)){
		Intent i = new Intent(this, Gerente.class );
		startActivity(i);
		finish();
	}else {
		tv3.setText("Contraseña Incorrecta");
		editText1.setText("");
		/*new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}
				tv3.setText("");
			}
		}).start();
		*/
	}
}

public void fin(View view){
	env_main();
	finish();
	//Intent i = new Intent(this, MainActivity.class );
	//startActivity(i);
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