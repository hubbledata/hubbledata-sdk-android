package com.netease.mobidroid.demo;

import android.util.Log;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by zhangdan on 2017/11/28.
 */

public class MyClient implements DAClient {
    private String TAG = "MyClient";
    private SSLSocketFactory mFoundSSLFactory;

    public MyClient() {
        // By default, we use a clean, FACTORY default SSLSocket. In general this is the right
        // thing to do.
        try {
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);
            mFoundSSLFactory = sslContext.getSocketFactory();
        } catch (final GeneralSecurityException e) {
            Log.i("TAG", "System has no SSL support. Built-in events editor will not be available", e);
            mFoundSSLFactory = null;
        }
    }

    @Override
    public Pair<Integer, String> execute(String endpointUrl, Map<String, String> headers, byte[] dataEncrypted) {
        byte[] response = performRequest(endpointUrl, headers, dataEncrypted);
        if (response == null) {
            Log.d(TAG, "the response is null, abort");
            return null;
        }
        try {
            JSONObject json = new JSONObject(new String(response));
            int code = json.optInt("code");
            if (code == 200) {
                Log.d(TAG, "Finish uploading: " + code);
                return new Pair<Integer, String>(code, "Finish uploading");
            } else {
                Log.d(TAG, "Failed to upload, resonse code: " + code);
                return new Pair<Integer, String>(code, "Failed to upload");
            }
        } catch (JSONException e) {
            Log.d(TAG, "Failed to transfer response from the internet to JsonObject: " + e.getLocalizedMessage());
            return new Pair<Integer, String>(4004, "Failed to transfer response from the internet to JsonObject");
        }
    }

    public byte[] performRequest(String endpointUrl, Map<String, String> headers, byte[] dataEncrypted) {
        Log.v(TAG, "Attempting request to " + endpointUrl);
        byte[] response = null;

        // the while(retries) loop is a workaround for a bug in some Android HttpURLConnection
        // libraries- The underlying library will attempt to reuse stale connections,
        // meaning the second (or every other) attempt to connect fails with an EOFException.
        // Apparently this nasty retry logic is the current state of the workaround art.
        int retries = 0;
        boolean succeeded = false;
        while (retries < 3 && !succeeded) {
            InputStream in = null;
            OutputStream out = null;
            BufferedOutputStream bout = null;
            HttpURLConnection connection = null;

            try {
                final URL url = new URL(endpointUrl);
                connection = (HttpURLConnection) url.openConnection();
                if (null != mFoundSSLFactory && connection instanceof HttpsURLConnection) {
                    ((HttpsURLConnection) connection).setSSLSocketFactory(mFoundSSLFactory)
                    ;
                }
                connection.setConnectTimeout(2000);
                connection.setReadTimeout(10000);
                if (headers != null) {
                    for (String key : headers.keySet()) {
                        connection.setRequestProperty(key, headers.get(key));
                    }
                }
                connection.setFixedLengthStreamingMode(dataEncrypted.length);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                out = connection.getOutputStream();
                bout = new BufferedOutputStream(out);
                bout.write(dataEncrypted);
                bout.flush();
                bout.close();
                bout = null;
                out.close();
                out = null;
                in = connection.getInputStream();
                response = slurp(in);
                in.close();
                in = null;
                succeeded = true;
            } catch (final Exception e) {
                Log.d(TAG, "Error occured during data sending, abort reason: " + e.getMessage());
                retries = retries + 1;
            } finally {
                if (null != bout)
                    try {
                        bout.close();
                    } catch (final IOException e) {
                        ;
                    }
                if (null != out)
                    try {
                        out.close();
                    } catch (final IOException e) {
                        ;
                    }
                if (null != in)
                    try {
                        in.close();
                    } catch (final IOException e) {
                        ;
                    }
                if (null != connection)
                    connection.disconnect();
            }
        }
        if (retries >= 3) {
            Log.v(TAG, "Could not connect to DATracker service after three retries.");
        }
        return response;
    }

    private static byte[] slurp(final InputStream inputStream)
            throws IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[8192];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }
}
