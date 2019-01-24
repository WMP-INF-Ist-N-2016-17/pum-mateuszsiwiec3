package com.siwiec.gocarts.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.siwiec.gocarts.ListAdapterClass;
import com.siwiec.gocarts.Order;
import com.siwiec.gocarts.R;
import com.siwiec.gocarts.httpClasses.HttpServicesClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShowAllOrdersActivity extends AppCompatActivity {

    ListView listViewOrders;
    ProgressBar progressBar;
    List<String> IdList = new ArrayList<>();
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_orders);

        Intent intent = getIntent();
        String userId = intent.getStringExtra("id").toString();
        save_user_id(userId);

        listViewOrders = (ListView) findViewById(R.id.lvOrders);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        new GetHttpResponse(ShowAllOrdersActivity.this).execute();

        listViewOrders.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(ShowAllOrdersActivity.this, ShowSingleRecordActivity.class);

                intent.putExtra("ListViewValue", IdList.get(position).toString());

                startActivity(intent);

                finish();

            }
        });
    }

    public void save_user_id(String id) {
        userId = id;
    }

    private class GetHttpResponse extends AsyncTask<Void, Void, Void> {

        String HttpUrl = "http://gocarts.000webhostapp.com/AllOrdersData.php?userId=" + userId;

        public Context context;

        String JSonResult;

        List<Order> orderList;

        public GetHttpResponse(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpServicesClass httpServicesClass = new HttpServicesClass(HttpUrl);
            try {
                httpServicesClass.ExecutePostRequest();

                if (httpServicesClass.getResponseCode() == 200) {
                    JSonResult = httpServicesClass.getResponse();

                    if (JSonResult != null) {
                        JSONArray jsonArray = null;

                        try {
                            jsonArray = new JSONArray(JSonResult);

                            JSONObject jsonObject;

                            Order order;

                            orderList = new ArrayList<Order>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                order = new Order();

                                jsonObject = jsonArray.getJSONObject(i);

                                // Adding order Id TO IdList Array.
                                IdList.add(jsonObject.getString("id"));

                                // Adding order's date, type of game and number of player.
                                order.data = "Rezerwacja: " + "Data: "+ jsonObject.getString("startDate") + ", rodzaj toru: " + jsonObject.getString("track") + ", ilość uczestników: " + jsonObject.getString("numberOfParticipants") + ", cena : " +jsonObject.getString("price");

                                orderList.add(order);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(context, httpServicesClass.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)

        {
            progressBar.setVisibility(View.GONE);

            listViewOrders.setVisibility(View.VISIBLE);

            ListAdapterClass adapter = new ListAdapterClass(orderList, context);

            listViewOrders.setAdapter(adapter);

        }
    }
}
