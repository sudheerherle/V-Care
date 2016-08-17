package tub.inquisit.com.vcare;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by I14746 on 7/2/2016.
 */
public class WifiAdapter extends ArrayAdapter<WiFi> {

    public static class ViewHolder{
        ImageView wifiIcon;
        TextView wifiSSID;
        TextView wifiStrength;
    }
    public WifiAdapter(Context context, ArrayList<WiFi> wifi){
        super(context,0,wifi);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        WiFi wifi = getItem(position);

        ViewHolder viewholder;
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row,parent,false);
            viewholder = new ViewHolder();
            viewholder.wifiIcon = (ImageView) convertView.findViewById(R.id.wifiIcon);
            viewholder.wifiSSID = (TextView) convertView.findViewById(R.id.ListItemWifiSSID);
            viewholder.wifiStrength = (TextView) convertView.findViewById(R.id.ListItemSSIDStrength);

            convertView.setTag(viewholder);
        }else{
            viewholder = (ViewHolder) convertView.getTag();
        }

//        TextView wifiSSID = (TextView) convertView.findViewById(R.id.ListItemWifiSSID);
//        TextView wifiStrength = (TextView) convertView.findViewById(R.id.ListItemSSIDStrength);
//        ImageView image = (ImageView) convertView.findViewById(R.id.wifiIcon);

        viewholder.wifiSSID.setText(wifi.getSSID());
        viewholder.wifiStrength.setText(wifi.getStrength());

        return convertView;

    }
}
