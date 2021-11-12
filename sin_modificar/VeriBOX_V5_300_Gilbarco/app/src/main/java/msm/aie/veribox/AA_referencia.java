package msm.aie.veribox;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

public class AA_referencia {

    //@Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {

        if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                keyCode == EditorInfo.IME_ACTION_DONE ||
                event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            //Toast.makeText(this, "ENTER",Toast.LENGTH_SHORT).show();
        }
        if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                keyCode == EditorInfo.IME_ACTION_DONE ||
                event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
            //Toast.makeText(this, "VOL ABAJO",Toast.LENGTH_SHORT).show();
        }
        if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                keyCode == EditorInfo.IME_ACTION_DONE ||
                event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            //Toast.makeText(this, "VOL ARRIBA",Toast.LENGTH_SHORT).show();
        }
        if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                keyCode == EditorInfo.IME_ACTION_DONE ||
                event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_NEXT) {
            //Toast.makeText(this, "SIGUIENTE",Toast.LENGTH_SHORT).show();
        }
        if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                keyCode == EditorInfo.IME_ACTION_DONE ||
                event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
            //Toast.makeText(this, "ANTES",Toast.LENGTH_SHORT).show();
        }

        if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                keyCode == EditorInfo.IME_ACTION_DONE ||
                event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
            //Toast.makeText(this, "AMBOS",Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(this, "return 3",Toast.LENGTH_SHORT).show();
        return false; // pass on to other listeners.
    }
}
