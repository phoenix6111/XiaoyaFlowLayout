package com.wanghaisheng.view.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: sheng on 2016/9/11 19:49
 * Email: 1392100700@qq.com
 */
public class FlowLayout extends ViewGroup {
    public FlowLayout(Context context) {
        this(context,null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 测量ViewGroup和子View的宽度和高度
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取系统自己测量的宽和高的测量模式，测量值
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //减去左右padding
        widthSize = widthSize - getPaddingLeft() - getPaddingRight();
        //减去上下padding
        heightSize = heightSize - getPaddingTop() - getPaddingBottom();

        //ViewGroup宽度，为各行的宽度中最大值，最后的宽度
        int destWidth = 0;
        //ViewGroup宽度，为各行height之和，最后的高度
        int destHeight = 0;

        //单独的一行的宽和高的值，此值为一行中child的宽度之和以及最高的child的高度
        int lineWidth = 0;
        int lineHeight = 0;

        //子View的数量
        int childCount = getChildCount();

        //测量子View的宽和高，进而计算ViewGroup的宽和高
        for(int i=0; i<childCount; i++) {

            View child = getChildAt(i);
            //测量子View，这句话要放在child.getLayoutParams前面
            measureChild(child,widthMeasureSpec,heightMeasureSpec);
            //获得child的LayoutParams，才可以获得child的leftMargin和rightMargin
//            LogUtils.d(child.getLayoutParams());
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            //获得child的宽度和高度
            int childWidth = child.getMeasuredWidth()+lp.leftMargin+lp.rightMargin;
            int childHeight = child.getMeasuredHeight()+lp.topMargin+lp.bottomMargin;

            //如果lineWidth+childWidth>widthSize，则表示这一行已经放不下这个childWidth了，需要换行
            //在换行时才计算叠加行高，此时还没进行下一行的处理
            if(lineWidth+childWidth > widthSize) {
                //对比获得该line的最大宽度
                destWidth = Math.max(destWidth,lineWidth);
                //重置lineWidth
                lineWidth = childWidth;

                //记录未换行之前的行高，即没有加上新换行的child的行高，此时的lineHeight是未换行之前的child最大的行高
                destHeight += lineHeight;
                //重置行高
                lineHeight = childHeight;
            } else {
                //否则不用换行
                //叠加行宽
                lineWidth += childWidth;
                //得到当前行最大高度
                lineHeight = Math.max(lineHeight,childHeight);
            }

            //如果是最后一个控件，因为还没有叠加这一行的行高，且没有判断这一行是不是比lineWidth更宽，所以需要判断
            if(i == childCount-1 ) {
                destWidth = Math.max(destWidth,lineWidth);
                destHeight += lineHeight;
            }

        }

        //如果测量模式为EXACTLY，即为精确值：100dp或match_parent，则为系统测量的值
        //否则测量械为AT_MOST，即为：wrap_content，则为我们自己测量的值
        setMeasuredDimension(
                widthMode == MeasureSpec.EXACTLY?widthSize:destWidth+getPaddingLeft()+getPaddingRight(),
                heightMode == MeasureSpec.EXACTLY?heightSize:destHeight+getPaddingTop()+getPaddingBottom()
        );
    }

    //记录所有行中每一行中的View
    private List<List<View>> mAllChildViews = new ArrayList<>();
    //记录所有的行高数据
    private List<Integer> mAllLineHeights = new ArrayList<>();

    /**
     * 设置child的位置
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mAllChildViews.clear();
        mAllLineHeights.clear();


        //获取所有的child数
        int childCount = getChildCount();

        //单独一行的宽和高
        int lineWidth = 0;
        int lineHeight = 0;

        //测量出的宽
        int destWidth = getWidth() - getPaddingLeft() - getPaddingRight();

        //记录单独一行中的所有View
        List<View> lineViews = new ArrayList<>();

        //获取子View的width和height信息
        for(int i=0; i<childCount ; i++) {

            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            //如果加上childWidth大于测量的行宽，则换行
            if(lineWidth + childWidth > destWidth) {
                //记录行高
                mAllLineHeights.add(lineHeight);
                //记录当前行的views
                mAllChildViews.add(lineViews);

                //重置行宽和行高
                lineWidth = 0;
                lineHeight = childHeight;
                lineViews = new ArrayList<>();
            }

            lineWidth += childWidth;
            lineHeight = Math.max(lineHeight,childHeight);
            lineViews.add(child);

        }

        //处理最后一行
        mAllLineHeights.add(lineHeight);
        mAllChildViews.add(lineViews);

        /************为子View设置布局********/
        //总行数
        int lineCount = mAllChildViews.size();
        //当前行子View的left和top
        int posLeft = getPaddingLeft();
        int posTop = getPaddingTop();

        for(int i=0; i<lineCount; i++) {
            List<View> childViews = mAllChildViews.get(i);

            for(int j=0; j<childViews.size(); j++) {
                View child = childViews.get(j);
                // 判断child的状态
                if (child.getVisibility() == View.GONE) {
                    continue;
                }

                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

                //获取Child View 的 left,top,right,bottom位置
                int childLeft = posLeft + lp.leftMargin;
                int childRight = childLeft + child.getMeasuredWidth();
                int childTop = posTop + lp.topMargin;
                int childBottom = childTop + child.getMeasuredHeight();

                //设置child layout数据
                child.layout(childLeft,childTop,childRight,childBottom);

                //叠加left数据
                posLeft += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            }

            //循环一次之后，新开一行，重置postLeft，叠加postTop
            posLeft = getPaddingLeft();
            posTop += mAllLineHeights.get(i);
        }

    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(),attrs);
    }


}
