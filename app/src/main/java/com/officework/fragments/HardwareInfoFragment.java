package com.officework.fragments;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.officework.R;
import com.officework.base.BaseFragment;
import com.officework.constants.AppConstantsTheme;
import com.officework.constants.Constants;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.Utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by Girish on 7/29/2016.
 */
public class HardwareInfoFragment extends BaseFragment {
    View view;
    Utilities utils;

    Context ctx;
    /*	Processor*/
    TextView mtvProcessor, mtvHardware, mtvCPU, mtvProcess, mtvCores, mtvFrequencies, mtvGovernor,processorHeading;
    /*	Graphics*/
    TextView mtvVendor, mtvGPU, mtvOpenGL, mtvMaxFreguency, mtvGraphicGovernor, mtvResolution, mtvScreenDensity, mtvScreenSize, mtvFrameRate;
    /*	Memory*/
    TextView mtvRamSize;
    /*	STORAGE*/
    TextView mtvInternalStorage, mtvStorageAvailable, mtvSystemStorage, mtvStorageAvailable_Card, mtvIOScheduler, mtvReadAhead, tvAvailableRamSize, tvUsedRamSize,graphicsHeading,memoryHeading,storageHeading;

    /*Constant Flags for Frequency*/
    int MIN_FREQ = 0, MAX_FREQ = 1, CURRENT_FREQ = 2;

    @Override

    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_hardware, null);
                utils = Utilities.getInstance(getActivity());
                ctx = getActivity();
             //   Crashlytics.getInstance().log(FragmentTag.HARDWARE_INFO_FRAGMENT.name());
                initViews();
            }
            return view;
        } catch (Exception e) {
            logException(e, "HardwareInfoFragment_initUI()");
            return null;
        }

    }

    public HardwareInfoFragment() {
    }

/*
    There are some methods which has deprecated by Google since 2013 and you might change these methods (API 18+ only):

    getAvailableBlocks() to getAvailableBlocksLong()
    getBlockCount() to getBlockCountLong()
    getBlockSize() to getBlockSizeLong()
    getFreeBlocks() to getFreeBlocksLong()*/


    /**
     * Initialize view
     */

    private void initViews() {
        try {
            /*Processor*/
            tvUsedRamSize = (TextView) view.findViewById(R.id.tvUsedRamSize);
            tvAvailableRamSize = (TextView) view.findViewById(R.id.tvAvailableRamSize);
            mtvProcessor = (TextView) view.findViewById(R.id.txtViewProcessor);
            mtvHardware = (TextView) view.findViewById(R.id.txtViewHardware);
            mtvCPU = (TextView) view.findViewById(R.id.txtViewCPU);
            mtvProcess = (TextView) view.findViewById(R.id.txtViewProcess);
            mtvCores = (TextView) view.findViewById(R.id.txtViewCores);
            mtvFrequencies = (TextView) view.findViewById(R.id.txtViewFrequencies);
            mtvGovernor = (TextView) view.findViewById(R.id.txtViewGovernor);

            /*	Graphics*/
            mtvVendor = (TextView) view.findViewById(R.id.tvGraphicVendor);
            mtvGPU = (TextView) view.findViewById(R.id.tvGraphicGPU);
            mtvOpenGL = (TextView) view.findViewById(R.id.tvGraphicOpenGL);
            mtvMaxFreguency = (TextView) view.findViewById(R.id.tvGraphicMaxFreg);
            mtvGraphicGovernor = (TextView) view.findViewById(R.id.tvGraphicGovernor);
            mtvResolution = (TextView) view.findViewById(R.id.tvGraphicResolution);
            mtvScreenDensity = (TextView) view.findViewById(R.id.tvGraphicDensity);
            mtvScreenSize = (TextView) view.findViewById(R.id.tvGraphicScreenSize);
            mtvFrameRate = (TextView) view.findViewById(R.id.tvGraphicFrameRate);

            /*	Memory*/
            mtvRamSize = (TextView) view.findViewById(R.id.tvMemoryRamSize);

            /*	STORAGE*/
            mtvInternalStorage = (TextView) view.findViewById(R.id.tvInternalStorage);
            mtvStorageAvailable = (TextView) view.findViewById(R.id.tvStorageAvailable);
            mtvSystemStorage = (TextView) view.findViewById(R.id.tvSystemStorage);
            mtvStorageAvailable_Card = (TextView) view.findViewById(R.id.tvStorageAvailable_card);
            mtvIOScheduler = (TextView) view.findViewById(R.id.tvIOScheduler);
            mtvReadAhead = (TextView) view.findViewById(R.id.tvReadAhead);
            processorHeading=(TextView)view.findViewById(R.id.processorheading);
            graphicsHeading=(TextView)view.findViewById(R.id.graphicsHeading);
            memoryHeading=(TextView)view.findViewById(R.id.memoryheading);
            storageHeading=(TextView)view.findViewById(R.id.storageheading);
//            int color= Color.parseColor(new ThemeManager(getActivity()).getTheme());
            int color= AppConstantsTheme.getIconColor();
            processorHeading.setTextColor(color);
            graphicsHeading.setTextColor(color);
            memoryHeading.setTextColor(color);
            storageHeading.setTextColor(color);
            getHardwareInfo();
            getStorageInfo();
            getGraphicInfo();
            doSomethingMemoryIntensive();

            utils.addLog(ctx, "Hardware Info", getInfo());
        } catch (Exception e) {
            logException(e, "HardwareInfoFragment_initViews()");
        }

    }

    /**
     * This method is used to fetch hardware information
     */

    private void getHardwareInfo() {
        try {
            mtvProcessor.setText(fetch_cpu_info());
            mtvHardware.setText(Build.HARDWARE);
            mtvRamSize.setText(getRamSize());
            tvUsedRamSize.setText(getRAMStatus(2));
            tvAvailableRamSize.setText(getRAMStatus(1));
            mtvVendor.setText(Build.PRODUCT);
            mtvCores.setText(String.valueOf(Runtime.getRuntime().availableProcessors()));
            mtvGovernor.setText(utils.getInfo(Constants.FILE_PATH_GOVERNOR));
            mtvFrequencies.setText(getFrequency(MIN_FREQ) + " - " + getFrequency(MAX_FREQ));
        } catch (Exception e) {
            logException(e, "HardwareInfoFragment_getHardwareInfo()");
        }

    }

    /**
     * This will return frequency based on the @parameters based to it.
     *
     * @param type
     * @return Min Freq and Max Freq
     */
    private String getFrequency(int type) {
        try {
            String Freq = "";
            switch (type) {
                case 0:
                    Freq = utils.getInfo(Constants.FILE_PATH_MIN_FREQUENCY);
                    break;
                case 1:
                    Freq = utils.getInfo(Constants.FILE_PATH_MAX_FREQUENCY);
                    break;
                case 2:
                    Freq = utils.getInfo(Constants.FILE_PATH_CURRENT_FREQUENCY);
                    break;
            }

            if (Freq.length() < 4) {
                return Freq;
            } else if (Freq.length() < 8) {
                return Integer.valueOf(Freq) / 1000 + " MHz";
            } else {
                return Integer.valueOf(Freq) / 1000000 + " MHz";
            }
        } catch (Exception e) {
            logException(e, "HardwareInfoFragment_getFrequency()");
            return "";
        }

    }

    /**
     * This method find the storage information
     */
    private void getStorageInfo() {
        try {
            mtvInternalStorage.setText(getTotalInternalMemorySize());
            mtvStorageAvailable.setText(getAvailableInternalMemorySize());
            mtvSystemStorage.setText(bytesToHuman(totalMemory()));
            mtvStorageAvailable_Card.setText(bytesToHuman(freeMemory()));
        } catch (Exception e) {
            logException(e, "HardwareInfoFragment_getStorageInfo()");
        }

    }

    /**
     * This method find the Graphic information
     */

    private void getGraphicInfo() {
        try {
            //Screen Resolution
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;
            mtvResolution.setText(String.valueOf(height) + "x" + String.valueOf(width));

            //Screen Density
            int densityDpi = (int) (displaymetrics.density * 160f);
            mtvScreenDensity.setText(String.valueOf(densityDpi) + "dpi");

            double x = Math.pow(displaymetrics.widthPixels / displaymetrics.xdpi, 2);
            double y = Math.pow(displaymetrics.heightPixels / displaymetrics.ydpi, 2);
            double screenInches = Math.sqrt(x + y);

            screenInches = (double) Math.round(screenInches * 10) / 10;
            mtvScreenSize.setText(String.valueOf(screenInches));

            // Max Frequency
            mtvMaxFreguency.setText(getFrequency(CURRENT_FREQ));
        } catch (Exception e) {
            logException(e, "HardwareInfoFragment_getGraphicInfo()");
        }


    }

    /**
     * This Method is used to find processor information
     */

    public String fetch_cpu_info() {
        StringBuffer buffer;
        String result = null;

        try {
            String[] args = {"/system/bin/cat", "/proc/cpuinfo"};
            result = run(args, "/system/bin/");
            Log.i("result", "result=" + result);
        } catch (IOException ex) {
            ex.printStackTrace();
            logException(ex, "HardwareInfoFragment_fetch_cpu_info()");

        }
        return result;
    }

    public synchronized String run(String[] cmd, String workdirectory)
            throws IOException {
        String result = "";

        try {
            ProcessBuilder builder = new ProcessBuilder(cmd);
            // set working directory
            if (workdirectory != null)
                builder.directory(new File(workdirectory));
            builder.redirectErrorStream(true);
            Process process = builder.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[1024];
            while (in.read(re) != -1) {
                System.out.println(new String(re));
                result = result + new String(re);
            }
            in.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    //Processor info ends

    /**
     * These methods fing the storage information
     *
     * @return
     */
    public static boolean externalMemoryAvailable() {
        try {
            return android.os.Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            HardwareInfoFragment hardwareInfoFragment = new HardwareInfoFragment();
            hardwareInfoFragment.logException(e, "HardwareInfoFragment_getAvailableInternalMemorySize()");
            return false;
        }

    }

    public static String getAvailableInternalMemorySize() {
        try {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return bytesToHuman(availableBlocks * blockSize);
        } catch (Exception e) {
            HardwareInfoFragment hardwareInfoFragment = new HardwareInfoFragment();
            hardwareInfoFragment.logException(e, "HardwareInfoFragment_getAvailableInternalMemorySize()");
            return "";
        }

    }

    public static String getTotalInternalMemorySize() {
        try {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return bytesToHumanTotal(totalBlocks * blockSize);
        } catch (Exception e) {
            HardwareInfoFragment hardwareInfoFragment = new HardwareInfoFragment();
            hardwareInfoFragment.logException(e, "HardwareInfoFragment_getTotalInternalMemorySize()");
            return "";
        }

    }

//    public static String getUsedInternalStorage(){
//        File path = Environment.getDataDirectory();
//        StatFs stat = new StatFs(path.getPath());
//        long blockSize = stat.getBlockSize();
//        long totalBlocks = stat.getBlockCount();
//        long totalKblong = (totalBlocks * blockSize);
//
//        long availableBlocks = stat.getAvailableBlocks();
//        long totalavailableBlocks=(availableBlocks*blockSize);
//        long usedStorage=totalKblong-totalavailableBlocks;
//        return String.valueOf(usedStorage);
//
//    }

    public static String getUsedInternalStorage(){
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        long totalKblong = (totalBlocks * blockSize);

        long availableBlocks = stat.getAvailableBlocks();
        long totalavailableBlocks=(availableBlocks*blockSize);
        long usedStorage=totalKblong-totalavailableBlocks;
//        new DecimalFormat("##.##").format(usedStorage/1e+9);
        String valueGb=String.valueOf(new DecimalFormat("##.##").format(usedStorage/1e+9))+" GB";

        return valueGb;

    }
    /**
     * This will return device total memory in
     * bytes
     */
    public static String getTotalInternalMemorySizeinKB() {
        try {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            long totalKblong = (totalBlocks * blockSize);
            long totalKb = (totalKblong) / 1024;
            return String.valueOf(totalKblong);
        } catch (Exception e) {
            HardwareInfoFragment hardwareInfoFragment = new HardwareInfoFragment();
            hardwareInfoFragment.logException(e, "HardwareInfoFragment_getTotalInternalMemorySizeinKB()");
            return "";
        }

    }

    public static String getAvailableExternalMemorySize() {
        try {
            if (externalMemoryAvailable()) {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                long availableBlocks = stat.getAvailableBlocks();
                return formatSize(availableBlocks * blockSize);
            } else {
                return "ERROR";
            }
        } catch (Exception e) {
            HardwareInfoFragment hardwareInfoFragment = new HardwareInfoFragment();
            hardwareInfoFragment.logException(e, "HardwareInfoFragment_getAvailableExternalMemorySize()");
            return "";
        }

    }

    public static String getTotalExternalMemorySize() {
        try {
            if (externalMemoryAvailable()) {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                long totalBlocks = stat.getBlockCount();
                return formatSize(totalBlocks * blockSize);
            } else {
                return "ERROR";
            }
        } catch (Exception e) {
            HardwareInfoFragment hardwareInfoFragment = new HardwareInfoFragment();
            hardwareInfoFragment.logException(e, "HardwareInfoFragment_getTotalExternalMemorySize()");
            return "";
        }

    }

    public static String formatSize(long size) {
        try {
            String suffix = null;

            if (size >= 1024) {
                suffix = " KB";
                size /= 1024;
                if (size >= 1024) {
                    suffix = " MB";
                    size /= 1024;
                }
            }

            StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

            int commaOffset = resultBuffer.length() - 3;
            while (commaOffset > 0) {
                resultBuffer.insert(commaOffset, ',');
                commaOffset -= 3;
            }

            if (suffix != null) resultBuffer.append(suffix);
            return resultBuffer.toString();
        } catch (Exception e) {
            HardwareInfoFragment hardwareInfoFragment = new HardwareInfoFragment();
            hardwareInfoFragment.logException(e, "HardwareInfoFragment_formatSize()");
            return "";
        }

    }

    ///

    public long totalMemory() {
        try {
            StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
            long total;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                total = (statFs.getBlockCountLong() * statFs.getBlockSizeLong());
            } else {
                total = (statFs.getBlockCount() * statFs.getBlockSize());
            }


            return total;
        } catch (Exception e) {
            logException(e, "HardwareInfoFragment_totalMemory()");
            return 0L;

        }

    }

    public long freeMemory() {

        try {
            StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
            long free = (statFs.getAvailableBlocks() * statFs.getBlockSize());
            return free;
        } catch (Exception e) {
            logException(e, "HardwareInfoFragment_freeMemory()");
            return 0L;

        }

    }

    public long busyMemory() {
        try {
            StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
            long total = (statFs.getBlockCount() * statFs.getBlockSize());
            long free = (statFs.getAvailableBlocks() * statFs.getBlockSize());
            long busy = total - free;
            return busy;
        } catch (Exception e) {
            logException(e, "HardwareInfoFragment_busyMemory()");
            return 0L;

        }

    }

    public static String floatForm(double d) {
        try {
            return new DecimalFormat("#.##").format(d);
        } catch (Exception e) {
            HardwareInfoFragment hardwareInfoFragment = new HardwareInfoFragment();
            hardwareInfoFragment.logException(e, "HardwareInfoFragment_floatForm()");
            return "";
        }

    }


    public static String bytesToHuman(long size) {
        try {
            long Kb = 1 * 1024;
            long Mb = Kb * 1024;
            long Gb = Mb * 1024;
            long Tb = Gb * 1024;
            long Pb = Tb * 1024;
            long Eb = Pb * 1024;

            if (size < Kb) return floatForm(size) + " byte";
            if (size >= Kb && size < Mb) return floatForm((double) size / Kb) + " Kb";
            if (size >= Mb && size < Gb) return floatForm((double) size / Mb) + " MB";
            if (size >= Gb && size < Tb) return floatForm((double) size / Gb) + " GB";

            if (size >= Tb && size < Pb) return floatForm((double) size / Tb) + " TB";
            if (size >= Pb && size < Eb) return floatForm((double) size / Pb) + " PB";
            if (size >= Eb) return floatForm((double) size / Eb) + " Eb";

            return "???";
        } catch (Exception e) {
            HardwareInfoFragment hardwareInfoFragment = new HardwareInfoFragment();
            hardwareInfoFragment.logException(e, "HardwareInfoFragment_bytesToHuman()");
            return "";
        }

    }

    public static String bytesToHumanTotal(long size) {
        try {
            long Kb = 1 * 1024;
            long Mb = Kb * 1024;
            long Gb = Mb * 1024;
            long Tb = Gb * 1024;
            long Pb = Tb * 1024;
            long Eb = Pb * 1024;

            if (size < Kb) return floatForm(size) + " byte";
            if (size >= Kb && size < Mb) return floatForm((double) size / Kb) + " Kb";
            if (size >= Mb && size < Gb) {
                if ((size / Gb) < 1) {
                    return "1 GB";
                } else if ((size / Gb) < 2.5) {
                    return "2 GB";
                } else if ((size / Gb) < 4.0) {
                    return "4 GB";
                } else if ((size / Gb) < 8.0) {
                    return "8 GB";
                } else if ((size / Gb) < 16.0) {
                    return "16 GB";
                } else if ((size / Gb) < 32.0) {
                    return "32 GB";
                } else if ((size / Gb) < 64.0) {
                    return "64 GB";
                } else if ((size / Gb) < 128.0) {
                    return "128 GB";
                } else if ((size / Gb) < 256.0) {
                    return "256 GB";
                } else if ((size / Gb) < 512.0) {
                    return "512 GB";
                }else if((size / Gb) < 512.0){

                }
                //return floatForm((double) size / Mb) + " Mb";
            }
            if (size >= Gb && size < Tb) {
                if ((size / Gb) < 1) {
                    return "1 GB";
                } else if ((size / Gb) < 2.0) {
                    return "2 GB";
                } else if ((size / Gb) < 4.0) {
                    return "4 GB";
                } else if ((size / Gb) < 8.0) {
                    return "8 GB";
                } else if ((size / Gb) < 16.0) {
                    return "16 GB";
                } else if ((size / Gb) < 32.0) {
                    return "32 GB";
                } else if ((size / Gb) < 64.0) {
                    return "64 GB";
                } else if ((size / Gb) < 128.0) {
                    return "128 GB";
                } else if ((size / Gb) < 256.0) {
                    return "256 GB";
                } else if ((size / Gb) < 512.0) {
                    return "512 GB";
                }else if((size / Gb) > 512.0){
                    return "1 TB";
                }
                //return floatForm((double) size / Gb) + " Gb";
            }

            if (size >= Tb && size < Pb) return floatForm((double) size / Tb) + " Tb";
            if (size >= Pb && size < Eb) return floatForm((double) size / Pb) + " Pb";
            if (size >= Eb) return floatForm((double) size / Eb) + " Eb";

            return "???";
        } catch (Exception e) {
            HardwareInfoFragment hardwareInfoFragment = new HardwareInfoFragment();
            hardwareInfoFragment.logException(e, "HardwareInfoFragment_bytesToHumanTotal()");
            return "";
        }

    }

    private String getRamSize() {
        try {
            return getTotalRAM();
        } catch (Exception e) {
            logException(e, "HardwareInfoFragment_getRamSize()");
            return "";
        }

    }


    public String getTotalRAM() {

        RandomAccessFile reader = null;
        String load = null;
        DecimalFormat twoDecimalForm = new DecimalFormat("#.##");
        double totRam = 0;
        String lastValue = "";
        try {
            reader = new RandomAccessFile("/proc/meminfo", "r");
            load = reader.readLine();

            // Get the Number value from the string
            Pattern p = Pattern.compile("(\\d+)");
            Matcher m = p.matcher(load);
            String value = "";
            while (m.find()) {
                value = m.group(1);
                // System.out.println("Ram : " + value);
            }
            reader.close();

            totRam = Double.parseDouble(value);
            // totRam = totRam / 1024;

            double mb = totRam / 1024.0;
            double gb = totRam / 1048576.0;
            double tb = totRam / 1073741824.0;

            if (tb > 1) {
                lastValue = twoDecimalForm.format(tb).concat(" TB");
            } else if (gb > 1) {
                //  lastValue = twoDecimalForm.format(gb).concat(" GB");
                if (gb < 2) {
                    lastValue = "2 GB";
                } else if (gb < 3) {
                    lastValue = "3 GB";
                } else if (gb < 4) {
                    lastValue = "4 GB";
                } else if (gb < 6) {
                    lastValue = "6 GB";
                } else if (gb < 8) {
                    lastValue = "8 GB";
                } else if (gb < 16) {
                    lastValue = "16 GB";
                }


            } else if (mb > 1) {
                if (mb >= 600)
                    lastValue = "1".concat(" GB");
                else if (mb <= 600) {
                    lastValue = "512".concat(" MB");
                }

                // lastValue = twoDecimalForm.format(mb).concat(" MB");
            } else {
                lastValue = twoDecimalForm.format(totRam).concat(" KB");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            logException(ex, "HardwareInfoFragment_getTotalRAM()");
        } finally {
            // Streams.close(reader);
        }

        return lastValue;
    }

    private String getInfo() {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("abi: ").append(Build.CPU_ABI).append("n");
            if (new File("/proc/cpuinfo").exists()) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(new File("/proc/cpuinfo")));
                    String aLine;
                    while ((aLine = br.readLine()) != null) {
                        sb.append(aLine + "n");
                    }
                    if (br != null) {
                        br.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        } catch (Exception e) {
            logException(e, "HardwareInfoFragment_getInfo()");
            return "";
        }

    }


    public void logException(Exception e, String methodName) {
        try {
            Utilities utilities = Utilities.getInstance(getActivity());
            Activity activity = getActivity();
            Context context = getActivity();
            ExceptionLogUtils exceptionLogUtils = new ExceptionLogUtils(utilities, context, activity);
            exceptionLogUtils.addExceptionLog(utilities, context, activity, null, null, e, methodName);
        } catch (Exception exp) {
            //    logException(exp, "HardwareInfoFragment_logException()");
        }

    }


    public void doSomethingMemoryIntensive() {

        // Before doing something that requires a lot of memory,
        // check to see whether the device is in a low memory state.
        ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();
        Log.d("Total Memory : ", String.valueOf(memoryInfo.totalMem / 1048576L));
        Log.d("Available Memory : ", String.valueOf(memoryInfo.availMem / 1048576L));
        Log.d("Used Memory : ", String.valueOf((memoryInfo.totalMem - memoryInfo.availMem) / 1048576L));
        Log.d("Available Memory Hu: ", bytesToHuman(memoryInfo.availMem));
        Log.d("Used Memory Hu: ", bytesToHuman(memoryInfo.totalMem - memoryInfo.availMem));
      /*  if (!memoryInfo.lowMemory) {
            // Do memory intensive work ...
        }*/
    }

    private String getRAMStatus(int position) {

        String ram = "";
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        switch (position) {
            case 1:
                ram = bytesToHuman(memoryInfo.availMem);
                break;

            case 2:
                ram = bytesToHuman(memoryInfo.totalMem - memoryInfo.availMem);
                break;


        }
        return ram;
    }


    // Get a MemoryInfo object for the device's current memory status.
    private ActivityManager.MemoryInfo getAvailableMemory() {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }
}
