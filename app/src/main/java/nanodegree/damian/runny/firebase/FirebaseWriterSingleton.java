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

import nanodegree.damian.runny.persistence.data.WorkoutSession;
import nanodegree.damian.runny.persistence.database.converters.CalendarConverter;

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

    public void writeUserInfo(@NonNull  final GoogleSignInAccount account) {
        final String accountId = mUser.getUid();

        final DatabaseReference usersReference = mDatabaseReference.child("users").child(accountId);
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    return ;
                }

                Map<String, String> userAsKeyValue = new HashMap<>();
                userAsKeyValue.put("email", account.getEmail());
                userAsKeyValue.put("name", account.getDisplayName());
                userAsKeyValue.put("photoUrl", account.getPhotoUrl() == null ? null :
                        account.getPhotoUrl().toString());
                usersReference.setValue(userAsKeyValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void writeWorkoutSession(@NonNull WorkoutSession session) {
        DatabaseReference runReference = mDatabaseReference.child("workouts").push();
        Map<String, String> runAsKeyValue = new HashMap<>();
        runAsKeyValue.put("startTime", CalendarConverter.toTimestamp(session.getStartTime()).toString());
        runAsKeyValue.put("distance", session.getFormattedDistanceValue());
        runAsKeyValue.put("time", session.getFormattedTimeValue(false));
        runAsKeyValue.put("user", mUser.getUid());

        runReference.setValue(runAsKeyValue);
    }
}
