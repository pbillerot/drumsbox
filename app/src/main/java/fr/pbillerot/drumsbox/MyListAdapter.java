package fr.pbillerot.drumsbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;

public class MyListAdapter extends ArrayAdapter<File> {
    private static final String TAG = "MyListAdapter";

    // Constructor
    public MyListAdapter(Context context, ArrayList<File> files) {
        super(context, 0, files);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        File file = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.drums_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        // Lookup view for data population
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        // Populate the data into the template view using the data object
        viewHolder.pathname.setText(file.getName());
        // Return the completed view to render on screen
        return convertView;
    }

    private class ViewHolder {
        TextView pathname;
        public ViewHolder(View view) {
            pathname = (TextView)view.findViewById(R.id.pathname);
        }
    }

}