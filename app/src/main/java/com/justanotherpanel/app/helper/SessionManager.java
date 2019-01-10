/*
 * Child Growth Monitor - quick and accurate data on malnutrition
 * Copyright (c) 2018 Markus Matiaschek <mmatiaschek@gmail.com> for Welthungerhilfe
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.justanotherpanel.app.helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Emerald on 2/21/2018.
 */

public class SessionManager {
    private final String TAG = SessionManager.class.getSimpleName();
    private final String PREF_KEY = "pref_key";

    private final String KEY_REMEMBER = "remember";
    private final String KEY_API_KEY = "api_key";
    private final String KEY_BALANCE = "balance";
    private final String KEY_NOTIF_BALANCE = "notif_balance";
    private final String KEY_SERVICES = "services";

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private Context context;

    public SessionManager(Context ctx) {
        this.context = ctx;
        pref = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public boolean setRemember(boolean remember) {
        editor.putBoolean(KEY_REMEMBER, remember);

        return editor.commit();
    }

    public boolean isRemember() {
        return pref.getBoolean(KEY_REMEMBER, false);
    }

    public boolean saveApiKey(String apiKey) {
        editor.putString(KEY_API_KEY, apiKey);

        return editor.commit();
    }

    public String getApiKey() {
        return pref.getString(KEY_API_KEY, "");
    }

    public boolean saveBalance(String balance) {
        editor.putString(KEY_BALANCE, String.valueOf(balance));

        return editor.commit();
    }

    public String getBalance() {
        return pref.getString(KEY_BALANCE, "0");
    }

    public boolean saveNotifBalance(String balance) {
        editor.putString(KEY_NOTIF_BALANCE, String.valueOf(balance));

        return editor.commit();
    }

    public String getNotifBalance() {
        return pref.getString(KEY_NOTIF_BALANCE, "0");
    }

    public boolean saveServices(String services) {
        editor.putString(KEY_SERVICES, services);

        return editor.commit();
    }

    public String getServices() {
        return pref.getString(KEY_SERVICES, "");
    }
}
