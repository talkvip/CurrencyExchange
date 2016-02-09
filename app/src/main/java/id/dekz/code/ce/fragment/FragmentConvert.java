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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import id.dekz.code.ce.R;
import id.dekz.code.ce.pojo.MySingleton;
import id.dekz.code.ce.pojo.Rate;
import id.dekz.code.ce.util.ConnectionChecker;
import id.dekz.code.ce.util.Database;
import id.dekz.code.ce.util.DialogManager;
import id.dekz.code.ce.util.PreferencesManager;
import id.dekz.code.ce.util.StringToDate;

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
    private String dateNow;

    private Database db;
    private ConnectionChecker connectionChecker;

    private long rate;
    private Rate currentRate;
    private PreferencesManager preferencesManager;
    private DialogManager dialogManager;

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
        String pattern = "yyyy-MM-dd";
        simpleDateFormat = new SimpleDateFormat(pattern, new Locale("en", "US"));
        dateNow = simpleDateFormat.format(c.getTime());
        db = new Database(getActivity());

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        dialogManager = new DialogManager(getActivity());
        preferencesManager = new PreferencesManager(getActivity());
        currentRate = new Rate(cFrom.getText().toString(),cTo.getText().toString());
        connectionChecker = new ConnectionChecker(getActivity());

        if(!connectionChecker.isOnline()){
            //Toast.makeText(getActivity(),"not connected",Toast.LENGTH_SHORT).show();
            useRateInDB();
        }else{
            if(!isCurrentRateAvailableInDB()){
                //try req data to be inserted to db
                refreshCurrentRate(cFrom.getText().toString(),cTo.getText().toString());
            }else{
                //check if data uptodate
                isCurrentRateUpToDate();
            }
        }
    }

    private void useRateInDB(){
        //check if rate is available in db
        if(db.checkData(currentRate)>0){
            //rate available
            Rate rateOffline = db.getSingleRate(currentRate);
            tvCRDate.setText(rateOffline.getDate());
            tvCurrentRate.setText(String.valueOf(rateOffline.getAmount()));
        }else{
            //rate not available
            //Toast.makeText(getActivity(),"need connection to update rate data!",Toast.LENGTH_SHORT).show();
            dialogManager.showSettingsInternet();
        }
    }

    private boolean isCurrentRateAvailableInDB(){
        if(db.checkData(currentRate)>0){
            return true;
        }else{
            return false;
        }
    }

    private void isCurrentRateUpToDate(){
        String lastUpdatedDateRate = db.getDataDate(currentRate);
        StringToDate compareDate = new StringToDate(getActivity(),lastUpdatedDateRate,dateNow);
        String dateCompared = compareDate.difference(lastUpdatedDateRate, dateNow);
        if(dateCompared.equals("ok")){
            //data uptodate
            Rate ratedb = db.getSingleRate(currentRate);
            tvCurrentRate.setText(String.valueOf(ratedb.getAmount()));
            tvCRDate.setText(ratedb.getDate());
        }else if(dateCompared.equals("outdated")){
            //try req new rate
            //Toast.makeText(getActivity(),"rate outdated! need update rate",Toast.LENGTH_SHORT).show();
            refreshCurrentRate(cFrom.getText().toString(),cTo.getText().toString());
        }else{
            //something wrong
            Toast.makeText(getActivity(),"something wrong",Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshCurrentRate(final String base, final String symbols){
        String  URLsingleRate = "http://api.fixer.io/latest?base="+base+"&symbols="+symbols;
        JsonObjectRequest refreshReq = new JsonObjectRequest(Request.Method.GET,
                URLsingleRate, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    String datestr = response.getString("date");
                    JSONObject rates = response.getJSONObject("rates");
                    int cRate = rates.getInt(symbols);

                    if(datestr!=null && cRate!=0){
                        //insert data to db
                        Rate newRate = new Rate(base,symbols,datestr,cRate,dateNow);
                        long statusSaveData = db.saveData(newRate);
                        if(statusSaveData>0){
                            //succes save data
                            tvCurrentRate.setText(String.valueOf(cRate));
                            tvCRDate.setText(datestr);
                        }else{
                            //something wrong
                            Toast.makeText(getActivity(),"something wrong",Toast.LENGTH_SHORT).show();
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),"Error: " + e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),
                        "Request Time Out", Toast.LENGTH_SHORT).show();
            }
        });

        MySingleton.getInstance(getActivity()).addToRequestQueue(refreshReq);
    }

    private void getSingleRate(String base, final String symbols, final long amountBase) {
        String  URLsingleRate = "http://api.fixer.io/latest?base="+base+"&symbols="+symbols;
        JsonObjectRequest singleRateReq = new JsonObjectRequest(Request.Method.GET,
                URLsingleRate, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
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
                Toast.makeText(getActivity(),"Request Time Out", Toast.LENGTH_SHORT).show();
            }
        });

        MySingleton.getInstance(getActivity()).addToRequestQueue(singleRateReq);
    }
}
