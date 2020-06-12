package com.example.verifycounttool.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    public static void showToast(Context context, String data){
        Toast.makeText(context,data, Toast.LENGTH_SHORT).show();
    }

    /* 将byte数组转换为字符串,用16进制表示 */
    public static String bytesToHexString(byte[] src, int len) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || len <= 0) {
            System.out.println("src == null");
            return null;
        }
        for (int i = 0; i < len; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv + ",");
        }
        return stringBuilder.toString();
    }
}
