package com.viewnext.tools;
 
import javax.swing.text.*;
 
public class NumericFilter extends DocumentFilter {
    private final int maxLength;
    private final Integer minValue;
    private final Integer maxValue;
 
    public NumericFilter(int maxLength) {
        this(maxLength, null, null);
    }
 
    public NumericFilter(int maxLength, Integer minValue, Integer maxValue) {
        this.maxLength = maxLength;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
 
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
        if (string != null && string.matches("\\d+")) {
            StringBuilder builder = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
            builder.insert(offset, string);
            if (isValid(builder.toString())) {
                super.insertString(fb, offset, string, attr);
            }
        }
    }
 
    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        if (text != null && text.matches("\\d*")) {
            StringBuilder builder = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
            builder.replace(offset, offset + length, text);
            if (isValid(builder.toString())) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }
 
    private boolean isValid(String text) {
        if (text.length() > maxLength) return false;
        if (text.isEmpty()) return true;
 
        try {
            int value = Integer.parseInt(text);
            if (minValue != null && value < minValue) return false;
            if (maxValue != null && value > maxValue) return false;
        } catch (NumberFormatException e) {
            return false;
        }
 
        return true;
    }
}