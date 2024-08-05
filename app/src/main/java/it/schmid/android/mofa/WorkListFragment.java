package it.schmid.android.mofa;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import it.schmid.android.mofa.adapter.WorkAdapter;
import it.schmid.android.mofa.databinding.FragmentWorklistBinding;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.Work;
import it.schmid.android.mofa.util.SwipeCallback;


public class WorkListFragment extends Fragment {

    private FragmentWorklistBinding binding;

    private RecyclerView mView;
    private WorkAdapter mAdapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentWorklistBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mView = view.findViewById(R.id.workerlistview2);

        updateData();
        enableSwipeToDeleteAndUndo();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateData() {
        List<Work> workList = DatabaseManager.getInstance().getAllNotSendedWorks();
        mAdapter = new WorkAdapter(workList, getContext());
        mView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(work -> {
            Intent intent = new Intent(getActivity(), WorkEditTabActivity.class); // opening the corresponding activity////
            intent.putExtra("Work_ID", work.getId());

            startActivity(intent);
        });
    }

    private void enableSwipeToDeleteAndUndo() {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_delete);
        SwipeCallback onDelete = new SwipeCallback(drawable, "#b80f0a") {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final Work item = mAdapter.getData().get(position);

                mAdapter.removeItem(position);

                Snackbar snackbar = Snackbar
                        .make(mView, "Item was removed from the list.", Snackbar.LENGTH_LONG);
//                snackbar.setAction("UNDO", view -> {
//                    mAdapter.restoreItem(item, position);
//                    mView.scrollToPosition(position);
//                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper touchHelper = new ItemTouchHelper(onDelete);
        touchHelper.attachToRecyclerView(mView);
    }
}