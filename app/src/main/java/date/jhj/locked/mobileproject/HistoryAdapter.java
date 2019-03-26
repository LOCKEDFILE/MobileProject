package date.jhj.locked.mobileproject;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//public class HistoryAdapter extends ArrayAdapter<HistoryData> {// 커스텀.... 으으
//
//    Context mContext;
//    List<HistoryData> list;
//
//    public HistoryAdapter(@NonNull Context context, @SuppressLint("SupportAnnotationUsage") @LayoutRes List<HistoryData> list) {
//        super(context, 0 , list);
//        mContext = context;
//        this.list = list;
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    @NonNull
//    @Override
//    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        View listItem = convertView;
//        if(listItem == null)
//            listItem = LayoutInflater.from(mContext).inflate(R.layout.history_list,parent,false);
//
//        final HistoryData current = list.get(position);
//        final TextView cal =  listItem.findViewById(R.id.cal);
//        cal.setText(current.cal);
//        TextView sub_text = listItem.findViewById(R.id.result);
//        sub_text.setText(current.result);
//
//
//        return listItem;
//    }
//
//}
import androidx.recyclerview.widget.RecyclerView;


public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView cal,result;

        @SuppressLint("ClickableViewAccessibility")
        MyViewHolder(View view){
            super(view);
            cal=view.findViewById(R.id.cal);
            result=view.findViewById(R.id.result);
        }

    }
    public List<HistoryData> list;
    HistoryAdapter(List<HistoryData>list){
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list, parent, false);

        return new MyViewHolder(v);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MyViewHolder myViewHolder = (MyViewHolder) holder;

        myViewHolder.cal.setText(list.get(position).cal+"");
        myViewHolder.result.setText(list.get(position).result+"");
    }

    @Override
    public int getItemCount() {
        if(list==null)
            return 0;
        return list.size();
    }


}