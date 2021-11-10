package msm.aie.veribox;

/**
 * Created by Miguel Santiago on 25/10/17.
 * Clase que se encarga de Guardar en la Base de Datos los Lectores
 * asociados a la Posicion
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class Configura_pos_lector extends Activity implements View.OnKeyListener {

    private LinearLayout visP1, visP2, visP3, visP4, visP5, visP6, visP7, visP8, visP9, visP10, visP11, visP12, visP13, visP14, visP15, visP16, visP17, visP18, visP19
            ,visP20, visP21, visP22, visP23, visP24, visP25, visP26, visP27, visP28, visP29, visP30, visP31, visP32;
    private TextView textView001, textView002, textView003, textView004, textView005, textView006, textView007, textView008, textView009, textView010, textView011,
            textView012, textView013, textView014, textView015, textView016, textView017, textView018, textView019, textView020, textView021, textView022, textView023
            ,textView024, textView025, textView026, textView027, textView028, textView029, textView030, textView031, textView032;
    private Button button001, button002, button003, button004, button005, button006, button007, button008, button009, button010, button011, button012, button013, button014
            ,button015, button016, button017, button018, button019, button020, button021, button022, button023, button024, button025, button026, button027, button028
            ,button029, button030, button031, button032;
    //Coordenadas
    StringBuilder stringBuilder = new StringBuilder();
    private Coor_xy cox_coy;
    private TextView textView_xy;

    private SQLiteDatabase bd;

    private int posActualiza;

    private String [][] lec_pos;
    private String Lector1, Lector2, Lector3, Lector4, Lector5, PosLec01, PosLec02, PosLec03, PosLec04, PosLec05;


    String asigna;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.configura_pos_lector);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Inicia arreglo para lectores y Posiciones
        lec_pos = new String [5][2];
        //Inicia valor para lectores ya que se Actualiza toda la Tabla de Lectores
        Lector1 = Lector2 = Lector3 = Lector4 = Lector5 = PosLec01 = PosLec02 = PosLec03 = PosLec04 = PosLec05 = "";

        //Asigna layout a codigos de uso
        visP1 = (LinearLayout) findViewById(R.id.visP1);
        visP2 = (LinearLayout) findViewById(R.id.visP2);
        visP3 = (LinearLayout) findViewById(R.id.visP3);
        visP4 = (LinearLayout) findViewById(R.id.visP4);
        visP5 = (LinearLayout) findViewById(R.id.visP5);
        visP6 = (LinearLayout) findViewById(R.id.visP6);
        visP7 = (LinearLayout) findViewById(R.id.visP7);
        visP8 = (LinearLayout) findViewById(R.id.visP8);
        visP9 = (LinearLayout) findViewById(R.id.visP9);
        visP10 = (LinearLayout) findViewById(R.id.visP10);
        visP11 = (LinearLayout) findViewById(R.id.visP11);
        visP12 = (LinearLayout) findViewById(R.id.visP12);
        visP13 = (LinearLayout) findViewById(R.id.visP13);
        visP14 = (LinearLayout) findViewById(R.id.visP14);
        visP15 = (LinearLayout) findViewById(R.id.visP15);
        visP16 = (LinearLayout) findViewById(R.id.visP16);
        visP17 = (LinearLayout) findViewById(R.id.visP17);
        visP18 = (LinearLayout) findViewById(R.id.visP18);
        visP19 = (LinearLayout) findViewById(R.id.visP19);
        visP20 = (LinearLayout) findViewById(R.id.visP20);
        visP21 = (LinearLayout) findViewById(R.id.visP21);
        visP22 = (LinearLayout) findViewById(R.id.visP22);
        visP23 = (LinearLayout) findViewById(R.id.visP23);
        visP24 = (LinearLayout) findViewById(R.id.visP24);
        visP25 = (LinearLayout) findViewById(R.id.visP25);
        visP26 = (LinearLayout) findViewById(R.id.visP26);
        visP27 = (LinearLayout) findViewById(R.id.visP27);
        visP28 = (LinearLayout) findViewById(R.id.visP28);
        visP29 = (LinearLayout) findViewById(R.id.visP29);
        visP30 = (LinearLayout) findViewById(R.id.visP30);
        visP31 = (LinearLayout) findViewById(R.id.visP31);
        visP32 = (LinearLayout) findViewById(R.id.visP32);

        textView001 = (TextView)findViewById(R.id.textView001);
        textView002 = (TextView)findViewById(R.id.textView002);
        textView003 = (TextView)findViewById(R.id.textView003);
        textView004 = (TextView)findViewById(R.id.textView004);
        textView005 = (TextView)findViewById(R.id.textView005);
        textView006 = (TextView)findViewById(R.id.textView006);
        textView007 = (TextView)findViewById(R.id.textView007);
        textView008 = (TextView)findViewById(R.id.textView008);
        textView009 = (TextView)findViewById(R.id.textView009);
        textView010 = (TextView)findViewById(R.id.textView010);
        textView011 = (TextView)findViewById(R.id.textView011);
        textView012 = (TextView)findViewById(R.id.textView012);
        textView013 = (TextView)findViewById(R.id.textView013);
        textView014 = (TextView)findViewById(R.id.textView014);
        textView015 = (TextView)findViewById(R.id.textView015);
        textView016 = (TextView)findViewById(R.id.textView016);
        textView017 = (TextView)findViewById(R.id.textView017);
        textView018 = (TextView)findViewById(R.id.textView018);
        textView019 = (TextView)findViewById(R.id.textView019);
        textView020 = (TextView)findViewById(R.id.textView020);
        textView021 = (TextView)findViewById(R.id.textView021);
        textView022 = (TextView)findViewById(R.id.textView022);
        textView023 = (TextView)findViewById(R.id.textView023);
        textView024 = (TextView)findViewById(R.id.textView024);
        textView025 = (TextView)findViewById(R.id.textView025);
        textView026 = (TextView)findViewById(R.id.textView026);
        textView027 = (TextView)findViewById(R.id.textView027);
        textView028 = (TextView)findViewById(R.id.textView028);
        textView029 = (TextView)findViewById(R.id.textView029);
        textView030 = (TextView)findViewById(R.id.textView030);
        textView031 = (TextView)findViewById(R.id.textView031);
        textView032 = (TextView)findViewById(R.id.textView032);
        button001 = (Button)findViewById(R.id.button001);
        button002 = (Button)findViewById(R.id.button002);
        button003 = (Button)findViewById(R.id.button003);
        button004 = (Button)findViewById(R.id.button004);
        button005 = (Button)findViewById(R.id.button005);
        button006 = (Button)findViewById(R.id.button006);
        button007 = (Button)findViewById(R.id.button007);
        button008 = (Button)findViewById(R.id.button008);
        button009 = (Button)findViewById(R.id.button009);
        button010 = (Button)findViewById(R.id.button010);
        button011 = (Button)findViewById(R.id.button011);
        button012 = (Button)findViewById(R.id.button012);
        button013 = (Button)findViewById(R.id.button013);
        button014 = (Button)findViewById(R.id.button014);
        button015 = (Button)findViewById(R.id.button015);
        button016 = (Button)findViewById(R.id.button016);
        button017 = (Button)findViewById(R.id.button017);
        button018 = (Button)findViewById(R.id.button018);
        button019 = (Button)findViewById(R.id.button019);
        button020 = (Button)findViewById(R.id.button020);
        button021 = (Button)findViewById(R.id.button021);
        button022 = (Button)findViewById(R.id.button022);
        button023 = (Button)findViewById(R.id.button023);
        button024 = (Button)findViewById(R.id.button024);
        button025 = (Button)findViewById(R.id.button025);
        button026 = (Button)findViewById(R.id.button026);
        button027 = (Button)findViewById(R.id.button027);
        button028 = (Button)findViewById(R.id.button028);
        button029 = (Button)findViewById(R.id.button029);
        button030 = (Button)findViewById(R.id.button030);
        button031 = (Button)findViewById(R.id.button031);
        button032 = (Button)findViewById(R.id.button032);
        button001.setOnKeyListener(this);
        button002.setOnKeyListener(this);
        button003.setOnKeyListener(this);
        button004.setOnKeyListener(this);
        button005.setOnKeyListener(this);
        button006.setOnKeyListener(this);
        button007.setOnKeyListener(this);
        button008.setOnKeyListener(this);
        button009.setOnKeyListener(this);
        button010.setOnKeyListener(this);
        button011.setOnKeyListener(this);
        button012.setOnKeyListener(this);
        button013.setOnKeyListener(this);
        button014.setOnKeyListener(this);
        button015.setOnKeyListener(this);
        button016.setOnKeyListener(this);
        button017.setOnKeyListener(this);
        button018.setOnKeyListener(this);
        button019.setOnKeyListener(this);
        button020.setOnKeyListener(this);
        button021.setOnKeyListener(this);
        button022.setOnKeyListener(this);
        button023.setOnKeyListener(this);
        button024.setOnKeyListener(this);
        button025.setOnKeyListener(this);
        button026.setOnKeyListener(this);
        button027.setOnKeyListener(this);
        button028.setOnKeyListener(this);
        button029.setOnKeyListener(this);
        button030.setOnKeyListener(this);
        button031.setOnKeyListener(this);
        button032.setOnKeyListener(this);

        //lee el contenido de la Base de Datos - Config
        Leedb();
        //muestra_DB();

        /*
        //Inicia captura de Coordenadas XY
        cox_coy= new Coor_xy();
        this.textView_xy = (TextView) findViewById( R.id.strXY );
        //Evento Touch
        this.textView_xy.setOnTouchListener( new View.OnTouchListener()
        {
            @Override
            public boolean onTouch( View arg0, MotionEvent arg1 ) {

                stringBuilder.setLength(0);
                //si la accion es soltar
                if( arg1.getAction() == MotionEvent.ACTION_UP )
                {
                    float co_X = arg1.getX();
                    float co_Y = arg1.getY();
                    String rev=cox_coy.co_xy(co_X, co_Y);
                    revisar(rev);
                }
                return true;
            }
        });
        //FIN Captura de Coordenadas XY
        */

    }

    //Funcion Acciones
    public void revisar (String rev_coor){
        switch (rev_coor) {
            //Boton a HOME
            case "H1":
                finish();
                break;
            //Guarda los datos a DB
            case "H8":
                guardar(null);
                break;
        }
    }

    //Lee de la Base de Datos
    public void Leedb(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "tablet", null, 1);
        bd = admin.getWritableDatabase();
        //*****************************************************************************
        int numLec = 1, pos_ini, pos_fin, total_pos;

        //Solicita solo los datos requeridos a la Base de Datos
        Cursor fila = bd.rawQuery("select rango_pos_ini, rango_pos_fin from config where num="+numLec + "", null);
        if (fila.moveToFirst()) {
            //Lee Posicion Inicial y Final configurados
            pos_ini = fila.getInt(0);
            pos_fin = fila.getInt(1);
            //Muetra los Botones disponibles
            verPos(pos_ini, pos_fin);
        } else{
            Toast.makeText(this, "NO lee DB",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //Recorre y activa los botones que se usaran para asignar Lector
    public void verPos(int pos_ini, int pos_fin){
        //El total de botones a mostrar
        int total_pos = 1 + pos_fin - pos_ini;

        String lecPos = "";

        //recorre desde pos inicial a pos final mostrando botones y nombre asignado
        for(int numvista=1; numvista<=total_pos; numvista++) {
            //Pregunta por el Lector que se tiene Registrado a la DB
            lecPos = buscaLectoPos(pos_ini);
            vista_muestra(numvista, pos_ini, lecPos);
            pos_ini++;
        }
    }


    //Activa la Vista de cada Boton a solicitid del numero tatal de posiciones activas
    public void vista_muestra(int numvista, int pos_muestra, String lecPos){
        //Cambia de un entero a cadena para mostrar la posicion asignada al Boton
        String pos_muestra_s = String.format("%02d", pos_muestra);
        //Dependiendo del Numero de Posicion se asigna al Boton
        switch (numvista) {
            case 1:
                //Muestra el Boton y el Numero de posicion
                visP1.setVisibility(View.VISIBLE);
                textView001.setText("POSICIÓN " + pos_muestra_s + " : ");
                button001.setText(lecPos);
                button001.setFocusable(true);
                button001.requestFocus();
                break;
            case 2:
                visP2.setVisibility(View.VISIBLE);
                textView002.setText("POSICIÓN " + pos_muestra_s + " : ");
                button002.setText(lecPos);
                break;
            case 3:
                visP3.setVisibility(View.VISIBLE);
                textView003.setText("POSICIÓN " + pos_muestra_s + " : ");
                button003.setText(lecPos);
                break;
            case 4:
                visP4.setVisibility(View.VISIBLE);
                textView004.setText("POSICIÓN " + pos_muestra_s + " : ");
                button004.setText(lecPos);
                break;
            case 5:
                visP5.setVisibility(View.VISIBLE);
                textView005.setText("POSICIÓN " + pos_muestra_s + " : ");
                button005.setText(lecPos);
                break;
            case 6:
                visP6.setVisibility(View.VISIBLE);
                textView006.setText("POSICIÓN " + pos_muestra_s + " : ");
                button006.setText(lecPos);
                break;
            case 7:
                visP7.setVisibility(View.VISIBLE);
                textView007.setText("POSICIÓN " + pos_muestra_s + " : ");
                button007.setText(lecPos);
                break;
            case 8:
                visP8.setVisibility(View.VISIBLE);
                textView008.setText("POSICIÓN " + pos_muestra_s + " : ");
                button008.setText(lecPos);
                break;
            case 9:
                visP9.setVisibility(View.VISIBLE);
                textView009.setText("POSICIÓN " + pos_muestra_s + " : ");
                button009.setText(lecPos);
                break;
            case 10:
                visP10.setVisibility(View.VISIBLE);
                textView010.setText("POSICIÓN " + pos_muestra_s + " : ");
                button010.setText(lecPos);
                break;
            case 11:
                visP11.setVisibility(View.VISIBLE);
                textView011.setText("POSICIÓN " + pos_muestra_s + " : ");
                button011.setText(lecPos);
                break;
            case 12:
                visP12.setVisibility(View.VISIBLE);
                textView012.setText("POSICIÓN " + pos_muestra_s + " : ");
                button012.setText(lecPos);
                break;
            case 13:
                visP13.setVisibility(View.VISIBLE);
                textView013.setText("POSICIÓN " + pos_muestra_s + " : ");
                button013.setText(lecPos);
                break;
            case 14:
                visP14.setVisibility(View.VISIBLE);
                textView014.setText("POSICIÓN " + pos_muestra_s + " : ");
                button014.setText(lecPos);
                break;
            case 15:
                visP15.setVisibility(View.VISIBLE);
                textView015.setText("POSICIÓN " + pos_muestra_s + " : ");
                button015.setText(lecPos);
                break;
            case 16:
                visP16.setVisibility(View.VISIBLE);
                textView016.setText("POSICIÓN " + pos_muestra_s + " : ");
                button016.setText(lecPos);
                break;
            case 17:
                visP17.setVisibility(View.VISIBLE);
                textView017.setText("POSICIÓN " + pos_muestra_s + " : ");
                button017.setText(lecPos);
                break;
            case 18:
                visP18.setVisibility(View.VISIBLE);
                textView018.setText("POSICIÓN " + pos_muestra_s + " : ");
                button018.setText(lecPos);
                break;
            case 19:
                visP19.setVisibility(View.VISIBLE);
                textView019.setText("POSICIÓN " + pos_muestra_s + " : ");
                button019.setText(lecPos);
                break;
            case 20:
                visP20.setVisibility(View.VISIBLE);
                textView020.setText("POSICIÓN " + pos_muestra_s + " : ");
                button020.setText(lecPos);
                break;
            case 21:
                visP21.setVisibility(View.VISIBLE);
                textView021.setText("POSICIÓN " + pos_muestra_s + " : ");
                button021.setText(lecPos);
                break;
            case 22:
                visP22.setVisibility(View.VISIBLE);
                textView022.setText("POSICIÓN " + pos_muestra_s + " : ");
                button022.setText(lecPos);
                break;
            case 23:
                visP23.setVisibility(View.VISIBLE);
                textView023.setText("POSICIÓN " + pos_muestra_s + " : ");
                button023.setText(lecPos);
                break;
            case 24:
                visP24.setVisibility(View.VISIBLE);
                textView024.setText("POSICIÓN " + pos_muestra_s + " : ");
                button024.setText(lecPos);
                break;
            case 25:
                visP25.setVisibility(View.VISIBLE);
                textView025.setText("POSICIÓN " + pos_muestra_s + " : ");
                button025.setText(lecPos);
                break;
            case 26:
                visP26.setVisibility(View.VISIBLE);
                textView026.setText("POSICIÓN " + pos_muestra_s + " : ");
                button026.setText(lecPos);
                break;
            case 27:
                visP27.setVisibility(View.VISIBLE);
                textView027.setText("POSICIÓN " + pos_muestra_s + " : ");
                button027.setText(lecPos);
                break;
            case 28:
                visP28.setVisibility(View.VISIBLE);
                textView028.setText("POSICIÓN " + pos_muestra_s + " : ");
                button028.setText(lecPos);
                break;
            case 29:
                visP29.setVisibility(View.VISIBLE);
                textView029.setText("POSICIÓN " + pos_muestra_s + " : ");
                button029.setText(lecPos);
                break;
            case 30:
                visP30.setVisibility(View.VISIBLE);
                textView030.setText("POSICIÓN " + pos_muestra_s + " : ");
                button030.setText(lecPos);
                break;
            case 31:
                visP31.setVisibility(View.VISIBLE);
                textView031.setText("POSICIÓN " + pos_muestra_s + " : ");
                button031.setText(lecPos);
                break;
            case 32:
                visP32.setVisibility(View.VISIBLE);
                textView032.setText("POSICIÓN " + pos_muestra_s + " : ");
                button032.setText(lecPos);
                break;

        }
    }

    public String buscaLectoPos(int busca){
        String pos_busca = String.format("%02d", busca);
        String ms_Total="- - - -";

        Cursor fila = bd.rawQuery("select lec from lectores where posc like '%"+pos_busca+"%'"
                + "", null);
        if (fila.moveToFirst()) {
            String lec=fila.getString(0);
            ms_Total = lec;
        }
        //msj(ms_Total);
        return ms_Total;
    }

    public void vista_boton(String lec){
        switch (posActualiza) {
            case 1:
                button001.setText(lec);
                //msj(button001.getText().toString());
                break;
            case 2:
                button002.setText(lec);
                break;
            case 3:
                button003.setText(lec);
                break;
            case 4:
                button004.setText(lec);
                break;
            case 5:
                button005.setText(lec);
                break;
            case 6:
                button006.setText(lec);
                break;
            case 7:
                button007.setText(lec);
                break;
            case 8:
                button008.setText(lec);
                break;
            case 9:
                button009.setText(lec);
                break;
            case 10:
                button010.setText(lec);
                break;
            case 11:
                button011.setText(lec);
                break;
            case 12:
                button012.setText(lec);
                break;
            case 13:
                button013.setText(lec);
                break;
            case 14:
                button014.setText(lec);
                break;
            case 15:
                button015.setText(lec);
                break;
            case 16:
                button016.setText(lec);
                break;
            case 17:
                button017.setText(lec);
                break;
            case 18:
                button018.setText(lec);
                break;
            case 19:
                button019.setText(lec);
                break;
            case 20:
                button020.setText(lec);
                break;
            case 21:
                button021.setText(lec);
                break;
            case 22:
                button022.setText(lec);
                break;
            case 23:
                button023.setText(lec);
                break;
            case 24:
                button024.setText(lec);
                break;
            case 25:
                button025.setText(lec);
                break;
            case 26:
                button026.setText(lec);
                break;
            case 27:
                button027.setText(lec);
                break;
            case 28:
                button028.setText(lec);
                break;
            case 29:
                button029.setText(lec);
                break;
            case 30:
                button030.setText(lec);
                break;
            case 31:
                button031.setText(lec);
                break;
            case 32:
                button032.setText(lec);
                break;


        }
    }

    public void onButtonClicked(View view) {
        // Revisar que Id se dio click
        switch (view.getId()) {
            case R.id.button001:
                //Toast.makeText(this, "ENTER EN BOTON 1", Toast.LENGTH_SHORT).show();
                posActualiza = 1;
                rev_lectores();
                break;
            case R.id.button002:
                posActualiza = 2;
                rev_lectores();
                break;
            case R.id.button003:
                posActualiza = 3;
                rev_lectores();
                break;
            case R.id.button004:
                posActualiza = 4;
                rev_lectores();
                break;
            case R.id.button005:
                posActualiza = 5;
                rev_lectores();
                break;
            case R.id.button006	:
                posActualiza = 6;
                rev_lectores();
                break;
            case R.id.button007	:
                posActualiza = 7;
                rev_lectores();
                break;
            case R.id.button008	:
                posActualiza = 8;
                rev_lectores();
                break;
            case R.id.button009	:
                posActualiza = 9;
                rev_lectores();
                break;
            case R.id.button010	:
                posActualiza = 10;
                rev_lectores();
                break;
            case R.id.button011	:
                posActualiza = 11;
                rev_lectores();
                break;
            case R.id.button012	:
                posActualiza = 12;
                rev_lectores();
                break;
            case R.id.button013	:
                posActualiza = 13;
                rev_lectores();
                break;
            case R.id.button014	:
                posActualiza = 14;
                rev_lectores();
                break;
            case R.id.button015	:
                posActualiza = 15;
                rev_lectores();
                break;
            case R.id.button016	:
                posActualiza = 16;
                rev_lectores();
                break;
            case R.id.button017	:
                posActualiza = 17;
                rev_lectores();
                break;
            case R.id.button018	:
                posActualiza = 18;
                rev_lectores();
                break;
            case R.id.button019	:
                posActualiza = 19;
                rev_lectores();
                break;
            case R.id.button020	:
                posActualiza = 20;
                rev_lectores();
                break;
            case R.id.button021	:
                posActualiza = 21;
                rev_lectores();
                break;
            case R.id.button022	:
                posActualiza = 22;
                rev_lectores();
                break;
            case R.id.button023	:
                posActualiza = 23;
                rev_lectores();
                break;
            case R.id.button024	:
                posActualiza = 24;
                rev_lectores();
                break;
            case R.id.button025	:
                posActualiza = 25;
                rev_lectores();
                break;
            case R.id.button026	:
                posActualiza = 26;
                rev_lectores();
                break;
            case R.id.button027	:
                posActualiza = 27;
                rev_lectores();
                break;
            case R.id.button028	:
                posActualiza = 28;
                rev_lectores();
                break;
            case R.id.button029	:
                posActualiza = 29;
                rev_lectores();
                break;
            case R.id.button030	:
                posActualiza = 30;
                rev_lectores();
                break;
            case R.id.button031	:
                posActualiza = 31;
                rev_lectores();
                break;
            case R.id.button032	:
                posActualiza = 32;
                rev_lectores();
                break;
        }
    }

    //Revisa en que Boton se preciono "ENTER"
    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                keyCode == EditorInfo.IME_ACTION_DONE ||
                event.getAction() == KeyEvent.ACTION_DOWN &&
                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if (!event.isShiftPressed()) {
                //Revisa que Boton
                switch (view.getId()) {
                    case R.id.button001:
                        //Toast.makeText(this, "ENTER EN BOTON 1", Toast.LENGTH_SHORT).show();
                        posActualiza = 1;
                        rev_lectores();
                        break;
                    case R.id.button002:
                        posActualiza = 2;
                        rev_lectores();
                        break;
                    case R.id.button003:
                        posActualiza = 3;
                        rev_lectores();
                        break;
                    case R.id.button004:
                        posActualiza = 4;
                        rev_lectores();
                        break;
                    case R.id.button005:
                        posActualiza = 5;
                        rev_lectores();
                        break;
                    case R.id.button006	:
                        posActualiza = 6;
                        rev_lectores();
                        break;
                    case R.id.button007	:
                        posActualiza = 7;
                        rev_lectores();
                        break;
                    case R.id.button008	:
                        posActualiza = 8;
                        rev_lectores();
                        break;
                    case R.id.button009	:
                        posActualiza = 9;
                        rev_lectores();
                        break;
                    case R.id.button010	:
                        posActualiza = 10;
                        rev_lectores();
                        break;
                    case R.id.button011	:
                        posActualiza = 11;
                        rev_lectores();
                        break;
                    case R.id.button012	:
                        posActualiza = 12;
                        rev_lectores();
                        break;
                    case R.id.button013	:
                        posActualiza = 13;
                        rev_lectores();
                        break;
                    case R.id.button014	:
                        posActualiza = 14;
                        rev_lectores();
                        break;
                    case R.id.button015	:
                        posActualiza = 15;
                        rev_lectores();
                        break;
                    case R.id.button016	:
                        posActualiza = 16;
                        rev_lectores();
                        break;
                    case R.id.button017	:
                        posActualiza = 17;
                        rev_lectores();
                        break;
                    case R.id.button018	:
                        posActualiza = 18;
                        rev_lectores();
                        break;
                    case R.id.button019	:
                        posActualiza = 19;
                        rev_lectores();
                        break;
                    case R.id.button020	:
                        posActualiza = 20;
                        rev_lectores();
                        break;
                    case R.id.button021	:
                        posActualiza = 21;
                        rev_lectores();
                        break;
                    case R.id.button022	:
                        posActualiza = 22;
                        rev_lectores();
                        break;
                    case R.id.button023	:
                        posActualiza = 23;
                        rev_lectores();
                        break;
                    case R.id.button024	:
                        posActualiza = 24;
                        rev_lectores();
                        break;
                    case R.id.button025	:
                        posActualiza = 25;
                        rev_lectores();
                        break;
                    case R.id.button026	:
                        posActualiza = 26;
                        rev_lectores();
                        break;
                    case R.id.button027	:
                        posActualiza = 27;
                        rev_lectores();
                        break;
                    case R.id.button028	:
                        posActualiza = 28;
                        rev_lectores();
                        break;
                    case R.id.button029	:
                        posActualiza = 29;
                        rev_lectores();
                        break;
                    case R.id.button030	:
                        posActualiza = 30;
                        rev_lectores();
                        break;
                    case R.id.button031	:
                        posActualiza = 31;
                        rev_lectores();
                        break;
                    case R.id.button032	:
                        posActualiza = 32;
                        rev_lectores();
                        break;
                }
                return true;
            }
        }
        return false; // pass on to other listeners.
    }


    //MSM 05/Oct/2017 Ver:1.8-0
    //Revisa lectores, si son mas de 1 envia a seleccinar Lector.
    public void rev_lectores(){
        String [] lectores;
        lectores = new String[5];
        int dis = 0;
        Object[] pairedObjects = BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray();
        final BluetoothDevice[] pairedDevices = new BluetoothDevice[pairedObjects.length];
        for(int i = 0; i < pairedObjects.length; ++i) {
            pairedDevices[i] = (BluetoothDevice)pairedObjects[i];
        }
        for (int i = 0; i < pairedDevices.length; ++i) {
            String nom_blue = pairedDevices[i].getName();
            String compara = nom_blue.substring(0,2);
            //Se quita restriccion ya que los Dispositivos a partir de Abri 2018 no contiene esa identificacion
            //if (compara.equals("WP")){
                //Regsitra los dispositivos encontrados
                lectores[dis] = nom_blue;
                dis++;
            //}
        }

        if(dis == 0){
            msj("Lectores NO\nVinculados.");
            //finish();
        }else{
            //lector_cone=lectores[0];
            if (dis == 1) {
                //asignaLector(lectores[0]);
                vista_boton(lectores[0]);
            }else{
                Intent i = new Intent(this, Lectores.class);
                i.putExtra("lectores", lectores);
                startActivityForResult(i, 0);
            }
        }
    }

    //MSM 05/Oct/2017 Ver:1.8-0
    //Regresa de selecion del Lector a conectar
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        if (requestCode == 0){
            if (data != null && resultCode == RESULT_OK) {
                //asignaLector(data.getStringExtra("lecBusca"));
                vista_boton(data.getStringExtra("lecBusca"));
            }else{
                vista_boton("- - - -");
            }
        }
    }

    //MSM 06/Oct/2017 Ver:1.8-0
    //Teniendo el lector se el asigna al Usuario.
    /*
    public void asignaLector(String lec){
        //msj(lec);
        vista_boton(lec);
        /*
        ContentValues registro = new ContentValues();
        registro.put("atributos", lector_cone);
        bd.update("users", registro, "user='"+user_edit+"'", null);
        Leedb();
        * /
    }
    */

    public void guardar(View view){

        String valPosRev = "";
        String[] valPosRev_1;
        String valLecRev = "";

        //Se lee todos los lectores con su respectiva posicion y se asignana para guardar
        for (int rev = 1; rev <= 32; rev++) {
            valLecRev = "";
            valLecRev = "- - - -";
            switch (rev) {
                case 1:
                    //Lee el Valor de la Posicion que se Muestra.
                    valPosRev = textView001.getText().toString();//POSICIÓN 01 : //
                    //Lee el Valor del lector asignado a la Posicion.
                    valLecRev = button001.getText().toString();
                    break;
                case 2:
                    valPosRev = textView002.getText().toString();
                    valLecRev = button002.getText().toString();
                    break;
                case 3:
                    valPosRev = textView003.getText().toString();
                    valLecRev = button003.getText().toString();
                    break;
                case 4:
                    valPosRev = textView004.getText().toString();
                    valLecRev = button004.getText().toString();
                    break;
                case 5:
                    valPosRev = textView005.getText().toString();
                    valLecRev = button005.getText().toString();
                    break;
                case 6:
                    valPosRev = textView006.getText().toString();
                    valLecRev = button006.getText().toString();
                    break;
                case 7:
                    valPosRev = textView007.getText().toString();
                    valLecRev = button007.getText().toString();
                    break;
                case 8:
                    valPosRev = textView008.getText().toString();
                    valLecRev = button008.getText().toString();
                    break;
                case 9:
                    valPosRev = textView009.getText().toString();
                    valLecRev = button009.getText().toString();
                    break;
                case 10:
                    valPosRev = textView010.getText().toString();
                    valLecRev = button010.getText().toString();
                    break;
                case 11:
                    valPosRev = textView011.getText().toString();
                    valLecRev = button011.getText().toString();
                    break;
                case 12:
                    valPosRev = textView012.getText().toString();
                    valLecRev = button012.getText().toString();
                    break;
                case 13:
                    valPosRev = textView013.getText().toString();
                    valLecRev = button013.getText().toString();
                    break;
                case 14:
                    valPosRev = textView014.getText().toString();
                    valLecRev = button014.getText().toString();
                    break;
                case 15:
                    valPosRev = textView015.getText().toString();
                    valLecRev = button015.getText().toString();
                    break;
                case 16:
                    valPosRev = textView016.getText().toString();
                    valLecRev = button016.getText().toString();
                    break;
                case 17:
                    valPosRev = textView017.getText().toString();
                    valLecRev = button017.getText().toString();
                    break;
                case 18:
                    valPosRev = textView018.getText().toString();
                    valLecRev = button018.getText().toString();
                    break;
                case 19:
                    valPosRev = textView019.getText().toString();
                    valLecRev = button019.getText().toString();
                    break;
                case 20:
                    valPosRev = textView020.getText().toString();
                    valLecRev = button020.getText().toString();
                    break;
                case 21:
                    valPosRev = textView021.getText().toString();
                    valLecRev = button021.getText().toString();
                    break;
                case 22:
                    valPosRev = textView022.getText().toString();
                    valLecRev = button022.getText().toString();
                    break;
                case 23:
                    valPosRev = textView023.getText().toString();
                    valLecRev = button023.getText().toString();
                    break;
                case 24:
                    valPosRev = textView024.getText().toString();
                    valLecRev = button024.getText().toString();
                    break;
                case 25:
                    valPosRev = textView025.getText().toString();
                    valLecRev = button025.getText().toString();
                    break;
                case 26:
                    valPosRev = textView026.getText().toString();
                    valLecRev = button026.getText().toString();
                    break;
                case 27:
                    valPosRev = textView027.getText().toString();
                    valLecRev = button027.getText().toString();
                    break;
                case 28:
                    valPosRev = textView028.getText().toString();
                    valLecRev = button028.getText().toString();
                    break;
                case 29:
                    valPosRev = textView029.getText().toString();
                    valLecRev = button029.getText().toString();
                    break;
                case 30:
                    valPosRev = textView030.getText().toString();
                    valLecRev = button030.getText().toString();
                    break;
                case 31:
                    valPosRev = textView031.getText().toString();
                    valLecRev = button031.getText().toString();
                    break;
                case 32:
                    valPosRev = textView032.getText().toString();
                    valLecRev = button032.getText().toString();
                    break;
            }

            try {
                valPosRev_1 = valPosRev.split(" ");
                valPosRev = valPosRev_1[1];
                //if(valPosRev.length()>0 && !valLecRev.equals("- - - -")){
                if(valPosRev.length()>0){
                    if(!valLecRev.equals("- - - -")){
                        //msj("SI\n"+valLecRev+"*"+valPosRev);
                        preparadb(valLecRev, valPosRev);
                    }
                }else {
                    String mmms = String.valueOf(rev);
                    msj("Se DETIENE:"+mmms);
                    break;
                }
            }catch (Exception ex){
                //String mmms = String.valueOf(rev);
                //msj("Error y Detiene:"+mmms);
                break;
            }
        }
        guarda_DB();

    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void preparadb(String Lector, String masPos){

        //Busca si esta Libre la Pos o si ya se Guardo el Lector
        for (int i = 0; i < 5; i++) {
            String compara = lec_pos[i][0];
            try {
                if (compara == null || compara.isEmpty()) {
                    //Almacena el contenido general
                    lec_pos[i][0] = Lector;
                    lec_pos[i][1] = masPos;
                    break;
                }else{
                    if (compara.equals(Lector)){
                        //Almacena el contenido de Mas Posiciones
                        lec_pos[i][1] += "|"+masPos;
                        break;
                    }
                }
            }catch (Exception ex)
            {
                msj("Error2");
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void guarda_DB(){
        String lec_DB = "";
        String poss_DB = "";
        int numLec = 1;
        ContentValues registro = new ContentValues();
        /*
        msj(lec_pos[0][0]+"*"+lec_pos[0][1]
                +"\n"+lec_pos[1][0]+"*"+lec_pos[1][1]
                +"\n"+lec_pos[2][0]+"*"+lec_pos[2][1]
                +"\n"+lec_pos[3][0]+"*"+lec_pos[3][1]
                +"\n"+lec_pos[4][0]+"*"+lec_pos[4][1]);
        */
        msj("Guardando...");
        for (int i = 0; i < 5; i++) {
            lec_DB = lec_pos[i][0];
            poss_DB = lec_pos[i][1];
            try {
                //if (lec_DB == null || lec_DB.isEmpty()) {
                  //  break;
                //}else{
                    registro.put("lec", lec_DB);
                    registro.put("posc", poss_DB);
                    int cant = bd.update("lectores", registro, "num="+numLec, null);
                    //bd.update("lectores", registro, "num="+numLec, null);
                    numLec++;
                //}
            }catch (Exception ex)
            {
                msj("Sale DB");
                break;
            }
        }
        //bd.close();
    }

    public void muestra_DB(){
        String ms_Total = "";
        for (int i = 1; i < 6; i++) {
            Cursor fila = bd.rawQuery("select * from lectores where num="+i
                    + "", null);
            if (fila.moveToFirst()) {
                int num=fila.getInt(0);
                String lec=fila.getString(1);
                String posc=fila.getString(2);
                ms_Total += lec+"/"+posc+"\n";
            }
        }
        msj(ms_Total);
    }

    //Muestra en Pantalla Con Fondo Transparente
    public void msj(String msjcon){
        msjcon =">"+ msjcon+ ">2>3>4>5>6>7>";
        Intent i = new Intent(this, Msj.class );
        i.putExtra("msjcon", msjcon);
        startActivity(i);
    }

    public void cerrar(View view){
        finish();
    }

}

