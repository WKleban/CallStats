package pl.wotu.callstats;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.CallLog;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallHistoryFragment extends Fragment {
    private FragmentActivity activity;
    private Context context;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private NavController navController;
//    private List<UserCallsSummaryModel> rvCallList;
    private boolean isReadyListOfCalls = false;
    private List<String> callers;
    private Map<String,UserCallsSummaryModel> userCallsSummaryMap;
    private List<CallLogModel> callLogList;

    public CallHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        activity = getActivity();
        context = activity.getApplicationContext();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        return inflater.inflate(R.layout.fragment_call_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

//        rvCallList = new ArrayList<>();
        userCallsSummaryMap = new HashMap<>();
        callLogList = new ArrayList<>();

        TelephonyManager telephonyManger = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if(telephonyManger != null) {
            telephonyManger.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

//        adapter = new CallHistoryAdapter(getActivity(),userCallsSummaryMap)

//        adapter = new LocalCallListAdapter(getActivity(),listOfLastCallsPerUser,callLogModelUserCallsModelHashMap);

    }





    final PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                System.out.println("TelephonyManager.CALL_STATE_RINGING,incomingNumber"+incomingNumber);

            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                setCallLogToList(getCursor());
                isReadyListOfCalls = true;

            }

        }

    };


    private Cursor getCursor() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
            String[] details = new String[]{CallLog.Calls.NUMBER,                   //0
                    CallLog.Calls.TYPE,                                             //1
                    CallLog.Calls.DURATION,                                         //2
                    CallLog.Calls.CACHED_NAME,                                      //3
                    CallLog.Calls._ID,                                              //4
                    CallLog.Calls.DATE,                                             //5
                    CallLog.Calls.CACHED_FORMATTED_NUMBER, //570 082 100            //6
                    CallLog.Calls.COUNTRY_ISO, //PL                                 //7
                    CallLog.Calls.LAST_MODIFIED, //1582571365480                    //8
                    CallLog.Calls.NEW, //0                                           //9
                    CallLog.Calls.PHONE_ACCOUNT_ID
            };

            Cursor cursor;
            cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, details, null, null, CallLog.Calls._ID + " DESC");
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
            }
            return cursor;
        }else {
            return null;
        }
    }

    private void setCallLogToList(Cursor cursor) {

        String phoneModel = Build.MANUFACTURER+" "+ Build.MODEL;
        String androidId = Settings.Global.getString(context.getContentResolver(), Settings.Global.DEVICE_NAME);
        System.out.println("phoneModel = "+phoneModel);
        System.out.println("androidId = "+androidId);


        for (int i = 0; i < cursor.getCount(); i++) {
            String phoneNumber = "" + cursor.getString(0);
            int type = Integer.parseInt(cursor.getString(1));
            int duration = Integer.parseInt(cursor.getString(2));
            String cachedName = "" + cursor.getString(3);
            int callID = Integer.parseInt(cursor.getString(4));
            Date callDate = new Date(Long.parseLong(cursor.getString(5)));
            String cachedFormattedNumber = "" + cursor.getString(6);
            String countryISO = "" + cursor.getString(7);
            Date lastModified = new Date(Long.parseLong(cursor.getString(8)));
            boolean isNew = Boolean.parseBoolean(cursor.getString(9));
            String phoneAccountId = "" + cursor.getString(10);

            //Wszystkie połączenia
            CallLogModel callLogEntry = new CallLogModel(phoneNumber, type, duration, cachedName, callID, callDate, cachedFormattedNumber, countryISO, lastModified, isNew, phoneAccountId, androidId, phoneModel);

            //Połączenia z podziałem na użytkowników
            UserCallsSummaryModel userCallsSummaryModel;
//            UserCallsSummaryModel userCallsSummaryModel = new UserCallsSummaryModel(phoneNumber, cachedFormattedNumber, cachedName, lastCallType, lastCallDuration, lastCallID, lastCallDate, countryISO,phoneModel);


            if (callers.contains(phoneNumber)) {
                userCallsSummaryModel = userCallsSummaryMap.get(phoneNumber);
                int lastCallType = userCallsSummaryModel.getLastCalltype(); //OK
                int durationOfCalls = userCallsSummaryModel.getDurationOfCalls() + 1; //+1
                int lastCallID = userCallsSummaryModel.getLastCallID();
                Date lastCallDate = userCallsSummaryModel.getLastCallDate();

                if (callDate.getTime() > lastCallDate.getTime()) {
                    lastCallDate = callDate;
                    lastCallID = callID;
                }
                userCallsSummaryModel.updateSummaryInfo(lastCallType, durationOfCalls, lastCallID, lastCallDate);
            } else {
                callers.add(phoneNumber);
//                userCallsSummaryModel = new UserCallsSummaryModel(phoneNumber, cachedFormattedNumber, cachedName, type, duration, callID, callDate, countryISO,phoneAccountId, androidId, phoneModel);
//                userCallsSummaryModel = new UserCallsSummaryModel(phoneNumber, cachedFormattedNumber, cachedName, type, duration, callID, callDate, countryISO,phoneAccountId, androidId, phoneModel);
                userCallsSummaryModel = new UserCallsSummaryModel(phoneNumber, cachedFormattedNumber, cachedName, 0, 0, 0, 0, null, countryISO, phoneAccountId, androidId, phoneModel);


                callLogList.add(callLogEntry);
                userCallsSummaryMap.put(phoneNumber, userCallsSummaryModel);

                cursor.moveToNext();

            }
//        Log.d("listOfLastCallsPerUser size",""+listOfLastCallsPerUser.size());
//        Log.d("listOfAllCalls size",""+listOfAllCalls.size());

//        TODO FIREBASE
            CollectionReference collectionReference = db.collection("Users").document("testUser").collection("Contacts");
            collectionReference.orderBy("phoneNumber", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                String doc_id="";
                    String tempPhoneNumber = "";
                    String tempCachedName = "";

                    try {
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
//                        doc_id = doc.getDocument().getId();
                            if (doc.getDocument().contains("phoneNumber")) {
                                tempPhoneNumber = doc.getDocument().get("phoneNumber").toString();
                            }
                            if (doc.getDocument().contains("cachedName")) {
                                tempCachedName = doc.getDocument().get("cachedName").toString();
                            }
                            if (callers.contains(tempPhoneNumber)) {
                                UserCallsSummaryModel userCallsSummaryModel = userCallsSummaryMap.get(tempPhoneNumber);
                                userCallsSummaryModel.updateFromDatabase(true, tempCachedName);
                                if ((doc.getType() == DocumentChange.Type.ADDED) || (doc.getType() == DocumentChange.Type.MODIFIED)) {
                                    userCallsSummaryMap.put(tempPhoneNumber, userCallsSummaryModel);
                                    userCallsSummaryMap.notifyAll();
                                }
                            } //Jeśli nie ma na liście tempPhoneNumber to nie rób nic z tym
                        }

                    } catch (NullPointerException nullPointerException) {
                        Toast.makeText(activity, "Nie udało się pobrać danych :(", Toast.LENGTH_LONG).show();
                        System.out.println("Nie udało się pobrać danych :(" + e.getMessage());
                    }
                }
            });

//
//
//        collectionReference
//                .orderBy("phoneNumber", Query.Direction.ASCENDING)
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                        String contactName = "";
//                        String doc_id = "";
//                        try {
//                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
//                                doc_id = doc.getDocument().getId();
//                                String tempCachedName = "";
//                                String tempPhoneNumber = "";
//                                if (doc.getDocument().contains("cachedName")) {
//                                    tempCachedName = doc.getDocument().get("cachedName").toString();
//                                }
//                                if (doc.getDocument().contains("formattedCachedNumber")) {
//                                    tempPhoneNumber = doc.getDocument().get("phoneNumber").toString();
//                                }
//
//                                if (doc.getType() == DocumentChange.Type.ADDED) {
//                                    if(callers.contains(doc_id)){
//                                        System.out.println("+Snapshot ADDED doc_id"+doc_id+" tempPhoneNumber"+tempPhoneNumber+" tempCachedName"+tempCachedName);
//                                        CallLogUserStatsModel tempCallLogUserStatsModel = callLogModelUserCallsModelHashMap.get(doc_id);
//                                        if(tempCallLogUserStatsModel!=null) {
//                                            if (!tempCallLogUserStatsModel.getDownloadedFromCloud()) {
//                                                if (doc.getDocument().contains("cachedName")) {
//                                                    if (!isNullOrEmpty(doc.getDocument().get("cachedName").toString())) {
//                                                        contactName = doc.getDocument().get("cachedName").toString();
//                                                        tempCallLogUserStatsModel.setNameFromCloud(contactName);
//                                                        tempCallLogUserStatsModel.setDownloadedFromCloud(true);
//                                                        callLogModelUserCallsModelHashMap.put(doc_id,tempCallLogUserStatsModel);
//                                                        System.out.println("ADDED: " + doc_id + " ->" + contactName);
//
//                                                        adapter.notifyDataSetChanged();
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }else {
//                                        System.out.println("-Snapshot ADDED doc_id"+doc_id+" tempPhoneNumber "+tempPhoneNumber+" tempCachedName "+tempCachedName);
//                                    }
//
//
//                                }else if (doc.getType() == DocumentChange.Type.MODIFIED) {
//                                    System.out.println("Snapshot MODIFIED doc_id"+doc_id+" tempPhoneNumber "+tempPhoneNumber+" tempCachedName "+tempCachedName);
//                                }else if (doc.getType() == DocumentChange.Type.REMOVED) {
//                                    System.out.println("Snapshot REMOVED doc_id"+doc_id+" tempPhoneNumber "+tempPhoneNumber+" tempCachedName "+tempCachedName);
//                                }
//
//                                synchronized(callLogModelUserCallsModelHashMap){
//                                    callLogModelUserCallsModelHashMap.notify();
//                                }
//
//
//                            }
//
//                            int i=0;
//                            for (String caller:callers) {
//                                i++;
////                                                     System.out.println("Caller: "+i+" "+caller);
//                                if (callLogModelUserCallsModelHashMap.containsKey(caller)){
//                                    System.out.println("Pobrany z bazy: "  +caller+" "+callLogModelUserCallsModelHashMap.get(caller).getDownloadedFromCloud());
//                                }else {
//                                    System.out.println("Nie znaleziono: " +caller);
//                                }
//                            }
//
////                                                 callLogModelUserCallsModelHashMap.notifyAll();
//                        }catch (NullPointerException e1){
//                            Toast.makeText(activity,"Nie udało się pobrać danych :(",Toast.LENGTH_LONG).show();
//                            System.out.println("Nie udało się pobrać danych :("+e1.getMessage());
//                        }
////                                             x = tempCallLogUserStatsModel;
//                        adapter.notifyDataSetChanged();
//
//                    }
//
//                });


//
////
//        collectionReference
//                .orderBy("phoneNumber", Query.Direction.ASCENDING)
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                        String contactName = "";
//                        String doc_id = "";
//                        try{
//                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
//                                doc_id = doc.getDocument().getId();
//                                String tempCachedName = "";
//                                String tempFormattedCachedNumber = "";
//                                if (doc.getDocument().contains("cachedName")) {
//                                    tempCachedName = doc.getDocument().get("cachedName").toString();
//                                }
//                                if (doc.getDocument().contains("formattedCachedNumber")) {
//                                    tempFormattedCachedNumber = doc.getDocument().get("formattedCachedNumber").toString();
//                                }
////                                String tempCachedName = doc.getDocument().get("cachedName").toString();
////                                String tempFormattedCachedNumber = doc.getDocument().get("formattedCachedNumber").toString();
////                                System.out.println("doc.getDocument().getId() = "+doc_id);
//
//                                if (doc.getType() == DocumentChange.Type.ADDED) {
//                                    CallLogUserStatsModel tempCallLogUserStatsModel = callLogModelUserCallsModelHashMap.get(tempFormattedCachedNumber);
//                                    if (tempCallLogUserStatsModel!=null) {
//
//                                        System.out.println("tempCallLogUserStatsModel!=null :(");
//                                        if (!tempCallLogUserStatsModel.getDownloadedFromCloud()) {
//                                            if (doc.getDocument().contains("cachedName")) {
//                                                if (!isNullOrEmpty(doc.getDocument().get("cachedName").toString())) {
//                                                    contactName = doc.getDocument().get("cachedName").toString();
//                                                    tempCallLogUserStatsModel.setNameFromCloud(contactName);
//                                                    tempCallLogUserStatsModel.setDownloadedFromCloud(true);
//                                                    System.out.println("ADDED: " + doc_id + " ->" + contactName);
//                                                }
//                                            }
//                                        }
//                                    }
//                                    else{
//                                        System.out.println("doc.getDocument().getId() = "+doc_id+" tempCallLogUserStatsModel==null :(");
//                                    }
//                                } else if (doc.getType() == DocumentChange.Type.MODIFIED) {
////                                    (""+cachedFormattedNumber
//                                    CallLogUserStatsModel tempCallLogUserStatsModel = callLogModelUserCallsModelHashMap.get(""+doc_id);
//                                    if (tempCallLogUserStatsModel!=null) {
//                                        if (!tempCallLogUserStatsModel.getDownloadedFromCloud()) {
//                                            if (doc.getDocument().contains("cachedName")) {
//                                                if (!isNullOrEmpty(doc.getDocument().get("cachedName").toString())) {
//                                                    contactName = doc.getDocument().get("cachedName").toString();
//                                                    tempCallLogUserStatsModel.setNameFromCloud(""+contactName);
//                                                    tempCallLogUserStatsModel.setDownloadedFromCloud(true);
//                                                    System.out.println("MODIFIED: " + doc_id + " ->" + contactName);
//                                                }
//                                            }
//                                        }
//                                    }
//                                } else if (doc.getType() == DocumentChange.Type.REMOVED) {
////                                    list.remove(doc.getOldIndex());
////                                    mAdapter.notifyItemRemoved(doc.getOldIndex());
////                                    getSupportActionBar().setTitle("Users ("+list.size()+")");
//                                }
//                            }
//
//                            adapter.notifyDataSetChanged();
//
////                            Toast.makeText(activity,"Liczba pobranych z bazy: "+callersFromCloud.size(),Toast.LENGTH_LONG).show();
////                            for (CallLogModel call:listOfAllCalls) {
////                                if(call.getCachedName()==call.getCachedFormattedNumber()){
////                                    String newName = callersFromCloud.get(call.getCachedName());
////                                    call.setDownloadedFromDatabase(true);
////                                    call.setCachedName(newName);
////                                }
////                            }
//
//
//                        }catch (NullPointerException e1){
//                            Toast.makeText(activity,"Nie udało się pobrać danych :(",Toast.LENGTH_LONG).show();
//                            System.out.println("Nie udało się pobrać danych :("+e1.getMessage());
//                        }
//                    }
//                });

        }
    }
}
