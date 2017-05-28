package com.example.jeka.exampledrawerbar.Utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by jeka on 29.05.2017.
 */

public class AnimationUtils {

    public static void show(View view){
        AnimatorSet floatingButtonAnimate = new AnimatorSet();
        floatingButtonAnimate.setDuration(200).playTogether(
                ObjectAnimator.ofFloat(view, View.SCALE_X, 0f, 1f),
                ObjectAnimator.ofFloat(view, View.SCALE_Y, 0f, 1f)
        );
        floatingButtonAnimate.setStartDelay(150);
        floatingButtonAnimate.start();
        view.setVisibility(View.VISIBLE);
    }

    public static void showListItems(final RecyclerView recyclerView){
        recyclerView.getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);

                        for (int i = 0; i < recyclerView.getChildCount(); i++) {
                            View item = recyclerView.getChildAt(i);
                            AnimatorSet floatingButtonAnimate= new AnimatorSet();
                            floatingButtonAnimate.setDuration(200).playTogether(
                                    ObjectAnimator.ofFloat(item, View.SCALE_X, 0f, 1f),
                                    ObjectAnimator.ofFloat(item, View.SCALE_Y, 0f, 1f)
                            );
                            floatingButtonAnimate.setStartDelay(i * 70);
                            floatingButtonAnimate.start();
                        }
                        return true;
                    }
                });
    }

}
