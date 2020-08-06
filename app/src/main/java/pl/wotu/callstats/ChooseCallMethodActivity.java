package pl.wotu.callstats;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChooseCallMethodActivity extends AppCompatActivity {

    private Button buttonCard0, buttonCard1;
    private TextView whoYouGonnaCall;
    private List<Map<String, String>> simCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_call_method);

        buttonCard0 = findViewById(R.id.simCard0Button);
        buttonCard1 = findViewById(R.id.simCard1Button);
        whoYouGonnaCall = findViewById(R.id.whoYouGonnaCall);

        Bundle bundle = this.getIntent().getExtras();
        int numberOfSimCards = bundle.getInt("numberOfSimCards");
        final String phoneNumber = bundle.getString("phoneNumber");
        String cachedName = bundle.getString("cachedName");

        simCards = new ArrayList<>();

        for(int i=0;i<numberOfSimCards;i++){
            simCards.add(i,CallAppConfig.getSimCardsInformation(ChooseCallMethodActivity.this, this, i));
        }

//        whoYouGonnaCall.setText("Zadzwoń do\n" + cachedName+"\n"+phoneNumber);
        whoYouGonnaCall.setText("Zadzwoń do: " + cachedName);
        if (numberOfSimCards == 2) {
            buttonCard0.setText(simCards.get(0).get("getCarrierName"));
            buttonCard1.setText(simCards.get(1).get("getCarrierName"));

            buttonCard0.setOnClickListener(view -> {
                justCall(phoneNumber,0);
                finish();
            });

            buttonCard1.setOnClickListener(view -> {
                justCall(phoneNumber,1);
                finish();
            });

        }

//        getSimSlotIndex 0
//        getIccId 89480610500438431628
//        getCarrierName PLAY (T-Mobile)
//        getCountryIso pl
//        getDisplayName CARD 1
//        getNumber null
//        getDataRoaming 0
//        I/System.out: Liczba dostępnych kart SIM: 2
//                ----------------------------------------
//        getSimSlotIndex 1
//        getIccId 8948031631200549332F
//        getCarrierName Orange
//        getCountryIso
//        getDisplayName Orange
//        getNumber null
//        getDataRoaming 0

    }

    private void justCall(String phoneNumber,int simCardSlot) {
        final TelecomManager telecomManager = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ChooseCallMethodActivity.this, Manifest.permission.READ_CALL_LOG)) {
                ActivityCompat.requestPermissions(ChooseCallMethodActivity.this, new String[]{Manifest.permission.READ_CALL_LOG}, 1);
                Toast.makeText(getApplicationContext(), "TRUE\nNie masz uprawnień do wykonywania połączeń!", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(ChooseCallMethodActivity.this, new String[]{Manifest.permission.READ_CALL_LOG}, 1);
                Toast.makeText(getApplicationContext(), "FALSE\nNie masz uprawnień do wykonywania połączeń!", Toast.LENGTH_LONG).show();
            }
        }
        List<PhoneAccountHandle> accounts = telecomManager.getCallCapablePhoneAccounts();

        Uri uri = Uri.fromParts("tel", phoneNumber, "");
        Bundle extras = new Bundle();
        extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, accounts.get(simCardSlot));
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ChooseCallMethodActivity.this, Manifest.permission.READ_CALL_LOG)) {
                ActivityCompat.requestPermissions(ChooseCallMethodActivity.this, new String[]{Manifest.permission.READ_CALL_LOG}, 1);
                Toast.makeText(getApplicationContext(), "TRUE\nNie masz uprawnień do wykonywania połączeń!", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(ChooseCallMethodActivity.this, new String[]{Manifest.permission.READ_CALL_LOG}, 1);
                Toast.makeText(getApplicationContext(), "FALSE\nNie masz uprawnień do wykonywania połączeń!", Toast.LENGTH_LONG).show();
            }
        } else {
            telecomManager.placeCall(uri, extras);
        }
    }

}
