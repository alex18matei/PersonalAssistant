package matei.personalassistant;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsPrefferenceActivity extends PreferenceActivity {

    SharedPreferences.OnSharedPreferenceChangeListener spChanged = new
            SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                      String key) {
                    // your stuff here
                    isFirstTime();
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        getPreferenceManager().setSharedPreferencesName("my_prefs");
//        getPreferenceManager().getSharedPreferences();
        addPreferencesFromResource(R.xml.activity_preference);
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(spChanged);
        isFirstTime();
    }

    public void isFirstTime() {
        final SharedPreferences sharedPref = getPreferenceManager().getSharedPreferences();
        String location =
                sharedPref.getString("trigger_command", "Hello Jimmy");
        getPreferenceManager().findPreference("trigger_command").setSummary(location);

        String text_for_general_command =
                sharedPref.getString("general_user_command", "");
        getPreferenceManager().findPreference("general_user_command")
                .setSummary(text_for_general_command);

        String general_command =
                sharedPref.getString("general_command", "");
        getPreferenceManager().findPreference("general_command")
                .setSummary(general_command);
    }
}
