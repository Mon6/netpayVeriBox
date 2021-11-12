package msm.aie.veribox;

/**
 * Created by Ing Miguel Santiago on 22/02/18.
 * Clase base para implementar un componente de administración de dispositivo.
 * Esta clase proporciona una conveniencia para interpretar las acciones de intención sin procesar que envía el sistema.
 */

import android.app.admin.DeviceAdminReceiver;
import android.content.ComponentName;
import android.content.Context;

public class BasicDeviceAdminReceiver extends DeviceAdminReceiver {

    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context.getApplicationContext(), BasicDeviceAdminReceiver.class);
    }

}
