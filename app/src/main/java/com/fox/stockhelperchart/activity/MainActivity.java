package com.fox.stockhelperchart.activity;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.fox.stockhelperchart.R;

public class MainActivity extends AppCompatActivity {

    /**
     * 单日分钟K线图按钮
     */
    @BindView(R.id.minuteKLineBtn)
    Button minuteKLineBtn;
    /**
     * 5日K线图按钮
     */
    @BindView(R.id.fiveDayKLineBtn)
    Button fiveDayKLineBtn;
    /**
     * K线图按钮
     */
    @BindView(R.id.kLineBtn)
    Button kLineBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.minuteKLineBtn, R.id.fiveDayKLineBtn, R.id.kLineBtn})
    public void onViewClicked(View view) {
        Class desClass = null;
        switch (view.getId()) {
            case R.id.minuteKLineBtn:
                desClass = MinuteKLineActivity.class;
                break;
            case R.id.fiveDayKLineBtn:
                desClass = FiveDayKLineActivity.class;
                break;
            default:
                desClass = KLineActivity.class;
        }
        startActivity(new Intent(MainActivity.this, desClass));
    }
}
