package date.jhj.locked.mobileproject;

import android.os.Handler;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

class TimeTask extends TimerTask {

    Button b;
    long time,time_2;

    TimeTask(Button b,long time){
        this.b=b;
        this.time=time;
    }
    public void run() {
        mHandler.post(mUpdateTimeTask);
    }

    public int getTime(){
        return (int)(time_2-time)/1000;
    }
    private Handler mHandler = new Handler();

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            time_2=System.currentTimeMillis();
//            Date rightNow = new Date();
//            SimpleDateFormat formatter = new SimpleDateFormat(
//                    "시간\nss초");

//            String dateString = formatter.format(rightNow);
//            b.setText(dateString);
            String time_str = "시간 : "+((time_2-time)/1000)+"."+((time_2-time)%1000)/100+"초";
            b.setText(time_str);
        }
    };
}
