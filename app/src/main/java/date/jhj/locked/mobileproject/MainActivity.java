package date.jhj.locked.mobileproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    static MainActivity activity;
////////////////////////////////////////////////////
///// BUTTON
////////////////////////////////////////////////////
    // backspace 버튼 [1개]
    ImageButton backspace;
    // bit 연산자 [4개]
    Button[] bit;// and or not xor  // & | ~ ^
    // mod 연산자, () , Clear [3개]
    Button mod;
    Button bracket;
    Button clear;
    // 숫자 버튼 0~9 랑 "." [11개]
    Button[] num;
    Button dot;
    // 4칙 연산자 [4개]
    Button[] cal; // + - * / (plus, minus, mul, div)
    String operator="+-*/%&|^";
    // 결과 버튼[1개]
    Button result_button;
    // 최근 목록
    ImageButton history;
    List<String> history_cal=new ArrayList<>();
    List<String> history_result=new ArrayList<>();

    AlertDialog dialog;
    View dialog_view;
    HistoryAdapter adapter;
    List<HistoryData> history_list=new ArrayList<>();
    RecyclerView listView;
    RecyclerView.LayoutManager mLayoutManager;
    Button complete;
    // 파일 읽기
    ImageButton file;

    // Backup
    String BACKUP;
////////////////////////////////////////////////////
///// 수식
////////////////////////////////////////////////////
    // 과정 텍스트
    TextView during;
    // 결과 값 텍스트
    TextView result_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity =this;
        // BACK UP
        BACKUP = BackupData.getPrefHistory(MainActivity.this);

        if(BACKUP!="") {
            HistoryData[] array= new Gson().fromJson(BACKUP, HistoryData[].class);
            history_list.addAll(Arrays.asList(array));
        }

////////////////////////////////////////////////////
/////
/////  버튼
/////
////////////////////////////////////////////////////
        // 숫자 버튼
        num=new Button[10];
        num[0]=findViewById(R.id.num_0);
        num[1]=findViewById(R.id.num_1);
        num[2]=findViewById(R.id.num_2);
        num[3]=findViewById(R.id.num_3);
        num[4]=findViewById(R.id.num_4);
        num[5]=findViewById(R.id.num_5);
        num[6]=findViewById(R.id.num_6);
        num[7]=findViewById(R.id.num_7);
        num[8]=findViewById(R.id.num_8);
        num[9]=findViewById(R.id.num_9);
        // 숫자 버튼 리스너
        for(int i=0;i<10;i++)
            num[i].setOnClickListener(this);
        // dot 버튼
        dot = findViewById(R.id.num_point);
        dot.setOnClickListener(this);

        // clear 버튼
        clear =findViewById(R.id.clear);
        clear.setOnClickListener(this);

        // result 버튼
        result_button= findViewById(R.id.result);
        result_button.setOnClickListener(this);


        // backspace 버튼
        backspace = findViewById(R.id.backspace);
        backspace.setOnClickListener(this);
        backspace.setLongClickable(true);
        backspace.setEnabled(false);
        backspace.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                during.setText("");
                return false;
            }
        });
        backspace.setImageDrawable(getDrawable(R.drawable.backspace_white));

        // cal 버튼
        cal = new Button[4];
        cal[0]=findViewById(R.id.plus);
        cal[1]=findViewById(R.id.minus);
        cal[2]=findViewById(R.id.mul);
        cal[3]=findViewById(R.id.div);
        for(int i=0;i<4;i++)
            cal[i].setOnClickListener(this);

        // bracket 버튼
        bracket =findViewById(R.id.bracket);
        bracket.setOnClickListener(this);

        // mod 버튼
        mod = findViewById(R.id.mod);
        mod.setOnClickListener(this);

        bit= new Button[4];
        bit[0]=findViewById(R.id.and);
        bit[1]=findViewById(R.id.or);
        bit[2]=findViewById(R.id.not);
        bit[3]=findViewById(R.id.xor);
        for(int i=0;i<4;i++)
            bit[i].setOnClickListener(this);

        //history
        history = findViewById(R.id.history);
        history.setOnClickListener(this);

////////////////////////////////////////////////////
/////
/////  텍스트
/////
////////////////////////////////////////////////////
        during = findViewById(R.id.text_cal);
        result_text=findViewById(R.id.text_result);
        during.setText("");
        result_text.setText("");
        during.setOnClickListener(this);
        result_text.setOnClickListener(this);
////////////////////////////////////////////////////
/////
/////  히스토리
/////
////////////////////////////////////////////////////
        AlertDialog.Builder bu = new AlertDialog.Builder(MainActivity.this);
        dialog = bu.create();

        // xml파일을 dialog로 붙이기
        LayoutInflater inflater = getLayoutInflater();
        dialog_view = inflater.inflate(R.layout.history_dialog, null);
        listView=dialog_view.findViewById(R.id.listview);

        listView.addItemDecoration(new Divider(MainActivity.this));


        complete=dialog_view.findViewById(R.id.complete);
        complete.setOnClickListener(this);

        adapter=new HistoryAdapter(history_list);
        adapter.setHasStableIds(false);

        adapter.notifyDataSetChanged();

        final ItemTouchHelper.SimpleCallback simpleItemTouchCallback =  new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN| ItemTouchHelper.START| ItemTouchHelper.END, ItemTouchHelper.START|ItemTouchHelper.END) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                Log.e("포지션", " "+viewHolder.getAdapterPosition());
                history_list.remove(viewHolder.getAdapterPosition());
                // 백업
                BACKUP = new Gson().toJson(history_list);
                BackupData.setPrefHistory(MainActivity.this,BACKUP);

                adapter.notifyItemRemoved(viewHolder.getAdapterPosition()); //TODO 스와이프 삭제기능
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                final int fromPos = viewHolder.getAdapterPosition();
                final int toPos = target.getAdapterPosition();
                Collections.swap(history_list,fromPos,toPos);
                TextView beforeTV = viewHolder.itemView.findViewById(R.id.count);
                TextView afterTV = target.itemView.findViewById(R.id.count);
                String before= beforeTV.getText().toString();
                String after= afterTV.getText().toString();

                beforeTV.setText(after);
                afterTV.setText(before);

                adapter.notifyItemMoved(fromPos,toPos);
                return true;
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(listView);
        listView.setAdapter(adapter);
        mLayoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(mLayoutManager);

        dialog_view.setBackgroundColor(getColor(android.R.color.white));
        dialog_view.setBackground(getDrawable(R.drawable.dialog));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setView(dialog_view);





    }

    @Override
    public void onClick(View view) {
        for(int i=0;i<10;i++) {
            if (view == num[i]) {
                if(during.getText().length()>0) {
                    char tmp = during.getText().toString().charAt(during.getText().length() - 1);
                    if(tmp==')')
                        during.setText(during.getText() + "*");
                }
                during.setText(during.getText() + String.valueOf(i));
            }
        }
        if(view == result_button){
            if(during.getText().length()>0) {
                Cal c=new Cal();
                String kk=" ";
                kk = c.bracketCalMain(during.getText().toString());

                Log.e(" kkk ::: ", " "+kk);
                // 숫자가 아니면 빨간색
                if(kk.length()>0) {
                    if (Character.isDigit(kk.charAt(kk.length() - 1)))
                        result_text.setTextColor(getColor(R.color.result_button_up));
                    else
                        result_text.setTextColor(getColor(R.color.colorAccent));
                    result_text.setText(kk);

                    // 50개 넘으면 하나 삭제.
                    if(history_list.size()>=50)
                        history_list.remove(history_list.size()-1);
                    history_list.add(0,new HistoryData(during.getText().toString(),kk));
                    adapter.notifyDataSetChanged();

                    // 백업
                    BACKUP = new Gson().toJson(history_list);
                    BackupData.setPrefHistory(MainActivity.this,BACKUP);
                }

            }
        }
        else if(view == clear){// 글씨 초기화
            result_text.setText("");
            during.setText("");
        }
        else if(view == backspace){
            if(during.getText().length()>0)
                during.setText(during.getText().subSequence(0,during.getText().length()-1)); // 뒤에서 한자리씩 자르기
        }
        else if(view == cal[0]) {// 더하기
            checkCal("+");
        }
        else if(view == cal[1]){// 빼기 TODO 음수로 변환도 가능하게.
            checkCal("-");
        }
        else if(view == cal[2]){// 곱하기
            checkCal("*");
        }
        else if(view == cal[3]){// 나누기
            checkCal("/");
        }
        else if(view == dot){ // TODO 앞에 .이 이미 있으면 안붙임.
            boolean dot_flag=false;

            if(!during.getText().toString().contains(".")){
                dot_flag=true;
            }
            else{     // (앞에 있다면)점 부터 맨 마지막 까지의 문자열중에 연산자가 있으면 . 생성
                int dot_index=during.getText().toString().lastIndexOf(".");
                String str=during.getText().toString().substring(dot_index,during.getText().length()-1);
                Log.e("DOT :::: ", " "+str + "  is :: "+str.contains("+"));
                if(str.contains("+")||str.contains("-")||str.contains("/")||str.contains("*")||str.contains("~")||str.contains("|")||str.contains("&")||str.contains("^")||str.contains("%"))
                    dot_flag=true;
                else
                    dot_flag=false;
            }
            if(dot_flag){
                if (during.getText().length() > 0) {
                    char tmp = during.getText().toString().charAt(during.getText().length() - 1);
                    if (tmp == ')')
                        during.setText(during.getText() + "*");
                    if (!Character.isDigit(tmp))
                        during.setText(during.getText() + "0");
                } else
                    during.setText(during.getText() + "0");
                during.setText(during.getText() + ".");
            }

        }
        else if(view == mod){
            checkCal("%");
        }else if(view == bracket){
            checkBracket();
        }
        else if(view == history){
            dialog.show();
        }
        else if(view == complete){
            dialog.dismiss();
        }
        else if(view == bit[0]){
            checkCal("&");
        }
        else if(view == bit[1]){
            checkCal("|");
        }
        else if(view == bit[2]){
            // ~ 연산자만 앞에 옴.
            // 어느 상황에서도 올 수 있음?
            // 숫자나 ) 뒤에는 *붙일것..?
            if(during.getText().length()>0) {
                char last = during.getText().charAt(during.getText().length() - 1);
                if (last == ')' || Character.isDigit(last))
                    during.setText(during.getText() + "*");
            }
            during.setText(during.getText() + "~");
        }
        else if(view == bit[3]){
            checkCal("^");
        }else if(view == during|| view == result_text){
            clipBoard();
        }



        if(during.getText().length()>0){
            backspace.setImageDrawable(getDrawable(R.drawable.backspace));
            backspace.setEnabled(true);
        }else {
            backspace.setImageDrawable(getDrawable(R.drawable.backspace_white));
            backspace.setEnabled(false);
        }
    }

    public void checkBracket(){// TODO 이거 스택에 ( 검사하고 해야할듯
        if(during.getText().length()>0){
            char tmp = during.getText().toString().charAt(during.getText().length()-1);
            int start=0;
            int end=0;

            for(int i=0;i<during.getText().length();i++){
                if(during.getText().toString().charAt(i)=='(')
                    start++;
                else if(during.getText().toString().charAt(i)==')')
                    end++;
            }
            if(end==start&&!operator.contains(tmp+"")&&tmp!='~'){
                during.setText(during.getText() + "*(");
            }
            else if(tmp == '('||operator.contains(tmp+"")||tmp=='~') {
                during.setText(during.getText() + "(");
            }
            else {
                during.setText(during.getText() + ")");
            }
        }else{
            during.setText("(");
        }
    }

    public void checkCal(String a){
        if(during.getText().length()>0) {
            char tmp = during.getText().toString().charAt(during.getText().length()-1);
            if ('~'==tmp && a.equals("-"))
                during.setText(during.getText() + a);
            else if (!operator.contains(tmp+""))
                during.setText(during.getText() + a);
            else
                during.setText(during.getText().subSequence(0,during.getText().length()-1) + a);
        }
    }

    public void clipBoard(){
        ClipData.Item item = new ClipData.Item(during.getText().toString());
        String[] mimeType = new String[1];
        mimeType[0] = ClipDescription.MIMETYPE_TEXT_URILIST;
        ClipData cd = new ClipData(new ClipDescription("text_data", mimeType), item);
        android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(cd);
        Toast.makeText(this, "클립보드에 복사 되었습니다.", Toast.LENGTH_LONG).show();
    }

}
