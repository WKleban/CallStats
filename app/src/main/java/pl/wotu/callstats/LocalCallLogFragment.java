package pl.wotu.callstats;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocalCallLogFragment extends Fragment {
    private static final String TAG = "LocalCallLogFragment";

    private Activity activity;
    private Context context;

    private NavController navController;
    private RecyclerView recyclerView;
    private CallListAdapter adapter;

    private List<CallLogModel> listOfCalls;
    private List<CallLogModel> listOfCallers;


    //TODO to będzie po to żeby pokazać zsumowane rozmowy
    private Map<String,CallLogModel> mapOfCallers;
    private Set<String> setOfCallers;

    private boolean isReadyListOfCalls=false;

    public LocalCallLogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        activity = getActivity();
        context = activity.getApplicationContext();

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

//        getAllSms();

        mapOfCallers = new HashMap<>();
        setOfCallers = new HashSet<>();

        if (!isReadyListOfCalls) {
            setCallLogToList(getCallLogCursor(),getAllSms());
        }

        System.out.println("listOfEntries.size()"+ listOfCalls.size());
        adapter = new CallListAdapter(getActivity(), listOfCallers);
        Log.d(TAG, "onViewCreated: new adapter");

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager =new LinearLayoutManager(getActivity()) ;
        RecyclerView.ItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),mLayoutManager.getOrientation());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(mDividerItemDecoration);
        recyclerView.setAdapter(adapter);

        adapter = new CallListAdapter(getActivity(), listOfCalls);
//        getSMSCursor();
    }

    private void setCallLogToList(Cursor cursor, List<Sms> allSms) {

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

        listOfCalls = new ArrayList<>();
        listOfCallers = new ArrayList<>();

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


            CallLogModel callLogSingleEntry = new CallLogModel(phoneNumber, type, duration, cachedName, callID, callDate, cachedFormattedNumber, countryISO, lastModified, isNew ,phoneAccountId, androidId, phoneModel,duration,1);
            listOfCalls.add(callLogSingleEntry);

            String caller = simplifyPhoneNumber(cachedFormattedNumber);
//            if (!CallAppConfig.isNullOrEmpty(cachedName)){
//                caller = cachedFormattedNumber;
//            }else {
//                caller = cachedFormattedNumber;
//            }


            setOfCallers.add(caller);
//            int indexOfEntry = 0;
            CallLogModel callLogUserEntry;
            if (mapOfCallers.containsKey(caller)){
                callLogUserEntry = mapOfCallers.get(caller);
                int indexOfEntry = listOfCallers.indexOf(callLogUserEntry);
                callLogUserEntry.setNumberOfCalls(callLogUserEntry.getNumberOfCalls()+1);
                callLogUserEntry.setDurationOfTheWhole(callLogUserEntry.getDurationOfTheWhole()+duration);

                listOfCallers.remove(indexOfEntry);
                listOfCallers.add(indexOfEntry,callLogUserEntry);

            }else {
                callLogUserEntry = callLogSingleEntry;
                listOfCallers.add(callLogUserEntry);
                mapOfCallers.put(caller,callLogUserEntry);
            }


            //TODO tutaj będzie sumaryczna liczba i czas rozmów
//            setOfCallers.add(cachedFormattedNumber);
//            listOfCallers.get(cachedFormattedNumber);
            mapOfCallers.put(caller,callLogUserEntry);

//            System.out.println(callLogSingleEntry.getCachedName()+" "+callLogSingleEntry.getCachedFormattedNumber());

            cursor.moveToNext();

        }
        for (Sms sms:allSms) {
            //TODO trzeba będzie uprościć całość do SmsModel
            SmsModel smsModel = new SmsModel(sms._id,sms._address,sms._msg,sms._readState,sms._time,sms._folderName);

            CallLogModel callLogSingleEntry = new CallLogModel(sms._address, 0, 0, sms._address, 0,  null,sms._address, null, null, false ,null, androidId, phoneModel,0,1);
            listOfCalls.add(callLogSingleEntry);

            String caller = simplifyPhoneNumber(sms._address);
//            if (!CallAppConfig.isNullOrEmpty(sms._address)){
//                caller = simplifyPhoneNumber(sms._address);
//            }else {
//                caller = sms._address;
//            }
            CallLogModel callLogUserEntry;
            if(setOfCallers.contains(caller)){
                callLogUserEntry = mapOfCallers.get(caller);
                if (callLogUserEntry==null){
                    callLogUserEntry = new CallLogModel();
                }
                if (callLogUserEntry.getLastSms()==null){
                    callLogUserEntry.setLastSms(smsModel);
                }else {
                    //Głupie rozwiązanie ale działa
                    long x = Long.parseLong(smsModel.get_time().trim());
                    long y = Long.parseLong(callLogUserEntry.getLastSms().get_time());
                    if(x>y){
                        callLogUserEntry.setLastSms(smsModel);
                    }
                }


                if (listOfCallers.contains(callLogUserEntry)){
                    int indexOfEntry = listOfCallers.indexOf(callLogUserEntry);
                    listOfCallers.remove(indexOfEntry);
                    listOfCallers.add(indexOfEntry,callLogUserEntry);
                }else {
                    listOfCallers.add(callLogUserEntry);
                }


            }else {
                callLogUserEntry = new CallLogModel(smsModel,null);


                setOfCallers.add(caller);
            }


//            int indexOfEntry = 0;

            if (mapOfCallers.containsKey(caller)){


//                callLogUserEntry.setNumberOfCalls(callLogUserEntry.getNumberOfCalls()+1);
//                callLogUserEntry.setDurationOfTheWhole(callLogUserEntry.getDurationOfTheWhole()+duration);



            }else {
                callLogUserEntry = callLogSingleEntry;
                listOfCallers.add(callLogUserEntry);
            }


        }
//        Log.d("listOfLastCallsPerUser size",""+listOfLastCallsPerUser.size());
//        Log.d("listOfAllCalls size",""+listOfAllCalls.size());

    }

    private String simplifyPhoneNumber(String cachedFormattedNumber) {
        String simplePhoneNumber = cachedFormattedNumber.replace(" ", "");
        simplePhoneNumber = simplePhoneNumber.replace("+48","");
        return simplePhoneNumber;
    }

    public List<Sms> getAllSms() {
        List<Sms> lstSms = new ArrayList<Sms>();
        Sms objSms = new Sms();
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = activity.getContentResolver();

        Cursor c = cr.query(message, null, null, null, null);
        activity.startManagingCursor(c);
        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {

                objSms = new Sms();
                objSms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
                objSms.setAddress(c.getString(c
                        .getColumnIndexOrThrow("address")));
                objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
                objSms.setReadState(c.getString(c.getColumnIndex("read")));
                objSms.setTime(c.getString(c.getColumnIndexOrThrow("date")));
                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                    objSms.setFolderName("inbox");
                } else {
                    objSms.setFolderName("sent");
                }

                lstSms.add(objSms);
                c.moveToNext();
                /*
                 * // System.out.println("_id: "+objSms._id+"\n_address: "+objSms._address+"\n_folderName: "+objSms._folderName+"\n_msg: "+objSms._msg+"\n_readState: "+objSms._readState+"\n_time: "+objSms._time+"\n--------------");
                 */


            }
        }
        // else {
        // throw new RuntimeException("You have no SMS");
        // }
        c.close();

//            System.out.println("Liczba SMSów: "+lstSms.size());
        return lstSms;
    }
    public class Sms{
        private String _id;
        private String _address;
        private String _msg;
        private String _readState; //"0" for have not read sms and "1" for have read sms
        private String _time;
        private String _folderName;

        public String getId(){
            return _id;
        }
        public String getAddress(){
            return _address;
        }
        public String getMsg(){
            return _msg;
        }
        public String getReadState(){
            return _readState;
        }
        public String getTime(){
            return _time;
        }
        public String getFolderName(){
            return _folderName;
        }


        public void setId(String id){
            _id = id;
        }
        public void setAddress(String address){
            _address = address;
        }
        public void setMsg(String msg){
            _msg = msg;
        }
        public void setReadState(String readState){
            _readState = readState;
        }
        public void setTime(String time){
            _time = time;
        }
        public void setFolderName(String folderName){
            _folderName = folderName;
        }

    }

//    public List<String> getSMS(){
//        if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED){
//            List<String> sms = new ArrayList<>();
//            Uri uriSMSURI = Uri.parse("content://sms/all");
//            Cursor cur = activity.getContentResolver().query(uriSMSURI, null, null, null, null);
//
//            while (cur != null && cur.moveToNext()) {
//                String address = cur.getString(cur.getColumnIndex("address"));
//                String body = cur.getString(cur.getColumnIndexOrThrow("body"));
//                sms.add("Number: " + address + " .Message: " + body);
//                System.out.println("Number: " + address + " .Message: " + body);
//            }
//
//            if (cur != null) {
//                cur.close();
//            }
//            System.out.println("Liczba SMSów: "+sms.size());
//
//            return sms;
//        }
//        Toast.makeText(getContext(),"Brak Manifest.permission.READ_SMS",Toast.LENGTH_LONG).show();
//        System.out.println("Brak uprawnień Manifest.permission.READ_SMS");
//        return null;
//
//    }
//    private Cursor getSMSCursor(){
//
////        List<Sms> lstSms = new ArrayList<Sms>();
////        Sms objSms = new Sms();
//        Uri message = Uri.parse("content://sms/");
////        Cursor managedCursor = activity.getContentResolver().query(message, null, null, null, null);
//        String[] details = new String[]{"_id",                   //0
//                "_address",                                             //1
//                "_folderName",                                         //2
//                "_msg",                                      //3
//                "_readState",                                              //4
//                "_time"
//                //    _id: 3353
//                //    _address: +48886131121
//                //    _folderName: inbox
//                //    _msg: Panie Wojtku mam video spotkanie, jak coś pilnego proszę pisać, ja mogę zadzwonic za dwie godziny ok ?
//                //    _readState: 1
//                //    _time: 1588059461040
//        };
////        Cursor c = cr
////        activity.startManagingCursor(c);
////        int totalSMS = c.getCount();
//        Cursor cursor;
//        cursor = context.getContentResolver().query(message, details, null, null, "_id" + " DESC");
//        if (cursor.getCount() != 0) {
//            cursor.moveToFirst();
//        }
//
//        Toast.makeText(getActivity(),"[SMS] Liczba pozycji po wykonaniu getCursor() = "+cursor.getCount(),Toast.LENGTH_LONG).show();
//        return cursor;
//    }

    private Cursor getCallLogCursor() {
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
