/*
 * BTC-e client for JavaFX
 * Copyright (C) 2014  QuarkDev Solutions <quarkdev.solutions@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.QuarkLabs.BTCeClientJavaFX.networking;

import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class responsible for all requests with API key required
 */
public class AuthRequest {
    private static final String PATH_TO_API_KEY_CONFIG = "config/API.txt";
    private static final String TRADE_API_URL = "https://btc-e.com/tapi";

    static {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(PATH_TO_API_KEY_CONFIG));
            String temp;
            while ((temp = reader.readLine()) != null) {
                String[] values = temp.split(":");
                if ("Key".equals(values[0])) {
                    key = values[1];
                } else if ("Secret".equals(values[0])) {
                    secret = values[1];
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String key;
    private static String secret;
    private long nonce;
    private Mac mac;
    private SecretKeySpec _key;

    public AuthRequest(long nonce) {
        this.nonce = nonce;
    }

    /**
     * Converts byte array to HEX string
     *
     * @param array Byte array
     * @return HEX String
     */
    private static String byteArrayToHexString(byte[] array) {

        StringBuffer hexString = new StringBuffer();
        for (byte b : array) {
            int intVal = b & 0xff;
            if (intVal < 0x10)
                hexString.append("0");
            hexString.append(Integer.toHexString(intVal));
        }
        return hexString.toString();
    }

    public JSONObject makeRequest(String method, Map<String, String> arguments) throws UnsupportedEncodingException {
        if (method == null) {
            return null;
        }
        if (arguments == null) {
            arguments = new HashMap<>();
        }
        arguments.put("method", method);
        arguments.put("nonce", "" + ++nonce);
        String postData = "";
        for (Iterator it = arguments.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, String> ent = (Map.Entry<String, String>) it.next();
            if (postData.length() > 0) {
                postData += "&";
            }
            postData += ent.getKey() + "=" + ent.getValue();
        }
        try {
            _key = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA512");
        } catch (UnsupportedEncodingException uee) {
            System.err.println("Unsupported encoding exception: " + uee.toString());
            return null;
        }

        try {
            mac = Mac.getInstance("HmacSHA512");
        } catch (NoSuchAlgorithmException nsae) {
            System.err.println("No such algorithm exception: " + nsae.toString());
            return null;
        }

        try {
            mac.init(_key);
        } catch (InvalidKeyException ike) {
            System.err.println("Invalid key exception: " + ike.toString());
            return null;
        }
        StringBuilder out = new StringBuilder();
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) (new URL(TRADE_API_URL)).openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Key", key);
            String sign = byteArrayToHexString(mac.doFinal(postData.getBytes("UTF-8")));
            urlConnection.setRequestProperty("Sign", sign);
            urlConnection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
            wr.writeBytes(postData);
            wr.flush();
            wr.close();
            if (urlConnection.getResponseCode() == 200) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;

                while ((line = rd.readLine()) != null) {
                    out.append(line);
                }
                rd.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new JSONObject(out.toString());
    }
}
