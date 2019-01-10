package com.justanotherpanel.app.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

import com.justanotherpanel.app.R;
import com.xw.repo.XEditText;

/**
 * Created by Emerald on 3/18/2018.
 */

public class EditBox extends XEditText implements TextWatcher {

    public EditBox(Context context) {
        super(context);


        addTextChangedListener(this);
    }

    public EditBox(Context context, AttributeSet attrs) {
        super(context, attrs);

        addTextChangedListener(this);
    }

    public EditBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        addTextChangedListener(this);
    }

    public void setErrorResult() {
        setHintTextColor(ContextCompat.getColor(getContext(), R.color.colorYoutube));
        setTextColor(ContextCompat.getColor(getContext(), R.color.colorYoutube));
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        setHintTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
        setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
