package com.example.se2ex1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //find components by id
        EditText inputField = findViewById(R.id.input);
        TextView responseText = findViewById(R.id.result);

        Button sendBtn = findViewById(R.id.sendButton);
        Button sortBtn = findViewById(R.id.sortButton);

        //action listener for send button
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save getText toString in String input, create new NetworkThread class and start
                String input = inputField.getText().toString();
                NetworkThread n = new NetworkThread(input);
                n.start();

                try {
                    n.join();
                } catch (InterruptedException e) {
                    Log.d("custom", e.getMessage());

                }

                responseText.setText(n.response);
            }
        });

        //action listener for sort button
        sortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = inputField.getText().toString();

                SortingThread c = new SortingThread(input);
                c.start();

                try {
                    c.join();
                } catch (InterruptedException e) {
                    Log.d("custom", e.getMessage());

                }

                responseText.setText(c.response);
            }
        });
    }

    //NetworkThread for send button
    private static class NetworkThread extends Thread {
        String studentNumber;
        String response;

        NetworkThread(String studentNumber) {
            this.studentNumber = studentNumber;
            this.response = "None";
        }

        //initialized by n.start(); changes not possible in main thread (android)
        public void run() {

            try {
                //create tcp socket
                Socket socket = new Socket("se2-isys.aau.at", 53212);

                DataOutputStream sendChannel = new DataOutputStream(socket.getOutputStream());
                BufferedReader receiveChannel = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                sendChannel.writeBytes(studentNumber + '\n');
                response = receiveChannel.readLine();

                socket.close();

                //try/catch necessary for sockets
            } catch (IOException e) {
                this.response = "Failed";
            }
        }

    }

    public static class SortingThread extends Thread{
        String studentNumber;
        String response;

        SortingThread(String studentNumber){
            this.studentNumber = studentNumber;
            this.response = "None";
        }

        //sorting method
        public void run(){
            StringBuilder result = new StringBuilder();
            //sort prim digits
            outer:
            for (char c : studentNumber.toCharArray()) {
                int n = Character.getNumericValue(c);

                if (n <= 1) continue;
                for (int i = 2; i < n; i++) {
                    if (n % i == 0) continue outer;
                }
                result.append(c);
            }
            response = result.toString();
        }
    }
}