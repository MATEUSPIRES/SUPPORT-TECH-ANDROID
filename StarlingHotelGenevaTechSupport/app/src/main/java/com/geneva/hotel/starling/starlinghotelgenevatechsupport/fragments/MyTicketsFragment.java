package com.geneva.hotel.starling.starlinghotelgenevatechsupport.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
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

import java.io.Console;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import services.ServiceHandler;

/**
 * A simple {@link android.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyTicketsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyTicketsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyTicketsFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section";//Used to display the good title on Navigation Drawer
    private static final String ID_USER = "param2";
    private static final String GETTHESETICKETS_URL = "http://212.243.48.10/support_technique/database/databaseManager.php?func=getTheseTickets";

    // TODO: Rename and change types of parameters
    private int mSectionNumber;
    private String mIdUser;

    //UI references
    private ListView mItemlist;
    private View mView;
    private CheckBox mCheckBox;
    /**
     * Keep track of the create task to ensure we can cancel it if requested.
     */
    private MyTicketsTask mItemListTask = null;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber Parameter 1.
     * @param idUser Parameter 2.
     * @return A new instance of fragment MyTicketsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyTicketsFragment newInstance(int sectionNumber, String idUser) {
        MyTicketsFragment fragment = new MyTicketsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ID_USER, idUser);
        fragment.setArguments(args);
        return fragment;
    }

    public MyTicketsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            mIdUser = getArguments().getString(ID_USER);
            //MYCODE
            getAllThisUserItems(false);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView=inflater.inflate(R.layout.fragment_mytickets, container, false);
        mItemlist=(ListView)mView.findViewById(R.id.listViewTickets);

        //CHECKBOX
        mCheckBox = (CheckBox) mView.findViewById(R.id.checkBoxTickets);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getAllThisUserItems(isChecked);
            }
        });
        mItemlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterViewCompat, View view, int position, long l) {
                try{
                    Context context = getActivity().getApplication();

                    String data=adapterViewCompat.getItemAtPosition(position*3).toString();
                    Log.d("DATA",data);

                    CharSequence text = "Impossible de modifier ou de supprimer une demande";
                    int duration = Toast.LENGTH_SHORT;


                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                }catch(Exception e){
                    Log.e("ERROR NO ITEM", e.getMessage());
                }

            }
        });
        return mView;    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    public void getAllThisUserItems(Boolean onlyCreated){
        if(mItemListTask != null){
            mItemListTask=null;
        }
        mItemListTask = new MyTicketsTask(onlyCreated);
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

    public class MyTicketsTask extends AsyncTask<Void, Void, JSONArray> {

        private final Boolean mOnlyCreated;

    MyTicketsTask(Boolean onlyCreated) {
        mOnlyCreated = onlyCreated;
    }

    @Override
    protected JSONArray doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.
        int success;
        try {
            // Building Parameters
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            Log.d("IDUSER",mIdUser);
            parameters.add(new BasicNameValuePair("idUser", mIdUser));
            if(mOnlyCreated){
                parameters.add(new BasicNameValuePair("onlyCreated", mOnlyCreated.toString()));
            }
            // getting product details by making HTTP request
            String response = new ServiceHandler().makeServiceCall(
                    GETTHESETICKETS_URL, ServiceHandler.POST, parameters);


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
            Log.d("JSON",json.toString());
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
                            if(key.equals("statut"))
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
        Log.d("LISTDATA", listdata.toString());
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
