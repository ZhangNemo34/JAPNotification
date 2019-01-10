package com.justanotherpanel.app;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.justanotherpanel.app.adapters.ServiceRecyclerAdapter;
import com.justanotherpanel.app.helper.AppConstants;
import com.justanotherpanel.app.helper.SessionManager;
import com.justanotherpanel.app.models.ServiceModel;
import com.justanotherpanel.app.service.MonitorService;
import com.justanotherpanel.app.views.EditBox;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.editNotifBalance)
    EditBox editNotifBalance;

    @BindView(R.id.txtBalance)
    TextView txtBalance;

    @OnClick(R.id.btnSave)
    void onBtnSaveClicked(FrameLayout btnSave) {
        String balance = editNotifBalance.getText().toString();
        session.saveNotifBalance(balance);

        txtSuccess.setVisibility(View.VISIBLE);
        txtError.setVisibility(View.GONE);
    }

    @OnClick(R.id.txtBack)
    void onBackClicked(TextView txtBack) {
        session.setRemember(false);

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @BindView(R.id.txtSuccess)
    TextView txtSuccess;
    @BindView(R.id.txtError)
    TextView txtError;

    @BindView(R.id.lytAddedServices)
    LinearLayout lytAddedServices;
    @BindView(R.id.lytUpdatedServices)
    LinearLayout lytUpdatedServices;
    @BindView(R.id.lytDeletedServices)
    LinearLayout lytDeletedServices;

    @BindView(R.id.recyclerAdded)
    RecyclerView recyclerAdded;
    @BindView(R.id.recyclerUpdated)
    RecyclerView recyclerUpdated;
    @BindView(R.id.recyclerDeleted)
    RecyclerView recyclerDeleted;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        session = new SessionManager(this);

        String balance = getIntent().getStringExtra(AppConstants.EXTRA_BALANCE);
        txtBalance.setText(balance);
        editNotifBalance.setText(session.getNotifBalance());

        startService(new Intent(getApplicationContext(), MonitorService.class));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        editNotifBalance.setText(session.getNotifBalance());


        String balance = intent.getStringExtra(AppConstants.EXTRA_BALANCE);
        if (balance != null) {
            txtBalance.setText(balance);

            txtSuccess.setVisibility(View.GONE);
            txtError.setText("Current balance is low");
            txtError.setVisibility(View.VISIBLE);
        } else {
            txtBalance.setText(session.getBalance());

            txtSuccess.setVisibility(View.GONE);
            txtError.setText("Service has updated");
            txtError.setVisibility(View.VISIBLE);
        }

        HashMap<Integer, ServiceModel> added = (HashMap<Integer, ServiceModel>)intent.getSerializableExtra(AppConstants.EXTRA_ADDED_SERVICES);
        if (added != null) {
            ServiceRecyclerAdapter addedAdapter = new ServiceRecyclerAdapter(added);
            recyclerAdded.setAdapter(addedAdapter);
            recyclerAdded.setLayoutManager(new LinearLayoutManager(MainActivity.this));

            lytAddedServices.setVisibility(View.VISIBLE);
        }

        HashMap<Integer, ServiceModel> updated = (HashMap<Integer, ServiceModel>)intent.getSerializableExtra(AppConstants.EXTRA_UPDATED_SERVICES);
        if (updated != null) {
            ServiceRecyclerAdapter updatedAdapter = new ServiceRecyclerAdapter(updated);
            recyclerUpdated.setAdapter(updatedAdapter);
            recyclerUpdated.setLayoutManager(new LinearLayoutManager(MainActivity.this));

            lytUpdatedServices.setVisibility(View.VISIBLE);
        }

        HashMap<Integer, ServiceModel> deleted = (HashMap<Integer, ServiceModel>)intent.getSerializableExtra(AppConstants.EXTRA_DELETED_SERVICES);
        if (deleted != null) {
            ServiceRecyclerAdapter deletedAdapter = new ServiceRecyclerAdapter(deleted);
            recyclerDeleted.setAdapter(deletedAdapter);
            recyclerDeleted.setLayoutManager(new LinearLayoutManager(MainActivity.this));

            lytDeletedServices.setVisibility(View.VISIBLE);
        }
    }
}
