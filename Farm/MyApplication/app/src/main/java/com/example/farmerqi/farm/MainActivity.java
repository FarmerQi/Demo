package com.example.farmerqi.farm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FarmerQi on 2017/12/13.
 */

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,View.OnClickListener{
    ImageView userImage;
    RecyclerView recyclerView;
    RelativeLayout user;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = (RelativeLayout) findViewById(R.id.fourth_button);
        user.setOnClickListener(this);

        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        DrawerLayout mDrawerLayout = (DrawerLayout)findViewById(R.id.nav_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.drawer_layout_open_state,R.string.drawer_layout_close_state);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);

        View headLayout = navigationView.getHeaderView(0);
        userImage = (ImageView)headLayout.findViewById(R.id.nav_user_image);
        navigationView.setNavigationItemSelectedListener(this);
        userImage.setOnClickListener(this);

        recyclerView = (RecyclerView)findViewById(R.id.main_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(false);
        List<String> input = new ArrayList<String>();
        input.add("213123123");
        input.add("dsadasd");
        input.add("dadda");
        input.add("dasd");
        MyAdapter myAdapter = new MyAdapter(input);
        recyclerView.setAdapter(myAdapter);

    }


    //重写该方法，在打开DrawerLayout后，在用户点击返回按钮后，应用不会退出，同时回到之前的界面
    @Override
    public void onBackPressed(){
        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.nav_drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.First_item:
            case R.id.Second_item:
            case R.id.Third_item:
            case R.id.Fourth_item:
            case R.id.fifth_item:
                Toast.makeText(this,"Hello,FarmerQi!",Toast.LENGTH_SHORT).show();
                break;

        }
        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.nav_drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nav_user_image:
                Toast.makeText(this,"hi,FarmerQi",Toast.LENGTH_LONG).show();
                break;
            case R.id.fourth_button:
                Toast.makeText(this,"打开第二个活动",Toast.LENGTH_LONG).show();
//                Intent toSecondActivity = new Intent(MainActivity.this,SecondActivity.class);
//                startActivity(toSecondActivity);

        }

    }
}
