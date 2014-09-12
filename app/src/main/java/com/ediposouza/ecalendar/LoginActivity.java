package com.ediposouza.ecalendar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends ActionBarActivity {

    private static final String SP_NAME = "lg";
    private static final String SP_LAST_USER = "lastUser";

    //To inform which fragment is showing
    private boolean registering;

    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        sharedPrefs = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    /*
    * Change between Login and New User Fragments
    */
    private void swapFragments(){
        Fragment fragment = (registering) ? new PlaceholderFragment() : new NewUserFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        registering = !registering;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment extends Fragment {

        private EditText etUser;
        private EditText etPass;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            etUser = (EditText) view.findViewById(R.id.etUser);
            etPass = (EditText) view.findViewById(R.id.etPass);
            checkLastUser();
            Button btLogin = (Button) view.findViewById(R.id.btLogin);
            Button btNewUser = (Button) view.findViewById(R.id.btNewUser);
            btLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLoginClick();
                }
            });
            btNewUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    swapFragments();
                }
            });
        }

        /*
        * Verify and get the last user name
        */
        private void checkLastUser() {
            String lastUser = sharedPrefs.getString(SP_LAST_USER, "");
            if("".equals(lastUser))
                etUser.requestFocus();
            else{
                etUser.setText(lastUser);
                etPass.requestFocus();
            }
        }

        private void onLoginClick() {
            String userName = etUser.getText().toString().trim();
            String pass = etPass.getText().toString().trim();
            int spUserHash = sharedPrefs.getInt(userName, 0);
            int userHash = userName.concat(pass).hashCode();
            if(spUserHash != userHash){
                etPass.setError(getString(R.string.login_error_user_pass));
                Toast.makeText(getActivity(), getString(R.string.login_error_user_pass), Toast.LENGTH_SHORT).show();
                return;
            }
            sharedPrefs.edit().putString(SP_LAST_USER, userName).apply();
            //start home
            Intent i = new Intent(getActivity(), HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class NewUserFragment extends Fragment {

        private EditText etUser;
        private EditText etPass;
        private EditText etPassConfirm;

        public NewUserFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_new_user, container, false);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            etUser = (EditText) view.findViewById(R.id.etUser);
            etPass = (EditText) view.findViewById(R.id.etPass);
            etPassConfirm = (EditText) view.findViewById(R.id.etPassConfirm);
            etUser.requestFocus();
            Button btRegister = (Button) view.findViewById(R.id.btRegister);
            Button btCancel = (Button) view.findViewById(R.id.btCancel);
            btRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRegisterClick();
                }
            });
            btCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    swapFragments();
                }
            });
        }

        private void onRegisterClick() {
            String pass = etPass.getText().toString().trim();
            if(!pass.equals(etPassConfirm.getText().toString())) {
                etPass.setError(getString(R.string.login_error_pass_match));
                etPassConfirm.setError(getString(R.string.login_error_pass_match));
                etPass.requestFocus();
                return;
            }
            String userKey = etUser.getText().toString().trim();
            int userHash = userKey.concat(pass).hashCode();
            if(sharedPrefs.edit().putInt(userKey, userHash).commit()) {
                swapFragments();
                Toast.makeText(getActivity(), getString(R.string.login_reg_success), Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(getActivity(), getString(R.string.login_error_writing), Toast.LENGTH_SHORT).show();
        }
    }

}
