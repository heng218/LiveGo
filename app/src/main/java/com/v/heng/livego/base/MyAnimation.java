package com.v.heng.livego.base;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

public class MyAnimation {
	
	/**
	 * 缩小后放大
	 */
	public static void scaleOnBtnClick(View view) {
		Animation animation = new ScaleAnimation(1.0f, 0.8f, 1.0f, 0.8f, 
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(50);
		view.startAnimation(animation);
	}
	
//	
//	// 移动->下 
//	public static void translateToDown(final ViewGroup viewgroup, int durationMillis) {
//		Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
//				0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
//				Animation.RELATIVE_TO_SELF, 1.0f);
//		animation.setFillAfter(true);
//		animation.setDuration(durationMillis);
//		animation.setAnimationListener(new Animation.AnimationListener() {
//			@Override
//			public void onAnimationStart(Animation animation) {
//				
//			}
//			@Override
//			public void onAnimationRepeat(Animation animation) {
//			}
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				viewgroup.setVisibility(View.GONE);
//			}
//		});
//		viewgroup.startAnimation(animation);
//	}
//	
//	
//	// 背景move 
//	public static void translateBg(final View view, int durationMillis) {
//		final Animation animationToLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0f,
//				Animation.RELATIVE_TO_PARENT, -0.5f,
//				Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT,
//				0f);
//		animationToLeft.setFillAfter(true);
//		animationToLeft.setDuration(durationMillis);
//		
//		
//		final Animation animationToRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -0.5f,
//				Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT,
//				0f, Animation.RELATIVE_TO_PARENT, 0f);
//		
//		animationToRight.setFillAfter(true);
//		animationToRight.setDuration(durationMillis);
//		animationToRight.setAnimationListener(new Animation.AnimationListener() {
//			@Override
//			public void onAnimationStart(Animation animation) {
//				
//			}
//			@Override
//			public void onAnimationRepeat(Animation animation) {
//			}
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				view.startAnimation(animationToLeft);
//			}
//		});
//		
//		animationToLeft.setAnimationListener(new Animation.AnimationListener() {
//			@Override
//			public void onAnimationStart(Animation animation) {
//				
//			}
//			@Override
//			public void onAnimationRepeat(Animation animation) {
//			}
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				view.startAnimation(animationToRight);
////				System.out.println("animationToRight");
//			}
//		});
//		
//		view.startAnimation(animationToLeft);
//		
//		
//	}
//	
	
	
	
	
	
	
	
	
	
	
	
//	// 图标的动画(入动画)
//	public static void startAnimationsIn(ViewGroup viewgroup, int durationMillis) {
//
//		viewgroup.setVisibility(0);
//		for (int i = 0; i < viewgroup.getChildCount(); i++) {
//			viewgroup.getChildAt(i).setVisibility(0);
//			viewgroup.getChildAt(i).setClickable(true);
//			viewgroup.getChildAt(i).setFocusable(true);
//		}
//		Animation animation;
//		animation = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF,
//				0.5f, Animation.RELATIVE_TO_SELF, 1.0f);
//		animation.setFillAfter(true);
//		animation.setDuration(durationMillis);
//		viewgroup.startAnimation(animation);
//
//	}
//
//	// 图标的动画(出动画)
//	public static void startAnimationsOut(final ViewGroup viewgroup,
//			int durationMillis, int startOffset) {
//
//		Animation animation;
//		
//		animation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF,
//				0.5f, Animation.RELATIVE_TO_SELF, 1.0f);
//		animation.setFillAfter(true);
//		animation.setDuration(durationMillis);
//		animation.setStartOffset(startOffset);
//		animation.setAnimationListener(new Animation.AnimationListener() {
//			@Override
//			public void onAnimationStart(Animation arg0) {}
//			@Override
//			public void onAnimationRepeat(Animation arg0) {}
//			@Override
//			public void onAnimationEnd(Animation arg0) {
//				viewgroup.setVisibility(8);
//				for (int i = 0; i < viewgroup.getChildCount(); i++) {
//					viewgroup.getChildAt(i).setVisibility(8);
//					viewgroup.getChildAt(i).setClickable(false);
//					viewgroup.getChildAt(i).setFocusable(false);
//				}
//			}
//		});
//		viewgroup.startAnimation(animation);
//	}

	
	
	
	
}







