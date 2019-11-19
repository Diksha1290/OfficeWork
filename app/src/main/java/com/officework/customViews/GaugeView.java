//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.officework.customViews;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.officework.R;


public class GaugeView extends View {
    public static final int SIZE = 300;
    public static final float TOP = 0.0F;
    public static final float LEFT = 0.0F;
    public static final float RIGHT = 1.0F;
    public static final float BOTTOM = 1.0F;
    public static final float CENTER = 0.5F;
    public static final boolean SHOW_OUTER_SHADOW = true;
    public static final boolean SHOW_OUTER_BORDER = true;
    public static final boolean SHOW_OUTER_RIM = true;
    public static final boolean SHOW_INNER_RIM = true;
    public static final boolean SHOW_NEEDLE = true;
    public static final boolean SHOW_SCALE = false;
    public static final boolean SHOW_RANGES = true;
    public static final boolean SHOW_TEXT = false;
    public static final boolean DEFAULT_SHOW_RANGE_VALUES = false;
    public static final float OUTER_SHADOW_WIDTH = 0.03F;
    public static final float OUTER_BORDER_WIDTH = 0.04F;
    public static final float OUTER_RIM_WIDTH = 0.05F;
    public static final float INNER_RIM_WIDTH = 0.06F;
    public static final float INNER_RIM_BORDER_WIDTH = 0.005F;
    public static final float NEEDLE_WIDTH = 0.025F;
    public static final float NEEDLE_HEIGHT = 0.32F;
    public static final int INNER_CIRCLE_COLOR = Color.rgb(190, 215, 123);
    public static final int OUTER_CIRCLE_COLOR = Color.rgb(224, 231, 33);
    public static final float SCALE_POSITION = 0.015F;
    public static final float SCALE_START_VALUE = 0.0F;
    public static final float SCALE_END_VALUE = 100.0F;
    public static final float SCALE_START_ANGLE = 60.0F;
    public static final int SCALE_DIVISIONS = 10;
    public static final int SCALE_SUBDIVISIONS = 5;
    public static final int[] OUTER_SHADOW_COLORS = new int[]{Color.argb(40, 255, 254, 187), Color.argb(20, 255, 247, 219), Color.argb(5, 255, 255, 255)};
    public static final float[] OUTER_SHADOW_POS = new float[]{0.9F, 0.95F, 0.99F};
    public static final float[] RANGE_VALUES = new float[]{5000.0F};
    public static final int[] RANGE_COLORS = new int[]{-1};
    public static final int TEXT_SHADOW_COLOR = Color.argb(100, 0, 0, 0);
    public static final int TEXT_VALUE_COLOR = -1;
    public static final int TEXT_UNIT_COLOR = -1;
    public static final float TEXT_VALUE_SIZE = 0.3F;
    public static final float TEXT_UNIT_SIZE = 0.1F;
    private static final float[] DEFAULT_RANGE_VALUES = new float[]{0.0F, 100.0F, 200.0F, 500.0F, 1000.0F, 3000.0F, 5000.0F,643.0F,474.0F,64.0F};
    private static final int DEFAULT_COLOR = Color.parseColor("#4169e1");
    private static final int DEFAULT_FACE_COLOR = Color.parseColor("#3498db");
    private boolean mShowOuterShadow;
    private boolean mShowOuterBorder;
    private boolean mShowOuterRim;
    private boolean mShowInnerRim;
    private boolean mShowScale;
    private boolean mShowRanges;
    private boolean mShowNeedle;
    private boolean mShowText;
    private float mOuterShadowWidth;
    private float mOuterBorderWidth;
    private float mOuterRimWidth;
    private float mInnerRimWidth;
    private float mInnerRimBorderWidth;
    private float mNeedleWidth;
    private float mNeedleHeight;
    private int mInnerCircleColor;
    private int mOuterCircleColor;
    private float mScalePosition;
    private float mScaleStartValue;
    private float mScaleEndValue;
    private float mScaleStartAngle;
    private float[] mRangeValues;
    private int[] mRangeColors;
    private int mDivisions;
    private double mSubdivisions;
    private RectF mOuterShadowRect;
    private RectF mOuterBorderRect;
    private RectF mOuterRimRect;
    private RectF mInnerRimRect;
    private RectF mInnerRimBorderRect;
    private RectF mFaceRect;
    private RectF mScaleRect;
    private Bitmap mBackground;
    private Paint mBackgroundPaint;
    private Paint mOuterShadowPaint;
    private Paint mOuterBorderPaint;
    private Paint mOuterRimPaint;
    private Paint mInnerRimPaint;
    private Paint mInnerRimBorderLightPaint;
    private Paint mInnerRimBorderDarkPaint;
    private Paint mFacePaint;
    private Paint mFaceBorderPaint;
    private Paint mFaceShadowPaint;
    private Paint[] mRangePaints;
    private Paint mNeedleRightPaint;
    private Paint mNeedleLeftPaint;
    private Paint mNeedleScrewPaint;
    private Paint mNeedleScrewBorderPaint;
    private Paint mTextValuePaint;
    private Paint mTextUnitPaint;
    private String mTextValue;
    private String mTextUnit;
    private int mTextValueColor;
    private int mTextUnitColor;
    private int mTextShadowColor;
    private float mTextValueSize;
    private float mTextUnitSize;
    private Path mNeedleRightPath;
    private Path mNeedleLeftPath;
    private float mScaleRotation;
    private float mDivisionValue;
    private float mSubdivisionValue;
    private float mSubdivisionAngle;
    private float mTargetValue;
    private float mCurrentValue;
    private float mNeedleVelocity;
    private float mNeedleAcceleration;
    private long mNeedleLastMoved;
    private boolean mNeedleInitialized;
    private RectF mDynamicBorderRect;
    private boolean mShowRangeValues;

    public GaugeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mNeedleLastMoved = -1L;
        this.readAttrs(context, attrs, defStyle);
        this.init();
    }

    public GaugeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GaugeView(Context context) {
        this(context, (AttributeSet)null, 0);
    }

    private void readAttrs(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.GaugeView,
                0, 0);
        this.mShowOuterShadow = a.getBoolean(R.styleable.GaugeView_showOuterShadow, true);
        this.mShowOuterBorder = a.getBoolean(R.styleable.GaugeView_showOuterBorder, true);
        this.mShowOuterRim = a.getBoolean(R.styleable.GaugeView_showOuterRim, true);
        this.mShowInnerRim = a.getBoolean(R.styleable.GaugeView_showInnerRim, true);
        this.mShowNeedle = a.getBoolean(R.styleable.GaugeView_showNeedle, true);
        this.mShowScale = a.getBoolean(R.styleable.GaugeView_showScale, true);
        this.mShowRanges = a.getBoolean(R.styleable.GaugeView_showRanges, true);
        this.mShowText = a.getBoolean(R.styleable.GaugeView_showRangeText, false);
        this.mOuterShadowWidth = this.mShowOuterShadow?a.getFloat(R.styleable.GaugeView_outerShadowWidth, 0.03F):0.0F;
        this.mOuterBorderWidth = this.mShowOuterBorder?a.getFloat(R.styleable.GaugeView_outerBorderWidth, 0.04F):0.0F;
        this.mOuterRimWidth = this.mShowOuterRim?a.getFloat(R.styleable.GaugeView_outerRimWidth, 0.05F):0.0F;
        this.mInnerRimWidth = this.mShowInnerRim?a.getFloat(R.styleable.GaugeView_innerRimWidth, 0.06F):0.0F;
        this.mInnerRimBorderWidth = this.mShowInnerRim?a.getFloat(R.styleable.GaugeView_innerRimBorderWidth, 0.005F):0.0F;
        this.mNeedleWidth = a.getFloat(R.styleable.GaugeView_needleWidth, 0.025F);
        this.mNeedleHeight = a.getFloat(R.styleable.GaugeView_needleHeight, 0.32F);
        this.mInnerCircleColor = a.getColor(R.styleable.GaugeView_innerCircleColor, INNER_CIRCLE_COLOR);
        this.mOuterCircleColor = a.getColor(R.styleable.GaugeView_outerCircleColor, OUTER_CIRCLE_COLOR);
        this.mScalePosition = !this.mShowScale && !this.mShowRanges?0.0F:a.getFloat(R.styleable.GaugeView_scalePosition, 0.015F);
        this.mScaleStartValue = a.getFloat(R.styleable.GaugeView_scaleStartValue, 0.0F);
        this.mScaleEndValue = a.getFloat(R.styleable.GaugeView_scaleEndValue, 180.0F);
        this.mScaleStartAngle = a.getFloat(R.styleable.GaugeView_scaleStartAngle, 93.0F);
        this.mDivisions = a.getInteger(R.styleable.GaugeView_divisions, 2);
        this.mSubdivisions = a.getFloat(R.styleable.GaugeView_subdivisions, 1F );
        if(this.mShowRanges) {
            this.mTextShadowColor = a.getColor(R.styleable.GaugeView_textShadowColor, TEXT_SHADOW_COLOR);
            CharSequence[] rangeValues = a.getTextArray(R.styleable.GaugeView_rangeValues);
            CharSequence[] rangeColors = a.getTextArray(R.styleable.GaugeView_rangeColors);
            this.readRanges(rangeValues, rangeColors);
            this.mShowRangeValues = true;
        }

        if(this.mShowText) {
            int textValueId = a.getResourceId(R.styleable.GaugeView_textValue, 0);
            String textValue = a.getString(R.styleable.GaugeView_textValue);
            this.mTextValue = 0 < textValueId?context.getString(textValueId):(null != textValue?textValue:"");
            int textUnitId = a.getResourceId(R.styleable.GaugeView_textUnit, 0);
            String textUnit = a.getString(R.styleable.GaugeView_textUnit);
            this.mTextUnit = 0 < textUnitId?context.getString(textUnitId):(null != textUnit?textUnit:"");
            this.mTextValueColor = a.getColor(R.styleable.GaugeView_textValueColor, -1);
            this.mTextUnitColor = a.getColor(R.styleable.GaugeView_textUnitColor, -1);
            this.mTextShadowColor = a.getColor(R.styleable.GaugeView_textShadowColor, TEXT_SHADOW_COLOR);
            this.mTextValueSize = a.getFloat(R.styleable.GaugeView_textValueSize, 0.3F);
            this.mTextUnitSize = a.getFloat(R.styleable.GaugeView_textUnitSize, 0.1F);
        }

        a.recycle();
        }

    private void readRanges(CharSequence[] rangeValues, CharSequence[] rangeColors) {
        int rangeValuesLength;
        if(rangeValues == null) {
            rangeValuesLength = DEFAULT_RANGE_VALUES.length;
        } else {
            rangeValuesLength = rangeValues.length;
        }

        int length = rangeValuesLength;
        int i;
        if(rangeValues != null) {
            this.mRangeValues = new float[rangeValuesLength];

            for(i = 0; i < length; ++i) {
                this.mRangeValues[i] = Float.parseFloat(rangeValues[i].toString());
            }
        } else {
            this.mRangeValues = DEFAULT_RANGE_VALUES;
        }

        if(rangeColors != null) {
            this.mRangeColors = new int[length];

            for(i = 0; i < length; ++i) {
                this.mRangeColors[i] = Color.parseColor(rangeColors[i].toString());
            }
        } else {
            this.mRangeColors = RANGE_COLORS;
        }

    }

    @TargetApi(11)
    private void init() {
        if(VERSION.SDK_INT >= 11) {
            this.setLayerType(1, (Paint)null);
        }

        this.initDrawingRects();
        this.initDrawingTools();
        if(this.mShowRanges) {
            this.initScale();
        }

    }

    public void initDrawingRects() {
        this.mOuterShadowRect = new RectF(0.0F, 0.0F, 1.0F, 1.0F);
        float add = 0.015F;
        this.mOuterBorderRect = new RectF(this.mOuterShadowRect.left + this.mOuterShadowWidth + 0.015F, this.mOuterShadowRect.top + this.mOuterShadowWidth + 0.015F, this.mOuterShadowRect.right - this.mOuterShadowWidth - 0.015F, this.mOuterShadowRect.bottom - this.mOuterShadowWidth - 0.015F);
        this.mDynamicBorderRect = new RectF(this.mOuterBorderRect.left + 0.018F, this.mOuterBorderRect.top + 0.018F, this.mOuterBorderRect.right - 0.018F, this.mOuterBorderRect.bottom - 0.018F);
        this.mOuterRimRect = new RectF(this.mOuterBorderRect.left + this.mOuterBorderWidth, this.mOuterBorderRect.top + this.mOuterBorderWidth, this.mOuterBorderRect.right - this.mOuterBorderWidth, this.mOuterBorderRect.bottom - this.mOuterBorderWidth);
        this.mInnerRimRect = new RectF(this.mOuterRimRect.left + this.mOuterRimWidth, this.mOuterRimRect.top + this.mOuterRimWidth, this.mOuterRimRect.right - this.mOuterRimWidth, this.mOuterRimRect.bottom - this.mOuterRimWidth);
        this.mInnerRimBorderRect = new RectF(this.mInnerRimRect.left + this.mInnerRimBorderWidth, this.mInnerRimRect.top + this.mInnerRimBorderWidth, this.mInnerRimRect.right - this.mInnerRimBorderWidth, this.mInnerRimRect.bottom - this.mInnerRimBorderWidth);
        this.mFaceRect = new RectF(this.mInnerRimRect.left + this.mInnerRimWidth - 0.015F, this.mInnerRimRect.top + this.mInnerRimWidth - 0.015F, this.mInnerRimRect.right - this.mInnerRimWidth + 0.015F, this.mInnerRimRect.bottom - this.mInnerRimWidth + 0.015F);
        this.mScaleRect = new RectF(this.mFaceRect.left + this.mScalePosition, this.mFaceRect.top + this.mScalePosition, this.mFaceRect.right - this.mScalePosition, this.mFaceRect.bottom - this.mScalePosition);
    }

    private void initDrawingTools() {
        this.mBackgroundPaint = new Paint();
        this.mBackgroundPaint.setFilterBitmap(true);
        if(this.mShowOuterShadow) {
            this.mOuterShadowPaint = this.getDefaultOuterShadowPaint();
        }

        this.mOuterBorderPaint = this.getDefaultOuterBorderPaint();
        if(this.mShowOuterRim) {
            this.mOuterRimPaint = this.getDefaultOuterRimPaint();
        }

        if(this.mShowInnerRim) {
            this.mInnerRimPaint = this.getDefaultInnerRimPaint();
            this.mInnerRimBorderLightPaint = this.getDefaultInnerRimBorderLightPaint();
            this.mInnerRimBorderDarkPaint = this.getDefaultInnerRimBorderDarkPaint();
        }

        if(this.mShowRanges) {
            this.setDefaultScaleRangePaints();
        }

        if(this.mShowNeedle) {
            this.setDefaultNeedlePaths();
            this.mNeedleLeftPaint = this.getDefaultNeedleLeftPaint();
            this.mNeedleRightPaint = this.getDefaultNeedleRightPaint();
            this.mNeedleScrewPaint = this.getDefaultNeedleScrewPaint();
            this.mNeedleScrewBorderPaint = this.getDefaultNeedleScrewBorderPaint();
        }

        if(this.mShowText) {
            this.mTextValuePaint = this.getDefaultTextValuePaint();
            this.mTextUnitPaint = this.getDefaultTextUnitPaint();
        }

        this.mFacePaint = this.getDefaultFacePaint();
        this.mFaceBorderPaint = this.getDefaultFaceBorderPaint();
        this.mFaceShadowPaint = this.getDefaultFaceShadowPaint();
    }

    public Paint getDefaultOuterShadowPaint() {
        Paint paint = new Paint(1);
        paint.setStyle(Style.STROKE);
        paint.setColor(DEFAULT_COLOR);
        paint.setStrokeWidth(1.0F);
        return paint;
    }

    private Paint getDefaultOuterBorderPaint() {
        Paint paint = new Paint(1);
        paint.setStyle(Style.FILL);
        paint.setColor(mOuterCircleColor);
        return paint;
    }
    private Paint getDefaultBottomBorderPaint() {
        Paint paint = new Paint(1);
        paint.setStyle(Style.FILL);
        paint.setColor(Color.GREEN);
        return paint;
    }
    public Paint getDefaultOuterRimPaint() {
        Paint paint = new Paint(1);
        paint.setFilterBitmap(true);
        return paint;
    }

    private Paint getDefaultInnerRimPaint() {
        Paint paint = new Paint(1);
        paint.setStyle(Style.STROKE);
        paint.setColor(DEFAULT_COLOR);
        paint.setStrokeWidth(1.0F);
        return paint;
    }

    private Paint getDefaultInnerRimBorderLightPaint() {
        Paint paint = new Paint(1);
        paint.setStyle(Style.STROKE);
        paint.setColor(DEFAULT_COLOR);
        paint.setStrokeWidth(1.0F);
        return paint;
    }

    private Paint getDefaultInnerRimBorderDarkPaint() {
        Paint paint = new Paint(1);
        paint.setStyle(Style.STROKE);
        paint.setColor(DEFAULT_COLOR);
        paint.setStrokeWidth(1.0F);
        return paint;
    }

    public Paint getDefaultFacePaint() {
        Paint paint = new Paint(1);
        paint.setColor(mInnerCircleColor);
        return paint;
    }

    public Paint getDefaultFaceBorderPaint() {
        Paint paint = new Paint(1);
        paint.setStyle(Style.STROKE);
        paint.setColor(DEFAULT_COLOR);
        paint.setStrokeWidth(0.02F);
        return paint;
    }

    public Paint getDefaultFaceShadowPaint() {
        Paint paint = new Paint(1);
        paint.setStyle(Style.STROKE);
        paint.setColor(DEFAULT_COLOR);
        paint.setStrokeWidth(0.03F);
        return paint;
    }

    public void setDefaultNeedlePaths() {
        float x = 0.5F;
        float y = 0.5F;
        this.mNeedleLeftPath = new Path();
        this.mNeedleLeftPath.moveTo(0.5F, 0.5F);
        this.mNeedleLeftPath.lineTo(0.5F - this.mNeedleWidth, 0.5F);
        this.mNeedleLeftPath.lineTo(0.5F, 0.5F - this.mNeedleHeight);
        this.mNeedleLeftPath.lineTo(0.5F, 0.5F);
        this.mNeedleLeftPath.lineTo(0.5F - this.mNeedleWidth, 0.5F);
        this.mNeedleRightPath = new Path();
        this.mNeedleRightPath.moveTo(0.5F, 0.5F);
        this.mNeedleRightPath.lineTo(0.5F + this.mNeedleWidth, 0.5F);
        this.mNeedleRightPath.lineTo(0.5F, 0.5F - this.mNeedleHeight);
        this.mNeedleRightPath.lineTo(0.5F, 0.5F);
        this.mNeedleRightPath.lineTo(0.5F + this.mNeedleWidth, 0.5F);
    }

    public Paint getDefaultNeedleLeftPaint() {
        Paint paint = new Paint(1);
        paint.setColor( Color.parseColor("#1F4999"));
        return paint;
    }

    public Paint getDefaultNeedleRightPaint() {
        Paint paint = new Paint(1);
        paint.setColor( Color.parseColor("#1F4999"));
        return paint;
    }

    public Paint getDefaultNeedleScrewPaint() {
        Paint paint = new Paint(1);
        paint.setColor( Color.parseColor("#1F4999"));
        return paint;
    }

    public Paint getDefaultNeedleScrewBorderPaint() {
        Paint paint = new Paint(1);
        paint.setStyle(Style.STROKE);
        paint.setColor( Color.parseColor("#1F4999"));
        paint.setStrokeWidth(0.005F);
        return paint;
    }

    public void setDefaultRanges() {
        this.mRangeValues = new float[]{16.0F, 25.0F, 40.0F, 100.0F,353.0F,523.0F};
        this.mRangeColors = new int[]{Color.rgb(231, 32, 43), Color.rgb(232, 111, 33), Color.rgb(232, 231, 33), Color.rgb(27, 202, 33)};
    }

    public void setDefaultScaleRangePaints() {
        int length = this.mRangeColors.length;
        this.mRangePaints = new Paint[length];

        for(int i = 0; i < length; ++i) {
            this.mRangePaints[i] = new Paint(65);
            this.mRangePaints[i].setColor(this.mRangeColors[i]);
            this.mRangePaints[i].setStyle(Style.STROKE);
            this.mRangePaints[i].setStrokeWidth(0.005F);
            this.mRangePaints[i].setTextSize(0.05F);
            this.mRangePaints[i].setTypeface(Typeface.SANS_SERIF);
            this.mRangePaints[i].setTextAlign(Align.CENTER);
            this.mRangePaints[i].setShadowLayer(0.005F, 0.002F, 0.002F, this.mTextShadowColor);
        }

    }

    public Paint getDefaultTextValuePaint() {
        Paint paint = new Paint(65);
        paint.setColor(this.mTextValueColor);
        paint.setStyle(Style.FILL_AND_STROKE);
        paint.setStrokeWidth(0.005F);
        paint.setTextSize(this.mTextValueSize);
        paint.setTextAlign(Align.CENTER);
        paint.setTypeface(Typeface.SANS_SERIF);
        paint.setShadowLayer(0.01F, 0.002F, 0.002F, this.mTextShadowColor);
        return paint;
    }

    public Paint getDefaultTextUnitPaint() {
        Paint paint = new Paint(65);
        paint.setColor(this.mTextUnitColor);
        paint.setStyle(Style.FILL_AND_STROKE);
        paint.setStrokeWidth(0.005F);
        paint.setTextSize(this.mTextUnitSize);
        paint.setTextAlign(Align.CENTER);
        paint.setTypeface(Typeface.SANS_SERIF);
        paint.setShadowLayer(0.01F, 0.002F, 0.002F, this.mTextShadowColor);
        return paint;
    }

    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle)state;
        Parcelable superState = bundle.getParcelable("superState");
        super.onRestoreInstanceState(superState);
        this.mNeedleInitialized = bundle.getBoolean("needleInitialized");
        this.mNeedleVelocity = bundle.getFloat("needleVelocity");
        this.mNeedleAcceleration = bundle.getFloat("needleAcceleration");
        this.mNeedleLastMoved = bundle.getLong("needleLastMoved");
        this.mCurrentValue = bundle.getFloat("currentValue");
        this.mTargetValue = bundle.getFloat("targetValue");
    }

    private void initScale() {
        this.mScaleRotation = (this.mScaleStartAngle + 180.0F) % 360.0F;
        this.mDivisionValue = (this.mScaleEndValue - this.mScaleStartValue) / (float)this.mDivisions;
        Log.d("mDivisionValue:", String.valueOf(this.mDivisionValue));
        this.mSubdivisionValue = this.mDivisionValue / (float)this.mSubdivisions;
        this.mSubdivisionAngle = (360.0F - 2.0F * this.mScaleStartAngle) / (float)(this.mDivisions * this.mSubdivisions);
    }

    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        Bundle state = new Bundle();
        state.putParcelable("superState", superState);
        state.putBoolean("needleInitialized", this.mNeedleInitialized);
        state.putFloat("needleVelocity", this.mNeedleVelocity);
        state.putFloat("needleAcceleration", this.mNeedleAcceleration);
        state.putLong("needleLastMoved", this.mNeedleLastMoved);
        state.putFloat("currentValue", this.mCurrentValue);
        state.putFloat("targetValue", this.mTargetValue);
        return state;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int chosenWidth = this.chooseDimension(widthMode, widthSize);
        int chosenHeight = this.chooseDimension(heightMode, heightSize);
        this.setMeasuredDimension(chosenWidth, chosenHeight);
    }

    private int chooseDimension(int mode, int size) {
        switch(mode) {
        case -2147483648:
        case 1073741824:
            return size;
        case 0:
        default:
            return this.getDefaultDimension();
        }
    }

    private int getDefaultDimension() {
        return 300;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.drawGauge();
    }

    private void drawGauge() {
        if(null != this.mBackground) {
            this.mBackground.recycle();
        }

        this.mBackground = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(this.mBackground);
        float scale = (float)Math.min(this.getWidth(), this.getHeight());
        canvas.scale(scale, scale);
        canvas.translate(scale == (float)this.getHeight()?((float)this.getWidth() - scale) / 2.0F / scale:0.0F, scale == (float)this.getWidth()?((float)this.getHeight() - scale) / 2.0F / scale:0.0F);
        this.drawRim(canvas);
        this.drawFace(canvas);
        this.drawRim1(canvas);
        if(this.mShowRanges) {
            this.drawScale(canvas);
        }

    }

    protected void onDraw(Canvas canvas) {
        this.drawBackground(canvas);
        float scale = (float)Math.min(this.getWidth(), this.getHeight());
        canvas.scale(scale, scale);
        canvas.translate(scale == (float)this.getHeight()?((float)this.getWidth() - scale) / 2.0F / scale:0.0F, scale == (float)this.getWidth()?((float)this.getHeight() - scale) / 2.0F / scale:0.0F);
        if(this.mShowRanges) {
            if(this.mShowNeedle) {
                this.drawNeedle(canvas);
            }

            if(this.mShowText) {
                this.drawText(canvas);
            }

            this.computeCurrentValue();
        }

    }

    private void drawBackground(Canvas canvas) {
        if(null != this.mBackground) {
            canvas.drawBitmap(this.mBackground, 0.0F, 0.0F, this.mBackgroundPaint);
        }

    }

    private void drawRim(Canvas canvas) {
        canvas.drawArc(this.mOuterBorderRect, 180.0F, 360.0F, true, this.mOuterBorderPaint);
    //   drawRim1(canvas);
    }
    private void drawRim1(Canvas canvas) {
        canvas.drawArc(this.mOuterBorderRect, 180.0F, 0.0F, true, this.getDefaultBottomBorderPaint());
    }

    private void drawFace(Canvas canvas) {
        canvas.drawArc(this.mFaceRect, 180.0F, 180.0F, false, this.mFacePaint);
    }

    private void drawText(Canvas canvas) {
        String textValue = !TextUtils.isEmpty(this.mTextValue)?this.mTextValue:this.valueString(this.mCurrentValue);
        this.mTextValuePaint.measureText(textValue);
        if(!TextUtils.isEmpty(this.mTextUnit)) {
            this.mTextUnitPaint.measureText(this.mTextUnit);
        } else {
            float var10000 = 0.0F;
        }

        float startX = 0.5F;
        float startY = 0.7F;
        this.drawText(canvas, -1, textValue, 0.5F, 0.7F, this.mTextValuePaint);
        if(!TextUtils.isEmpty(this.mTextUnit)) {
            this.drawText(canvas, -1, this.mTextUnit, 0.5F, 0.8F, this.mTextUnitPaint);
        }

    }

    private void drawScale(Canvas canvas) {
        canvas.save();
        canvas.rotate(this.mScaleRotation, 0.5F, 0.5F);
        Log.d("mScaleRotation: ", String.valueOf(this.mScaleRotation));
        int totalTicks = (int) (this.mDivisions * this.mSubdivisions + 1);

        for(int i = 0; i < totalTicks; ++i) {
            float y1 = this.mScaleRect.top;
            Log.d("mScaleRect.top: ", String.valueOf(this.mScaleRect.top));
            float y2 = y1 + 0.045F;
            float y3 = y1 + 0.09F;
            float value = this.getValueForTick(i);
            Paint paint = this.getRangePaint(value);
            float div = this.mScaleEndValue / (float)this.mDivisions;
            float var10000 = value % div;
            paint.setStrokeWidth(0.01F);
            paint.setColor(mOuterCircleColor);
            canvas.drawLine(0.5F, y1 - 0.015F, 0.5F, y3 - 0.07F, paint);
            paint.setStyle(Style.FILL);
            if(this.mShowRangeValues) {
                this.drawText(canvas, i, this.valueString(value), 0.5F, y3, paint);
                Log.d("TEXT:", this.valueString(value));
            }

            canvas.rotate(this.mSubdivisionAngle, 0.5F, 0.5F);
            Log.d("mSubdivisionAngle: ", String.valueOf(this.mSubdivisionAngle));
        }

        canvas.restore();
    }

    private void drawText(Canvas canvas, int tick, String value, float x, float y, Paint paint) {
        float originalTextSize = paint.getTextSize();
        float magnifier = 100.0F;
        canvas.save();
        canvas.scale(0.01F, 0.01F);
        float textWidth = 0.0F;
        float textHeight = 0.0F;
        if(tick != -1) {
            int middleValue = this.mRangeValues.length / 2;
            if(tick == middleValue) {
                textHeight = -1.0F;
                textWidth = 0.0F;
            } else {
                textHeight = 1.5F;
                textWidth = -1.0F;
            }

            canvas.rotate(90.0F - (float)tick * this.mSubdivisionAngle, x * 100.0F, y * 100.0F);
        }

        paint.setTextSize(originalTextSize * 100.0F);
        canvas.drawText(value, x * 100.0F + textWidth, y * 100.0F + textHeight, paint);
        canvas.restore();
        paint.setTextSize(originalTextSize);
    }


    public static void drawSpacedText(Canvas canvas, String text, float left, float top, Paint paint, float spacingPx) {
        float currentLeft = left;

        for(int i = 0; i < text.length(); ++i) {
            String c = text.charAt(i) + "";
            canvas.drawText(c, currentLeft, top, paint);
            currentLeft += spacingPx;
            currentLeft += paint.measureText(c);
        }

    }

    public static float getSpacedTextWidth(Paint paint, String text, float spacingX) {
        return paint.measureText(text) + spacingX * (float)(text.length() - 1);
    }

    private String valueString(float value) {
        return String.format("%d", new Object[]{Integer.valueOf((int)value)});
    }

    private float getValueForTick(int tick) {
        return this.mRangeValues[tick];
    }

    private Paint getRangePaint(float value) {
        return this.mRangePaints[0];
    }

    private void drawNeedle(Canvas canvas) {
        if(this.mNeedleInitialized) {
            float angle = this.getAngleForValue(this.mCurrentValue);
            Log.d("drawNeedle", String.format("value=%f -> angle=%f", new Object[]{Float.valueOf(this.mCurrentValue), Float.valueOf(angle)}));
            Paint paint = new Paint(1);
            paint.setStyle(Style.STROKE);
            paint.setColor(mOuterCircleColor);
            paint.setStrokeWidth(0F);
            if(angle != 0.0F && angle != 240.0F) {
                float sweepAngle;
                if(angle < 240.0F) {
                    sweepAngle = angle + 120.0F;
                } else {
                    sweepAngle = angle - 240.0F;
                }

                canvas.drawArc(this.mDynamicBorderRect, 148.0F, sweepAngle, false, paint);
            }

            canvas.save();
            canvas.rotate(angle, 0.5F, 0.5F);
            this.setNeedleShadowPosition(angle);
            canvas.drawPath(this.mNeedleLeftPath, this.mNeedleLeftPaint);
            canvas.drawPath(this.mNeedleRightPath, this.mNeedleRightPaint);
            canvas.restore();
            canvas.drawCircle(0.5F, 0.5F, 0.04F, this.mNeedleScrewPaint);
            Log.d("NEEDLE", "Needle drawn :/");
        }

    }

    private void setNeedleShadowPosition(float angle) {
        if(angle > 180.0F && angle < 360.0F) {
            this.mNeedleRightPaint.setShadowLayer(0.0F, 0.0F, 0.0F, -16777216);
            this.mNeedleLeftPaint.setShadowLayer(0.01F, -0.005F, 0.005F, Color.argb(127, 0, 0, 0));
        } else {
            this.mNeedleLeftPaint.setShadowLayer(0.0F, 0.0F, 0.0F, -16777216);
            this.mNeedleRightPaint.setShadowLayer(0.01F, 0.005F, -0.005F, Color.argb(127, 0, 0, 0));
        }

    }

    private float getAngleForValue(float value) {
        if(!this.mShowRanges) {
            return -1.0F;
        } else {
            int range = -1;

            for(int i = 0; i < this.mRangeValues.length - 1; ++i) {
                ++range;
                if(value == this.mRangeValues[i]) {
                    range = i;
                    break;
                }

                if(value == this.mRangeValues[i + 1]) {
                    range = i + 1;
                    break;
                }

                if(value > this.mRangeValues[i] && value < this.mRangeValues[i + 1]) {
                    break;
                }
            }

            if(range == -1) {
                return 0.0F;
            } else {
                float angle = (float)range * this.mSubdivisionAngle + (value - this.mRangeValues[range]) * this.mSubdivisionAngle / (this.mRangeValues[range + 1] - this.mRangeValues[range]);
                return (this.mScaleRotation + angle) % 360.0F;
            }
        }
    }

    private void computeCurrentValue() {
        if(Math.abs(this.mCurrentValue - this.mTargetValue) > 0.01F) {
            if(-1L != this.mNeedleLastMoved) {
                float time = (float)(System.currentTimeMillis() - this.mNeedleLastMoved) / 1000.0F;
                float direction = Math.signum(this.mNeedleVelocity);
                if(Math.abs(this.mNeedleVelocity) < 90.0F) {
                    this.mNeedleAcceleration = 5.0F * (this.mTargetValue - this.mCurrentValue);
                } else {
                    this.mNeedleAcceleration = 0.0F;
                }

                this.mNeedleAcceleration = 5.0F * (this.mTargetValue - this.mCurrentValue);
                this.mCurrentValue += this.mNeedleVelocity * time;
                this.mNeedleVelocity += this.mNeedleAcceleration * time;
                if((this.mTargetValue - this.mCurrentValue) * direction < 0.01F * direction) {
                    this.mCurrentValue = this.mTargetValue;
                    this.mNeedleVelocity = 0.0F;
                    this.mNeedleAcceleration = 0.0F;
                    this.mNeedleLastMoved = -1L;
                } else {
                    this.mNeedleLastMoved = System.currentTimeMillis();
                }

                this.invalidate();
            } else {
                this.mNeedleLastMoved = System.currentTimeMillis();
                this.computeCurrentValue();
            }

        }
    }

    public void setTargetValue(float value) {
        if(!this.mShowScale && !this.mShowRanges) {
            this.mTargetValue = value;
        } else if(value < this.mScaleStartValue) {
            this.mTargetValue = this.mScaleStartValue;
        } else if(value > this.mScaleEndValue) {
            this.mTargetValue = this.mScaleEndValue;
        } else {
            this.mTargetValue = value;
        }

        this.mNeedleInitialized = true;
        this.invalidate();
    }

    public void setShowRangeValues(boolean mShowRangeValues) {
        this.mShowRangeValues = mShowRangeValues;
        this.mNeedleInitialized = true;
        this.invalidate();
    }
}
