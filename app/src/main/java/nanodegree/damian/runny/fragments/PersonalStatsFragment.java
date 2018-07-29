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
 * Activities that contain this fragment must implement the
 * {@link PersonalStatsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class PersonalStatsFragment extends Fragment {

    private static final int MONTHS_IN_YEAR = 12;
    private static final float CHART_MAX_VALUE_RATIO = 5f/3f;

    private OnFragmentInteractionListener mListener;
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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

    @UiThread
    public void updatePerformanceBarChart(TreeMap<Integer, Float> monthlyPerformance) {
        if (monthlyPerformance.size() < 3) {
            monthlyPerformance.put((monthlyPerformance.firstKey() - 1) % MONTHS_IN_YEAR, 3000f);
            monthlyPerformance.put((monthlyPerformance.lastKey() + 1) % MONTHS_IN_YEAR, 13000f);
        }

        List<BarEntry> data = new ArrayList<>();
        float max = 0;
        String[] months = new DateFormatSymbols().getMonths();
        int index = 0;
        for (Map.Entry<Integer, Float> entry : monthlyPerformance.entrySet()) {
            data.add(new BarEntry(entry.getKey(), entry.getValue() / 1000));
            if (entry.getValue() / 1000 > max) {
                max = entry.getValue() / 1000;
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
        mHistoryBarChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if ((int) value != value) {
                    return "";
                }

                return months[(int) value];
            }
        });
        mHistoryBarChart.invalidate();
     }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
