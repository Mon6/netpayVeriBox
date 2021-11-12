package msm.aie.veribox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by aie on 4/06/18.
 */

public class Flotilla_rein extends Activity {
    private TextView textView1;
    private Button button20, button21;

    private Coor_xy cox_coy;
    private TextView textView_xy;
    StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.flotilla_rein);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        textView1 = (TextView)findViewById(R.id.textView1);
        button20 = (Button)findViewById(R.id.button20);
        button21 = (Button)findViewById(R.id.button21);

        //recive datos del accion anterior
        Bundle bundle=getIntent().getExtras();
        String  msj = bundle.getString("msj");
        String ops1 = bundle.getString("ops1");
        String ops2 = bundle.getString("ops2");

        textView1.setText(msj);
        button20.setText(ops1);
        button21.setText(ops2);

        /*
        //Inicia captura de Coordenadas XY
        //Coor_xy
        cox_coy= new Coor_xy();
        this.textView_xy = (TextView) findViewById( R.id.strXY );
        //this.textView_xy.setText("X: ,Y: ");//texto inicial

        //Evento Touch
        this.textView_xy.setOnTouchListener( new View.OnTouchListener()
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


    /*
    //Funcion Acciones
    public void revisar (String rev_coor) throws IOException {
        switch (rev_coor) {
		case "D3":case "D4":case "C3":case "C4":
                Intent i = new Intent();
                setResult(RESULT_CANCELED, i);
                finish();
			break;
		case "D5":case "D6":case "C5":case "C6":
                Intent ii = new Intent();
                setResult(RESULT_OK, ii);
                finish();
			break;
		default:
		}
    }
    */

    public void ops1(View view){
        Intent i = new Intent();
        setResult(RESULT_CANCELED, i);
        finish();
    }

    public void ops2(View view){
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }

    //Deshabilitar BOTON atras
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        //setResult(RESULT_OK, intent);
        setResult(RESULT_CANCELED, intent);
        finish();
    }



}