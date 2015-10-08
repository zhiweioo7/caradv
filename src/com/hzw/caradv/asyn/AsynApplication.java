package com.hzw.caradv.asyn;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;

import com.baidu.mapapi.SDKInitializer;
import com.hzw.caradv.constance.CarAdv;
import com.hzw.caradv.util.HActivityManager;

public class AsynApplication extends Application {

    private static TaskRunnable mTaskRunnable;
    public static boolean ishint=false;
    

	@Override
    public void onCreate() {
        super.onCreate();
        // 百度地图初始化
        SDKInitializer.initialize(this);
        TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        CarAdv.IMEI = tm.getDeviceId();
        mTaskRunnable = new TaskRunnable();
        new Thread(mTaskRunnable).start();
        
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            if (bundle == null)
                return;
            int id = bundle.getInt("id");
            String identification = bundle.getString("identification");
            Object result = bundle.get("result");
            IUIController controller = TaskManager.getInstance()
                    .getUIController(identification);
            if (controller == null)
                return;
            controller.refreshUI(id, result);
        }

    };

    private class TaskRunnable implements Runnable {

        private volatile boolean mIdle = false;
        private volatile boolean mExit = false;

        @Override
        public void run() {
            ITask task = null;
            while (!mExit) {
                while ((task = TaskManager.getInstance().getTask()) == null) {
                    synchronized (TaskRunnable.this) {
                        mIdle = true;
                        TaskRunnable.this.notify();
                        try {
                            System.out.println("task runnable is waitting ...");
                            TaskRunnable.this.wait();
                        } catch (Exception e) {
                        }
                        mIdle = false;
                    }
                }

                Object result = task.doTask();
                Bundle bundle = new Bundle();
                bundle.putInt("id", task.mId);
                bundle.putString("identification", task.mIdentification);
                bundle.putSerializable("result", (Serializable) result);
                Message msg = new Message();
                msg.setData(bundle);
                mHandler.sendMessage(msg);

                System.out.println(" task is executed " + task);
            }
        }

        public synchronized void notifyTask() {
            if (mIdle)
                TaskRunnable.this.notify();
        }

        public synchronized void exitAllTask() {
            mExit = true;
            TaskManager.getInstance().destroy();
            if (mIdle)
                TaskRunnable.this.notify();
        }

    }

    public static class TaskManager {
        private static TaskManager mManager = null;
        private Queue<ITask> mTaskQueue;
        private ArrayList<IUIController> mControllerList;

        private TaskManager() {
            mTaskQueue = new ConcurrentLinkedQueue<ITask>();
            mControllerList = new ArrayList<IUIController>();
        }

        public synchronized static TaskManager getInstance() {
            if (mManager == null)
                mManager = new TaskManager();
            return mManager;
        }

        public void addTask(ITask task) {
            if (!mTaskQueue.contains(task)) {
                mTaskQueue.offer(task);
            }
            mTaskRunnable.notifyTask();
        }

        public ITask getTask() {
        	if(mTaskQueue.isEmpty()){
        		return null;
        	}
            return mTaskQueue.poll();
        }

        public synchronized void registerUIController(IUIController con) {
            if (mControllerList.contains(con))
            	mControllerList.remove(con);
        	mControllerList.add(con);
        }

        public synchronized void unRegisterUIController(IUIController con) {
            if (mControllerList.contains(con))
                mControllerList.remove(con);
        }

        public IUIController getUIController(String identification) {
            for (IUIController controller : mControllerList) {
                if (controller.getIdentification().equals(identification)) {
                    return controller;
                }
            }
            return null;
        }

        public void destroy() {
            mTaskQueue.clear();
            mControllerList.clear();
        }
    }

    @Override
    public void onTerminate() {
        HActivityManager.getActivityManager().finishAllActivity();
//        BDLocationUtil.destoryInstance();
//        System.exit(0);
        System.out.println("onTerminate");
        super.onTerminate();
        
    }

}
