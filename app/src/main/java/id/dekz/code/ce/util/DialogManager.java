package id.dekz.code.ce.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import id.dekz.code.ce.R;

/**
 * Created by DEKZ on 2/9/2016.
 */
public class DialogManager {
    private Context context;
    private PreferencesManager preferencesManager;

    public DialogManager(Context context){
        this.context = context;
        preferencesManager = new PreferencesManager(context);
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
