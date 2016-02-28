package id.dekz.code.ce.fragment;

import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.dekz.code.ce.R;
import id.dekz.code.ce.adapter.CurrencyConvertListAdapter;
import id.dekz.code.ce.pojo.Currency;
import id.dekz.code.ce.pojo.MySingleton;
import id.dekz.code.ce.pojo.Rate;
import id.dekz.code.ce.util.ConnectionChecker;
import id.dekz.code.ce.util.Database;
import id.dekz.code.ce.util.DialogManager;
import id.dekz.code.ce.util.ExponentHandler;
import id.dekz.code.ce.util.GenerateCurrencyList;
import id.dekz.code.ce.util.PreferencesManager;
import id.dekz.code.ce.util.StringToDate;
import id.dekz.code.ce.util.SubStringCurrency;

/**
 * Created by DEKZ on 2/6/2016.
 */
public class FragmentConvert extends Fragment {
    private View rootView;
    private AutoscaleEditText etAmount;

    public TextView cFrom;
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

    private ExponentHandler exponentHandler = new ExponentHandler();
    private GenerateCurrencyList generateCurrencyList = new GenerateCurrencyList();
    public AlertDialog listCurrencyDialog;

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
        cFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogListCurrency("from");
            }
        });

        cTo = (TextView) rootView.findViewById(R.id.tvCurrencyTo);
        cTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogListCurrency("to");
            }
        });
        tvResultConvert = (TextView) rootView.findViewById(R.id.tvResultConvert);
        tvCurrentRate = (TextView) rootView.findViewById(R.id.tvCurrentRate);
        tvCRDate = (TextView) rootView.findViewById(R.id.tvCRDate);

        etAmount = (AutoscaleEditText) rootView.findViewById(R.id.etAmount);

        btnSwap = (Button) rootView.findViewById(R.id.btnSwap);
        btnSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String from = cFrom.getText().toString();
                String to = cTo.getText().toString();
                cFrom.setText(to);
                preferencesManager.setKeyLastCurrencyFrom(to);
                cTo.setText(from);
                preferencesManager.setKeyLastCurrencyTo(from);
                onResume();
                /*
                if(etAmount.getText().length()!=0){
                    Double amountDB = Double.parseDouble(String.valueOf(db.getSingleRate(currentRate).getAmount()));
                    Double amountBase = Double.parseDouble(etAmount.getText().toString());
                    Double result = amountDB * amountBase;
                    tvResultConvert.setText(decimalFormat.format(result).toString());
                }*/
            }
        });

        //TODO how to show keyboard auto ?
        etAmount.requestFocus();
        //InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        //imm.showSoftInput(etAmount, InputMethodManager.SHOW_IMPLICIT);
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
                        if(!(db.checkData(currentRate)>0)){
                            onResume();
                        }else{
                            Double amountDB = Double.parseDouble(String.valueOf(db.getSingleRate(currentRate).getAmount()));
                            Double amountBase = Double.parseDouble(etAmount.getText().toString());
                            Double result = amountDB * amountBase;
                            tvResultConvert.setText(decimalFormat.format(result).toString());
                        }
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

    public void dialogListCurrency(final String tag){
        List<Currency> currencies = new ArrayList<Currency>();
        GenerateCurrencyList generateCurrencyList = new GenerateCurrencyList();
        currencies = generateCurrencyList.getListCurrency();

        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.dialog_list_currency, null, false);
        AlertDialog.Builder listCurrencyBuilder = new AlertDialog.Builder(getActivity());
        listCurrencyBuilder.setView(promptsView);

        ListView listConvertCurrency = (ListView) promptsView.findViewById(R.id.listConvertCurrency);
        final List<Currency> finalCurrencies = currencies;
        listConvertCurrency.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(context, finalCurrencies.get(position).getCurrencyName()+" selected",Toast.LENGTH_SHORT).show();
                if(tag.equals("from")){
                    preferencesManager.setKeyLastCurrencyFrom(finalCurrencies.get(position).getCurrencyName());
                }else{
                    preferencesManager.setKeyLastCurrencyTo(finalCurrencies.get(position).getCurrencyName());
                }
                onResume();
                listCurrencyDialog.dismiss();
            }
        });

        CurrencyConvertListAdapter cAdapter = new CurrencyConvertListAdapter(getActivity(),currencies);
        listConvertCurrency.setAdapter(cAdapter);

        listCurrencyBuilder.setCancelable(true);
        listCurrencyDialog = listCurrencyBuilder.show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            etAmount.setText(savedInstanceState.getString("etAmount"));
        }else{
            //Toast.makeText(getActivity(),"no state saved",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("etAmount", etAmount.getText().toString());
    }

    @Override
    public void onResume(){
        super.onResume();
        //Toast.makeText(getActivity(),"resumed",Toast.LENGTH_SHORT).show();
        db = new Database(getActivity());
        dialogManager = new DialogManager(getActivity());
        preferencesManager = new PreferencesManager(getActivity());
        cFrom.setText(preferencesManager.getKeyLastCurrencyFrom());
        cTo.setText(preferencesManager.getKeyLastCurrencyTo());
        currentRate = new Rate(cFrom.getText().toString(),cTo.getText().toString());
        connectionChecker = new ConnectionChecker(getActivity());

        compareDate = new StringToDate(getActivity(),lastUpdatedDateRate,dateNow);

        if(!connectionChecker.isOnline()){
            //not connected
            useRateInDB();
        }else{
            if(!isCurrentRateAvailableInDB()){
                //try req data to be inserted to db
                refreshCurrentRate(cFrom.getText().toString(), cTo.getText().toString());
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
            String strRates = exponentHandler.removeExponent(rateOffline.getAmount().toString());
            tvCurrentRate.setText(String.valueOf(strRates));
            useOfflineRate = true;
            isCurrentRateUpToDate();
        }else{
            //rate not available
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
            String strRates = exponentHandler.removeExponent(ratedb.getAmount().toString());
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

        //String base = cFrom.getText().toString();
        //String symbol = cTo.getText().toString();
        //Double amountBase = Double.parseDouble(etAmount.getText().toString());
        //getSingleRate(base,symbol,amountBase);
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
                    String strRates = exponentHandler.removeExponent(cRate.toString());
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
                    String strRates = exponentHandler.removeExponent(cRate.toString());
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
