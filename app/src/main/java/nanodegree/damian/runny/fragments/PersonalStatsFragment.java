package nanodegree.damian.runny.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nanodegree.damian.runny.R;
import nanodegree.damian.runny.adapters.WorkoutHistoryAdapter;
import nanodegree.damian.runny.viewmodel.WorkoutHistoryViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PersonalStatsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class PersonalStatsFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private WorkoutHistoryAdapter mAdapter;

    public PersonalStatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_personal_stats,
                container, false);

        RecyclerView historyRecyclerView = (RecyclerView) result.findViewById(R.id.rv_history);
        mAdapter = new WorkoutHistoryAdapter(getContext());
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        historyRecyclerView.setAdapter(mAdapter);

        setupViewModel();

        return result;
    }

    private void setupViewModel() {
        WorkoutHistoryViewModel viewModel = ViewModelProviders.of(this)
                .get(WorkoutHistoryViewModel.class);
        viewModel.getSessionsLiveData().observe(this, sessionList -> {
            mAdapter.swapSessionList(sessionList);
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
