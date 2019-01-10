package com.justanotherpanel.app.helper;

public class AppConstants {
    public static final int BALANCE_MONITOR_INTERVAL = 25 * 1000; // 10 secs

    public static final String API_URL = "https://justanotherpanel.com/api/v2";
    public static final String API_GET_BALANCE = "https://justanotherpanel.com/api/v2?key=%API_KEY%&action=balance";
    public static final String API_GET_SERVICES = "https://justanotherpanel.com/api/v2?key=%API_KEY%&action=services";

    public static final String EXTRA_BALANCE = "extra_balance";
    public static final String EXTRA_ADDED_SERVICES = "extra_added_services";
    public static final String EXTRA_UPDATED_SERVICES = "extra_updated_services";
    public static final String EXTRA_DELETED_SERVICES = "extra_deleted_services";
}
