package pl.wotu.callstats;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallAppConfig {

    public static int getNumberOfSimCards(Activity activity, Context context) {

        final SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
        int simCount = 0;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_CALL_LOG)) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CALL_LOG}, 1);
                Toast.makeText(context, "TRUE\nNie masz uprawnień do wykonywania połączeń!", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CALL_LOG}, 1);
                Toast.makeText(context, "FALSE\nNie masz uprawnień do wykonywania połączeń!", Toast.LENGTH_LONG).show();
            }
        } else {
            final List<SubscriptionInfo> activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
            simCount = activeSubscriptionInfoList.size();
        }
        return simCount;
    }

    public static Map<String,String> getSimCardsInformation(Activity activity, Context context, int cardSlot) {
        final SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
        int simCount;
        Map<String,String> result = null;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_CALL_LOG)) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CALL_LOG}, 1);
                Toast.makeText(context, "TRUE\nNie masz uprawnień do wykonywania połączeń!", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CALL_LOG}, 1);
                Toast.makeText(context, "FALSE\nNie masz uprawnień do wykonywania połączeń!", Toast.LENGTH_LONG).show();
            }
        } else {
            final List<SubscriptionInfo> activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();

            result = new HashMap<>();

            simCount = activeSubscriptionInfoList.size();
            if ((simCount==1&&cardSlot==0)||(simCount==2)){
                int simSlotIndex = activeSubscriptionInfoList.get(cardSlot).getSimSlotIndex();
                activeSubscriptionInfoList.get(cardSlot);
                result.put("getIccId",activeSubscriptionInfoList.get(cardSlot).getIccId());
                result.put("getCarrierName", String.valueOf(activeSubscriptionInfoList.get(cardSlot).getCarrierName()));
                result.put("getCountryIso",activeSubscriptionInfoList.get(cardSlot).getCountryIso());
//                result.put("getDisplayName",String.valueOf(activeSubscriptionInfoList.get(cardSlot).getDisplayName()));
//                result.put("getNumber",activeSubscriptionInfoList.get(cardSlot).getNumber());
//                result.put("getDataRoaming", String.valueOf(activeSubscriptionInfoList.get(cardSlot).getDataRoaming()));

//                System.out.println("Liczba dostępnych kart SIM: "+simCount);
//                System.out.println("----------------------------------------");
//                System.out.println("getSimSlotIndex "+activeSubscriptionInfoList.get(cardSlot).getSimSlotIndex());
//                System.out.println("getIccId "+activeSubscriptionInfoList.get(cardSlot).getIccId());
//                System.out.println("getCarrierName "+activeSubscriptionInfoList.get(cardSlot).getCarrierName());
//                System.out.println("getCountryIso "+activeSubscriptionInfoList.get(cardSlot).getCountryIso());
//                System.out.println("getDisplayName "+activeSubscriptionInfoList.get(cardSlot).getDisplayName());
//                System.out.println("getNumber "+activeSubscriptionInfoList.get(cardSlot).getNumber());
//                System.out.println("getDataRoaming "+activeSubscriptionInfoList.get(cardSlot).getDataRoaming());
            }

        }

        return result;
    }

    static boolean isNullOrEmpty(String string) {
        if ((string==null)||(string.equals(""))){
            return true;
        }else{
            return false;
        }
    }
}
