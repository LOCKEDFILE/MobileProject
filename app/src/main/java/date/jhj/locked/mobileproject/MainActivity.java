package date.jhj.locked.mobileproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.DuplicateFormatFlagsException;
import java.util.List;
import java.util.Stack;
import java.util.Timer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    static MainActivity activity;
    static final int REQUEST_WRITE_FILE= 1000;
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

    AlertDialog dialog;
    View dialog_view;
    HistoryAdapter adapter;
    List<HistoryData> history_list=new ArrayList<>();
    RecyclerView listView;
    RecyclerView.LayoutManager mLayoutManager;
    Button complete;
    Button history_clear;
    boolean history_clear_check;
    Button save;
    // 파일 읽기
    ImageButton file;
    String fileAllStr;
    List<String> fileStr=new ArrayList<>();
    List<HistoryData> fileList=new ArrayList<>();
    // Backup
    String BACKUP;

    // 퀴즈 모드
    boolean quiz_flag;
    Button quiz_info;
    Button quiz_time;
//    Button quiz_score;
    Button quiz_stage;
    Button quiz_close;
    Button quiz_count;
    Button quiz_empty;
//    TextView quiz_;
    int quiz_num=0;
    int quiz_count_=0;

    long quiz_time_default;
    float quiz_score_print; // 점수

    //
    float DURING_DP=24;
    float RESULT_DP=24;
////////////////////////////////////////////////////
///// 수식
////////////////////////////////////////////////////
    String beforeResult;
    // 과정 텍스트
    TextView during;
    // 결과 값 텍스트
    TextView result_text;

    Timer mTimer;
    TimeTask timerTask;
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

        // 파일
        file = findViewById(R.id.file);
        file.setOnClickListener(this);
////////////////////////////////////////////////////
/////
/////  텍스트
/////
////////////////////////////////////////////////////
        during = findViewById(R.id.text_cal);
        result_text=findViewById(R.id.text_result);
        during.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        result_text.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        during.setMovementMethod(new ScrollingMovementMethod());// 스크롤 하기 위함
        result_text.setMovementMethod(new ScrollingMovementMethod());
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
        bu.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //확인버튼이랑 같은 기능
                history_clear_check=false;
                history_clear.setBackground(getDrawable(R.drawable.white_button));
                history_clear.setTextColor(getColor(R.color.colorAccent));
            }
        });
        dialog = bu.create();

        // xml파일을 dialog로 붙이기
        LayoutInflater inflater = getLayoutInflater();
        dialog_view = inflater.inflate(R.layout.history_dialog, null);
        listView=dialog_view.findViewById(R.id.listview);

        listView.addItemDecoration(new Divider(MainActivity.this));


        complete=dialog_view.findViewById(R.id.complete);
        complete.setOnClickListener(this);

        save=dialog_view.findViewById(R.id.history_save);
        save.setOnClickListener(this);

        history_clear=dialog_view.findViewById(R.id.history_clear);
        history_clear.setOnClickListener(this);

        adapter=new HistoryAdapter(history_list);
        adapter.setHasStableIds(false);

        adapter.notifyDataSetChanged();

        final ItemTouchHelper.SimpleCallback simpleItemTouchCallback =  new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN| ItemTouchHelper.START| ItemTouchHelper.END, ItemTouchHelper.START|ItemTouchHelper.END) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                Log.e("swipe :: ", " "+swipeDir); // << 16 // >> 32
                if(swipeDir==16) {// << 삭제
                    history_list.remove(viewHolder.getAdapterPosition());
                    // 백업
                    BACKUP = new Gson().toJson(history_list);
                    BackupData.setPrefHistory(MainActivity.this, BACKUP);

                    adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged(); // 숫자 변경떄문! 인덱스
                        }
                    }, 1000);
                }
                else if(swipeDir==32){// >> 복사
                    TextView calTV = viewHolder.itemView.findViewById(R.id.cal);
                    TextView resultTV = viewHolder.itemView.findViewById(R.id.result);
                    during.setText(calTV.getText().toString());
                    result_text.setText(resultTV.getText().toString());
                    //
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged(); // 숫자 변경떄문! 인덱스
                        }
                    }, 150);

                }
            }
            GradientDrawable background;
            Drawable xMark;

            @Override// 이걸로 ... 뒷 배경
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                if (viewHolder.getAdapterPosition() == -1) {
                    return;
                }

                background=new GradientDrawable();
                xMark=new GradientDrawable();
                background.setCornerRadius(60f);
                // dx 음수 == <<< 스와이프
                if(dX <0) {
                    background.setColor(getColor(R.color.colorAccent));
                    xMark = ContextCompat.getDrawable(MainActivity.this, R.drawable.backspace);
                    xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                    background.setBounds(itemView.getLeft() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    xMark.setBounds(itemView.getRight() - 80, itemView.getTop()+30, itemView.getRight() - 30, itemView.getTop() + 80);
                }else if(dX>0){// >>> 스와이프
                    background.setColor(getColor(R.color.result_button_up));
                    xMark = ContextCompat.getDrawable(MainActivity.this, R.drawable.copy);
                    xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                    background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getRight()+(int)dX, itemView.getBottom());
                    xMark.setBounds(itemView.getLeft() + 30, itemView.getTop()+30 , itemView.getLeft() + 80, itemView.getTop()+ 80);

                }
                background.draw(c);
                xMark.draw(c);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
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



        ///// 퀴즈
//        quiz_=findViewById(R.id.quiz_);
        // 퀴즈 모드 표시
        quiz_info= findViewById(R.id.quiz_info);
        // 퀴즈 시간
        quiz_time=findViewById(R.id.quiz_time);
        // 퀴즈 점수
//        quiz_score = findViewById(R.id.quiz_score);
        // 퀴즈 스테이지
        quiz_stage= findViewById(R.id.quiz_stage);
        // 퀴즈 카운트
        quiz_count =findViewById(R.id.quiz_count);
        //
        quiz_empty = findViewById(R.id.quiz_empty);
        // 퀴즈 종료
        quiz_close= findViewById(R.id.quiz_close);
        quiz_close.setOnClickListener(this);

        quizVisible(View.GONE);
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
                if(quiz_flag) {
                    if(during.getText().length()>0) {
                        char last = during.getText().toString().charAt(during.getText().length() - 1);
                        if (!Character.isDigit(last)&&last!='-')
                            during.setText("");
                    }
                }
                during.setTextColor(getColor(R.color.blacky));
                during.setText(during.getText() + String.valueOf(i));

            }
        }
        if(view == result_button){
            if(during.getText().length()>0) {
                if(quiz_flag){
                    if(quiz_num<fileList.size()) {
                        quiz_count.setText("도전\n" + (++quiz_count_));
                        // 퀴즈 모드일 때,
                        during.setTextColor(getColor(R.color.blacky));
                        // 눌렀을 때 답이 맞으면 넘어감
                        if (during.getText().toString().equals(fileList.get(quiz_num).result)) {
                            quiz_num++;
                            if (quiz_num >= fileList.size()) {
                                // 문제 끝났을떄
                                Log.e("문제 종료", " 시간 ::"+ " 점수 ::"+ "도전 횟수");
                                during.setText("");// 점수? 시간?
//                                quiz_.setVisibility(View.GONE);
                                result_text.setText("퀴즈 종료!\n수고하셨습니다.");
                                mTimer.cancel();

                                // 점수 == 100 - (도전 횟수-문제수)*5 and 문제당 5초 할당, 5초 초과마다 5점 감점.

                                int score_count=0;
                                int score_time=0;
                                if(fileList.size()<quiz_count_)
                                    score_count=quiz_count_-fileList.size();
                                if(timerTask.getTime()>fileList.size()*5)
                                    score_time=(timerTask.getTime()-(5*fileList.size()))/5;
                                quiz_score_print = 100 - (score_count)*5 - (score_time)*5;

                                if(quiz_score_print<0)
                                    quiz_score_print=0;
                                Log.e("스코어 ", "문제 점수 차감 : "+score_count+ " 문제 시간 차감 : "+score_time);
                                during.setText("점수 : "+quiz_score_print);
                                return;
                            }
                            quiz_stage.setText((1 + quiz_num) + "/" + fileList.size());
                            result_text.setText(fileList.get(quiz_num).cal);
                            during.setText("");
                        } else {// 답이 아니면
                            during.setTextColor(getColor(R.color.colorAccent));
                            during.setText("정답이 아닙니다");
                        }
                    }
                }else{
                    Cal c=new Cal();
                    String kk=" ";
                    kk = c.bracketCalMain(during.getText().toString());

                    beforeResult = during.getText().toString();

                    String sub_kk="";
                    Log.e("kk 테스트 ", "  "+kk);
                    if(kk.contains(".")&&!kk.contains("다")){// 소수점 이하가 0일때 출력안하기
                        sub_kk=kk.substring(kk.indexOf("."));
                        Log.e(" sub스트링 ::: ", " "+sub_kk);
                        if(Double.parseDouble(sub_kk)==0)
                            kk=kk.substring(0,kk.indexOf("."));
                    }
                    Log.e(" kkk ::: ", " "+kk);
                    // 숫자가 아니면 빨간색
                    if(kk.length()>0) {
                        if (Character.isDigit(kk.charAt(kk.length() - 1))) {
                            result_text.setTextColor(getColor(R.color.result_button_up));
                            // 괄호 추가인데 이미 괄호가 앞뒤로 있을떄는 추가안하기,  (가 2개 이상인지 검사 -> )보다 2번째 (가 앞에 있는지 검사 -> 맞으면 ( ) 안 씌움
//                            if(during.getText().toString().charAt(0)=='('&&during.getText().toString().charAt(during.getText().toString().length()-1)==')') {// 맨 앞과 맨 뒤가 괄호 이고,
//                                int start=during.getText().toString().indexOf("(",1);
//                                int end=during.getText().toString().indexOf(")");
//                                Log.e("흠,,", "( :: "+start+ "  ):::" +end);
//                                if(start!=-1&&start<end) {// 맨 앞뒤 괄호를 빼도 멀쩡할 경우
//                                    during.setText(during.getText());//
//                                }else if(start!=-1){
//                                    during.setText("(" + during.getText() + ")");//
//                                }
//                            }else{
//                                during.setText("(" + during.getText() + ")");//
//                            }

                            // ex) A = ((2)) + ((3)) 가 있을때,
                            // *2 를 하면 , 10이 되야함
                            // 괄호를 안 씌우면 7이 됨
                            // 즉, 식 A * 2 랑 (A) * 2 의 결과가 같으면 괄호를 안 씌우고, 다르면 괄호를 씌움
                            if(!c.bracketCalMain(beforeResult+"*2").equals(c.bracketCalMain("("+beforeResult+")*2"))){
                                during.setText("(" + during.getText() + ")");
                            }

                        }
                        else
                            result_text.setTextColor(getColor(R.color.colorAccent));
                        result_text.setText(kk);


                        if(history_list.size()>0) {
                            // 이전 히스토리랑 같지 않으면 저장.
                            if (!history_list.get(0).cal.equals(during.getText().toString())) {
                                // 50개 넘으면 하나 삭제.
                                if (history_list.size() >= 50)
                                    history_list.remove(history_list.size() - 1);
                                history_list.add(0, new HistoryData(during.getText().toString(), kk));
                                adapter.notifyDataSetChanged();
                            }
                        }else{
                            history_list.add(0, new HistoryData(during.getText().toString(), kk));
                            adapter.notifyDataSetChanged();
                        }
                        // 백업
                        BACKUP = new Gson().toJson(history_list);
                        BackupData.setPrefHistory(MainActivity.this, BACKUP);
                    }


                }


            }
        }
        else if(view == clear){// 글씨 초기화
            result_text.setText("");
            during.setText("");
            DURING_DP=RESULT_DP=24f;//textsize
        }
        else if(view == backspace){
            if(during.getText().length()>0)
                during.setText(during.getText().subSequence(0,during.getText().length()-1)); // 뒤에서 한자리씩 자르기
        }
        else if(view == cal[0]) {// 더하기
            checkCal("+");
        }
        else if(view == cal[1]){// 빼기
            if(quiz_flag&&during.getText().toString().contains("니")) {
                during.setText("");
                during.setTextColor(getColor(R.color.blacky));
            }
            checkCal("-");
        }
        else if(view == cal[2]){// 곱하기
            checkCal("*");
        }
        else if(view == cal[3]){// 나누기
            checkCal("/");
        }
        else if(view == dot){ //
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
            if(quiz_flag&&during.getText().toString().contains("니")) {
                during.setText("");
                during.setTextColor(getColor(R.color.blacky));
            }
            checkBracket();
        }
        else if(view == history){
            dialog.show();
        }
        else if(view == complete){
            dialog.dismiss();
            history_clear_check=false;
            history_clear.setBackground(getDrawable(R.drawable.white_button));
            history_clear.setTextColor(getColor(R.color.colorAccent));
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
        }
        else if(view == during|| view == result_text){
            clipBoard();
        }
        else if(view == file){
//            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//            i.setType("text/plain");  //여러가지 Type은 아래 표로 정리해두었습니다.
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(i.createChooser(i,"파일 탐색기 선택"));
            Intent intent = new Intent();
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/plain");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "파일 탐색기 선택"), 82);


        }
        ///
        else if(view == quiz_close){
            quizVisible(View.GONE);
            result_text.setText("");
            during.setText("");
            mTimer.cancel();
            quiz_flag=false;
        }
        else if(view == save){
            if(checkPermission()) {
                writeFile();
                Toast.makeText(this,"파일이 생성되었습니다.",Toast.LENGTH_LONG).show();
            }else{
                new AlertDialog.Builder(this)
                        .setTitle("권한 설정 알림")
                        .setMessage("환경 설정에서 [권한 - 저장공간] 을 활성화 시켜주세요. ")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .create()
                        .show();
//                Toast.makeText(this,"권한이 없습니다.",Toast.LENGTH_LONG).show();
            }
        }
        else if(view == history_clear){
            if(!history_clear_check) {
                history_clear.setBackground(getDrawable(R.drawable.delete_button));
                history_clear.setTextColor(getColor(R.color.white_));
            }else{
                history_list.clear();
                adapter.notifyDataSetChanged();
                // 백업
                BACKUP = new Gson().toJson(history_list);
                BackupData.setPrefHistory(MainActivity.this, BACKUP);

                history_clear.setBackground(getDrawable(R.drawable.white_button));
                history_clear.setTextColor(getColor(R.color.colorAccent));
            }

            history_clear_check = !history_clear_check;
        }

/////////////////////////////////////
/////////////////////////////////////
        if(during.getText().length()>0){
            backspace.setImageDrawable(getDrawable(R.drawable.backspace));
            backspace.setEnabled(true);

        }else {
            backspace.setImageDrawable(getDrawable(R.drawable.backspace_white));
            backspace.setEnabled(false);
        }
        scrollBottom(during);
        scrollBottom(result_text);
    }
    public void scrollBottom(TextView textView) {
        int lineTop =  textView.getLayout().getLineTop(textView.getLineCount()) ;
        int scrollY = lineTop - textView.getHeight();
        if (scrollY > 0) {
            textView.scrollTo(0, scrollY);
        } else {
            textView.scrollTo(0, 0);
        }
    }

    public void checkBracket(){//
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            } else {
                fileList.clear();
                fileStr.clear();
                Uri uri = data.getData();

                fileAllStr = readTextFile(uri);
                Log.e("TEXT", fileAllStr);
                // 파일 읽고나서
                String[] divStr=fileAllStr.split("\n");
                for(String s:divStr)
                    fileStr.add(s);
                // 파일이 한줄이면 == 불러와서 during에 입력  ==> 문제
                if(fileStr.size()==1 && !fileStr.get(0).contains("=")){
                    during.setText(fileStr.get(0));
                }
                // 여러 줄이면 퀴즈로???
                else{
                    boolean check_quiz=true;
                    // 문제 검증
                    for(String s:fileStr) {
                        if(!s.contains("="))
                            check_quiz=false;
                        // 에러 예외 처리
                    }

                    if(check_quiz) {
                        // 문제를 저장
                        for (String s : fileStr) {
                            String left, right;
                            left=s.substring(0,s.indexOf("="));
                            right=s.substring(s.indexOf("=")+1);
                            Log.e("레프트 라이트", " l:"+left+ "  r:"+right);
                            if(Character.isDigit(right.charAt(right.length()-1))) {
                                if(Double.parseDouble(right)<0)
                                    right="("+right+")";
                                fileList.add(new HistoryData(left, right));
                            }
                        }
                        // 셔플
                        Collections.shuffle(fileList);

                        // 퀴즈 모드 온
                        quiz_flag = true;
                        quiz_num=quiz_count_=0;
                        quiz_time_default=System.currentTimeMillis();
                        // 퀴즈 VISIBLE 로 바꾸고
                        quizVisible(View.VISIBLE);
                        timerTask = new TimeTask(quiz_time,quiz_time_default);
                        mTimer = new Timer();
                        mTimer.schedule(timerTask, 500, 100);
                        during.setText("");
                        result_text.setText(fileList.get(quiz_num).cal);
                        quiz_stage.setText((quiz_num+1)+"/"+fileList.size());

                        quiz_count.setText("도전\n"+quiz_count_);

                    }
                }
            }
        } else {
            return;
        }
    }
    public void writeFile(){
        // 파일 생성
        File saveFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/txt"); // 저장 경로
// 폴더 생성
        if(!saveFile.exists()){ // 폴더 없을 경우
            saveFile.mkdir(); // 폴더 생성
        }
        try {
            long now = System.currentTimeMillis(); // 현재시간 받아오기
            Date date = new Date(now); // Date 객체 생성
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String nowTime = sdf.format(date);

            FileOutputStream fos = new FileOutputStream(saveFile.getPath()+"/"+nowTime+".txt", true);
            //파일쓰기
            BufferedWriter buf = new BufferedWriter(new OutputStreamWriter(fos,"MS949"));


            for(HistoryData hd:history_list) {
                buf.append(hd.cal+"="+hd.result); // 파일 쓰기
                buf.newLine(); // 개행
            }
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public String readTextFile(Uri uri){
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri), "MS949")); // 한글 읽기 위해서. ( 소스파일이 MS949 형식)
            String line = "";

            while ((line = reader.readLine()) != null) {
                builder.append(line+"\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }

    public void quizVisible(int visible){
        // 퀴즈 문제
//        quiz_.setVisibility(visible);
        // 퀴즈 배너
        quiz_info.setVisibility(visible);
        // 퀴즈 시간
        quiz_time.setVisibility(visible);
        // 퀴즈 점수
//        quiz_score.setVisibility(visible);
        // 퀴즈 스테이지
        quiz_stage.setVisibility(visible);
        // 퀴즈 카운트
        quiz_count.setVisibility(visible);
        //
        quiz_empty.setVisibility(visible);
        // 퀴즈 종료
        quiz_close.setVisibility(visible);
    }
    public boolean checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 다시 보지 않기 버튼을 만드려면 이 부분에 바로 요청을 하도록 하면 됨 (아래 else{..} 부분 제거)
            // ActivityCompat.requestPermissions((Activity)mContext, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_CAMERA);
            // 처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{..}의 요청으로 넘어감
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.e("권한 위 조건문", " 22");

                return false;
            }
            else {
                Log.e("권한 위 조건문", " 33");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_FILE);
                return true;
            }
        }

        Log.e("권한 위 조건문", " 44");
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_FILE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("권한 허가", " 상태  ok");

                } else {
                    Log.e("권한 허가", " 상태  no");
                }
                // 허용했다면 이 부분에서..

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPermission();
    }
}

// TODO histroy - > file 로 저장
// TODO 퀴즈 만들기
/*
    1. 파일에서 입력 ( 형식 : 1 + 2 = 3  // 좌항 우항 나눠서)
    2. 랜덤하게 한문제씩 화면에 출력 ( 중복 x // 문제 수만큼 인트 배열, 랜덤으로 섞기 = Collections.Shuffle(list) )
    3. 정답인지 확인 ( 점수? , 정답률 )
    4. 문제 풀이 시간 저장 ( 각문제 ?, 전체  합)

 */