package com.example.bagito;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

// NOTE: following androidtutorialonline -- replacing Activity name
// "QrCodeScannerActivity" with "RentActivity"

// implementation also acts as Barcode scanner
public class ReturnActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, LocationListener {

    private static final int REQUEST = 1;
    private ZXingScannerView mScannerView;          // view to scan the QR code
    private TextView mScannerInstructions;

    private LocationManager locationManager;
    private String location = null;
    private String bag_qrcode_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScannerView = new ZXingScannerView(this);
        setContentView(R.layout.scanner);

        ViewGroup contentFrame = findViewById(R.id.content_frame);
        contentFrame.addView(mScannerView);

        mScannerInstructions = findViewById(R.id.scanner_instructions);
        mScannerInstructions.setText(getString(R.string.return_bag_instructions));

        // Have to get user permissions at runtime --> check API version
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (!checkPermission()) {
                requestPermission();
            }
        }
    }

    // checks user permissions
    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    // requests user permissions
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA, ACCESS_FINE_LOCATION}, REQUEST);
    }

    // UNDERSTAND AND TEST THIS METHOD
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST) {
            boolean cameraAccepted = (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
            if (!cameraAccepted) {
                Toast.makeText(getApplicationContext(), "Permission Denied.", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(CAMERA)) {
                        showMessageOKCancel("You need to allow access to the camera",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermissions(new String[]{CAMERA}, REQUEST);
                                    }
                                });
                    }
                }
            }
        }
    }

    //
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(ReturnActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
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
    }

    // handles result of the QR Code scan
    @Override
    public void handleResult(Result rawResult) {
        getLocation();

        final String result = rawResult.getText();  // literally whatever is in the QR code

        // Show the result of the scan and two buttons, OK and Visit
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Successfully scanned!");

        // instead of above, have a Cancel button that "refreshes" scanning (don't send to server)
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mScannerView.resumeCameraPreview(ReturnActivity.this);
            }
        });

        // instead of above, have a rent button that sends bagID over to server for processing/DB (confirm)
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(bag_qrcode_id)) {
                    confirmBag(result);
                } else {
                    returnBagAtBin(result);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // getting location
    private void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location loc) {
        location = loc.getLatitude() + ", " + loc.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    private void confirmBag(final String bagId) {
        RequestParams rp = new RequestParams();
        rp.add("bag_qrcode_id", bagId);

        HttpUtils.post("/api/return/confirm", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                bag_qrcode_id = bagId;
                startScanningBin();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                failureServer(null);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse == null) {
                    failureServer(null);
                    return;
                }

                try {
                    String message = errorResponse.getString(HttpUtils.ERROR_MSG);
                    failureServer(message);
                } catch (JSONException e) {
                    failureServer(null);
                }
            }
        });
    }

    private void returnBagAtBin(String binId) {
        DataHolder.User user = DataHolder.getInstance().getUser();

        RequestParams rp = new RequestParams();
        rp.add("user_id", Integer.toString(user.id));
        rp.add("bag_qrcode_id", bag_qrcode_id);
        rp.add("bin_qrcode_id", binId);
        rp.add("location", location);

        HttpUtils.post("/api/return", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                successReturn();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                failureServer(null);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse == null) {
                    failureServer(null);
                    return;
                }

                try {
                    String message = errorResponse.getString(HttpUtils.ERROR_MSG);
                    failureServer(message);
                } catch (JSONException e) {
                    failureServer(null);
                }
            }
        });
    }

    private void startScanningBin() {
        mScannerInstructions.setText(getString(R.string.return_bin_instructions));
        mScannerView.resumeCameraPreview(ReturnActivity.this);
    }

    private void successReturn() {
        // Show the result of the scan and two buttons, OK and Visit
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bag successfully returned!");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void failureServer(String error) {
        if (TextUtils.isEmpty(error)) {
            error = "Server error";
        }
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        mScannerView.resumeCameraPreview(ReturnActivity.this);
    }
}
