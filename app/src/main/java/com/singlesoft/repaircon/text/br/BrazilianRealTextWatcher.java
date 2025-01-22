package com.singlesoft.repaircon.text.br;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class BrazilianRealTextWatcher implements TextWatcher {

    private final EditText editText;
    private BigDecimal value = BigDecimal.ZERO;

    public BrazilianRealTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Remove any non-numeric characters from the input
        String cleanInput = s.toString().replaceAll("[^\\d]", "");

        // Parse the input value as a BigDecimal
        BigDecimal inputValue = new BigDecimal(cleanInput).divide(BigDecimal.valueOf(100));

        // Format the value as Brazilian Real and set it on the EditText
        String formattedValue = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"))
                .format(inputValue);
        editText.setText(formattedValue);
        editText.setSelection(formattedValue.length());

        // Store the parsed value for retrieval later
        value = inputValue;
    }

    @Override
    public void afterTextChanged(Editable s) {}

    public BigDecimal getValue() {
        return value;
    }
}
