package com.solveit.sdnu;

import android.os.Bundle;
import androidx.preference.PreferenceFragment;

/**
 */
public class Setting extends PreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle bundle, String tem){

    }

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.prefer_config);
    }

}
