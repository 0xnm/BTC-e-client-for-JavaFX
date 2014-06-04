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

import com.QuarkLabs.BTCeClientJavaFX.networking.App;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class of the app
 */
public class Main extends Application {

    private static final String PATH_TO_MAIN_LAYOUT = "layouts/mainlayout.fxml";
    private static final String PATH_TO_STYLE = "style/JMetroLightTheme.css";
    private static final String APP_TITLE = "BTC-e client";
    private App app;
    private MainController mainController;
    private OrdersBookController ordersBookController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource(PATH_TO_MAIN_LAYOUT));
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.getScene().getStylesheets().add(getClass().getResource(PATH_TO_STYLE).toExternalForm());
        primaryStage.show();
    }


}
