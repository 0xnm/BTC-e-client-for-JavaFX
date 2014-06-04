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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * BTC-e API: Class for wrapping all main API methods
 */
public class App {

    private static AuthRequest authRequest = new AuthRequest(System.currentTimeMillis() / 1000L);

    public static JSONObject getPairInfo(String[] pairs) {
        String url = "https://btc-e.com/api/3/ticker/";
        for (String x : pairs) {
            url += x.replace("/", "_").toLowerCase() + "-";
        }
        return new JSONObject(SimpleRequest.makeRequest(url));
    }

    public static JSONObject getOrdersBook(String pair) {
        String url = "https://btc-e.com/api/2/" + pair + "/depth";
        return new JSONObject(SimpleRequest.makeRequest(url));
    }

    public static JSONArray getPublicTrades(String pair) {
        String url = "https://btc-e.com/api/2/" + pair + "/trades";
        return new JSONArray(SimpleRequest.makeRequest(url));
    }

    public JSONObject getAccountInfo() throws UnsupportedEncodingException {
        return authRequest.makeRequest("getInfo", null);

    }

    public void getTransactionsHistory() {

        try {
            authRequest.makeRequest("TransHistory", null);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void getTradeHistory() {

        try {
            authRequest.makeRequest("TradeHistory", null);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getActiveOrders() throws UnsupportedEncodingException {
        return authRequest.makeRequest("ActiveOrders", null);
    }

    public JSONObject trade(String pair, String type, String rate, String amount) throws UnsupportedEncodingException {

        Map<String, String> temp = new HashMap<String, String>(4);
        temp.put("pair", "" + pair);
        temp.put("type", "" + type);
        temp.put("rate", "" + rate);
        temp.put("amount", "" + amount);

        return authRequest.makeRequest("Trade", temp);
    }

    public JSONObject cancelOrder(long OrderId) throws UnsupportedEncodingException {

        Map<String, String> temp = new HashMap<String, String>(1);
        temp.put("order_id", "" + OrderId);
        return authRequest.makeRequest("CancelOrder", temp);

    }

}
