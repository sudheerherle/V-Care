package tub.inquisit.com.vcare;

import android.net.wifi.ScanResult;

/**
 * Created by I14746 on 7/3/2016.
 */
public class SharedData {

    private ScanResult scanresult;
    private static SharedData shareddata = new SharedData( );

    /* A private Constructor prevents any other
     * class from instantiating.
     */
    private SharedData(){ }

    /* Static 'instance' method */
    public static SharedData getInstance( ) {
        return shareddata;
    }

    public ScanResult getScanResult(){
        return this.scanresult;
    }

    public void setScanResult(ScanResult sr){
        this.scanresult = sr;
    }
}
