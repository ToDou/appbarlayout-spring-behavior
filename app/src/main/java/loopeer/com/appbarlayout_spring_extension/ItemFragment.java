package loopeer.com.appbarlayout_spring_extension;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import loopeer.com.appbarlayout_spring_extension.dummy.DummyContent;

public class ItemFragment extends Fragment {

    public static ItemFragment newInstance() {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new MyItemRecyclerViewAdapter(DummyContent.ITEMS));
        return view;
    }


}
