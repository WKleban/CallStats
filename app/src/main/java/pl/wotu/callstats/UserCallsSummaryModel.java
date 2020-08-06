package pl.wotu.callstats;

import java.util.Date;

class UserCallsSummaryModel {


    private String phoneNumber;
    private String cachedFormattedNumber;
    private String cachedName;
    private int lastCalltype;
    private int lastCallID;
    private Date lastCallDate;
    private int durationOfCalls;
    private String countryISO;
    private String phoneModel;
    private String phoneAccountId;
    private String androidId;
    private int lastCallDuration;

    private String cachedNameFromDatabase;
    private boolean isDownloadedFromDatabase;


    public UserCallsSummaryModel() {
    }

    public UserCallsSummaryModel(String phoneNumber,
                                 String cachedFormattedNumber,
                                 String cachedName,
                                 int lastCalltype,
                                 int durationOfCalls,
                                 int lastCallDuration,
                                 int lastCallID,
                                 Date lastCallDate,
                                 String countryISO,
                                 String phoneAccountId,
                                 String androidId,
                                 String phoneModel){
        this.phoneNumber = phoneNumber;
        this.cachedFormattedNumber = cachedFormattedNumber;
        this.cachedName = cachedName;
        this.lastCalltype = lastCalltype;
        this.durationOfCalls = durationOfCalls;
        this.lastCallID = lastCallID;
        this.lastCallDate = lastCallDate;
        this.countryISO = countryISO;
        this.phoneAccountId = phoneAccountId;
        this.androidId = androidId;
        this.phoneModel = phoneModel;
        this.lastCallDuration = lastCallDuration;

        this.cachedNameFromDatabase = "";
        this.isDownloadedFromDatabase = false;

    }

    public void updateFromDatabase(boolean isDownloadedFromDatabase,String cachedNameFromDatabase){
        this.cachedNameFromDatabase =cachedNameFromDatabase;
        this.isDownloadedFromDatabase = isDownloadedFromDatabase;
    }

    public void updateSummaryInfo(int lastCalltype, int durationOfCalls, int lastCallID, Date lastCallDate){
        this.lastCalltype = lastCalltype;
        this.durationOfCalls = durationOfCalls;
        this.lastCallID = lastCallID;
        this.lastCallDate = lastCallDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCachedFormattedNumber() {
        return cachedFormattedNumber;
    }

    public void setCachedFormattedNumber(String cachedFormattedNumber) {
        this.cachedFormattedNumber = cachedFormattedNumber;
    }

    public String getCachedName() {
        return cachedName;
    }

    public void setCachedName(String cachedName) {
        this.cachedName = cachedName;
    }

    public int getLastCalltype() {
        return lastCalltype;
    }

    public void setLastCalltype(int lastCalltype) {
        this.lastCalltype = lastCalltype;
    }

    public int getDurationOfCalls() {
        return durationOfCalls;
    }

    public void setDurationOfCalls(int durationOfCalls) {
        this.durationOfCalls = durationOfCalls;
    }

    public int getLastCallID() {
        return lastCallID;
    }

    public void setLastCallID(int lastCallID) {
        this.lastCallID = lastCallID;
    }

    public Date getLastCallDate() {
        return lastCallDate;
    }

    public void setLastCallDate(Date lastCallDate) {
        this.lastCallDate = lastCallDate;
    }

    public String getCountryISO() {
        return countryISO;
    }

    public void setCountryISO(String countryISO) {
        this.countryISO = countryISO;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public String getPhoneAccountId() {
        return phoneAccountId;
    }

    public void setPhoneAccountId(String phoneAccountId) {
        this.phoneAccountId = phoneAccountId;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getCachedNameFromDatabase() {
        return cachedNameFromDatabase;
    }

    public void setCachedNameFromDatabase(String cachedNameFromDatabase) {
        this.cachedNameFromDatabase = cachedNameFromDatabase;
    }

    public boolean isDownloadedFromDatabase() {
        return isDownloadedFromDatabase;
    }

    public void setDownloadedFromDatabase(boolean downloadedFromDatabase) {
        isDownloadedFromDatabase = downloadedFromDatabase;
    }

    public int getLastCallDuration() {
        return lastCallDuration;
    }

    public void setLastCallDuration(int lastCallDuration) {
        this.lastCallDuration = lastCallDuration;
    }
}