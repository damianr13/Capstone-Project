package nanodegree.damian.runny;

import android.app.SearchManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nanodegree.damian.runny.adapters.FirebaseRegisteredUserAdapter;
import nanodegree.damian.runny.firebase.FirebaseReaderSingleton;
import nanodegree.damian.runny.firebase.data.FirebaseRegisteredUser;
import nanodegree.damian.runny.utils.Basics;

import static nanodegree.damian.runny.firebase.FirebaseConstants.KEY_NAME;
import static nanodegree.damian.runny.firebase.FirebaseConstants.KEY_PHOTO_URL;

public class FriendSearchActivity extends AppCompatActivity implements
        FirebaseRegisteredUserAdapter.OnFirebaseRegisteredUserInteractionListener{

    @BindView(R.id.rv_users)
    RecyclerView mUsersRecyclerView;

    private FirebaseRegisteredUserAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_search);
        ButterKnife.bind(this);

        mAdapter = new FirebaseRegisteredUserAdapter(this, this,null);
        mUsersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUsersRecyclerView.setAdapter(mAdapter);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            performSearch(getIntent().getStringExtra(SearchManager.QUERY));
        }
    }

    private void performSearch(String query) {
        FirebaseReaderSingleton.getInstance().queryUsers(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<FirebaseRegisteredUser> userList = new ArrayList<>();
                Iterable<DataSnapshot> allUsersSnapshot = dataSnapshot.getChildren();
                for (DataSnapshot userSnapshot : allUsersSnapshot) {
                    boolean match = userSnapshot.child(KEY_NAME).getValue().toString()
                            .toLowerCase().contains(query.toLowerCase());
                    if (match) {
                        userList.add(FirebaseRegisteredUser.fromDataSnapshot(userSnapshot));
                    }
                }

                mAdapter.swapUserList(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void userSelected() {
        onBackPressed();
    }
}
