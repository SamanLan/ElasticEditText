package com.samanlan.elasticedittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by SamanLan on 2016-05-15.
 */
public class ElasticEditText extends RelativeLayout {

    private TextView textView;//提示框
    private EditText editText;//输入框
    private LayoutParams textViewLp;//提示框的LayoutParams
    private LayoutParams editLp;//输入框的LayoutParams
    private String tipText;//提示框的内容

    private int tipTextColor;//提示框内容的颜色
    private float tipTextSize;//提示框内容的大小
    private int myEditType;//输入框的内容的类型，还没做
    private String myEditText;//输入框的内容
    private int myEditTextColor;//输入框的内容的颜色
    private float myEditTextSize;//输入框的内容的大小
    private int lineColor;//输入框没有焦点时，线的颜色
    private int lineChangeColor;//输入框有焦点时，线的颜色
    private int lineWhereWidth;//线的长度
    private int lineWhereHeight;//线在哪个高度画
    private int lineSpeed;//线跳动的速度
    private int lineWhereJump=0;
    private boolean editFocus=false;//输入框是否有焦点
    private Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);//画线的画笔
    private Paint linePaint=new Paint(Paint.ANTI_ALIAS_FLAG);//画跳动的线的画笔
    private int changeHeight=0;//线跳动的高度
    private int jumpCount=7;//线跳动的次数
    private boolean isDraw=false;//线是否跳动
    private int textViewHeight;//提示框的高度
    public String getTipText() {
        return ((TextView)this.findViewWithTag("tipText")).getText().toString();
    }
    public void setTipText(String tipText) {
        ((TextView)this.findViewWithTag("tipText")).setText(tipText);
    }
    public void setTipTextColor(int tipTextColor) {
        ((TextView)this.findViewWithTag("tipText")).setTextColor(tipTextColor);
    }
    public float getTipTextSize() {
        return ((TextView)this.findViewWithTag("tipText")).getTextSize();
    }
    public void setTipTextSize(float tipTextSize) {
        ((TextView)this.findViewWithTag("tipText")).setTextSize(tipTextSize);
    }
    public String getMyEditText() {
        return ((EditText)this.findViewWithTag("editText")).getText().toString();
    }
    public void setMyEditText(String myEditText) {
        ((EditText)this.findViewWithTag("editText")).setText(myEditText);
    }
    public void setMyEditTextColor(int myEditTextColor) {
        ((EditText)this.findViewWithTag("editText")).setTextColor(myEditTextColor);
    }
    public float getMyEditTextSize() {
        return ((EditText)this.findViewWithTag("editText")).getTextSize();
    }
    public void setMyEditTextSize(float myEditTextSize) {
        ((EditText)this.findViewWithTag("editText")).setTextSize(myEditTextSize);
    }
    public int getLineColor() {
        return lineColor;
    }
    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }
    public int getLineChangeColor() {
        return lineChangeColor;
    }
    public void setLineChangeColor(int lineChangeColor) {
        this.lineChangeColor = lineChangeColor;
    }
    public int getLineSpeed() {
        return lineSpeed;
    }
    public void setLineSpeed(int lineSpeed) {
        this.lineSpeed = lineSpeed;
    }


    public ElasticEditText(Context context) {
        super(context);
        init(context);
    }
    public ElasticEditText(Context context, AttributeSet attrs) {
        this(context,attrs,0);
        init(context);
    }
    public ElasticEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typeArray=context.obtainStyledAttributes(attrs,R.styleable.ElasticEditText);//获取设置的属性
        this.setBackgroundColor(Color.TRANSPARENT);//设置透明背景，如果没有设置背景，则不能绘画
        tipText=typeArray.getString(R.styleable.ElasticEditText_tipText);
        tipTextColor=typeArray.getColor(R.styleable.ElasticEditText_tipTextColor, Color.BLACK);
        tipTextSize=typeArray.getDimension(R.styleable.ElasticEditText_tipTextSize,12);//提示框内容的大小获取，没有设置默认１２
        textViewHeight= (int) (tipTextSize*2+2);
        //获取输入框类型
        myEditText=typeArray.getString(R.styleable.ElasticEditText_myEditText);
        myEditTextColor=typeArray.getColor(R.styleable.ElasticEditText_myEditTextColor,Color.BLACK);
        myEditTextSize=typeArray.getDimension(R.styleable.ElasticEditText_myEditTextSize,18);//输入框内容的大小获取，没有设置默认１８
        lineColor=typeArray.getColor(R.styleable.ElasticEditText_lineColor,Color.YELLOW);
        lineChangeColor=typeArray.getColor(R.styleable.ElasticEditText_lineChangeColor,Color.GREEN);
        lineSpeed=typeArray.getInt(R.styleable.ElasticEditText_lineSpeed,3);
        Log.e("tipTextSize", String.valueOf(tipTextSize));
        Log.e("myEditTextSize", String.valueOf(myEditTextSize));
        typeArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth=0;
        int measuredHeight=0;
        final int childCount=getChildCount();
        measureChildren(widthMeasureSpec,heightMeasureSpec);
        int widthSpecSize=MeasureSpec.getSize(widthMeasureSpec);
        int widthSpecMode=MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecSize=MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode=MeasureSpec.getMode(heightMeasureSpec);
        if (childCount==0){
            setMeasuredDimension(0,0);
        }
        EditText view= (EditText) getChildAt(1);
        if (widthSpecMode==MeasureSpec.EXACTLY){
            measuredWidth=widthSpecSize;
        }else if (widthSpecMode==MeasureSpec.AT_MOST){
            measuredWidth= (int) Math.max(view.getText().length()*myEditTextSize,200);//设置wrap_content则根据输入框内容的长度来设置，要是没长度则设置２００ｐｘ
            Log.e("measuredWidth", String.valueOf(measuredWidth));
            Log.e("widthSpecSize", String.valueOf(widthSpecSize));
        }
        lineWhereWidth=measuredWidth;
        measuredHeight=view.getHeight();
        lineWhereHeight=measuredHeight+textViewHeight;//设置线所在的高度是输入框的高度+提示框的高度
        setMeasuredDimension(measuredWidth,measuredHeight+textViewHeight+28);//为整个控件设置宽高，高是输入框的高度+提示框的高度+线跳动最高的高度
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e("textView.getHeight()", String.valueOf(textView.getHeight()));
        Log.e("editText.getHeight()", String.valueOf(editText.getHeight()));
        paint.setStrokeWidth(3);//笔宽5像素
        paint.setAntiAlias(true);//锯齿不显示
        linePaint.setStyle(Paint.Style.STROKE);//空心
        linePaint.setAntiAlias(true);//锯齿不显示
        linePaint.setStrokeWidth(3);//笔宽5像素
        linePaint.setColor(lineChangeColor);
        Path bessel=new Path();
        bessel.moveTo(10, lineWhereHeight);
        Log.e("运行draw","111");
        if (isDraw) {//根据输入框是否有内容而决定是否绘制跳动的线
            changeHeight += jumpCount;
            jumpCount--;
            if (jumpCount < -10) {
                bessel.quadTo(lineWhereWidth / 2, lineWhereHeight, lineWhereWidth, lineWhereHeight);
                canvas.drawPath(bessel, linePaint);
                jumpCount=7;
                changeHeight=0;
                isDraw=false;
            } else {
                lineWhereJump=lineWhereJump!=0?lineWhereJump:lineWhereWidth/2;
                Log.e("lineWhereJump", String.valueOf(lineWhereJump));
                bessel.quadTo(lineWhereJump, lineWhereHeight + changeHeight, lineWhereWidth, lineWhereHeight);
                canvas.drawPath(bessel, linePaint);
                Log.e("跳动的高度", String.valueOf(changeHeight));
                postInvalidateDelayed(lineSpeed*10);//设置绘制的速度
            }
        }else {//根据输入框是否有焦点，绘制不同色的线
            if (editFocus){
                paint.setColor(lineChangeColor);Log.e("改变线条的颜色","有焦点的颜色");
            }
            else{
                paint.setColor(lineColor);Log.e("改变线条的颜色","没有焦点的颜色");
            }
            canvas.drawLine(0,lineWhereHeight,lineWhereWidth,lineWhereHeight,paint);
        }
    }
    private void init(Context context){
        textView=new TextView(context);
        textView.setTag("tipText");
        editText=new EditText(context);
        editText.setTag("editText");
        textViewLp= new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        editLp= new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        editLp.topMargin=textViewHeight;
        textView.setText(tipText);
        if (TextUtils.isEmpty(myEditText)){
            textView.setTextSize(myEditTextSize);
            textView.setTextColor(Color.GRAY);
            textViewLp.topMargin=textViewHeight;
        }else{
            textView.setTextSize(tipTextSize);
            textView.setTextColor(tipTextColor);
            textViewLp.topMargin=0;
        }
        editText.setText(myEditText);
        editText.setTextColor(myEditTextColor);
        editText.setTextSize(myEditTextSize);
        editText.setMaxLines(1);
        editText.setBackgroundColor(Color.TRANSPARENT);
        editText.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                lineWhereJump= (int) motionEvent.getX();
                Log.e("lineWhereJump", String.valueOf(lineWhereJump));
                Log.e("onTouch","执行");
                return false;
            }
        });
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (editText.hasFocus()){
                    Log.e("是否有焦点","true");
                    editFocus=true;
                    textView.setTextSize(tipTextSize);
                    textView.setTextColor(tipTextColor);
                    textViewLp.topMargin=0;
                    if (TextUtils.isEmpty(editText.getText())){
                        isDraw=true;
                    }
                    invalidate();
                }else {
                    Log.e("是否有焦点","false");
                    editFocus=false;
                    if (TextUtils.isEmpty(editText.getText())){
                        textView.setTextSize(myEditTextSize);
                        textView.setTextColor(Color.GRAY);
                        textViewLp.topMargin=textViewHeight;
                        Log.e("是否有内容","没有");
                    }else{
                        textView.setTextSize(tipTextSize);
                        textView.setTextColor(tipTextColor);
                        textViewLp.topMargin=0;
                        Log.e("是否有内容","有");
                    }
                    invalidate();
                }
            }
        });
        //textViewLp.addRule(RelativeLayout.ALIGN_TOP,TRUE);
        //editLp.addRule(RelativeLayout.ALIGN_BOTTOM,TRUE);
        addView(textView,textViewLp);
        addView(editText,editLp);
    }
}
