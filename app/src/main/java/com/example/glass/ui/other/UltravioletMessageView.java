package com.example.glass.ui.other;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class UltravioletMessageView extends View {


    private int backgroundHeight = 100;
    private int backgroundWidth = 3*backgroundHeight;


    private Paint warringPicPaint;
    private Paint warringPicBackPaint;
    private Paint backgroundPaint;
    private Paint textPaint;

    private Path warringPath;

    private Rect textRect ;

    private String text = "当前镜头存在紫外信号";


    public UltravioletMessageView(Context context) {
        super(context);
        init();
    }

    public UltravioletMessageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UltravioletMessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public UltravioletMessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.GRAY);

        warringPicPaint = new Paint();
        warringPicPaint.setColor(Color.RED);

        warringPicBackPaint = new Paint();
        warringPicBackPaint.setColor(Color.YELLOW);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(18);

        warringPath = new Path();
        textRect = new Rect();

        textPaint.getTextBounds(text,0,text.length(),textRect);
    }


    private void drawArea(Canvas canvas){
        RectF rectF = new RectF(0,0,backgroundWidth,backgroundHeight);
        canvas.drawRoundRect(rectF,10,10,backgroundPaint);
        RectF warringRect = new RectF(0,0,backgroundWidth/3,backgroundHeight);
        canvas.drawRoundRect(warringRect,10,10,warringPicBackPaint);
        warringPath.moveTo(backgroundHeight/2-backgroundHeight/12,backgroundHeight/3-backgroundHeight/12);
        warringPath.lineTo(backgroundHeight/2-backgroundHeight/18,backgroundHeight*7/12-backgroundHeight/18);
        warringPath.lineTo(backgroundHeight/2+backgroundHeight/18,backgroundHeight*7/12-backgroundHeight/18);
        warringPath.lineTo(backgroundHeight/2+backgroundHeight/12,backgroundHeight/3-backgroundHeight/12);
        warringPath.lineTo(backgroundHeight/2-backgroundHeight/12,backgroundHeight/3-backgroundHeight/12);
        canvas.drawCircle(backgroundHeight/2,backgroundHeight/3-backgroundHeight/12,backgroundHeight/12,warringPicPaint);
        canvas.drawCircle(backgroundHeight/2,backgroundHeight*7/12-backgroundHeight/18,backgroundHeight/18,warringPicPaint);
        canvas.drawCircle(backgroundHeight/2,backgroundHeight*9/12,backgroundHeight/16,warringPicPaint);
        canvas.drawPath(warringPath,warringPicPaint);


        canvas.drawText(text,backgroundWidth*2/3/2+backgroundHeight-textRect.width()/2,backgroundHeight/2+textRect.height()/2,textPaint);




    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;

        switch (heightMode){
            case MeasureSpec.AT_MOST:{
                width = this.backgroundWidth;
                height = this.backgroundHeight;
                break;
            }
            default:{
                this.backgroundWidth = widthSize;
                this.backgroundHeight = widthSize/3;
                width = widthSize;
                height = widthSize/3;
                break;
            }
        }

        setMeasuredDimension(width,height);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

           drawArea(canvas);

        invalidate();
    }
}
