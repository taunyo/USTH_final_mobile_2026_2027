package vn.edu.usth.final_mobile_2026_2027;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import vn.edu.usth.final_mobile_2026_2027.ui.home.HomeFragment;
import vn.edu.usth.final_mobile_2026_2027.ui.search.SearchFragment;
import vn.edu.usth.final_mobile_2026_2027.utils.ViewUtils;

/**
 * Entry point of the app. It holds the bottom navigation bar and switches
 * between the Home and Search pages. The other pages are separate activities.
 *
 * The two pages are created once and only shown or hidden when the user changes tab,
 * so they keep their content (list, scroll position, search text) and Home is not reloaded.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG_HOME = "home";
    private static final String TAG_SEARCH = "search";

    private HomeFragment homeFragment;
    private SearchFragment searchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewUtils.applySystemBarsPadding(findViewById(R.id.main));

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            // First start: add both pages, only Home is visible
            homeFragment = new HomeFragment();
            searchFragment = new SearchFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, homeFragment, TAG_HOME)
                    .add(R.id.fragment_container, searchFragment, TAG_SEARCH)
                    .hide(searchFragment)
                    .commit();
        } else {
            // After a rotation the fragment manager has already restored both pages,
            // with the one that was hidden still hidden: we only get them back
            homeFragment = (HomeFragment) fragmentManager.findFragmentByTag(TAG_HOME);
            searchFragment = (SearchFragment) fragmentManager.findFragmentByTag(TAG_SEARCH);
        }

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                fragmentManager.beginTransaction()
                        .show(homeFragment)
                        .hide(searchFragment)
                        .commit();
                return true;
            } else if (item.getItemId() == R.id.nav_search) {
                fragmentManager.beginTransaction()
                        .show(searchFragment)
                        .hide(homeFragment)
                        .commit();
                return true;
            }
            return false;
        });

        // Tapping the tab of the page already shown: Home goes back to the first post
        bottomNavigation.setOnItemReselectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                homeFragment.scrollToTop();
            }
        });
    }
}
