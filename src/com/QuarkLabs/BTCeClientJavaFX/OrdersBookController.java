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

import com.QuarkLabs.BTCeClientJavaFX.models.OrdersBookEntry;
import com.QuarkLabs.BTCeClientJavaFX.networking.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for Orders Book layout
 */
public class OrdersBookController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableView<OrdersBookEntry> asksTable;

    @FXML
    private TableColumn<OrdersBookEntry, Double> asksTablePriceColumn;

    @FXML
    private TableColumn<OrdersBookEntry, Double> asksTableVolumeColumn;

    @FXML
    private TableView<OrdersBookEntry> bidsTable;

    @FXML
    private TableColumn<OrdersBookEntry, Double> bidsTablePriceColumn;

    @FXML
    private TableColumn<OrdersBookEntry, Double> bidsTableVolumeColumn;


    private ObservableList<OrdersBookEntry> asks = FXCollections.observableArrayList();
    private ObservableList<OrdersBookEntry> bids = FXCollections.observableArrayList();


    @FXML
    void initialize() {

        assert asksTable != null : "fx:id=\"asksTable\" was not injected: check your FXML file 'ordersbooklayout.fxml'.";
        assert asksTablePriceColumn != null : "fx:id=\"asksTablePriceColumn\" was not injected: check your FXML file 'ordersbooklayout.fxml'.";
        assert asksTableVolumeColumn != null : "fx:id=\"asksTableVolumeColumn\" was not injected: check your FXML file 'ordersbooklayout.fxml'.";
        assert bidsTable != null : "fx:id=\"bidsTable\" was not injected: check your FXML file 'ordersbooklayout.fxml'.";
        assert bidsTablePriceColumn != null : "fx:id=\"bidsTablePriceColumn\" was not injected: check your FXML file 'ordersbooklayout.fxml'.";
        assert bidsTableVolumeColumn != null : "fx:id=\"bidsTableVolumeColumn\" was not injected: check your FXML file 'ordersbooklayout.fxml'.";

        asksTable.setItems(asks);
        bidsTable.setItems(bids);

        asksTablePriceColumn.setCellValueFactory(new PropertyValueFactory<OrdersBookEntry, Double>("price"));
        asksTableVolumeColumn.setCellValueFactory(new PropertyValueFactory<OrdersBookEntry, Double>("volume"));

        asksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        bidsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        bidsTablePriceColumn.setCellValueFactory(new PropertyValueFactory<OrdersBookEntry, Double>("price"));
        bidsTableVolumeColumn.setCellValueFactory(new PropertyValueFactory<OrdersBookEntry, Double>("volume"));

    }

    /**
     * Loads Orders Book data
     *
     * @param pair Currency pair
     */
    void injectPair(final String pair) {
        Task<JSONObject> loadOrdersBook = new Task<JSONObject>() {
            @Override
            protected JSONObject call() throws Exception {
                return App.getOrdersBook(pair);
            }
        };
        loadOrdersBook.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                JSONObject jsonObject = (JSONObject) workerStateEvent.getSource().getValue();
                JSONArray asksArray = jsonObject.optJSONArray("asks");
                JSONArray bidsArray = jsonObject.optJSONArray("bids");
                for (int i = 0; i < asksArray.length(); i++) {
                    JSONArray item = asksArray.optJSONArray(i);
                    OrdersBookEntry ordersBookEntry = new OrdersBookEntry();
                    ordersBookEntry.setPrice(item.optDouble(0));
                    ordersBookEntry.setVolume(item.optDouble(1));
                    asks.add(ordersBookEntry);
                }
                for (int i = 0; i < bidsArray.length(); i++) {
                    JSONArray item = bidsArray.optJSONArray(i);
                    OrdersBookEntry ordersBookEntry = new OrdersBookEntry();
                    ordersBookEntry.setPrice(item.optDouble(0));
                    ordersBookEntry.setVolume(item.optDouble(1));
                    bids.add(ordersBookEntry);
                }
            }
        });
        Thread thread = new Thread(loadOrdersBook);
        thread.start();
    }

}
