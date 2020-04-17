package com.ma7moud3ly.sha5rameter;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.codeandmagic.android.gauge.GaugeView;

import androidx.appcompat.app.AppCompatActivity;

public class Result extends AppCompatActivity {
    private GaugeView meter;
    private TextView description, strength;
    private String _description, _strength;
    private LinearLayout sharelayout, btnslayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_page);
        description = findViewById(R.id.description);
        strength = findViewById(R.id.strength);
        description.setText("جارى معالجة الشخرة ...");
        meter = findViewById(R.id.meter);

        sharelayout = findViewById(R.id.sharelayout);
        btnslayout = findViewById(R.id.btnslayout);
        btnslayout.setVisibility(View.INVISIBLE);

        String desc = "";
        Intent intent = getIntent();
        if (intent.hasExtra("val")) {
            val = intent.getIntExtra("val", 0);
            meter.setTargetValue(0);
            if (val >= 80)
                desc = "شخرة مدوية";
            else if (val >= 60)
                desc = "شخرة من الشمخ الجوانى";
            else if (val >= 40)
                desc = "شخرة متوسطة";
            else if (val >= 20)
                desc = "شخرة ضعيفة";
            else
                desc = " شخرة أطفال";
            _description = desc + " بشدة : ";
            _strength = val + " ديسيبل";
            getReady();
        }

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3016789990656088~2230534292");
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("7229BDFA1A49F3AE5DE9BD6276416790").build();
        ((AdView) findViewById(R.id.adView)).loadAd(adRequest);
    }

    final int pos[] = {0, 25, 50, 75, 100};
    final int tick = 50;
    final int total = (pos.length * tick + (tick * 2)) * 4;
    final int loop = 1;
    int current = 1;
    int i = 0;
    int val = 0;


    CountDownTimer cw = new CountDownTimer(total, tick) {
        @Override
        public void onTick(final long millisUntilFinished) {
            if (i < pos.length) {
                meter.setTargetValue(pos[i]);
                i++;
            }
        }

        @Override
        public void onFinish() {
            i = pos.length - 1;
            ccw.start();
        }
    };

    CountDownTimer ccw = new CountDownTimer(total, tick) {
        @Override
        public void onTick(final long millisUntilFinished) {
            if (i >= 0) {
                meter.setTargetValue(pos[i]);
                i--;
            }
        }

        @Override
        public void onFinish() {
            if (current < loop) {
                i = 0;
                cw.start();
                current++;
            } else {
                btnslayout.setVisibility(View.VISIBLE);
                description.setText(_description);
                strength.setText(_strength);
                meter.setTargetValue(val);
                current = 1;
            }
        }
    };

    private void getReady() {
        i = 0;
        meter.setTargetValue(0);
        cw.start();
    }

    public void share(View v) {
        Screenshot screenshot = new Screenshot(this, sharelayout);
        screenshot.subject = "Sha5rometer";
        screenshot.text = "من تطبيق شخروميتر - مقياس شدة الشخرة";
        screenshot.CaptureAndShare();
    }

    public void back(View v) {
        finish();
    }


}
