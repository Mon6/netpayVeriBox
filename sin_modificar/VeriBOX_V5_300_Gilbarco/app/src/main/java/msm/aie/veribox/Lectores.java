package msm.aie.veribox;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Miguel Santiago on 4/10/17.
 * Version 1.8-0
 * Clase que se encarga de enviar a que Lector de Codigo de Barras
 * se conectara, existiendo mas de 2, sin sera asignados a usuarios.
 */

//private String [] lectores;


public class Lectores extends Activity {

    //Lector que se envia
    String conLec;
    String [] lectores;
    //Radios de Seleccion
    RadioButton radio1, radio2, radio3, radio4, radio5;
    //Tipo letra
    Typeface normal, marcado;
    //Uso de Coordenadas del Touch
    private Coor_xy cox_coy;
    private TextView textView_xy;
    StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.lectores);



        Bundle bundle = getIntent().getExtras();
        //lectores = bundle.getString("xmp_pago1");
        lectores = bundle.getStringArray("lectores");

        //Tipos letra
        normal = Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");
        marcado = Typeface.createFromAsset(getAssets(), "fonts/arialbold.ttf");

        radio1 = (RadioButton) findViewById(R.id.metodo1);
        radio1.requestFocus();
        radio2 = (RadioButton) findViewById(R.id.metodo2);
        radio3 = (RadioButton) findViewById(R.id.metodo3);
        radio4 = (RadioButton) findViewById(R.id.metodo4);
        radio5 = (RadioButton) findViewById(R.id.metodo5);

        //Se les asigna el tipo de letra por defecto
        radio1.setTypeface(marcado);
        radio2.setTypeface(normal);
        radio3.setTypeface(normal);
        radio4.setTypeface(normal);
        radio5.setTypeface(normal);


        //Marca el primer Lector como predeterminado.
        radio1.setChecked(true);
        //Se asigna por defecto el primer lector
        conLec = lectores[0];

        //Se asigna lectores que por lo menos debe contener.
        radio1.setText(lectores[0]);
        radio2.setText(lectores[1]);


        //Si hay por lo menos 3 Dispositivos
        if(lectores[2] != null){
            radio3.setText(lectores[2]);
        }else{
            radio3.setVisibility(View.INVISIBLE);
        }

        //Si hay por lo menos 4 Dispositivos
        if(lectores[3] != null){
            radio4.setText(lectores[3]);
        }else{
            radio4.setVisibility(View.INVISIBLE);
        }

        //Si hay por lo menos 5 Dispositivos
        if(lectores[4] != null){
            radio5.setText(lectores[4]);
        }else{
            radio5.setVisibility(View.INVISIBLE);
        }

        /*
        //Inicia captura de Coordenadas XY
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

    /*
    //Funcion Acciones
    public void revisar (String rev_coor){

        switch (rev_coor) {
            case "G3": case"G4": case"G5": case"G6":
                lec1();
                break;
            case "F3": case"F4": case"F5": case"F6":
                lec2();
                break;
            case "E3": case"E4": case"E5": case"E6":
                if (lectores[2] != null)
                    lec3();
                break;
            case "D3": case"D4": case"D5": case"D6":
                if (lectores[3] != null)
                lec4();
                break;
            case "C3": case"C4": case"C5": case"C6":
                if (lectores[4] != null)
                    lec5();
                break;
            case "A2": case"A3": case"B2": case"B3":
                atras(null);
                break;
            case "A5": case"A6":case "B5": case"B6":
                envia(null);
                break;
        }
    }
    */

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.metodo1:
                if (checked && lectores[0] != null){
                    lec1();
                }
                break;
            case R.id.metodo2:
                if (checked && lectores[1] != null){
                    lec2();
                }

                break;
            case R.id.metodo3:
                if (checked && (lectores[2] != null)){
                    lec3();
                }

                break;
            case R.id.metodo4:
                if (checked && lectores[3] != null){
                    lec4();
                }
                break;
            case R.id.metodo5:
                if (checked && lectores[4] != null){
                    lec5();
                }
                break;
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
                        /*
                        if (sig_estado==0){
                            ok(null);
                        }
                        * /
                        Toast.makeText(this, "ENTER EN 1",Toast.LENGTH_SHORT).show();
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


    private void lec1(){
        //Mustra la selecion activa
        radio1.setChecked(true);
        radio2.setChecked(false);
        radio3.setChecked(false);
        radio4.setChecked(false);
        radio5.setChecked(false);
        //Cambia el Tipo de Letra
        radio1.setTypeface(marcado);
        radio2.setTypeface(normal);
        radio3.setTypeface(normal);
        radio4.setTypeface(normal);
        radio5.setTypeface(normal);

        conLec = lectores[0];
    }

    private void lec2(){
        //Mustra la selecion activa
        radio1.setChecked(false);
        radio2.setChecked(true);
        radio3.setChecked(false);
        radio4.setChecked(false);
        radio5.setChecked(false);
        //Cambia el Tipo de Letra
        radio1.setTypeface(normal);
        radio2.setTypeface(marcado);
        radio3.setTypeface(normal);
        radio4.setTypeface(normal);
        radio5.setTypeface(normal);

        conLec = lectores[1];
    }

    private void lec3(){
        //Mustra la selecion activa
        radio1.setChecked(false);
        radio2.setChecked(false);
        radio3.setChecked(true);
        radio4.setChecked(false);
        radio5.setChecked(false);
        //Cambia el Tipo de Letra
        radio1.setTypeface(normal);
        radio2.setTypeface(normal);
        radio3.setTypeface(marcado);
        radio4.setTypeface(normal);
        radio5.setTypeface(normal);

        conLec = lectores[2];
    }

    private void lec4(){
        //Mustra la selecion activa
        radio1.setChecked(false);
        radio2.setChecked(false);
        radio3.setChecked(false);
        radio4.setChecked(true);
        radio5.setChecked(false);
        //Cambia el Tipo de Letra
        radio1.setTypeface(normal);
        radio2.setTypeface(normal);
        radio3.setTypeface(normal);
        radio4.setTypeface(marcado);
        radio5.setTypeface(normal);

        conLec = lectores[3];
    }

    private void lec5(){
        //Mustra la selecion activa
        radio1.setChecked(false);
        radio2.setChecked(false);
        radio3.setChecked(false);
        radio4.setChecked(false);
        radio5.setChecked(true);
        //Cambia el Tipo de Letra
        radio1.setTypeface(normal);
        radio2.setTypeface(normal);
        radio3.setTypeface(normal);
        radio4.setTypeface(normal);
        radio5.setTypeface(marcado);

        conLec = lectores[4];
    }

    public void envialec(View view){
        Intent intent = new Intent();
        intent.putExtra("lecBusca", conLec);
        setResult(RESULT_OK, intent);

        finish();
    }

    public void atras(View view){
        Intent intent = new Intent();
        intent.putExtra("lecBusca", "");
        setResult(RESULT_CANCELED, intent);

        finish();

    }



}

