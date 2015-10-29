package com.v.heng.livego.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.v.heng.livego.R;
import com.v.heng.livego.base.BaseActivity;
import com.v.heng.livego.ui.fragment.LiveListFragment;

/**
 * Created by heng on 2015/10/15.
 * 邮箱：252764480@qq.com
 */
public class LiveListActivity extends BaseActivity {

    public String platform;
    private String game;

    private LiveListFragment liveListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContentView(R.layout.activity_live_list);

        Intent intent = getIntent();
        platform = intent.getStringExtra("platform");
        game = intent.getStringExtra("game");

        initViews();

    }

    public void initViews() {
        View content = findViewById(R.id.content);

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        LiveListFragment liveListFragment = new  LiveListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("platform", platform);
        bundle.putString("game", game);
        liveListFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.content, liveListFragment);
        fragmentTransaction.commit();

//      liveListFragment = (LiveListFragment) fm.findFragmentById(R.id.liveListFragment);
    }




}
