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

package com.QuarkLabs.BTCeClientJavaFX;

import com.QuarkLabs.BTCeClientJavaFX.models.PublicTrade;
import com.QuarkLabs.BTCeClientJavaFX.networking.App;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;

/**
 * Controller for Public Trades layout
 */
public class PublicTradesController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableView<PublicTrade> publicTradesTable;

    @FXML
    private TableColumn<PublicTrade, Double> publicTradesTableAmountColumn;

    @FXML
    private TableColumn<PublicTrade, String> publicTradesTableDateColumn;

    @FXML
    private TableColumn<PublicTrade, String> publicTradesTableItemColumn;

    @FXML
    private TableColumn<PublicTrade, Double> publicTradesTablePriceColumn;

    @FXML
    private TableColumn<PublicTrade, String> publicTradesTablePriceCurrencyColumn;

    @FXML
    private TableColumn<PublicTrade, Long> publicTradesTableTIDColumn;

    @FXML
    private TableColumn<PublicTrade, String> publicTradesTableTradeTypeColumn;

    private ObservableList<PublicTrade> publicTrades = FXCollections.observableArrayList();
    private String pair;


    @FXML
    void initialize() {
        assert publicTradesTable != null : "fx:id=\"publicTradesTable\" was not injected: check your FXML file 'markettrades.fxml'.";
        assert publicTradesTableAmountColumn != null : "fx:id=\"publicTradesTableAmountColumn\" was not injected: check your FXML file 'markettrades.fxml'.";
        assert publicTradesTableDateColumn != null : "fx:id=\"publicTradesTableDateColumn\" was not injected: check your FXML file 'markettrades.fxml'.";
        assert publicTradesTableItemColumn != null : "fx:id=\"publicTradesTableItemColumn\" was not injected: check your FXML file 'markettrades.fxml'.";
        assert publicTradesTablePriceColumn != null : "fx:id=\"publicTradesTablePriceColumn\" was not injected: check your FXML file 'markettrades.fxml'.";
        assert publicTradesTablePriceCurrencyColumn != null : "fx:id=\"publicTradesTablePriceCurrencyColumn\" was not injected: check your FXML file 'markettrades.fxml'.";
        assert publicTradesTableTIDColumn != null : "fx:id=\"publicTradesTableTIDColumn\" was not injected: check your FXML file 'markettrades.fxml'.";
        assert publicTradesTableTradeTypeColumn != null : "fx:id=\"publicTradesTableTradeTypeColumn\" was not injected: check your FXML file 'markettrades.fxml'.";

        publicTradesTable.setItems(publicTrades);
        publicTradesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        publicTradesTableAmountColumn.setCellValueFactory(new PropertyValueFactory<PublicTrade, Double>("amount"));
        publicTradesTableDateColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PublicTrade, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PublicTrade, String> publicTradeStringCellDataFeatures) {
                PublicTrade publicTrade = publicTradeStringCellDataFeatures.getValue();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(publicTrade.getDate() * 1000);
                DateFormat dateFormat = DateFormat.getDateTimeInstance();
                return new SimpleStringProperty(dateFormat.format(calendar.getTime()));
            }
        });
        publicTradesTableItemColumn.setCellValueFactory(new PropertyValueFactory<PublicTrade, String>("item"));
        publicTradesTablePriceColumn.setCellValueFactory(new PropertyValueFactory<PublicTrade, Double>("price"));
        publicTradesTablePriceCurrencyColumn.setCellValueFactory(new PropertyValueFactory<PublicTrade, String>("priceCurrency"));
        publicTradesTableTIDColumn.setCellValueFactory(new PropertyValueFactory<PublicTrade, Long>("tid"));
        publicTradesTableTradeTypeColumn.setCellValueFactory(new PropertyValueFactory<PublicTrade, String>("tradeType"));

        Task<JSONArray> loadPublicTrades = new Task<JSONArray>() {
            @Override
            protected JSONArray call() throws Exception {
                return App.getPublicTrades(pair);
            }
        };
        loadPublicTrades.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                JSONArray jsonArray = (JSONArray) workerStateEvent.getSource().getValue();
                publicTrades.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    PublicTrade publicTrade = new PublicTrade();
                    publicTrade.setDate(item.getLong("date"));
                    publicTrade.setAmount(item.getDouble("amount"));
                    publicTrade.setItem(item.getString("item"));
                    publicTrade.setPrice(item.getDouble("price"));
                    publicTrade.setPriceCurrency(item.getString("price_currency"));
                    publicTrade.setTid(item.getLong("tid"));
                    publicTrade.setTradeType(item.getString("trade_type"));
                    publicTrades.add(publicTrade);
                }
            }
        });
        Thread thread = new Thread(loadPublicTrades);
        thread.start();
    }

    /**
     * Loads public trades data
     *
     * @param pair Currency pair
     */
    void injectPair(final String pair) {
        this.pair = pair;
    }
}
