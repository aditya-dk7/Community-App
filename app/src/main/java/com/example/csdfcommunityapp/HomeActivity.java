package com.example.csdfcommunityapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


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
}




