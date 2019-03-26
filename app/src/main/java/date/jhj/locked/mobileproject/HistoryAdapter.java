package date.jhj.locked.mobileproject;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static date.jhj.locked.mobileproject.MainActivity.activity;


public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView cal,result,count;

        @SuppressLint("ClickableViewAccessibility")
        MyViewHolder(View view){
            super(view);
            cal=view.findViewById(R.id.cal);
            result=view.findViewById(R.id.result);
            count=view.findViewById(R.id.count);
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


        if(list.get(position).result.length()>0) {
            char last = list.get(position).result.charAt(list.get(position).result.length() - 1);

            if (Character.isDigit(last))
                myViewHolder.result.setTextColor(activity.getColor(R.color.result_button_up));
            else
                myViewHolder.result.setTextColor(activity.getColor(R.color.colorAccent));
        }

        myViewHolder.result.setText(list.get(position).result+"");
        myViewHolder.count.setText(position+".");
    }

    @Override
    public int getItemCount() {
        if(list==null)
            return 0;
        return list.size();
    }


}