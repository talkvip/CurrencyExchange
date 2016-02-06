package id.dekz.code.ce.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.txusballesteros.AutoscaleEditText;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import id.dekz.code.ce.R;
import id.dekz.code.ce.pojo.MySingleton;
import id.dekz.code.ce.task.ConvertTask;

/**
 * Created by DEKZ on 2/6/2016.
 */
public class FragmentConvert extends Fragment {
    private View rootView;
    private AutoscaleEditText etAmount;

    private TextView cFrom;
    private TextView cTo;
    private TextView tvResultConvert;
    private TextView tvCurrentRate;
    private TextView tvCRDate;
    private SimpleDateFormat simpleDateFormat;

    private long rate;

    public FragmentConvert() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_convert, container, false);

        cFrom = (TextView) rootView.findViewById(R.id.tvCurrencyFrom);
        cTo = (TextView) rootView.findViewById(R.id.tvCurrencyTo);
        tvResultConvert = (TextView) rootView.findViewById(R.id.tvResultConvert);
        tvCurrentRate = (TextView) rootView.findViewById(R.id.tvCurrentRate);
        tvCRDate = (TextView) rootView.findViewById(R.id.tvCRDate);

        etAmount = (AutoscaleEditText) rootView.findViewById(R.id.etAmount);

        //TODO how to show keyboard auto ?

        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        //etAmount.setFocusable(true);
        etAmount.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        imm.showSoftInput(etAmount, InputMethodManager.SHOW_IMPLICIT);
        //imm.showSoftInput(getView(), InputMethodManager.SHOW_IMPLICIT);

        etAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    String base = cFrom.getText().toString();
                    String symbol = cTo.getText().toString();
                    long amountBase = Long.parseLong(etAmount.getText().toString());
                    getSingleRate(base,symbol,amountBase);
                }
                return false;
            }
        });

        Calendar c = Calendar.getInstance();
        String pattern = "MM, DD yyyy";
        simpleDateFormat = new SimpleDateFormat(pattern, new Locale("en", "US"));

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        refreshCurrentRate(cFrom.getText().toString(),cTo.getText().toString());
    }

    private void refreshCurrentRate(String base, final String symbols){
        String  URLsingleRate = "http://api.fixer.io/latest?base="+base+"&symbols="+symbols;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                URLsingleRate, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                //Log.d(TAG, response.toString());
                Date date = null;
                try {
                    String datestr = response.getString("date");

                    //date = simpleDateFormat.parse(datestr);
                    //Toast.makeText(getActivity(),""+date,Toast.LENGTH_SHORT).show();
                    tvCRDate.setText(datestr);

                    JSONObject rates = response.getJSONObject("rates");
                    String cRate = rates.getString(symbols);

                    tvCurrentRate.setText(cRate);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),"Error: " + e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),
                        "error response", Toast.LENGTH_SHORT).show();
            }
        });

        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjReq);
    }

    private void getSingleRate(String base, final String symbols, final long amountBase) {
        String  URLsingleRate = "http://api.fixer.io/latest?base="+base+"&symbols="+symbols;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                URLsingleRate, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                //Log.d(TAG, response.toString());

                try {
                    JSONObject rates = response.getJSONObject("rates");
                    //rate = Long.parseLong(rates.getString(symbols));
                    rate = (long)Double.parseDouble(rates.getString(symbols));
                    long result = amountBase * rate;
                    tvResultConvert.setText(String.valueOf(result));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),"Error: " + e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjReq);
    }
}
