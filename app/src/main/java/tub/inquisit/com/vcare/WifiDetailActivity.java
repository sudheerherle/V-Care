package tub.inquisit.com.vcare;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WifiDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_detail);
        CreateAndAddFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void CreateAndAddFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        WifiViewFragment wifiViewFragment = new WifiViewFragment();
        fragmentTransaction.add(R.id.wifi_container,wifiViewFragment,"Wifi_view_fragment");
        fragmentTransaction.commit();
    }
}
