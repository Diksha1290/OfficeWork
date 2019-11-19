package com.officework.utils.SMS;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.officework.R;


public class SendSms extends AppCompatActivity implements View.OnClickListener {

    EditText txtNumber, txtMessage;
    Button btnSend;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);

     /*   txtNumber = (EditText) findViewById(R.id.txtNumber);
        txtMessage = (EditText) findViewById(R.id.txtMesssage);
        btnSend = (Button) findViewById(R.id.btnSMS);

        // Attached Click Listener
        btnSend.setOnClickListener(this);*/
    }

    @Override
    public void onClick(View v) {
        /*if (v == btnSend) {

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(txtNumber.getText().toString(), null,txtMessage.getText().toString(), null, null);
            Toast.makeText(this, "Message sent successfully", Toast.LENGTH_LONG)
                    .show();

        }*/
    }
}
