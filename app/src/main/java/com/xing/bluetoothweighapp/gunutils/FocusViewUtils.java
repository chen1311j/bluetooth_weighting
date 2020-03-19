package com.xing.bluetoothweighapp.gunutils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class FocusViewUtils {

    public static void requestViewUnFocus(ViewGroup viewGroup){
        int count = viewGroup.getChildCount();
        for (int i=0;i<count;i++){
            View child = viewGroup.getChildAt(i);
            if(child instanceof EditText){
                child.setFocusable(false);
                child.setFocusableInTouchMode(false);
            }else if(child instanceof ViewGroup){
                requestViewUnFocus((ViewGroup) child);
            }else {

            }
        }
    }

    public static void requestViewFocus(EditText editText){
        editText.setFocusableInTouchMode(true);
        editText.setFocusable(true);
        editText.requestFocus();
    }

    public static void requestViewsFocus(EditText... editTexts){
        for (EditText et:editTexts){
            requestViewFocus(et);
        }
    }
}
