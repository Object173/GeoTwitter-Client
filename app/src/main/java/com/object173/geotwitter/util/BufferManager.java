package com.object173.geotwitter.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

import com.object173.geotwitter.R;

import static android.content.Context.CLIPBOARD_SERVICE;

public final class BufferManager {

    public static void copyText(final Context context, final String text) {
        if(context == null || text == null) {
            return;
        }
        final ClipboardManager clipboardManager=(ClipboardManager)context.getSystemService(CLIPBOARD_SERVICE);
        final ClipData clipData = ClipData.newPlainText("text",text);
        clipboardManager.setPrimaryClip(clipData);

        Toast.makeText(context, R.string.message_text_copy, Toast.LENGTH_SHORT).show();
    }
}
