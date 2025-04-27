package com.enicarthage.coulisses.util;

import android.text.Editable;
import android.text.TextWatcher;

public class CreditCardNumberFormattingTextWatcher implements TextWatcher {
    private boolean isFormatting;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        if (isFormatting) return;

        isFormatting = true;

        // Remove all non-digit characters
        String digits = s.toString().replaceAll("[^\\d]", "");

        // Format as XXXX XXXX XXXX XXXX
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < digits.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append(" ");
            }
            formatted.append(digits.charAt(i));
        }

        // Update the text
        s.replace(0, s.length(), formatted.toString());

        isFormatting = false;
    }
}