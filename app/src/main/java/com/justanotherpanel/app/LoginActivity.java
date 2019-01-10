package com.justanotherpanel.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.justanotherpanel.app.helper.AppConstants;
import com.justanotherpanel.app.helper.SessionManager;
import com.justanotherpanel.app.helper.network.ObjectRequester;
import com.justanotherpanel.app.views.EditBox;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class LoginActivity extends AppCompatActivity {
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @OnTouch(R.id.container)
    boolean handleKeyboard(RelativeLayout container, MotionEvent event) {
        hideKeyboard();

        btnGetApi.setVisibility(View.VISIBLE);

        editApi.setHintTextColor(getResources().getColor(R.color.colorGrey));
        editApi.setTextColor(getResources().getColor(R.color.colorBlack));
        txtError.setVisibility(View.GONE);

        return true;
    }

    @OnClick(R.id.lytRemember)
    void onRemember(LinearLayout lytRemember) {
        checkRemember.setChecked(!checkRemember.isChecked());
    }

    @OnClick(R.id.btnLogin)
    void doLogin(FrameLayout btnLogin) {
        if (prgLogin.getVisibility() == View.VISIBLE)
            return;

        if (!AppController.getInstance().isNetworkOnline()) {
            txtError.setVisibility(View.VISIBLE);
            txtError.setText(R.string.error_network);
            return;
        }
        if (!validate()) {
            return;
        }

        login();
    }

    @OnClick(R.id.btnGetApi)
    void getAPIKey(TextView btnGetApi) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://justanotherpanel.com"));
        startActivity(browserIntent);
    }

    @OnClick(R.id.txtJAP)
    void openJAP(TextView txtJAP) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://justanotherpanel.com/account"));
        startActivity(browserIntent);
    }

    @BindView(R.id.btnGetApi)
    TextView btnGetApi;
    @BindView(R.id.editApi)
    EditBox editApi;
    @BindView(R.id.txtError)
    TextView txtError;
    @BindView(R.id.checkRemember)
    AppCompatCheckBox checkRemember;
    @BindView(R.id.prgLogin)
    ProgressBar prgLogin;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        session = new SessionManager(LoginActivity.this);
        if (session.isRemember() && !session.getApiKey().isEmpty()) {
            editApi.setText(session.getApiKey());
            checkRemember.setChecked(session.isRemember());
            login();
        } else {
            editApi.setText(session.getApiKey());
            checkRemember.setChecked(false);
        }
    }

    private boolean validate() {
        boolean status = true;

        if (editApi.getText().toString().isEmpty()) {
            editApi.setErrorResult();
            btnGetApi.setVisibility(View.GONE);
            txtError.setVisibility(View.VISIBLE);
            txtError.setText(R.string.error_api_empty);
            status = false;
        }

        return status;
    }

    private void login() {
        prgLogin.setVisibility(View.VISIBLE);

        String url = AppConstants.API_GET_BALANCE.replace("%API_KEY%", editApi.getText().toString());

        ObjectRequester request = new ObjectRequester(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("error")) {
                        txtError.setVisibility(View.VISIBLE);
                        txtError.setText(response.getString("error"));
                        editApi.setErrorResult();
                    } else {
                        session.saveApiKey(editApi.getText().toString());
                        session.setRemember(checkRemember.isChecked());
                        session.saveBalance(response.getString("balance"));

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra(AppConstants.EXTRA_BALANCE, response.getString("balance"));
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                prgLogin.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                prgLogin.setVisibility(View.GONE);

                txtError.setVisibility(View.VISIBLE);
                txtError.setText(R.string.error_network_timeout);
            }
        });
        AppController.getInstance().addToRequestQueue(request);
    }
}
