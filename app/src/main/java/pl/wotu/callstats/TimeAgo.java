package pl.wotu.callstats;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeAgo {

//    public static String getTimeAgo(long duration){
//        Date now = new Date();
//        long nowPoint = now.getTime();
//
//        long seconds = TimeUnit.MILLISECONDS.toSeconds(nowPoint - duration);
//        long minutes =  TimeUnit.MILLISECONDS.toMinutes(nowPoint - duration);
//        long hours =  TimeUnit.MILLISECONDS.toHours(nowPoint - duration);
//        long days =  TimeUnit.MILLISECONDS.toDays(nowPoint - duration);
//
//        if(seconds<60){
//            return "przed chwilą";
//        }else if(minutes==1){
//            return "minutę temu";
//        }else if(minutes>1&&minutes<5){
//            return minutes + " minuty temu";
//        }else if(minutes>=5&&minutes<60){
//            return minutes + " minut temu";
//        }else if(hours==1){
//            return "godzinę temu";
//        }else if((hours>1&&hours<5)||(hours>21&&hours<24)){
//            return minutes + " godziny temu";
//        }else if(hours>=5&&hours<=21){
//            return hours + " godzin temu";
//        }else if(days==1){
//            return "wczoraj";
//        }else{
//            return days+" dni temu";
//        }
//    }

    public static String callDateFormatter(Date callDate){
        long duration = callDate.getTime();
        Date nowDate = new Date();
        long now = nowDate.getTime();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");


        long seconds = TimeUnit.MILLISECONDS.toSeconds(now - duration);
        long minutes =  TimeUnit.MILLISECONDS.toMinutes(now - duration);
        long hours =  TimeUnit.MILLISECONDS.toHours(now - duration);
        long days =  TimeUnit.MILLISECONDS.toDays(now - duration);
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

