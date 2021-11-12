package msm.aie.veribox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

public class Fac_rapida1_msj extends Activity{
	
	private Button button1, button2;
	private TextView textView1;
	//Captura de XY
	private TextView textView_xy, TextView001;
	StringBuilder stringBuilder = new StringBuilder();
	private Coor_xy cox_coy;
	
@Override
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState); 
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.fac_rapida1_msj);
    
     
    textView1 = (TextView)findViewById(R.id.textView1);
    button1 = (Button)findViewById(R.id.button1);
    button2 = (Button)findViewById(R.id.button2);
    
    /*
    i.putExtra("texto","Cliente sin CORREO:" );
    i.putExtra("opc1", "REGRESAR");
    i.putExtra("opc2", "CONTINUAR");
    */
    Bundle bundle=getIntent().getExtras();
    String texto = bundle.getString("texto");
    String opcX = bundle.getString("opcX");
    String opcY = bundle.getString("opcY");
    
    textView1.setText(texto);
    button1.setText(opcX);
    button2.setText(opcY);
    
    /*
    //Inicia captura de Coordenadas XY	
    //Opcional Incializar 2do touch
    //boolean sig_estado = false;
    cox_coy= new Coor_xy();
    this.textView_xy = (TextView) findViewById( R.id.strXY );
    //Evento Touch
    this.textView_xy.setOnTouchListener( new OnTouchListener(){
	   	@Override
	   	public boolean onTouch( View arg0, MotionEvent arg1 ) {
	   		stringBuilder.setLength(0);
	   		//Si la accion que se recibe es de soltar
	   		if( arg1.getAction() == MotionEvent.ACTION_UP )
	   		{
	   			float co_X = arg1.getX();
	   			float co_Y = arg1.getY();
	   			String rev=cox_coy.co_xy(co_X, co_Y);
	   			touch_ms(rev);
	   		}								
	   		return true;
	   	}			
    });
    //FIN Captura de Coordenadas XY
	*/
 
    }

	//Funcion Acciones
	//public void revisar (String rev_coor) throws IOException{
	public void touch_ms (String rev_coor){
		//Cambia de Opcion a procesar
		if (rev_coor.equals("D2") || rev_coor.equals("D3") || rev_coor.equals("D4")){
			cancelado(null);
	    }
		//Boton Editar
		if (rev_coor.equals("D5") || rev_coor.equals("D6") || rev_coor.equals("D7")){
			continua(null);
	    }
	}
	
	public void cancelado(View view){
		Intent intent = new Intent();
	    //intent.putExtra("user", usr);
	    //intent.putExtra("pass", "");
	    setResult(RESULT_CANCELED, intent);
	    finish();
	}
	
	public void continua(View view){
		Intent intent = new Intent();
	    //intent.putExtra("user", usr);
	    //intent.putExtra("pass", "");
	    setResult(RESULT_OK, intent);
	    finish();
	}
	



}
