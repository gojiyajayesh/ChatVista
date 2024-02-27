package com.gojiyajayesh.chatvista.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gojiyajayesh.chatvista.R;

public class AndroidUtils {
    private static Toast toast;
    public static void customToast(Context context, String message, int timeDuration) {
        if (toast != null) {
            toast.cancel();
        }
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = li.inflate(R.layout.custom_toast_layout, null);
        TextView textView = layout.findViewById(R.id.custom_toast_message);
        textView.setText(message);
        toast = new Toast(context);
        toast.setDuration(timeDuration);
        toast.setView(layout);
        toast.show();
    }
}
