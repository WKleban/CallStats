package pl.wotu.callstats;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocalCallLogFragment extends Fragment {
    private static final String TAG = "LocalCallLogFragment";
    private NavController navController;
    private RecyclerView recyclerView;
    private CallListAdapter adapter;

    private Set<String> callerNamesSet;
    private List<UserCallsSummaryModel> callsPerUserList;
    private Map<String,UserCallsSummaryModel> callsMap;

    private Activity activity;
    private Context context;

//    private Set<String> callers;
    private List<UserCallsSummaryModel> userCallsList;

    private List<CallLogModel> listOfCalls;

    //TODO to będzie po to żeby pokazać zsumowane rozmowy
    private List<CallLogModel> listOfCallers;
    private Set<String> setOfCallers;

    private boolean isReadyListOfCalls=false;
//    private HashSet<CallLogModel> listOfAllCalls;
    private HashMap<String, CallLogUserStatsModel> callLogModelUserCallsModelHashMap;



//    private HashSet <CallLogModel> listOfLastCallsPerUser;

    public LocalCallLogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        activity = getActivity();
        context = activity.getApplicationContext();

//        db = FirebaseFirestore.getInstance();
//        mAuth = FirebaseAuth.getInstance();

        return inflater.inflate(R.layout.fragment_local_call_log, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.local_call_log_rv);

        TelephonyManager telephonyManger = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if(telephonyManger != null) {
            telephonyManger.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

        navController = Navigation.findNavController(view);

        callerNamesSet = new TreeSet<>();
        callsPerUserList = new ArrayList<>();
        callsMap = new HashMap<>();
        userCallsList = new ArrayList<>();



        if (!isReadyListOfCalls) {
            setCallLogToList(getCursor());
        }

//        adapter = new LocalCallListAdapter(getActivity(),callerNamesSet,callsPerUserList,callsMap);

        System.out.println("listOfEntries.size()"+ listOfCalls.size());
        adapter = new CallListAdapter(getActivity(), listOfCalls);
        Log.d(TAG, "onViewCreated: new adapter");

//

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager =new LinearLayoutManager(getActivity()) ;
        RecyclerView.ItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),mLayoutManager.getOrientation());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(mDividerItemDecoration);
        recyclerView.setAdapter(adapter);




//        listOfAllCalls = new ArrayList<>();
//        listOfLastCallsPerUser = new ArrayList<>();
//
//
//        userCallsSummaryMap = new HashMap<>();
//        callLogList = new ArrayList<>();
//
//
//        callers = new TreeSet<>();
//        callersFromCloud = new HashMap<>();  //Pobrane z chmury
//        callLogModelUserCallsModelHashMap = new HashMap<>();

////
//        userCalls = new HashMap<>();
////
//
//        adapter = new LocalCallListAdapter(getActivity(),listOfLastCallsPerUser,callLogModelUserCallsModelHashMap);
        adapter = new CallListAdapter(getActivity(), listOfCalls);

//        Cursor cursor = getCursor();
//        if (cursor!=null){
//        }else {
//            Toast.makeText(getActivity(),"Liczba pozycji po wykonaniu getCursor() = null",Toast.LENGTH_LONG).show();
//        }


    }


//    private void setCallLogToList(Cursor cursor) {
//
//        String phoneModel = Build.MANUFACTURER+" "+ Build.MODEL;
//        String androidId = Settings.Global.getString(context.getContentResolver(), Settings.Global.DEVICE_NAME);
//        System.out.println("phoneModel = "+phoneModel);
//        System.out.println("androidId = "+androidId);
//
//
//        for (int i = 0; i < cursor.getCount(); i++) {
//            String phoneNumber = ""+cursor.getString(0);
//            int type = Integer.parseInt(cursor.getString(1));
//            int duration = Integer.parseInt(cursor.getString(2));
//            String cachedName = ""+cursor.getString(3);
//            int callID = Integer.parseInt(cursor.getString(4));
//            Date callDate = new Date(Long.parseLong(cursor.getString(5)));
//            String cachedFormattedNumber = ""+cursor.getString(6);
//            String countryISO = ""+cursor.getString(7);
//            Date lastModified = new Date(Long.parseLong(cursor.getString(8)));
//            boolean isNew = Boolean.parseBoolean(cursor.getString(9));
//            String phoneAccountId = ""+cursor.getString(10);
//
//            //Wszystkie połączenia
//            CallLogModel callLogEntry = new CallLogModel(phoneNumber, type, duration, cachedName, callID, callDate, cachedFormattedNumber, countryISO, lastModified, isNew ,phoneAccountId, androidId, phoneModel);
//
//            //Połączenia z podziałem na użytkowników
//            UserCallsSummaryModel userCallsSummaryModel;
////            UserCallsSummaryModel userCallsSummaryModel = new UserCallsSummaryModel(phoneNumber, cachedFormattedNumber, cachedName, lastCallType, lastCallDuration, lastCallID, lastCallDate, countryISO,phoneModel);
//
//
//            if (callers.contains(phoneNumber)){
//                userCallsList.get(0).get(phoneNumber);
////                userCallsSummaryModel = .get(phoneNumber);
//                int lastCallType = userCallsSummaryModel.getLastCalltype(); //OK
//                int durationOfCalls = userCallsSummaryModel.getDurationOfCalls() + 1; //+1
//                int lastCallID = userCallsSummaryModel.getLastCallID();
//                Date lastCallDate = userCallsSummaryModel.getLastCallDate();
//
//                if (callDate.getTime()>lastCallDate.getTime()){
//                    lastCallDate = callDate;
//                    lastCallID = callID;
//                }
//                userCallsSummaryModel.updateSummaryInfo(lastCallType,durationOfCalls,lastCallID,lastCallDate);
//            }else {
//                callers.add(phoneNumber);
//
//
//
//
////    public UserCallsSummaryModel(String phoneNumber,
////                        String cachedFormattedNumber,
////                        String cachedName,
////                int lastCalltype,
////                int durationOfCalls,
////                int lastCallDuration,
////                int lastCallID,
////                Date lastCallDate,
////                String countryISO,
////                String phoneAccountId,
////                String androidId,
////                String phoneModel){
//
//                userCallsSummaryModel = new UserCallsSummaryModel(phoneNumber, cachedFormattedNumber, cachedName, type, duration, callID, callDate, countryISO,phoneAccountId, androidId, phoneModel);
//            }
//
//            callLogList.add(callLogEntry);
//            userCallsSummaryMap.put(phoneNumber,userCallsSummaryModel);
//
//            cursor.moveToNext();
//
//        }
////        Log.d("listOfLastCallsPerUser size",""+listOfLastCallsPerUser.size());
////        Log.d("listOfAllCalls size",""+listOfAllCalls.size());
//
////        TODO FIREBASE
//        CollectionReference collectionReference = db.collection("Users").document("testUser").collection("Contacts");
//        collectionReference.orderBy("phoneNumber", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
////                String doc_id="";
//                String tempPhoneNumber = "";
//                String tempCachedName = "";
//
//                try{
//                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
////                        doc_id = doc.getDocument().getId();
//                        if (doc.getDocument().contains("phoneNumber")) {
//                            tempPhoneNumber = doc.getDocument().get("phoneNumber").toString();
//                        }
//                        if (doc.getDocument().contains("cachedName")) {
//                            tempCachedName = doc.getDocument().get("cachedName").toString();
//                        }
//                        if (callers.contains(tempPhoneNumber)){
//                            UserCallsSummaryModel userCallsSummaryModel = userCallsSummaryMap.get(tempPhoneNumber);
//                            userCallsSummaryModel.updateFromDatabase(true,tempCachedName);
//                            if ((doc.getType() == DocumentChange.Type.ADDED)|| (doc.getType() == DocumentChange.Type.MODIFIED)){
//                                userCallsSummaryMap.put(tempPhoneNumber,userCallsSummaryModel);
//                                userCallsSummaryMap.notifyAll();
//                            }
//                        } //Jeśli nie ma na liście tempPhoneNumber to nie rób nic z tym
//                    }
//
//                }catch (NullPointerException nullPointerException){
//                    Toast.makeText(activity,"Nie udało się pobrać danych :(",Toast.LENGTH_LONG).show();
//                    System.out.println("Nie udało się pobrać danych :("+e.getMessage());
//                }
//            }
//        });
//
////
////
////        collectionReference
////                .orderBy("phoneNumber", Query.Direction.ASCENDING)
////                .addSnapshotListener(new EventListener<QuerySnapshot>() {
////                    @Override
////                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
////                        String contactName = "";
////                        String doc_id = "";
////                        try {
////                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
////                                doc_id = doc.getDocument().getId();
////                                String tempCachedName = "";
////                                String tempPhoneNumber = "";
////                                if (doc.getDocument().contains("cachedName")) {
////                                    tempCachedName = doc.getDocument().get("cachedName").toString();
////                                }
////                                if (doc.getDocument().contains("formattedCachedNumber")) {
////                                    tempPhoneNumber = doc.getDocument().get("phoneNumber").toString();
////                                }
////
////                                if (doc.getType() == DocumentChange.Type.ADDED) {
////                                    if(callers.contains(doc_id)){
////                                        System.out.println("+Snapshot ADDED doc_id"+doc_id+" tempPhoneNumber"+tempPhoneNumber+" tempCachedName"+tempCachedName);
////                                        CallLogUserStatsModel tempCallLogUserStatsModel = callLogModelUserCallsModelHashMap.get(doc_id);
////                                        if(tempCallLogUserStatsModel!=null) {
////                                            if (!tempCallLogUserStatsModel.getDownloadedFromCloud()) {
////                                                if (doc.getDocument().contains("cachedName")) {
////                                                    if (!isNullOrEmpty(doc.getDocument().get("cachedName").toString())) {
////                                                        contactName = doc.getDocument().get("cachedName").toString();
////                                                        tempCallLogUserStatsModel.setNameFromCloud(contactName);
////                                                        tempCallLogUserStatsModel.setDownloadedFromCloud(true);
////                                                        callLogModelUserCallsModelHashMap.put(doc_id,tempCallLogUserStatsModel);
////                                                        System.out.println("ADDED: " + doc_id + " ->" + contactName);
////
////                                                        adapter.notifyDataSetChanged();
////                                                    }
////                                                }
////                                            }
////                                        }
////                                    }else {
////                                        System.out.println("-Snapshot ADDED doc_id"+doc_id+" tempPhoneNumber "+tempPhoneNumber+" tempCachedName "+tempCachedName);
////                                    }
////
////
////                                }else if (doc.getType() == DocumentChange.Type.MODIFIED) {
////                                    System.out.println("Snapshot MODIFIED doc_id"+doc_id+" tempPhoneNumber "+tempPhoneNumber+" tempCachedName "+tempCachedName);
////                                }else if (doc.getType() == DocumentChange.Type.REMOVED) {
////                                    System.out.println("Snapshot REMOVED doc_id"+doc_id+" tempPhoneNumber "+tempPhoneNumber+" tempCachedName "+tempCachedName);
////                                }
////
////                                synchronized(callLogModelUserCallsModelHashMap){
////                                    callLogModelUserCallsModelHashMap.notify();
////                                }
////
////
////                            }
////
////                            int i=0;
////                            for (String caller:callers) {
////                                i++;
//////                                                     System.out.println("Caller: "+i+" "+caller);
////                                if (callLogModelUserCallsModelHashMap.containsKey(caller)){
////                                    System.out.println("Pobrany z bazy: "  +caller+" "+callLogModelUserCallsModelHashMap.get(caller).getDownloadedFromCloud());
////                                }else {
////                                    System.out.println("Nie znaleziono: " +caller);
////                                }
////                            }
////
//////                                                 callLogModelUserCallsModelHashMap.notifyAll();
////                        }catch (NullPointerException e1){
////                            Toast.makeText(activity,"Nie udało się pobrać danych :(",Toast.LENGTH_LONG).show();
////                            System.out.println("Nie udało się pobrać danych :("+e1.getMessage());
////                        }
//////                                             x = tempCallLogUserStatsModel;
////                        adapter.notifyDataSetChanged();
////
////                    }
////
////                });
//
//
////
//////
////        collectionReference
////                .orderBy("phoneNumber", Query.Direction.ASCENDING)
////                .addSnapshotListener(new EventListener<QuerySnapshot>() {
////
////                    @Override
////                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
////                        String contactName = "";
////                        String doc_id = "";
////                        try{
////                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
////                                doc_id = doc.getDocument().getId();
////                                String tempCachedName = "";
////                                String tempFormattedCachedNumber = "";
////                                if (doc.getDocument().contains("cachedName")) {
////                                    tempCachedName = doc.getDocument().get("cachedName").toString();
////                                }
////                                if (doc.getDocument().contains("formattedCachedNumber")) {
////                                    tempFormattedCachedNumber = doc.getDocument().get("formattedCachedNumber").toString();
////                                }
//////                                String tempCachedName = doc.getDocument().get("cachedName").toString();
//////                                String tempFormattedCachedNumber = doc.getDocument().get("formattedCachedNumber").toString();
//////                                System.out.println("doc.getDocument().getId() = "+doc_id);
////
////                                if (doc.getType() == DocumentChange.Type.ADDED) {
////                                    CallLogUserStatsModel tempCallLogUserStatsModel = callLogModelUserCallsModelHashMap.get(tempFormattedCachedNumber);
////                                    if (tempCallLogUserStatsModel!=null) {
////
////                                        System.out.println("tempCallLogUserStatsModel!=null :(");
////                                        if (!tempCallLogUserStatsModel.getDownloadedFromCloud()) {
////                                            if (doc.getDocument().contains("cachedName")) {
////                                                if (!isNullOrEmpty(doc.getDocument().get("cachedName").toString())) {
////                                                    contactName = doc.getDocument().get("cachedName").toString();
////                                                    tempCallLogUserStatsModel.setNameFromCloud(contactName);
////                                                    tempCallLogUserStatsModel.setDownloadedFromCloud(true);
////                                                    System.out.println("ADDED: " + doc_id + " ->" + contactName);
////                                                }
////                                            }
////                                        }
////                                    }
////                                    else{
////                                        System.out.println("doc.getDocument().getId() = "+doc_id+" tempCallLogUserStatsModel==null :(");
////                                    }
////                                } else if (doc.getType() == DocumentChange.Type.MODIFIED) {
//////                                    (""+cachedFormattedNumber
////                                    CallLogUserStatsModel tempCallLogUserStatsModel = callLogModelUserCallsModelHashMap.get(""+doc_id);
////                                    if (tempCallLogUserStatsModel!=null) {
////                                        if (!tempCallLogUserStatsModel.getDownloadedFromCloud()) {
////                                            if (doc.getDocument().contains("cachedName")) {
////                                                if (!isNullOrEmpty(doc.getDocument().get("cachedName").toString())) {
////                                                    contactName = doc.getDocument().get("cachedName").toString();
////                                                    tempCallLogUserStatsModel.setNameFromCloud(""+contactName);
////                                                    tempCallLogUserStatsModel.setDownloadedFromCloud(true);
////                                                    System.out.println("MODIFIED: " + doc_id + " ->" + contactName);
////                                                }
////                                            }
////                                        }
////                                    }
////                                } else if (doc.getType() == DocumentChange.Type.REMOVED) {
//////                                    list.remove(doc.getOldIndex());
//////                                    mAdapter.notifyItemRemoved(doc.getOldIndex());
//////                                    getSupportActionBar().setTitle("Users ("+list.size()+")");
////                                }
////                            }
////
////                            adapter.notifyDataSetChanged();
////
//////                            Toast.makeText(activity,"Liczba pobranych z bazy: "+callersFromCloud.size(),Toast.LENGTH_LONG).show();
//////                            for (CallLogModel call:listOfAllCalls) {
//////                                if(call.getCachedName()==call.getCachedFormattedNumber()){
//////                                    String newName = callersFromCloud.get(call.getCachedName());
//////                                    call.setDownloadedFromDatabase(true);
//////                                    call.setCachedName(newName);
//////                                }
//////                            }
////
////
////                        }catch (NullPointerException e1){
////                            Toast.makeText(activity,"Nie udało się pobrać danych :(",Toast.LENGTH_LONG).show();
////                            System.out.println("Nie udało się pobrać danych :("+e1.getMessage());
////                        }
////                    }
////                });
//
//
//    }

    private void setCallLogToList(Cursor cursor) {

        String phoneModel = Build.MANUFACTURER+" "+ Build.MODEL;
        String androidId = Settings.Global.getString(context.getContentResolver(), Settings.Global.DEVICE_NAME);
        System.out.println("phoneModel = "+phoneModel);
        System.out.println("androidId = "+androidId);


        Map<String, String> card0 = CallAppConfig.getSimCardsInformation(activity, context, 0);
        Map<String, String> card1 = CallAppConfig.getSimCardsInformation(activity, context, 1);

        String getSimSlotIndex_0 =  card0.get("getSimSlotIndex");
        String getIccId_0 = card0.get("getIccId");
        String getCarrierName_0 = card0.get("getCarrierName"); //Nazwa sieci
        String getCountryIso_0 = card0.get("getCountryIso");
        String getDisplayName_0 = card0.get("getDisplayName");
        String getNumber_0 = card0.get("getNumber");
        String getDataRoaming_0 = card0.get("getDataRoaming");

        String getSimSlotIndex_1 =  card0.get("getSimSlotIndex");
        String getIccId_1 = card0.get("getIccId");
        String getCarrierName_1 = card0.get("getCarrierName");
        String getCountryIso_1 = card0.get("getCountryIso");
        String getDisplayName_1 = card0.get("getDisplayName");
        String getNumber_1 = card0.get("getNumber");
        String getDataRoaming_1 = card0.get("getDataRoaming");

        Date startDate = null,endDate=null;
        try {
            startDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-04-01 00:00:00");
            endDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-04-30 23:59:59");
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        callers = new TreeSet<>();
//        listOfAllCalls = new HashSet<>();
//        callLogModelUserCallsModelHashMap = new HashMap<>();
//        listOfLastCallsPerUser = new HashSet<>();
        listOfCalls = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            String phoneNumber = cursor.getString(0);
            int type = Integer.parseInt(cursor.getString(1));
            int duration = Integer.parseInt(cursor.getString(2));
            String cachedName = cursor.getString(3);
            int callID = Integer.parseInt(cursor.getString(4));
            Date callDate = new Date(Long.parseLong(cursor.getString(5)));
            String cachedFormattedNumber = cursor.getString(6);
            String countryISO = cursor.getString(7);
            Date lastModified = new Date(Long.parseLong(cursor.getString(8)));
            boolean isNew = Boolean.parseBoolean(cursor.getString(9));
            String phoneAccountId = cursor.getString(10);
//            if (isNullOrEmpty(cachedFormattedNumber)){cachedFormattedNumber="Numer prywatny";}
//            if (isNullOrEmpty(cachedName)){cachedName = cachedFormattedNumber;}

//            if (isNullOrEmpty(cachedName)){cachedName="Numer prywatny";}


            CallLogModel callLogEntry = new CallLogModel(phoneNumber, type, duration, cachedName, callID, callDate, cachedFormattedNumber, countryISO, lastModified, isNew ,phoneAccountId, androidId, phoneModel);


            listOfCalls.add(callLogEntry);

            System.out.println(callLogEntry.getCachedName()+" "+callLogEntry.getCachedFormattedNumber());
            //            CallLogUserStatsModel callLogUserSummary = new CallLogUserStatsModel();

//            listOfAllCalls.add(callLogEntry);
//            userCallsList.add(callLogEntry);

//            if(!callers.contains(""+phoneNumber)){
//                callers.add(""+phoneNumber);
//                callLogUserSummary = new CallLogUserStatsModel(duration, 1,cachedName,false);
//                callLogModelUserCallsModelHashMap.put(""+cachedFormattedNumber, callLogUserSummary);
////                System.out.println("Pierwszy raz ->"+cachedName+", duration="+duration+", count=1");
//                listOfLastCallsPerUser.add(callLogEntry);
//            }
//            else{
//                callLogUserSummary = callLogModelUserCallsModelHashMap.get(""+cachedFormattedNumber);
//                int summaryDuration = callLogUserSummary.getDuration() + duration;
//                int summaryCount = callLogUserSummary.getCount() + 1;
//                callLogUserSummary.setDuration(summaryDuration);
//                callLogUserSummary.setCount(summaryCount);
////                System.out.println("Kolejny raz ->"+cachedName+", duration="+summaryDuration+", count="+summaryCount);
//            }

//            if(!callers.contains(""+cachedName)){
//                callers.add(""+cachedName);
//                callLogUserSummary = new CallLogUserStatsModel(duration, 1);
//                callLogModelUserCallsModelHashMap.put(""+cachedName, callLogUserSummary);
////                System.out.println("Pierwszy raz ->"+cachedName+", duration="+duration+", count=1");
//                listOfLastCallsPerUser.add(callLogEntry);
//            }else{
//                callLogUserSummary = callLogModelUserCallsModelHashMap.get(""+cachedName);
//                int summaryDuration = callLogUserSummary.getDuration() + duration;
//                int summaryCount = callLogUserSummary.getCount() + 1;
//                callLogUserSummary.setDuration(summaryDuration);
//                callLogUserSummary.setCount(summaryCount);
////                System.out.println("Kolejny raz ->"+cachedName+", duration="+summaryDuration+", count="+summaryCount);
//            }

            cursor.moveToNext();

        }
//        Log.d("listOfLastCallsPerUser size",""+listOfLastCallsPerUser.size());
//        Log.d("listOfAllCalls size",""+listOfAllCalls.size());

//        TODO FIREBASE
//        CollectionReference collectionReference = db.collection("Users").document("testUser").collection("Contacts");

//        collectionReference
//                .orderBy("phoneNumber", Query.Direction.ASCENDING)
//
//
//
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                         @Override
//                                         public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                                             String contactName = "";
//                                             String doc_id = "";
//                                             try {
//                                                 for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
//                                                     doc_id = doc.getDocument().getId();
//                                                     String tempCachedName = "";
//                                                     String tempPhoneNumber = "";
//                                                     if (doc.getDocument().contains("cachedName")) {
//                                                         tempCachedName = doc.getDocument().get("cachedName").toString();
//                                                     }
//                                                     if (doc.getDocument().contains("phoneNumber")) {
//                                                         tempPhoneNumber = doc.getDocument().get("phoneNumber").toString();
//                                                     }
//
//                                                     if (doc.getType() == DocumentChange.Type.ADDED) {
//                                                        if(callers.contains(doc_id)){
//                                                            System.out.println("+Snapshot ADDED doc_id"+doc_id+" tempPhoneNumber"+tempPhoneNumber+" tempCachedName"+tempCachedName);
//                                                            CallLogUserStatsModel tempCallLogUserStatsModel = callLogModelUserCallsModelHashMap.get(doc_id);
//                                                            if(tempCallLogUserStatsModel!=null) {
//                                                                if (!tempCallLogUserStatsModel.getDownloadedFromCloud()) {
//                                                                    if (doc.getDocument().contains("cachedName")) {
//                                                                        if (!isNullOrEmpty(doc.getDocument().get("cachedName").toString())) {
//                                                                            contactName = doc.getDocument().get("cachedName").toString();
//                                                                            tempCallLogUserStatsModel.setNameFromCloud(contactName);
//                                                                            tempCallLogUserStatsModel.setDownloadedFromCloud(true);
//                                                                            callLogModelUserCallsModelHashMap.put(doc_id,tempCallLogUserStatsModel);
//                                                                            System.out.println("ADDED: " + doc_id + " ->" + contactName);
//
//                                                                            adapter.notifyDataSetChanged();
//                                                                        }
//                                                                    }
//                                                                }
//                                                            }
//                                                        }else {
//                                                            System.out.println("-Snapshot ADDED doc_id"+doc_id+" tempPhoneNumber "+tempPhoneNumber+" tempCachedName "+tempCachedName);
//                                                        }
//
//
//                                                     }else if (doc.getType() == DocumentChange.Type.MODIFIED) {
//                                                         System.out.println("Snapshot MODIFIED doc_id"+doc_id+" tempPhoneNumber "+tempPhoneNumber+" tempCachedName "+tempCachedName);
//                                                     }else if (doc.getType() == DocumentChange.Type.REMOVED) {
//                                                         System.out.println("Snapshot REMOVED doc_id"+doc_id+" tempPhoneNumber "+tempPhoneNumber+" tempCachedName "+tempCachedName);
//                                                     }
//
//                                                     synchronized(callLogModelUserCallsModelHashMap){
//                                                         callLogModelUserCallsModelHashMap.notify();
//                                                     }
//
//
//                                                 }
//
//                                                 int i=0;
//                                                 for (String caller:callers) {
//                                                     i++;
////                                                     System.out.println("Caller: "+i+" "+caller);
//                                                     if (callLogModelUserCallsModelHashMap.containsKey(caller)){
//                                                     System.out.println("Pobrany z bazy: "  +caller+" "+callLogModelUserCallsModelHashMap.get(caller).getDownloadedFromCloud());
//                                                     }else {
//                                                         System.out.println("Nie znaleziono: " +caller);
//                                                     }
//                                                 }
//
////                                                 callLogModelUserCallsModelHashMap.notifyAll();
//                                             }catch (NullPointerException e1){
//                                                 Toast.makeText(activity,"Nie udało się pobrać danych :(",Toast.LENGTH_LONG).show();
//                                                 System.out.println("Nie udało się pobrać danych :("+e1.getMessage());
//                                             }
////                                             x = tempCallLogUserStatsModel;
//                                             adapter.notifyDataSetChanged();
//
//                                         }
//
//                                     });


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

            Toast.makeText(getActivity(),"Liczba pozycji po wykonaniu getCursor() = "+cursor.getCount(),Toast.LENGTH_LONG).show();
            return cursor;
        }else {
            return null;
        }
    }

    final PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            if (state == TelephonyManager.CALL_STATE_RINGING) {
//                System.out.println("incomingNumber"+incomingNumber);
                System.out.println("TelephonyManager.CALL_STATE_RINGING,incomingNumber"+incomingNumber);



            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
//                setCallLogToList(getCursor());
                isReadyListOfCalls = true;

            }

        }

    };


    private boolean isNullOrEmpty(String string) {
        if ((string==null)||(string.equals(""))){
            return true;
        }else{
            return false;
        }
    }
}
