package com.geneva.hotel.starling.starlinghotelgenevatechsupport;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.geneva.hotel.starling.starlinghotelgenevatechsupport.mainActivity.MainActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import services.ServiceHandler;


public class FormTicketsActivity extends ActionBarActivity {
    //Login url to call php function
    private static final String CREATE_TICKET_URL = "http://212.243.48.10/support_technique/database/databaseManager.php?func=createTicket";


    //Tags for AsyncTask
    private static final String TAG_SUCCESS = "success";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private TicketTask mTicketTask = null;

    // UI references.
    TextView mTextViewTitle;
    EditText mDescriptionEditText;
    EditText mLieuEditText;
    Button mDescriptionButton;
    RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_tickets);

        String itemSelectedName = getIntent().getStringExtra("name_of_item");
        this.setTitle(itemSelectedName);

        // Set up the form.
        mDescriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        mLieuEditText = (EditText) findViewById(R.id.lieuEditText);
        LinearLayout mRelativeLayout = (LinearLayout) findViewById(R.id.relative_layout_form_tickets);

        mDescriptionButton = (Button) findViewById(R.id.descriptionButton);
        mDescriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //hide KB
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                attemptSendTicket();
            }
        });
        mDescriptionEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == 66) {
                    mLieuEditText.requestFocus();
                }
                return false;
            }
        });

        mRelativeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptSendTicket() {
        if (mTicketTask != null) {
            return;
        }

        // Reset errors.
        mDescriptionEditText.setError(null);
        mLieuEditText.setError(null);


        // Store values at the time of the login attempt.
        String description = mDescriptionEditText.getText().toString();
        String lieu = mLieuEditText.getText().toString();

        boolean cancel = false;
        EditText focusEditText = null;


        // Check for a valid description
        if (mDescriptionEditText.getText().length() == 0) {
            mDescriptionEditText.setError("Veuillez rentrer une description !");
            focusEditText = mDescriptionEditText;
            cancel = true;
        }
        // Check for a valid description
        if (mLieuEditText.getText().length() == 0) {
            mLieuEditText.setError("Veuillez rentrer un lieu !");
            focusEditText = mLieuEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusEditText.requestFocus();
        } else {


            mTicketTask = new TicketTask(description,lieu);
            mTicketTask.execute((Void) null);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_form_tickets, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class TicketTask extends AsyncTask<Void, Void, Boolean> {

        private final String mDescription;
        private final String mLieu;

        TicketTask(String description, String lieu) {
            mDescription = description;
            mLieu = lieu;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            int success;
            try {
                // Building Parameters
                List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                parameters.add(new BasicNameValuePair("description", mDescription));
                parameters.add(new BasicNameValuePair("lieu", mLieu));
                parameters.add(new BasicNameValuePair("idhotelitem",getIntent().getStringExtra("id_of_item")));
                parameters.add(new BasicNameValuePair("idutilisateurDemande",getIntent().getStringExtra("id_of_user")));
                // getting product details by making HTTP request
                String response = new ServiceHandler().makeServiceCall(
                        CREATE_TICKET_URL, ServiceHandler.POST, parameters);
                Log.d("response", response);

                //Getting JSON Array node
                JSONObject json = new JSONObject(response);

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    //Log.d("Login Successful!", json.toString());
                    Intent i = new Intent(FormTicketsActivity.this, MainActivity.class);
                    i.putExtra("id_of_user", getIntent().getStringExtra("id_of_user"));
                    finish();
                    startActivity(i);
                    return true;
                } else {
                    Log.d("Login Failure!", json.getString(TAG_SUCCESS));
                    return false;

                }


            } catch (Exception e) {
                e.printStackTrace();
            }


            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mTicketTask = null;
            if (success) {
                Context context = getApplicationContext();
                CharSequence text = "Succès";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            } else {
                Context context = getApplicationContext();
                CharSequence text = "La demande a échoué (Vérifiez la connexion Internet)";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

            }
        }

        @Override
        protected void onCancelled() {
            mTicketTask = null;
        }
    }
}


