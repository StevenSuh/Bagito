package com.example.bagito.Account;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bagito.MainActivity;
import com.example.bagito.R;

public class AccountActivity extends AppCompatActivity {

    String [] info = new String [] {"Power2Sustain Website", "Bagito Website", "Terms of Service", "Privacy Policy"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ListView webpageListView = (ListView) findViewById(R.id.webpageListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , info);

        webpageListView.setAdapter(adapter);
        webpageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(AccountActivity.this, info[position], Toast.LENGTH_SHORT).show();
            }
        });
    }
}