package com.example.csdfcommunityapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewAnimationUtils;


import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    /*
    This is the main Activity or the Home activity displayed only after successful sign In
         */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final TabLayout tabLayout = findViewById(R.id.tabBar);
        TabItem tabSOS = findViewById(R.id.tabSOS);
        TabItem tabUpload = findViewById(R.id.tabUpload);
        TabItem tabMaps = findViewById(R.id.tabMaps);
        final ViewPager viewPager = findViewById(R.id.viewPager);




        PagerAdapter pagerAdapter = new
                PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());


        viewPager.setAdapter(pagerAdapter);

        //If the Tabs are changed by clicking the Tabs
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {



                tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#73FBD3"));
                tabLayout.setTabTextColors(Color.parseColor("#C1C1C1"), Color.parseColor("#0080FF"));
                if(tab.getPosition()==0){
                    tintSystemBars(1);

                    tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FF0000"));
                    tabLayout.setTabTextColors(Color.parseColor("#C1C1C1"), Color.parseColor("#FF0000"));
                }else{
                    tintSystemBars(0);
                }
                viewPager.setCurrentItem(tab.getPosition());


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //If the Tabs are changed by swiping the pages
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    // The options menu on top right is for Sign Out
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    /*
    The Menu has only the Sign Out option
    But you can easily change this by using a Switch case statement and work your way
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item1) {
            FirebaseAuth.getInstance().signOut();
            Intent intToMain = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intToMain);
        }
        return super.onOptionsItemSelected(item);

    }
    private void tintSystemBars(int x) {


        // Initial colors of each system bar.
        final int statusBarColor;
        final int toolbarColor;

        // Desired final colors of each bar.
        final int statusBarToColor;
        final int toolbarToColor;
        if(x == 0){
            statusBarColor = getResources().getColor(R.color.bleedred);
            toolbarColor = getResources().getColor(R.color.lessbleedred);


            // Desired final colors of each bar.
            statusBarToColor = getResources().getColor(R.color.colorPrimary);
            toolbarToColor = getResources().getColor(R.color.colorPrimaryDark);
        }else
        {
            statusBarToColor = getResources().getColor(R.color.bleedred);
            toolbarToColor = getResources().getColor(R.color.lessbleedred);


            // Desired final colors of each bar.
            statusBarColor = getResources().getColor(R.color.colorPrimary);
            toolbarColor = getResources().getColor(R.color.colorPrimaryDark);


        }


        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Use animation position to blend colors.
                float position = animation.getAnimatedFraction();

                // Apply blended color to the status bar.
                int blended = blendColors(statusBarColor, statusBarToColor, position);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(blended);
                }

                // Apply blended color to the ActionBar.
                blended = blendColors(toolbarColor, toolbarToColor, position);
                ColorDrawable background = new ColorDrawable(blended);
                getSupportActionBar().setBackgroundDrawable(background);
            }
        });

        anim.setDuration(0).start();
    }

    private int blendColors(int from, int to, float ratio) {
        final float inverseRatio = 1f - ratio;

        final float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
        final float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
        final float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

        return Color.rgb((int) r, (int) g, (int) b);
    }
}




