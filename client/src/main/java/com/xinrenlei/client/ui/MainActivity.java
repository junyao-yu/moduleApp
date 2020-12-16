package com.xinrenlei.client.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xinrenlei.arouter_annotation.ARouter;
import com.xinrenlei.arouter_annotation.Parameter;
import com.xinrenlei.arouter_api.ParameterManager;
import com.xinrenlei.arouter_api.RouterManager;
import com.xinrenlei.client.R;
import com.xinrenlei.common.business.BusinessDrawable;

/**
 * Auth：yujunyao
 * Since: 2020/12/5 10:27 PM
 * Email：yujunyao@xinrenlei.net
 */
@ARouter(path = "/client/MainActivity")
public class MainActivity extends AppCompatActivity {

    @Parameter
    String name;

    @Parameter
    int age;

    @Parameter
    boolean isAdult;

    @Parameter(name = "/business/drawable")
    BusinessDrawable businessDrawable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_c);

        ParameterManager.getInstance().loadParameter(this);

        TextView textView = findViewById(R.id.text);
        textView.setText("client >>> name: " + name + ", age: " + age + ", isAdult: " + isAdult);
    }

    public void toApp(View view) {

    }

    public void toBusiness(View view) {
        RouterManager.getInstance()
                .build("/business/MainActivity")
                .withString("name", "yujunyao")
                .withInt("age", 20)
                .withBoolean("isAdult", true)
                .navigation(this);
    }
}
