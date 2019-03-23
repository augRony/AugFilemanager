package com.augustro.filemanager.activities.superclasses;

import android.support.v7.app.AppCompatActivity;

import com.augustro.filemanager.ui.colors.ColorPreferenceHelper;
import com.augustro.filemanager.utils.application.AppConfig;
import com.augustro.filemanager.utils.provider.UtilitiesProvider;
import com.augustro.filemanager.utils.theme.AppTheme;

/**
 * Created by rpiotaix on 17/10/16.
 */
public class BasicActivity extends AppCompatActivity {

    protected AppConfig getAppConfig() {
        return (AppConfig) getApplication();
    }

    public ColorPreferenceHelper getColorPreference() {
        return getAppConfig().getUtilsProvider().getColorPreference();
    }

    public AppTheme getAppTheme() {
        return getAppConfig().getUtilsProvider().getAppTheme();
    }

    public UtilitiesProvider getUtilsProvider() {
        return getAppConfig().getUtilsProvider();
    }
}
