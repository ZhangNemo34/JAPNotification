package com.justanotherpanel.app.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.JsonParser;
import com.justanotherpanel.app.AppController;
import com.justanotherpanel.app.MainActivity;
import com.justanotherpanel.app.R;
import com.justanotherpanel.app.helper.AppConstants;
import com.justanotherpanel.app.helper.SessionManager;
import com.justanotherpanel.app.helper.network.ArrayRequester;
import com.justanotherpanel.app.helper.network.ObjectRequester;
import com.justanotherpanel.app.helper.network.RawRequester;
import com.justanotherpanel.app.models.ServiceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;
import static android.app.NotificationManager.IMPORTANCE_HIGH;
import static com.android.volley.Request.Method.GET;

public class MonitorService extends Service {
    private final String TAG = MonitorService.class.getSimpleName();
    private SessionManager session;
    private Timer timer = new Timer();
    private boolean notified = false;
    private String prevNotifBalance = "";

    public void onCreate() {
        session = new SessionManager(getApplicationContext());

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkBalance();
            }
        }, 0, AppConstants.BALANCE_MONITOR_INTERVAL);
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void checkBalance() {
        if (session.getApiKey() != null) {
            String balanceUrl = AppConstants.API_GET_BALANCE.replace("%API_KEY%", session.getApiKey());
            String servicesUrl = AppConstants.API_GET_SERVICES.replace("%API_KEY%", session.getApiKey());

            ObjectRequester balanceRequest = new ObjectRequester(balanceUrl, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (!response.has("error")) {
                            String balance = response.getString("balance");
                            Log.e("current balance", balance);

                            if (Double.parseDouble(balance) != Double.parseDouble(session.getBalance())) {
                                session.saveBalance(balance);
                                notified = false;
                            }

                            if (!session.getNotifBalance().equals(prevNotifBalance)) {
                                notified = false;
                                prevNotifBalance = session.getNotifBalance();
                            }

                            if (Double.parseDouble(balance) < Double.parseDouble(prevNotifBalance)) {
                                if (!notified)
                                    notifyLowBalance(balance);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, null);
            AppController.getInstance().addToRequestQueue(balanceRequest);

            RawRequester servicesRequest = new RawRequester(GET, servicesUrl, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    if (!session.getServices().equals(response)) {
                        if (session.getServices() != "") {
                            try {
                                HashMap<Integer, ServiceModel> updatedServices = new HashMap<>();
                                HashMap<Integer, ServiceModel> addedServices = new HashMap<>();

                                JSONArray oJson = new JSONArray(session.getServices());
                                JSONArray nJson = new JSONArray(response);

                                HashMap<Integer, ServiceModel> oServices = new HashMap<>();
                                for (int i = 0; i < oJson.length(); i++) {
                                    ServiceModel service = new ServiceModel();
                                    service.setService(oJson.getJSONObject(i).getInt("service"));
                                    service.setName(oJson.getJSONObject(i).getString("name"));
                                    service.setCategory(oJson.getJSONObject(i).getString("category"));
                                    service.setType(oJson.getJSONObject(i).getString("type"));
                                    service.setRate(oJson.getJSONObject(i).getDouble("rate"));
                                    service.setMin(oJson.getJSONObject(i).getInt("min"));
                                    service.setMax(oJson.getJSONObject(i).getInt("max"));

                                    oServices.put(oJson.getJSONObject(i).getInt("service"), service);
                                }

                                for (int i = 0; i < nJson.length(); i++) {
                                    ServiceModel nVal = new ServiceModel();
                                    nVal.setService(nJson.getJSONObject(i).getInt("service"));
                                    nVal.setName(nJson.getJSONObject(i).getString("name"));
                                    nVal.setCategory(nJson.getJSONObject(i).getString("category"));
                                    nVal.setType(nJson.getJSONObject(i).getString("type"));
                                    nVal.setRate(nJson.getJSONObject(i).getDouble("rate"));
                                    nVal.setMin(nJson.getJSONObject(i).getInt("min"));
                                    nVal.setMax(nJson.getJSONObject(i).getInt("max"));

                                    if (oServices.containsKey(nJson.getJSONObject(i).getInt("service"))) {
                                        ServiceModel oVal = oServices.get(nVal.getService());

                                        if (!oVal.equals(nVal)) {
                                            updatedServices.put(nVal.getService(), nVal);

                                            oServices.remove(nVal.getService());
                                        }
                                    } else {
                                        addedServices.put(nJson.getJSONObject(i).getInt("service"), nVal);
                                    }
                                }

                                notifyServiceChange(addedServices, updatedServices, oServices);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        session.saveServices(response);
                    }
                }
            }, null);
            AppController.getInstance().addToRequestQueue(servicesRequest);
        }
    }

    private void notifyLowBalance(String balance) {
        notified = true;

        NotificationCompat.Builder mBuilder = buildNotification("Current balance is low");


        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(AppConstants.EXTRA_BALANCE, balance);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(new Random().nextInt(100), mBuilder.build());
    }

    private void notifyServiceChange(HashMap added, HashMap updated, HashMap deleted) {
        NotificationCompat.Builder mBuilder = buildNotification("Service has updated");

        Intent intent = new Intent(this, MainActivity.class);
        if (!added.isEmpty()) {
            intent.putExtra(AppConstants.EXTRA_ADDED_SERVICES, added);
        }
        if (!updated.isEmpty()) {
            intent.putExtra(AppConstants.EXTRA_UPDATED_SERVICES, updated);
        }
        if (!deleted.isEmpty()) {
            intent.putExtra(AppConstants.EXTRA_DELETED_SERVICES, deleted);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(new Random().nextInt(100), mBuilder.build());
    }

    private NotificationCompat.Builder buildNotification(String message) {
        String CHANNEL_ID = "jap_notification";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            int importance = IMPORTANCE_HIGH;;

            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, TAG);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        mBuilder.setContentTitle(getResources().getString(R.string.app_name));
        mBuilder.setVibrate(new long[] { 1000, 1000 });
        mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentText(message);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.appicon));
        mBuilder.setSmallIcon(R.drawable.notificon);
        mBuilder.setChannelId(CHANNEL_ID);

        return mBuilder;
    }
}
