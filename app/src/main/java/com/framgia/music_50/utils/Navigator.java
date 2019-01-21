package com.framgia.music_50.utils;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class Navigator {
    private Activity mActivity;
    private Fragment mFragment;

    public Navigator(Activity activity) {
        mActivity = activity;
    }

    public Navigator(Fragment fragment) {
        mFragment = fragment;
        mActivity = fragment.getActivity();
    }

    public void goNextChildFragment(FragmentManager fragmentManager, int containerViewId,
            Fragment fragment, boolean addToBackStack, String rootTag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment currentFragment =
                fragmentManager.findFragmentByTag(fragment.getClass().getSimpleName());
        if (currentFragment == null) {
            currentFragment = fragment;
            transaction.add(containerViewId, fragment, fragment.getClass().getSimpleName());
        }
        if (addToBackStack) {
            transaction.addToBackStack(rootTag);
        } else {
            showFragment(fragmentManager, currentFragment);
        }
        transaction.commit();
    }

    private void showFragment(FragmentManager fragmentManager, Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for (int i = 0; i < fragmentManager.getFragments().size(); i++) {
            transaction.hide(fragmentManager.getFragments().get(i));
        }
        transaction.show(fragment);
        transaction.commit();
    }

    public void removeFragment(FragmentManager fragmentManager, String rootTag) {
        fragmentManager.popBackStack(rootTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Fragment fragment = fragmentManager.findFragmentByTag(rootTag);
        showFragment(fragmentManager, fragment);
    }
}
