package vn.edu.usth.final_mobile_2026_2027.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.final_mobile_2026_2027.R;
import vn.edu.usth.final_mobile_2026_2027.api.ApiCallback;
import vn.edu.usth.final_mobile_2026_2027.api.LemmyApi;
import vn.edu.usth.final_mobile_2026_2027.model.Community;
import vn.edu.usth.final_mobile_2026_2027.ui.common.ErrorView;

/**
 * Page 2 - Search: find a community by its name.
 */
public class SearchFragment extends Fragment {

    private EditText searchInput;
    private CommunityAdapter adapter;
    private ProgressBar progressBar;
    private ErrorView errorView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress_bar);
        errorView = new ErrorView(view.findViewById(R.id.error_view));

        // The adapter opens the Community page itself
        adapter = new CommunityAdapter();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_communities);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // The "search" key of the keyboard (imeOptions="actionSearch" in fragment_search.xml)
        searchInput = view.findViewById(R.id.search_input);
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search(searchInput.getText().toString().trim());
                return true;
            }
            return false;
        });
    }

    private void search(String query) {
        if (query.isEmpty()) {
            return;
        }
        hideKeyboard();

        // Remove the results of the previous search
        adapter.setCommunities(new ArrayList<>());
        progressBar.setVisibility(View.VISIBLE);
        errorView.hide();

        LemmyApi.getInstance(requireContext()).searchCommunities(query, new ApiCallback<List<Community>>() {
            @Override
            public void onSuccess(List<Community> communities) {
                // The user may have left the page while the request was running
                if (!isAdded()) {
                    return;
                }
                progressBar.setVisibility(View.GONE);
                adapter.setCommunities(communities);
                if (communities.isEmpty()) {
                    errorView.showEmpty(getString(R.string.empty_communities));
                }
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                progressBar.setVisibility(View.GONE);
                // "Retry" sends the same search again
                errorView.showError(message, () -> search(query));
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager keyboard =
                (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
    }
}
