package study.pac.customview;

import study.pac.R;
import android.util.AttributeSet;
import android.view.View;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;


/**
 * Mã nguồn lấy từ: ApisDemo
 * Hiển thị chuỗi trên màn hình, có thể tùy chỉnh kích thước và màu sắc
 */
public class LabelView extends View {
	private String mText;
	private int mTextColor;
	private static final int DEFAULT_TEXT_COLOR = 0xFF000000;
	private int mTextSize;
	private static final int DEFALUT_RAW_TEXT_SIZE = 16;
	private Paint mTextPaint;
	
	/*
	 * Khởi tạo 
	 */
	public LabelView(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mText = "Label View";
		mTextColor = DEFAULT_TEXT_COLOR;
		mTextSize = (int)(DEFALUT_RAW_TEXT_SIZE * getResources().getDisplayMetrics().density);
	}
	
	public LabelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LabelView);
		
		CharSequence s = a.getString(R.styleable.LabelView_text);
		if (s != null) {
			setText(s.toString());
		}
		
		setTextColor(a.getColor(R.styleable.LabelView_textColor, DEFAULT_TEXT_COLOR));
		
		int textSize = a.getDimensionPixelOffset(R.styleable.LabelView_textSize, 
				(int)(DEFALUT_RAW_TEXT_SIZE * getResources().getDisplayMetrics().density));
		
		if (textSize >= 0) {
			setTextSize(textSize);
		}
		
		a.recycle();
	}
	
	public LabelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	public String getText() {
		return mText;
	}
	
	public void setText(String text) {
		if (mText != text) {
			mText = text;
			requestLayout();
			invalidate();
		}
	}
	
	public int getTextSize() {
		return mTextSize;
	}
	
	public void setTextSize(int size) {
		if (mTextSize != size) {
			mTextSize = size;
			mTextPaint.setTextSize(mTextSize);
			requestLayout();
			invalidate();
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Tính chiều dài
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		
		int width = widthSize;
		if (widthMode != MeasureSpec.EXACTLY) {
			width = (int)mTextPaint.measureText(mText) + getPaddingLeft() + getPaddingRight();
			if (widthMode == MeasureSpec.AT_MOST) {
				if (width > widthSize) {
					width = widthSize;
				}
			}
		}
		
		// Tính chiều cao
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		
		int height = heightSize;
		if (heightMode != MeasureSpec.EXACTLY) {
			height = (int) (- mTextPaint.ascent()) + (int)mTextPaint.descent()    // tham khảo ascent và descent ở đây : http://www.pawlan.com/monica/articles/texttutorial/other.html 
					+ getPaddingTop() + getPaddingBottom();
			if (heightMode == MeasureSpec.AT_MOST) {
				if (height > heightSize) {
					height = heightSize;
				}
			}
		}
		setMeasuredDimension(width, height);
	}
	
	public int getTextColor() {
		return mTextColor;
	}
	
	public void setTextColor(int color) {
		if (mTextColor != color) {
			mTextColor = color;
			mTextPaint.setColor(mTextColor);
			invalidate();
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawText(mText, getPaddingLeft(), 
				getPaddingTop() - mTextPaint.ascent(), mTextPaint);  // ascent là số âm nên dùng phép trừ
	}
}