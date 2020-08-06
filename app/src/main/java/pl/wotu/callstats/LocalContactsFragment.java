package pl.wotu.callstats;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class LocalContactsFragment extends Fragment {

    private FragmentActivity activity;
    private Context context;

    private NavController navController;

    private RecyclerView recyclerView;

    private List<Object> listOfContacts;
    private boolean isReadyListOfCalls=false;
    private LocalContactsAdapter adapter;
    private String TAG = "LocalContactsFragment_Wotu";


    public LocalContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        getContactsCursor(); // TYMCZASOWO
        recyclerView = view.findViewById(R.id.local_contacts_rv);

        listOfContacts = new ArrayList<>();

        if (!isReadyListOfCalls) {

            setCallLogToList(getCursor());
        }
        adapter = new LocalContactsAdapter(getActivity(),listOfContacts);



    }
    private Cursor getContactsCursor() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            Cursor managedCursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            String[] contactDetails = new String[]{

//                    ContactsContract.CommonDataKinds.Phone.DI
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Organization.DATA,
                    ContactsContract.CommonDataKinds.Organization.DATA1,
                    ContactsContract.CommonDataKinds.Organization.DATA2,
                    ContactsContract.CommonDataKinds.Organization.DATA3,
                    ContactsContract.CommonDataKinds.Organization.DATA4,
                    ContactsContract.CommonDataKinds.Organization.DATA5,
                    ContactsContract.CommonDataKinds.Organization.DATA6,
                    ContactsContract.CommonDataKinds.Organization.DATA7,
                    ContactsContract.CommonDataKinds.Organization.DATA8,
                    ContactsContract.CommonDataKinds.Organization.DATA9,
                    ContactsContract.CommonDataKinds.Organization.DATA10,
                    ContactsContract.CommonDataKinds.Organization.DATA11,
                    ContactsContract.CommonDataKinds.Organization.DATA12,
                    ContactsContract.CommonDataKinds.Organization.DATA13,
                    ContactsContract.CommonDataKinds.Organization.DATA14,
                    ContactsContract.CommonDataKinds.Organization.DATA15,
//                    ContactsContract.CommonDataKinds.Nickname.

//                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
//                    ContactsContract.CommonDataKinds.Phone.CONTACT_STATUS,
//                    ContactsContract.CommonDataKinds.Callable.DATA,
//                    ContactsContract.CommonDataKinds.Contactables.DATA,
//                    ContactsContract.CommonDataKinds.Email.DATA,
//                    ContactsContract.CommonDataKinds.Event.DATA,
//                    ContactsContract.CommonDataKinds.GroupMembership.DATA1,
//                    ContactsContract.CommonDataKinds.Nickname.DATA,
//                    ContactsContract.CommonDataKinds.Note.DATA1,
//                    ContactsContract.CommonDataKinds.Organization.DATA,
//                    ContactsContract.CommonDataKinds.SipAddress.DATA,
//                    ContactsContract.CommonDataKinds.StructuredName.DATA1,
//                    ContactsContract.CommonDataKinds.StructuredPostal.DATA,
//                    ContactsContract.CommonDataKinds.Website.DATA

//                    ContactsContract.CommonDataKinds.Phone.DATA,
//                    ContactsContract.CommonDataKinds.Phone.DATA1,
//                    ContactsContract.CommonDataKinds.Phone.DATA2,
//                    ContactsContract.CommonDataKinds.Phone.DATA3,
//                    ContactsContract.CommonDataKinds.Phone.DATA4,
//                    ContactsContract.CommonDataKinds.Phone.DATA5,
//                    ContactsContract.CommonDataKinds.Phone.DATA6,
//                    ContactsContract.CommonDataKinds.Phone.DATA7,
//                    ContactsContract.CommonDataKinds.Phone.DATA8,
//                    ContactsContract.CommonDataKinds.Phone.DATA9,
//                    ContactsContract.CommonDataKinds.Phone.DATA10,
//                    ContactsContract.CommonDataKinds.Phone.DATA11,
//                    ContactsContract.CommonDataKinds.Phone.DATA12,
//                    ContactsContract.CommonDataKinds.Phone.DATA13,
//                    ContactsContract.CommonDataKinds.Phone.DATA14,
//                    ContactsContract.CommonDataKinds.Phone.DATA15,
//                    ContactsContract.CommonDataKinds.Identity.DATA1,
//                    ContactsContract.CommonDataKinds.Identity.DATA2,
//                    ContactsContract.CommonDataKinds.Identity.DATA3,
//                    ContactsContract.CommonDataKinds.Identity.DATA4,
//                    ContactsContract.CommonDataKinds.Identity.DATA5,
//                    ContactsContract.CommonDataKinds.Identity.DATA6,
//                    ContactsContract.CommonDataKinds.Identity.DATA7,
//                    ContactsContract.CommonDataKinds.Identity.DATA8,
//                    ContactsContract.CommonDataKinds.Identity.DATA9,
//                    ContactsContract.CommonDataKinds.Identity.DATA10,
//                    ContactsContract.CommonDataKinds.Identity.DATA11,
//                    ContactsContract.CommonDataKinds.Identity.DATA12,
//                    ContactsContract.CommonDataKinds.Identity.DATA13,
//                    ContactsContract.CommonDataKinds.Identity.DATA14,
//                    ContactsContract.CommonDataKinds.Identity.DATA15,
//                    ContactsContract.CommonDataKinds.Phone.DATA9,
//                    ContactsContract.CommonDataKinds.Phone.DATA10,
//                    ContactsContract.CommonDataKinds.Phone.DATA11,
//                    ContactsContract.CommonDataKinds.Phone.DATA12,
//                    ContactsContract.CommonDataKinds.Phone.DATA13,
//                    ContactsContract.CommonDataKinds.Phone.DATA14,
//                    ContactsContract.CommonDataKinds.Phone.DATA15
            };
            Cursor cursor;
            cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, contactDetails, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
//                cursor.getString()


//                ContactsContract.CommonDataKinds.Phone.NUMBER,
//                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
//                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
//                        ContactsContract.CommonDataKinds.Identity.DATA1,
//                        ContactsContract.CommonDataKinds.Callable.DATA1,
//                        ContactsContract.CommonDataKinds.Contactables.DATA1,
//                        ContactsContract.CommonDataKinds.Email.DATA1,
//                        ContactsContract.CommonDataKinds.Event.DATA1,
//                        ContactsContract.CommonDataKinds.GroupMembership.DATA1,
//                        ContactsContract.CommonDataKinds.Nickname.DATA1,
//                        ContactsContract.CommonDataKinds.Note.DATA1,
//                        ContactsContract.CommonDataKinds.Organization.DATA1,
//                        ContactsContract.CommonDataKinds.SipAddress.DATA1,
//                        ContactsContract.CommonDataKinds.StructuredName.DATA1,
//                        ContactsContract.CommonDataKinds.StructuredPostal.DATA1,
//                        ContactsContract.CommonDataKinds.Website.DATA1
//                ContactsContract.CommonDataKinds.Phone.NUMBER,
//                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
//                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
//                        ContactsContract.CommonDataKinds.Organization.COMPANY
                System.out.println("Phone.NUMBER "+cursor.getString(0));
                System.out.println("Phone.DISPLAY_NAME = "+cursor.getString(1));
                System.out.println("Phone.CONTACT_ID = "+cursor.getString(2));
                System.out.println("Organization.COMPANY = "+cursor.getString(3));
//                System.out.println("Callable.DATA1 = "+cursor.getString(4));
//                System.out.println("Contactables.DATA1 = "+cursor.getString(5));
//                System.out.println("Email.DATA1 = "+cursor.getString(6));
//                System.out.println("Event.DATA1 = "+cursor.getString(7));
//                System.out.println("GroupMembership.DATA1 = "+cursor.getString(8));
//                System.out.println("Nickname.DATA1 = "+cursor.getString(9));
//                System.out.println("Note.DATA1 = "+cursor.getString(10));
//                System.out.println("Organization.DATA1 = "+cursor.getString(11));
//                System.out.println("SipAddress.DATA1 = "+cursor.getString(12));
//                System.out.println("StructuredName.DATA1 = "+cursor.getString(13));
//                System.out.println("StructuredPostal.DATA1 = "+cursor.getString(14));
//                System.out.println("Website.DATA1 = "+cursor.getString(15));
//                System.out.println("PhoneDATA13 = "+cursor.getString(16));
//                System.out.println("PhoneDATA14 = "+cursor.getString(17));
//                System.out.println("PhoneDATA15 = "+cursor.getString(18));
//                System.out.println("Identity.DATA1 = "+cursor.getString(19));
//                System.out.println("Identity.DATA2 = "+cursor.getString(20));
//                System.out.println("Identity.DATA3 = "+cursor.getString(21));
//                System.out.println("Identity.DATA4 = "+cursor.getString(22));
//                System.out.println("Identity.DATA5 = "+cursor.getString(23));
//                System.out.println("Identity.DATA6 = "+cursor.getString(24));
//                System.out.println("Identity.DATA7 = "+cursor.getString(25));
//                System.out.println("Identity.DATA8 = "+cursor.getString(26));
//                System.out.println("Identity.DATA9 = "+cursor.getString(27));
//                System.out.println("Identity.DATA10 = "+cursor.getString(28));
//                System.out.println("Identity.DATA11 = "+cursor.getString(29));
//                System.out.println("Identity.DATA12 = "+cursor.getString(30));
//                System.out.println("Identity.DATA13 = "+cursor.getString(31));
//                System.out.println("Identity.DATA14 = "+cursor.getString(32));
//                System.out.println("Identity.DATA15 = "+cursor.getString(33));



//                System.out.println("----------------");

                for (int i = 0; i < cursor.getCount(); i++) {
//                    String string0 = cursor.getString(0);

                    for (int j = 0; j < 18; j++) {
                        if (cursor.getString(j)!="null"&&cursor.getString(j)!=null&&cursor.getString(j)!=""){
                            System.out.println(j+" = "+cursor.getString(j));

                        }

                    }

                    System.out.println("===============");

//                    }

//
//                    System.out.println("Phone.NUMBER "+cursor.getString(0));
//                    System.out.println("Phone.DISPLAY_NAME = "+cursor.getString(1));
//                    System.out.println("Phone.CONTACT_ID = "+cursor.getString(2));
//                    System.out.println("Organization.Organization.DATA = "+cursor.getString(3));
//                    System.out.println("Organization.Organization.DATA1 = "+cursor.getString(4));
//                    System.out.println("Organization.Organization.DATA2 = "+cursor.getString(5));
//                    System.out.println("Organization.Organization.DATA3 = "+cursor.getString(6));
//                    System.out.println("Organization.Organization.DATA4 = "+cursor.getString(7));
//                    System.out.println("Organization.Organization.DATA5 = "+cursor.getString(8));
//                    System.out.println("Organization.Organization.DATA6 = "+cursor.getString(9));
//                    System.out.println("Organization.Organization.DATA7 = "+cursor.getString(10));
//                    System.out.println("Organization.Organization.DATA8 = "+cursor.getString(11));
//                    System.out.println("Organization.Organization.DATA9 = "+cursor.getString(12));
//                    System.out.println("Organization.Organization.DATA10 = "+cursor.getString(13));
//                    System.out.println("Organization.Organization.DATA11 = "+cursor.getString(14));
//                    System.out.println("Organization.Organization.DATA12 = "+cursor.getString(15));
//                    System.out.println("Organization.Organization.DATA13 = "+cursor.getString(16));
//                    System.out.println("Organization.Organization.DATA14 = "+cursor.getString(17));
//                    System.out.println("Organization.Organization.DATA15 = "+cursor.getString(18));

//                    System.out.println("===============");


                    cursor.moveToNext();
                }
               System.out.println( "Liczba kontaktów: "+cursor.getCount());


            }
            return cursor;
        }else {
            return null;
        }


//            String[] details = new String[]{CallLog.Calls.NUMBER,                   //0
//                    CallLog.Calls.TYPE,                                             //1
//                    CallLog.Calls.DURATION,                                         //2
//                    CallLog.Calls.CACHED_NAME,                                      //3
//                    CallLog.Calls._ID,                                              //4
//                    CallLog.Calls.DATE,                                             //5
//                    CallLog.Calls.CACHED_FORMATTED_NUMBER, //570 082 100            //6
//                    CallLog.Calls.COUNTRY_ISO, //PL                                 //7
//                    CallLog.Calls.LAST_MODIFIED, //1582571365480                    //8
//                    CallLog.Calls.NEW, //0                                           //9
//                    CallLog.Calls.PHONE_ACCOUNT_ID
//            };



    }


    private void getContactList() {
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String data1 = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.STATUS));


                        System.out.println("Nazwa: " + name+", Numer telefonu: " + phoneNo);
//                        Log.i(TAG, "Name: " + name);
//                        Log.i(TAG, "Phone Number: " + phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }
    }





    private void setCallLogToList(Cursor cursor) {


    }

    private Cursor getCursor() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
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
            cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, details, null, null, CallLog.Calls._ID + " DESC");
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
            }
            return cursor;
        }else {
            return null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = getActivity();
        context = activity.getApplicationContext();
        return inflater.inflate(R.layout.fragment_local_contacts, container, false);
    }


    private boolean isNullOrEmpty(String string) {
        if ((string==null)||(string.equals(""))){
            return true;
        }else{
            return false;
        }
    }

}
