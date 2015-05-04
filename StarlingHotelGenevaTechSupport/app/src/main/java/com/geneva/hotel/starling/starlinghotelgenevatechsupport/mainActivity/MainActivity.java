package com.geneva.hotel.starling.starlinghotelgenevatechsupport.mainActivity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.geneva.hotel.starling.starlinghotelgenevatechsupport.R;
import com.geneva.hotel.starling.starlinghotelgenevatechsupport.fragments.MyTicketsFragment;
import com.geneva.hotel.starling.starlinghotelgenevatechsupport.fragments.CreateTicketsFragment;
import com.geneva.hotel.starling.starlinghotelgenevatechsupport.fragments.NavigationDrawerFragment;


public class MainActivity extends ActionBarActivity implements
        CreateTicketsFragment.OnFragmentInteractionListener,
        MyTicketsFragment.OnFragmentInteractionListener,
        NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private ListView listViewAjax;

    private EditText mItemSearched;
    @Override
    public void onFragmentInteraction(Uri uri) {
        //do something here, maybe switch to another fragment
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_manage_and_create_tickets);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));





        //listViewAjax= (ListView) findViewById(R.id.listViewAjax);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment fragment;
        switch(position) {
            default:
            case 0:
                Log.d("IDUSERMAINACT",getIntent().getStringExtra("id_of_user"));
                fragment = CreateTicketsFragment.newInstance(position + 1,getIntent().getStringExtra("id_of_user"));
                break;

            case 1:
                fragment = MyTicketsFragment.newInstance(position + 1, getIntent().getStringExtra("id_of_user"));
                break;

        }
        if(fragment !=null){
            FragmentManager fragmentManager = getSupportFragmentManager(); // For AppCompat use getSupportFragmentManager
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }



    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section_create_ticket);
                break;

            case 2:
                mTitle = getString(R.string.title_section_manage_ticket);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.manage_and_create_tickets, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();




        return super.onOptionsItemSelected(item);
    }


}
