package com.jdyxtech.backrunning.autolayout;

import com.jdyxtech.backrunning.autolayout.utils.AutoLayoutHelper;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by zhy on 16/2/25.
 */
public class AutoContentFrameLayout extends FrameLayout
{
    private final AutoLayoutHelper mHelper = new AutoLayoutHelper(this);


    public AutoContentFrameLayout(Context context)
    {
        super(context);
    }

    public AutoContentFrameLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AutoContentFrameLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public AutoFrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs)
    {
        return new AutoFrameLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        if (!isInEditMode())
        {
            mHelper.adjustChildren();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
