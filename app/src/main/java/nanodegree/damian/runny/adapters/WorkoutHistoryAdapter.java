package nanodegree.damian.runny.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import nanodegree.damian.runny.R;
import nanodegree.damian.runny.persistence.data.WorkoutSession;
import nanodegree.damian.runny.utils.Basics;

/**
 * Created by robert_damian on 29.07.2018.
 */

public class WorkoutHistoryAdapter extends
        RecyclerView.Adapter<WorkoutHistoryAdapter.WorkoutSessionViewHolder> {

    private Context mContext;
    private List<WorkoutSession> mWorkoutSessionList;

    public WorkoutHistoryAdapter(Context context) {
        mContext = context;
    }

    public void swapSessionList(List<WorkoutSession> sessionList) {
        mWorkoutSessionList = sessionList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WorkoutSessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View result = LayoutInflater.from(mContext)
                .inflate(R.layout.item_personal_stats, parent, false);

        return new WorkoutSessionViewHolder(result);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutSessionViewHolder holder, int position) {
        WorkoutSession session = mWorkoutSessionList.get(position);

        String runName = mContext.getResources()
                .getString(R.string.run_label, Basics.formatCalendar(session.getStartTime()));
        holder.workoutName.setText(runName);

        holder.timeValueTextView.setText(session.getFormattedTimeValue(false));
        holder.distanceValueTextView.setText(session.getFormattedDistanceValue());
    }

    @Override
    public int getItemCount() {
        if (mWorkoutSessionList == null) {
            return 0;
        }

        return mWorkoutSessionList.size();
    }

    class WorkoutSessionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_value_time)
        TextView timeValueTextView;

        @BindView(R.id.tv_value_distance)
        TextView distanceValueTextView;

        @BindView(R.id.tv_entry_title)
        TextView workoutName;

        WorkoutSessionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
