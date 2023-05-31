/* **
package com.tistory.webnautes.phptest;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private static String IP_ADDRESS = "192.168.35.63";
    private static String TAG = "phptest";

    private EditText mEditTextName;
    private EditText mEditTextCountry;
    private TextView mTextViewResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditTextName = (EditText)findViewById(R.id.editText_main_name);
        mEditTextCountry = (EditText)findViewById(R.id.editText_main_country);
        mTextViewResult = (TextView)findViewById(R.id.textView_main_result);

        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());


        Button buttonInsert = (Button)findViewById(R.id.button_main_insert);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String time = mEditTextName.getText().toString();
                String habit = mEditTextCountry.getText().toString();

                InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "/create.php", time,habit);


                mEditTextName.setText("");
                mEditTextCountry.setText("");

            }
        });

    }



    class InsertData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String time = (String)params[1];
            String habit = (String)params[2];

            String serverURL = (String)params[0];
            String postParameters = "time=" + time + "&habit=" + habit;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }


}

 */
package com.tistory.webnautes.phptest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.method.ScrollingMovementMethod;
import android.widget.EditText;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //화면 켰을 때의 현재 시각을 출력하기 위함
    private static String IP_ADDRESS = "192.168.35.63";
    private static String TAG = "phptest";

    private EditText mEditTextName;
    private EditText mEditTextCountry;
    private TextView mTextViewResult;



    Chronometer chronometer;

    TextView mSplit0;
    TextView mSplit1;
    TextView mSplit2;
    TextView mSplit3;

    private EditText mchronometer;

    Button markBtn;
    LinearLayout resultLayout;
    long stopTime = 0;
    int num = 1;
    int time;   //전체 사용 시간
    int time2;  //한번 사용 시간
    int mSplitCount;
    String starttime;
    String endtime;
    Date startDate;
    Date endDate;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    long screen_on_minute;
    long screen_on_second;


    //현재 시간 출력 텍스트
    private TextView Show_Time_TextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chronometer = (Chronometer) findViewById(R.id.chronometer);
        chronometer.setBase(SystemClock.elapsedRealtime() + stopTime);
        chronometer.start();





        //화면 켠 시간 출력
        mSplit0 = (TextView)findViewById(R.id.split0);
        mSplit1 = (TextView)findViewById(R.id.split1);
        mSplit2 = (TextView)findViewById(R.id.split2);
        mSplit3 = (TextView)findViewById(R.id.split3);

        //현재 시간 출력
        Show_Time_TextView = (TextView) findViewById(R.id.Date);
        ShowTimeMethod();

        String sSplit0 = mSplit0.getText().toString();
        String sSplit1 = mSplit1.getText().toString();

        sSplit0 += String.format("[%d]", mSplitCount);
        sSplit1 += String.format("%s\n", getEllapse());

        mSplit0.setText(sSplit0);
        mSplit1.setText(sSplit1);

        mSplitCount++;



        starttime= sdf.format(new Date(System.currentTimeMillis()));
        try {
            startDate = sdf.parse(starttime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //starttime = System.currentTimeMillis();


    }

    public void ShowTimeMethod() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Show_Time_TextView.setText(DateFormat.getDateTimeInstance().
                        format(new Date()));
            }
        };
        Runnable task = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {}
                    handler.sendEmptyMessage(1);
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }


    BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                chronometer.setBase(SystemClock.elapsedRealtime() + stopTime);
                chronometer.start();


                starttime= sdf.format(new Date(System.currentTimeMillis()));
                try {
                    startDate = sdf.parse(starttime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                String sSplit0 = mSplit0.getText().toString();
                String sSplit1 = mSplit1.getText().toString();



                //+연산자로 이어붙임
                sSplit0 += String.format("[%d]", mSplitCount);
                sSplit1 += String.format("%s\n", getEllapse());




                //텍스트뷰의 값을 바꿔줌
                mSplit0.setText(sSplit0);
                mSplit1.setText(sSplit1);


                mSplitCount++;

            }
            else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                stopTime = (chronometer.getBase() - SystemClock.elapsedRealtime());
                chronometer.stop();
                time = (int) (-1 * stopTime / 1000);
                String time_s = String.valueOf(time);
                endtime= sdf.format(new Date(System.currentTimeMillis()));
                try {
                    endDate = sdf.parse(endtime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long diff = endDate.getTime() - startDate.getTime();
                time2 = (int) diff/1000;
                String time2_s = String.valueOf(time2);
                String screen_using_time = String.format("%02d:%02d", diff / 1000 / 60, diff / 1000 % 60);
                String sSplit2 = mSplit2.getText().toString();
                String sSplit3 = mSplit3.getText().toString();
                sSplit2 += String.format("%s\n", getEllapse());
                sSplit3 += String.format("%s\n", screen_using_time);
                mSplit2.setText(sSplit2);
                mSplit3.setText(sSplit3);
                int habit_s = 0;
                if (time2 <5) {
                    habit_s = 1;
                }
                if (time2>5){
                    habit_s = 2;
                }
                //대충 서버에 데이터를 전송하는 코드
                String time = chronometer.getText().toString();
                String habit = String.format("%d", habit_s);
                String created = Show_Time_TextView.getText().toString();
                //String habit = chronometer.getText().toString();
                InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "/create.php", time,habit, created);

            }
        }
    };
    class InsertData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String time = (String)params[1];
            String habit = (String)params[2];
            String created = (String)params[3];

            String serverURL = (String)params[0];
            String postParameters = "time=" + time + "&habit=" + habit + "created=" + created;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);
                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                return sb.toString();
            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                return new String("Error: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume","Resume!");
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(br,filter);
    }


    String getEllapse(){

        //time11 = (int) (stopTime / 1000);
        //long now = SystemClock.elapsedRealtime();
        //long ell = now - mBaseTime;//현재 시간과 지난 시간을 빼서 ell값을 구하고
        //아래에서 포맷을 예쁘게 바꾼다음 리턴해준다.
        //String sEll = String.format("%02d:%02d:%02d", -1 * stopTime / 1000 / 60, (-1 * stopTime/1000)%60, (-1 * stopTime%1000)/10);
        // sdf = new SimpleDateFormat("HH:mm:ss");
        String time= sdf.format(new Date(System.currentTimeMillis()));
        return time;
    }


}