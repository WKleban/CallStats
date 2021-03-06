package pl.wotu.callstats;

import android.text.format.DateFormat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

class CallLogModel {
    private static final String TAG = "CallLogModel";
    private String phoneAccountId;
    private String phoneNumber;
    private int type;
    private int duration;
    private String cachedName;
    private int callID;
    private Date callDate;
    private Date lastActivityDate;
    private String cachedFormattedNumber;
    private String countryISO;
    private Date lastModified;
    private boolean isNew;
    private String androidId;
    private String phoneModel;
    private boolean isDownloadedFromDatabase = false;

    private int durationOfTheWhole;
    private int numberOfCalls;

    private SmsModel lastSms;


    public CallLogModel() {
    }

    public CallLogModel(SmsModel lastSms, String cachedName) {
        this.lastSms = lastSms;
        this.phoneNumber = lastSms.get_address();
        this.cachedName = cachedName;
    }

    public CallLogModel(SmsModel lastSms,String phoneNumber,String cachedName) {
        this.lastSms = lastSms;
        this.phoneNumber = phoneNumber;
        this.cachedName = cachedName;
    }

    //    CallLogModel callLogEntry = new CallLogModel(phoneNumber, type, duration, cachedName, callID, callDate, cachedFormattedNumber, countryISO, lastModified, isNew ,phoneAccountId, androidId, phoneModel);
    public CallLogModel(String phoneNumber, int type, int duration, String cachedName, int callID, Date callDate, String cachedFormattedNumber, String countryISO, Date lastModified, boolean isNew, String phoneAccountId, String androidId, String phoneModel,int durationOfTheWhole,int numberOfCalls) {
        this.phoneNumber = phoneNumber;
        this.type = type;
        this.duration = duration;
        this.cachedName = cachedName;
        this.callID = callID;
        this.callDate = callDate;
        this.cachedFormattedNumber = cachedFormattedNumber;
        this.countryISO = countryISO;
        this.lastModified = lastModified;
        this.isNew = isNew;
        this.androidId = androidId;
        this.phoneModel = phoneModel;
        this.phoneAccountId = phoneAccountId;
        this.isDownloadedFromDatabase = false;
        this.durationOfTheWhole = durationOfTheWhole;
        this.numberOfCalls = numberOfCalls;
        this.lastSms = null;

        this.lastActivityDate = callDate;
    }

    public CallLogModel(String phoneNumber, int type, int duration, String cachedName, int callID, Date callDate, String cachedFormattedNumber, String countryISO, Date lastModified, boolean isNew, String phoneAccountId, String androidId, String phoneModel,int durationOfTheWhole,int numberOfCalls,SmsModel lastSms) {
        this.phoneNumber = phoneNumber;
        this.type = type;
        this.duration = duration;
        this.cachedName = cachedName;
        this.callID = callID;
        this.callDate = callDate;
        this.cachedFormattedNumber = cachedFormattedNumber;
        this.countryISO = countryISO;
        this.lastModified = lastModified;
        this.isNew = isNew;
        this.androidId = androidId;
        this.phoneModel = phoneModel;
        this.phoneAccountId = phoneAccountId;
        this.isDownloadedFromDatabase = false;
        this.durationOfTheWhole = durationOfTheWhole;
        this.numberOfCalls = numberOfCalls;
        this.lastSms = lastSms;

        if ((lastSms.get_time()!=null)&&(callDate!=null)){
            updateLastActivityDate();
//            long lastSmsLong = Long.parseLong(lastSms.get_time());
//            long callDateLong = callDate.getTime();
//            if (lastSmsLong>callDateLong){
//                this.lastActivityDate.setTime(Long.parseLong(lastSms.get_time()));
//            }else {
//                this.lastActivityDate = callDate;
//            }
        }else if((lastSms.get_time()==null)&&(callDate!=null)){
            this.lastActivityDate = callDate;

        }else if((lastSms.get_time()!=null)&&(callDate==null)){
            this.lastActivityDate.setTime(Long.parseLong(lastSms.get_time()));
        }else{//null z obu stron
            this.lastActivityDate = null;
        }



    }

    public String getPhoneAccountId() {
        return phoneAccountId;
    }

    public void setPhoneAccountId(String phoneAccountId) {
        this.phoneAccountId = phoneAccountId;
    }

    public int getDurationOfTheWhole() {
        return durationOfTheWhole;
    }

    public void setDurationOfTheWhole(int durationOfTheWhole) {
        this.durationOfTheWhole = durationOfTheWhole;
    }

    public int getNumberOfCalls() {
        return numberOfCalls;
    }

    public void setNumberOfCalls(int numberOfCalls) {
        this.numberOfCalls = numberOfCalls;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getCachedName() {
        return cachedName;
    }

    public void setCachedName(String cachedName) {
        this.cachedName = cachedName;
    }

    public int getCallID() {
        return callID;
    }

    public void setCallID(int callID) {
        this.callID = callID;
    }

    public Date getCallDate() {
        return callDate;
    }

    public void setCallDate(Date callDate) {
        this.callDate = callDate;
        updateLastActivityDate();


    }

    private void updateLastActivityDate() {
        if (callDate!=null){
            long lastSmsLong = Long.parseLong(lastSms.get_time());
            long callDateLong = callDate.getTime();
            if (lastSmsLong>callDateLong){
                Log.d(TAG, "updateLastActivityDate: Porównywanie "+lastSmsLong+" > "+Long.parseLong(lastSms.get_time()));
//                System.out.println();
                this.lastActivityDate.setTime(Long.parseLong(lastSms.get_time()));
            }else if (lastSmsLong<callDateLong) {
//                System.out.println("Porównywanie "+lastSmsLong+" < "+Long.parseLong(lastSms.get_time()));

                Log.d(TAG, "updateLastActivityDate: Porównywanie "+lastSmsLong+" > "+Long.parseLong(lastSms.get_time()));
                this.lastActivityDate = callDate;
            }
            else {
//                System.out.println("Porównywanie "+lastSmsLong+" = "+Long.parseLong(lastSms.get_time()));

                Log.d(TAG, "updateLastActivityDate: Porównywanie "+lastSmsLong+" > "+Long.parseLong(lastSms.get_time()));
            }
        }
    }

    public String getCachedFormattedNumber() {
        return cachedFormattedNumber;
    }

    public void setCachedFormattedNumber(String cachedFormattedNumber) {
        this.cachedFormattedNumber = cachedFormattedNumber;
    }

    public String getCountryISO() {
        return countryISO;
    }

    public void setCountryISO(String countryISO) {
        this.countryISO = countryISO;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }


    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public boolean isDownloadedFromDatabase() {
        return isDownloadedFromDatabase;
    }

    public void setDownloadedFromDatabase(boolean downloadedFromDatabase) {
        isDownloadedFromDatabase = downloadedFromDatabase;
    }

    public SmsModel getLastSms() {
        return lastSms;
    }

    public void setLastSms(SmsModel lastSms) {
        this.lastSms = lastSms;

        updateLastActivityDate();
    }
}