package com.object173.geotwitter.gui.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.object173.geotwitter.util.resources.ImagesManager;

public class DeleteImageActivity extends AppCompatActivity {

    public static final int RESULT_ACTIVITY = 222;

    private static final String KEY_PATH = "path";
    private DeleteTask task;

    public static Intent getStartIntent(final Activity activity, final String path) {
        if(activity == null) {
            return null;
        }
        final Intent intent = new Intent(activity, DeleteImageActivity.class);
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

            if(path == null) {
                finishActivity();
            }
            else {
                task = new DeleteTask();
                task.execute(path);
            }
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

    private class DeleteTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            if(strings != null && strings.length > 0) {
                final String path = Uri.parse(strings[0]).getPath();
                ImagesManager.deleteImage(path);
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
