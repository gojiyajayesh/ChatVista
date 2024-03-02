package com.gojiyajayesh.chatvista.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gojiyajayesh.chatvista.R;
import com.gojiyajayesh.chatvista.models.Users;

public class AndroidUtils {
    private static Toast toast;

    public static void customToast(Context context, String message, int timeDuration) {
        if (toast != null) {
            toast.cancel();
        }
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_toast_layout, null);
        TextView textView = layout.findViewById(R.id.custom_toast_message);
        textView.setText(message);
        toast = new Toast(context);
        toast.setDuration(timeDuration);
        toast.setView(layout);
        toast.show();
    }

    public static void setPassedIntentData(Intent intent, Users user) {
        intent.putExtra("UserId", user.getUserId());
        intent.putExtra("Username", user.getUsername());
        intent.putExtra("LastMessage", user.getLastMessage());
        intent.putExtra("ProfileId", user.getProfileId());
        intent.putExtra("Password", user.getPassword());
        intent.putExtra("PhoneOrEmail", user.getPhoneOrEmail());
        intent.putExtra("FullName", user.getFullName());
        intent.putExtra("LastMessageTime", user.getLastMessageTime());
    }

    public static Users getPassedIntentData(Intent intent) {
        Users users = new Users();
        users.setUserId(intent.getStringExtra("UserId"));
        users.setUsername(intent.getStringExtra("Username"));
        users.setLastMessage(intent.getStringExtra("LastMessage"));
        users.setProfileId(intent.getStringExtra("ProfileId"));
        users.setPassword(intent.getStringExtra("Password"));
        users.setPhoneOrEmail(intent.getStringExtra("PhoneOrEmail"));
        users.setFullName(intent.getStringExtra("FullName"));
        users.setLastMessageTime(intent.getLongExtra("LastMessageTime", 0L));
        return users;
    }
}
