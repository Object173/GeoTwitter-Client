package com.object173.geotwitter.database.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.object173.geotwitter.database.DatabaseContract;
import com.object173.geotwitter.database.DatabaseHelper;
import com.object173.geotwitter.database.MyContentProvider;
import com.object173.geotwitter.database.entities.Image;
import com.object173.geotwitter.database.entities.Message;
import com.object173.geotwitter.database.repository.ImageRepository;
import com.object173.geotwitter.database.repository.MarkerRepository;
import com.object173.geotwitter.database.repository.MessageRepository;
import com.object173.geotwitter.server.json.MessageJson;
import com.object173.geotwitter.util.resources.CacheManager;

import java.util.ArrayList;
import java.util.List;

public final class MessageService {

    public static Message getLastMessage(final Context context, final long dialogId) {
        if(context == null) {
            return null;
        }
        try {
            final DatabaseHelper dbHelper = new DatabaseHelper(context);
            final Message message = MessageRepository.findLastByDialog(dbHelper.getReadableDatabase(), dialogId);
            dbHelper.close();
            return message;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public static Message getMessage(final Context context, final long localId) {
        if(context == null) {
            return null;
        }
        try {
            final DatabaseHelper dbHelper = new DatabaseHelper(context);
            final Message message = MessageRepository.findByLocalId(dbHelper.getReadableDatabase(), localId);
            dbHelper.close();
            return message;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public static Cursor getMessageList(final Context context, final long dialogId) {
        if(context == null) {
            return null;
        }
        return context.getContentResolver().query(MyContentProvider.MESSAGES_CONTENT_URI, null,
                DatabaseContract.MessagesTableScheme.COLUMN_DIALOG + " = ?",
                new String[] {String.valueOf(dialogId)},
                DatabaseContract.MessagesTableScheme.COLUMN_DATE + " DESC");
    }

    public static boolean updateMessage(final Context context, final Message message) {
        if(context == null || message == null) {
            return false;
        }
        try {
            final DatabaseHelper db = new DatabaseHelper(context);
            final boolean result = MessageRepository.update(db.getWritableDatabase(), message);
            db.close();
            return result;
        }
        catch (Exception ex) {
            return false;
        }
    }

    public static long addUserMessage(final Context context, final Message newMessage) {
        if(context == null || newMessage == null) {
            return DatabaseContract.NULL_ID;
        }
        try {
            final DatabaseHelper db = new DatabaseHelper(context);
            final Message oldMessage = MessageRepository.findByLocalId(db.getReadableDatabase(), newMessage.getLocalId());

            final long localId;
            if (oldMessage == null) {
                if(newMessage.getImage() != null) {
                    final long imageId = ImageService.addImage(context, newMessage.getImage());
                    newMessage.setImageId(imageId);
                }
                if(newMessage.getMarker() != null) {
                    final long markerId = MarkerRepository.insert(db.getWritableDatabase(), newMessage.getMarker());
                    newMessage.setMarkerId(markerId);
                }
                localId = MessageRepository.insert(db.getWritableDatabase(), newMessage);
                newMessage.setLocalId(localId);
            }
            else {
                if(newMessage.getImage() != null && oldMessage.getImage() != null) {
                    final Image image = oldMessage.getImage();
                    image.setLocalPath(CacheManager.createImagePath(image.getId()));
                    image.setOnlineUrl(newMessage.getImage().getOnlineUrl());
                    ImageRepository.update(db.getWritableDatabase(), image);
                    newMessage.setImageId(image.getId());
                }
                if(oldMessage.getMarker() != null) {
                    newMessage.setMarkerId(oldMessage.getMarkerId());
                }
                localId = oldMessage.getLocalId();
            }
            context.getContentResolver().update(MyContentProvider.MESSAGES_CONTENT_URI, newMessage.insert(),
                    DatabaseContract.MessagesTableScheme._ID + "=?",
                    new String[]{String.valueOf(localId)});

            db.close();
            return localId;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return DatabaseContract.NULL_ID;
        }
    }

    public static  boolean addMessage(final Context context, final MessageJson messageJson) {
        if(context == null || messageJson == null) {
            return false;
        }
        final List<MessageJson> messageJsonList = new ArrayList<>();
        messageJsonList.add(messageJson);
        return addMessages(context, messageJsonList, false);
    }

    public static boolean addMessages(final Context context, final List<MessageJson> messageJsonList,
                                      final boolean isNewMessages) {
        if(context == null || messageJsonList == null) {
            return false;
        }
        try {
            final DatabaseHelper db = new DatabaseHelper(context);

            for(final MessageJson messageJson: messageJsonList) {
                final Message newMessage = new Message(messageJson, isNewMessages);
                final Message oldMessage = MessageRepository.findByGlobalId(db.getReadableDatabase(), newMessage.getGlobalId());

                if (oldMessage == null) {
                    if(newMessage.getImage() != null) {
                        final long imageId = ImageService.addImage(context, newMessage.getImage());
                        newMessage.setImageId(imageId);
                    }
                    if(newMessage.getMarker() != null) {
                        final long markerId = MarkerRepository.insert(db.getWritableDatabase(), newMessage.getMarker());
                        newMessage.setMarkerId(markerId);
                    }
                    context.getContentResolver().insert(MyContentProvider.MESSAGES_CONTENT_URI, newMessage.insert());
                } else {
                    newMessage.setImageId(oldMessage.getImageId());
                    newMessage.setMarkerId(oldMessage.getMarkerId());

                    context.getContentResolver().update(MyContentProvider.MESSAGES_CONTENT_URI, newMessage.insert(),
                            DatabaseContract.MessagesTableScheme.COLUMN_GLOBAL_ID + "=?",
                            new String[]{String.valueOf(oldMessage.getGlobalId())});
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

    public static int getUnreadCount(final Context context, final long dialogId) {
        if(context == null) {
            return DatabaseContract.NULL_ID;
        }
        try {
            final DatabaseHelper dbHelper = new DatabaseHelper(context);
            final int result = MessageRepository.getUnreadCount(dbHelper.getReadableDatabase(), dialogId);
            dbHelper.close();
            return result;
        }
        catch (Exception ex) {
            return DatabaseContract.NULL_ID;
        }
    }

    public static Message getMessage(final Context context, final Cursor cursor) {
        if(context == null || cursor == null) {
            return null;
        }
        try {
            final Message message = new Message(cursor);

            message.setImage(ImageService.getImage(context, message.getImageId()));
            message.setMarker(MarkerService.getMarker(context, message.getMarkerId()));

            return message;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public static boolean setStatus(final Context context, final long messageId, final int status) {
        if(context == null) {
            return false;
        }
        try {
            final ContentValues values = new ContentValues();
            values.put(DatabaseContract.MessagesTableScheme.COLUMN_STATUS, status);

            context.getContentResolver().update(MyContentProvider.MESSAGES_CONTENT_URI, values,
                    DatabaseContract.MessagesTableScheme._ID + " =?", new String[] {String.valueOf(messageId)});
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    public static boolean cancelMessage(final Context context, final long messageId) {
        if(context == null) {
            return false;
        }
        try {
            final DatabaseHelper dbHelper = new DatabaseHelper(context);
            final Message message = MessageRepository.findByLocalId(dbHelper.getWritableDatabase(), messageId);

            if(message == null) {
                return false;
            }

            if(message.getImage() != null) {
                ImageService.removeImage(context, message.getImage().getId());
            }
            if(message.getMarker() != null) {
                MarkerRepository.remove(dbHelper.getWritableDatabase(), message.getMarker().getId());
            }

            context.getContentResolver().delete(MyContentProvider.MESSAGES_CONTENT_URI,
                    DatabaseContract.MessagesTableScheme._ID + " = ? AND " +
                            DatabaseContract.MessagesTableScheme.COLUMN_STATUS + " = ?",
                    new String[] {String.valueOf(messageId), String.valueOf(Message.STATUS_FAIL)});
            return true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
