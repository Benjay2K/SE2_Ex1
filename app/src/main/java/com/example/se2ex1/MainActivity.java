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

        EditText inputField = findViewById(R.id.input);
        TextView responseText = findViewById(R.id.result);

        Button sendBtn = findViewById(R.id.sendButton);
        Button sortBtn = findViewById(R.id.sortButton);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
    }

    private static class NetworkThread extends Thread {
        String studentNumber;
        String response;

        NetworkThread(String studentNumber) {
            this.studentNumber = studentNumber;
            this.response = "None";
        }

        public void run() {

            try {
                Socket socket = new Socket("se2-isys.aau.at", 53212);

                DataOutputStream sendChannel = new DataOutputStream(socket.getOutputStream());
                BufferedReader receiveChannel = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                sendChannel.writeBytes(studentNumber + '\n');
                response = receiveChannel.readLine();

                socket.close();

            } catch (IOException e) {
                this.response = "Failed";
            }
        }

    }
}