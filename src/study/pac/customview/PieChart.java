package study.pac.customview;

import study.pac.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * PieChart trình bày dữ liệu được đưa vào
 * theo biểu đồ hình tròn.
 */
public class PieChart extends View {
	/*
	 * Dữ liệu bên ngoài đưa vào để trình bày
	 */
	protected float[] mData = null;                 //TODO Thêm khả năng định nghĩa dữ liệu này từ tập tin xml. (attributeSet)
	/*
	 * Độ lớn cung tròn của mỗi dữ liệu trong mData
	 * tương ứng theo giá trị của nó (1% = 3.6 độ)
	 */
	protected float[] mSweepAngles;
	
	/*
	 * Danh sách các góc bắt đầu vẽ của mỗi dữ liệu trong mData
	 */
	protected float[] mStartAngles;
	
	/* 
	 * Mảng chứa màu sắc của mỗi dữ liệu
	 */
	protected int[] mColors = null;
	protected boolean mAutoGenerateColors = true;
	
	protected String[] mTexts = null;                //TODO Thêm chứa năng hiển thị chữ vào biểu đồ
	protected boolean mShowText = false;

	protected boolean mDrawLine = true;
	protected static final int DEFAULT_LINE_COLOR = Color.rgb(50, 50, 50);
	protected int mLineColor = DEFAULT_LINE_COLOR;
	protected int mLineWidth = DEFAULT_LINE_WIDTH;
	protected static final int DEFAULT_LINE_WIDTH = 1;
	
	
	protected int mMinimumWidth;
	protected int mMinimumHeight;
	protected final static int DEFAULT_WIDTH = 200;   // hard code
	protected final static int DEFAULT_HEIGHT = 200;  // hard code
	
	/**
	 * Kiểu hiển thị bản đồ: vẽ biểu đồ vào trung tâm
	 * của hình chữ nhật chứa biểu đồ
	 */
	public static final int FIT_CENTER = 0;
	
	/**
	 * Vẽ biểu đồ sang góc trên bên trái của hình chữ nhật
	 * chứa biểu đồ
	 */
	public static final int FIT_LEFT_TOP = 1;
	
	/**
	 * Vẽ biểu đồ sang góc trên bên phải của hình chữ nhật
	 * chứa biểu đồ
	 */
	public static final int FIT_TOP_RIGHT = 2;
	
	/**
	 * Vẽ biểu đồ sang góc phải bên dưới của hình chữ nhật
	 * chứa biểu đồ
	 */
	public static final int FIT_RIGHT_BOTTOM = 3;
	
	/**
	 * Vẽ hình chữ nhật sang góc dưới bên trái của hình chữ nhật
	 * chứa biểu đồ
	 */
	public static final int FIT_BOTTOM_LEFT = 4;
	
	/**
	 * Vẽ hình chữ nhật dãn theo hình chữ nhật chứa biểu đồ. Đặt kiểu
	 * vẽ này có thể biểu đồ có hình elip
	 */
	public static final int STRETCH = 5;
	
	/*
	 * Kiểu hiển thị biểu đồ
	 */
	protected int mScaleType = FIT_CENTER;
	
	/*
	 * Hình chữ nhật bao lấy biểu đồ hình tròn
	 */
	protected RectF mBoundRect = new RectF();
	
	protected Paint mChartPaint = new Paint();
	
	
	
	/** 
	 * Khởi tạo biểu đồ mới
	 */
	public PieChart(Context context) {
		super(context);
		init();
	}
	
	public PieChart(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PieChart);
		
		setDrawLine(a.getBoolean(R.styleable.PieChart_drawLine, true));
		setLineWidth(a.getDimensionPixelOffset(R.styleable.PieChart_lineWidth, DEFAULT_LINE_WIDTH));
		setLineColor(a.getColor(R.styleable.PieChart_lineColor, DEFAULT_LINE_COLOR));
		setScaleType(a.getInt(R.styleable.PieChart_scaleType, FIT_CENTER));
		
		a.recycle();
	}
	
	public PieChart(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		mChartPaint.setAntiAlias(true);
	}
	
	/**
	 * Đặt dữ liệu để trình bày cho biểu đồ. Khi đặt dữ liệu
	 * chương trình sẽ tính phần trăm theo tổng của tất cả dữ liệu,
	 * từ đó tính cung tròn chiếm bởi mỗi dữ liệu.
	 * @param data Mảng dữ liệu
	 */
	public void setData(float[] data) {
		mData = data;
		final int count = mData.length;
		
		// Tính tổng để xác định phần trăm
		float sum = 0;
		for (float value : mData) {
			sum += value;
		}
		
		// Tính cung chiếm bởi mỗi dữ liệu
		mSweepAngles = new float[count];
		sum = sum / 100;
		for (int i = 0; i < count; i++) {
			mSweepAngles[i] = mData[i] / sum * 3.6f;
		}
		
		// Tính góc bắt đầu vẽ cho mỗi dữ liệu
		mStartAngles = new float[count];
		for (int i = 1; i < count; i++) {
			mStartAngles[i] = mStartAngles[i - 1] + mSweepAngles[i - 1];
		}
		
		if (mAutoGenerateColors) {
			generateColors(count);
		}
		
		invalidate();
	}
	
	/**
	 * Tự động phát sinh màu cho mỗi dữ liệu trong biểu đồ.
	 * @param numColors Số lượng màu cần phát sinh
	 */
	private void generateColors(int numColors) {
		if (mColors != null && mColors.length >= numColors) {
			return; // don't need to generate colors again
		}
		mColors = new int[numColors];
		
		final int redInc = 80;  // TODO hard code!
		final int greenInc = 5;
		final int blueInc = 25;
		int r = 68, g = 111, b = 176;
		
		for (int i = 0; i < numColors; i++) {
			r += redInc;
			g += greenInc;
			b += blueInc;
			mColors[i] = Color.rgb(r, g, b);
		}
	}
	
	public int[] getColors() {
		return mColors;
	}
	
	/**
	 * Đặt màu cho mảng dữ liệu.
	 * @param colors Mảng chứa danh sách các màu
	 */
	public void setColors(int[] colors) {
		mAutoGenerateColors = false;
		if (mColors != colors) {
			mColors = colors;
			invalidate();
		}
	}
	
	/**
	 * Cho phép vẽ đường viền của biểu đồ hay không.
	 */
	public void setDrawLine(boolean drawLine) {
		if (mDrawLine != drawLine) {
			mDrawLine = drawLine;
			requestLayout();
			invalidate();
		}
	}
	
	public boolean isDrawLine() {
		return mDrawLine;
	}
	
	/**
	 * Đặt màu của đường viền biểu đồ.
	 */
	public void setLineColor(int color) {
		if (color != mLineColor) {
			mLineColor = color;
			invalidate();
		}
	}
	
	public int getLineColor() {
		return mLineColor;
	}
	
	public void setLineWidth(int lineWidth) {
		//TODO: kiểm tra khi trong những màn hình có mật độ pixel (density) khác nhau
		//      thì hiển thị như thế nào
		
		if (lineWidth != mLineWidth) {
			mLineWidth = lineWidth;
			requestLayout();
			invalidate();
		}
	}
	
	public int getLineWidth() {
		return mLineWidth;
	}
	
	public int getScaleType() {
		return mScaleType;
	}
	
	/**
	 * Đặt kiểu hiển thị của đồ thị, là 1 trong các kiểu:
	 * . FIT_CENTER
	 * . FIT_LEFT_TOP
	 * . FIT_TOP_RIGHT
	 * . FIT_RIGHT_BOTTOM
	 * . FIT_BOTTOM_LEFT
	 * . STRETCH
	 */
	public void setScaleType(int scaleType) {
		if (scaleType != mScaleType) {
			mScaleType = scaleType;
			requestLayout();
			invalidate();
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int desiredWidth = DEFAULT_WIDTH;
		final int desiredHeight = DEFAULT_HEIGHT;
		
		int width;
		
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		
		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else if (widthMode == MeasureSpec.AT_MOST) {
			width = Math.min(widthSize, desiredWidth);
		} else {
			width = desiredWidth;
		}
		
		
		int height;
		
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		
		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else if (heightMode == MeasureSpec.AT_MOST) {
			height = Math.min(heightSize, desiredHeight);
		} else {
			height = desiredHeight;
		}
		
		if (width < mMinimumWidth) {
			width = mMinimumWidth;
		}
		if (height < mMinimumHeight) {
			height = mMinimumHeight;
		}
		
		// Khi chưa vẽ chữ lên, hình tròn biểu đồ chiếm hết hình chữ nhật
		// được vẽ, khi thêm chức năng hiển thị chữ vào thì cần tính toán
		// vùng hình tròn biểu đồ chiếm bao nhiêu diện tích rồi truyền
		// chiều rộng và chiều cao của diện tích đó vào hàm này
		calculateBoundRect(width, height);
		
		setMeasuredDimension(width, height);
	}
	
	/**
	 * Tính toán hình chữ nhật bao lấy vùng hình tròn
	 * của biểu đồ.
	 * @param drawRectangleWidth
	 * @param drawRectangleHeight
	 */
	private void calculateBoundRect(int drawRectangleWidth, int drawRectangleHeight) {
		final int w = drawRectangleWidth;  // chiều rộng của hình chữ nhật mà phần hình tròn được vẽ trong đó
		final int h = drawRectangleHeight; // chiều cao của hình chữ nhật mà phần hình tròn được vẽ trong đó
		
		switch (mScaleType) {
		case FIT_CENTER:
			if (w > h) {
				mBoundRect.set((w-h)/2, 0, (h + (w-h)/2), h );
			} else {
				mBoundRect.set(0, (h-w)/2, w, (w + (h-w)/2));
			}
			break;
			
		case FIT_LEFT_TOP:
			if (w > h) {
				mBoundRect.set(0, 0, h, h);
			} else {
				mBoundRect.set(0, 0, w, w);
			}
			break;
			
		case FIT_TOP_RIGHT:
			if (w > h) {
				mBoundRect.set(w-h, 0, h, h);
			} else {
				mBoundRect.set(0, 0, w, w);
			}
			break;
			
		case FIT_RIGHT_BOTTOM:
			if (w > h) {
				mBoundRect.set(w-h, 0, h, h);
			} else {
				mBoundRect.set(0, h-w, w, w);
			}
			break;
			
		case FIT_BOTTOM_LEFT:
			if (w > h) {
				mBoundRect.set(0, 0, h, h);
			} else {
				mBoundRect.set(0, h-w, w, w);
			}
			break;
			
		case STRETCH:
			mBoundRect.set(0, 0, w, h);
			break;
		}
		
		// trừ đi kích thước của đường viền
		if (mDrawLine) {
			mBoundRect.left += mLineWidth;
			mBoundRect.right -= mLineWidth;
			mBoundRect.top += mLineWidth;
			mBoundRect.bottom -= mLineWidth;
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (mData != null) {
			mChartPaint.setStyle(Paint.Style.FILL);
			int length = mData.length;
			for (int i = 0; i < length; i++) {
				mChartPaint.setColor(mColors[i]);
				canvas.drawArc(mBoundRect, mStartAngles[i],
						mSweepAngles[i], true, mChartPaint);
			}
		}
		if (mDrawLine) {
			mChartPaint.setColor(mLineColor);
			mChartPaint.setStyle(Paint.Style.STROKE);
			mChartPaint.setStrokeWidth(mLineWidth);
			canvas.drawOval(mBoundRect, mChartPaint);
		}
	}
}