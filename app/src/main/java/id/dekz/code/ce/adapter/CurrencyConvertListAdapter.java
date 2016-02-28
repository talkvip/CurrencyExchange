package id.dekz.code.ce.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import id.dekz.code.ce.R;
import id.dekz.code.ce.app.CurrencyExchange;
import id.dekz.code.ce.pojo.Currency;

/**
 * Created by DEKZ on 2/27/2016.
 */
public class CurrencyConvertListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Currency> currencyItems;

    public CurrencyConvertListAdapter(Context context, List<Currency> currencyItems) {
        this.context = context;
        this.currencyItems = currencyItems;
    }

    @Override
    public int getCount() {
        return currencyItems.size();
    }

    @Override
    public Object getItem(int position) {
        return currencyItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.row_convert_flags, null);

        TextView tvCurrencyCode = (TextView) convertView.findViewById(R.id.tvCurrencyCode);
        TextView tvCurrencyCountry = (TextView) convertView.findViewById(R.id.tvCurrencyCountry);
        ImageView imgCurrency = (ImageView) convertView.findViewById(R.id.imgCurrency);

        Currency currency = currencyItems.get(position);

        String uri = "@drawable/"+currency.getCurrencyName().toLowerCase();
        int imageResource = convertView.getResources().getIdentifier(uri, null, context.getPackageName());
        Drawable res = context.getResources().getDrawable(imageResource);
        imgCurrency.setImageDrawable(res);

        tvCurrencyCode.setText(currency.getCurrencyName());
        tvCurrencyCountry.setText(currency.getCurrencyCountry());

        return convertView;
    }
}
