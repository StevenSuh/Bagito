package com.example.bagito.Account;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bagito.MainActivity;
import com.example.bagito.R;

import org.w3c.dom.Text;

public class AccountActivity extends AppCompatActivity {

    String [] info = new String [] {"Power2Sustain Website", "Bagito Website", "Terms of Service", "Privacy Policy"};
    String [] settingsOptions = new String [] {"Check Rental Status", "Change Information", "Change Notifications"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
//        TextView settingsView = (TextView) findViewById(R.id.settingsView);
//        settingsView.setText("SETTINGS");

        ListView settingsListView = (ListView) findViewById(R.id.settingsListView);
        ArrayAdapter<String> settingsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , settingsOptions);

        settingsListView.setAdapter(settingsAdapter);
        settingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(info[position].equals("Check Rental Status")){
                    power2SustainWebsite();
                }  else if(info[position].equals("Change Information")) {
                    termsOSPage();
                } else if(info[position].equals("Change Notifications")) {
                    privacyPolicyPage();
                }
                Toast.makeText(AccountActivity.this, "Welcome to the " + info[position], Toast.LENGTH_SHORT).show();
            }
        });

        ListView webpageListView = (ListView) findViewById(R.id.webpageListView);
//        webpageListView.addHeaderView(settingsView);
        ArrayAdapter<String> infoAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , info);

        webpageListView.setAdapter(infoAdapter);
        webpageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(info[position].equals("Power2Sustain Website")){
                    power2SustainWebsite();
                } else if(info[position].equals("Bagito Website")) {
                    bagitoWebsite();
                } else if(info[position].equals("Terms of Service")) {
                    termsOSPage();
                } else if(info[position].equals("Privacy Policy")) {
                    privacyPolicyPage();
                }
                Toast.makeText(AccountActivity.this, "Welcome to the " + info[position], Toast.LENGTH_SHORT).show();
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
        Intent startIntent = new Intent(getApplicationContext(), TOSActivity.class);
//        startIntent.putExtra("com.example.myapplication.ExtraInfo","Hello World");
        startActivity(startIntent);
    }

    public void privacyPolicyPage(){
        Intent startIntent = new Intent(getApplicationContext(), PrivacyPolicyActivity.class);
//        startIntent.putExtra("com.example.myapplication.ExtraInfo","Hello World");
        startActivity(startIntent);
    }

}