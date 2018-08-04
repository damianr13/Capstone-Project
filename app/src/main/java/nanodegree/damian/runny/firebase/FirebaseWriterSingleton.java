package nanodegree.damian.runny.firebase;

import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import nanodegree.damian.runny.firebase.data.FirebaseRegisteredUser;
import nanodegree.damian.runny.persistence.data.WorkoutSession;
import nanodegree.damian.runny.persistence.database.converters.CalendarConverter;

import static nanodegree.damian.runny.firebase.FirebaseConstants.KEY_DISTANCE;
import static nanodegree.damian.runny.firebase.FirebaseConstants.KEY_EMAIL;
import static nanodegree.damian.runny.firebase.FirebaseConstants.KEY_FRIENDSHIPS;
import static nanodegree.damian.runny.firebase.FirebaseConstants.KEY_NAME;
import static nanodegree.damian.runny.firebase.FirebaseConstants.KEY_PHOTO_URL;
import static nanodegree.damian.runny.firebase.FirebaseConstants.KEY_START_TIME;
import static nanodegree.damian.runny.firebase.FirebaseConstants.KEY_TIME;
import static nanodegree.damian.runny.firebase.FirebaseConstants.KEY_USERS;

/**
 * Created by robert_damian on 01.08.2018.
 */

public class FirebaseWriterSingleton {
    private static final Object LOCK = new Object();

    private static FirebaseWriterSingleton INSTANCE;

    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;

    private FirebaseWriterSingleton() {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public static FirebaseWriterSingleton getInstance() {
        synchronized (LOCK) {
            if (INSTANCE == null) {
                INSTANCE = new FirebaseWriterSingleton();
            }

            return INSTANCE;
        }
    }

    public void writeUserInfo(@NonNull final GoogleSignInAccount account) {
        if (mUser == null) {
            return ;
        }

        final String accountId = mUser.getUid();

        final DatabaseReference usersReference = mDatabaseReference.child(KEY_USERS).child(accountId);
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    return ;
                }

                Map<String, String> userAsKeyValue = new HashMap<>();
                userAsKeyValue.put(KEY_EMAIL, account.getEmail());
                userAsKeyValue.put(KEY_NAME, account.getDisplayName());
                userAsKeyValue.put(KEY_PHOTO_URL, account.getPhotoUrl() == null ? null :
                        account.getPhotoUrl().toString());
                usersReference.setValue(userAsKeyValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void writeWorkoutSession(@NonNull WorkoutSession session) {
        if (mUser == null) {
            return ;
        }

        DatabaseReference runReference = mDatabaseReference.child(mUser.getUid()).push();
        Map<String, String> runAsKeyValue = new HashMap<>();
        runAsKeyValue.put(KEY_START_TIME, CalendarConverter.toTimestamp(session.getStartTime()).toString());
        runAsKeyValue.put(KEY_DISTANCE, session.getFormattedDistanceValue());
        runAsKeyValue.put(KEY_TIME, session.getFormattedTimeValue(false));


        runReference.setValue(runAsKeyValue);
    }

    public void addFriend(@NonNull FirebaseRegisteredUser otherUser) {
        if (mUser == null) {
            return ;
        }

        DatabaseReference newFriendshipReference = mDatabaseReference.child(KEY_FRIENDSHIPS)
                .child(mUser.getUid()).push();
        newFriendshipReference.setValue(otherUser.userid);
    }
}
