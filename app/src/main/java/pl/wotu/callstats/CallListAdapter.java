package pl.wotu.callstats;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.CallLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CallListAdapter extends RecyclerView.Adapter<CallListAdapter.CallLogViewHolder> {

    private static final String TAG = "CallListAdapter";

    private final Activity activity;
    private final List<CallLogModel> listOfEntries;

    public CallListAdapter(Activity activity, List<CallLogModel> listOfEntries) {
        this.activity = activity;
        this.listOfEntries = listOfEntries;
    }

    @NonNull
    @Override
    public CallLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_local_call_log_item, parent, false);
        return new CallListAdapter.CallLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallLogViewHolder holder, int position) {

        String phoneNumber = listOfEntries.get(position).getPhoneNumber();
        String cachedFormattedNumber = listOfEntries.get(position).getCachedFormattedNumber();
        String cachedName = listOfEntries.get(position).getCachedName();
        int lastCallType = listOfEntries.get(position).getType();
        int durationOfTheWhole = listOfEntries.get(position).getDurationOfTheWhole();
        int duration = listOfEntries.get(position).getDuration();
        int numberOfCalls = listOfEntries.get(position).getNumberOfCalls();

//        int lastCallDuration = listOfEntries.get(position).getLastCallDuration();

        int lastCallID = listOfEntries.get(position).getCallID();
        Date lastCallDate = listOfEntries.get(position).getCallDate();
        String countryISO = listOfEntries.get(position).getCountryISO();
        String phoneModel = listOfEntries.get(position).getPhoneModel();
        String phoneAccountId = listOfEntries.get(position).getAndroidId();
        String androidId = listOfEntries.get(position).getAndroidId();
        String cachedNameFromDatabase = listOfEntries.get(position).getCachedName();
        boolean isDownloadedFromDatabase = listOfEntries.get(position).isDownloadedFromDatabase();

        String dateString = TimeAgo.callDateFormatter(lastCallDate);

        holder.callDate_tv.setText(dateString);
        holder.callDate_tv.setTextColor(ContextCompat.getColor(holder.type_iv.getContext(), R.color.grey_800));

        if (!isNullOrEmpty(cachedFormattedNumber) && !isNullOrEmpty(cachedFormattedNumber)) {
            holder.phoneNumber_tv.setVisibility(View.VISIBLE);
//            if (!cachedFormattedNumber.equals(cachedName)) {
            if (!isNullOrEmpty(cachedName)) {
                holder.name_tv.setText(cachedName);
//                holder.name_tv.setTextColor(ContextCompat.getColor(holder.type_iv.getContext(), R.color.grey_800));
                holder.phoneNumber_tv.setText(cachedFormattedNumber);
                holder.phoneNumber_tv.setTextColor(ContextCompat.getColor(holder.type_iv.getContext(), R.color.grey_800));
                holder.phoneNumber_tv.setVisibility(View.VISIBLE);

                if (isDownloadedFromDatabase){
                    holder.name_tv.setTextColor(ContextCompat.getColor(holder.type_iv.getContext(), R.color.blue_800));
                }else {
                    holder.name_tv.setTextColor(ContextCompat.getColor(holder.type_iv.getContext(), R.color.grey_800));
                }
            } else {
                holder.name_tv.setText(cachedFormattedNumber);
                holder.name_tv.setTextColor(ContextCompat.getColor(holder.type_iv.getContext(), R.color.grey_800));
                holder.phoneNumber_tv.setVisibility(View.GONE);
            }
        } else {
            holder.name_tv.setText("Numer prywatny");
            holder.name_tv.setTextColor(ContextCompat.getColor(holder.type_iv.getContext(), R.color.red_500));

            holder.phoneNumber_tv.setVisibility(View.GONE);
        }

        holder.single_local_last_call_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = holder.single_local_last_call_layout.getContext();
                int numberOfSimCards = CallAppConfig.getNumberOfSimCards(activity, context);
                if (numberOfSimCards == 0) {
                    //BRAK KART
                    Toast.makeText(context, "Brak dostępnych kart SIM :(", Toast.LENGTH_SHORT).show();

                } else if (numberOfSimCards == 1) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phoneNumber));
                    if (ActivityCompat.checkSelfPermission(holder.single_local_last_call_layout.getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        holder.single_local_last_call_layout.getContext().startActivity(callIntent);
                    } else {
                        Toast.makeText(holder.single_local_last_call_layout.getContext(), "Nie masz uprawnień do wykonywania połączeń\ntel:" + cachedFormattedNumber, Toast.LENGTH_LONG).show();
                    }
                } else if (numberOfSimCards == 2){
                    Toast.makeText(context,"W telefonie są 2 karty sim\njeszcze nie wiem co zrobić",Toast.LENGTH_LONG).show();
                }



            }
        });

        String durationString = getFormattedDuration(duration);
        String durationOfTheWholeString = getFormattedDuration(durationOfTheWhole);


        String callType = "";
        switch (lastCallType) {
            case CallLog.Calls.OUTGOING_TYPE:
                holder.type_iv.setImageResource(R.drawable.ic_call_made_black_24dp);
                if (!isNullOrEmpty(durationString)) {
                    callType = "Wychodzące";
                    holder.type_iv.setColorFilter(ContextCompat.getColor(holder.type_iv.getContext(), R.color.green_500));
                } else {
                    callType = "Anulowane";
                    holder.type_iv.setColorFilter(ContextCompat.getColor(holder.type_iv.getContext(), R.color.red_500));
                }
                ;
                break;

            case CallLog.Calls.INCOMING_TYPE:
                holder.type_iv.setImageResource(R.drawable.ic_call_received_black_24dp);
                callType = "Przychodzące";
                if (!isNullOrEmpty(durationString)) {
                    holder.type_iv.setColorFilter(ContextCompat.getColor(holder.type_iv.getContext(), R.color.blue_500));
                } else {
                    holder.type_iv.setColorFilter(ContextCompat.getColor(holder.type_iv.getContext(), R.color.red_500));
                }
                break;
            case CallLog.Calls.MISSED_TYPE:
                holder.type_iv.setImageResource(R.drawable.ic_call_missed_black_24dp);
                holder.type_iv.setColorFilter(ContextCompat.getColor(holder.type_iv.getContext(), R.color.red_500));
                callType = "Nieodebrane";
                break;
            case CallLog.Calls.BLOCKED_TYPE:
                holder.type_iv.setImageResource(R.drawable.ic_call_rejected_black_24dp);
                holder.type_iv.setColorFilter(ContextCompat.getColor(holder.type_iv.getContext(), R.color.red_500));
                callType = "Zablokowane";
                break;
            case CallLog.Calls.REJECTED_TYPE:
                holder.type_iv.setImageResource(R.drawable.ic_call_rejected_black_24dp);
                holder.type_iv.setColorFilter(ContextCompat.getColor(holder.type_iv.getContext(), R.color.red_500));
                callType = "Odrzucone";
                break;
        }

        if (!isNullOrEmpty(durationString)) {
            holder.last_call_duration_tv.setText(durationString);
//            holder.type_iv.setColorFilter(Color.BLACK);
            holder.last_call_duration_tv.setTextColor(ContextCompat.getColor(holder.type_iv.getContext(), R.color.green_500));
            holder.phone_icon.setColorFilter(ContextCompat.getColor(holder.type_iv.getContext(), R.color.green_500));
        } else {
            holder.last_call_duration_tv.setText(callType);
            holder.last_call_duration_tv.setTextColor(ContextCompat.getColor(holder.type_iv.getContext(), R.color.red_500));
            holder.phone_icon.setColorFilter(ContextCompat.getColor(holder.type_iv.getContext(), R.color.red_500));
        }
        if (!isNullOrEmpty(durationOfTheWholeString)) {
            holder.user_duration_tv.setText(durationOfTheWholeString);
            holder.user_duration_tv.setVisibility(View.VISIBLE);
        } else {
            holder.user_duration_tv.setVisibility(View.GONE);
        }

//        String totalUserCalls = "1";
        holder.single_local_counter.setText("" + numberOfCalls);



    }

    @Override
    public int getItemCount() {
        if(listOfEntries ==null){
            return 0;
        }else {
            return listOfEntries.size();
        }
    }

    public class CallLogViewHolder extends RecyclerView.ViewHolder {

        private TextView phoneNumber_tv,name_tv,user_duration_tv,last_call_duration_tv,callDate_tv,single_local_counter;
        private ImageView more_iv,type_iv,phone_icon;
        private ConstraintLayout single_local_last_call_layout,single_local_details_layout;

        public CallLogViewHolder(@NonNull View itemView) {
            super(itemView);
            phoneNumber_tv = itemView.findViewById(R.id.single_local_phone_number);
            type_iv = itemView.findViewById(R.id.single_local_type_iv);
            name_tv = itemView.findViewById(R.id.single_local_name);
            more_iv = itemView.findViewById(R.id.single_local_more_ib);
            user_duration_tv = itemView.findViewById(R.id.single_local_user_duration);
            phone_icon = itemView.findViewById(R.id.single_local_phone_icon_iv);
            last_call_duration_tv = itemView.findViewById(R.id.single_local_last_call_duration);
            callDate_tv = itemView.findViewById(R.id.single_local_call_date);
            single_local_last_call_layout = itemView.findViewById(R.id.single_local_last_call_layout);
            single_local_details_layout = itemView.findViewById(R.id.single_local_details_layout);

            single_local_counter = itemView.findViewById(R.id.single_local_counter);

        }
    }

    private boolean isNullOrEmpty(String string) {
        if ((string==null)||(string.equals(""))){
            return true;
        }else{
            return false;
        }
    }
    private String getFormattedDate(Date callDate) {
        Date currentTime;
        Date timeAgo = currentTime = callDate;
        long time = timeAgo.getTime();
        Log.d("timeAgo",time+"");
        System.out.println("timeAgo"+time);

//        currentTime.getTime();
//        callDate.getTime();
        System.out.println("currentTime.getTime()"+currentTime.getTime());
        System.out.println("callDate.callDate()"+callDate.getTime());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm:ss");

        return formatter.format(callDate);
//        return formatter2.format(timeAgo);
    }

    private String getFormattedDuration(int duration) {
        String resultString = "";
        int sekundy = duration % 60;
        int minuty = duration / 60;
        int godziny = minuty / 60;
        minuty = minuty % 60;
        if (godziny > 0) {
            resultString = godziny + "g ";
        }
        if (minuty > 0) {
            resultString = resultString + minuty + "m ";
        }
        if (sekundy > 0) {
            resultString = resultString + sekundy + "s";
        }
        return resultString;
    }

}
