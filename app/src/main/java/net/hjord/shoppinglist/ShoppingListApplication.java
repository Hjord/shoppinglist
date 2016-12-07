package net.hjord.shoppinglist;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Hjord on 17/11/2016.
 */

public class ShoppingListApplication extends Application {

    private static final String TAG = "Shopping Application";

    @Override
    public void onCreate() {
        super.onCreate();
        if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            Log.d(TAG, "Firebase: Persistence Enabled");
        } else {
            Log.d(TAG, "Firebase: Persistence ! Enabled");
        }
    }

}
