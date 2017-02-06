package le.qulei.pre.compile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import le.precompile.annotation.BindView;
import le.precompile.library.LeViewBinder;

/**
 * Demo 测试类
 * Created by lei.qu on 2017/1/19.
 */
public class MainActivity extends Activity {

    @BindView(R.id.ib)
    ImageButton ib;

    @BindView(R.id.tv)
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LeViewBinder.bind(this, getWindow().getDecorView());
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(R.string.action_settings);
                Toast.makeText(MainActivity.this, R.string.app_name, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LeViewBinder.unbind(this);
    }
}
