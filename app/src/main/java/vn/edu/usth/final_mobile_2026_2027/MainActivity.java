package vn.edu.usth.final_mobile_2026_2027;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import vn.edu.usth.final_mobile_2026_2027.ui.home.HomeFragment;
import vn.edu.usth.final_mobile_2026_2027.ui.search.SearchFragment;
import vn.edu.usth.final_mobile_2026_2027.utils.ViewUtils;

/**
 * Entry point of the app. It holds the bottom navigation bar and switches
 * between the Home and Search pages. The other pages are separate activities.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewUtils.applySystemBarsPadding(findViewById(R.id.main));

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                showFragment(new HomeFragment());
                return true;
            } else if (item.getItemId() == R.id.nav_search) {
                showFragment(new SearchFragment());
                return true;
            }
            return false;
        });

        // On rotation the fragment manager restores the current page by itself
        if (savedInstanceState == null) {
            showFragment(new HomeFragment());
        }
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
