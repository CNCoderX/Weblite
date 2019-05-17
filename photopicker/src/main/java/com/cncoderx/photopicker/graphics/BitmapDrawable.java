package com.cncoderx.photopicker.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.cncoderx.photopicker.core.BitmapLoader;
import com.cncoderx.photopicker.core.ImageCacheService;
import com.cncoderx.photopicker.io.GalleryBitmapPool;
import com.cncoderx.photopicker.utils.Thumbnail;

import java.lang.ref.WeakReference;

public class BitmapDrawable extends Drawable implements Runnable {
    private ImageCacheService mCacheService;
    private ThumbnailLoader mLoader;
    private String mImagePath;
    private Bitmap mBitmap;
    private Paint mPaint = new Paint();
    private Matrix mDrawMatrix = new Matrix();
    private int mMaxWidth = -1;
    private int mMaxHeight = -1;

    public BitmapDrawable(ImageCacheService cacheService) {
        mCacheService = cacheService;
    }

    public void setImagePath(String imagePath, int maxWidth, int maxHeight) {
        mImagePath = imagePath;
        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
        if (mLoader != null) {
            mLoader.cancel(true);
        }
        if (mBitmap != null) {
            GalleryBitmapPool.getInstance().put(mBitmap);
            mBitmap = null;
        }
        if (imagePath != null) {
            mLoader = new ThumbnailLoader(mCacheService, this, Math.max(maxWidth, maxHeight));
            mLoader.execute(imagePath);
        }
//        invalidateSelf();
    }

    @Override
    public void run() {
        Bitmap bitmap = mLoader.bitmap;
        if (bitmap != null) {
            mBitmap = bitmap;
            mLoader.bitmap = null;
            updateDrawMatrix();
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        updateDrawMatrix();
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        if (mBitmap != null) {
            canvas.save();
            canvas.clipRect(bounds);
            canvas.concat(mDrawMatrix);
            canvas.drawBitmap(mBitmap, 0, 0, mPaint);
            canvas.restore();
        } else {
            mPaint.setColor(0xFFCCCCCC);
            canvas.drawRect(bounds, mPaint);
        }
    }

    private void updateDrawMatrix() {
        Rect bounds = getBounds();
        if (mBitmap == null || bounds.isEmpty()) {
            mDrawMatrix.reset();
            return;
        }

        float scale;
        float dx = 0, dy = 0;

        int dwidth = mBitmap.getWidth();
        int dheight = mBitmap.getHeight();
        int vwidth = bounds.width();
        int vheight = bounds.height();

        if (dwidth * vheight > vwidth * dheight) {
            scale = (float) vheight / (float) dheight;
            dx = (vwidth - dwidth * scale) * 0.5f;
        } else {
            scale = (float) vwidth / (float) dwidth;
            dy = (vheight - dheight * scale) * 0.5f;
        }

        mDrawMatrix.setScale(scale, scale);
        mDrawMatrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
        invalidateSelf();
    }

    @Override
    public int getIntrinsicWidth() {
        return mMaxWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mMaxHeight;
    }

    public String getImagePath() {
        return mImagePath;
    }

    @Override
    public int getOpacity() {
        Bitmap bm = mBitmap;
        return (bm == null || bm.hasAlpha() || mPaint.getAlpha() < 255) ?
                PixelFormat.TRANSLUCENT : PixelFormat.OPAQUE;
    }

    @Override
    public void setAlpha(int alpha) {
        int oldAlpha = mPaint.getAlpha();
        if (alpha != oldAlpha) {
            mPaint.setAlpha(alpha);
            invalidateSelf();
        }
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
        invalidateSelf();
    }

    static class ThumbnailLoader extends BitmapLoader {
        WeakReference<BitmapDrawable> mParent;
        Bitmap bitmap;

        public ThumbnailLoader(ImageCacheService cacheService, BitmapDrawable drawable, int size) {
            super(cacheService, Thumbnail.micro, size);
            mParent = new WeakReference<>(drawable);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            this.bitmap = bitmap;
            BitmapDrawable drawable = mParent.get();
            if (drawable != null) {
                drawable.scheduleSelf(drawable, 0);
            }
        }
    }
}
