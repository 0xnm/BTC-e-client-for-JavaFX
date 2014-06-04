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

package com.QuarkLabs.BTCeClientJavaFX.cellfactories;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.text.DecimalFormat;

public class CenteredCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

    private static final DecimalFormat decimalFormat = new DecimalFormat("#.######");

    //Field to make text uppercase
    private boolean uppercase;
    //Field to make text bold
    private boolean bold;
    private boolean decimal;

    public boolean isDecimal() {
        return decimal;
    }

    public void setDecimal(boolean decimal) {
        this.decimal = decimal;
    }

    public boolean isUppercase() {
        return uppercase;
    }

    public void setUppercase(boolean uppercase) {
        this.uppercase = uppercase;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    @Override
    public TableCell<S, T> call(TableColumn<S, T> stTableColumn) {
        return new TableCell<S, T>() {
            @Override
            protected void updateItem(T t, boolean b) {
                if (t == getItem()) {
                    return;
                }
                super.updateItem(t, b);
                if (t != null) {
                    setAlignment(Pos.CENTER);
                    if (uppercase) {
                        setText(t.toString().replace("_", "/").toUpperCase());
                    } else {
                        setText(t.toString().replace("_", "/"));
                    }

                    if (decimal) {
                        setText(decimalFormat.format(t));
                    }
                    if (bold) {
                        setStyle("-fx-font-weight: bold");
                    }
                }
            }
        };
    }
}
