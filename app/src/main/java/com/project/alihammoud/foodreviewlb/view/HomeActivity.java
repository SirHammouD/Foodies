package com.project.alihammoud.foodreviewlb.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.alihammoud.foodreviewlb.MainActivity;
import com.project.alihammoud.foodreviewlb.R;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;

    private SearchFragment searchFragment;
    private AccountFragment accountFragment;
    private DashboardFragment dashboardFragment;
    private NotificationFragment notificationFragment;
    private FirebaseAuth auth;

    private AdView mAdView;

    protected  void  onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        MobileAds.initialize(this, "ca-app-pub-1272284068166713~7761017280");
        mAdView = findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        auth = FirebaseAuth.getInstance();

        accountFragment = new AccountFragment();
        dashboardFragment = new DashboardFragment();
        searchFragment = new SearchFragment();
        notificationFragment = new NotificationFragment();

        mMainFrame = (FrameLayout) findViewById(R.id.frame);
        mMainNav =  (BottomNavigationView) findViewById(R.id.nav_view);
        SetFragment(dashboardFragment);
        mMainNav.setSelectedItemId(R.id.navigation_home);

        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navigation_home:
                        //mMainNav.setItemBackgroundResource(R.color.colorAccent);
                        SetFragment(dashboardFragment);
                        return true;

                    case R.id.navigation_account:
                        // mMainNav.setItemBackgroundResource(R.color.colorPrimary);
                        SetFragment(accountFragment);
                        return true;

                    case R.id.navigation_search:
                        // mMainNav.setItemBackgroundResource(R.color.colorPrimaryDark);
                        SetFragment(searchFragment);
                        return true;

                    case R.id.navigation_notification:
                        // mMainNav.setItemBackgroundResource(R.color.colorPrimaryDark);
                        SetFragment(notificationFragment);
                        return true;

                    case R.id.navigation_review:
                        // mMainNav.setItemBackgroundResource(R.color.colorPrimaryDark);
                        Intent intent = new Intent(HomeActivity.this, ReviewActivity.class);
                        startActivity(intent);
                        return true;
                }
                return true;
            }
        });



    }
    private void SetFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();

    }

    private void updateUI(FirebaseUser account) {
        if (account == null) {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdView != null) {
            mAdView.pause();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}
