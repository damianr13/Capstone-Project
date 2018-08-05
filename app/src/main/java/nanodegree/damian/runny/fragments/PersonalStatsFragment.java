package nanodegree.damian.runny.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import nanodegree.damian.runny.R;
import nanodegree.damian.runny.adapters.WorkoutHistoryAdapter;
import nanodegree.damian.runny.persistence.data.WorkoutSession;
import nanodegree.damian.runny.viewmodel.WorkoutHistoryViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalStatsFragment extends Fragment {

    private static final int METERS_IN_KILOMETER = 1000;

    private WorkoutHistoryAdapter mAdapter;

    public PersonalStatsFragment() {
        // Required empty public constructor
    }

    @BindView(R.id.rv_history)
    RecyclerView mHistoryRecyclerView;

    @BindView(R.id.bc_history)
    BarChart mHistoryBarChart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_personal_stats,
                container, false);
        ButterKnife.bind(this, result);

        mAdapter = new WorkoutHistoryAdapter(getContext());
        mHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mHistoryRecyclerView.setAdapter(mAdapter);

        setupViewModel();

        return result;
    }

    private void setupViewModel() {
        WorkoutHistoryViewModel viewModel = ViewModelProviders.of(this)
                .get(WorkoutHistoryViewModel.class);
        viewModel.getSessionsLiveData().observe(this, sessionList -> {
            mAdapter.swapSessionList(sessionList);
            TreeMap<Integer, Float> monthlyPerformance = computeMonthlyPerformance(sessionList);

            if (getActivity() == null) {
                return ;
            }
            getActivity().runOnUiThread(() -> updatePerformanceBarChart(monthlyPerformance));
        });
    }

    /**
     * Computes the monthly performance of the user
     *
     * @param sessionList The list of all the workout sessions completed by the user
     * @return A map having the month index as key (January is 0) and the number of completed meters
     * as value
     */
    @WorkerThread
    public TreeMap<Integer, Float> computeMonthlyPerformance(List<WorkoutSession> sessionList) {
        TreeMap<Integer, Float> monthlyPerformance = new TreeMap<>();

        for (WorkoutSession session : sessionList) {
            float value = 0;
            int currentMonth = session.getStartTime().get(Calendar.MONTH);
            if (monthlyPerformance.containsKey(currentMonth)) {
                value = monthlyPerformance.get(currentMonth);
            }

            monthlyPerformance.put(currentMonth, value + session.getDistance());
        }

        return monthlyPerformance;
    }

    /**
     * Updates the bar chart that displays the user's monthly performance (number of kilometers
     * completed each month)
     *
     * @param monthlyPerformance - a map that has month index as keys (January is 0) and number of
     *                           completed meters as value
     */
    @UiThread
    public void updatePerformanceBarChart(TreeMap<Integer, Float> monthlyPerformance) {
        List<BarEntry> data = new ArrayList<>();
        float max = 0;
        String[] months = new DateFormatSymbols().getMonths();
        for (Map.Entry<Integer, Float> entry : monthlyPerformance.entrySet()) {
            data.add(new BarEntry(entry.getKey(), entry.getValue() / METERS_IN_KILOMETER));
            if (entry.getValue() / METERS_IN_KILOMETER > max) {
                max = entry.getValue() / METERS_IN_KILOMETER;
            }
        }
        BarDataSet dataSet = new BarDataSet(data, "Label");
        BarData barData = new BarData(dataSet);
        mHistoryBarChart.setData(barData);
        mHistoryBarChart.setScaleEnabled(false);
        mHistoryBarChart.setDrawGridBackground(false);
        mHistoryBarChart.setDrawBorders(false);
        mHistoryBarChart.getAxisLeft().setEnabled(false);
        mHistoryBarChart.getAxisRight().setEnabled(false);
        mHistoryBarChart.getXAxis().setDrawGridLines(false);
        mHistoryBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mHistoryBarChart.getDescription().setEnabled(false);
        mHistoryBarChart.getLegend().setEnabled(false);
        mHistoryBarChart.setTouchEnabled(false);
        mHistoryBarChart.getXAxis().setValueFormatter((value, axis) -> {
            if ((int) value != value) {
                return "";
            }

            return months[(int) value];
        });
        mHistoryBarChart.invalidate();
     }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
