package pl.wotu.callstats;

class CallLogUserStatsModel {
    private int duration;
    private int count;
    private String nameFromCloud;
    private boolean isDownloadedFromCloud;


    public CallLogUserStatsModel() {

    }


    public CallLogUserStatsModel(int duration, int count) {
        this.duration = duration;
        this.count = count;
        this.nameFromCloud = "";
        this.isDownloadedFromCloud = false;
    }


    public CallLogUserStatsModel(int duration, int count,String nameFromCloud,boolean isDownloadedFromCloud) {
        this.duration = duration;
        this.count = count;
        this.nameFromCloud = nameFromCloud;
        this.isDownloadedFromCloud = isDownloadedFromCloud;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNameFromCloud() {
        return nameFromCloud;
    }

    public void setNameFromCloud(String nameFromCloud) {
        this.nameFromCloud = nameFromCloud;
    }

    public boolean getDownloadedFromCloud() {
        return isDownloadedFromCloud;
    }

    public void setDownloadedFromCloud(boolean downloadedFromCloud) {
        isDownloadedFromCloud = downloadedFromCloud;
    }
}
