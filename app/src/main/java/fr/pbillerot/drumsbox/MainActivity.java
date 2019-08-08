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
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener {
    private static final String TAG = "MainActivity";

    private SoundPool mSoundPool;
    int mStreamId = -1;
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

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // Lecture des médias
        ArrayList<File> files = new ArrayList<>();
        try {
            File folderMedia;
            if ( Environment.isExternalStorageEmulated() ) {
                // cas de l'émulateur d'Android Studio
                // /storage/emulated/0/Android/data/your.application.package.appname/files
                folderMedia = getExternalFilesDir(mPrefs.getString("pref_folder", ""));
            } else {
                folderMedia = Environment.getExternalStoragePublicDirectory(mPrefs.getString("pref_folder", ""));
            }
            File[] listFiles = folderMedia.listFiles();
            if ( listFiles != null ) {
                for (int i = 0; i < listFiles.length; i++) {
                    if (listFiles[i].isFile()) {
                        // On ne prend que les fichiers < 1Mo
                        long size = listFiles[i].length();
                        if (size < 1000000l) {
                            files.add(listFiles[i]);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }

        if ( files.size() > 0 ) {
            ListView listView = findViewById(R.id.list_view);
            MyListAdapter adapter = new MyListAdapter(this, files);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            SoundPool.Builder builder= new SoundPool.Builder();
            builder.setAudioAttributes(audioAttributes).setMaxStreams(1);

            mSoundPool = builder.build();

            // Remplissage du pool de sounds
            for( File file: files) {
                mSoundPool.load(file.getAbsolutePath(), 1);
            }

            // When Sound Pool load complete.
            this.mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    Log.d(TAG, "onLoadComplete of " + sampleId);
                }
            });

            Toast.makeText(getApplicationContext()
                    , "Drumbox Version "  + BuildConfig.VERSION_NAME

                    , Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext()
                    , "Drumbox - Files not found"
                    , Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if ( mStreamId != -1 ) {
            // Visuel : on supprime la surbrillance de la view du media en cours
            // ainsi que la ligne de la view qui vient d'être sélectionnée
            // -> au final aucune ligne n'est sélectionnée
            mViewCurrent.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            // On arrête le media en cours
            mSoundPool.stop(mStreamId);
            mStreamId = -1;
        } else {
            // Visuel : on met en surbrillance la ligne de la view sélectionnée
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            mViewCurrent = view;
            // On démarre le media
            // à noter que l'indice des éléments dans le pool commence à 1 (position+1 de la view)
            mStreamId = mSoundPool.play(position+1, 1, 1, 1, -1, 1f);
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

        // Démarrage de l'activité associé au menu
        if (id == R.id.action_settings) {
            Intent is = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(is);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();  // Always call the superclass method first
        // Arrêt du media en cours
        // suivi de la suppression du pool de media
        // le pool sera recré lors de la mise en avant plan de l'activité dans onStart()
        if ( mSoundPool != null ) {
            if (mStreamId != -1) {
                mSoundPool.stop(mStreamId + 1);
            }
            mSoundPool.release();
            mSoundPool = null;
        }
    }

}
