package pl.wotu.callstats;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class CallHelper {

    /**
     * Listener to detect incoming calls.
     */
    private class CallStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            switch (state) {
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Toast.makeText(ctx,
                            "CALL_STATE_OFFHOOK: " +incomingNumber,
                            Toast.LENGTH_LONG).show();
                    System.out.println("Call Helper CALL_STATE_OFFHOOK: " +incomingNumber);

                case TelephonyManager.CALL_STATE_RINGING:
                    // called when someone is ringing to this phone

                    Toast.makeText(ctx,
                            "CALL_STATE_RINGING: " +incomingNumber,
                            Toast.LENGTH_LONG).show();
                    System.out.println("Call Helper CALL_STATE_RINGING: " +incomingNumber);

                    break;

            }
        }
    }

    /**
     * Broadcast receiver to detect the outgoing calls.
     */
    public class OutgoingReceiver extends BroadcastReceiver {
        public OutgoingReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            System.out.println("Call Helper Outgoing: " +number);
            Log.e( "onCallStateChanged: ",number );
            Toast.makeText(ctx,"Outgoing: "+number,
                    Toast.LENGTH_LONG).show();
        }

    }

    private Context ctx;
    private TelephonyManager tm;
    private CallStateListener callStateListener;

    private OutgoingReceiver outgoingReceiver;

    public CallHelper(Context ctx) {
        this.ctx = ctx;

        callStateListener = new CallStateListener();
        outgoingReceiver = new OutgoingReceiver();
    }

    /**
     * Start calls detection.
     */
    public void start() {
        tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        ctx.registerReceiver(outgoingReceiver, intentFilter);

    }

    /**
     * Stop calls detection.
     */
    public void stop() {
        tm.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
        ctx.unregisterReceiver(outgoingReceiver);
    }

}