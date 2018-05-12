
package io.github.brijoe;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;


class DHTool {

    private static final String TAG = "DH";

    public static String getAppPackageName(Context context) {
        if (context == null) {
            return null;
        }
        return context.getPackageName();
    }


    public static String getAppVersionName(Context context) {
        if (context == null) {
            return null;
        }
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }


    public static int getAppVersionCode(Context context) {
        if (context == null) {
            return -1;
        }
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }



    public static int getScreenWidth(Context context) {
        if (null == context) {
            return 0;
        }
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        final int screenWidth = displayMetrics.widthPixels;
        return screenWidth;
    }


    public static int getScreenHeight(Context context) {
        if (null == context) {
            return 0;
        }
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        final int screenHeight = displayMetrics.heightPixels;
        return screenHeight;
    }

    public static String getDensity(Context context) {
        if (context == null) {
            return "";
        }

        String [] type={"ldpi","mdpi","hdpi","xhdpi","xxhdpi","xxxhdpi"};

        String densityDes;
        float dpi=context.getResources().getDisplayMetrics().densityDpi;

        if(dpi<=120){
           densityDes=type[0];
        }
        else if(dpi>120&&dpi<=160)
            densityDes=type[1];
        else if(dpi>160&&dpi<=240)
            densityDes=type[2];
        else if(dpi>240&&dpi<=320)
            densityDes=type[3];
        else if(dpi>320&&dpi<=480)
            densityDes=type[4];
        else
            densityDes=type[5];


        return String.valueOf(context.getResources().getDisplayMetrics().densityDpi)+"dp / "+densityDes;
    }


    public static int dip2px(Context context, float dipValue) {
        if (null == context) {
            return 0;
        }
        final float scaleValue = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scaleValue + 0.5f);
    }


    public static int px2dip(Context context, float pxValue) {
        if (null == context) {
            return 0;
        }
        final float scaleValue = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scaleValue + 0.5f);
    }


    public static String getPhoneBrand() {
        return android.os.Build.BRAND;
    }


    public static String getPhoneModel() {
        return android.os.Build.MODEL;
    }


    public static int getBuildLevel() {
        return android.os.Build.VERSION.SDK_INT;
    }


    public static String getBuildVersion() {
        return android.os.Build.VERSION.RELEASE;
    }


    public static int getAppProcessId() {
        return android.os.Process.myPid();
    }


    public static String getAppProcessName(Context context, int processId) {
        String processName = null;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = context.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == processId) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));

                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return processName;
    }


    public static String getDeviceId(Context context) {
        if (null == context) {
            return null;
        }
        final String imeiStr = getIMEIString(context);
        if (!TextUtils.isEmpty(imeiStr)) {
            return imeiStr;
        } else {
            final String imsiStr = getImsiString(context);
            if (!TextUtils.isEmpty(imsiStr)) {
                return imsiStr;
            } else {
                return getMacString(context);
            }
        }
    }


    public static boolean isNetConnected(Context context) {
        boolean isConnected = false;
        try {
            final ConnectivityManager connManager = getConnectService(context);
            NetworkInfo info = connManager.getActiveNetworkInfo();
            if (info != null && info.isConnected()
                    && info.getState() == NetworkInfo.State.CONNECTED) {
                isConnected = true;
            }
        } catch (Exception e) {
            isConnected = false;
        }
        return isConnected;
    }


    public static String getIMEIString(Context context) {
        if (null == context) {
            return "";
        }
        try {
            final String imei = getTelpephonyService(context).getDeviceId();
            return imei;
        } catch (Exception e) {
            return "";
        }
    }

    public static String getImsiString(Context context) {
        if (null == context) {
            return "";
        }
        try {
            final String imsi = getTelpephonyService(context).getSubscriberId();
            Log.e("imsiNormal:", getTelpephonyService(context).getSubscriberId() + "");
            return imsi;
        } catch (Exception e) {
            Log.e("imsiError:", e.getMessage() + "");
            return "";
        }
    }


    public static String getMacString(Context context) {
        WifiInfo info = null;
        try {
            info = getWifiService(context).getConnectionInfo();
        } catch (Exception e) {
            info = null;
        }
        String mac = "";
        if (info != null) {
            mac = info.getMacAddress();
            if (!TextUtils.isEmpty(mac)) {
                mac = mac.replaceAll(":", "");
            }
        }
        return mac;
    }


    private static ActivityManager getActivityService(Context context) {
        return ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
    }


    public static ConnectivityManager getConnectService(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }


    public static WifiManager getWifiService(Context context) {
        return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }


    public static TelephonyManager getTelpephonyService(Context context) {
        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }


    public static float sp2px(Context context, float spValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }


    public static void gotoDevModeSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
        handleIntent(context, intent);
    }


    public static void gotoAppSetting(Context context) {
        Uri packageURI = Uri.parse("package:" + getAppPackageName(context));
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
        handleIntent(context, intent);
    }

    public static void gotoLanguageSetting(Context context) {

        Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
        handleIntent(context, intent);
    }


    private static void handleIntent(Context context, Intent intent) {
        if (intent == null)
            return;
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.GET_ACTIVITIES);
        if (list.size() > 0)
            context.startActivity(intent);
        else
            Toast.makeText(context, context.getString(R.string.handle_intent_error), Toast.LENGTH_SHORT).show();

    }


    public static String getSignSHA1(Context context) {

        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    getAppPackageName(context),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.DEFAULT);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    public static String getSignSHA256(Context context) {

        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    getAppPackageName(context),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.DEFAULT);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    public static String getSignMD5(Context context) {

        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    getAppPackageName(context),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                return getMD5Str(signature.toByteArray());
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }

        return "";
    }

    private  static String getMD5Str(byte[] buffer) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }



    public static String getMemory(){
        Runtime runtime=Runtime.getRuntime();
       return  "maxMemory:"+runtime.maxMemory()/1024/1024+
                "MB\nfreeMemory:"+runtime.freeMemory()/1024/1024+
                "MB\ntotalMemory:"+runtime.totalMemory()/1024/1024 +"MB";
    }

    public static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr)) return "";
        StringBuilder sb = new StringBuilder();
        char last = '\0';
        char current = '\0';
        int indent = 0;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);
            switch (current) {
                case '{':
                case '[':
                    sb.append(current);
                    sb.append('\n');
                    indent++;
                    addIndentBlank(sb, indent);
                    break;
                case '}':
                case ']':
                    sb.append('\n');
                    indent--;
                    addIndentBlank(sb, indent);
                    sb.append(current);
                    break;
                case ',':
                    sb.append(current);
                    if (last != '\\') {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;
                default:
                    sb.append(current);
            }
        }

        return sb.toString();
    }

    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append('\t');
        }
    }


}
