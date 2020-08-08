package pl.wotu.callstats;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.CallLog;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.arch.core.internal.SafeIterableMap;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.widget.Toast.LENGTH_LONG;

class LocalCallListAdapter extends RecyclerView.Adapter<LocalCallListAdapter.LocalCallLogViewHolder> {

    private final Activity activity;
    private final List<CallLogModel> listOfEntries;
    //    private final FragmentActivity activity;
//    private final Set<String> callers;
//    private final Map<String, UserCallsSummaryModel> callsMap;
//    private final List<UserCallsSummaryModel> userCallsList;
    private Map<String, UserCallsSummaryModel> listOfCalls;

//    public LocalCallListAdapter(FragmentActivity activity, Set<String> callerNamesSet, List<UserCallsSummaryModel> userCallsList, Map<String, UserCallsSummaryModel> callsMap) {
//        this.activity = activity;
//        this.callers = callerNamesSet;
//        this.userCallsList = userCallsList;
//        this.callsMap = callsMap;
//    }

    public LocalCallListAdapter(FragmentActivity activity, List<CallLogModel> listOfEntries) {
        this.activity = activity;
        this.listOfEntries = listOfEntries;



    }

    @NonNull
    @Override
    public LocalCallLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_local_call_log_item, parent, false);
        return new LocalCallLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LocalCallLogViewHolder holder, int position) {
        String phoneNumber = listOfEntries.get(position).getPhoneNumber();
        String cachedFormattedNumber = listOfEntries.get(position).getCachedFormattedNumber();
        String cachedName = listOfEntries.get(position).getCachedName();
        int lastCallType = listOfEntries.get(position).getType();
        int duration = listOfEntries.get(position).getDuration();
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

        String durationString = getFormattedDuration(duration);
//        String durationOfUserString = getFormattedDuration(durationOfCalls);


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
//        if (!isNullOrEmpty(durationOfUserString)) {
//            holder.user_duration_tv.setText(durationOfUserString);
//            holder.user_duration_tv.setVisibility(View.VISIBLE);
//        } else {
//            holder.user_duration_tv.setVisibility(View.GONE);
//        }

        String totalUserCalls = "blablabla";
        holder.single_local_counter.setText("" + totalUserCalls);


//        holder.single_local_last_call_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Context context = holder.single_local_last_call_layout.getContext();
//                int numberOfSimCards = CallAppConfig.getNumberOfSimCards(mActivity, context);
//
//                if (numberOfSimCards == 0) {
//                    //BRAK KART
//                    Toast.makeText(context, "Brak dostępnych kart SIM :(", Toast.LENGTH_SHORT).show();
//
//                } else if (numberOfSimCards == 1) {
////                    callLogModelUserCallsModelHashMap.get("")fdsfds
//                    String text="Nie ma w bazie";
//                    if (callLogModelUserCallsModelHashMap.containsKey(cachedName)){
//                        text = callLogModelUserCallsModelHashMap.get(cachedName).getNameFromCloud();
//                    }
//
//                    Toast.makeText(holder.single_local_last_call_layout.getContext(), cachedName+"\nCloudName: "+text, Toast.LENGTH_SHORT).show();
//
////                } else if (numberOfSimCards == 1) {
////                    //1 KARTA
//////                    Toast.makeText(holder.single_local_last_call_layout.getContext(), "Lewa strona", Toast.LENGTH_LONG).show();
////                    Intent callIntent = new Intent(Intent.ACTION_CALL);
////                    callIntent.setData(Uri.parse("tel:" + phoneNumber));
////                    if (ActivityCompat.checkSelfPermission(holder.single_local_last_call_layout.getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
////                        holder.single_local_last_call_layout.getContext().startActivity(callIntent);
////                    } else {
////                        Toast.makeText(holder.single_local_last_call_layout.getContext(), "Nie masz uprawnień do dzwonienia\ntel:" + cachedFormattedNumber, Toast.LENGTH_LONG).show();
////                    }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//                }
////                else {
//////                    Toast.makeText(holder.single_local_last_call_layout.getContext(), "Lewa strona", Toast.LENGTH_LONG).show();
////                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
////                    callIntent.setData(Uri.parse("tel:"+holder.phoneNumber_tv.getText().toString()));
////                    if (ActivityCompat.checkSelfPermission(holder.single_local_last_call_layout.getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
////                        holder.single_local_last_call_layout.getContext().startActivity(callIntent);
////                    }else {
////                        Toast.makeText(holder.single_local_last_call_layout.getContext(), "Nie masz uprawnień do dzwonienia\ntel:"+cachedFormattedNumber, Toast.LENGTH_LONG).show();
////                    }
////
////                }
//                else {
//                    //2 KARTY
//                    Intent chooseCallMethodIntent = new Intent(mActivity, ChooseCallMethodActivity.class);
//                    chooseCallMethodIntent.putExtra("numberOfSimCards", numberOfSimCards);
//                    chooseCallMethodIntent.putExtra("phoneNumber", phoneNumber);
//                    chooseCallMethodIntent.putExtra("cachedName", cachedName);
//                    mActivity.startActivity(chooseCallMethodIntent);
//                }
//
//
//                /*
////                Toast.makeText(holder.single_local_last_call_layout.getContext(), "Lewa strona", Toast.LENGTH_LONG).show();
//                Intent callIntent = new Intent(Intent.ACTION_CALL);
//                callIntent.setData(Uri.parse("tel:"+holder.phoneNumber_tv.getText().toString()));
//                if (ActivityCompat.checkSelfPermission(holder.single_local_last_call_layout.getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
//                    holder.single_local_last_call_layout.getContext().startActivity(callIntent);
//                }else {
//                Toast.makeText(holder.single_local_last_call_layout.getContext(), "Nie masz uprawnień do dzwonienia\ntel:"+cachedFormattedNumber, Toast.LENGTH_LONG).show();
//                }
////                holder.single_local_last_call_layout.getContext().startActivity(callIntent);
//                */
//
//
//            }
//        });

//        holder.

//        holder.single_local_last_call_layout.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//
//                Context context = holder.single_local_details_layout.getContext();
//
//                Toast.makeText(context, "callID = " + callID + "\nandroidId = " + androidId + "\nphoneNumber = " + phoneNumber + "\ncachedName = " + cachedName + "\nSzczegóły w konsoli", Toast.LENGTH_LONG).show();
//                System.out.println("callID = " + callID);
//                System.out.println("androidId = " + androidId);
//                System.out.println("phoneNumber = " + phoneNumber);
//                System.out.println("type = " + type);
//                System.out.println("duration = " + duration);
//                System.out.println("cachedName = " + cachedName);
//                System.out.println("callDate = " + callDate.toString());
//                System.out.println("cachedFormattedNumber = " + cachedFormattedNumber);
//                System.out.println("countryISO = " + countryISO);
//                System.out.println("lastModified = " + lastModified);
//                System.out.println("isNew = " + isNew);
//                System.out.println("phoneModel = " + phoneModel);
//
//                final SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
//                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                }
//                final List<SubscriptionInfo> activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
//
//                int simCount = activeSubscriptionInfoList.size();
//                int cardSlot = 0;
//                if(simCount==1){
//                    System.out.println("phoneModel = " + phoneModel);
//                    System.out.println("getSimSlotIndex "+activeSubscriptionInfoList.get(cardSlot).getSimSlotIndex());
//                    System.out.println("getIccId "+activeSubscriptionInfoList.get(cardSlot).getIccId());
//                    System.out.println("getCarrierName "+activeSubscriptionInfoList.get(cardSlot).getCarrierName());
//                }
//
//                Map<String,Object> callMap = new HashMap<>();
//                callMap.put("timestamp", FieldValue.serverTimestamp());
//                callMap.put("callID", callID);
//                callMap.put("androidId" , androidId);
//                callMap.put("phoneNumber" , phoneNumber);
//                callMap.put("type" ,type);
//                callMap.put("duration" , duration);
//                callMap.put("cachedName", cachedName);
//                callMap.put("callDate" , callDate);
//                callMap.put("cachedFormattedNumber" , cachedFormattedNumber);
//                callMap.put("countryISO", countryISO);
//                callMap.put("lastModified" , lastModified);
//                callMap.put("isNew" ,isNew);
//                callMap.put("phoneModel", phoneModel);
//
//                callMap.put("getSimSlotIndex", activeSubscriptionInfoList.get(cardSlot).getSimSlotIndex());
//                callMap.put("getIccId", activeSubscriptionInfoList.get(cardSlot).getIccId());
//                callMap.put("getCarrierName", activeSubscriptionInfoList.get(cardSlot).getCarrierName());
//
//
//                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
//                FirebaseAuth mAuth = FirebaseAuth.getInstance();
//                String userID = mAuth.getCurrentUser().getUid();
//                firebaseFirestore
//                        .collection("Users")
//                        .document(userID)
//                        .collection("SimCards")
//                        .document(activeSubscriptionInfoList.get(cardSlot).getIccId())
//                        .collection("Calls")
//                        .add(callMap)
//                        .addOnCompleteListener(task -> {
//                            if (task.isSuccessful()){
//                                Toast.makeText(context, "Zapisano rozmowę :)",LENGTH_LONG).show();
//                                Map<String,Object> userMap = new HashMap<>();
//                                if (!cachedFormattedNumber.equals(cachedName)) {
//                                    userMap.put("cachedName", cachedName);
//                                }
//                                userMap.put("phoneNumber" , phoneNumber);
//                                userMap.put("cachedFormattedNumber" , cachedFormattedNumber);
//                                userMap.put("lastCallDate" , callDate);
//                                userMap.put("timestamp", FieldValue.serverTimestamp());
//
//                                firebaseFirestore
//                                        .collection("Users")
//                                        .document("testUser")
//                                        .collection("Contacts")
//                                        .document(phoneNumber)
//                                        .set(userMap)
//                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if(task.isSuccessful()){
//                                                    Toast.makeText(context, "Zapisano rozmówcę :)",LENGTH_LONG).show();
//                                                } else {
//                                                    Toast.makeText(context, task.getException().toString(),LENGTH_LONG).show();
//                                                }
//                                            }
//                                        });
//
////                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
////                                            @Override
////                                            public void onComplete(@NonNull Task<DocumentReference> task) {
////
////                                                if (task.isSuccessful()) {
////                                                    Toast.makeText(holder.single_local_details_layout.getContext(), "Zapisano rozmówcę :)",LENGTH_LONG).show();
////                                                } else {
////                                                    Toast.makeText(holder.single_local_details_layout.getContext(), task.getException().toString(),LENGTH_LONG).show();
////                                                }
////                                            }
////                                        });
//
//
//
//
//
//                            } else {
//                                Toast.makeText(holder.single_local_details_layout.getContext(), task.getException().toString(),LENGTH_LONG).show();
//                            }
//                        });
//
//                return true;
//            }
//        });

//        holder.single_local_details_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(holder.single_local_details_layout.getContext(),"callID = "+callID+"\nandroidId = "+androidId+"\nphoneNumber = "+phoneNumber+"\ncachedName = "+cachedName+"\nSzczegóły w konsoli",Toast.LENGTH_LONG).show();
//
////                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
////                FirebaseAuth mAuth = FirebaseAuth.getInstance();
////                String userID = mAuth.getCurrentUser().getUid();
////
////                firebaseFirestore.collection("Users").document(userID).collection("Categories").orderBy("name", Query.Direction.ASCENDING).addSnapshotListener((queryDocumentSnapshots, e) {
////
////                }
//
//
//
//
//
//
//
//
//            }
//        });


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

    private boolean isNullOrEmpty(String string) {
        if ((string==null)||(string.equals(""))){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public int getItemCount() {
        if(listOfCalls ==null){
            return 0;
        }else {
            return listOfCalls.size();
        }

    }

    public class LocalCallLogViewHolder extends RecyclerView.ViewHolder{

            private TextView phoneNumber_tv,name_tv,user_duration_tv,last_call_duration_tv,callDate_tv,single_local_counter;
            private ImageView more_iv,type_iv,phone_icon;
            private ConstraintLayout single_local_last_call_layout,single_local_details_layout;

        public LocalCallLogViewHolder(@NonNull View itemView) {
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

}
