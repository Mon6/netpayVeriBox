package msm.aie.veribox;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by Ing Miguel Santiago on 5/07/18.
 * Clase para Guardar la ejecucion del programa principal.
 */

public class Bitacora {

    public Bitacora() {
        //Inicia variables de uso en la Clase.
        //this.p = p;
        //this.pf = pf;
    }

    public void guarda_b (String dato) {

        dato += "\n";
        File path = new File(Environment.getExternalStorageDirectory(), "Tickets");
        path.mkdirs();
        //Una vez creado disponemos de un archivo para guardar datos
        try
        {
            File ruta_sd = Environment.getExternalStorageDirectory();

            File f = new File(ruta_sd.getAbsolutePath(), "Tickets/btc.txt");

            //escribe en el archivo
            OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(f));
            fout.write(dato);
            fout.close();
        }
        catch (Exception ex)
        {
            Log.e("Ficheros", "Error al escribir fichero.");
        }
    }


}
