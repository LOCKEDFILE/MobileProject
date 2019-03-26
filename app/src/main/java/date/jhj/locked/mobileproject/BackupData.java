package date.jhj.locked.mobileproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BackupData  {
    static final String PREF_HISTORY = "history";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }
    // 리스트 저장
    public static void setPrefHistory(Context ctx, String userData) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_HISTORY, userData);
        editor.commit();
    }
    // 저장된 정보 가져오기
    public static String getPrefHistory(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_HISTORY, "");
    }
    ///////////////
}
