package nanodegree.damian.runny.firebase.data;

import com.google.firebase.database.DataSnapshot;

import static nanodegree.damian.runny.firebase.FirebaseConstants.KEY_NAME;
import static nanodegree.damian.runny.firebase.FirebaseConstants.KEY_PHOTO_URL;

/**
 * Created by robert_damian on 02.08.2018.
 */

public class FirebaseRegisteredUser {

    public final String name;
    public final String photoURL;
    public final String userid;

    public FirebaseRegisteredUser(String name, String photoURL, String userid) {
        this.name = name;
        this.photoURL = photoURL;
        this.userid = userid;
    }

    public static FirebaseRegisteredUser fromDataSnapshot(DataSnapshot userSnapshot) {
        return new FirebaseRegisteredUser(
                userSnapshot.child(KEY_NAME).getValue().toString(),
                userSnapshot.child(KEY_PHOTO_URL).getValue().toString(),
                userSnapshot.getKey());
    }
}
