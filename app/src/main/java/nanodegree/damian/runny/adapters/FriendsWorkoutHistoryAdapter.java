package nanodegree.damian.runny.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import nanodegree.damian.runny.R;
import nanodegree.damian.runny.firebase.data.FriendWorkoutSession;
import nanodegree.damian.runny.utils.Basics;

/**
 * Created by robert_damian on 02.08.2018.
 */

public class FriendsWorkoutHistoryAdapter extends
        RecyclerView.Adapter<FriendsWorkoutHistoryAdapter.FriendWorkoutHistoryViewHolder>{

    private Context mContext;
    private List<FriendWorkoutSession> mFriendsWorkoutList;

    public FriendsWorkoutHistoryAdapter(Context context,
                                        List<FriendWorkoutSession> friendsWorkoutSessionList) {
        mContext = context;
        mFriendsWorkoutList = friendsWorkoutSessionList;
    }

    public void emptyFriendsWorkoutList() {
        mFriendsWorkoutList = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addFriendsWorkout(FriendWorkoutSession friendWorkoutSession) {
        if (mFriendsWorkoutList == null) {
            emptyFriendsWorkoutList();
        }

        mFriendsWorkoutList.add(friendWorkoutSession);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FriendWorkoutHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View resultView = LayoutInflater.from(mContext)
                .inflate(R.layout.item_friend_stats, parent, false);

        return new FriendWorkoutHistoryViewHolder(resultView);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendWorkoutHistoryViewHolder holder, int position) {
        FriendWorkoutSession friendWorkout = mFriendsWorkoutList.get(position);

        Picasso.get().load(friendWorkout.friend.photoURL).into(holder.profileImageView);
        holder.nameTextView.setText(friendWorkout.friend.name);
        holder.entryTitleTextView.setText(mContext.getResources()
                .getString(R.string.run_label, Basics.formatCalendar(friendWorkout.startTime)));
        holder.distanceTextView.setText(friendWorkout.distance);
        holder.timeTextView.setText(friendWorkout.time);
    }

    @Override
    public int getItemCount() {
        if (mFriendsWorkoutList == null) {
            return 0;
        }

        return mFriendsWorkoutList.size();
    }

    class FriendWorkoutHistoryViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.civ_profile_image)
        CircleImageView profileImageView;

        @BindView(R.id.tv_name)
        TextView nameTextView;

        @BindView(R.id.tv_entry_title)
        TextView entryTitleTextView;

        @BindView(R.id.tv_value_distance)
        TextView distanceTextView;

        @BindView(R.id.tv_value_time)
        TextView timeTextView;

        FriendWorkoutHistoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
