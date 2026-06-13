package jp.sourceforge.acerola3d.a3;

import javax.media.j3d.*;
import java.util.*;
import org.newdawn.j3d.loaders.md2.MD2ModelInstance;

class MD2Behavior extends Behavior {
    MD2ModelInstance instance;
    MD2Behavior(MD2ModelInstance mi) {
        instance = mi;
    }

    public void initialize() {
        WakeupOnElapsedTime w = new WakeupOnElapsedTime(33l);
        wakeupOn(w);
    }

    public void processStimulus(Enumeration criteria) {
        WakeupOnElapsedTime w = new WakeupOnElapsedTime(33l);
        wakeupOn(w);
        instance.nextFrame();
    }
}
