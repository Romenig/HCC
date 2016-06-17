package com.humorcomciencia.hcc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by rdasilvar on 17/06/2016.
 */
public class NoInternet extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nointernet);

        ImageButton orderButton = (ImageButton) findViewById(R.id.refreshbutton);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent i = new Intent(NoInternet.this, Splash.class);
                overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                startActivity(i);
            }
        });
    }
}
