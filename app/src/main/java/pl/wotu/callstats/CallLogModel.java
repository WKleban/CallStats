package pl.wotu.callstats;

import java.util.Date;

class CallLogModel {
    private String phoneAccountId;
    private String phoneNumber;
    private int type;
    private int duration;
    private String cachedName;
    private int callID;
    private Date callDate;
    private String cachedFormattedNumber;
    private String countryISO;
    private Date lastModified;
    private boolean isNew;
    private String androidId;
    private String phoneModel;
    private boolean isDownloadedFromDatabase = false;

    public CallLogModel() {
    }


    //    CallLogModel callLogEntry = new CallLogModel(phoneNumber, type, duration, cachedName, callID, callDate, cachedFormattedNumber, countryISO, lastModified, isNew ,phoneAccountId, androidId, phoneModel);
    public CallLogModel(String phoneNumber, int type, int duration, String cachedName, int callID, Date callDate, String cachedFormattedNumber, String countryISO, Date lastModified, boolean isNew, String phoneAccountId, String androidId, String phoneModel) {
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
    }
    //    CallLogModel callLogEntry = new CallLogModel(phoneNumber, type, duration, cachedName, callID, callDate, cachedFormattedNumber, countryISO, lastModified, isNew ,phoneAccountId, androidId, phoneModel);
    public CallLogModel(String phoneNumber, int type, int duration, String cachedName, int callID, Date callDate, String cachedFormattedNumber, String countryISO, Date lastModified, boolean isNew, String phoneAccountId, String androidId, String phoneModel,boolean isDownloadedFromDatabase) {
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
        this.isDownloadedFromDatabase = isDownloadedFromDatabase;
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
}