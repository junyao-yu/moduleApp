package com.xinrenlei.business.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xinrenlei.arouter_annotation.ARouter;
import com.xinrenlei.arouter_annotation.Parameter;
import com.xinrenlei.arouter_api.ParameterManager;
import com.xinrenlei.arouter_api.RouterManager;
import com.xinrenlei.business.R;
import com.xinrenlei.common.client.ClientDrawable;

/**
 * Auth：yujunyao
 * Since: 2020/12/5 10:26 PM
 * Email：yujunyao@xinrenlei.net
 */

@ARouter(path = "/business/MainActivity")
public class MainActivity extends AppCompatActivity {

    @Parameter
    String name;

    @Parameter
    int age;

    @Parameter
    boolean isAdult;

    @Parameter(name = "/client/drawable")
    ClientDrawable clientDrawable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_b);

        ParameterManager.getInstance().loadParameter(this);

        TextView textView = findViewById(R.id.text);
        textView.setText("business >>> name: " + name + ", age: " + age + ", isAdult: " + isAdult);
    }

    public void toClient(View view) {
        RouterManager.getInstance()
                .build("/client/MainActivity")
                .withString("name", "yujunyao")
                .withInt("age", 20)
                .withBoolean("isAdult", true)
                .navigation(this);
    }

    public void toApp(View view) {

    }
}
