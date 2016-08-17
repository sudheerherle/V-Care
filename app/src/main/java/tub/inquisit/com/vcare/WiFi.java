package tub.inquisit.com.vcare;

import android.provider.ContactsContract;

/**
 * Created by I14746 on 7/2/2016.
 */
public class WiFi {
    private String SSID, Strength;

    public WiFi(String ssid, String strength){
        this.SSID = ssid;
        this.Strength = strength;
    }

    public String getSSID(){
        return this.SSID;
    }

    public String getStrength(){
        return this.Strength;
    }
}
