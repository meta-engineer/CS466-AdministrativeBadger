package com.example.y47bai.test;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.net.Uri;

import java.io.File;


public class MainActivity extends Activity implements View.OnClickListener {
    private static final int REQUEST_CODE = 1;
    private MediaProjectionManager mMediaProjectionManager;
    private ScreenRecorder mRecorder;
    private String lastRecordedVideoPath;
    private Button mButton;
    private Button mButtonShare;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = (Button) findViewById(R.id.button);
        mButtonShare = (Button) findViewById(R.id.button2);
        mButton.setOnClickListener(this);
        mButtonShare.setOnClickListener(this);
        lastRecordedVideoPath = "";
        //noinspection ResourceType
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        verifyStoragePermissions(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
        if (mediaProjection == null) {
            Log.e("@@", "media projection is null");
            return;
        }
        // video size
        final int width = 1280;
        final int height = 720;
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "record-" + width + "x" + height + "-" + System.currentTimeMillis() + ".mp4");
        final int bitrate = 6000000;
        mRecorder = new ScreenRecorder(width, height, bitrate, 1, mediaProjection, file.getAbsolutePath());
        mRecorder.start();
        mButton.setText("Stop Recorder");
        Toast.makeText(this, "Screen recorder is running...", Toast.LENGTH_SHORT).show();
    }


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                if (mRecorder != null) {
                    mRecorder.quit();
                    lastRecordedVideoPath = mRecorder.getmDstPath();
                    mRecorder = null;
                    mButton.setText("Restart recorder");
                } else {
                    Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
                    startActivityForResult(captureIntent, REQUEST_CODE);
                }
                break;
            case R.id.button2:
                if (!lastRecordedVideoPath.isEmpty()) {
                    System.out.println("trying to share the video");
                    shareVideo();
                    mButtonShare.setText("Share Finished");
                }
                break;
        }
    }

    private void shareVideo() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("video/*");
        File videoFileToShare = new File(lastRecordedVideoPath);
        Uri uri = GenericFileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", videoFileToShare);
        shareIntent.putExtra( android.content.Intent.EXTRA_SUBJECT, "Share to");
        shareIntent.putExtra( android.content.Intent.EXTRA_TITLE, "Share to");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(shareIntent, "Share to"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRecorder != null) {
            mRecorder.quit();
            mRecorder = null;
        }
    }


}
