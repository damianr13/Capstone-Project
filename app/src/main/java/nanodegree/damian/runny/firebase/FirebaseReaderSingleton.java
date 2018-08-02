package nanodegree.damian.runny.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static nanodegree.damian.runny.firebase.FirebaseConstants.*;

/**
 * Created by robert_damian on 02.08.2018.
 */

public class FirebaseReaderSingleton {

    private static FirebaseReaderSingleton INSTANCE;
    private static final Object LOCK = new Object();

    public static FirebaseReaderSingleton getInstance() {
        synchronized (LOCK) {
            if (INSTANCE == null) {
                INSTANCE = new FirebaseReaderSingleton();
            }

            return INSTANCE;
        }
    }

    private final FirebaseUser mUser;
    private final DatabaseReference mDatabaseReference;

    private FirebaseReaderSingleton() {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void queryUsers(ValueEventListener valueEventListener) {
        mDatabaseReference.child(KEY_USERS)
                .addListenerForSingleValueEvent(valueEventListener);
    }

    public void queryUser(String userid, ValueEventListener valueEventListener) {
        mDatabaseReference.child(KEY_USERS).child(userid)
                .addListenerForSingleValueEvent(valueEventListener);
    }

    public void queryWorkouts(ValueEventListener valueEventListener) {
        mDatabaseReference.child(KEY_WORKOUTS)
                .addListenerForSingleValueEvent(valueEventListener);
    }

    public void queryFriends(ValueEventListener valueEventListener) {
        mDatabaseReference.child(KEY_FRIENDSHIPS).child(mUser.getUid())
                .addValueEventListener(valueEventListener);
    }

    public void queryFriendsWorkouts(String friendsUid, ValueEventListener valueEventListener) {
        mDatabaseReference.child(KEY_WORKOUTS).child(friendsUid)
                .addValueEventListener(valueEventListener);
    }
}
