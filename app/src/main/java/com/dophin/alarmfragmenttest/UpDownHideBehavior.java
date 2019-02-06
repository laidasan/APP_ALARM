package com.dophin.alarmfragmenttest;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;

public class UpDownHideBehavior extends CoordinatorLayout.Behavior<View>{

    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

    private float viewY;
    private boolean isAnimate;

    public UpDownHideBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll( CoordinatorLayout coordinatorLayout,  View child,  View directTargetChild,  View target, int axes,int type) {

        if(child.getVisibility() == View.VISIBLE&&viewY == 0) {
            viewY = coordinatorLayout.getHeight() - child.getY();
        }
        //return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) !=0;
        return (axes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout,child,directTargetChild,target,axes,type));
    }

    @Override
    public void onNestedPreScroll( CoordinatorLayout coordinatorLayout,  View child, View target, int dx, int dy, int[] consumed,int type) {
        if(dy > 0 &&!isAnimate&&child.getVisibility()==View.VISIBLE) {
            hide(child);
        }else if (dy < 0&& !isAnimate && child.getVisibility() != View.VISIBLE) {
            show(child);
        }

        //super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
    }

    private void hide(final View view) {
        ViewPropertyAnimator animator = view.animate().translationY(viewY).setInterpolator(INTERPOLATOR).setDuration(200);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isAnimate = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(View.INVISIBLE);
                isAnimate = false;

            }

            @Override
            public void onAnimationCancel(Animator animator) {
                show(view);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
            animator.start();

    }

    private void show(final View view) {
        ViewPropertyAnimator animator = view.animate().translationY(0).setInterpolator(INTERPOLATOR).setDuration(200);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                view.setVisibility(View.VISIBLE);
                isAnimate = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isAnimate = false;

            }

            @Override
            public void onAnimationCancel(Animator animator) {
                hide(view);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }


}
