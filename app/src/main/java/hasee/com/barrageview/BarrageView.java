package hasee.com.barrageview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.LinkedList;
import java.util.Random;

/**
 * Created by Hasee on 2016/4/14.
 */
public class BarrageView extends View {


    //枚举显示所有的模式
    enum showMode {
        allScreen,//用逗号分隔开,全屏
        topOfScreen,//屏幕上方
        bottomOfScreen//屏幕下方

    }

    //创建一个集合用来存储画板、字、画笔
    private LinkedList<Point> pos = new LinkedList<>();
    private LinkedList<String> txts = new LinkedList<>();
    private LinkedList<TextPaint> txtPaints = new LinkedList<>();

    //创建一个集合用来存储中间
    private LinkedList<centerPoint> centerPos = new LinkedList<>();
    private LinkedList<TextPaint> centerTxtPaints = new LinkedList<>();
    private LinkedList<String> centerTxts = new LinkedList<>();

    //创建屏幕宽度和高度对象
    private int screenWidth;
    private int screenHeight;
    //创建画板对象
    private TextPaint txtPaint;

    //定义一个速度的数值
    private int speed = 4;
    private int txtSize = 30;
    private int showSeconds = 3;
    private int x, y;
    //创建一个随机数
    private Random random = new Random();
    //显示模式
    private showMode mShowMode = showMode.topOfScreen;//设置为屏幕下方

    //一个参数的构造方法调用两个参数的构造方法
    public BarrageView(Context context) {
        this(context, null);
    }

    //两个参数的构造方法
    public BarrageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //初始化，这样调用时不管是一个参数还是两个参数都可以调用初始化
        init();
    }

    private void init() {
        //ANTI_ALIAS_FLAG:使位图抗锯齿的标志 DITHER_FLAG：使位图进行有利的抖动的掩码标志
        txtPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        //设置画笔大小
        txtPaint.setTextSize(txtSize);
        //矩形对象
        Rect rect = new Rect();
        getWindowVisibleDisplayFrame(rect);//获取到程序显示的区域，包括标题栏，但不包括状态栏
        screenWidth = rect.width();//把矩形的宽度复制我们定义的屏幕宽度对象
        screenHeight = rect.height();
    }

    //重写绘制方法
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //在画板上进行绘制//for循环读取画板集合的大小
        for (int i = 0; i < pos.size(); i++) {

            canvas.drawText(txts.get(i), pos.get(i).x, pos.get(i).y, txtPaints.get(i));
        }
        for (int i = 0; i < centerTxts.size(); i++) {

            canvas.drawText(centerTxts.get(i), centerPos.get(i).x, centerPos.get(i).y, centerTxtPaints.get(i));
        }
        logic();//显示和隐藏的时间
        invalidate();
    }

    public void setTextSize(int txtSize) {
        this.txtSize = txtSize;
        //传入过来画笔设置文字大小
        txtPaint.setTextSize(txtSize);
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setTextColor(int color) {
        txtPaint.setColor(color);
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setShowSeconds(int i) {
        this.showSeconds = i;
    }

    public int setYShowMode(showMode mode) {
        switch (mode) {
            case allScreen:
                //全屏幕 屏幕的宽度减去文字占用的文字
                y = random.nextInt(screenHeight - txtSize) + txtSize;
                break;
            case topOfScreen:
                //屏幕上方
                y = random.nextInt(screenHeight / 2 - txtSize) + txtSize;
                break;
            case bottomOfScreen:
                //屏幕的下方 x轴不动，y轴加上
                y = random.nextInt(screenHeight / 2 - txtSize) + screenHeight / 2 - txtSize;
                break;
        }

        return y;
    }

    private void logic() {
        for (int i = 0; i < pos.size(); i++) {
            pos.get(i).x = pos.get(i).x - speed;
            if (pos.get(i).x < -txtPaint.measureText(txts.get(i))) {
                pos.remove(i);
                txts.remove(i);
                txtPaints.remove(i);
            }
        }
        for (int i = 0; i < centerTxts.size(); i++) {
            //获取当前系统事件赋值给所有中间的文字
            centerPos.get(i).endTime = System.currentTimeMillis();
            if (centerPos.get(i).endTime - centerPos.get(i).startTime >= 1000 * showSeconds) {
                //显示3秒后消失
                centerPos.remove(i);
                centerTxts.remove(i);
                centerTxtPaints.remove(i);
            }
        }

    }

    //重写触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //当用户按下时，停止
                speed = 0;
                break;
            case MotionEvent.ACTION_UP:
                //用户手指离开，速度恢复
                speed = 4;
                break;
        }
        return true;
    }

    //发送弹幕
    public void sendBarrage(String txt){
        pos.add(new Point(screenWidth,setYShowMode(mShowMode)));
        TextPaint newPaint = new TextPaint(txtPaint);
        txtPaints.add(newPaint);
        txts.add(txt);
    }
    //发送居中的弹幕
    public void sendCenterBarrage(String txt){
        centerTxts.add(txt);//把内容添加进去
        //x轴位置，屏幕宽度减去文字最后的大小的宽度一半就是中间位置
        x = (int) ((screenWidth - txtPaint.measureText(centerTxts.getLast()))/2);
        centerPos.add(new centerPoint(x,setYShowMode(showMode.bottomOfScreen)));
        centerPos.getLast().startTime = System.currentTimeMillis();
        TextPaint newPaint = new TextPaint(txtPaint);
        centerTxtPaints.add(newPaint);
    }
    //清空屏幕
    public void clearScreen(){
        pos.clear();
        txts.clear();
        txtPaints.clear();
        centerPos.clear();
        centerTxtPaints.clear();
        centerTxts.clear();
    }

    //自定义一个类centerPoint
    class centerPoint {
        int x, y;//定义xy
        long startTime, endTime;//定义开始结束时间

        public centerPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

    }
}
