package date.jhj.locked.mobileproject;

import android.util.Log;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Queue;


public class Cal {
    boolean div_flag;

    public String bracketCalMain(String sNumList){
        //0. ( ) 괄호 갯수 검증
        if (!checkBracket(sNumList)) return "괄호의 갯수가 맞지 않습니다.";

        // 1. 1차 문자열 Parsing
        Queue firstQueue;
        firstQueue = stringParsing(sNumList);
        // 2. 2차 괄호안 계산
        Queue secondQueue = new LinkedList();
        String oneChar;
        while(firstQueue.peek() != null){
            oneChar  = firstQueue.poll().toString();
            if ("()".contains(oneChar)){
                //괄호안 계산 (firstQueue)
                secondQueue.offer(braketInCal(firstQueue));
            }else{
                secondQueue.offer(oneChar);
            }
        }
        secondQueue= bitCal(secondQueue);
        Log.e("bit 변경 후", " "+secondQueue);
        div_flag=false;
        //3. Queue에 저장 된 값 중  곱셈. 나눗셈 계산
        secondQueue = multiplyDivideCal(secondQueue);

        Log.e("곱 나누기 변경 후", " "+secondQueue);
        if(div_flag)
            return "0으로 나눌 수 없습니다.";
        //4. Queue에 저장 된 값 중  덧셈. 뺄샘 계산
        String sResult = addSubtractCal(secondQueue);
        Log.e("덧셈 변경 후", " "+sResult);
        return sResult;
    }

    //( ) 괄호 갯수 검증
    public boolean checkBracket(String sNumList){
        int count1=0, count2=0;
        for(int i=0; i<sNumList.length(); i++){
            if ("(".equals(sNumList.substring(i, i+1))){
                count1 ++;
            }else if (")".equals(sNumList.substring(i, i+1))){
                count2 ++;
            }
        }

        if (count1 == count2)
            return true;
        else{
            return false;
        }
    }

    //------------------------------------------------------------------------
    // 1차. 문자열 Parsing
    //------------------------------------------------------------------------
    public Queue stringParsing(String sNumList){
        String sOneNum = "";  //하나의 숫자열
        Queue firstQueue = new LinkedList();
        for(int i=0; i<sNumList.length(); i++){
            String oneChar = sNumList.substring(i, i+1);
            if ("+-*/()%&|~^".indexOf(oneChar) >= 0 ){// TODO 여기에 % 랑 비트연산자 추가

                // 3+(3  과 같이   4칙연산자와 괄호가 동시에 존재 하는 경우 때문
                if (!"".equals(sOneNum)) firstQueue.offer(sOneNum);
                firstQueue.offer(oneChar);
                sOneNum = "";
            }else{
                sOneNum += oneChar;

                if (i+1 == sNumList.length()){
                    firstQueue.offer(sOneNum);
                }
            }
        }
        return firstQueue;
    }

    public Queue bitCal(Queue firstQueue){
        Queue secondQueue = new LinkedList();
        Queue thirdQueue =new LinkedList();
        String oneChar;
        String preChar="";
        String nextChar="";
        // 낫 연산자 먼저 다 돌고..
        while(firstQueue.peek() != null){
            oneChar  = firstQueue.poll().toString();
            if ("~".equals(oneChar)){
                if(firstQueue.size()>0) {
                    nextChar = firstQueue.poll().toString();
                    if (nextChar.equals("~")) // not 연산자 중복 해결
                        continue;

                    int nextInt;
                    if(nextChar.equals("-")){
                        nextChar = firstQueue.poll().toString();
                        nextInt = (int) Double.parseDouble(nextChar);
                        nextInt = ~(-nextInt);
                    }else {
                        nextInt = (int) Double.parseDouble(nextChar);
                        nextInt = ~nextInt;

                    }
                    secondQueue.offer(nextInt + "");

                }
            }else
                secondQueue.offer(oneChar);
        }
        while(secondQueue.peek()!=null){
            // 하나씩 꺼냄
            oneChar = secondQueue.poll().toString();
            // 꺼낸게 비트 연산자가 아니면 third에 저장
            if(!("^&|".contains(oneChar))){
                thirdQueue.offer(oneChar);
            }
            else{
                // 비트연산자면 third에서 하나 꺼내고. second에서 하나꺼내서 연산 하고 다시 third로 넣음
                if(secondQueue.peek()!=null) {
                    preChar =((LinkedList) thirdQueue).getLast().toString(); // 스택으로 변경?
                    ((LinkedList) thirdQueue).removeLast();
                    nextChar = secondQueue.poll().toString();
                    int preInt, nextInt;
                    preInt = (int) Double.parseDouble(preChar);
                    nextInt = (int) Double.parseDouble(nextChar);
                    switch (oneChar) {
                        case "&":
                            nextInt = preInt & nextInt;
                            break;
                        case "|":
                            nextInt = preInt | nextInt;
                            break;
                        case "^":
                            nextInt = preInt ^ nextInt;
                            break;
                    }
                    thirdQueue.offer(nextInt + "");
                }
            }

        }
        return thirdQueue;
    }




    //------------------------------------------------------------------------
    // 2차. 괄호 내부 계산 (재귀적 호출)
    //------------------------------------------------------------------------
    public String braketInCal(Queue firstQueue){
        String oneChar;
        //1. 괄호 내부에 존재 하는 값을 Queue에 저장
        Queue secondQueue = new LinkedList();
        while(firstQueue.peek() != null){
            oneChar  = firstQueue.poll().toString();
            if ("(".equals(oneChar)){
                //괄호안 계산 (firstQueue)
                secondQueue.offer(braketInCal(firstQueue));
            }else if (")".equals(oneChar)){
                break; //닫기 괄호가 나오면 stop
            }else{
                secondQueue.offer(oneChar);
            }
        }
        secondQueue= bitCal(secondQueue);
        Log.e("내부 bit 변경 후", " "+secondQueue);
        //2. Queue에 저장 된 값 중  곱셈. 나눗셈 계산
        secondQueue = multiplyDivideCal(secondQueue);
        Log.e("내부 곱 변경 후", " "+secondQueue);
        if(div_flag)
            return "0으로 나눌 수 없습니다.";
        //3. Queue에 저장 된 값 중  덧셈. 뺄샘 계산
        String sResult = addSubtractCal(secondQueue);
        Log.e("내부 합 변경 후", " "+sResult);
        return sResult;
    }


    //------------------------------------------------------------------------
    // 곱셈, 나눗셈, 나머지 계산
    //------------------------------------------------------------------------
    public Queue multiplyDivideCal(Queue inQueue){
        Queue outQueue = new LinkedList();
        String oneChar;
        String sNum1 = "", sNum2 = "", sResult = "";
        String operator = "";
        BigDecimal nResult = new BigDecimal("0");
        while(inQueue.peek() != null){
            oneChar  = inQueue.poll().toString();

            if ("+-".contains(oneChar)){
                if(!sNum1.equals(""))
                    outQueue.offer(sNum1);
                outQueue.offer(oneChar);
                sNum1 = "";
            }else if ("*/%&|~^".contains(oneChar)) {
                operator = oneChar;
            }else{
                if ("".equals(sNum1)){
                    sNum1 = oneChar;
                }else if ("".equals(sNum2)) {
                    sNum2 = oneChar;
                    if ("*".equals(operator)) {
                        nResult = (new BigDecimal(sNum1)).multiply(new BigDecimal(sNum2));
                    }else if ("/".equals(operator)) {
                        if ("0".equals(sNum2)){
                            div_flag=true;
                            return null;
                        }

                        nResult = (new BigDecimal(sNum1)).divide(new BigDecimal(sNum2), 6, BigDecimal.ROUND_UP);
                    }
                    else if("%".equals(operator)){
                        nResult = (new BigDecimal(sNum1)).remainder(new BigDecimal(sNum2));
                    }


                    sResult  = String.valueOf(nResult);


                    sNum1    = sResult;
                    sNum2    = "";
                    operator = "";
                }
            }

            if (inQueue.peek() == null) outQueue.offer(sNum1);
        }
        return outQueue;
    }


    //------------------------------------------------------------------------
    // 덧셈. 뺄샘 계산
    //------------------------------------------------------------------------
    public String addSubtractCal(Queue inQueue){
        if(inQueue.peek()==null)
            return "";
        String operator = "";
        String oneChar  = "";
        Queue plus=new LinkedList();
        // - 있으면 음수로 교체
        while(inQueue.peek()!=null){
            oneChar = inQueue.poll().toString();
            if(oneChar.equals("-")){
                String minusValue=inQueue.poll().toString();
                plus.offer("+");
                if(minusValue.contains("-")) {
                    minusValue=minusValue.trim().replace("-","");
                    plus.offer(minusValue);
                }
                else
                    plus.offer("-"+minusValue);
            }else
                plus.offer(oneChar);
        }
        Log.e("PLUS :: " , " "+plus+ " ,   dd::"+(-~7)+" ddd: "+(~-7));
        // 이후 덧셈
        BigDecimal nResult;
        if(plus.peek().equals("+"))
            nResult= new BigDecimal("0");
        else {
            String insert=plus.peek().toString();
            nResult = new BigDecimal(insert);
        }

        while (plus.peek() != null) {
            oneChar = plus.poll().toString();
            if ("+-".contains(oneChar)) {
                operator = oneChar;
            } else {
                if (operator.equals("+")) {
                    nResult = nResult.add(new BigDecimal(oneChar));
                }
                operator = "";
            }
        }
        return nResult.toString();
    }
}