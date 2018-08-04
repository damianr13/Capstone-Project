package nanodegree.damian.runny.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nanodegree.damian.runny.R;
import nanodegree.damian.runny.adapters.FriendsWorkoutHistoryAdapter;
import nanodegree.damian.runny.firebase.FirebaseReaderSingleton;
import nanodegree.damian.runny.firebase.data.FirebaseRegisteredUser;
import nanodegree.damian.runny.firebase.data.FriendWorkoutSession;

import static nanodegree.damian.runny.firebase.FirebaseConstants.KEY_DISTANCE;
import static nanodegree.damian.runny.firebase.FirebaseConstants.KEY_START_TIME;
import static nanodegree.damian.runny.firebase.FirebaseConstants.KEY_TIME;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private OnFriendsFragmentInteractionListener mListener;

    @BindView(R.id.rv_friends_history)
    RecyclerView friendsHistoryRecyclerView;

    private FriendsWorkoutHistoryAdapter mAdapter;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_friends, container, false);
        // Inflate the layout for this fragment
        ButterKnife.bind(this, result);

        mAdapter = new FriendsWorkoutHistoryAdapter(
                getActivity(), null);
        friendsHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendsHistoryRecyclerView.setAdapter(mAdapter);

        return result;
    }

    public void loadFriendsWorkouts() {
        mAdapter.emptyFriendsWorkoutList();
        FirebaseReaderSingleton.getInstance().queryFriends(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                    addWorkoutsForFriendWithId(friendSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addWorkoutsForFriendWithId(String friendId) {
        FirebaseReaderSingleton.getInstance().queryUser(friendId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                addWorkoutsForFriend(FirebaseRegisteredUser.fromDataSnapshot(dataSnapshot));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addWorkoutsForFriend(FirebaseRegisteredUser friend) {
        FirebaseReaderSingleton.getInstance().queryFriendsWorkouts(friend.userid,
            new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot workoutSnapshots : dataSnapshot.getChildren()) {
                        mAdapter.addFriendsWorkout(new FriendWorkoutSession(friend,
                                workoutSnapshots.child(KEY_TIME).getValue().toString(),
                                workoutSnapshots.child(KEY_DISTANCE).getValue().toString(),
                                Long.valueOf(workoutSnapshots.child(KEY_START_TIME).getValue().toString())));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFriendsFragmentInteractionListener) {
            mListener = (OnFriendsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFriendsFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick(R.id.iv_add_friend)
    public void onClickAddFriend(View v) {
        mListener.onAddFriendClicked();
    }

    public interface OnFriendsFragmentInteractionListener {
        void onAddFriendClicked();
    }
}
