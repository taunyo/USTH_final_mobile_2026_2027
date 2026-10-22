package vn.edu.usth.final_mobile_2026_2027.ui.home;

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
 * Page 1 - Home: feed of the popular posts (r/popular).
 */
public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // TODO: create a PostAdapter in ui/common (the Subreddit and Profile pages use it too)
        // TODO: load the posts with RedditApi.getInstance(requireContext()).getPopularPosts(...)
        //       and show progress_bar while waiting
        // TODO: open DiscussionActivity.newIntent(...) when a post is clicked
    }
}
