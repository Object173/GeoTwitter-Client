package com.object173.geotwitter.gui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

public class DeleteAvatarActivity extends AppCompatActivity {

    public static final int REQUEST_ACTIVITY = 222;
    public static final int RESULT_ACTIVITY = 222;

    private static final String KEY_PATH = "path";
    private DeleteTask task;

    public static boolean startActivityForResult(final Activity activity, final String path) {
        if(activity == null || path == null) {
            return false;
        }
        final Intent intent = new Intent(activity, DeleteAvatarActivity.class);
        intent.putExtra(KEY_PATH, path);
        activity.startActivityForResult(intent, REQUEST_ACTIVITY);
        return true;
    }

    public static Intent getStartIntent(final Activity activity, final String path) {
        if(activity == null || path == null) {
            return null;
        }
        final Intent intent = new Intent(activity, DeleteAvatarActivity.class);
        intent.putExtra(KEY_PATH, path);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        task = (DeleteTask) getLastNonConfigurationInstance();
        if (task == null) {
            final Bundle args = getIntent().getExtras();
            final String path = args.getString(KEY_PATH);

            task = new DeleteTask();
            task.execute(path);
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return task;
    }

    private void finishActivity() {
        setResult(RESULT_ACTIVITY);
        finish();
    }

    class DeleteTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            if(strings != null && strings.length > 0) {
                final String path = Uri.parse(strings[0]).getPath();
                final File avatar = new File(path);
                avatar.delete();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finishActivity();
        }
    }
}
