package msm.aie.veribox;

/**
 * Created by Ing Miguel Santiago on Version 2.0 Enero/2018.
 * Clase que se encarga de usar la camara para la captura de Codigo Bidimencionales (QR).
 */


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import java.io.IOException;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;


public class LeeQR extends Activity {

    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private String token = "";
    private String tokenanterior = "";
    private TextView leevista;
    //String mms = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.leeqr);

        leevista = (TextView)findViewById(R.id.leevista);
        cameraView = (SurfaceView) findViewById(R.id.camera_view);

        initQR();

        /*
        //Abre Mensaje de Datos Importantes de QR
        Intent i = new Intent(this, ManualGuia2.class );
        startActivity(i);
        */

    }

    public void initQR() {
        //mms += " - Inicio 2";
        //leevista.setText(mms);

        /*
        // creo el detector qr
        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.ALL_FORMATS)
                        .build();
        */
        // creo el detector qr
        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();


        //mms += " - Inicio 3";
        //leevista.setText(mms);
        // creo la camara
        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(800, 800)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        //mms += " - Inicio 4";
        //leevista.setText(mms);
        // listener de ciclo de vida de la camara
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //mms += " - Inicio 5";
                //leevista.setText(mms);

                // verifico si el usuario dio los permisos para la camara
                if (ActivityCompat.checkSelfPermission(LeeQR.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    //mms += " - Inicio 6";
                    //leevista.setText(mms);

                    /*
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // verificamos la version de ANdroid que sea al menos la M para mostrar
                        // el dialog de la solicitud de la camara
                        if (shouldShowRequestPermissionRationale(
                                Manifest.permission.CAMERA)) ;
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                    return;
                    */
                } else {
                    //mms += " - Inicio 7";
                    //leevista.setText(mms);
                    try {
                        //mms += " - Inicio 8";
                        //leevista.setText(mms);
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException ie) {
                        Log.e("CAMERA SOURCE", ie.getMessage());
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        // preparo el detector de QR
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }


            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                //Se encontro un codigo de barras

                //leevista.setText("Inicio 3: "+barcodes.valueAt(0).displayValue.toString());
                //mms += " - Inicio 9:"+barcodes.valueAt(0).displayValue.toString();
                //leevista.setText(mms);

                if (barcodes.size() > 0) {

                    //mms += " - Inicio 10:"+barcodes.valueAt(0).displayValue.toString();
                    //leevista.setText(mms);

                    // obtenemos el token
                    token = barcodes.valueAt(0).displayValue.toString();

                    // verificamos que el token anterior no se igual al actual
                    // esto es util para evitar multiples llamadas empleando el mismo token
                    if (!token.equals(tokenanterior)) {

                        //mms += " - Inicio 11:"+barcodes.valueAt(0).displayValue.toString();
                        //leevista.setText(mms);

                        // guardamos el ultimo token proceado
                        tokenanterior = token;

                        //muestra();
                        //mms += " - Inicio 11.1:"+barcodes.valueAt(0).displayValue.toString();
                        //leevista.setText(mms);

                        //Cerramos y regresamos el valor encontrado
                        Intent intent = new Intent();
                        intent.putExtra("token", token);
                        setResult(RESULT_OK, intent);
                        finish();

                        //Despues del tiempo determinado se borra el token para evitar
                        //el mismo token en la lectura
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    synchronized (this) {
                                        wait(5000);
                                        // limpiamos el token
                                        tokenanterior = "";
                                    }
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    Log.e("Error", "Waiting didnt work!!");
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
            }
        });

    }

    public void atras(View view){
        finish();
    }

    @Override
    public void onBackPressed() {
        //Cerramos y regresamos el valor encontrado
        Intent intent = new Intent();
        intent.putExtra("token", "");
        setResult(RESULT_CANCELED, intent);
        finish();

    }

}