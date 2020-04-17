package com.ma7moud3ly.sha5rameter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class Main extends AppCompatActivity {
    private TextView txt;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        CheckRecordPermission();

        intent = new Intent(this, Result.class);
        txt = findViewById(R.id.txt);

        ((ImageView) findViewById(R.id.go)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);

                new CountDownTimer(2500, 500) {
                    @Override
                    public void onTick(final long millisUntilFinished) {
                        if (i < count.length) {
                            tg.startTone(ToneGenerator.TONE_PROP_BEEP);
                            txt.setText(count[i]);
                            i++;
                        } else {
                            i = 0;
                        }
                    }

                    @Override
                    public void onFinish() {
                        i = 0;
                        txt.setText("");
                        listen(recordTime);
                    }
                }.start();
            }
        });

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3016789990656088~2230534292");
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("7229BDFA1A49F3AE5DE9BD6276416790").build();
        ((AdView) findViewById(R.id.adView)).loadAd(adRequest);

    }

    private final String[] count = {"3", "2", "1", "هوب"};
    private int i = 0;
    private MediaRecorder mr = null;
    private boolean isRec = false;

    private Handler handler;
    private Runnable r;
    private static int max = 0;
    private final int recordTime = 2000;
    private final int getMaxDelay = 200;
    private final int maxAmp = 32767;
    //private final int maxAmp = 10000;

    private void listen(int duration) {
        if (mr != null) return;
        try {
            mr = new MediaRecorder();
            mr.setAudioSource(MediaRecorder.AudioSource.MIC);
            mr.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mr.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mr.setOutputFile("/dev/null");
            mr.setMaxDuration(duration);
            mr.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mmr, int what, int extra) {
                    if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                        txt.setText("كفاية كده..");
                        mr.stop();
                        mr.release();
                        mr = null;
                        int max_perc = max * 100 / maxAmp;

                        //Log.v("hob","final : " + max_perc);
                        //Toast.makeText(getApplicationContext(), "" + max_perc, Toast.LENGTH_SHORT).show();
                        intent.putExtra("val", max_perc);
                        startActivity(intent);
                    }
                }
            });

            mr.prepare();
            mr.start();
            max = 0;
            handler = new Handler(getMainLooper());
            r = new Runnable() {
                @Override
                public void run() {
                    if (mr != null) {
                        int amp = mr.getMaxAmplitude();
                        //Log.v("hob","" + amp);
                        if (amp > max)
                            max = amp;
                        handler.postDelayed(r, getMaxDelay);
                    }
                }
            };
            handler.post(r);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        mr.stop();
        mr.release();
    }

    private void CheckRecordPermission() {
        if (Build.VERSION.SDK_INT >= 23)
            if (this.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    ;
                else finish();

                break;
        }
    }


}
