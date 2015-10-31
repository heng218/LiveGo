package com.v.heng.livego.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.update.UmengUpdateAgent;
import com.v.heng.livego.R;
import com.v.heng.livego.adapter.TabsAdapter;
import com.v.heng.livego.base.AppManager;
import com.v.heng.livego.base.BaseActivity;
import com.v.heng.livego.ui.fragment.PlatformSortFragment;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener,
        CompoundButton.OnCheckedChangeListener, OnMenuItemClickListener, OnMenuItemLongClickListener {


    private ViewPager mViewPager;
    private TabsAdapter mTabsAdapter;

    private RadioButton allLiveRadioBtn, platformRadioBtn, gameRadioBtn;


    // title menu
    private FragmentManager fragmentManager;
    private DialogFragment mMenuDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContentView(R.layout.activity_main);

        // 结束welcome Activity
        AppManager.getAppManager().finishActivity(WelcomeActivity.class);

        initTitle();

        initViews();

        initTitleMenu();

        // umeng 检查更新
//        UmengUpdateAgent.update(this);
        UmengUpdateAgent.silentUpdate(this); // 静默

    }

    public void initTitle() {
        TextView leftTv = getLeftTv();
        leftTv.setCompoundDrawables(null, null, null, null);
        leftTv.setOnClickListener(null);

        getRightTv().setVisibility(View.VISIBLE);
    }

    public void initViews() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(2);
        mTabsAdapter = new TabsAdapter(this, mViewPager);
//        mTabsAdapter.addTab(null, LiveListFragment.class, null);
        mTabsAdapter.addTab(null, PlatformSortFragment.class, null);
//        mTabsAdapter.addTab(null, GameSortFragment.class, null);
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(this);

        allLiveRadioBtn = (RadioButton) findViewById(R.id.allLiveRadioBtn);
        platformRadioBtn = (RadioButton) findViewById(R.id.platformRadioBtn);
        gameRadioBtn = (RadioButton) findViewById(R.id.gameRadioBtn);
        allLiveRadioBtn.setTag(0);
        allLiveRadioBtn.setOnCheckedChangeListener(this);
        platformRadioBtn.setTag(1);
        platformRadioBtn.setOnCheckedChangeListener(this);
        gameRadioBtn.setTag(2);
        gameRadioBtn.setOnCheckedChangeListener(this);


    }


    private void initTitleMenu() {
        fragmentManager = getSupportFragmentManager();

        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.title_menu_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(true);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);

    }

    private List<MenuObject> getMenuObjects() {
        // You can use any [resource, bitmap, drawable, color] as image:
        // item.setResource(...)
        // item.setBitmap(...)
        // item.setDrawable(...)
        // item.setColor(...)
        // You can set image ScaleType:
        // item.setScaleType(ScaleType.FIT_XY)
        // You can use any [resource, drawable, color] as background:
        // item.setBgResource(...)
        // item.setBgDrawable(...)
        // item.setBgColor(...)
        // You can use any [color] as text color:
        // item.setTextColor(...)
        // You can set any [color] as divider color:
        // item.setDividerColor(...)

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.drawable.icn_close);

        MenuObject send = new MenuObject("分享");
        send.setResource(R.drawable.icn_3);

        MenuObject like = new MenuObject("打赏");
        like.setResource(R.drawable.icn_2);

        MenuObject addFr = new MenuObject("检查更新");
        addFr.setResource(R.drawable.icn_1);

//        MenuObject addFav = new MenuObject("关于");
//        addFav.setResource(R.drawable.icn_4);


        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(like);
        menuObjects.add(addFr);
//        menuObjects.add(addFav);
        return menuObjects;
    }


    @Override
    public void onMenuItemClick(View clickedView, int position) {
        switch (position) {
            case 0: // 关闭

                break;
            case 1: // 分享
                String share_content = "看游戏直播用{0} ，斗鱼 战旗 龙珠 熊猫, 虎牙...一起看！";

                Intent intent_share  = new Intent(Intent.ACTION_SEND);
                intent_share.setType("image/*");
                intent_share.putExtra(Intent.EXTRA_TEXT, MessageFormat.format(share_content,
                        getResources().getString(R.string.app_name)));
                        startActivity(Intent.createChooser(intent_share, ""));

                break;
            case 2: // 打赏
                Intent intent = new Intent(this, RewardActivity.class);
                startActivity(intent);
                break;
            case 3: // 检查更新
                Toast.makeText(this, "当前为最新版本！", 0).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onMenuItemLongClick(View clickedView, int position) {
//        Toast.makeText(this, "Long clicked on position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.rightTv:
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
            mMenuDialogFragment.dismiss();
        } else{
            if (exit()) {
                AppManager.getAppManager().AppExit(this);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                allLiveRadioBtn.setChecked(true);
                break;
            case 1:
                platformRadioBtn.setChecked(true);
                break;
            case 2:
                gameRadioBtn.setChecked(true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            int position = (int) compoundButton.getTag();
            mViewPager.setCurrentItem(position);
        }
    }


}
