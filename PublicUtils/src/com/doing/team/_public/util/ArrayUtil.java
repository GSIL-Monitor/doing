/**
 * @author mudelong
 * @since 2014年9月5日 下午12:04:36
 */

package com.doing.team._public.util;

import java.util.ArrayList;
import java.util.Arrays;

public class ArrayUtil {

    /**
     * 找不同 a有b没有
     * @param a
     * @param b
     */
    public static ArrayList<Integer> getDiffArray(int a[], int b[]) {
        Arrays.sort(a);
        Arrays.sort(b);
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0, size = a.length; i < size; i++) {
            int index = Arrays.binarySearch(b, a[i]);
            if (index < 0) {
                list.add(a[i]);
            }
        }
        return list;
    }
}
