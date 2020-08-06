package pl.wotu.callstats;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.icu.text.SimpleDateFormat;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

public class CallHandleService extends Service {

    private WindowManager mWindowManager;
    private View mFloatingView;
    private TextView last_call_number_textview;
    private boolean isFloatingViewVisible = false;
    private WindowManager.LayoutParams params;
    private Intent intent;
    private int startId;
    private int flags;


    private String recordPermission =  Manifest.permission.RECORD_AUDIO;
    private static final int PERMISSION_CODE = 21;
    private MediaRecorder mediaRecorder;
    private String recordFile;

//    private CallHelper callHelper;

    public CallHandleService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        callHelper = new CallHelper(this);
        this.intent = intent;
        this.flags = flags;
        this.startId = startId;

        int res = super.onStartCommand(intent, flags, startId);
//        callHelper.start();
//        System.out.println("onStartCommand");
        return res;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) {
            mWindowManager.removeView(mFloatingView);

            isFloatingViewVisible = false;
        }
//        System.out.println("onDestroy");
//        callHelper.stop();
    }


//    @Override
//    public void onTaskRemoved(Intent rootIntent) {
//        super.onTaskRemoved(rootIntent);
//        System.out.println("onTaskRemoved");
//
//    }

    @Override
    public IBinder onBind(Intent intent) {
//         TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Inflate the floating view layout we created

       mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater layoutInflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view=layoutInflater.inflate(R.layout.xxact_copy_popupmenu, null);



        mFloatingView = layoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
//        mFloatingView.setVisibility(View.INVISIBLE);



        TelephonyManager telephonyManger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if(telephonyManger != null) {
            telephonyManger.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }



        int LAYOUT_FLAG;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);



//        final WindowManager.LayoutParams params;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            params = new WindowManager.LayoutParams(
//                    WindowManager.LayoutParams.WRAP_CONTENT,
//                    WindowManager.LayoutParams.WRAP_CONTENT,
//                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//
//                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                    PixelFormat.TRANSLUCENT);
//        } else {
//            params = new WindowManager.LayoutParams(
//                    WindowManager.LayoutParams.WRAP_CONTENT,
//                    WindowManager.LayoutParams.WRAP_CONTENT,
//                    WindowManager.LayoutParams.TYPE_PHONE,
//                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                    PixelFormat.TRANSLUCENT);
//        }

        //Add the view to the window.
//         params = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_PHONE,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
//        params.gravity = Gravity.CENTER;
        params.x = 20;
        params.y = 200;

        //Add the view to the window
//        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);
        isFloatingViewVisible = true;
//        mFloatingView.setVisibility(View.INVISIBLE);
        mFloatingView.setVisibility(View.VISIBLE);

        //The root element of the collapsed view layout
        final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);

        last_call_number_textview = mFloatingView.findViewById(R.id.last_call_number_textview);

        //Drag and move floating view using user's touch action.
        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

//            Date touchDate;
            long touchStartTime = 0;



            @Override
            public boolean onTouch(View v, MotionEvent event) {

//                Toast.makeText(getApplicationContext(),"touched",Toast.LENGTH_SHORT).show();

//                System.out.println("touched");
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();

                        Date nowDate = new Date();
                        touchStartTime = nowDate.getTime();

                        return true;
                    case MotionEvent.ACTION_UP:

                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);

                        Date nowEndDate = new Date();
                        long touchTime = nowEndDate.getTime() - touchStartTime;


                        if(touchTime>1500){
                            if (Xdiff < 10 && Ydiff < 10) {
                                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    vibrator.vibrate(VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE));
                                } else {
                                    //deprecated in API 26
                                    vibrator.vibrate(450);
                                }
//                            }
                            }
                        }
                        else if(touchTime > 120){
                            if (Xdiff < 10 && Ydiff < 10) {
                                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    vibrator.vibrate(VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE));
                                } else {
                                    //deprecated in API 26
                                    vibrator.vibrate(45);
                                }
//                            }
                            }
                        }else{
                            Intent intent = new Intent(CallHandleService.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }

                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.

                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);

//                        Date someDate = new Date();
//                        long someTime = someDate.getTime() - touchStartTime;

                        return true;
                }

                return false;
            }
        });


        //Open the application on thi button click
//        ImageView openButton = (ImageView) mFloatingView.findViewById(R.id.collapsed_iv);
//        openButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(),"klik",Toast.LENGTH_SHORT).show();
//                //Open the application  click.
//                Intent intent = new Intent(CallHandleService.this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//
//                //close the service and remove view from the view hierarchy
//                stopSelf();
//            }
//        });
    }

    /**
     * Detect if the floating view is collapsed or expanded.
     *
     * @return true if the floating view is collapsed.
     */
//    private boolean isViewCollapsed() {
//        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
//    }


    final PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCellLocationChanged(CellLocation location) {
            super.onCellLocationChanged(location);
//            CellLocation l = location;
//            System.out.println(l);

        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

//            last_call_number_textview.setText("state:"+state);

            if (state == TelephonyManager.CALL_STATE_RINGING) { //przychodzące
                if(!CallAppConfig.isNullOrEmpty(incomingNumber)){
                    last_call_number_textview.setVisibility(View.VISIBLE);
                    last_call_number_textview.setText("Łączenie\n"+incomingNumber);
                    mFloatingView.setVisibility(View.VISIBLE);
                }
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                if(!CallAppConfig.isNullOrEmpty(incomingNumber)){
                    last_call_number_textview.setVisibility(View.GONE);
                    System.out.println("Service CALL_STATE_IDLE: " +incomingNumber);
//                    mFloatingView.setVisibility(View.INVISIBLE);
                    mFloatingView.setVisibility(View.VISIBLE);
                }
            }
            else if(state ==TelephonyManager.CALL_STATE_OFFHOOK){
                if(!CallAppConfig.isNullOrEmpty(incomingNumber)){
                    mFloatingView.setVisibility(View.VISIBLE);
                    last_call_number_textview.setText("Sprawdzanie numeru\n"+incomingNumber);
                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    String userID = mAuth.getCurrentUser().getUid();
                    firebaseFirestore.collection("Users")

                            .document("testUser")
                            .collection("Contacts")
                            .document(incomingNumber)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                last_call_number_textview.setText("Rozmowa z\n"+task.getResult().get("cachedName"));
                            }else {
                                last_call_number_textview.setText("Rozmowa z\n"+incomingNumber);
                            }

                            //            if (task.isSuccessful()) {
//                if (task.getResult().exists()) {
//
//                    //                        Toast.makeText(SetupActivity.this,"data exist",Toast.LENGTH_LONG).show();
////                    name = task.getResult().getString("cachedName");
//                } else {
////                            Toast.makeText(getApplicationContext(),"data nie exist",Toast.LENGTH_LONG).show();
//                }
                        }
                    });

//                    String userName = checkNumberOnline();

                    last_call_number_textview.setVisibility(View.VISIBLE);
                    last_call_number_textview.setText("Rozmowa z\n"+incomingNumber);
                    System.out.println("Service CALL_STATE_OFFHOOK: " +incomingNumber);


                    mFloatingView.setVisibility(View.VISIBLE);
                }
//                startRecording(incomingNumber);
            }
            else {
                last_call_number_textview.setText("state"+state);
//                mFloatingView.setVisibility(View.INVISIBLE);
                mFloatingView.setVisibility(View.VISIBLE);
            }

        }

    };



//        firebaseFirestore.collection("Users")
//                .document("testUser")
//                .collection("Contacts")
//                .document(phoneNumber)
//                .get().addOnCompleteListener((OnCompleteListener<DocumentSnapshot>) task -> {
//            if (task.isSuccessful()) {
//                if (task.getResult().exists()) {
//
//                    //                        Toast.makeText(SetupActivity.this,"data exist",Toast.LENGTH_LONG).show();
////                    name = task.getResult().getString("cachedName");
//                } else {
////                            Toast.makeText(getApplicationContext(),"data nie exist",Toast.LENGTH_LONG).show();
//                }
//            }


        private void startRecording(String number) {

    //        timer.setBase(SystemClock.elapsedRealtime());
    //        timer.start();
            String recordPath = getExternalFilesDir("/").getAbsolutePath();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss", Locale.CANADA);

            Date now = new Date();
            recordFile = "CallRecording_"+formatter.format(now)+"_tel"+number+".3gp";
            System.out.println("Nagrywanie "+recordFile);
    //        filenameText.setText("Nagrywanie pliku:\n"+recordFile);

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(recordPath+"/"+recordFile);
            Log.d("AUDIO_LOG",recordPath+"/"+recordFile);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                mediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
    //        recorder.prepare();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    //        recorder.start();
            mediaRecorder.start();

        }

        private void stopRecording() {
    //        filenameText.setText("Utworzono plik:\n"+recordFile);
    //        timer.stop();
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;

        }


    //    private boolean checkPermissions() {
    //        if(ActivityCompat.checkSelfPermission(getContext(), recordPermission)== PackageManager.PERMISSION_GRANTED){
    //            return true;
    //        }else{
    //            ActivityCompat.requestPermissions(this, new String[]{recordPermission},PERMISSION_CODE);
    //            return false;
    //        }
    //    }

    }
