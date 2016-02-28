package id.dekz.code.ce.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import id.dekz.code.ce.R;
import id.dekz.code.ce.adapter.CurrencyConvertListAdapter;
import id.dekz.code.ce.pojo.Currency;

/**
 * Created by DEKZ on 2/9/2016.
 */
public class DialogManager {
    private Context context;
    private PreferencesManager preferencesManager;
    public AlertDialog listCurrencyDialog;

    public DialogManager(Context context){
        this.context = context;
        preferencesManager = new PreferencesManager(context);
    }

    public void dialogListCurrency(){
        List<Currency> currencies = new ArrayList<Currency>();
        GenerateCurrencyList generateCurrencyList = new GenerateCurrencyList();
        currencies = generateCurrencyList.getListCurrency();

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_list_currency, null, false);
        AlertDialog.Builder listCurrencyBuilder = new AlertDialog.Builder(context);
        listCurrencyBuilder.setView(promptsView);

        ListView listConvertCurrency = (ListView) promptsView.findViewById(R.id.listConvertCurrency);
        final List<Currency> finalCurrencies = currencies;
        listConvertCurrency.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(context, finalCurrencies.get(position).getCurrencyName()+" selected",Toast.LENGTH_SHORT).show();
                preferencesManager.setKeyLastCurrencyFrom(finalCurrencies.get(position).getCurrencyName());
                listCurrencyDialog.dismiss();
            }
        });

        CurrencyConvertListAdapter cAdapter = new CurrencyConvertListAdapter(context,currencies);
        listConvertCurrency.setAdapter(cAdapter);;

        listCurrencyBuilder.setCancelable(true);
        listCurrencyDialog = listCurrencyBuilder.show();
    }

    public void dialogUseAppOffline(){
        LayoutInflater li = LayoutInflater.from(context);
        View promtsView = li.inflate(R.layout.dialog_offline, null);
        AlertDialog.Builder offlineBuilder = new AlertDialog.Builder(context);
        offlineBuilder.setView(promtsView);

        final CheckBox cbDontAsk = (CheckBox) promtsView.findViewById(R.id.cbDontAsk);

        offlineBuilder.setCancelable(false)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        preferencesManager.setUsingAppOffline();
                        if(cbDontAsk.isChecked()){
                            preferencesManager.dontShowOfflineDialog();
                        }
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        preferencesManager.setUsingAppOnline();
                        if (cbDontAsk.isChecked()) {
                            preferencesManager.dontShowOfflineDialog();
                        }
                    }
                });
        AlertDialog offlineDialog = offlineBuilder.create();
        offlineDialog.show();
    }

    public void showSettingsInternet(){
        AlertDialog.Builder alertDialogInet = new AlertDialog.Builder(context);
        alertDialogInet.setTitle("Internet Connection Not Available");
        alertDialogInet.setMessage("No Rates Data Saved On Your Device, Do You Want To Activate Your Internet Connection?");
        alertDialogInet.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                context.startActivity(intent);
            }
        });
        alertDialogInet.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if(!preferencesManager.isUsingAppOffline() && preferencesManager.isOfflineDialogCanAppear()){
                    //dialogUseAppOffline();
                }
            }
        });
        alertDialogInet.show();
    }
}
