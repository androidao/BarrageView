package hasee.com.barrageview;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //定义时间
    public static final int DELAY_TIME = 800;
    private BarrageView mBarrageView;
    private Button mButtonTop;
    private Button mButtonBottom;

    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
    }

    private void initEvent() {
        mButtonTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBarrageView.sendBarrage("我是弹幕");
            }
        });
        mButtonBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBarrageView.clearScreen();//清空屏幕
            }
        });
        final Handler handler = new Handler();
        Runnable createBarrageView = new Runnable() {
            @Override
            public void run() {
                int color = Color.rgb(random.nextInt(256),random.nextInt(256),random.nextInt(256));
                mBarrageView.setTextColor(color);
                mBarrageView.sendBarrage("你是猴子搬来的救兵吗？");
                mBarrageView.setTextSize(random.nextInt(100));
                handler.postDelayed(this, DELAY_TIME);
            }
        };
        handler.post(createBarrageView);
    }

    private void initView() {
        mBarrageView = ((BarrageView) findViewById(R.id.barrage));
        mButtonTop = ((Button) findViewById(R.id.button_top));
        mButtonBottom = ((Button) findViewById(R.id.button_bottom));
    }
}
