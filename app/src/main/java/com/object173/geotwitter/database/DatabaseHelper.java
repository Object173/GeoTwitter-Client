package com.object173.geotwitter.database;

/**
 * Created by Object173
 * on 26.04.2017.
 */

public final class DatabaseHelper {

    /*public static void writeUser(final User user, final String userId) {
        if(user == null || userId == null) {
            return;
        }
        if(user.getAvatar() == null) {
            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child(User.TABLE_NAME).child(userId).setValue(user);
        }
        else {
            final FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
            final StorageReference reference = mFirebaseStorage.getReference();

            final Uri file = Uri.parse(user.getAvatar());
            final StorageReference avatarRef = reference.child(
                    DatabaseContract.getReferenceImages(AvatarManager.getFilenameAvatar(user.getLogin())));

            final UploadTask uploadTask = avatarRef.putFile(file);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    user.setAvatar(null);
                    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child(User.TABLE_NAME).child(userId).setValue(user);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    user.setAvatar(taskSnapshot.getDownloadUrl().toString());
                    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child(User.TABLE_NAME).child(userId).setValue(user);
                }
            });
        }
    }

    public static void isUserExists(final String login, final ValueEventListener listener) {
        if(login == null || listener == null) {
            return;
        }
        final Query query = FirebaseDatabase.getInstance().getReference()
                .child(User.TABLE_NAME).orderByChild(User.FIELD_LOGIN).equalTo(login);
        query.addListenerForSingleValueEvent(listener);
    }

    public static boolean readUser(final String userId, final ChildEventListener listener) {
        if(userId == null || listener == null) {
            return false;
        }
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child(User.TABLE_NAME);
        mDatabase.addChildEventListener(listener);

        return true;
    }*/
}
