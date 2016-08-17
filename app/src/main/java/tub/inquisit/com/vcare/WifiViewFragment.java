package tub.inquisit.com.vcare;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class WifiViewFragment extends Fragment {

    public String SSID;
    public ScanResult scanResult;
    public String m_Text = "";
//    private AlertDialog.Builder builder;
    Button refreshBtn;
    TextView PercentageFilled;
    Client myClient;
    List<WifiConfiguration> list;
    WifiManager mWifiManager = null;
//    WiFiConnectTask wificonencttask = null;
    public WifiViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentlayout = inflater.inflate(R.layout.fragment_wifi_view,container,false);
        Intent intent = getActivity().getIntent();
        refreshBtn = (Button)fragmentlayout.findViewById(R.id.RefreshBtn);

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UpdatePercentage();
            }
        });
        SSID = intent.getStringExtra(MainActivity.WifiID);
        scanResult = SharedData.getInstance().getScanResult();
        this.getActivity().setTitle(intent.getStringExtra(MainActivity.WifiID));
        String value = getPerecentageFilled();
        PercentageFilled = (TextView) fragmentlayout.findViewById(R.id.Percentage_filled);
        PercentageFilled.setText(value);
        getActivity().registerReceiver(WifiReceiver,
                new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        mWifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        mWifiManager.startScan();
        list = mWifiManager.getConfiguredNetworks();
        return fragmentlayout;
    }

    private void EstablishAndGetStatus(){
        try {
            WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
            scanResult = SharedData.getInstance().getScanResult();
            Log.v("rht", "Item clicked, SSID " + scanResult.SSID + " Security : " + scanResult.capabilities);
            String networkSSID = scanResult.SSID;

            String networkPass = m_Text;

            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes
            conf.status = WifiConfiguration.Status.ENABLED;

            if (scanResult.capabilities.toUpperCase().contains("WEP")) {
                Log.v("rht", "Configuring WEP");
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

                if (networkPass.matches("^[0-9a-fA-F]+$")) {
                    conf.wepKeys[0] = networkPass;
                } else {
                    conf.wepKeys[0] = "\"".concat(networkPass).concat("\"");
                }

                conf.wepTxKeyIndex = 0;

            } else if (scanResult.capabilities.toUpperCase().contains("WPA")) {
                Log.v("rht", "Configuring WPA");

                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

                conf.preSharedKey = "\"" + networkPass + "\"";

            } else {
                Log.v("rht", "Configuring OPEN network");
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.clear();
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            }



            int networkId = wifiManager.addNetwork(conf);

            Log.v("rht", "Add result " + networkId);

            list = wifiManager.getConfiguredNetworks();
            boolean attempted = false;
//            for (WifiConfiguration i : list) {
                if (conf.SSID != null && conf.SSID.equals("\"" + networkSSID + "\"")) {
                       attempted = true;
                    WiFiConnectTask wificonencttask = new WiFiConnectTask(wifiManager,conf,list,getActivity(),conf.SSID,true,conf.preSharedKey, new AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                            boolean b_return = Boolean.parseBoolean(output);
                            setStatus(b_return);
                        }
                    });
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
//                        wificonencttask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,null);
//                    else
//                        wificonencttask.execute(null,null);
                    wificonencttask.execute();
//                    break;
                }
//            }
//            if(attempted){
//                getActivity().onBackPressed();
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setStatus(boolean status){
        if(getView()==null) return;
        ImageView img = (ImageView) getView().findViewById(R.id.wifi_status_icon);
        String uri = null;
        if(status){
            uri = "@android:drawable/presence_online";  // where myresource (without the extension) is the file
            UpdatePercentage();

        }else{
            uri = "@android:drawable/presence_offline";
        }
        int imageResource = getResources().getIdentifier(uri, null, getContext().getPackageName());

        Drawable res = getResources().getDrawable(imageResource);
        img.setImageDrawable(res);
    }
    private void UpdatePercentage(){
         myClient = new Client("192.168.4.1", 80, new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if(output.compareToIgnoreCase("error")==0){
//                    new DisplayToast(getContext().getApplicationContext(),"Error: Could not connect to the device");
                }else if(output.equals("")){
//                    new DisplayToast(getContext().getApplicationContext(),"Error: Could not connect to the device");
                    PercentageFilled.setText(output);
                }
                else PercentageFilled.setText(output);
            }
        });
        myClient.execute();
    }
    private String getPerecentageFilled(){
        String percent = "NA";
        long echo_time = getEchoTime();
        percent = getPercentFromEchoTime(echo_time);
        return percent;
    }


    @Override
    public void onResume() {
        super.onResume();
        askForPassword();
    }

    private void askForPassword(){
//        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
//        list = mWifiManager.getConfiguredNetworks();//WifiManagergetConfiguredNetworks();
        if(list==null){
           return; //list = mWifiManager.getConfiguredNetworks();//WifiManagergetConfiguredNetworks();
        }
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + this.getActivity().getTitle() + "\"")) {
                EstablishAndGetStatus();
                return;
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Password");
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.text_inpu_password, (ViewGroup) getView(), false);
        // Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                m_Text = input.getText().toString();
                EstablishAndGetStatus();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private long getEchoTime(){
        return -1;
    }

    private String getPercentFromEchoTime(long echoTime){
        return "NA";
    }

    private final BroadcastReceiver WifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                list = mWifiManager.getConfiguredNetworks();//WifiManagergetConfiguredNetworks();
                WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                NetworkInfo.DetailedState ds = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
//                if (ds != NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    String ssid = wifiInfo.getSSID().replace("\"","");
                    String title = getActivity().getTitle().toString();
                    if(!ssid.equals(title)){
                        setStatus(false);
//                        askForPassword();
                    }else{
                        setStatus(true);
                        UpdatePercentage();
                    }
//                }

            }
        }


    };

    @Override
    public void onDestroy()
    {
        getActivity().unregisterReceiver(WifiReceiver);
        super.onStop();
    }
}
