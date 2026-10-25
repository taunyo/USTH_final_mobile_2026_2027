package vn.edu.usth.final_mobile_2026_2027.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import vn.edu.usth.final_mobile_2026_2027.R;

/**
 * Page 2 - Search: find a community by its name.
 */
public class SearchFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_communities);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // TODO: run the search when the user presses the "search" key of the keyboard
        //       (setOnEditorActionListener on search_input)
        // TODO: implement LemmyApi.searchCommunities() and create a CommunityAdapter
        // TODO: open CommunityActivity.newIntent(...) when a community is clicked
    }
}
