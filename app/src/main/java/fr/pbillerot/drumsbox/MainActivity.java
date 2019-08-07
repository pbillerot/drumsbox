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
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener {
    private static final String TAG = "MainActivity";

    private ArrayList<File> mFiles = new ArrayList<>();

    private SharedPreferences mPrefs;

    private ListView mListView;
    private MyListAdapter mAdapter;

    private SoundPool mSoundPool;
    private int mCurrentPlayingPosition = -1;
    private View mViewCurrent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // permission
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();  // Always call the superclass method first
        // Activity being restarted from stopped state

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // Lecture des médias
        String path = Environment.getExternalStorageDirectory() + mPrefs.getString("pref_folder", "");
        try {
            File root = new File(path);
            File[] listFiles = root.listFiles();
            for (int i=0; i<listFiles.length; i++) {
                if ( listFiles[i].isFile() ) {
                    mFiles.add(listFiles[i]);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }

        mListView = findViewById(R.id.list_view);
        mAdapter = new MyListAdapter(this, mFiles);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        SoundPool.Builder builder= new SoundPool.Builder();
        builder.setAudioAttributes(audioAttributes).setMaxStreams(1);

        mSoundPool = builder.build();

        for( File file: mFiles) {
            mSoundPool.load(file.getAbsolutePath(), 1);
        }

        // When Sound Pool load complete.
        this.mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Log.d(TAG, "onLoadComplete");
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if ( mCurrentPlayingPosition != -1 ) {
            mViewCurrent.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            mSoundPool.stop(mCurrentPlayingPosition+1);
            mCurrentPlayingPosition = -1;
        } else {
            mViewCurrent = view;
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

            mSoundPool.play(position+1, 1, 1, 1, -1, 1f);
            mCurrentPlayingPosition = position;
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

    @Override
    protected void onResume() {
        super.onResume();  // Always call the superclass method first

    }

    @Override
    protected void onStop() {
        super.onStop();  // Always call the superclass method first
        if ( mCurrentPlayingPosition != -1) {
            mSoundPool.stop(mCurrentPlayingPosition+1);
        }
        mSoundPool.release();
        mSoundPool = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();  // Always call the superclass method first

    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first
    }

}
