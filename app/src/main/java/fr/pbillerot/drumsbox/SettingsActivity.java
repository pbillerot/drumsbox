package fr.pbillerot.drumsbox;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
//        ListPreference listPreferenceCategory = (ListPreference) findPreference("default_category");
//        if (listPreferenceCategory != null) {
//            ArrayList<Locale.Category> categoryList = getCategories();
//            CharSequence entries[] = new String[categoryList.size()];
//            CharSequence entryValues[] = new String[categoryList.size()];
//            int i = 0;
//            for (Category category : categoryList) {
//                entries[i] = category.getCategoryName();
//                entryValues[i] = Integer.toString(i);
//                i++;
//            }
//            listPreferenceCategory.setEntries(entries);
//            listPreferenceCategory.setEntryValues(entryValues);
//        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

}