package tub.inquisit.com.vcare;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainActivityListFragment extends ListFragment {

    WifiManager mWifiManager = null;
    ArrayList<WiFi> wifis = new ArrayList<WiFi>();
    WifiAdapter wifiAdapter = null;
    SharedData shareddata = SharedData.getInstance();
    List<ScanResult> mScanResults;
    public ArrayAdapter<String> adapter = null;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mWifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        if(!mWifiManager.isWifiEnabled()) {
            new DisplayToast(getContext(), "Please turn on WiFi");

        }
        getActivity().registerReceiver(mWifiScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            mWifiManager.startScan();

    }

    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                mScanResults = mWifiManager.getScanResults();
                String[] array = new String[mScanResults.size()];

                wifis.clear();
                for (int i = 0; i < array.length; i++) {
//                    if(mScanResults.get(i).SSID.isEmpty()){
//                        mScanResults.remove(i);
//                    }else
                        wifis.add(new WiFi(mScanResults.get(i).SSID, getSignalStrength(mScanResults.get(i).level)));
                }

                wifiAdapter = new WifiAdapter(getContext(), wifis);
                setListAdapter(wifiAdapter);
                getListView().setDivider(ContextCompat.getDrawable(getActivity(),android.R.color.holo_blue_dark));
                getListView().setDividerHeight(2);
                // add your logic here
                }
        }
    };

    private String getSignalStrength(int dBm){
        // dBm to Quality:
        String quality = "Signal Strength: ";
        if(dBm <= -100)
            quality = quality+0;
        else if(dBm >= -50)
            quality = quality+100;
        else
            quality = quality+(2 * (dBm + 100));
        return quality+"%";
    }
    @Override
    public void onListItemClick(ListView l,View v,int position,long id){
        super.onListItemClick(l,v,position,id);
        LaunchWifiContainer(position);
    }

    public void LaunchWifiContainer(int position){
        WiFi wifi = (WiFi) getListAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), WifiDetailActivity.class);
        intent.putExtra(MainActivity.WifiID, wifi.getSSID());
        shareddata.setScanResult(mScanResults.get(position));
        startActivity(intent);
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

        try{
            if(mWifiScanReceiver!=null)
                getActivity().unregisterReceiver(mWifiScanReceiver);
        }catch(Exception e)
        {

        }
        super.onDestroy();

    }
}
