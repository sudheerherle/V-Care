package tub.inquisit.com.vcare;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by I14746 on 7/3/2016.
 */
public class DisplayToast extends Toast {
    public DisplayToast(Context context, String str) {
        super(context);
        if(context==null){
            this.cancel();
        }
        else{
            Toast.makeText(context, str,
                    Toast.LENGTH_SHORT).show();
        }
    }
}
