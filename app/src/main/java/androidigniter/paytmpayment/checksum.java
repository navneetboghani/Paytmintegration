package androidigniter.paytmpayment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Checksum;

/**
* Created by AndroidIgniter on 01-02-2019.
 */

public class checksum extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    private ProgressDialog dialog ;
    /* Test Merchant ID
    sqkKIk18068938862790
    Test Account Secret Key
    0gv3ZFSHrXQgxK6b*/
    private RequestQueue mRequestQueue;
    String CHECKSUMHASH ="";
    String url ="https://www.b2bshoppy.com/payment/payment_paytm/generateChecksum.php";
    String varifyurl ="https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=";
    String custid="", orderId="", mid="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        //initOrderId();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Intent intent = getIntent();
        orderId = intent.getExtras().getString("orderid");
        custid = intent.getExtras().getString("custid");

        mid = "sqkKIk18068938862790" +
                ""; /// your marchant id
        //sendUserDetailTOServerdd dl = new sendUserDetailTOServerdd();

        //dl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        getchecksum();
        mRequestQueue = Volley.newRequestQueue(this);
// vollye , retrofit, asynch

    }

    public class sendUserDetailTOServerdd extends AsyncTask<ArrayList<String>, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(checksum.this);

        //private String orderId , mid, custid, amt;


                /*"https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";*//*
                            "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID"+orderId*/
        String CHECKSUMHASH ="";

                @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait");
            this.dialog.show();
        }

        protected String doInBackground(ArrayList<String>... alldata) {
            JSONParser jsonParser = new JSONParser(checksum.this);
            String param=
                    "MID="+mid+
                    "&ORDER_ID=" + orderId+
                    "&CUST_ID="+custid+
                    "&CHANNEL_ID=WAP&TXN_AMOUNT=100&WEBSITE=WEBSTAGING"+
                            "&CALLBACK_URL="+ varifyurl+"&INDUSTRY_TYPE_ID=Retail";

            JSONObject jsonObject = jsonParser.makeHttpRequest(url,"POST",param);
            // yaha per checksum ke saht order id or status receive hoga..
            Log.e("CheckSum result >>",jsonObject.toString());
            if(jsonObject != null){
                Log.e("CheckSum result >>",jsonObject.toString());
                try {

                    CHECKSUMHASH=jsonObject.has("CHECKSUMHASH")?jsonObject.getString("CHECKSUMHASH"):"";
                    Log.e("CheckSum result >>",CHECKSUMHASH);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return CHECKSUMHASH;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(" setup acc ","  signup result  " + result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }




        }








    }
    public void getchecksum()
    {
        try {

            if(dialog==null)
            {
                dialog= new ProgressDialog(checksum.this);
            }
            this.dialog.setMessage("Please wait");
            this.dialog.show();
            StringRequest str = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());
                        if(jsonObject != null){
                            Log.e("CheckSum result >>",jsonObject.toString());
                            try {

                                CHECKSUMHASH=jsonObject.has("CHECKSUMHASH")?jsonObject.getString("CHECKSUMHASH"):"";
                                Log.e("CheckSum result >>",CHECKSUMHASH);
                                callpaytm();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }){
                @Override
                protected Map<String,String> getParams(){

                    HashMap<String, String> paramMap = new HashMap<String, String>();
                    paramMap.put("MID", mid); //MID provided by paytm
                    paramMap.put("ORDER_ID", orderId);
                    paramMap.put("CUST_ID", custid);
                    paramMap.put("CHANNEL_ID", "WAP");
                    paramMap.put("TXN_AMOUNT", "100");
                    paramMap.put("WEBSITE", "WEBSTAGING");
                    paramMap.put("CALLBACK_URL" ,varifyurl+orderId);
                    paramMap.put("INDUSTRY_TYPE_ID", "Retail");
                    return paramMap;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("Content-Type","application/x-www-form-urlencoded");
                    return params;
                }
            };
            if (AppController.getInstance() != null)
                AppController.getInstance().addToRequestQueue(str);
        } catch (Exception e) {
            e.printStackTrace();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private void callpaytm() {
        try {
            PaytmPGService Service = PaytmPGService.getStagingService();
            // when app is ready to publish use production service
            // PaytmPGService  Service = PaytmPGService.getProductionService();

            // now call paytm service here
            //below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values
            HashMap<String, String> paramMap = new HashMap<String, String>();
            //these are mandatory parameters
            paramMap.put("MID", mid); //MID provided by paytm
            paramMap.put("ORDER_ID", orderId);
            paramMap.put("CUST_ID", custid);
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT", "100");
            paramMap.put("WEBSITE", "WEBSTAGING");
            paramMap.put("CALLBACK_URL" ,varifyurl+orderId);
            //paramMap.put( "EMAIL" , "abc@gmail.com");   // no need
            // paramMap.put( "MOBILE_NO" , "9144040888");  // no need
            paramMap.put("CHECKSUMHASH" ,CHECKSUMHASH);
            //paramMap.put("PAYMENT_TYPE_ID" ,"CC");    // no need
            paramMap.put("INDUSTRY_TYPE_ID", "Retail");

            PaytmOrder Order = new PaytmOrder(paramMap);
            Log.e("checksum ", "param "+ paramMap.toString());
            Service.initialize(Order,null);
            // start payment service call here
            Service.startPaymentTransaction(checksum.this, true, true,
                    checksum.this  );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTransactionResponse(Bundle bundle) {
        Log.e("checksum ", " respon true " + bundle.toString());
    }

    @Override
    public void networkNotAvailable() {

    }

    @Override
    public void clientAuthenticationFailed(String s) {

    }

    @Override
    public void someUIErrorOccurred(String s) {
        Log.e("checksum ", " ui fail respon  "+ s );
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Log.e("checksum ", " error loading pagerespon true "+ s + "  s1 " + s1);
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Log.e("checksum ", " cancel call back respon  " );
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Log.e("checksum ", "  transaction cancel " );
    }


}
