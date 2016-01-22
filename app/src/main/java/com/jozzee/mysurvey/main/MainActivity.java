package com.jozzee.mysurvey.main;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;

import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.event.BusProvider;
import com.jozzee.mysurvey.event.LoginEvent;
import com.jozzee.mysurvey.event.LogoutEvent;
import com.jozzee.mysurvey.event.OnBackPressedAfterRegisterEvent;
import com.jozzee.mysurvey.event.OnSetTypeRecycleViewEvent;
import com.jozzee.mysurvey.adpter.PagerAdapter;
import com.squareup.otto.Subscribe;


public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getSimpleName();

    private CoordinatorLayout rootLayout;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int currentPage;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int accountID = 1;
    private int typeOffRecycleView = 0; // type 0 is listView , type 1 is cardView
    private PagerAdapter pagerAdapter;
    private  int[] tabIcons;
    //FloatingActionButton createSurveyFormButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("LOG", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        checkFirstOpenApp();checkLoginOnApp();

        setContentView(R.layout.activity_main);
        initInstances();
        if(savedInstanceState != null){
            viewPager.setCurrentItem(savedInstanceState.getInt("currentPage"));
            getSupportActionBar().setTitle(pagerAdapter.getPageTitle2(viewPager.getCurrentItem()));
        }



    }

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart");

        super.onStart();
    }

    @Override
    protected void onResume(){
        Log.i(TAG, "onResume");

        super.onResume();
        BusProvider.getInstance().register(this);// Register ourselves so that we can provide the initial value.
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");

        super.onPause();
        BusProvider.getInstance().unregister(this);// Always unregister when an object no longer should be on the bus.
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");

        super.onStop();
    }

    @Override
    protected void onRestart(){
        Log.i(TAG, "onRestart");

        super.onRestart();
    }
    @Override
    protected void onDestroy(){
        Log.i(TAG, "onDestroy");

        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected");
        int id = item.getItemId();
        if(id == R.id.action_settings){
            Log.i(TAG,"press setting menu"); //when click setting on apps
            return true;
        }
        else if(id == R.id.action_listView){
            editor.putInt("typeOffRecycleView",0);
            editor.commit();
            BusProvider.getInstance().post(new OnSetTypeRecycleViewEvent(0));

            return true;
        }
        else if(id == R.id.action_cardView){
            editor.putInt("typeOffRecycleView",1);
            editor.commit();
            BusProvider.getInstance().post(new OnSetTypeRecycleViewEvent(1));
            return  true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putInt("currentPage", viewPager.getCurrentItem());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "onRestoreInstanceState");

    }
    private void initInstances() {
        Log.i(TAG, "initInstances");
        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout_main);  //set RootLayout

        toolbar = (Toolbar) findViewById(R.id.toolbar_main_activity); //Set Toolbar replace Actionbar
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        getSupportActionBar().setTitle(pagerAdapter.getPageTitle2(viewPager.getCurrentItem()));
        setTabIcon(pagerAdapter.getPageTitle2(viewPager.getCurrentItem()).toString());
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                toolbar.setTitle(pagerAdapter.getPageTitle2(tab.getPosition()));
                setTabIcon(pagerAdapter.getPageTitle2(tab.getPosition()).toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
    public void setupViewPager(ViewPager viewPager) {
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());

        pagerAdapter.addFragment(new SurveyPublicFragment().newInstance(accountID, typeOffRecycleView), "Public");
        pagerAdapter.addFragment(new SurveyPasswordFragment().newInstance(accountID, typeOffRecycleView), "Password");
        if(sharedPreferences.getBoolean("loginOnApps", false)){
            pagerAdapter.addFragment(new SurveyPrivateFragment().newInstance(accountID, typeOffRecycleView), "Private");
            pagerAdapter.addFragment(new SurveyAccountOnLoginFragment().newInstance(accountID), "Menu");
        }
        else{
            pagerAdapter.addFragment(new SurveyAccountUnLoginFragment().newInstance(),"Menu");
        }
        viewPager.setAdapter(pagerAdapter);
    }
    /*public void setupTabIcons() {
        Log.i(TAG, "setupTabIcons");
        tabIcons = new int[]{
                R.drawable.ic_public,
                R.drawable.ic_password,
                R.drawable.ic_private,
                R.drawable.ic_home_account};
        if(accountID == 1){
            tabLayout.getTabAt(0).setIcon(tabIcons[0]);
            tabLayout.getTabAt(1).setIcon(tabIcons[1]);
            tabLayout.getTabAt(2).setIcon(tabIcons[3]);
        }
        else if(accountID != 1){
            tabLayout.getTabAt(0).setIcon(tabIcons[0]);
            tabLayout.getTabAt(1).setIcon(tabIcons[1]);
            tabLayout.getTabAt(2).setIcon(tabIcons[2]);
            tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        }

    }*/
    public void setTabIcon(String currentPager){
        if(currentPager.equals("Public")){
            onSelectPublic();
        }
        else if(currentPager.equals("Password")){
            onSelectPassword();
        }
        else if(currentPager.equals("Private")){
            onSelectPrivate();
        }
        else if(currentPager.equals("Menu")){
            onSelectMenu();
        }
    }
    public void onSelectPublic(){
        tabIcons = new int[]{
                R.drawable.ic_public_focused,
                R.drawable.ic_password_normal,
                R.drawable.ic_private_normal,
                R.drawable.ic_menu_normal};
        onSetTabIcon();
    }
    public void onSelectPrivate(){
        tabIcons = new int[]{
                R.drawable.ic_public_normal,
                R.drawable.ic_password_normal,
                R.drawable.ic_private_focused,
                R.drawable.ic_menu_normal};
        onSetTabIcon();
    }
    public void onSelectPassword(){
        tabIcons = new int[]{
                R.drawable.ic_public_normal,
                R.drawable.ic_password_focused,
                R.drawable.ic_private_normal,
                R.drawable.ic_menu_normal};
        onSetTabIcon();
    }
    public void onSelectMenu(){
        tabIcons = new int[]{
                R.drawable.ic_public_normal,
                R.drawable.ic_password_normal,
                R.drawable.ic_private_normal,
                R.drawable.ic_menu_focused};
        onSetTabIcon();
    }

    public void onSetTabIcon(){
        if(accountID == 1){
            tabLayout.getTabAt(0).setIcon(tabIcons[0]);
            tabLayout.getTabAt(1).setIcon(tabIcons[1]);
            tabLayout.getTabAt(2).setIcon(tabIcons[3]);
        }
        else if(accountID != 1){
            tabLayout.getTabAt(0).setIcon(tabIcons[0]);
            tabLayout.getTabAt(1).setIcon(tabIcons[1]);
            tabLayout.getTabAt(2).setIcon(tabIcons[2]);
            tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        }
    }
    // Event on Fragment ------------------------------------------------------------------------------------------
    @Subscribe
    public void onLoginEvent(LoginEvent event){ //has login event ,refresh activity
        Log.i(TAG, "onLoginEvent");
        editor.putBoolean("loginOnApps", true);
        editor.putInt("accountID", event.getAccountID());
        editor.commit();
        reStartActivity();
        //super.onCreate(null);
        //removeAllTabs();
        //setTabLayoutOnLogin();

    }
    @Subscribe
    public void onLogoutEvent(LogoutEvent event){
        Log.i(TAG, "onLogoutEvent");

        editor.putBoolean("loginOnApps", false);
        editor.remove("accountID");
        editor.commit();
        reStartActivity();
        //super.onCreate(null);
        //removeAllTabs();
        //setTabLayoutUnLogin();
    }
    @Subscribe
    public void onBackpressedFromRegister(OnBackPressedAfterRegisterEvent event){ Log.i(TAG, "onBackpressedFromRegister");
        if(event.isRefresh()){
            reStartActivity();
        }
    }

    public void checkFirstOpenApp(){
        if(sharedPreferences.getBoolean("firstOpenApp",true)){
            Log.i(TAG, "First open Application");
            //this is first a open application and then do somethings
            editor.putBoolean("firstOpenApp", false);
            editor.putBoolean("loginOnApps", false);
            editor.putInt("typeOffRecycleView",0);
            editor.commit();
        }

    }
    public void checkLoginOnApp(){
        if(sharedPreferences.getBoolean("loginOnApps",false)){
            accountID = sharedPreferences.getInt("accountID",1);
            typeOffRecycleView = sharedPreferences.getInt("typeOffRecycleView",0);
        }
    }

    public void reStartActivity(){
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        overridePendingTransition(0, 0);

        startActivity(intent);
        overridePendingTransition(0, 0);
       /* if (Build.VERSION.SDK_INT >= 11) {
            Log.i(TAG, "Build.VERSION.SDK_INT > = 11");
            recreate();

        } else {
            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();
            overridePendingTransition(0, 0);

            startActivity(intent);
            overridePendingTransition(0, 0);
        }*/

    }
}
    //---------------------------------------------------------------------------------------------
    /* public void removeAllTabs(){
        tabLayout.removeAllTabs();
        }*/
    //-------------------------------------------------------------------------------------------
   /*public void dialogMethod() {
       AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
       alertDialogBuilder.setTitle("Exit");
       alertDialogBuilder.setMessage("Exit2");

       alertDialogBuilder.setPositiveButton("ยกเลิก", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int arg1) {
               dialog.dismiss();

           }
       });

       alertDialogBuilder.setNegativeButton("ตกลง", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int arg1) {
               Intent a = new Intent(Intent.ACTION_MAIN);
               a.addCategory(Intent.CATEGORY_HOME);
               a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(a);
           }
       });

       AlertDialog alertDialog = alertDialogBuilder.create();
       alertDialog.show();
   }*/
    //-------------------------------------------------------------------------------------------