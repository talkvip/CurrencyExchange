package id.dekz.code.ce.task;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import id.dekz.code.ce.pojo.MySingleton;

/**
 * Created by DEKZ on 2/6/2016.
 */
public class ConvertTask {

    private Context context;
    private String from,to,rate;

    public ConvertTask(Context context){
        this.context = context;
    }

    public String reqRate(String base, final String symbols){
        getSingleRate(base,symbols);
        return rate;
    }

    private void getSingleRate(String base, final String symbols) {
        String  URLsingleRate = "http://api.fixer.io/latest?base="+base+"&symbols="+symbols;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                URLsingleRate, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                //Log.d(TAG, response.toString());

                try {
                    JSONObject rates = response.getJSONObject("rates");
                    rate = rates.getString(symbols);

                    //Toast.makeText(context,""+rate,Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context,"Error: " + e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(context,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        MySingleton.getInstance(context).addToRequestQueue(jsonObjReq);
    }
}
