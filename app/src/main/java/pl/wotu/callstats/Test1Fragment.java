package pl.wotu.callstats;

import android.Manifest;
import android.content.pm.PackageManager;
//import android.database.Cursor;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.CallLog;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 * A simple {@link Fragment} subclass.
 */
public class Test1Fragment extends Fragment {

    private List<CallLogModel> listOfCalls;
    private Set<String> callers;
    private HashMap<String, Integer> callsMap;
    private HashMap<String, Integer> callCountMap;
    private SimpleDateFormat formatter;
    private StringBuffer sb;
    private String manufacturer;
    private String name;
    private String model;
    private String  codename;
    private String deviceName;
//    private Date callDate;
//    private String dateString;


    public Test1Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sb = new StringBuffer();

        listOfCalls = new ArrayList<>();
        callers = new TreeSet<>();
        callsMap = new HashMap<>();
        callCountMap = new HashMap<>();


        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CALL_LOG)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CALL_LOG}, 1);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CALL_LOG}, 1);
            }
        } else {
            //do stuff
            TextView tv = view.findViewById(R.id.textView);

            Cursor cursor = getCursor();
            tv.setText(getCallDetails(cursor));
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getActivity(), "Permission Granted!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                return;

        }

    }

    /*
    CallLog.Calls.TYPE,
    CallLog.Calls.DURATION,
    CallLog.Calls.CACHED_NAME,
    CallLog.Calls._ID,
    CallLog.Calls.DATE,
    CallLog.Calls.CACHED_FORMATTED_NUMBER, //570 082 100
    CallLog.Calls.COUNTRY_ISO, //PL
    CallLog.Calls.LAST_MODIFIED, //1582571365480
    CallLog.Calls.NEW //0
    //                    CallLog.Calls.CACHED_LOOKUP_URI, //content://com.android.contacts/contacts/lookup/1170i716124fb8f9875dc/479
//                    CallLog.Calls.CACHED_MATCHED_NUMBER, //+48570082100
//                    CallLog.Calls.CACHED_NORMALIZED_NUMBER, //+48570082100
//                    CallLog.Calls.CACHED_NUMBER_TYPE, //2
//                    CallLog.Calls.CACHED_PHOTO_ID, //2651
//                    CallLog.Calls.CACHED_PHOTO_URI, //null
//                    CallLog.Calls.CALL_SCREENING_APP_NAME, //null
//                    CallLog.Calls.CALL_SCREENING_COMPONENT_NAME, //null
//                    CallLog.Calls.CONTENT_ITEM_TYPE,//błąd
//                    CallLog.Calls.CONTENT_TYPE, //błąd
//                    CallLog.Calls.DATA_USAGE, //NULL
//                    CallLog.Calls.DEFAULT_SORT_ORDER,
//                    CallLog.Calls.EXTRA_CALL_TYPE_FILTER,
//                    CallLog.Calls.FEATURES, //0
//                    CallLog.Calls.GEOCODED_LOCATION, //Polska
//                    CallLog.Calls.IS_READ, //null
    // date 6: ->1582569265941
//                    CallLog.Calls.LIMIT_PARAM_KEY,
//                    CallLog.Calls.NUMBER_PRESENTATION //1



    */

    private String getCallDetails(Cursor cursor) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {

//            String phoneID = "";
            String phoneModel = Build.MANUFACTURER+" "+ Build.MODEL;
//            String simCardID = "";
            String androidId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

                sb.append("phoneModel = " + phoneModel);
                sb.append("\nandroidId = " + androidId);
                sb.append("\n\n");
            sb.append("Rozmowy od 2020-04-01 do 2020-04-30\n");

//                    formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDate = null,endDate=null;
            try {
                startDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-04-01 00:00:00");
                endDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-04-30 23:59:59");
            } catch (ParseException e) {
                e.printStackTrace();
            }

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
                listOfCalls.add(new CallLogModel(phoneNumber, type, duration, cachedName, callID, callDate, cachedFormattedNumber, countryISO, lastModified, isNew, "", androidId, phoneModel));

                if (!isNullOrEmpty(cachedName)) {
                    callers.add(cachedName);


                    int durationAtCaller;
                    int callCount=0;

                    try {
                        durationAtCaller = callsMap.get(cachedName);
                    } catch (NullPointerException e) {
                        durationAtCaller = 0;
                    }
                    try {
                        callCount = callCountMap.get(cachedName);
                    } catch (NullPointerException e) {
                        callCount = 0;
                    }

//                    callCount = callCountMap

                    if(startDate!=null){
                        if(callDate.after(startDate) && callDate.before(endDate)) {
                            // In between
                            callsMap.put(cachedName, durationAtCaller + duration);
                            callCountMap.put(cachedName,callCount+1);

                        }else {
                            callsMap.put(cachedName, durationAtCaller);
                            callCountMap.put(cachedName,callCount);
                        }
                    }else {
                        callsMap.put(cachedName, durationAtCaller + duration);
                        callCountMap.put(cachedName,callCount);
                    }



                }


                cursor.moveToNext();

            }


            Toast.makeText(getActivity(), "cursor.getCount()" + cursor.getCount(), Toast.LENGTH_LONG).show();

            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
//                cursor.mo
                int i = 0;
                String number = cursor.getString(i++);
                Log.d("number " + (i), "->" + number);
                String type = cursor.getString(i++);
                Log.d("type " + (i), "->" + type);
                String duration = cursor.getString(i++);
                Log.d("duration " + (i), "->" + duration);
                String name = cursor.getString(i++);
                Log.d("name " + (i), "->" + name);
                String id = cursor.getString(i++);
                Log.d("id " + (i), "->" + id);
                String date = cursor.getString(i++);
                Log.d("date " + (i), "->" + date);
                String cachedFormattedNumber = cursor.getString(i++);

                for (i = 0; i < cursor.getColumnCount(); i++) {
                    Log.d("WOTU_LOG," + cursor.getColumnName(i), "" + cursor.getString(i));
                }
                Log.d("KONIEC", "----");
//
//                Log.d(cursor.getColumnName(i-1),"->"+cachedFormattedNumber);
//
//
//                Log.d("field nr "+(i+1),"->"+cursor.getString(i++));
//                Log.d("field nr "+(i+1),"->"+cursor.getString(i++));
//                Log.d("field nr "+(i+1),"->"+cursor.getString(i++));
//                Log.d("field nr "+(i+1),"->"+cursor.getString(i++));
//                Log.d("field nr "+(i+1),"->"+cursor.getString(i++));


//                String DATA_USAGE = cursor.getString(i++);
//                String DEFAULT_SORT_ORDER = cursor.getString(i++);
//                String EXTRA_CALL_TYPE_FILTER = cursor.getString(i++);
//                String FEATURES = cursor.getString(i++);
//                String GEOCODED_LOCATION = cursor.getString(i++);
//                String IS_READ = cursor.getString(i++);
//                String LAST_MODIFIED = cursor.getString(i++);
//                String LIMIT_PARAM_KEY = cursor.getString(i++);
//                String NEW = cursor.getString(i++);
//                String NUMBER_PRESENTATION =  cursor.getString(i++);


//                Log.d("call id",id);
//                Log.d("call number",number);

                Date callDate = new Date(Long.valueOf(date));
//                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateString = formatter.format(callDate);


                String dir = null;

                switch (Integer.parseInt(type)) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        dir = "OUTGOING";
                        break;

                    case CallLog.Calls.INCOMING_TYPE:
                        dir = "INCOMING";
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        dir = "MISSED";
                        break;
                    case CallLog.Calls.BLOCKED_TYPE:
                        dir = "BLOCKED";
                        break;
                    case CallLog.Calls.REJECTED_TYPE:
                        dir = "REJECTED";
                        break;
                }

/*
                sb.append("\nPhone number: " + number);
                sb.append("\ntype: " + type);
                sb.append("\nduration: " + duration);
                sb.append("\nname: " + name);
                sb.append("\nid: " + id);
                sb.append("\ndir: " + dir);
                sb.append("\nDate: " + dateString);

                sb.append("\n-------------FIRST");
*/
                while (cursor.moveToNext()) {

                    number = cursor.getString(0);
                    type = cursor.getString(1);
                    duration = cursor.getString(2);
                    name = cursor.getString(3);
                    id = cursor.getString(4);
                    date = cursor.getString(5);
//                     cachedName = cursor.getString(6);
//                     cachedNumberLabel = cursor.getString(7);
//                     viaNumber = cursor.getString(8);

                    callDate = new Date(Long.valueOf(date));
                    formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    dateString = formatter.format(callDate);


                    dir = null;


                    switch (Integer.parseInt(type)) {
                        case CallLog.Calls.OUTGOING_TYPE:
                            dir = "OUTGOING";
                            break;

                        case CallLog.Calls.INCOMING_TYPE:
                            dir = "INCOMING";
                            break;
                        case CallLog.Calls.MISSED_TYPE:
                            dir = "MISSED";
                            break;
                    }
                   /* sb.append("\nPhone number: ").append(number);
                    sb.append("\ntype: ").append(type);
                    sb.append("\nduration: ").append(duration);
                    sb.append("\nname: ").append(name);
                    sb.append("\nid: ").append(id);
                    sb.append("\ndir: ").append(dir);
                    sb.append("\nDate: ").append(dateString);*/

//                    sb.append("\nFEATURES: ").append(FEATURES);
//                    sb.append("\nGEOCODED_LOCATION: ").append(GEOCODED_LOCATION);
//                    sb.append("\nIS_READ: ").append(IS_READ);
//                    sb.append("\nLAST_MODIFIED: ").append(LAST_MODIFIED);
//                    sb.append("\nLIMIT_PARAM_KEY: ").append(LIMIT_PARAM_KEY);
//                    sb.append("\nNEW: ").append(NEW);
//                    sb.append("\nNUMBER_PRESENTATION: ").append(NUMBER_PRESENTATION);
//                    sb.append("\ncachedName: " + cachedName);
//                    sb.append("\ncachedNumberLabel: " + cachedNumberLabel);
//                    sb.append("\nviaNumber: " + viaNumber);
//                    sb.append("\n-------------");

                }
            }
            cursor.close();


                Log.d("Liczba rozmówców", "" + callers.size());


            int lacznyCzasRozmow = 0;

            for (String s : callers) {

                int czas = callsMap.get(s);
                int liczba = callCountMap.get(s);
                lacznyCzasRozmow +=czas;
                int sekundy = czas % 60;
                int minuty = czas / 60;
                int godziny = minuty / 60;
                minuty = minuty % 60;
                String czasString = "";
                if (godziny > 0) {
                    czasString = godziny + "h ";
                }
                if (minuty > 0) {
                    czasString = czasString + minuty + "m ";
                }
                if (sekundy > 0) {
                    czasString = czasString + sekundy + "s";
                }
                String napis = s + "<<"+liczba+">>, czas:" + czas + "->" + czasString;

                Log.d("CALLER:", napis);
//                sb.append(s+", czas rozmów:"+czas+"->"+czasString+"\n");
                if (czas>0){
                    sb.append(napis + "\n");
                }
            }

            int sekundy = lacznyCzasRozmow % 60;
            int minuty = lacznyCzasRozmow / 60;
            int godziny = minuty / 60;
            minuty = minuty % 60;
            String czasString = "";
            if (godziny > 0) {
                czasString = godziny + "h ";
            }
            if (minuty > 0) {
                czasString = czasString + minuty + "m ";
            }
            if (sekundy > 0) {
                czasString = czasString + sekundy + "s";
            }
            sb.append("\nŁączny czas rozmów: "+czasString + "\n");
/*
            DeviceName.with(getContext()).request(new DeviceName.Callback() {


                @Override public void onFinished(DeviceName.DeviceInfo info, Exception error) {
                    manufacturer = info.manufacturer;  // "Samsung"
                    name = info.marketName;            // "Galaxy S8+"
                    model = info.model;                // "SM-G955W"
                    codename = info.codename;          // "dream2qltecan"
                    deviceName = info.getName();       // "Galaxy S8+"
//                    sb.append("manufacturer = "+manufacturer);
//                    sb.append("name = "+name);
//                    sb.append("model = "+model);
//                    sb.append("codename = "+codename);
//                    sb.append("deviceName = "+deviceName);
//                    sb.toString();
//                    Log.d("DeviceName_WOTU","manufacturer "+manufacturer);
                    Log.d("DeviceName_WOTU","name "+name);
                    Log.d("DeviceName_WOTU","model "+model);
                    Log.d("DeviceName_WOTU","codename "+codename);
                    Log.d("DeviceName_WOTU","deviceName "+deviceName);

//                    Toast.makeText(getActivity(),"manufactu"manufacturer,Toast.LENGTH_SHORT).show();
                    // FYI: We are on the UI thread.
                }
            });
*/

//            String model = Build.MODEL;


            return sb.toString();

        } else {
            return "Brak uprawnień";
        }


    }

    private Cursor getCursor() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            Cursor managedCursor = getContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
            String[] details = new String[]{CallLog.Calls.NUMBER,                   //0
                    CallLog.Calls.TYPE,                                             //1
                    CallLog.Calls.DURATION,                                         //2
                    CallLog.Calls.CACHED_NAME,                                      //3
                    CallLog.Calls._ID,                                              //4
                    CallLog.Calls.DATE,                                             //5
                    CallLog.Calls.CACHED_FORMATTED_NUMBER, //570 082 100            //6
                    CallLog.Calls.COUNTRY_ISO, //PL                                 //7
                    CallLog.Calls.LAST_MODIFIED, //1582571365480                    //8
                    CallLog.Calls.NEW //0                                           //9
            };

            Cursor cursor;
            cursor = getContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, details, null, null, CallLog.Calls._ID + " DESC");
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
            }
            return cursor;
        }else {
            return null;
        }
    }

    private boolean isNullOrEmpty(String string) {
        if ((string==null)||(string.equals(""))){
            return true;
        }else{
            return false;
        }
    }

}
