package com.example.indiegamecommunity.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SakuraView extends View {
    private List<Petal> petals = new ArrayList<>();
    private Random random = new Random();
    private Paint paint;
    private int viewWidth, viewHeight;
    private boolean isRunning = false;
    private Bitmap petalBitmap;
    private static final int PETAL_COUNT = 22;
    private static final int[] PETAL_COLORS = {
            0xFFFFB7C5, 0xFFFFC0CB, 0xFFFFD1DC,
            0xFFFFE4E9, 0xFFFF9AAD, 0xFFFFB3C1,
    };

    public SakuraView(Context context) {
        super(context);
        init();
    }

    public SakuraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setStyle(Paint.Style.FILL);
        setWillNotDraw(false);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
        createPetalBitmap();
        initPetals();
    }

    private void createPetalBitmap() {
        int size = (int) (Math.min(viewWidth, viewHeight) * 0.025f);
        if (size < 5) size = 5;
        // 使用 ARGB_4444 减少内存 (API 14+ still works for bitmaps)
        petalBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_4444);
        Canvas c = new Canvas(petalBitmap);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.rgb(255, 185, 200));
        p.setStyle(Paint.Style.FILL);
        float cx = size / 2f;
        float cy = size / 2f + size / 6f;
        c.drawOval(cx - size / 3f, cy - size / 2f, cx + size / 3f, cy + size / 3f, p);
        p.setColor(Color.rgb(255, 160, 180));
        c.drawCircle(cx, cy - size / 2f, size / 4f, p);
    }

    private void initPetals() {
        petals.clear();
        for (int i = 0; i < PETAL_COUNT; i++) {
            Petal petal = new Petal();
            petal.x = random.nextFloat() * viewWidth;
            petal.y = random.nextFloat() * viewHeight;
            petal.speed = 1.2f + random.nextFloat() * 2.8f;
            petal.drift = -0.4f + random.nextFloat() * 0.8f;
            petal.rotation = random.nextFloat() * 360f;
            petal.rotationSpeed = -1.5f + random.nextFloat() * 3f;
            petal.alpha = 0.25f + random.nextFloat() * 0.45f;
            petal.color = PETAL_COLORS[random.nextInt(PETAL_COLORS.length)];
            petals.add(petal);
        }
    }

    public void start() {
        isRunning = true;
        invalidate();
    }

    public void stop() {
        isRunning = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isRunning || petals.isEmpty()) return;

        for (Petal petal : petals) {
            paint.setColor(petal.color);
            paint.setAlpha((int) (petal.alpha * 255));

            int saveCount = canvas.save();
            canvas.translate(petal.x, petal.y);
            canvas.rotate(petal.rotation);

            if (petalBitmap != null && !petalBitmap.isRecycled()) {
                float hw = petalBitmap.getWidth() / 2f;
                float hh = petalBitmap.getHeight() / 2f;
                canvas.drawBitmap(petalBitmap, -hw, -hh, paint);
            }
            canvas.restoreToCount(saveCount);

            petal.y += petal.speed;
            petal.x += petal.drift;
            petal.rotation += petal.rotationSpeed;

            if (petal.y > viewHeight + 30) {
                petal.y = -30;
                petal.x = random.nextFloat() * viewWidth;
            }
            if (petal.x > viewWidth + 30) petal.x = -30;
            if (petal.x < -30) petal.x = viewWidth + 30;
        }

        // 使用 Vsync 对齐的动画帧调度，比固定 33ms 更流畅
        postInvalidateOnAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
        if (petalBitmap != null && !petalBitmap.isRecycled()) {
            petalBitmap.recycle();
        }
    }

    private static class Petal {
        float x, y;
        float speed;
        float drift;
        float rotation;
        float rotationSpeed;
        float alpha;
        int color;
    }
}
