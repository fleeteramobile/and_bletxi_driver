/*
package com.taximobility.driver.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.taximobility.driver.MapZoomAct;
import com.taximobility.driver.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

import me.relex.photodraweeview.Attacher;
import me.relex.photodraweeview.IAttacher;
import me.relex.photodraweeview.OnPhotoTapListener;
import me.relex.photodraweeview.OnScaleChangeListener;
import me.relex.photodraweeview.OnViewTapListener;

public class PhotoDraweeView extends SimpleDraweeView implements IAttacher {

    private Attacher mAttacher;

    private boolean mEnableDraweeMatrix = true;

    public PhotoDraweeView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
        init();
    }

    public PhotoDraweeView(Context context) {
        super(context);
        init();
    }

    public PhotoDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PhotoDraweeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    protected void init() {
        if (mAttacher == null || mAttacher.getDraweeView() == null) {
            mAttacher = new Attacher(this);
        }
    }

    public Attacher getAttacher() {
        return mAttacher;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        int saveCount = canvas.save();
        if (mEnableDraweeMatrix) {
            canvas.concat(mAttacher.getDrawMatrix());
        }
        super.onDraw(canvas);
        canvas.restoreToCount(saveCount);
    }

    @Override
    protected void onAttachedToWindow() {
        init();
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
//        mAttacher.onDetachedFromWindow();
        super.onDetachedFromWindow();
    }

    @Override
    public float getMinimumScale() {
        return mAttacher.getMinimumScale();
    }

    @Override
    public void setMinimumScale(float minimumScale) {
        mAttacher.setMinimumScale(minimumScale);
    }

    @Override
    public float getMediumScale() {
        return mAttacher.getMediumScale();
    }

    @Override
    public void setMediumScale(float mediumScale) {
        mAttacher.setMediumScale(mediumScale);
    }

    @Override
    public float getMaximumScale() {
        return mAttacher.getMaximumScale();
    }

    @Override
    public void setMaximumScale(float maximumScale) {
        mAttacher.setMaximumScale(maximumScale);
    }

    @Override
    public float getScale() {
        return mAttacher.getScale();
    }

    @Override
    public void setScale(float scale) {
        mAttacher.setScale(scale);
    }

    @Override
    public void setScale(float scale, boolean animate) {
        mAttacher.setScale(scale, animate);
    }

    @Override
    public void setScale(float scale, float focalX, float focalY, boolean animate) {
        mAttacher.setScale(scale, focalX, focalY, animate);
    }

    @Override
    public void setOrientation(@Attacher.OrientationMode int orientation) {
        mAttacher.setOrientation(orientation);
    }

    @Override
    public void setZoomTransitionDuration(long duration) {
        mAttacher.setZoomTransitionDuration(duration);
    }

    @Override
    public void setAllowParentInterceptOnEdge(boolean allow) {
        mAttacher.setAllowParentInterceptOnEdge(allow);
    }

    @Override
    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener listener) {
        mAttacher.setOnDoubleTapListener(listener);
    }

    @Override
    public void setOnScaleChangeListener(OnScaleChangeListener listener) {
        mAttacher.setOnScaleChangeListener(listener);
    }

    @Override
    public void setOnLongClickListener(View.OnLongClickListener listener) {
        mAttacher.setOnLongClickListener(listener);
    }

    @Override
    public OnPhotoTapListener getOnPhotoTapListener() {
        return mAttacher.getOnPhotoTapListener();
    }

    @Override
    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        mAttacher.setOnPhotoTapListener(listener);
    }

    @Override
    public OnViewTapListener getOnViewTapListener() {
        return mAttacher.getOnViewTapListener();
    }

    @Override
    public void setOnViewTapListener(OnViewTapListener listener) {
        mAttacher.setOnViewTapListener(listener);
    }

    @Override
    public void update(int imageInfoWidth, int imageInfoHeight) {
        mAttacher.update(imageInfoWidth, imageInfoHeight);
    }

    public boolean isEnableDraweeMatrix() {
        return mEnableDraweeMatrix;
    }

    public void setEnableDraweeMatrix(boolean enableDraweeMatrix) {
        mEnableDraweeMatrix = enableDraweeMatrix;
    }

    public void setPhotoUri(Uri uri) {
//        setPhotoUri(uri, null);
    }

    public void setPhotoUri(Uri uri, @Nullable Context context, LinearLayout imageView, LinearLayout tool_bar_lay) {
        mEnableDraweeMatrix = false;
     */
/*   DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setCallerContext(context)
                .setUri(uri)
                .setOldController(getController())
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        super.onFailure(id, throwable);
                        mEnableDraweeMatrix = false;
                        if (context != null) {
                            CToast.ShowToast(context,NC.getString(R.string.something));
                            ((MapZoomAct) context).finish();
                        }
                    }

                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo,
                                                Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        mEnableDraweeMatrix = true;
                        if (imageInfo != null) {
                            tool_bar_lay.setVisibility(View.VISIBLE);
                            update(imageInfo.getWidth(), imageInfo.getHeight());
                            if (imageView != null)
                                imageView.setVisibility(GONE);
                        }
                    }

                    @Override
                    public void onIntermediateImageFailed(String id, Throwable throwable) {
                        super.onIntermediateImageFailed(id, throwable);
                        mEnableDraweeMatrix = false;
                    }

                    @Override
                    public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
                        super.onIntermediateImageSet(id, imageInfo);
                        mEnableDraweeMatrix = true;
                        if (imageInfo != null) {
                            if (imageView != null)
                                imageView.setVisibility(GONE);
                            update(imageInfo.getWidth(), imageInfo.getHeight());
                        }
                    }
                })
                .build();
        setController(controller);*//*

    }
}
*/
