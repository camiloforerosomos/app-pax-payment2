package com.example.paxpayment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.pax.poslink.CommSetting;
import com.pax.poslink.PosLink;
import com.pax.poslink.ProcessTransResult;
import com.pax.poslink.base.BaseRequest;
import com.pax.poslink.poslink.POSLinkCreator;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Activity";

    private SectionsPageAdapter mSectionsPageAdapter;
    private TabLayout tabLayout;
    private ViewPager2 mViewPager;

    private PosLink link;

    public MainActivity() {
        super(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager(), getLifecycle());

        PaymentFragment fragment = new PaymentFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("link_instance", (Serializable) link);
        fragment.setArguments(bundle);
        mSectionsPageAdapter.addFragment(fragment, "Payment");

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragContainer, fragment, null)
                .commit();
    }
}