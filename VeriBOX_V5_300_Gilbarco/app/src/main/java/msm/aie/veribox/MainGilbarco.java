package msm.aie.veribox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/*
Desactivar TODOS los botones de la intefaz
Correr los timers asincronos
revisar el bloqueo de la interfaz
Uso del Boton BACK en todas las interfaces
 */

public class MainGilbarco extends Activity implements View.OnClickListener {

    private Envia_servidor envia;
    TextView textView44, textView39;
    String pos_solicita, datos_enviando, user_sol;;
    boolean dispo_botones, servidor_ok;;
    int visOperador;
    Button tck_factura, pago_banco, tck_banco, flotilla, tienda;

    //Creamos el puente para regresar
    //el mensaje recibido de peticiones
    private Handler puente = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                if (((String)msg.obj).equals("")){
                    msj(">Problemas COM>2>3>4>5>6>7>");
                    //respuesta_ser("SIN DATOS");
                }
                else {
                    respuesta_ser((String)msg.obj);
                }

            }catch(Exception e)
            {
                e.printStackTrace();
                msj(">Sin comunicacion con SERVIDOR>2>3>4>5>6>7>");
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main_gilbarco);


        textView39 = (TextView)findViewById(R.id.textView39 );
        textView44 = (TextView)findViewById(R.id.textView44);
        envia= new Envia_servidor();

        tck_factura = (Button)findViewById(R.id.tck_factura);
        tck_factura.setOnClickListener(this);
        pago_banco = (Button)findViewById(R.id.pago_banco);
        pago_banco.setOnClickListener(this);
        tck_banco = (Button)findViewById(R.id.tck_banco);
        tck_banco.setOnClickListener(this);
        flotilla = (Button)findViewById(R.id.flotilla);
        flotilla.setOnClickListener(this);
        tienda = (Button)findViewById(R.id.tienda);
        tienda.setOnClickListener(this);


        //Inicializa el estado de la peticiones...
        textView44.setText("Inicia envio...");

        //Inicializa el Usuario de Solicitudes
        user_sol = "000";

        //Inicializa la Posicion de Solicitudes.
        pos_solicita = "0";

        //Bandera de Configuracion Realizada.
        servidor_ok = false;

        //Disponible todos los botones de peticiones.
        dispo_botones = true;

        //Lee la Base de Datos con todos los datos de configuracion
        lee_db();

        /*TESTE DE INICIO, ESTO DE MANDA A UN TIMER*/
        //Inicia el envio de la Prueba de cominicacion al Servidor.
        if (servidor_ok)
            gen_xml("TS");
        else
            msj(">SERVIDOR SIN CONFIGURAR>2>3>4>5>6>7>");
    }

    public void envia_servidor(final String salida, final String num_veribox){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message sms = new Message();
                String resp_servidor = envia.envia_ser(salida, num_veribox, getApplicationContext());
                sms.obj = resp_servidor;
                puente.sendMessage(sms);
            }
        }).start();
    }


    public void respuesta_ser(String xml){
        //Toast.makeText(this, xml,Toast.LENGTH_SHORT).show();

        String busca1 = "mensaje-tipo";
        String busca2 = "tipo";
        String dato=envia.busca_xml(xml, busca1,busca2);

        //Al obteber una respuesta se ACTIVAN los botones
        dispo_botones = true;

        switch (dato) {
            case "TS":
                busca1 = "com";
                busca2 = "res-test";
                dato=envia.busca_xml(xml, busca1,busca2);
                if (dato.equals("true")) {
                    //busca2 = "tester";
                    //dato = envia.busca_xml(xml, busca1, busca2);
                    msj(">Comunicacion con Servidor Correcta,\nsi no imprime comprobante verifique\nla configuracion de la impresora>4>1>4>5>6>7>");
                    user_soli(2);
                }
                break;
            case "TI":
                busca1 = "ticket";
                busca2 = "res";
                dato=envia.busca_xml(xml, busca1,busca2);
                if (dato.equals("true")){
                    busca1 = "display";
                    busca2 = "dato-impresiond";
                    dato=envia.busca_xml(xml, busca1,busca2);
                    msj(">"+dato+">2>3>4>5>6>7>");
                    user_soli(2);
                }else{
                    busca1 = "display";
                    busca2 = "dato-impresiond";
                    dato=envia.busca_xml(xml, busca1,busca2);
                    msj(">"+dato+">2>3>4>5>6>7>");
                    user_soli(2);
                }
                break;
            case "FA":
                busca1 = "factura";
                busca2 = "resf";
                dato=envia.busca_xml(xml, busca1,busca2);
                if (dato.equals("true")){
                    factura_sig("FA");
                }else{
                    busca1 = "display";
                    busca2 = "dato-impresiond";
                    dato=envia.busca_xml(xml, busca1,busca2);
                    msj(">"+dato+">2>3>4>5>6>7>");
                    user_soli(2);
                }

                break;
            case "PB":

                busca1 = "pago-bancario";
                busca2 = "respg";
                dato=envia.busca_xml(xml, busca1,busca2);
                if (dato.equals("true")){
                    Intent i = new Intent(this, Fac_rapida.class );
                    i.putExtra("pos", pos_solicita);
                    i.putExtra("envia_fac", "PB");
                    i.putExtra("tickets", "");
                    i.putExtra("met_pago_f", "");
                    i.putExtra("digitos_f", "");
                    i.putExtra("enti_banco_f", "");
                    i.putExtra("user_sol", user_sol);
                    startActivity(i);
                }else{
                    busca1 = "display";
                    busca2 = "dato-impresiond";
                    dato=envia.busca_xml(xml, busca1,busca2);
                    msj(">"+dato+">2>3>4>5>6>7>");
                    user_soli(2);
                }
                break;

            case "PR":
                busca1 = "preset";
                busca2 = "respr";
                dato=envia.busca_xml(xml, busca1,busca2);
                if (dato.equals("true")){
                    Intent i = new Intent(this, Flotilla1.class );
                    i.putExtra("pos", pos_solicita);
                    i.putExtra("user_sol", user_sol);
                    startActivity(i);
                }else{
                    busca1 = "display";
                    busca2 = "dato-impresiond";
                    dato=envia.busca_xml(xml, busca1,busca2);
                    msj(">"+dato+">2>3>4>5>6>7>");
                    user_soli(2);
                }
                break;

            //Version 2.0-0 MSM 13/Mar/2018
            //Cambio para opcion de Acumulado de Venta
            case "AC":
                busca1 = "acumulado";
                busca2 = "resac";
                dato=envia.busca_xml(xml, busca1,busca2);
                if (dato.equals("true")){
                    factura_sig("AC");
                    //msj(">CONTINUA ACUMULADO>2>3>4>5>6>7>");
                }else{
                    busca1 = "display";
                    busca2 = "dato-impresiond";
                    dato=envia.busca_xml(xml, busca1,busca2);
                    msj(">"+dato+">2>3>4>5>6>7>");
                    user_soli(2);
                }
                break;
        }
    }

    public void factura_sig(String tipo){
        Intent i = new Intent(this, Fac_rapida.class );
        i.putExtra("pos", pos_solicita);
        //i.putExtra("tipofac", tipo);
        i.putExtra("tickets", "");
        i.putExtra("envia_fac", tipo);
        i.putExtra("user_sol", user_sol);
        startActivity(i);
    }



    public void lee_db(){
        SQLiteDatabase bd;
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "tablet", null, 1);
        bd = admin.getWritableDatabase();

        Cursor fila = bd.rawQuery("select * from config where num=1", null);
        if (fila.moveToFirst()) {
            pos_solicita=fila.getString(11);
            int num_tabled=fila.getInt(2);
            String mac=fila.getString(3);
            String version=fila.getString(4);
            int imp_ext=fila.getInt(6);
            String mac_serial=fila.getString(7);
            String nomad=fila.getString(8);
            datos_enviando=">"+num_tabled+">"+mac+">"+version+">"+mac_serial+">"+nomad+">1>"+imp_ext+">";

            //Datos adicionales de Operacion
            visOperador=fila.getInt(23);

            String extra1=fila.getString(27);
            if (extra1.equals("FFFFFFFF"))
                servidor_ok = true;
            int unica_pos=fila.getInt(38);
            if (unica_pos == 1){
                Intent i = new Intent(this, MainActivity.class );
                startActivity(i);
                finish();
            }
        } else{
            Toast.makeText(this, "NO lee DB", Toast.LENGTH_SHORT).show();
        }
        int i=Integer.parseInt(pos_solicita.replaceAll("[\\D]", ""));
        String pos_format = String.format("%02d", i);
        textView39.setText(pos_format);
    }

    //Es llamaddo cuando se Solicita el ticket de la Posicion
    public void ticket(View view){
        if(dispo_botones){
            dispo_botones = false;
            gen_xml("TI");

        }
    }

    public void acumula(View view){
        if(dispo_botones){
            dispo_botones = false;
            gen_xml("AC");

        }
    }

    public void factura(View view){
        if(dispo_botones){
            dispo_botones = false;
            gen_xml("FA");

        }
    }

    @Override
    public void onClick(View v) {
            Intent i;
            switch(v.getId()){
                case R.id.tck_factura:
                    //Tickets para Facturacion.
                    i = new Intent(this, Main_ticket.class );
                    i.putExtra("user_sol", user_sol);
                    //Version 1.8-0 MSM 02/Oct/2017
                    //Parametro par saber quien solicita los Miltiples Tickets
                    i.putExtra("sol_tcks", "T");
                    startActivity(i);
                    break;
                case R.id.pago_banco:
                    //Envia a Pago Bancario
                    gen_xml("PB");
                    break;
                case R.id.tck_banco:
                    //Envia a tickets a Cobro Bancario
                    i = new Intent(this, Main_ticket.class );
                    i.putExtra("user_sol", user_sol);
                    //Version 1.8-0 MSM 02/Oct/2017
                    //Parametro par saber quien solicita los Miltiples Tickets
                    i.putExtra("sol_tcks", "B");
                    startActivity(i);
                    break;
                case R.id.flotilla:
                    //Envia a solicitar venta a FLOTILLA.
                    gen_xml("PR");
                    break;
                case R.id.tienda:
                    //Manda a la ventana de TIENDA --
                    i = new Intent(this, Tienda.class );
                    i.putExtra("envia_dat", "TC");
                    i.putExtra("dispo_f", "");
                    i.putExtra("flotilla", "");
                    i.putExtra("tarjeta", "");
                    i.putExtra("envia_dat", "TC");
                    i.putExtra("user_sol", user_sol);
                    startActivity(i);
                    /*
                    con_time = 4;
                    esp_con=false;
                    permite_salir = true;
                    */
                    break;
            }
    }


    //Genera XML para envio, dependiendo de la peticion.
    public void gen_xml(String tipo_sol){
        String[] partes = datos_enviando.split("\\>");
        String num_tabled = partes[1];;
        String mac = partes[2];
        String version = partes[3];
        String mac_serial = partes[4];
        String nomad = partes[5];
        String intentos = partes[6];
        String imp_ext = partes[7];
        
        String pos_tmp = null;
        if (tipo_sol.equals("TS")){
            pos_tmp = pos_solicita;
            pos_solicita = "99";
        }

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"+
                "<peticion>\n"+
                "   <mensaje-tipo tipo=\""+tipo_sol+"\"></mensaje-tipo>\n"+
                "	<envio tds=\""+num_tabled+"\" mac=\""+ mac+"\" version=\""+version+"\" mac_serial=\""+mac_serial+"\" nomad=\""+nomad+"\" intentos=\""+intentos+"\"></envio>\n"+
                "   <datos>\n"+
                "   <posicion pos=\""+pos_solicita+"\"></posicion>\n"+
                "   <usuario user_sol=\""+user_sol+"\"></usuario>\n";
        switch (tipo_sol) {
            case "TS":
                xml += " <com res-test=\"true\" tester=\"VeriBOX"+num_tabled+"\"></com>\n";
                pos_solicita = pos_tmp;
                break;
            case "TI":
                xml +=  "   <ticket tipo=\""+imp_ext+"\" tipo_pago=\"01\" enti_banco_t=\"\" dig_tarjeta=\"\" > </ticket>\n";
                break;

            case "FA":
                xml +=  "   <factura tipo_solicitu=\"ST\" tipo_clien=\"\" cliente=\"\" tipo_compro=\"\" tipo_pago=\"\" enti_banco_f=\"\" dig_tarjeta=\"\"  nombre=\"\" calle=\"\" num_ex=\"\" num_int=\"\" colonia=\"\" localidad=\"\" referencia=\"\" municipio=\"\" estado=\"\""+
                        "   pais=\"\" cp=\"\" telefono=\"\" correos=\"\" num_ref_f=\"\" >\n"+
                        "   </factura>\n";
                break;
            case "PB":
                xml += "<pago-bancario solicito=\"true\" processed=\"\" tranzaccion=\"ST\" guardar=\"-\">\n"+
                        "<comprobante tipo_compro=\"T\" tipo_cliepb=\"\" clientepb=\"\" >\n"+
                        "</comprobante>\n"+
                        "<registra bancoEmisor=\"\" cardNumber=\"\" hora_fecha=\"\" serieTDS=\"\" aid=\"\" error_description=\"\" transacc=\"\" monto=\"\" arqc=\"\" codigoAprobacion=\"\" marcaTarjeta=\"\" vigencia=\"\" terminalID=\"\" numeroControl=\"\" referenciaBanco=\"\" tvr=\"\" tsi=\"\" apn=\"\" afiliacion=\"\" ></registra>\n"+
                        "</pago-bancario>\n";
                break;

            case "PR":
                xml += "	<preset sol=\"ST\" usuario=\"\" nip=\"\" monto=\"\" monto_preset=\"\" odome_reg=\"\" tipo_venta=\"\" usuario_trj=\"\" ></preset>\n";
                break;

            case "AC":
                xml += "   <acumulado sol=\"ST\" usuario_flo=\"\" ></acumulado>\n";
                break;
            default:
                xml += "Solicitud Incorrecta";
                break;

        }
        xml += "   </datos>\n"+
                "</peticion>";

        envia_servidor (xml,  num_tabled);
    }

    //Muestra Mensaje a Usuario con fondo transparente
    public void msj(String muestra){
        Intent j = new Intent(this, Msj.class );
        j.putExtra("msjcon", muestra);
        startActivity(j);
    }

    public void user_soli(int esp){
        esp = esp * 1000;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                //esp_con = false;
                lee_user();
            }
        }, esp);
    }

    public void lee_user(){
        //Lanza panatalla de Bloquero solicitando usuario.
        //Toast.makeText(this, "Envia USER.", Toast.LENGTH_LONG).show();
        //if (lanza_user_sol && visOperador==0){
        if (visOperador==0){
            Intent intent = null;
            intent = new Intent(this, User_pass.class);
            startActivityForResult(intent, 0);
        }
    }

    public void conf(View view) {
        /*ACTIVARLOS
        con_time = 4;			//Tiempo espera para bloqueo
        esp_con = false;		//Sigue o no esperando
        revMsjEpos = false;		//Pausa mensaje de estatus de Impresora.
        revComm = false;		//Pone en pausa Revicion de Comunicacion
        permite_salir = true;	//Permite o no salir de la interfaz actual
        msImpNoRs = true;		//Detiene por completo el mensaje de  Impresora.
        disconnect();
        */
        Intent i = new Intent(this, Gerente_clv.class );
        startActivity(i);
    }


}
