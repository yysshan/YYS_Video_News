package com.example.yys.videonewss;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.bt1)Button button;
    @BindView(R.id.bt2)Button button1;

    private Unbinder unbinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder =  ButterKnife.bind(this);



    }
    @OnClick({R.id.bt1,R.id.bt2})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.bt1:
                Toast.makeText(this, "ssss", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt2:
                Toast.makeText(this, "yyyssss", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
