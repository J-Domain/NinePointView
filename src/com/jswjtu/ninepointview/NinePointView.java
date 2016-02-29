package com.jswjtu.ninepointview;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author LJiang_6
 * @date 2016年1月25日下午6:54:50
 */
public class NinePointView extends View {

	private List<Point> points = new ArrayList<Point>();
	private StringBuffer passPoint = new StringBuffer();
	private String passKey = "-1";

	private boolean isWrongPass = false;

	private OnPassListener listener;
	private boolean isPassListen = false;

	private int widthView;
	private int heigntView;
	private int minView;

	private double dCircle;
	private double rCircle;

	private int mX = 0;
	private int mY = 0;

	public NinePointView(Context context) {
		super(context);
	}

	public NinePointView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		this.widthView = getWidth();
		this.heigntView = getHeight();
		if (widthView != 0 && heigntView != 0) {
			initDRCircle();
		}
		initPointPlace(changed, left, top, right, bottom);
	}

	/**
	 * 设置Point直径半径
	 */
	private void initDRCircle() {
		if (this.widthView < this.heigntView) {
			this.minView = this.widthView;
		} else {
			this.minView = this.heigntView;
		}
		this.dCircle = this.minView / (3 * 2);
		this.rCircle = this.dCircle / 2;
	}

	/**
	 * 初始化设置Point参数
	 */
	private void initPointPlace(boolean changed, int left, int top, int right,
			int bottom) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				double x = (1 + j * 2) * (this.widthView / 6);
				double y = (1 + i * 2) * (this.heigntView / 6);
				Point point = new Point(x, y, dCircle, rCircle);
				points.add(point);
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawPoint(canvas);
		drawLines(canvas);
	}

	/**
	 * 绘制Point间连线
	 */
	private void drawLines(Canvas canvas) {
		for (int i = 0; i < passPoint.length(); i++) {

			Point pointA = points.get(passPoint.charAt(i) - 48);
			float cxA = (float) pointA.getxPlace();
			float cyA = (float) pointA.getyPlace();

			float cxB;
			float cyB;
			if (i == passPoint.length() - 1) {
				if(isThreadLive) break;
				cxB = mX;
				cyB = mY;
			} else {
				Point pointB = points.get(passPoint.charAt(i + 1) - 48);
				cxB = (float) pointB.getxPlace();
				cyB = (float) pointB.getyPlace();
			}

			Paint paint = new Paint();
			if (isWrongPass) {
				paint.setColor(Color.RED);
			} else {
				paint.setColor(Color.GREEN);
			}
			paint.setStrokeWidth(4);

			canvas.drawLine(cxA, cyA, cxB, cyB, paint);
		}
	}

	/**
	 * 绘制Point点
	 */
	private void drawPoint(Canvas canvas) {

		for (int i = 0; i < 9; i++) {

			Point point = points.get(i);

			float cx = (float) point.getxPlace();
			float cy = (float) point.getyPlace();
			float cr = (float) point.getrCircle();
			boolean isChoose = point.isChoose();

			Paint fPaint = new Paint();
			Paint sPaint = new Paint();
			Paint ePaint = new Paint();

			if (isChoose && isWrongPass) {
				fPaint.setColor(Color.argb(30, 255, 0, 0));
				sPaint.setColor(Color.argb(200, 255, 0, 0));
				ePaint.setColor(Color.argb(200, 255, 0, 0));
				canvas.drawCircle(cx, cy, 5, ePaint);
			} else if (isChoose) {
				fPaint.setColor(Color.argb(30, 0, 255, 0));
				sPaint.setColor(Color.argb(200, 0, 255, 0));
				ePaint.setColor(Color.argb(200, 0, 255, 0));
				canvas.drawCircle(cx, cy, 5, ePaint);
			} else {
				fPaint.setColor(Color.argb(30, 0, 0, 255));
				sPaint.setColor(Color.argb(200, 0, 0, 255));
			}

			fPaint.setStyle(Style.FILL);
			canvas.drawCircle(cx, cy, cr, fPaint);

			sPaint.setStrokeWidth(2);
			sPaint.setStyle(Style.STROKE);
			canvas.drawCircle(cx, cy, cr, sPaint);
		}

	}

	private boolean isThreadLive = false;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				clearDraw();
				invalidate();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
					if (isThreadLive) {
						Message message = new Message();
						message.what = 0;
						handler.sendMessage(message);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			clearDraw();
			isInPointPlace(event);
			isWrongPass = false;
			isThreadLive = false;
			invalidate();
			thread.interrupt();
			break;
		case MotionEvent.ACTION_MOVE:
			isInPointPlace(event);
			isWrongPass = false;
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			isInPointPlace(event);
			if (passPoint.toString().equals(passKey)) {
				isWrongPass = false;
				if (isPassListen) {
					listener.onPass();
				}
			} else {
				isWrongPass = true;
			}
			isThreadLive = true;
			invalidate();
			thread.start();
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 判断触点是否在Point范围内
	 */
	private void isInPointPlace(MotionEvent event) {
		mX = (int) event.getX();
		mY = (int) event.getY();

		for (int i = 0; i < 9; i++) {
			if (points.get(i).isInPlace(mX, mY) && !points.get(i).isChoose()) {
				passPoint.append(i);
				points.get(i).setChoose(true);
			}
		}
	}

	/**
	 * 清除连线信息
	 */
	private void clearDraw() {
		mX = 0;
		mY = 0;
		passPoint.delete(0, passPoint.length());
		for (int i = 0; i < 9; i++) {
			points.get(i).setChoose(false);
		}
	}

	/**
	 * 设置密码
	 * 
	 * @param passKey 密码
	 */
	public void setPassKey(String passKey) {
		this.passKey = passKey;
	}

	/**
	 * 设置Pass监听器
	 * 
	 * @param listener
	 */
	public void setOnPassListener(OnPassListener listener) {
		if (!isPassListen) {
			isPassListen = true;
		}
		this.listener = listener;
	}

	interface OnPassListener {
		void onPass();
	}
}

class Point {

	private double xPlace;
	private double yPlace;

	private double dCircle;
	private double rCircle;

	private boolean isChoose = false;

	public Point() {

	}

	public Point(double x, double y, double d, double r) {
		this.xPlace = x;
		this.yPlace = y;
		this.dCircle = d;
		this.rCircle = r;
	}

	public double getxPlace() {
		return xPlace;
	}

	public void setxPlace(double xPlace) {
		this.xPlace = xPlace;
	}

	public double getyPlace() {
		return yPlace;
	}

	public void setyPlace(double yPlace) {
		this.yPlace = yPlace;
	}

	public double getdCircle() {
		return dCircle;
	}

	public void setdCircle(double dCircle) {
		this.dCircle = dCircle;
	}

	public double getrCircle() {
		return rCircle;
	}

	public void setrCircle(double rCircle) {
		this.rCircle = rCircle;
	}

	public boolean isChoose() {
		return isChoose;
	}

	public void setChoose(boolean isChoose) {
		this.isChoose = isChoose;
	}

	public boolean isInPlace(int mX, int mY) {
		boolean isInX = (mX < (xPlace + rCircle)) && (mX > (xPlace - rCircle));
		boolean isInY = (mY < (yPlace + rCircle)) && (mY > (yPlace - rCircle));
		return (isInX && isInY);
	}
}
