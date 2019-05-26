package com.example.bagito.Account;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bagito.Enums;
import com.example.bagito.MainActivity;
import com.example.bagito.R;
import com.example.bagito.Utils;
import com.example.bagito.login.LoginActivity;

import org.w3c.dom.Text;

public class AccountActivity extends AppCompatActivity {

    String [] info = new String [] {"Power2Sustain Website", "Bagito Website", "Terms of Service", "Privacy Policy"};
    String [] settingsOptions = new String [] {"Check Rental Status", "Change Settings", "Sign Out"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        LinearLayout homeButton = findViewById(R.id.home_button);
        LinearLayout rentButton = findViewById(R.id.rent_button);
        LinearLayout returnButton = findViewById(R.id.return_button);
        LinearLayout accountButton = findViewById(R.id.account_button);

        Utils.initNavbar(this,
                Enums.ACCOUNT_BUTTON.toString(),
                homeButton,
                rentButton,
                returnButton,
                accountButton);

        ListView settingsListView = findViewById(R.id.settingsListView);
        ArrayAdapter<String> settingsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , settingsOptions);

        settingsListView.setAdapter(settingsAdapter);
        settingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (settingsOptions[position].equals("Check Rental Status")){
                    checkRentalStatus();
                } else if (settingsOptions[position].equals("Change Settings")) {
                    changeSettings();
                } else if (settingsOptions[position].equals("Sign Out")) {
                    signOut();
                }
            }
        });

        ListView webpageListView = findViewById(R.id.webpageListView);
        ArrayAdapter<String> infoAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , info);

        webpageListView.setAdapter(infoAdapter);
        webpageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (info[position].equals("Power2Sustain Website")){
                    power2SustainWebsite();
                } else if (info[position].equals("Bagito Website")) {
                    bagitoWebsite();
                } else if (info[position].equals("Terms of Service")) {
                    termsOSPage();
                } else if (info[position].equals("Privacy Policy")) {
                    privacyPolicyPage();
                }
            }
        });
    }

    public void power2SustainWebsite(){
        // Attempts to launch an activity outside our app

        String power2Sustain = "https://www.power2sustain.org/";
        Uri webaddress = Uri.parse(power2Sustain);

        Intent goTopower2Sustain= new Intent(Intent.ACTION_VIEW, webaddress);
        // Have to check if response to intent is null or not
        if (goTopower2Sustain.resolveActivity(getPackageManager()) != null){
            startActivity(goTopower2Sustain);
        }
    }
    public void bagitoWebsite(){
        // Attempts to launch an activity outside our app

        String bagito = "https://www.bagito.co/";
        Uri webaddress = Uri.parse(bagito);

        Intent goToBagito= new Intent(Intent.ACTION_VIEW, webaddress);
        // Have to check if response to intent is null or not
        if (goToBagito.resolveActivity(getPackageManager()) != null){
            startActivity(goToBagito);
        }
    }

    public void termsOSPage(){
        Intent startIntent = new Intent(this, TOSActivity.class);
        startActivity(startIntent);
    }

    public void privacyPolicyPage(){
        Intent startIntent = new Intent(this, PrivacyPolicyActivity.class);
        startActivity(startIntent);
    }

    public void checkRentalStatus() {
//        Intent startIntent = new Intent(this, CheckRentalStatus.class);
//        startActivity(startIntent);
    }

    public void changeSettings() {
        Intent startIntent = new Intent(this, ChangeInfo.class);
        startActivity(startIntent);
    }

    public void signOut() {
        Utils.nukePrefs(getSharedPreferences(Enums.SHARED_PREFS.toString(), MODE_PRIVATE));

        Intent startIntent = new Intent(this, LoginActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startIntent);
    }
}