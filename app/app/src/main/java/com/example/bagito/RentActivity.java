package com.example.bagito;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
// additional imports from androidtutorialonline.com
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import static android.Manifest.permission.CAMERA;

// NOTE: following androidtutorialonline -- replacing Activity name
// "QrCodeScannerActivity" with "RentActivity"

// implementation also acts as Barcode scanner
public class RentActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView mScannerView;          // view to scan the QR code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main); //default setContentView
        Log.e("onCreate", "onCreate");

        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        // Have to get user permissions at runtime --> check API version
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                Toast.makeText(getApplicationContext(),
                        "Permission already granted",
                        Toast.LENGTH_LONG).show();
            }
            else {
                requestPermission();
            }
        }
    }//<code class="java plain">

    // checks user permissions
    private boolean checkPermission() {
        return ( ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA ) == PackageManager.PERMISSION_GRANTED);
    }

    // requests user permissions
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    // UNDERSTAND AND TEST THIS METHOD
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++){
                        System.out.println(grantResults[i]);
                    }

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    //
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(RentActivity.this)
                .setMessage(message)
                .setPositiveButton("Purchase", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    //For the first time, when the user installs the app,
    // the app will request permission to use the Camera,
    // on subsequent app runs, we don’t need to provide any permission.
    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                // create a new ScannerView if null
                if(mScannerView == null) {
                    mScannerView = new ZXingScannerView(this);
                    setContentView(mScannerView);
                }
                mScannerView.setResultHandler(this);
                // starts Camera to capture QR Code
                mScannerView.startCamera();
            }
            else {
                requestPermission();
            }
        }
    }

    // To release the Camera using stopCamera() method, freezes
    @Override
    public void onDestroy() {
        super.onDestroy();
        mScannerView.stopCamera();
    }//<code class="java plain">

    // handles result of the QR Code scan
    @Override
    public void handleResult(Result rawResult) {
        final String result = rawResult.getText();  // literally whatever is in the QR code
        // Log result to the LogCat
        System.out.println("String result:" + result);

        Log.d("QRCodeScanner", rawResult.getText());    // returns text (e.g. http://www.google.com")
        Log.d("QRCodeScanner", rawResult.getBarcodeFormat().toString()); // returns QR_CODE

        // Show the result of the scan and two buttons, OK and Visit
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        // Clicking on OK button will resume the scanning
        /*builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //mScannerView.resumeCameraPreview(QrCodeScannerActivity.this);
                mScannerView.resumeCameraPreview(MainActivity.this);
            }
        });*/
        // instead of above, have a Cancel button that terminates scanning
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //mScannerView.resumeCameraPreview(QrCodeScannerActivity.this);
                mScannerView.resumeCameraPreview(RentActivity.this);
                //onDestroy();
            }
        });
        // Clicking on Visit button will open what was scanned
        /*builder.setNeutralButton("Purchase", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(result));
                startActivity(browserIntent);
            }
        });*/
        // instead of above, have a Buy button that sends bagID over to server for processing/DB
        builder.setNeutralButton("Purchase", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(result));
                System.out.println(browserIntent.toString());
            }
        });
        builder.setMessage(rawResult.getText());
        AlertDialog alert1 = builder.create();
        alert1.show();
    }



}
