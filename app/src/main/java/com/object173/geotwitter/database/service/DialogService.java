package com.object173.geotwitter.database.service;

import android.content.Context;
import android.database.Cursor;

import com.object173.geotwitter.database.DatabaseContract;
import com.object173.geotwitter.database.DatabaseHelper;
import com.object173.geotwitter.database.MyContentProvider;
import com.object173.geotwitter.database.entities.Dialog;
import com.object173.geotwitter.database.repository.DialogRepository;
import com.object173.geotwitter.server.json.DialogJson;

import java.util.ArrayList;
import java.util.List;

public final class DialogService {

    public static Cursor getDialogList(final Context context) {
        if(context == null) {
            return null;
        }
        return context.getContentResolver().query(MyContentProvider.DIALOG_CONTENT_URI, null, null, null, null);
    }

    public static List<Dialog> getEmptyDialogList(final Context context) {
        if(context == null) {
            return null;
        }
        try {
            final DatabaseHelper dbHelper = new DatabaseHelper(context);
            final List<Dialog> dialogList = DialogRepository.getAllEmpty(dbHelper.getReadableDatabase());
            dbHelper.close();
            return dialogList;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public static Dialog getDialog(final Context context, final long id) {
        if(context == null) {
            return null;
        }
        try {
            final DatabaseHelper dbHelper = new DatabaseHelper(context);
            final Dialog dialog = DialogRepository.findById(dbHelper.getReadableDatabase(), id);
            dbHelper.close();
            return dialog;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public static Dialog getCompanionDialog(final Context context, final long id) {
        if(context == null) {
            return null;
        }
        try {
            final DatabaseHelper dbHelper = new DatabaseHelper(context);
            final Dialog dialog = DialogRepository.findByCompanion(dbHelper.getReadableDatabase(), id);
            dbHelper.close();
            return dialog;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public static Dialog getDialog(final Context context, final Cursor cursor) {
        if(context == null || cursor == null) {
            return null;
        }
        try {
            final Dialog dialog = new Dialog(cursor);

            if(dialog.getCompanionId() > DatabaseContract.NULL_ID) {
                dialog.setCompanion(ProfileService.getProfile(context, dialog.getCompanionId()));
            }
            return dialog;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public static  boolean addDialog(final Context context, final DialogJson dialogJson) {
        if(context == null || dialogJson == null) {
            return false;
        }
        final List<DialogJson> dialogJsonList = new ArrayList<>();
        dialogJsonList.add(dialogJson);
        return addDialogs(context, dialogJsonList);
    }

    public static boolean invalidateDialog(final Context context, final long dialogId) {
        if(context == null) {
            return false;
        }
        try {
            final DatabaseHelper db = new DatabaseHelper(context);
            final Dialog dialog = DialogRepository.findById(db.getReadableDatabase(), dialogId);
            context.getContentResolver().update(MyContentProvider.DIALOG_CONTENT_URI, dialog.insert(),
                    DatabaseContract.DialogsTableScheme._ID + "=?",
                    new String[]{String.valueOf(dialog.getId())});
            db.close();
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    public static boolean addDialogs(final Context context, final List<DialogJson> dialogJsonList) {
        if(context == null || dialogJsonList == null) {
            return false;
        }
        try {
            final DatabaseHelper db = new DatabaseHelper(context);

            for(final DialogJson dialogJson: dialogJsonList) {
                final Dialog newDialog = new Dialog(dialogJson);
                final Dialog oldDialog = DialogRepository.findById(db.getReadableDatabase(), newDialog.getId());

                ProfileService.addProfile(context, dialogJson.getCompanion());
                MessageService.addMessages(context, dialogJson.getMessageList(), false);

                if (oldDialog == null) {
                    context.getContentResolver().insert(MyContentProvider.DIALOG_CONTENT_URI, newDialog.insert());
                }
                else {
                    context.getContentResolver().update(MyContentProvider.DIALOG_CONTENT_URI, newDialog.insert(),
                            DatabaseContract.DialogsTableScheme._ID + "=?",
                            new String[]{String.valueOf(oldDialog.getId())});
                }

            }

            db.close();
            return true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
