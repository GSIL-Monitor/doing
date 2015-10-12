package com.doing.team._public.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MemoryUtils {


    /** @return 返回内存占用的百分比，0-100 */
    public static int getMemoryUsedPercent() {
        long totalSize = getMemoryTotalKb();
        long freeSize = getMemoryFreeKb();

        if (totalSize > 0 && freeSize > 0) {
            return (int) ((totalSize - freeSize) * 100 / totalSize);
        } else {
            return 0;
        }
    }

    /**
     * 返回一键清理之后的内存占用百分比，0-100
     * @param freeMemoryInKb 清理掉的内存占用值，单位kb
     * @return
     */
    public static int getMemoryUsedPercentAfterClean(long freeMemoryInKb) {
        long totalSize = getMemoryTotalKb();
        long freeSize = getMemoryFreeKb();

        if (totalSize > 0 && freeSize > 0) {
            return (int) ((totalSize - freeSize - freeMemoryInKb) * 100 / totalSize);
        } else {
            return 0;
        }
    }

    private static long getMemoryTotalKb() {
        long totalSize = -1L;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/meminfo"));
            String line, totle = null;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("MemTotal:")) {
                    totle = line.split(" +")[1];
                    break;
                }
            }

            totalSize = Long.valueOf(totle);

            return totalSize;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }

        return totalSize;
    }

    private static long getMemoryFreeKb() {
        long freeSize = -1L;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/meminfo"));
            String line, buff = null, cache = null, free = null;
            int count = 0;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("MemFree")) {
                    count++;
                    free = line.split(" +")[1];
                    if (count >= 3) {
                        break;
                    }
                } else if (line.startsWith("Buffers")) {
                    count++;
                    buff = line.split(" +")[1];
                    if (count >= 3) {
                        break;
                    }
                } else if (line.startsWith("Cached")) {
                    count++;
                    cache = line.split(" +")[1];
                    if (count >= 3) {
                        break;
                    }
                } else {
                    continue;
                }
            }

            freeSize = (Long.valueOf(free) + Long.valueOf(buff) + Long.valueOf(cache));

            return freeSize;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        return freeSize;
    }

}
