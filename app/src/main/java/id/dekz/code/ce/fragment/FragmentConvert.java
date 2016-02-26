package id.dekz.code.ce.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.txusballesteros.AutoscaleEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
import id.dekz.code.ce.util.SubStringCurrency;

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
    private SimpleDateFormat dateFormat,timeFormat;
    private String dateNow,timeNow,lastUpdatedDateRate;
    private Date currentDate;

    private Button btnSwap;

    private Database db;
    private ConnectionChecker connectionChecker;

    private Double rate;
    private Rate currentRate;
    private PreferencesManager preferencesManager;
    private DialogManager dialogManager;
    private boolean useOfflineRate = false;
    private NumberFormat decimalFormat = new DecimalFormat("#0.00");
    StringToDate compareDate;

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

        btnSwap = (Button) rootView.findViewById(R.id.btnSwap);
        btnSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(),"swap!",Toast.LENGTH_SHORT).show();
                String from = cFrom.getText().toString();
                String to = cTo.getText().toString();
                cFrom.setText(to);
                cTo.setText(from);
                //currentRate = new Rate(cFrom.getText().toString(),cTo.getText().toString());
                //useRateInDB();
                onResume();
            }
        });

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
                if (etAmount.length()==0) {
                    //do nothing
                }else if((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)){
                    if(useOfflineRate){
                        Double amountDB = Double.parseDouble(String.valueOf(db.getSingleRate(currentRate).getAmount()));
                        Double amountBase = Double.parseDouble(etAmount.getText().toString());
                        Double result = amountDB * amountBase;
                        tvResultConvert.setText(decimalFormat.format(result).toString());

                        SubStringCurrency tes = new SubStringCurrency(String.valueOf(result));
                        //Toast.makeText(getActivity(),""+tes.getFormattedAmount(tvResultConvert.getText().toString()),Toast.LENGTH_LONG).show();
                    }else{
                        String base = cFrom.getText().toString();
                        String symbol = cTo.getText().toString();
                        Double amountBase = Double.parseDouble(etAmount.getText().toString());
                        getSingleRate(base,symbol,amountBase);
                    }
                }
                return false;
            }
        });

        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!(etAmount.length()==0)){
                    if(useOfflineRate){
                        Double amountDB = Double.parseDouble(String.valueOf(db.getSingleRate(currentRate).getAmount()));
                        Double amountBase = Double.parseDouble(etAmount.getText().toString());
                        Double result = amountDB * amountBase;
                        tvResultConvert.setText(decimalFormat.format(result).toString());

                        SubStringCurrency tes = new SubStringCurrency(String.valueOf(result));
                        //Toast.makeText(getActivity(),""+tes.getFormattedAmount(tvResultConvert.getText().toString()),Toast.LENGTH_LONG).show();
                    }else{
                        String base = cFrom.getText().toString();
                        String symbol = cTo.getText().toString();
                        Double amountBase = Double.parseDouble(etAmount.getText().toString());
                        getSingleRate(base,symbol,amountBase);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Calendar c = Calendar.getInstance();
        String pattern = "yyyy-MM-dd";
        dateFormat = new SimpleDateFormat(pattern, new Locale("en", "US"));
        dateNow = dateFormat.format(c.getTime());

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        db = new Database(getActivity());
        dialogManager = new DialogManager(getActivity());
        preferencesManager = new PreferencesManager(getActivity());
        currentRate = new Rate(cFrom.getText().toString(),cTo.getText().toString());
        connectionChecker = new ConnectionChecker(getActivity());

        compareDate = new StringToDate(getActivity(),lastUpdatedDateRate,dateNow);

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
            tvCRDate.setText(compareDate.convertDate(rateOffline.getUpdatedOn()));
            String strRates = String.valueOf(new BigDecimal((rateOffline.getAmount()).toString()));
            tvCurrentRate.setText(String.valueOf(strRates));
            useOfflineRate = true;
            isCurrentRateUpToDate();
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
        lastUpdatedDateRate = db.getDataDate(currentRate);
        String dateCompared = compareDate.difference(lastUpdatedDateRate, dateNow);
        if(dateCompared.equals("ok")){
            //data uptodate
            Rate ratedb = db.getSingleRate(currentRate);
            String strRates = String.valueOf(new BigDecimal((ratedb.getAmount()).toString()));
            tvCurrentRate.setText(String.valueOf(strRates));
            tvCRDate.setText(compareDate.convertDate(ratedb.getUpdatedOn()));
        }else if(dateCompared.equals("outdated")){
            //try req new rate
            if (connectionChecker.isOnline()){
                refreshCurrentRate(cFrom.getText().toString(),cTo.getText().toString());
            }else{
                Toast.makeText(getActivity(),"rate outdated! need update rate",Toast.LENGTH_SHORT).show();
            }
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
                    Double cRate = rates.getDouble(symbols);
                    String strRates = String.valueOf(new BigDecimal((cRate).toString()));
                    Rate newRate = new Rate(base,symbols,datestr,cRate,dateNow);

                    if(db.checkData(currentRate)>0){
                        //update data
                        int statusUpdateData = db.updateData(newRate);
                        if(statusUpdateData>0){
                            //success update data
                            tvCurrentRate.setText(strRates);
                            tvCRDate.setText(compareDate.convertDate(dateNow));
                        }else{
                            //something wrong
                            Toast.makeText(getActivity(),"something wrong",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        //insert new data to db
                        long statusSaveData = db.saveData(newRate);
                        if(statusSaveData>0){
                            //success save data
                            tvCurrentRate.setText(strRates);
                            //Toast.makeText(getActivity(),"rates: "+new BigDecimal((cRate).toString()),Toast.LENGTH_SHORT).show();
                            tvCRDate.setText(compareDate.convertDate(dateNow));
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

    private void getSingleRate(final String base, final String symbols, final Double amountBase) {
        String  URLsingleRate = "http://api.fixer.io/latest?base="+base+"&symbols="+symbols;
        JsonObjectRequest singleRateReq = new JsonObjectRequest(Request.Method.GET,
                URLsingleRate, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    String datestr = response.getString("date");
                    JSONObject rates = response.getJSONObject("rates");
                    Double cRate = rates.getDouble(symbols);
                    String strRates = String.valueOf(new BigDecimal((cRate).toString()));
                    rate = Double.parseDouble(rates.getString(symbols));

                    Rate newRate = new Rate(base,symbols,datestr,cRate,dateNow);
                    long statusSaveData = db.saveData(newRate);
                    if(statusSaveData>0){
                        //success save data
                        tvCurrentRate.setText(strRates);
                        tvCRDate.setText(compareDate.convertDate(dateNow));
                        useOfflineRate = true;
                    }else{
                        //something wrong
                        Toast.makeText(getActivity(),"something wrong",Toast.LENGTH_SHORT).show();
                    }

                    Double result = amountBase * rate;
                    tvResultConvert.setText(decimalFormat.format(result).toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),"Error: " + e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if(!(db.checkData(currentRate)>0)){
                    Toast.makeText(getActivity(),"Please Update Rates Using Internet First!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(),"Request Time Out", Toast.LENGTH_SHORT).show();
                }
            }
        });

        MySingleton.getInstance(getActivity()).addToRequestQueue(singleRateReq);
    }
}
