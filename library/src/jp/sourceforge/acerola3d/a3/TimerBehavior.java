package jp.sourceforge.acerola3d.a3;

import java.awt.Color;
import java.util.*;
import javax.media.j3d.*;

class TimerBehavior extends Behavior {
    A3VirtualUniverse universe;
    Material emphasizerMaterial;
    ArrayList<Runnable> runnableQueue = null;
    Object waitingRoom = new Object();
    ArrayList<Runnable> tasks = new ArrayList<Runnable>();

    TimerBehavior(A3VirtualUniverse u) {
        universe = u;
        runnableQueue = new ArrayList<Runnable>();
    }
    void setEmphasizerMaterial(Material m) {
        emphasizerMaterial = m;
        emphasizerMaterial.setDiffuseColor(0.333f, 0.333f, 0.333f);
        emphasizerMaterial.setSpecularColor(0.0f, 0.0f, 0.0f);
    }
    public void initialize() {
        WakeupOnElapsedTime w = new WakeupOnElapsedTime(universe.elapsedTime);
        wakeupOn(w);
    }
    @SuppressWarnings("unchecked")
    public void processStimulus(Enumeration criteria) {
        postId(1);
        WakeupOnElapsedTime w = new WakeupOnElapsedTime(universe.elapsedTime);
        wakeupOn(w);

        synchronized(waitingRoom) {
            waitingRoom.notifyAll();
        }

        float t = (float)((System.currentTimeMillis() % 2000)/2000.0);
        Color c = Color.getHSBColor(t,1.0f,1.0f);
        float r = c.getRed()/255.0f;
        float g = c.getGreen()/255.0f;
        float b = c.getBlue()/255.0f;
        emphasizerMaterial.setDiffuseColor(r,g,b);
        //runnableQueueの処理
        ArrayList<Runnable> runnableCopy = null;
        synchronized(runnableQueue) {
            runnableCopy = (ArrayList<Runnable>)runnableQueue.clone();
            runnableQueue.clear();
        }
        for (Runnable rr:runnableCopy) {
            rr.run();
        }
        ArrayList<Runnable> tasksCopy = null;
        synchronized(runnableQueue) {
            tasksCopy = (ArrayList<Runnable>)tasks.clone();
        }
        for (Runnable rr:tasksCopy) {
            rr.run();
        }
    }
    void addRunnable(Runnable r) {
        synchronized(runnableQueue) {
            runnableQueue.add(r);
        }
    }
    void insertTaskIntoTimerLoop(Runnable task) {
        synchronized(tasks) {
            tasks.add(task);
        }
    }
    void removeTaskFromTimerLoop(Runnable task) {
        synchronized(tasks) {
            tasks.remove(task);
        }
    }
    void waitForUpdate(long timeout) {
        synchronized (waitingRoom) {
            try {
                waitingRoom.wait(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
