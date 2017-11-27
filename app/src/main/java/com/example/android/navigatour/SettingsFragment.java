package com.example.android.navigatour;


import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }



//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_settings, container, false);
//    }

}
