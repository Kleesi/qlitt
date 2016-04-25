package com.kschenker.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DonutChartView extends View
{
    private static float DEFAULT_INNER_SPACE_RATIO = 0f;
    private static float DEFAULT_SEGMENT_SPACING_DEGREES = 0f;
    private static float DEFAULT_START_ANGLE_DEGREES = 0f;

    private float innerSpaceRatio;
    private float segmentSpacing;
    private float startAngle;
    private ArrayList<Segment> segments;

    private RectF drawingBounds;
    private RectF chartBounds;
    private RectF innerSpaceBounds;
    private Bitmap offscreenBitmap;
    private Canvas offscreenCanvas;
    private Paint innerSpacePaint;
    private Paint segmentFillPaint;
    private Paint segmentStrokePaint;

    private static ArrayList<Segment> previewSegments;

    //region ---- STATIC INITIALIZATION ----
    static
    {
        createPreviewSegments();
    }

    private static void createPreviewSegments()
    {
        previewSegments = new ArrayList<>();
        previewSegments.add(new Segment(0.2f, Color.CYAN, Color.BLACK));
        previewSegments.add(new Segment(0.5f, Color.MAGENTA, Color.BLACK));
        previewSegments.add(new Segment(0.3f, Color.YELLOW, Color.BLACK));
    }

    //endregion

    //region ---- CONSTRUCTORS ----
    public DonutChartView(Context context)
    {
        this(context, null, 0);
    }

    public DonutChartView(Context context, AttributeSet xmlAttributes)
    {
        this(context, xmlAttributes, 0);
    }

    public DonutChartView(Context context, AttributeSet xmlAttributes, int defaultStyleAttributeID)
    {
        super(context, xmlAttributes, defaultStyleAttributeID);
        setPropertiesFromXmlAttributes(context, xmlAttributes, defaultStyleAttributeID);
    }

    //endregion

    //region ---- GETTERS AND SETTERS ----
    public float getInnerSpaceRatio()
    {
        return innerSpaceRatio;
    }

    public void setInnerSpaceRatio(float innerSpaceRatio)
    {
        this.innerSpaceRatio = innerSpaceRatio;
        invalidate();
        requestLayout();
    }

    public float getStartAngle()
    {
        return startAngle;
    }

    public void setStartAngle(float angle)
    {
        this.startAngle = angle;
        invalidate();
        requestLayout();
    }

    public float getSegmentSpacing()
    {
        return segmentSpacing;
    }

    public void setSegmentSpacing(float segmentSpacing)
    {
        this.segmentSpacing = segmentSpacing % 360;
        invalidate();
        requestLayout();
    }

    private ArrayList<Segment> getSegments()
    {
        if (segments == null)
        {
            segments = new ArrayList<>();
        }

        return segments;
    }

    public void addSegment(Segment segment)
    {
        getSegments().add(segment);
        invalidate();
        requestLayout();
    }

    private void setPropertiesFromXmlAttributes(Context context, AttributeSet xmlAttributes, int defaultStyleAttributeID)
    {
        TypedArray attributeValues = context.getTheme().obtainStyledAttributes(xmlAttributes, R.styleable.DonutChartView, defaultStyleAttributeID, 0);
        innerSpaceRatio = attributeValues.getFraction(R.styleable.DonutChartView_innerSpaceRatio, 1, 1, DEFAULT_INNER_SPACE_RATIO);
        segmentSpacing = attributeValues.getFloat(R.styleable.DonutChartView_segmentSpacing, DEFAULT_SEGMENT_SPACING_DEGREES);
        startAngle = attributeValues.getFloat(R.styleable.DonutChartView_startAngle, DEFAULT_START_ANGLE_DEGREES);
        attributeValues.recycle();
    }

    private Paint getInnerSpacePaint()
    {
        if (innerSpacePaint == null)
        {
            innerSpacePaint = new Paint();
            innerSpacePaint.setStyle(Paint.Style.FILL);
            innerSpacePaint.setAntiAlias(true);
            innerSpacePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }

        return innerSpacePaint;
    }

    private Paint getSegmentFillPaint()
    {
        if (segmentFillPaint == null)
        {
            segmentFillPaint = new Paint();
            segmentFillPaint.setStyle(Paint.Style.FILL);
            segmentFillPaint.setAntiAlias(true);
        }

        return segmentFillPaint;
    }

    private Paint getSegmentStrokePaint()
    {
        if (segmentStrokePaint == null)
        {
            segmentStrokePaint = new Paint();
            segmentStrokePaint.setStyle(Paint.Style.STROKE);
            segmentStrokePaint.setStrokeWidth(1f);
            segmentStrokePaint.setAntiAlias(true);
        }

        return segmentStrokePaint;
    }

    //endregion

    //region ---- MISCELLANEOUS ----
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight)
    {
        initOffscreenCanvas(width, height);
    }

    private void computeBoundsForDrawing()
    {
        computeDrawingBounds();
        computeChartBounds();
        computeInnerSpaceBounds();
    }

    private void computeDrawingBounds()
    {
        drawingBounds = new RectF();
        drawingBounds.left = getPaddingLeft();
        drawingBounds.top = getPaddingTop();
        drawingBounds.right = getWidth() - getPaddingRight();
        drawingBounds.bottom = getHeight() - getPaddingBottom();
    }

    private void computeChartBounds()
    {
        float radius = Math.min(drawingBounds.width(), drawingBounds.height()) / 2f;

        chartBounds = new RectF();
        chartBounds.left = drawingBounds.centerX() - radius;
        chartBounds.top = drawingBounds.centerY() - radius;
        chartBounds.right = chartBounds.left + 2 * radius;
        chartBounds.bottom = chartBounds.top + 2 * radius;
    }

    private void computeInnerSpaceBounds()
    {
        final float innerSpaceRadius = chartBounds.width() * 0.5f * getInnerSpaceRatio();

        innerSpaceBounds = new RectF();
        innerSpaceBounds.left = chartBounds.centerX() - innerSpaceRadius;
        innerSpaceBounds.top = chartBounds.centerY() - innerSpaceRadius;
        innerSpaceBounds.right = chartBounds.centerX() + innerSpaceRadius;
        innerSpaceBounds.bottom = chartBounds.centerY() + innerSpaceRadius;
    }

    private void initOffscreenCanvas(int width, int height)
    {
        offscreenBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        offscreenCanvas = new Canvas(offscreenBitmap);
    }

    //endregion

    //region ---- DRAWING ----
    @Override
    protected void onDraw(Canvas viewCanvas)
    {
        computeBoundsForDrawing();

        clearBitmap(offscreenBitmap);
        drawSegments(offscreenCanvas);
        drawInnerSpace(offscreenCanvas);
        drawSegmentOutlines(offscreenCanvas);

        viewCanvas.drawBitmap(offscreenBitmap, 0, 0, null);
    }

    private void clearBitmap(Bitmap bitmap)
    {
        bitmap.eraseColor(Color.TRANSPARENT);
    }

    private void drawSegments(Canvas canvas)
    {
        List<Segment> allSegments = getSegments().size() > 0 ? getSegments() : previewSegments;
        float remainingSpace = 360f - allSegments.size() * getSegmentSpacing();
        float angle = getStartAngle();

        for (Segment segment : allSegments)
        {
            getSegmentFillPaint().setColor(segment.getFillColor());
            float sweepAngle = segment.getRatio() * remainingSpace;
            canvas.drawArc(chartBounds, angle, sweepAngle, true, getSegmentFillPaint());
            angle += sweepAngle + getSegmentSpacing();
        }
    }

    private void drawSegmentOutlines(Canvas canvas)
    {
        List<Segment> allSegments = getSegments();
        float remainingSpace = 360f - allSegments.size() * getSegmentSpacing();
        float startAngle = getStartAngle();

        for (Segment segment : allSegments)
        {
            getSegmentStrokePaint().setColor(segment.getStrokeColor());
            float sweepAngle = segment.getRatio() * remainingSpace;
            canvas.drawArc(chartBounds, startAngle, sweepAngle, false, getSegmentStrokePaint());
            canvas.drawArc(innerSpaceBounds, startAngle, sweepAngle, false, getSegmentStrokePaint());
            drawSegmentVerticalLine(canvas, startAngle);
            drawSegmentVerticalLine(canvas, startAngle + sweepAngle);
            startAngle += sweepAngle + getSegmentSpacing();
        }
    }

    private void drawSegmentVerticalLine(Canvas canvas, float angle)
    {
        final float innerSpaceRadius = innerSpaceBounds.width() * 0.5f;
        final float chartRadius = chartBounds.width() * 0.5f;

        float fromX = (float)(Math.cos(Math.toRadians(angle)) * innerSpaceRadius) + chartBounds.centerX();
        float toX = (float)(Math.cos(Math.toRadians(angle)) * chartRadius) + chartBounds.centerX();
        float fromY = (float)(Math.sin(Math.toRadians(angle)) * innerSpaceRadius) + chartBounds.centerY();
        float toY = (float)(Math.sin(Math.toRadians(angle)) * chartRadius) + chartBounds.centerY();

        canvas.drawLine(fromX, fromY, toX, toY, getSegmentStrokePaint());
    }

    private void drawInnerSpace(Canvas canvas)
    {
        canvas.drawOval(innerSpaceBounds, getInnerSpacePaint());
    }

    public static class Segment
    {
        private float ratio;
        private int fillColor;
        private int strokeColor;

        public Segment(float ratio, int fillColor, int strokeColor)
        {
            this.ratio = ratio;
            this.fillColor = fillColor;
            this.strokeColor = strokeColor;
        }

        public float getRatio()
        {
            return ratio;
        }

        public void setRatio(float ratio)
        {
            this.ratio = ratio;
        }

        public int getFillColor()
        {
            return fillColor;
        }

        public void setFillColor(int fillColor)
        {
            this.fillColor = fillColor;
        }

        public int getStrokeColor()
        {
            return strokeColor;
        }

        public void setStrokeColor(int strokeColor)
        {
            this.strokeColor = strokeColor;
        }
    }

    //endregion
}
