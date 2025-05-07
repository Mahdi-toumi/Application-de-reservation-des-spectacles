package com.enicarthage.coulisses.util;

import android.text.Editable;
import android.text.TextWatcher;

public class CreditCardNumberFormattingTextWatcher implements TextWatcher {
    private boolean isFormatting;  // Prevent recursive formatting

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        if (isFormatting) return;

        isFormatting = true;

        // Remove all non-digit characters (to ensure we only have digits)
        String digits = s.toString().replaceAll("[^\\d]", "");

        // Format the digits into groups of 4, adding a space between them
        StringBuilder formatted = new StringBuilder();

        // Loop through the digits and add a space after every 4 digits
        for (int i = 0; i < digits.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append(" ");  // Add a space every 4 digits
            }
            formatted.append(digits.charAt(i));
        }

        // Replace the original text with the formatted text
        s.replace(0, s.length(), formatted.toString());

        // Ensure the cursor remains at the correct position
        // (This should normally work well since we are directly modifying the Editable object)
        isFormatting = false;
    }
}
