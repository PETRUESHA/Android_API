package com.example.android_api;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.style.MaskFilterSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private static final String base_url = "http://numbersapi.com/";
    private Button btn;
    private TextView out_text;
    private EditText input_number;
    private Handler h;
    private int number;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.button);
        out_text = findViewById(R.id.textView);
        input_number = findViewById(R.id.editTextTextPersonName);
        h = new Handler(Looper.getMainLooper()) {
            @SuppressLint("SetTextI18n")
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 200) {
                    out_text.setText((Integer) bundle.get("key"));
                }
                else {
                    out_text.setText("Something wrong. Sorry.");
                }
            }
        };

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        number = Integer.parseInt(input_number.getText().toString());
                        String type = "/trivia";
                        String def = "Boring number. No facts.";
                        String url = base_url + number + type + "?json";

                        HttpURLConnection connection;

                        try {
                            URL u = new URL(url);
                            connection = (HttpURLConnection) u.openConnection();
                            connection.setRequestMethod("GET");
                            connection.setConnectTimeout(10000);
                            connection.connect();
                            int status = connection.getResponseCode();
                            Log.d("STATS", Integer.toString(status));
                            Log.d("url", url);

                            ArrayList<String> lines = new ArrayList<>();

                            if (status == 200) {
                                Scanner sc = new Scanner(connection.getInputStream());
                                while (sc.hasNext()) {
                                    lines.add(sc.nextLine());
                                }
                            }

                            String savePath = "D:\\Projects\\AndroidStudioProject\\Android_API\\app\\src\\main\\java\\com\\example\\android_api\\result";

                            //Path path = Paths.get(savePath);
                            //Files.write(path, lines);

                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < lines.size(); i++) {
                                stringBuilder.append(lines.get(i));
                            }

                            Gson gson = new Gson();

                            Result result = gson.fromJson(stringBuilder.toString(), Result.class);

                            h.sendEmptyMessage(status);
                            Message message = new Message();
                            bundle.putString("key", result.getText());
                            message.setData(bundle);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });
    }
}