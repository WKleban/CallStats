package pl.wotu.callstats;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.util.List;


public class FirebaseCallLogFragment extends Fragment {

    public FirebaseCallLogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_firebase_call_log, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//
//        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(getContext());
//
//        String imeiSIM1 = telephonyInfo.getImsiSIM1();
//        String imeiSIM2 = telephonyInfo.getImsiSIM2();
//
//        boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
//        boolean isSIM2Ready = telephonyInfo.isSIM2Ready();
//
//        boolean isDualSIM = telephonyInfo.isDualSIM();
//
        TextView tv = view.findViewById(R.id.textviewfirebase);
//        tv.setText(" IME1 : " + imeiSIM1 + "\n" +
//                " IME2 : " + imeiSIM2 + "\n" +
//                " IS DUAL SIM : " + isDualSIM + "\n" +
//                " IS SIM1 READY : " + isSIM1Ready + "\n" +
//                " IS SIM2 READY : " + isSIM2Ready + "\n");

        final SubscriptionManager subscriptionManager = SubscriptionManager.from(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            Dexter.withActivity(getActivity())
//
//                    .withPermission(Manifest.permission.READ_PHONE_STATE)
//                    .withListener(new PermissionListener() {
//                        @Override public void onPermissionGranted(PermissionGrantedResponse response) {
//                            PermissionListener dialogPermissionListener =
//                                    DialogOnDeniedPermissionListener.Builder
//                                            .withContext(getContext())
//                                            .withTitle("READ_PHONE_STATE permission")
//                                            .withMessage("READ_PHONE_STATE permission is needed to do this shit")
//                                            .withButtonText(android.R.string.ok)
//                                            .withIcon(R.mipmap.ic_launcher)
//                                            .build();
//                        }
//                        @Override public void onPermissionDenied(PermissionDeniedResponse response) { PermissionListener dialogPermissionListener =
//                                DialogOnDeniedPermissionListener.Builder
//                                        .withContext(getContext())
//                                        .withTitle("READ_PHONE_STATE permission")
//                                        .withMessage("READ_PHONE_STATE permission is needed to do this shit")
//                                        .withButtonText(android.R.string.ok)
//                                        .withIcon(R.mipmap.ic_launcher)
//                                        .build();}
//                        @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
//                    })
//                    .check();
        }


        final List<SubscriptionInfo> activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        int simCount = activeSubscriptionInfoList.size();
        StringBuffer simText = new StringBuffer();
        simText.append("Liczba dostępnych kart SIM: "+simCount);
//        Log.d("MainActivity: ","simCount:" +simCount);


        for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
//            Log.d("MainActivity: ","iccId :"+ subscriptionInfo.getIccId()+" , name : "+ subscriptionInfo.getDisplayName());
            simText.append("\nSimSlotIndex :"+ subscriptionInfo.getSimSlotIndex());
            simText.append("\niccId :"+ subscriptionInfo.getIccId());
            simText.append("\nCarrierName :"+ subscriptionInfo.getCarrierName());
            simText.append("\nCountryIso :"+ subscriptionInfo.getCountryIso());
            simText.append("\nDisplayName : "+ subscriptionInfo.getDisplayName());
            simText.append("\nNumber : "+subscriptionInfo.getNumber());
            simText.append("\n----------------\n");
        }

//        Dexter.withActivity(getActivity())
//                .withPermissions(
//                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                        Manifest.permission.ACCESS_FINE_LOCATION)
//                .withListener(listener)
//                .withErrorListener(new PermissionRequestErrorListener() {
//                    @Override
//                    public void onError(DexterError error) {
//                        Toast.makeText(getContext(), "Error occurred! " + error.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .check();

        tv.setText(simText);
    }

}
