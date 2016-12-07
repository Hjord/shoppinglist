package net.hjord.shoppinglist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

/**
 * Created by Hjord on 07/12/2016.
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_settings);


        final Preference colorPref = findPreference("colorPref");
        colorPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

                int color = (int)Long.parseLong(prefs.getString("colorPref", "000"), 16);

                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = (color) & 0xFF;

                final ColorPicker cp = new ColorPicker(getActivity(), r, g, b);

                cp.show();

                Button okColor = (Button) cp.findViewById(R.id.okColorButton);
                okColor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String hexColor = Integer.toHexString(cp.getColor());
                        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();

                        prefs.putString("colorPref", hexColor);
                        prefs.apply();
                        cp.dismiss();

                    }
                });


                return false;
            }
        });

    }
}
