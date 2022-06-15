package com.example.appbirthday.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appbirthday.Database;
import com.example.appbirthday.R;
import com.example.appbirthday.databinding.FragmentListBinding;

public class ListFragment extends Fragment {

    private FragmentListBinding binding;
    private RecyclerView rvList;
    private RecyclerViewAdapter rvAdapter;
    Button btnDelete;
    TextView tvNameList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        rvList = (RecyclerView) root.findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));

        Database db = Database.getInstance(getContext(), Database.DB_NAME, null, 1);

        //Use the recycler view adapter
        rvAdapter = new RecyclerViewAdapter(db.showItems());
        rvList.setAdapter(rvAdapter);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}