package pl.wotu.callstats;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.google.common.base.Strings.isNullOrEmpty;

public class MainActivity extends AppCompatActivity {

    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION =2084;
    private Toolbar mToolbar;
//    private NavController navController;
    private NavController mNavController;
    private BottomNavigationView mBottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBottomNavigation = findViewById(R.id.main_bottom_nav);
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Call Log App");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


//        initBottomNavigation(mNavigation);

//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(mNavController.getGraph()).build();

        // Toolbar
        NavigationUI.setupWithNavController(mToolbar, mNavController, appBarConfiguration);

        //BottomNavigation
        NavigationUI.setupWithNavController(mBottomNavigation,mNavController);



        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            initializeView();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        switch (item.getItemId()) {
            case R.id.cloud_upload_menuitem:
//
                sendListToFirebase();



                return true;

            default:
                return false;
        }
    }

    private Cursor getCursor() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            Cursor managedCursor = getApplicationContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
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
            cursor = getApplicationContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, details, null, null, CallLog.Calls._ID + " DESC");
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
            }
            return cursor;
        }else {
            return null;
        }
    }

    private void sendListToFirebase() {
        List<CallLogModel> listOfLastCallsPerUser = new ArrayList<>();
        Map<String, CallLogUserStatsModel> callLogModelUserCallsModelHashMap = new HashMap<>();
        Set<String> callers = new TreeSet<>();
        Cursor cursor = getCursor();
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
            if (isNullOrEmpty(cachedFormattedNumber)){cachedFormattedNumber="Numer prywatny";}
            if (isNullOrEmpty(cachedName)){cachedName = cachedFormattedNumber;}

//            CallLogModel callLogEntry = new CallLogModel(phoneNumber, type, duration, cachedName, callID, callDate, cachedFormattedNumber, countryISO, lastModified, isNew ,phoneAccountId, androidId, phoneModel);
//            CallLogUserStatsModel callLogUserSummary = new CallLogUserStatsModel();
//            listOfAllCalls.add(callLogEntry);

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
        Toast.makeText(MainActivity.this,"Tu będzie wysyłanie ["+callers.size()+"] dzwoniących",Toast.LENGTH_LONG).show();
//        for (CallLogModel caller:listOfLastCallsPerUser)
//            caller.ge
//
//        }

    }

    private void initializeView() {
            startService(new Intent(MainActivity.this, CallHandleService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

            for(int i = 0; i < menu.size(); i++){
                Drawable drawable = menu.getItem(i).getIcon();
                if(drawable != null) {
                    drawable.mutate();
                    drawable.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                }
            }


        return true;
    }

}
