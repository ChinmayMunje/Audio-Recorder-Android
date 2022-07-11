package com.gtappdevelopers.audiorecorder;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private TextView startRecordTV, stopRecordTV, playRecordingTV, pauseRecordingTV, statusTV;
    private MediaRecorder recorder;
    private MediaPlayer mediaPlayer;
    private static String fileName = null;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTV = findViewById(R.id.idTVStatus);
        startRecordTV = findViewById(R.id.idTVStartRecord);
        stopRecordTV = findViewById(R.id.idTVStopRecording);
        playRecordingTV = findViewById(R.id.idTVPlay);
        pauseRecordingTV = findViewById(R.id.idTVStopPlay);
        stopRecordTV.setBackgroundColor(getResources().getColor(R.color.gray));
        playRecordingTV.setBackgroundColor(getResources().getColor(R.color.gray));
        pauseRecordingTV.setBackgroundColor(getResources().getColor(R.color.gray));

        startRecordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });
        stopRecordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseRecording();

            }
        });
        playRecordingTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio();
            }
        });
        pauseRecordingTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pausePlaying();
            }
        });

    }

    private void startRecording() {
        if (checkPermissions()) {
            stopRecordTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
            startRecordTV.setBackgroundColor(getResources().getColor(R.color.gray));
            playRecordingTV.setBackgroundColor(getResources().getColor(R.color.gray));
            pauseRecordingTV.setBackgroundColor(getResources().getColor(R.color.gray));
            fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
            fileName += "/AudioRecording.3gp";
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(fileName);
            try {
                recorder.prepare();
            } catch (IOException e) {
                Toast.makeText(this, "Fail to prepare media recorder", Toast.LENGTH_SHORT).show();
                Log.e("TAG", "prepare() failed" + e.getMessage());
            }
            recorder.start();
            statusTV.setText("Recording Started");
        } else {
            requestPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }


    public void playAudio() {
        stopRecordTV.setBackgroundColor(getResources().getColor(R.color.gray));
        startRecordTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        playRecordingTV.setBackgroundColor(getResources().getColor(R.color.gray));
        pauseRecordingTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileName);
            mediaPlayer.prepare();
            mediaPlayer.start();
            statusTV.setText("Recording Started Playing");
        } catch (IOException e) {
            Toast.makeText(this, "Fail to prepare media player", Toast.LENGTH_SHORT).show();
        }
    }

    public void pauseRecording() {
        stopRecordTV.setBackgroundColor(getResources().getColor(R.color.gray));
        startRecordTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        playRecordingTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        pauseRecordingTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        recorder.stop();
        recorder.release();
        recorder = null;
        statusTV.setText("Recording Stopped");
    }

    public void pausePlaying() {
        mediaPlayer.release();
        mediaPlayer = null;
        stopRecordTV.setBackgroundColor(getResources().getColor(R.color.gray));
        startRecordTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        playRecordingTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        pauseRecordingTV.setBackgroundColor(getResources().getColor(R.color.gray));
        statusTV.setText("Recording Play Stopped");
    }
}