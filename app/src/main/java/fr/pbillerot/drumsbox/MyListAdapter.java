package fr.pbillerot.drumsbox;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import java.io.File;
import java.util.ArrayList;

public class MyListAdapter extends BaseAdapter {
    private static final String TAG = "MyListAdapter";
    private Context mContext;

    private ArrayList<File> mFiles = new ArrayList<>();
    private SharedPreferences mPrefs;

    // Constructor
    public MyListAdapter(Context c) {
        mContext = c;

        mPrefs = PreferenceManager.getDefaultSharedPreferences(c);
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
    }
    @Override
    public int getCount() {
        return mFiles == null ? 0 : mFiles.size();
    }

    @Override
    public Object getItem(int position) {
        return mFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.drums_item, parent, false);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        // Récup du File à afficher
        File file = (File)getItem(position);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.pathname.setText(file.getName());

        return convertView;
    }

    private class ViewHolder {
        TextView pathname;
        public ViewHolder(View view) {
            pathname = (TextView)view.findViewById(R.id.pathname);
        }
    }

}