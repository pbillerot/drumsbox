package fr.pbillerot.drumsbox;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.preference.PreferenceManager;

import java.io.File;

public class MyListAdapter extends BaseAdapter {
    private Context mContext;

    private File[] mFiles;
    private SharedPreferences mPrefs;

    // Constructor
    public MyListAdapter(Context c) {
        mContext = c;

        mPrefs = PreferenceManager.getDefaultSharedPreferences(c);
        // Lecture des médias
        String path = Environment.getExternalStorageDirectory() + mPrefs.getString("pref_folder", "");
        File root = new File(path);

        mFiles = root.listFiles();
    }
    @Override
    public int getCount() {
        return mFiles.length;
    }

    @Override
    public Object getItem(int position) {
        return mFiles[position];
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