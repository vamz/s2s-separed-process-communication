package vamz.kst.fri.serverapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // just start service
        Intent mIntent = new Intent(this, SourceService.class);
        startService(mIntent);

    }
}
