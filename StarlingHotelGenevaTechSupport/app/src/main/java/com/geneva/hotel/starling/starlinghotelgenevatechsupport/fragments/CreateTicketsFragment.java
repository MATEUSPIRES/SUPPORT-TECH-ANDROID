package com.geneva.hotel.starling.starlinghotelgenevatechsupport.fragments;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.geneva.hotel.starling.starlinghotelgenevatechsupport.FormTicketsActivity;
import com.geneva.hotel.starling.starlinghotelgenevatechsupport.R;
import com.geneva.hotel.starling.starlinghotelgenevatechsupport.classes.CustomListViewAdapter;
import com.geneva.hotel.starling.starlinghotelgenevatechsupport.mainActivity.MainActivity;


import org.apache.http.NameValuePair;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import services.ServiceHandler;

/**
 * A simple {@link android.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateTicketsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateTicketsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateTicketsFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section"; //Used to display the good title on Navigation Drawer
    private static final String ID_USER = "param2";

    private static final String GETALLOBJECTS_URL = "http://212.243.48.10/support_technique/database/databaseManager.php?func=getAllHotelitems";
    // TODO: Rename and change types of parameters
    private int mSectionNumber;
    private String mIdUser;

    private String mResearchText;

    //UI references
    private ListView mItemlist;
    private View mView;
    private EditText mResearchedItem;
    /**
     * Keep track of the create task to ensure we can cancel it if requested.
     */
    private CreateTicketsTask mItemListTask = null;


    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber Parameter 1.
     * @param idUser Parameter 2.
     * @return A new instance of fragment CreateTicketsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateTicketsFragment newInstance(int sectionNumber, String idUser) {
        CreateTicketsFragment fragment = new CreateTicketsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ID_USER, idUser);
        fragment.setArguments(args);
        return fragment;
    }

    public CreateTicketsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            mIdUser = getArguments().getString(ID_USER);
            //MYCODE
            getAllItems();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mView=inflater.inflate(R.layout.fragment_create_tickets, container, false);
        mItemlist=(ListView)mView.findViewById(R.id.listViewAjax);
        mItemlist.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //hide KB
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        mItemlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterViewCompat, View view, int position, long l) {
                Intent i = new Intent(getActivity(), FormTicketsActivity.class);
                i.putExtra("id_of_item", (String)adapterViewCompat.getItemAtPosition(position*3));
                i.putExtra("name_of_item", (String)adapterViewCompat.getItemAtPosition(position*3+1));
                i.putExtra("id_of_user", mIdUser);
                startActivity(i);
            }
        });
                //RESEARCH
        mResearchedItem = (EditText) mView.findViewById(R.id.searchItemEditText);
        mResearchedItem.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {

                mResearchText=mResearchedItem.getText().toString();
                getAllItems();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        return mView;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void getAllItems(){
        if(mItemListTask != null){
            mItemListTask=null;
        }
        mItemListTask = new CreateTicketsTask();
        mItemListTask.execute((Void) null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));




        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
    public class CreateTicketsTask extends AsyncTask<Void, Void, JSONArray> {


        CreateTicketsTask() {

        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            int success;
            try {
                // Building Parameters
                List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                parameters.add(new BasicNameValuePair("itemNameLike", mResearchText));
                // getting product details by making HTTP request
                String response = new ServiceHandler().makeServiceCall(
                        GETALLOBJECTS_URL, ServiceHandler.POST, parameters);


                //Getting JSON Array node
                JSONArray json = new JSONArray(response);

                // json success tag
                //success = json.getString(TAG_SUCCESS);
                //if (success == 1) {

                    return json;
                //}else{
                //    Log.d("Login Failure!", json.getString(TAG_SUCCESS));
                 //   return false;

                //}
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }


        protected void onPostExecute(final JSONArray json) {
            //mAuthTask = null;
            //showProgress(false);
            List<String> listdata = new ArrayList<String>();
            if (json!=null) {

                // Instanciating an array list (populated by my json).

                for (int i=0;i<json.length();i++){
                    try {
                        JSONObject jsonobj=new JSONObject(json.get(i).toString());
                        Iterator<String> iter = jsonobj.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            try {
                                String value = jsonobj.get(key).toString();

                                if(key.equals("name"))
                                    listdata.add(value);
                                if(key.equals("imagesource"))
                                    listdata.add(value);
                                if(key.equals("idhotelitem"))
                                    listdata.add(value);

                            } catch (JSONException e) {
                                // Something went wrong!
                            }
                        }
                        } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                }
            Log.d("LISTDATA",listdata.toString());
            if(listdata.isEmpty()){
                Context context = getActivity().getApplicationContext();
                CharSequence text = "Aucun résultat (Changez la recherche ou vérifiez votre connexion)";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
                // This is the array adapter, it takes the context of the activity as a
                // first parameter, the type of list view as a second parameter and your
                // array as a third parameter.
            Log.d("LISTDATATEST",listdata.toString());
            CustomListViewAdapter arrayAdapter = new CustomListViewAdapter(
                        getActivity(),
                        listdata);

                mItemlist.setAdapter(arrayAdapter);

            }


        @Override
        protected void onCancelled() {
            //mAuthTask = null;
            //showProgress(false);
        }
    }


}
