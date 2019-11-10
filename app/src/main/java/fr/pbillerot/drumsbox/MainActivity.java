package fr.pbillerot.drumsbox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

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

        try {
            // liste des fichiers dans assets
            String[] assetFiles = getAssets().list("");
            List<String> listFiles = new ArrayList<String>();
            // filtrage
            for ( String assetsFile: assetFiles ) {
                if ( assetsFile.endsWith(".wav")) {
                    listFiles.add(assetsFile);
                }
            }

            ListView listView = findViewById(R.id.list_view);
            MyListAdapter adapter = new MyListAdapter(this, listFiles);
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
            for( String fileName: listFiles) {
                mSoundPool.load(getAssets().openFd(fileName), 1);
            }

            // When Sound Pool load complete.
            this.mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    Log.d(TAG, "onLoadComplete of " + sampleId);
                }
            });

//            Toast.makeText(getApplicationContext()
//                    , "Drumbox Version "  + BuildConfig.VERSION_NAME
//                    , Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext()
                    , "Drumbox Version "  + BuildConfig.VERSION_NAME
                    , Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
            Toast.makeText(getApplicationContext()
                    , "Drumbox - Files not found"
                    , Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if ( mViewCurrent == view ) {
            // Visuel : on supprime la surbrillance de la view du media en cours
            // -> aucune ligne n'est sélectionnée
            mViewCurrent.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            // On arrête le media en cours
            mSoundPool.stop(mStreamId);
            mStreamId = -1;
            mViewCurrent = null;
        } else {
            if ( mStreamId != -1 ) {
                // Visuel : on supprime la surbrillance de la view du media en cours
                mViewCurrent.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                // On arrête le media en cours
                mSoundPool.stop(mStreamId);
            }
            // Visuel : on met en surbrillance la ligne de la view sélectionnée
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            mViewCurrent = view;
            // On démarre le media
            // à noter que l'indice des éléments dans le pool commence à 1 (position+1 de la view)
            mStreamId = mSoundPool.play(position+1, 1, 1, 1, -1, 1f);
        }
//        if ( mStreamId != -1 ) {
//            // Visuel : on supprime la surbrillance de la view du media en cours
//            // ainsi que la ligne de la view qui vient d'être sélectionnée
//            // -> au final aucune ligne n'est sélectionnée
//            mViewCurrent.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
//            view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
//            // On arrête le media en cours
//            mSoundPool.stop(mStreamId);
//            mStreamId = -1;
//        } else {
//            // Visuel : on met en surbrillance la ligne de la view sélectionnée
//            view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
//            mViewCurrent = view;
//            // On démarre le media
//            // à noter que l'indice des éléments dans le pool commence à 1 (position+1 de la view)
//            mStreamId = mSoundPool.play(position+1, 1, 1, 1, -1, 1f);
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
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
