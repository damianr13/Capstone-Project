package nanodegree.damian.runny.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import nanodegree.damian.runny.R;
import nanodegree.damian.runny.firebase.FirebaseWriterSingleton;
import nanodegree.damian.runny.firebase.data.FirebaseRegisteredUser;

/**
 * Created by robert_damian on 02.08.2018.
 */

public class FirebaseRegisteredUserAdapter extends
        RecyclerView.Adapter<FirebaseRegisteredUserAdapter.FirebaseRegisteredUserViewHolder>{

    private List<FirebaseRegisteredUser> mUserList;
    private Context mContext;
    private OnFirebaseRegisteredUserInteractionListener mUserInteractionListener;

    public FirebaseRegisteredUserAdapter(Context context,
                                         OnFirebaseRegisteredUserInteractionListener userListener,
                                         List<FirebaseRegisteredUser> userList) {
        mContext = context;
        mUserList = userList;
        mUserInteractionListener = userListener;
    }

    public void swapUserList(List<FirebaseRegisteredUser> userList) {
        mUserList = userList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FirebaseRegisteredUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View resultView = LayoutInflater.from(mContext)
                .inflate(R.layout.item_user_info, parent, false);

        return new FirebaseRegisteredUserViewHolder(resultView);
    }

    @Override
    public void onBindViewHolder(@NonNull FirebaseRegisteredUserViewHolder holder, int position) {
        FirebaseRegisteredUser user = mUserList.get(position);

        Picasso.get().load(user.photoURL).into(holder.mProfileImageView);
        holder.mNameTextView.setText(user.name);
        holder.mUidTextView.setText(user.userid);
    }

    @Override
    public int getItemCount() {
        if (mUserList == null) {
            return 0;
        }

        return mUserList.size();
    }

    class FirebaseRegisteredUserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.civ_profile_image)
        CircleImageView mProfileImageView;

        @BindView(R.id.tv_name)
        TextView mNameTextView;

        @BindView(R.id.tv_uid)
        TextView mUidTextView;

        FirebaseRegisteredUserViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            FirebaseWriterSingleton.getInstance().addFriend(mUserList.get(getAdapterPosition()));
            mUserInteractionListener.userSelected();
        }
    }

    public interface OnFirebaseRegisteredUserInteractionListener {
        void userSelected();
    }
}
