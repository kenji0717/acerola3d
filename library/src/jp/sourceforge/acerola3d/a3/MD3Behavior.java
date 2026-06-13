package jp.sourceforge.acerola3d.a3;

import javax.media.j3d.*;
import java.util.*;
import org.newdawn.j3d.loaders.md3.MD3ModelInstance;

class MD3Behavior extends Behavior {
    MD3ModelInstance instance;
    MD3Behavior(MD3ModelInstance mi) {
        instance = mi;
    }

    public void initialize() {
        WakeupOnElapsedTime w = new WakeupOnElapsedTime(33l);
        wakeupOn(w);
    }

    public void processStimulus(Enumeration criteria) {
        //WakeupOnElapsedTime w = new WakeupOnElapsedTime(33l);
        WakeupOnElapsedTime w = new WakeupOnElapsedTime(100l);
        wakeupOn(w);
        instance.nextFrame("lower");
    }
}
