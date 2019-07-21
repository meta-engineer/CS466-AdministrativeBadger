package com.yys.cs446.es;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.yys.cs446.es.castle_model.controller;
import com.yys.cs446.es.castle_model.player;
import com.yys.cs446.es.castle_model.unit;
import com.yys.cs446.es.views.tileView;

import java.io.File;

import java.lang.reflect.Type;

public class GameActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private static final int REQUEST_CODE = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private MediaProjectionManager mMediaProjectionManager;
    private ScreenRecorder mRecorder;
    private String lastRecordedVideoPath;

    private tileView customView;
    private controller gameController;
    private MediaPlayer gameTheme;

    private boolean recordState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        lastRecordedVideoPath = "";
        // noinspection ResourceType
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        verifyStoragePermissions(this);

        customView = (tileView) findViewById(R.id.tileView);

        customView.buildDrawingCache();

        gameController = new controller(customView);
        gameController.start();

        gameTheme = MediaPlayer.create(GameActivity.this, R.raw.es_game_theme);
        gameTheme.setLooping(true);
        gameTheme.start();
    }

    public void popupMenuOptions(View v) {
        PopupMenu pu = new PopupMenu(this, v);
        pu.setOnMenuItemClickListener(this);
        pu.inflate(R.menu.options_menu);
        // must edit menu instance just before showing
        if (recordState) {
            pu.getMenu().findItem(R.id.options_2).setTitle("Stop current recording");
        } else {
            pu.getMenu().findItem(R.id.options_2).setTitle("Start new recording");
        }
        pu.show();
    }

    public void popupMenuGather(View v) {
        popupMenuType(v, R.menu.gather_menu);
    }

    public void popupMenuBuild(View v) {
        popupMenuType(v, R.menu.build_menu);
    }

    public void popupMenuExpand(View v) {
        popupMenuType(v, R.menu.expand_menu);
    }

    public void popupMenuDefend(View v) {
        popupMenuType(v, R.menu.defend_menu);
    }

    public void popupMenuType(View v, int menures) {
        PopupMenu pu = new PopupMenu(this, v);
        pu.setOnMenuItemClickListener(this);
        pu.inflate(menures);
        pu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.gather_1:
                Toast.makeText(getApplicationContext(), "Gathering lumber", Toast.LENGTH_SHORT).show();
                gameController.giveWorkersOrder(player.RESOURCES.LUMBER);
                break;
            case R.id.gather_2:
                Toast.makeText(getApplicationContext(), "Gathering food", Toast.LENGTH_SHORT).show();
                gameController.giveWorkersOrder(player.RESOURCES.FOOD);
                break;
            case R.id.gather_3:
                Toast.makeText(getApplicationContext(), "Gathering stone", Toast.LENGTH_SHORT).show();
                gameController.giveWorkersOrder(player.RESOURCES.STONE);
                break;
            case R.id.gather_4:
                Toast.makeText(getApplicationContext(), "Retreating Workers", Toast.LENGTH_SHORT).show();
                gameController.giveWorkersOrder(player.RESOURCES.NONE);
                break;
            case R.id.build_1:
                Toast.makeText(getApplicationContext(), "Start producing worker", Toast.LENGTH_SHORT).show();
                gameController.startBuildUnit(unit.TYPE.WORKER);
                break;
            case R.id.build_2:
                Toast.makeText(getApplicationContext(), "Start producing troop", Toast.LENGTH_SHORT).show();
                gameController.startBuildUnit(unit.TYPE.TROOP);
                break;
            case R.id.build_3:
                Toast.makeText(getApplicationContext(), "Start producing settler", Toast.LENGTH_SHORT).show();
                gameController.startBuildUnit(unit.TYPE.SETTLER);
                break;
            case R.id.expand_1:
                Toast.makeText(getApplicationContext(), "Select tile to expand to", Toast.LENGTH_SHORT).show();
                gameController.selectExpandTile();
                break;
            case R.id.expand_2:
                Toast.makeText(getApplicationContext(), "Toggle stronghold heal", Toast.LENGTH_SHORT).show();
                gameController.toggleHeal();
                break;
            case R.id.defend_1:
                Toast.makeText(getApplicationContext(), "Advancing troops", Toast.LENGTH_SHORT).show();
                gameController.selectDefendArea();
                break;
            case R.id.options_1:
                Toast.makeText(getApplicationContext(), "Toggle Pause", Toast.LENGTH_SHORT).show();
                gameController.togglePause();
                break;
            case R.id.options_2:
                // toggle options text on click
                if (mRecorder != null) {
                    Toast.makeText(getApplicationContext(), "Stopped recording", Toast.LENGTH_SHORT).show();
                    recordState = false;
                    mRecorder.quit();
                    lastRecordedVideoPath = mRecorder.getmDstPath();
                    mRecorder = null;
                } else {
                    Toast.makeText(this, "Screen recorder is running...", Toast.LENGTH_SHORT).show();
                    recordState = true;
                    Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
                    startActivityForResult(captureIntent, REQUEST_CODE);
                }
                break;
            case R.id.options_3:
                if (!lastRecordedVideoPath.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Sharing video...", Toast.LENGTH_SHORT).show();
                    // if game is not over/paused pause while sharing
                    if (gameController.getState() == controller.GAMESTATE.RUNNING) {
                        gameController.togglePause();
                    }
                    shareVideo();
                } else {
                    Toast.makeText(getApplicationContext(), "No completed video to share", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
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
