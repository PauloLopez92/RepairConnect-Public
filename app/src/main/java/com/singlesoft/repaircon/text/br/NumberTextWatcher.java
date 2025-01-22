package com.singlesoft.repaircon.text.br;

import android.text.Editable;
import android.text.TextWatcher;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberTextWatcher implements TextWatcher {

    private final DecimalFormat df;

    public NumberTextWatcher() {
        df = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.getDefault()));
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().isEmpty()) {
            return;
        }

        try {
            double value = Double.parseDouble(s.toString());
            s.replace(0, s.length(), df.format(value));
        } catch (NumberFormatException e) {
            s.clear();
        }
    }
}
