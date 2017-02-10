package matei.personalassistant.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class FragmentUtils {

    public static void addFragment(FragmentManager fragmentManager, Class fragmentClass,
                                   int containerViewId) {

        addFragment(fragmentManager, fragmentClass, containerViewId, null, false, null);
    }

    public static void addFragment(FragmentManager fragmentManager, Class fragmentClass,
                                   int containerViewId, String tag) {

        addFragment(fragmentManager, fragmentClass, containerViewId, null, false, tag);
    }


    public static void addFragment(FragmentManager fragmentManager, Class fragmentClass,
                                   int containerViewId, Bundle bundle) {
        addFragment(fragmentManager, fragmentClass, containerViewId, bundle, false, null);
    }

    public static void addFragment(FragmentManager fragmentManager, Class fragmentClass,
                                   int containerViewId, boolean addToBackStack) {
        addFragment(fragmentManager, fragmentClass, containerViewId, null, addToBackStack, null);
    }

    public static void addFragment(FragmentManager fragmentManager, Class fragmentClass,
                                   int containerViewId, Bundle bundle, boolean addToBackStack) {
        addFragment(fragmentManager, fragmentClass, containerViewId, bundle, addToBackStack, null);
    }

    public static void addFragment(FragmentManager fragmentManager, Class fragmentClass,
                                   int containerViewId, Bundle bundle,
                                   String tag) {
        addFragment(fragmentManager, fragmentClass, containerViewId, bundle, false, tag);
    }

    public static void addFragment(FragmentManager fragmentManager, Class fragmentClass,
                                   int containerViewId, Bundle bundle, boolean addToBackStack,
                                   String tag) {

        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            if (bundle != null) {
                fragment.setArguments(bundle);
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (tag != null) {
            transaction.add(containerViewId, fragment, tag);
        } else {
            transaction.add(containerViewId, fragment);
        }

        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
    }

    public static void replaceFragment(FragmentManager fragmentManager, Class fragmentClass,
                                       int containerViewId) {

        replaceFragment(fragmentManager, fragmentClass, containerViewId, null, false, null);
    }

    public static void replaceFragment(FragmentManager fragmentManager, Class fragmentClass,
                                       int containerViewId, String tag) {

        replaceFragment(fragmentManager, fragmentClass, containerViewId, null, false, tag);
    }

    public static void replaceFragment(FragmentManager fragmentManager, Class fragmentClass,
                                       int containerViewId, Bundle bundle) {
        replaceFragment(fragmentManager, fragmentClass, containerViewId, bundle, false, null);
    }

    public static void replaceFragment(FragmentManager fragmentManager, Class fragmentClass,
                                       int containerViewId, boolean addToBackStack) {
        replaceFragment(fragmentManager, fragmentClass, containerViewId, null, addToBackStack, null);
    }

    public static void replaceFragment(FragmentManager fragmentManager, Class fragmentClass,
                                       int containerViewId, Bundle bundle, boolean addToBackStack) {
        replaceFragment(fragmentManager, fragmentClass, containerViewId, bundle, addToBackStack, null);
    }

    public static void replaceFragment(FragmentManager fragmentManager, Class fragmentClass,
                                       int containerViewId, Bundle bundle, boolean addToBackStack,
                                       String tag) {
        try {

            Fragment fragment = null;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                if (bundle != null) {
                    fragment.setArguments(bundle);
                }

            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (tag != null) {
                transaction.replace(containerViewId, fragment, tag);
            } else {
                transaction.replace(containerViewId, fragment);
            }
            if (addToBackStack) {
                transaction.addToBackStack(null);
            }
            transaction.commit();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}