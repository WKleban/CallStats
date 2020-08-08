package pl.wotu.callstats;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeAgo {

    public static String callDateFormatter(Date callDate){
        long duration = callDate.getTime();
        Date nowDate = new Date();
        long now = nowDate.getTime();
        String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
        String timeFormat = "HH:mm:ss";
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateTimeFormat);
        SimpleDateFormat timeFormatter = new SimpleDateFormat(timeFormat);

//        long seconds = TimeUnit.MILLISECONDS.toSeconds(now) - TimeUnit.MILLISECONDS.toSeconds(duration);
        long minutes =  TimeUnit.MILLISECONDS.toMinutes(now) -  TimeUnit.MILLISECONDS.toMinutes(duration);
//        long hours =  TimeUnit.MILLISECONDS.toHours(now) -  TimeUnit.MILLISECONDS.toHours(duration);
        long days =  TimeUnit.MILLISECONDS.toDays(now) -  TimeUnit.MILLISECONDS.toDays(duration);
        String minsAgo = "";
        if (minutes<60){
            switch ((int) minutes){
                case 0:
                    minsAgo = "Przed chwilą";
                    break;
                case 1:
                    minsAgo = "Minutę temu";
                    break;
                case 2:
                case 3:
                case 4:
                case 22:
                case 23:
                case 24:
                case 32:
                case 33:
                case 34:
                case 42:
                case 43:
                case 44:
                case 52:
                case 53:
                case 54:
                    minsAgo = "minuty temu";
                    break;
                default:
                    minsAgo = "minut temu";
            }
            if (minutes>1){
                minsAgo = minutes +" "+ minsAgo;
            }
            return minsAgo;
        }
        else if(days==0){
            return "Dzisiaj o "+timeFormatter.format(callDate);
        }else if (days==1) {
                return "Wczoraj o "+timeFormatter.format(callDate);
        }else{
            return dateFormatter.format(callDate);
        }
    }

}

