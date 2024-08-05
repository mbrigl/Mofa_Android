package it.schmid.android.mofa;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.List;

import it.schmid.android.mofa.adapter.WorkAdapter;
import it.schmid.android.mofa.databinding.FragmentFirstBinding;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.Work;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateData();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateData(); //filling the list
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Filling the listview with data
     */
    public void updateData() {
        List<Work> workList = DatabaseManager.getInstance().getAllNotSendedWorks();
        WorkAdapter adapter = new WorkAdapter(getContext(), R.layout.work_row, workList);

        binding.workerlistview.setAdapter(adapter);
        binding.workerlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() { // listener for click event
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Work work = adapter.getItem(position); //current work

                Intent i = new Intent(getActivity(), WorkEditTabActivity.class); // opening the corresponding activity////
                i.putExtra("Work_ID", work.getId());

                startActivity(i);
            }
        });

    }
}