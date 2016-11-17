/*
 * Copyright 2014 by SCSK Corporation.
 * 
 * This file is part of PrimeCloud Controller(TM).
 * 
 * PrimeCloud Controller(TM) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * PrimeCloud Controller(TM) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PrimeCloud Controller(TM). If not, see <http://www.gnu.org/licenses/>.
 */
package jp.primecloud.auto.ui;

import jp.primecloud.auto.ui.util.Icons;
import jp.primecloud.auto.ui.util.ViewProperties;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * <p>
 * 確認ダイアログのテンプレートを生成します。
 * </p>
 *
 */
@SuppressWarnings("serial")
public class DialogConfirm extends Window {

    Callback callback;

    Button okButton;

    Button cancelButton;

    ComboBox confirm;

    public DialogConfirm(String caption, String message) {
        this(caption, message, Buttons.OK);
    }

    public DialogConfirm(String caption, String message, Buttons buttons) {
        this(caption, message, buttons, null);
    }

    public DialogConfirm(String caption, String message, Layout optionLayout) {
        this(caption, message, Buttons.OK, optionLayout);
    }

    public DialogConfirm(String caption, String message, Buttons buttons, Layout optionLayout) {
        super(caption);

        setModal(true);
        setResizable(false);
        setClosable(false);
        setWidth("380px");
        addStyleName("dialog-confirm");
        setIcon(Icons.DLGWARNING.resource());

        VerticalLayout layout = (VerticalLayout) getContent();
        layout.setWidth("100%");
        layout.setMargin(false, true, false, true);
        layout.setSpacing(false);

        if (message != null && message.length() > 0) {
            // メッセージ内に"\n"か"\t"が含まれている場合は"PREFORMATTED"とする
            Label lbl;
            if (message.indexOf("\n") != -1 || message.indexOf("\t") != -1) {
                lbl = new Label(message, Label.CONTENT_PREFORMATTED);
            } else {
                lbl = new Label(message, Label.CONTENT_TEXT);
            }
            lbl.addStyleName("dialog-message");
            layout.addComponent(lbl);
        }

        // オプショナルなレイアウトがあれば表示
        if (optionLayout != null) {
            optionLayout.addStyleName("dialog-confirm-option");
            addComponent(optionLayout);
            layout.setComponentAlignment(optionLayout, Alignment.MIDDLE_CENTER);
        }

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);

        // ボタン表示
        okButton = new Button(ViewProperties.getCaption("button.ok"), this, "buttonClick");
        okButton.setDescription(ViewProperties.getCaption("description.ok"));
        okButton.setData(Result.OK);
        // [Enter]でokButtonクリック
        okButton.setClickShortcut(KeyCode.ENTER);
        okButton.focus();

        cancelButton = new Button(ViewProperties.getCaption("button.cancel"), this, "buttonClick");
        cancelButton.setDescription(ViewProperties.getCaption("description.cancel"));
        cancelButton.setData(Result.Cancel);

        switch (buttons) {
            case OK:
                hl.addComponent(okButton);
                break;
            case OKCancel:
                hl.addComponent(okButton);
                hl.addComponent(cancelButton);
                break;
            case OKCancelConfirm:
                hl.addComponent(okButton);
                hl.addComponent(cancelButton);
                okButton.setEnabled(false);

                confirm = new ComboBox();
                confirm.setWidth("200px");
                confirm.setImmediate(true);
                confirm.addStyleName("dialog-confirm");
                confirm.setInputPrompt(ViewProperties.getCaption("description.dialogConfirmComboBox"));
                confirm.addItem(ViewProperties.getCaption("field.dialogConfirmComboBox"));
                confirm.addListener(new ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        if (confirm.getValue() != null) {
                            okButton.setEnabled(true);
                        } else {
                            okButton.setEnabled(false);
                        }
                    }
                });
                layout.addComponent(confirm);
                layout.setComponentAlignment(confirm, Alignment.MIDDLE_CENTER);
                break;
            default:
                break;
        }

        layout.addComponent(hl);
        layout.setComponentAlignment(hl, Alignment.BOTTOM_CENTER);
    }

    public static enum Buttons {
        OK, OKCancel, OKCancelConfirm
    }

    public static enum Result {
        OK, Cancel,
    }

    public interface Callback {
        public void onDialogResult(Result result);
    }

    public void buttonClick(Button.ClickEvent event) {
        if (getParent() != null) {
            ((Window) getParent()).removeWindow(this);
        }
        if (callback != null) {
            callback.onDialogResult((Result) event.getButton().getData());
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

}
