package com.singlesoft.repaircon.text.br;

import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class BrazilianRealInputFilter implements InputFilter {

    private final DecimalFormat df;

    public BrazilianRealInputFilter() {
        df = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(Locale.getDefault()));
        df.setDecimalSeparatorAlwaysShown(true);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {

        StringBuilder builder = new StringBuilder(dest);
        builder.replace(dstart, dend, source.subSequence(start, end).toString());
        String result = builder.toString();

        if (result.isEmpty()) {
            return "";
        }
        if (result.equals(".")) {
            return "0,";
        }
        if (result.startsWith(".")) {
            result = "0" + result;
        }

        int index = result.indexOf(",");
        if (index != -1) {
            String decimal = result.substring(index + 1);
            if (decimal.length() > 2) {
                return "";
            }
        }

        return new SpannableString(df.format(Double.parseDouble(result))).toString()
                .substring(dest.toString().length());
    }
}