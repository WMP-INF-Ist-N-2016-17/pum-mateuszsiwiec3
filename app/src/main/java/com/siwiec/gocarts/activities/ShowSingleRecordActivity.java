package com.siwiec.gocarts.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ShowSingleRecordActivity extends AppCompatActivity {

    HttpParse httpParse = new HttpParse();
    ProgressDialog pDialog;

    String HttpURL = "http://gocarts.000webhostapp.com/FilterOrderData.php";
    String HttpURLUpdate = "http://gocarts.000webhostapp.com/UpdateOrder.php";
    String HttpURLDelete = "http://gocarts.000webhostapp.com/DeleteOrder.php";

    String finalResult;
    ProgressDialog progressDialog;
    HashMap<String, String> hashMap = new HashMap<>();
    String ParseResult;
    HashMap<String, String> ResultHash = new HashMap<>();
    String FinalJSonObject;
    TextView PRICE, TRACK, DURATION, DATE, NUMBEROFPARTICIPANTS;
    Button UpdateButton, DeleteButton;
    String priceHolder, trackHolder, durationHolder, dateHolder, playersHolder;
    String TempItem;
    String idHolder;
    ProgressDialog progressDialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_single_record);


        PRICE = (TextView) findViewById(R.id.tvPrice);
        TRACK = (TextView) findViewById(R.id.tvTrack);
        DURATION = (TextView) findViewById(R.id.tvDuration);
        DATE = (TextView) findViewById(R.id.tvDate);
        NUMBEROFPARTICIPANTS = (TextView) findViewById(R.id.tvParticipants);

        UpdateButton = (Button) findViewById(R.id.buttonUpdate);
        DeleteButton = (Button) findViewById(R.id.buttonDelete);

        TempItem = getIntent().getStringExtra("ListViewValue");
        idHolder = getIntent().getStringExtra("id");

        HttpWebCall(TempItem);


        UpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openTrackSelectionDialog(TempItem);
            }
        });

        DeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                orderDelete(TempItem);

            }
        });

    }

    public void orderDelete(final String orderId) {

        class OrderDeleteClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog2 = ProgressDialog.show(ShowSingleRecordActivity.this, "Loading Data", null, true, true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                progressDialog2.dismiss();

                Toast.makeText(ShowSingleRecordActivity.this, httpResponseMsg.toString(), Toast.LENGTH_LONG).show();

                finish();

            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("id", params[0]);

                finalResult = httpParse.postRequest(hashMap, HttpURLDelete);

                return finalResult;
            }
        }

        OrderDeleteClass OrderDeleteClass = new OrderDeleteClass();

        OrderDeleteClass.execute(orderId);
    }

    public void HttpWebCall(final String PreviousListViewClickedItem) {

        class HttpWebCallFunction extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pDialog = ProgressDialog.show(ShowSingleRecordActivity.this, "Loading Data", null, true, true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                pDialog.dismiss();

                FinalJSonObject = httpResponseMsg;

                new GetHttpResponse(ShowSingleRecordActivity.this).execute();

            }

            @Override
            protected String doInBackground(String... params) {

                ResultHash.put("id", params[0]);

                ParseResult = httpParse.postRequest(ResultHash, HttpURL);

                return ParseResult;
            }
        }

        HttpWebCallFunction httpWebCallFunction = new HttpWebCallFunction();

        httpWebCallFunction.execute(PreviousListViewClickedItem);
    }


    private class GetHttpResponse extends AsyncTask<Void, Void, Void> {
        public Context context;

        public GetHttpResponse(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                if (FinalJSonObject != null) {
                    JSONArray jsonArray = null;

                    try {
                        jsonArray = new JSONArray(FinalJSonObject);

                        JSONObject jsonObject;

                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);

                            priceHolder = jsonObject.getString("price");
                            trackHolder = jsonObject.getString("track");
                            durationHolder = jsonObject.getString("duration");
                            dateHolder = jsonObject.getString("startDate");
                            playersHolder = jsonObject.getString("numberOfParticipants");

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            PRICE.setText(priceHolder);
            TRACK.setText(trackHolder);
            DURATION.setText(durationHolder);
            DATE.setText(dateHolder);
            NUMBEROFPARTICIPANTS.setText(playersHolder);

        }
    }


    public void orderUpdate(final String userId, final String price, final String trackType, final String startDate, final String duration, final String numberOfParticipants) {

        class orderRecordUpdateClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(ShowSingleRecordActivity.this, "Loading Data", null, true, true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                progressDialog.dismiss();

                Toast.makeText(ShowSingleRecordActivity.this, httpResponseMsg.toString(), Toast.LENGTH_LONG).show();

                HttpWebCall(TempItem);

            }

            @Override
            protected String doInBackground(String... params) {


                hashMap.put("id", params[0]);
                hashMap.put("price", params[1]);
                hashMap.put("track", params[2]);
                hashMap.put("startDate", params[3]);
                hashMap.put("duration", params[4]);
                hashMap.put("numberOfParticipants", params[5]);

                finalResult = httpParse.postRequest(hashMap, HttpURLUpdate);

                return finalResult;
            }
        }

        orderRecordUpdateClass orderRecordUpdateClass = new orderRecordUpdateClass();

        orderRecordUpdateClass.execute(userId, price, trackType, startDate, duration , numberOfParticipants);
    }

    final CharSequence[] numberOFParticipantsArray = {"2", "3", "4", "5", "6", "7", "8", "9", "10"};

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


                orderUpdate(userId, stringPrice, trackType, startDate, duration, number);
            }
        }).show();

    }

}
