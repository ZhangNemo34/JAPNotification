package com.justanotherpanel.app.helper.network;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RawRequester extends Request<String> {
    private Response.Listener<String> listener;
    private Map<String, String> params;
    private Map<String, String> headers;

    public RawRequester(int method, String url, Response.Listener<String> responseListener, Response.ErrorListener listener) {
        super(method, url, listener);

        this.listener = responseListener;
    }

    public RawRequester(int method, String url, Map<String, String> params, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);

        this.listener = responseListener;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    public Map<String, String> getHeaders() throws AuthFailureError {
        if (headers == null)
            return new HashMap<>();
        else
            return headers;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(String response) {
        listener.onResponse(response);
    }
}
