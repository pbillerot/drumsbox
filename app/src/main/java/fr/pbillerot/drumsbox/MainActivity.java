package fr.pbillerot.drumsbox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private MyListAdapter mAdapter;

    private MediaPlayer mPlayer;
    private SharedPreferences mPrefs;

    private File mFileCurrent;
    private View mViewCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        mPlayer = new MediaPlayer();
        mPlayer.setLooping(true);

    }

    @Override
    protected void onStart() {
        super.onStart();  // Always call the superclass method first
        // Activity being restarted from stopped state

        mListView = findViewById(R.id.list_view);
        mAdapter = new MyListAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if ( mPlayer.isPlaying()) {
            mPlayer.pause();
            mViewCurrent.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        } else {
            File file = (File) parent.getItemAtPosition(position);
            if (file == mFileCurrent) {
                mPlayer.start();
            } else {
                mPlayer.reset();
                mViewCurrent = view;
                playMedia(file);
            }
            mViewCurrent = view;
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }

    private void playMedia(File mediaFile) {
        try {
            mPlayer.setDataSource(mediaFile.getAbsolutePath());
            mPlayer.setLooping(true);
            mPlayer.prepare();
            mPlayer.start();
            mFileCurrent = mediaFile;
        } catch (Exception e) {
            Log.d(this.getLocalClassName(), e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent is = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(is);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
