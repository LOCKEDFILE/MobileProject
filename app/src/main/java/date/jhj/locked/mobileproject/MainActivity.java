package date.jhj.locked.mobileproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.DuplicateFormatFlagsException;
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

    AlertDialog dialog;
    View dialog_view;
    HistoryAdapter adapter;
    List<HistoryData> history_list=new ArrayList<>();
    RecyclerView listView;
    RecyclerView.LayoutManager mLayoutManager;
    Button complete;
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
    Button quiz_score;
    Button quiz_stage;
    Button quiz_close;
    Button quiz_count;
    TextView quiz_;
    int quiz_num=0;
    int quiz_count_=0;

    long quiz_time1,quiz_time2;

    //
    float DURING_DP=24;
    float RESULT_DP=24;
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

        Log.e("높이 값 ", " "+during.getMeasuredHeight()+ "  ,d  ::"+ result_text.getMeasuredHeight()); // 57 57

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
        quiz_=findViewById(R.id.quiz_);
        // 퀴즈 모드 표시
        quiz_info= findViewById(R.id.quiz_info);
        // 퀴즈 시간
        quiz_time=findViewById(R.id.quiz_time);
        // 퀴즈 점수
        quiz_score = findViewById(R.id.quiz_score);
        // 퀴즈 스테이지
        quiz_stage= findViewById(R.id.quiz_stage);
        // 퀴즈 카운트
        quiz_count =findViewById(R.id.quiz_count);
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
                        if (!Character.isDigit(last))
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
                    quiz_count.setText("도전\n"+(++quiz_count_));
                    // 퀴즈 모드일 때,
                    during.setTextColor(getColor(R.color.blacky));
                    // 눌렀을 때 답이 맞으면 넘어감
                    if(during.getText().toString().equals(fileList.get(quiz_num).result)){
                        quiz_num++;
                        if(quiz_num>=fileList.size()){
                            return;
                        }
                        quiz_stage.setText((1+quiz_num)+"/"+fileList.size());
                        result_text.setText(fileList.get(quiz_num).cal);
                        during.setText("");
                    }else{// 답이 아니면
                        during.setTextColor(getColor(R.color.colorAccent));
                        during.setText("정답이 아닙니다");
                    }

                }else{
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

                        during.setText("("+during.getText()+")");//
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
            quiz_flag=false;
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
                    }

                    if(check_quiz) {
                        // 문제를 저장
                        for (String s : fileStr) {
                            String left, right;
                            left=s.substring(0,s.indexOf("="));
                            right=s.substring(s.indexOf("=")+1);
                            Log.e("레프트 라이트", " l:"+left+ "  r:"+right);
                            fileList.add(new HistoryData(left, right));
                        }
                        // 셔플
                        Collections.shuffle(fileList);

                        // 퀴즈 모드 온
                        quiz_flag = true;
                        quiz_num=quiz_count_=0;
                        quiz_time1=System.currentTimeMillis();
                        // 퀴즈 VISIBLE 로 바꾸고
                        quizVisible(View.VISIBLE);
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

    private String readTextFile(Uri uri){
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
        quiz_.setVisibility(visible);
        // 퀴즈 배너
        quiz_info.setVisibility(visible);
        // 퀴즈 시간
        quiz_time.setVisibility(visible);
        // 퀴즈 점수
        quiz_score.setVisibility(visible);
        // 퀴즈 스테이지
        quiz_stage.setVisibility(visible);
        // 퀴즈 카운트
        quiz_count.setVisibility(visible);
        // 퀴즈 종료
        quiz_close.setVisibility(visible);
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