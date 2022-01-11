package com.example.glass.component.video;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.glass.component.video.drawerbean.CircleBean;
import com.example.glass.component.video.drawerbean.DataBean;
import com.example.glass.component.video.drawerbean.RectBean;
import com.example.glass.component.video.drawerbean.TextBean;
import com.example.glass.component.video.drawerbean.TriangleBean;

import java.util.ArrayList;

public class CameraDrawer extends View {


    private ArrayList<DataBean> dataBeans = new ArrayList<>();


    private Paint textPaint;
    private Paint rectPaint;
    private Paint circlePaint;
    private Paint trianglePaint;

    public void updateData(ArrayList<DataBean> beans){
        this.dataBeans = beans;
    }

    public CameraDrawer(Context context) {
        super(context);
        initPaint();
    }

    public CameraDrawer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public CameraDrawer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    public CameraDrawer(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initPaint();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.parseColor("#00FFFFFF"));

        for (DataBean bean : dataBeans){
            switch (bean.getType()){
                case "rect":{
                    RectBean rectBean = (RectBean)bean;
                    canvas.drawRect(rectBean.getMinX(),rectBean.getMinY(),rectBean.getMaxX(),rectBean.getMaxY(),rectPaint);
                    break;
                }
                case "circle":{
                    CircleBean circleBean = (CircleBean)bean;
                    canvas.drawCircle(circleBean.getX(),circleBean.getY(),circleBean.getRadius(),circlePaint);
                    break;
                }
                case "textbox":{
                    TextBean textBean = (TextBean)bean;
                    canvas.drawText(textBean.getText(),textBean.getX(),textBean.getY(),textPaint);
                    break;
                }
                case "triangle":{
                    TriangleBean triangleBean = (TriangleBean)bean;
                    Path path = new Path();
                    path.moveTo(triangleBean.getTlx(),triangleBean.getTly());
                    path.lineTo(triangleBean.getBlx(),triangleBean.getBly());
                    path.lineTo(triangleBean.getBrx(),triangleBean.getBry());
                    path.close();
                    canvas.drawPath(path,trianglePaint);
                    break;
                }
                default:{
                    break;
                }
            }
        }

        invalidate();
    }


    private void initPaint(){
        textPaint = new Paint();
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(30);

        rectPaint = new Paint();
        rectPaint.setColor(Color.RED);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(3);

        circlePaint = new Paint();
        circlePaint.setColor(Color.RED);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(3);

        trianglePaint = new Paint();
        trianglePaint.setColor(Color.RED);
        trianglePaint.setStyle(Paint.Style.STROKE);
        trianglePaint.setStrokeWidth(3);


    }


}
