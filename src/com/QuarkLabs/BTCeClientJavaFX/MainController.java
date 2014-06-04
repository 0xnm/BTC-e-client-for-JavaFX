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

import com.QuarkLabs.BTCeClientJavaFX.models.ActiveOrder;
import com.QuarkLabs.BTCeClientJavaFX.models.Fund;
import com.QuarkLabs.BTCeClientJavaFX.models.Ticker;
import com.QuarkLabs.BTCeClientJavaFX.networking.App;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.DateFormat;
import java.util.*;

/**
 * Controller for main screen
 */
public class MainController {

    private static final String SOMETHING_WENT_WRONG = "Something went wrong";
    private static final String ERROR_TITLE = "Error: ";
    private static final String PATH_TO_ORDERS_BOOK_LAYOUT = "layouts/ordersbooklayout.fxml";
    private static final String PATH_TO_TRADES_LAYOUT = "layouts/markettrades.fxml";
    private static final String EXCHANGE_CONFIG_PATH = "config/exchange.xml";

    private App app;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button clearLogButton;

    @FXML
    private TableView<Fund> fundsTable;

    @FXML
    private TextArea logField;

    @FXML
    private Button sellButton;

    @FXML
    private Button buyButton;

    @FXML
    private Button showActiveOrdersButton;

    @FXML
    private TableColumn<Ticker, Double> tickersTableLastColumn;

    @FXML
    private TableColumn<Ticker, String> tickersTablePairColumn;

    @FXML
    private TableView<Ticker> tickersTable;

    @FXML
    private TableColumn<Ticker, Double> tickersTableBuyColumn;

    @FXML
    private TableColumn<Ticker, Double> tickersTableFeeColumn;

    @FXML
    private TableColumn<Ticker, Double> tickersTableSellColumn;

    @FXML
    private TableColumn<Fund, String> fundsTableCurrencyColumn;

    @FXML
    private TableColumn<Fund, Double> fundsTableValueColumn;

    @FXML
    private TextField tradeAmountValue;


    @FXML
    private ChoiceBox<String> tradePriceCurrencyType;

    @FXML
    private ChoiceBox<String> tradeCurrencyType;

    @FXML
    private TextField tradePriceValue;

    @FXML
    private Button updateFundsButton;

    @FXML
    private TableView<ActiveOrder> activeOrdersTable;

    @FXML
    private TableColumn<ActiveOrder, Double> activeOrdersAmountColumn;

    @FXML
    private TableColumn<ActiveOrder, Boolean> activeOrdersCancelColumn;

    @FXML
    private TableColumn<ActiveOrder, String> activeOrdersPairColumn;

    @FXML
    private TableColumn<ActiveOrder, Double> activeOrdersRateColumn;

    @FXML
    private TableColumn<ActiveOrder, String> activeOrdersTimeColumn;

    @FXML
    private TableColumn<ActiveOrder, String> activeOrdersTypeColumn;

    private List<String> currencies = new ArrayList<>();
    private List<String> pairs = new ArrayList<>();
    private Map<String, Ticker> tickersData = new HashMap<>();
    private ObservableList<ActiveOrder> activeOrders = FXCollections.observableArrayList();
    private ObservableList<Fund> fundsData = FXCollections.observableArrayList();
    private ObservableList<Ticker> tickers = FXCollections.observableArrayList();


    @FXML
    void initialize() {
        assert clearLogButton != null : "fx:id=\"clearLogButton\" was not injected: check your FXML file 'mainlayout.fxml'.";
        assert fundsTable != null : "fx:id=\"fundsTable\" was not injected: check your FXML file 'mainlayout.fxml'.";
        assert logField != null : "fx:id=\"logField\" was not injected: check your FXML file 'mainlayout.fxml'.";
        assert buyButton != null : "fx:id=\"buyButton\" was not injected: check your FXML file 'mainlayout.fxml'.";
        assert sellButton != null : "fx:id=\"sellButton\" was not injected: check your FXML file 'mainlayout.fxml'.";
        assert showActiveOrdersButton != null : "fx:id=\"showActiveOrdersButton\" was not injected: check your FXML file 'mainlayout.fxml'.";
        assert tickersTableLastColumn != null : "fx:id=\"tickerTableLastColumn\" was not injected: check your FXML file 'mainlayout.fxml'.";
        assert tickersTablePairColumn != null : "fx:id=\"tickerTablePairColumn\" was not injected: check your FXML file 'mainlayout.fxml'.";
        assert tickersTable != null : "fx:id=\"tickersTable\" was not injected: check your FXML file 'mainlayout.fxml'.";
        assert tickersTableBuyColumn != null : "fx:id=\"tickersTableBuyColumn\" was not injected: check your FXML file 'mainlayout.fxml'.";
        assert tickersTableFeeColumn != null : "fx:id=\"tickersTableFeeColumn\" was not injected: check your FXML file 'mainlayout.fxml'.";
        assert tickersTableSellColumn != null : "fx:id=\"tickersTableSellColumn\" was not injected: check your FXML file 'mainlayout.fxml'.";
        assert tradeAmountValue != null : "fx:id=\"tradeAmountValue\" was not injected: check your FXML file 'mainlayout.fxml'.";
        assert tradePriceCurrencyType != null : "fx:id=\"tradeCurrencyPriceValue\" was not injected: check your FXML file 'mainlayout.fxml'.";
        assert tradeCurrencyType != null : "fx:id=\"tradeCurrencyType\" was not injected: check your FXML file 'mainlayout.fxml'.";
        assert tradePriceValue != null : "fx:id=\"tradePriceValue\" was not injected: check your FXML file 'mainlayout.fxml'.";
        assert updateFundsButton != null : "fx:id=\"updateFundsButton\" was not injected: check your FXML file 'mainlayout.fxml'.";
        assert fundsTableCurrencyColumn != null : "fx:id=\"fundsTableCurrencyColumn\" was not injected: check your FXML file 'mainlayout.fxml'.";
        assert fundsTableValueColumn != null : "fx:id=\"fundsTableValueColumn\" was not injected: check your FXML file 'mainlayout.fxml'.";
        assert activeOrdersTable != null : "fx:id=\"fundsTableValueColumn\" was not injected: check your FXML file 'mainlayout.fxml'.";
        assert activeOrdersAmountColumn != null : "fx:id=\"activeOrdersAmountColumn\" was not injected: check your FXML file 'mainlayout.fxml'.";
        assert activeOrdersPairColumn != null : "fx:id=\"activeOrdersPairColumn\" was not injected: check your FXML file 'mainlayout.fxml'.";
        assert activeOrdersRateColumn != null : "fx:id=\"activeOrdersRateColumn\" was not injected: check your FXML file 'mainlayout.fxml'.";
        assert activeOrdersTimeColumn != null : "fx:id=\"activeOrdersTimeColumn\" was not injected: check your FXML file 'mainlayout.fxml'.";
        assert activeOrdersTypeColumn != null : "fx:id=\"activeOrdersTypeColumn\" was not injected: check your FXML file 'mainlayout.fxml'.";
        assert activeOrdersCancelColumn != null : "fx:id=\"activeOrdersCancelColumn\" was not injected: check your FXML file 'mainlayout.fxml'.";

        //Holder for all main API methods of exchange
        app = new App();

        //Loading configs
        loadExchangeConfig();

        //Populate choiceboxes at the trading section
        tradeCurrencyType.setItems(FXCollections.observableArrayList(currencies));
        tradeCurrencyType.setValue(currencies.get(0));
        tradePriceCurrencyType.setItems(FXCollections.observableArrayList(currencies));
        tradePriceCurrencyType.setValue(currencies.get(0));

        //Active Orders table
        activeOrdersAmountColumn.setCellValueFactory(new PropertyValueFactory<ActiveOrder, Double>("amount"));
        activeOrdersPairColumn.setCellValueFactory(new PropertyValueFactory<ActiveOrder, String>("pair"));
        activeOrdersRateColumn.setCellValueFactory(new PropertyValueFactory<ActiveOrder, Double>("rate"));
        activeOrdersTimeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ActiveOrder, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ActiveOrder, String> activeOrderStringCellDataFeatures) {
                ActiveOrder activeOrder = activeOrderStringCellDataFeatures.getValue();
                DateFormat dateFormat = DateFormat.getDateTimeInstance();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(activeOrder.getTimestamp() * 1000);
                return new SimpleStringProperty(dateFormat.format(calendar.getTime()));
            }
        });
        activeOrdersTypeColumn.setCellValueFactory(new PropertyValueFactory<ActiveOrder, String>("type"));
        activeOrdersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        activeOrdersCancelColumn.setCellFactory(new Callback<TableColumn<ActiveOrder, Boolean>, TableCell<ActiveOrder, Boolean>>() {
            @Override
            public TableCell<ActiveOrder, Boolean> call(TableColumn<ActiveOrder, Boolean> activeOrderBooleanTableColumn) {
                return new ButtonCell<>(activeOrdersTable);
            }
        });
        activeOrdersCancelColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ActiveOrder, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<ActiveOrder, Boolean> activeOrderBooleanCellDataFeatures) {
                return new SimpleBooleanProperty(true);
            }
        });

        //Tickers Table
        MenuItem showOrdersBook = new MenuItem("Show Orders Book");
        MenuItem showPublicTrades = new MenuItem("Show Public Trades");

        ContextMenu contextMenu = new ContextMenu(showOrdersBook, showPublicTrades);

        tickersTable.setItems(tickers);
        tickersTable.setContextMenu(contextMenu);
        tickersTableBuyColumn.setCellValueFactory(new PropertyValueFactory<Ticker, Double>("buy"));
        tickersTableFeeColumn.setCellValueFactory(new PropertyValueFactory<Ticker, Double>("fee"));
        tickersTableSellColumn.setCellValueFactory(new PropertyValueFactory<Ticker, Double>("sell"));
        tickersTableLastColumn.setCellValueFactory(new PropertyValueFactory<Ticker, Double>("last"));
        tickersTablePairColumn.setCellValueFactory(new PropertyValueFactory<Ticker, String>("pair"));
        tickersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tickersTable.setRowFactory(new Callback<TableView<Ticker>, TableRow<Ticker>>() {
            @Override
            public TableRow<Ticker> call(TableView<Ticker> tickerTableView) {
                return new TableRow<Ticker>() {
                    @Override
                    protected void updateItem(Ticker ticker, boolean b) {
                        super.updateItem(ticker, b);
                        if (!b) {
                            if (tickersData.containsKey(ticker.getPair())) {
                                if (ticker.getLast() < tickersData.get(ticker.getPair()).getLast()) {
                                    setStyle("-fx-control-inner-background: rgba(186, 0, 0, 0.5);");
                                } else if (ticker.getLast() == tickersData.get(ticker.getPair()).getLast()) {
                                    setStyle("-fx-control-inner-background: rgba(215, 193, 44, 0.5);");
                                } else {
                                    setStyle("-fx-control-inner-background: rgba(0, 147, 0, 0.5);");
                                }
                            }
                        }
                    }
                };
            }
        });

        //Menu item to show Orders Book
        showOrdersBook.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Ticker selectedTicker = tickersTable.getSelectionModel().getSelectedItem();
                Parent root;
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(PATH_TO_ORDERS_BOOK_LAYOUT), resources);
                    root = (Parent) fxmlLoader.load();
                    OrdersBookController ordersBookController = fxmlLoader.getController();
                    ordersBookController.injectPair(selectedTicker.getPair());
                    Stage stage = new Stage();
                    stage.setTitle("Orders Book for " + selectedTicker.getPair().replace("_", "/").toUpperCase());
                    stage.setScene(new Scene(root));
                    stage.setResizable(false);
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        //Menu item to show Public Trades
        showPublicTrades.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Ticker selectedTicker = tickersTable.getSelectionModel().getSelectedItem();
                Parent root;
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(PATH_TO_TRADES_LAYOUT), resources);
                    root = (Parent) fxmlLoader.load();
                    PublicTradesController publicTradesController = fxmlLoader.getController();
                    publicTradesController.injectPair(selectedTicker.getPair());
                    Stage stage = new Stage();
                    stage.setTitle("Public Trades for " + selectedTicker.getPair().replace("_", "/").toUpperCase());
                    stage.setScene(new Scene(root));
                    stage.setResizable(false);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //Funds Table
        fundsTableCurrencyColumn.setCellValueFactory(new PropertyValueFactory<Fund, String>("currency"));
        fundsTableValueColumn.setCellValueFactory(new PropertyValueFactory<Fund, Double>("value"));
        fundsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        fundsTable.setItems(fundsData);

        //Task to load tickers data from server
        final javafx.concurrent.Service loadTickersService = new javafx.concurrent.Service() {
            @Override
            protected Task createTask() {
                Task<JSONObject> loadTickers = new Task<JSONObject>() {
                    @Override
                    protected JSONObject call() throws Exception {
                        String[] pairsArray = new String[pairs.size()];
                        pairsArray = pairs.toArray(pairsArray);
                        return App.getPairInfo(pairsArray);
                    }
                };

                loadTickers.setOnFailed(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent workerStateEvent) {
                        logField.appendText(workerStateEvent.getSource().getException().getMessage() + "\r\n");
                    }
                });

                loadTickers.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent workerStateEvent) {
                        JSONObject jsonObject = (JSONObject) workerStateEvent.getSource().getValue();

                        //ugly hack to store old values
                        //dump old values to tickersData
                        //TODO think about better solution
                        if (tickers.size() != 0) {
                            for (Ticker x : tickers) {
                                tickersData.put(x.getPair(), x);
                            }
                        }
                        tickers.clear();
                        for (Iterator iterator = jsonObject.keys(); iterator.hasNext(); ) {
                            String key = (String) iterator.next();
                            JSONObject data = jsonObject.getJSONObject(key);
                            Ticker ticker = new Ticker();
                            ticker.setPair(key);
                            ticker.setUpdated(data.optLong("updated"));
                            ticker.setAvg(data.optDouble("avg"));
                            ticker.setBuy(data.optDouble("buy"));
                            ticker.setSell(data.optDouble("sell"));
                            ticker.setHigh(data.optDouble("high"));
                            ticker.setLast(data.optDouble("last"));
                            ticker.setLow(data.optDouble("low"));
                            ticker.setVol(data.optDouble("vol"));
                            ticker.setVolCur(data.optDouble("vol_cur"));
                            tickers.add(ticker);
                        }

                    }
                });
                return loadTickers;
            }
        };

        //Update tickers every 15 seconds
        //TODO better solution is required
        Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                loadTickersService.restart();
            }
        }), new KeyFrame(Duration.seconds(15)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.playFromStart();


    }

    /**
     * Loads exchange pairs and currencies from XML file, displays error message at the Log field in case of any Exception
     */
    private void loadExchangeConfig() {

        try {
            InputStream inputStream = new FileInputStream(EXCHANGE_CONFIG_PATH);
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("currency");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;
                    currencies.add(eElement.getTextContent());
                }
            }
            nodeList = document.getElementsByTagName("pair");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;
                    pairs.add(eElement.getTextContent());
                }
            }
            inputStream.close();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            logField.appendText(e.getMessage() + "\r\n");
        }
    }

    /**
     * Gets Active Orders data from server, displays error message at the Log field in case of any Exception
     */
    @FXML
    private void showActiveOrders() {

        Task<JSONObject> task = new Task<JSONObject>() {
            @Override
            protected JSONObject call() throws Exception {
                try {
                    return app.getActiveOrders();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return new JSONObject();
            }
        };
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                JSONObject jsonObject = (JSONObject) workerStateEvent.getSource().getValue();
                if (jsonObject.optInt("success") == 1) {
                    JSONObject data = jsonObject.getJSONObject("return");
                    for (Iterator iterator = data.keys(); iterator.hasNext(); ) {
                        String key = (String) iterator.next();
                        ActiveOrder activeOrder = new ActiveOrder();
                        activeOrder.setId(Long.parseLong(key));
                        activeOrder.setAmount(data.optJSONObject(key).optDouble("amount"));
                        activeOrder.setRate(data.optJSONObject(key).optDouble("rate"));
                        activeOrder.setStatus(data.optJSONObject(key).optInt("status"));
                        activeOrder.setTimestamp(data.optJSONObject(key).optLong("timestamp_created"));
                        activeOrder.setPair(data.optJSONObject(key).optString("pair"));
                        activeOrder.setType(data.optJSONObject(key).optString("type"));
                        activeOrders.add(activeOrder);
                    }
                    activeOrdersTable.setItems(activeOrders);
                } else {
                    logField.appendText(ERROR_TITLE + jsonObject.optString("error", SOMETHING_WENT_WRONG) + "\r\n");
                }

            }
        });
        task.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                logField.appendText(workerStateEvent.getSource().getException().getMessage() + "\r\n");
            }
        });
        Thread thread = new Thread(task);
        thread.start();
    }

    /**
     * Helper function to clear Log area
     */
    @FXML
    private void clearLog() {
        logField.clear();
    }

    /**
     * Gets funds data from server, displays error message at the Log field in case of any Exception
     */
    @FXML
    private void updateFunds() {
        Task<JSONObject> task = new Task<JSONObject>() {

            @Override
            protected JSONObject call() throws Exception {
                try {
                    return app.getAccountInfo();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return new JSONObject();
            }
        };
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                JSONObject jsonObject = (JSONObject) workerStateEvent.getSource().getValue();
                //TODO make a check for errors
                if (jsonObject.optInt("success", 0) == 1) {
                    parseFundsObject(jsonObject.optJSONObject("return").optJSONObject("funds"));
                } else {
                    logField.appendText(ERROR_TITLE + jsonObject.optString("error", SOMETHING_WENT_WRONG) + "\r\n");
                }
            }
        });
        task.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                logField.appendText(workerStateEvent.getSource().getException().getMessage() + "\r\n");
            }
        });
        Thread thread = new Thread(task);
        thread.start();

    }

    /**
     * Reads data from Trading section, sends trade request to server
     * Displays error message at the Log field in case of any Exception
     *
     * @param event Source fired an event (either "Buy" or "Sell" button)
     */
    @FXML
    private void makeTradeRequest(final ActionEvent event) {

        Task<JSONObject> task = new Task<JSONObject>() {
            @Override
            protected JSONObject call() throws Exception {
                String type;
                String idOfSource = ((Button) event.getSource()).getId();
                if (buyButton.getId().equals(idOfSource)) {
                    type = "buy";
                } else {
                    type = "sell";
                }
                String pair = tradeCurrencyType.getValue().toLowerCase() +
                        "_" + tradePriceCurrencyType.getValue().toLowerCase();

                String rate = tradePriceValue.getText();
                String amount = tradeAmountValue.getText();
                return app.trade(pair, type, rate, amount);
            }
        };
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                JSONObject jsonObject = (JSONObject) workerStateEvent.getSource().getValue();
                if (jsonObject.optInt("success") == 1) {
                    parseFundsObject(jsonObject.optJSONObject("return").optJSONObject("funds"));
                    logField.appendText("Order ID = " + jsonObject.optJSONObject("return").optString("order_id") +
                            " was registered successfully" + "\r\n");
                } else {
                    logField.appendText(ERROR_TITLE + jsonObject.optString("error", SOMETHING_WENT_WRONG) + "\r\n");
                }

            }
        });

        task.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                logField.appendText(workerStateEvent.getSource().getException().getMessage() + "\r\n");
            }
        });
        Thread thread = new Thread(task);
        thread.start();
    }

    /**
     * Parses JSONObject with funds data inside (key = "funds")
     *
     * @param fundsObject Funds JSONObject
     */
    private void parseFundsObject(JSONObject fundsObject) {
        fundsData.clear();
        for (Iterator iterator = fundsObject.keys(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            fundsData.add(new Fund(key, fundsObject.optDouble(key)));
        }
        FXCollections.sort(fundsData, new Comparator<Fund>() {
            @Override
            public int compare(Fund o1, Fund o2) {
                return o1.getCurrency().compareTo(o2.getCurrency());
            }
        });
    }

    /**
     * Class: TableCell with Button inside
     *
     * @param <S> Type of model
     * @param <T> Type of data inside cell
     */
    private class ButtonCell<S, T> extends TableCell<S, T> {

        final Button cellButton = new Button("Cancel");

        ButtonCell(final TableView tblView) {


            cellButton.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent t) {
                    final int selectedIndex = getTableRow().getIndex();
                    //delete the selected item in data
                    Task<JSONObject> cancelOrder = new Task<JSONObject>() {
                        @Override
                        protected JSONObject call() throws Exception {
                            return app.cancelOrder(activeOrders.get(selectedIndex).getId());
                        }
                    };
                    cancelOrder.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent workerStateEvent) {
                            JSONObject jsonObject = (JSONObject) workerStateEvent.getSource().getValue();
                            if (jsonObject.optInt("success") == 1) {
                                activeOrders.remove(selectedIndex);
                                parseFundsObject(jsonObject.optJSONObject("return").optJSONObject("funds"));
                            } else {
                                logField.appendText(ERROR_TITLE + jsonObject.optString("error", SOMETHING_WENT_WRONG) + "\r\n");
                            }
                        }
                    });
                    cancelOrder.setOnFailed(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent workerStateEvent) {
                            logField.appendText(workerStateEvent.getSource().getException().getMessage() + "\r\n");
                        }
                    });
                    Thread thread = new Thread(cancelOrder);
                    thread.run();
                }
            });
        }

        @Override
        protected void updateItem(T o, boolean b) {
            super.updateItem(o, b);
            //if non-empty
            if (!b) {
                setGraphic(cellButton);
            }
        }
    }
}
