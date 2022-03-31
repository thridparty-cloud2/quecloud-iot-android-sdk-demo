package in.srain.cube.views.ptr.header;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import in.srain.cube.views.ptr.R;

/**
 * @author maplejaw
 * @version 1.0, 2016/6/16
 */
public class XBView extends View {
    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private int mRadius;
    private RectF mRectF;
    private int mCircleWidth=5;
    private Bitmap mBitmap;

    private boolean isStartRotate;

    public XBView(Context context) {
        super(context);
        initView();
    }

    public XBView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public XBView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(0xff00aff0);
        mPaint.setStrokeWidth(mCircleWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mRectF=new RectF();
        mBitmap= BitmapFactory.decodeResource(getResources(), R.drawable.xb_refresh_image_view);

        buildAnimation();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth=w;
        mHeight=h;
        mRadius=Math.min(mWidth,mHeight)/2;
        mRectF.set(mCircleWidth,mCircleWidth,mRadius*2-mCircleWidth,mRadius*2-mCircleWidth);//-mCircleWidth是为了保证圆圈画在矩形内
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawImage(canvas);
        if(isStartRotate){
            drawCirce(canvas);
        }else{
            drawArc(canvas);
        }




    }

    private int mSweetAngle;//旋转角度
    private void drawArc(Canvas canvas){
        // canvas.drawCircle(mWidth/2,mHeight/2,mRadius,mPaint);
        canvas.drawArc(mRectF,-90,mSweetAngle,false,mPaint);


    }

    private void drawCirce(Canvas canvas){

        canvas.drawArc(mRectF,-90,350,false,mPaint);

    }

    private void drawImage(Canvas canvas){
        canvas.drawBitmap(mBitmap,null,mRectF,mPaint);
    }


    private RotateAnimation mRotateAnimation;
    private void buildAnimation() {
        mRotateAnimation = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setDuration(1000);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);


    }

    /**
     * 设置旋转角度
     * @param sweetAngle
     */
    public void setSweetAngle(int sweetAngle){
        this.mSweetAngle=sweetAngle;
        if(!isStartRotate){
            postInvalidate();
        }

    }

    /**
     * 开始旋转动画
     */
    public void startRotateAnim(){
        isStartRotate=true;
        postInvalidate();
        this.clearAnimation();
        this.startAnimation(mRotateAnimation);

    }

    /**
     * 重置
     */
    public void reset(){
        this.clearAnimation();
        isStartRotate=false;
        this.mSweetAngle=0;
    }
}
