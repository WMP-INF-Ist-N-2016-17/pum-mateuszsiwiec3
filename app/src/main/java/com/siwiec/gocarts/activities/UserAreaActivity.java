package com.siwiec.gocarts.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.siwiec.gocarts.R;
import com.siwiec.gocarts.httpClasses.HttpParse;

import java.util.HashMap;

public class UserAreaActivity extends AppCompatActivity {

    TextView tvWelcomeMsg;
    Button addOrderBtn, showOrders;
    ProgressDialog progressDialog;
    HashMap<String, String> hashMap = new HashMap<>();
    String finalResult;
    HttpParse httpParse = new HttpParse();
    String HttpURL = "http://gocarts.000webhostapp.com/AddOrder.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String surname = intent.getStringExtra("surname");
        tvWelcomeMsg = (TextView) findViewById(R.id.tvWelcomeMsg);

        String message = "Witaj " + name + " " + surname;
        tvWelcomeMsg.setText(message);

        addOrderBtn = (Button) findViewById(R.id.addOrderBtn);
        showOrders = (Button) findViewById(R.id.buttonShow);

        final String userId = intent.getStringExtra("id");

        addOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHowToDialog(userId);
            }
        });

        showOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(UserAreaActivity.this, ShowAllOrdersActivity.class);
                intent.putExtra("id", userId.toString());
                UserAreaActivity.this.startActivity(intent);

            }
        });
    }

    private void openHowToDialog(final String userId) {
        new AlertDialog.Builder(this).setTitle("Jak zarezerwować").setMessage("Po tej wiadomości otrzymasz listę wyborów twojej rezerwacji:" +
                " tor, liczbę uczestników oraz datę.").setCancelable(false)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openTrackSelectionDialog(userId);
                    }
                }).show();
    }

    private void openTrackSelectionDialog(final String userId) {
        new AlertDialog.Builder(this).setTitle("Wybierz tor").setItems(R.array.tracks, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String trackType;
                if (which == 0) {
                    trackType = "Trudny";
                } else if (which == 1) {
                    trackType = "Średni";
                } else {
                    trackType = "łatwy";
                }
                openPlayerSelectionDialog(userId, trackType);
            }
        }).show();
    }

    final CharSequence[] numberOFParticipantsArray = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

    private void openPlayerSelectionDialog(final String userId, final String trackType) {
        new AlertDialog.Builder(this).setTitle("Wybierz liczbę uczestników").setItems(numberOFParticipantsArray, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String number;
                if (which == 0)
                    number = "1";
                else if (which == 1)
                    number = "2";
                else if (which == 2)
                    number = "3";
                else if (which == 3)
                    number = "4";
                else if (which == 4)
                    number = "5";
                else if (which == 5)
                    number = "6";
                else if (which == 6)
                    number = "7";
                else if (which == 7)
                    number = "8";
                else if (which == 8)
                    number = "9";
                else
                    number = "10";
                openDatePickerSelectionDialog(userId, trackType, number);
            }
        }).show();

    }

    private void openDatePickerSelectionDialog(final String userId, final String trackType, final String number) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final DatePicker picker = new DatePicker(this);
        picker.setCalendarViewShown(false);

        builder.setTitle("Wybierz datę");
        builder.setView(picker);
        builder.setPositiveButton("Ustaw", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                int month = picker.getMonth() + 1;
                String currentDateString = String.valueOf(picker.getDayOfMonth() + "." + month + "." + picker.getYear());
                openTimePickerSelectionDialog(userId, trackType, number, currentDateString);
            }
        });
        builder.show();
    }

    final CharSequence[] durationInMinutes = {"10", "20", "30", "40", "50", "60"};



    private void openTimePickerSelectionDialog(final String userId, final String trackType, final String number, final String currentDateString) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final TimePicker picker = new TimePicker(this);

        builder.setTitle("Wybierz godzinę");
        builder.setView(picker);
        builder.setPositiveButton("Ustaw", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String startDate = currentDateString + " " + picker.getCurrentHour() + ":" + picker.getCurrentMinute();
                openDurationSelection(userId , trackType,number,startDate);

            }
        });
        builder.show();
    }

    private void openDurationSelection(final String userId, final String trackType, final String number, final String startDate) {
        new AlertDialog.Builder(this).setTitle("Wybierz czas jazdy").setItems(durationInMinutes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String duration;
                if (which == 0)
                    duration = "10";
                else if (which == 1)
                    duration = "20";
                else if (which == 2)
                    duration = "30";
                else if (which == 3)
                    duration = "40";
                else if (which == 4)
                    duration = "50";
                else
                    duration = "60";

                Integer intNumber = Integer.valueOf(number);
                Integer intDuration = Integer.valueOf(duration);
                Integer price = intDuration * intNumber;
                String stringPrice = String.valueOf(price);


                addOrder(userId, stringPrice, trackType, startDate, duration, number);
            }
        }).show();

    }

    public void addOrder(final String userId, final String price, final String trackType, final String startDate, final String duration, final String numberOfParticipants) {

        class OrderRegistrationClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(UserAreaActivity.this, "Loading Data", null, true, true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                progressDialog.dismiss();

                Toast.makeText(UserAreaActivity.this, httpResponseMsg.toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("id", params[0]);
                hashMap.put("price", params[1]);
                hashMap.put("track", params[2]);
                hashMap.put("startDate", params[3]);
                hashMap.put("duration", params[4]);
                hashMap.put("numberOfParticipants", params[5]);


                finalResult = httpParse.postRequest(hashMap, HttpURL);

                return finalResult;
            }
        }

        OrderRegistrationClass OrderRegistrationClass = new OrderRegistrationClass();

        OrderRegistrationClass.execute(userId, price, trackType, startDate,duration , numberOfParticipants);
    }
}
