package msm.aie.veribox;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Miguel Santiago on 23/09/17.
 * Clase que toma los datos enviados para modificar la configuracion
 * de la VeriBox par alterar o restaurar configuracion.
 */

public class Config_rst {

    public Config_rst() {
        //this.pf = pf;
    }

    //A partir de encontrar un archivo de Configuracion se procesa
    //por el momento se encuentra en la clase principal.
    public void configMod(){
        /*
        //Toast.makeText(this, "CONFIGURACION 2", Toast.LENGTH_LONG).show();
        //Verifica que se Pueda leer memoria
        if(isExternalStorageReadable()) {
            File rFileE = new File(Environment.getExternalStorageDirectory(), "Tickets/VBX.txt");

            //Toast.makeText(this, "CONFIGURACION 3", Toast.LENGTH_LONG).show();
            try {
                BufferedReader bReader = new BufferedReader(new FileReader(rFileE));

                //lee linea de archivo
                String leelinea = bReader.readLine();

                //Cierra lectura de Archivo
                bReader.close();
                //Borra archivo
                rFileE.delete();

                //Obtiene Mac a comparar
                //Toast.makeText(this, leelinea+"\nCONFIGURACION 4", Toast.LENGTH_LONG).show();
                obtiene_mac();

                String[] parte = leelinea.split("\\|");
                String ops1 = parte[0]; // Valor de la MAC
                String ops2 = parte[1]; // Valor para reset CLV
                String ops3 = parte[2]; // Valor para reset CLV_GERENTE

                //Toast.makeText(this, ops1+"\n"+address, Toast.LENGTH_LONG).show();

                if (ops1.equals(address)) {
                    //Toast.makeText(this, "CONFIGURACION 5", Toast.LENGTH_LONG).show();

                    //Contenedor de Registros
                    ContentValues registro = new ContentValues();

                    if (ops2.equals("1")) {
                        //Regresa a valor por defecto la Clave de Servicio
                        registro.put("clv", "111111");
                    }

                    if (ops3.equals("1")) {
                        //Regresa a valor por defecto la Clave de Gerente
                        registro.put("clv_gerente", "000000");
                    }

                    //Actualiza los datos en la DB
                    int cant = bd.update("config", registro, "num=1", null);
                    bd.close();


                    if (cant == 1) {
                        msj(">Se Realizaron Cambios en VeriBox>2>3>4>5>6>7>");
                    }
                } else {
                    msj(">NO Corresponde el archivo\na VeriBox >2>3>4>5>6>7>");
                }


            } catch (IOException e) {
                Toast.makeText(this, "NO Lee parametros de entrada", Toast.LENGTH_SHORT).show();
            }
        }
        //Toast.makeText(this, "CONFIGURACION 6", Toast.LENGTH_LONG).show();
        */
    }


}


