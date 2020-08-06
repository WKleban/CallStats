package pl.wotu.callstats;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Test2Fragment extends Fragment {

    private TextView test2textview;

    public Test2Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//       initPhoneListener();

        test2textview = view.findViewById(R.id.test2textview);

//        TelephonyManager telephonyManger = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
////                telepho
////                telephonyManger.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
//        if(telephonyManger != null) {
//            telephonyManger.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
//        }
    }

//    private void setupSignalStrength() {
//        final TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        final ImageView signalIcon = ((ImageView) statusBar.findViewById(R.id.signal_icon));
//        phoneListener = new PhoneStateListener() {
//            @Override
//            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
//                if (manager.getNetworkOperator().equals("")) {
//                    signalIcon.setVisibility(View.GONE);
//                } else {
//                    signalIcon.setVisibility(View.VISIBLE);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        // See https://github.com/AlstonLin/TheLearningLock/issues/54
//                        Integer imageRes = signalStrengthToIcon.get(signalStrength.getLevel());
//                        if (imageRes != null) signalIcon.setImageResource(imageRes);
//                        else signalIcon.setImageResource(signalStrengthToIcon.get(4));
//                    } else {
//                        // Just show the full icon
//                        signalIcon.setImageResource(signalStrengthToIcon.get(4));
//                    }
//                }
//            }
//        };
//        manager.listen(phoneListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
//    }


//    private void initPhoneListener(){

        final PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
//                    pauseMedia();
                    System.out.println("Wotu"+" onCallStateChanged,state == TelephonyManager.CALL_STATE_RINGING,incomingNumber"+incomingNumber);
                    test2textview.setText("onCallStateChanged,state == TelephonyManager.CALL_STATE_RINGING");
                } else if (state == TelephonyManager.CALL_STATE_IDLE) {

//                    isInCall = false;
//
//                    if (isFirstStart == false) {
//                        if (Build.VERSION.SDK_INT >= 17.0) {
//                            bigNotification = true;
//                            largeMediaPlayer = LargeMediaPlayer.getInstance(context);
//                        } else {
//                            bigNotification = false;
//                            smallMediaPlayer = SmallMediaPlayer.getInstance(context);
//                        }
//                        resumeMedia();
//                    }
//
//                    isFirstStart = false;
                    test2textview.setText("onCallStateChanged,state == TelephonyManager.CALL_STATE_IDLE");
                    System.out.println("Wotu"+" onCallStateChanged,state == TelephonyManager.CALL_STATE_IDLE");
                }
                else {
                    test2textview.setText("onCallStateChanged,state == "+state);
                    System.out.println("Wotu"+" onCallStateChanged,state == "+state);
                }
                super.onCallStateChanged(state, incomingNumber);


            }


        };

}
