package com.xinrenlei.moduletest;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xinrenlei.arouter_annotation.ARouter;
import com.xinrenlei.arouter_api.RouterManager;

/**
 * Auth：yujunyao
 * Since: 2020/12/8 3:55 PM
 * Email：yujunyao@xinrenlei.net
 */

@ARouter(path = "/app/MainActivity")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void toClient(View view) {
        //没有全部完成之前的复杂调用，后续接着封装
//        Map<String, Class<? extends ARouterPath>> groupMap
//                = new ARouter$$Group$$client().getGroupMap();
//        Class<? extends ARouterPath> clazz = groupMap.get("client");
//        try {
//            ARouter$$Path$$client path = (ARouter$$Path$$client) clazz.newInstance();
//            Map<String, RouterBean> pathMap = path.getPathMap();
//            RouterBean routerBean = pathMap.get("/client/MainActivity");
//            if (routerBean != null) {
//                Intent intent = new Intent(this, routerBean.getClazz());
//                startActivity(intent);
//            }
//        } catch (IllegalAccessException | InstantiationException e) {
//            e.printStackTrace();
//        }

        RouterManager.getInstance()
                .build("/client/MainActivity")
                .withString("name", "yujunyao")
                .withInt("age", 20)
                .withBoolean("isAdult", true)
                .navigation(this);
    }

    public void toBusiness(View view) {
        //完成封装后的跳转
        RouterManager.getInstance()
                .build("/business/MainActivity")
                .withString("name", "yujunyao")
                .withInt("age", 20)
                .withBoolean("isAdult", true)
                .navigation(getApplication());
    }

}
