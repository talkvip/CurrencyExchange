package id.dekz.code.ce;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.yalantis.guillotine.animation.GuillotineAnimation;

import id.dekz.code.ce.app.CurrencyExchange;

/**
 * Created by DEKZ on 1/27/2016.
 */
public class MainAct extends AppCompatActivity{

    private Toolbar toolbar;
    private FrameLayout root;
    private View contentHamburger;
    private TextView tvCLeft,tvCRight;
    private View viewConvert;
    private MaterialRippleLayout materialRippleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        contentHamburger = (View) findViewById(R.id.content_hamburger);
        root = (FrameLayout) findViewById(R.id.root);
        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.menu_main,null);
        root.addView(guillotineMenu);

        new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(250)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .build();

        tvCLeft = (TextView) findViewById(R.id.tvCurrencyLeft);
        tvCLeft.setTypeface(CurrencyExchange.robotoCondensedLight);
        tvCRight = (TextView) findViewById(R.id.tvCurrencyRight);
        tvCRight.setTypeface(CurrencyExchange.robotoCondensedLight);

        //viewConvert = (View) findViewById(R.id.viewConvert);
        //materialRippleLayout = (MaterialRippleLayout) findViewById(R.id.ripple);
        //materialRippleLayout.on(viewConvert).rippleColor(Color.GRAY).create();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            Toast.makeText(getApplicationContext(),"refresh",Toast.LENGTH_SHORT).show();
            CurrencyExchange.getInstance().trackEvent("click event","refresh data","refresh");
        }

        return super.onOptionsItemSelected(item);
    }
}
