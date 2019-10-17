package ai.tomorrow.codechallenge_kotlin

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class SettingDialogFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_setting)
    }


}