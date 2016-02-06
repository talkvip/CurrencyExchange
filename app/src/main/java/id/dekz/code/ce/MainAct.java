package id.dekz.code.ce;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
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

import id.dekz.code.ce.adapter.ViewPagerAdapter;
import id.dekz.code.ce.app.CurrencyExchange;
import id.dekz.code.ce.fragment.FragmentConvert;
import id.dekz.code.ce.fragment.FragmentList;

/**
 * Created by DEKZ on 1/27/2016.
 */
public class MainAct extends AppCompatActivity{

    private Toolbar toolbar;
    private FrameLayout root;
    private View contentHamburger;
    private TabLayout tabLayout;
    private ViewPager viewPager;

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

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentConvert(), "CONVERT");
        adapter.addFragment(new FragmentList(), "LIST");
        viewPager.setAdapter(adapter);
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
