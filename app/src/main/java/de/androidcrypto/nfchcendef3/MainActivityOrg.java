package de.androidcrypto.nfchcendef3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivityOrg extends AppCompatActivity {
    private static final String TAG = "JDR HostCardEmulation";

    // AID is setup in apduservice.xml
    // original AID: F0394148148100
    // new AID: D2760000850101

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar toolbar = getSupportActionBar();
        toolbar.setTitle(R.string.app_name);

        Button setNdef = (Button) findViewById(R.id.set_ndef_button);
        setNdef.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {

                                           //
                                           // Technically, if this is past our byte limit,
                                           // it will cause issues.
                                           //
                                           // TODO: add validation
                                           //
                                           TextView getNdefString = (TextView) findViewById(R.id.ndef_text);
                                           String test = getNdefString.getText().toString();

                                           Intent intent = new Intent(view.getContext(), myHostApduService.class);
                                           intent.putExtra("ndefMessage", test);
                                           startService(intent);
                                       }
                                   }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}