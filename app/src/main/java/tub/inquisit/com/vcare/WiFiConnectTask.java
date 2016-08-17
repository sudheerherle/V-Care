package tub.inquisit.com.vcare;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.text.TextUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WiFiConnectTask extends AsyncTask<String, String, String>
{
    private static final int MAX_PRIORITY = 999999;
    private WifiConfiguration config;
    private WifiManager wifiManager;
    private ProgressDialog waitDialog;
    private Context context;
    private List<WifiConfiguration> item;

    private boolean                 isConnected = false;
    private int                     counter     = 1;
    private String                  ssid;
    private String                  pass;
    private boolean                 flg;

    public WiFiConnectTask(WifiManager wifiManager, WifiConfiguration config, List<WifiConfiguration> item,
                           Context context, String ssid, boolean flg, String pass, AsyncResponse asyncResponse)
    {
        this.wifiManager = wifiManager;
        this.config = config;
        this.context = context;
        this.item = item;
        this.ssid = ssid;
        this.pass = pass;
        this.flg = flg;
        this.delegate = asyncResponse;
    }

    @Override
    protected void onPreExecute()
    {
        waitDialog = new ProgressDialog(context);
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitDialog.setIndeterminate(true);
        waitDialog.setMessage("Please wait...");
        waitDialog.setCanceledOnTouchOutside(false);
        waitDialog.show();
    }

    @Override
    protected String doInBackground(String... params)
    {
//        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        NetworkInfo.DetailedState ds = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
        if ( ds == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
            String ssid = wifiInfo.getSSID();
            if(ssid.equals(this.ssid)){
                if (waitDialog != null && waitDialog.isShowing())
                {
                    waitDialog.cancel();
                    waitDialog.dismiss();
                }
                return "Already Connected";
            }
        }

        for (int x = 0; x < item.size(); x++)
        {
            if (item.get(x).SSID.equals(config.SSID))
            {
                wifiManager.disconnect();
                //wifiManager.addNetwork(config);
                int newPri = getMaxPriority() + 1;
                if(newPri >= MAX_PRIORITY) {
                    // We have reached a rare situation.
                    newPri = shiftPriorityAndSave();
                }

                item.get(x).priority = newPri;
                wifiManager.updateNetwork(item.get(x));
                wifiManager.saveConfiguration();

                boolean state = wifiManager.enableNetwork(item.get(x).networkId, true);
                System.out.println("enable " + state);

                boolean c = wifiManager.reconnect();
                System.out.println("reconnect  " + c);

                try
                {
                    Thread.sleep(10000);
                }
                catch (InterruptedException e)
                {
                }

                if(flg)
                {
                    isConnected = isConnected(context);
                    if (!isConnected)
                        counter = 120;
                }
                else
                {
//                    while (true)
//                    {
//                        isConnected = isConnected(context);
//
//                        if (isConnected)
//                        {
//                            isConnected = true;
//                            break;
//                        }
//
//                        if (counter == 120)
//                            break;
//
//                        try
//                        {
//                            Thread.sleep(1000);
//                        }
//                        catch (InterruptedException e)
//                        {
//                        }
//
//                        counter++;
//                    }
                }

                break;
            }
        }

        return "";
    }

    @Override
    protected void onPostExecute(String result)
    {
        waitDialog.cancel();
        waitDialog.dismiss();

        if (counter == 120)
            //  if cannot connected within 2 minutes then show msg
            new DisplayToast(context, "Could not connect");
        if(isConnected(context)){
            result = "true";
        }else
            result = "false";
        delegate.processFinish(result);
    }

    public boolean isConnected(Context context)
    {
        //  Check to see if it is connected or not
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;

        if (connectivityManager != null)
            networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && isCurrentSSIDmatch() && networkInfo.getState() == NetworkInfo.State.CONNECTED;
    }

    public boolean isCurrentSSIDmatch() {
        String current_ssid = null;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                current_ssid = connectionInfo.getSSID();
            }
        }
        if(current_ssid!=null){
            if(current_ssid.equals(this.ssid)){
                return true;
            }else
                return false;
        }else return false;
    }

    public AsyncResponse delegate = null;//Call back interface

    private int getMaxPriority() {
        final List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        int pri = 0;
        for (final WifiConfiguration config : configurations) {
            if (config.priority > pri) {
                pri = config.priority;
            }
        }
        return pri;
    }

    private void sortByPriority(final List<WifiConfiguration> configurations) {
        Collections.sort(configurations,
                new Comparator<WifiConfiguration>() {
                    @Override
                    public int compare(WifiConfiguration object1, WifiConfiguration object2) {
                        return object1.priority - object2.priority;
                    }
                });
    }

    private int shiftPriorityAndSave() {
        final List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        sortByPriority(configurations);
        final int size = configurations.size();
        for (int i = 0; i < size; i++) {
            final WifiConfiguration config = configurations.get(i);
            config.priority = i;
            wifiManager.updateNetwork(config);
        }
        wifiManager.saveConfiguration();
        return size;
    }

    /**
     * Add quotes to string if not already present.
     *
     * @param string
     * @return
     */
    public static String convertToQuotedString(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }

        final int lastPos = string.length() - 1;
        if (lastPos > 0
                && (string.charAt(0) == '"' && string.charAt(lastPos) == '"')) {
            return string;
        }

        return "\"" + string + "\"";
    }
}