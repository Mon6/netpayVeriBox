package msm.aie.veribox;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class Envia_servidor {

    private final String NAMESPACE = "urn:veriboxwsdl";
    private String URL;// = "http://192.168.3.101/Veribox/Veribox.php";
    private final String SOAPACTION = "urn:veriboxwsdl#veribox";
    private final String METHOD = "veribox";


    public String envia_ser(String salida, String num_tabled, Context context_uso){
        SQLiteDatabase bd;
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(context_uso, "tablet", null, 1);
        bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select * from config where num=1", null);
        if (fila.moveToFirst()) {
            URL=fila.getString(5);
            URL = "http://" + URL + "/Veribox/Veribox.php"; 
        }

        String respuesta = "";
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
        request.addProperty( "d0" , num_tabled);
        request.addProperty( "d1" , salida);
        request.addProperty( "d2" , "");
        request.addProperty( "d3", "");

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
        return respuesta;
    }

    public String busca_xml(String xml, String b1, String b2){
        String envi_retun="";
        String cadena = xml;
        //Busca cadena1 y regresa donde inicia
        int resultado = cadena.indexOf(b1);
        if(resultado != -1) {
            //Desde encuentra hasta el final
            cadena=cadena.substring(resultado);
            //Busca cadena2 y regresa donde inicia
            resultado = cadena.indexOf(b2);
            if(resultado != -1) {
                //Desde encuentra hasta el final
                cadena=cadena.substring(resultado);
                //Dividir la respuesta, obtiene resultado
                String[] partes = cadena.split("\"");
                //Resultado Final
                String respg = partes[1];
                envi_retun=respg;
            }else{
                envi_retun="Respuesta INCOMPLETA";
            }
        }else{
            envi_retun="Respuesta INCOMPLETA";
        }
        return envi_retun;
    }

}
