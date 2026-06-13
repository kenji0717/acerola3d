package jp.sourceforge.acerola3d.a3;

import java.util.*;
import javax.media.j3d.*;

class CameraBehavior extends Behavior {
    A3VirtualUniverse virtualUniverse = null;
    double interpolateRatio = 0.1;
    
    CameraBehavior(A3VirtualUniverse vu) {
        virtualUniverse = vu;
    }
    public void initialize() {
        WakeupOnBehaviorPost w = new WakeupOnBehaviorPost(virtualUniverse.timerBehavior,1);
        wakeupOn(w);
    }
    @SuppressWarnings("unchecked")
    public void processStimulus(Enumeration criteria) {
        //WakeupOnElapsedTime w = new WakeupOnElapsedTime(100);
        //WakeupOnBehaviorPost w = new WakeupOnBehaviorPost(null,1);
        WakeupOnBehaviorPost w = new WakeupOnBehaviorPost(virtualUniverse.timerBehavior,1);
        wakeupOn(w);
        if (interpolateRatio==0.0) {
            virtualUniverse.scene.cameraNowS = virtualUniverse.scene.cameraNextS;
            virtualUniverse.scene.cameraNowQ.set(virtualUniverse.scene.cameraNextQ);
            virtualUniverse.scene.cameraNowQ.normalize();
            virtualUniverse.scene.cameraNowV.set(virtualUniverse.scene.cameraNextV);
        } else {
            double ratio= 1.0-Math.pow(interpolateRatio,((double)virtualUniverse.elapsedTime)/1000.0);
            virtualUniverse.scene.cameraNowS = virtualUniverse.scene.cameraNowS + ratio*(virtualUniverse.scene.cameraNextS - virtualUniverse.scene.cameraNowS);
            virtualUniverse.scene.cameraNowQ.normalize();
            virtualUniverse.scene.cameraNowQ.interpolate(virtualUniverse.scene.cameraNextQ,ratio);
            virtualUniverse.scene.cameraNowQ.normalize();
            virtualUniverse.scene.cameraNowV.interpolate(virtualUniverse.scene.cameraNextV,ratio);
        }
        virtualUniverse.transform.set(virtualUniverse.scene.cameraNowQ,virtualUniverse.scene.cameraNowV,virtualUniverse.scene.cameraNowS);
        try {
            virtualUniverse.tGroup.setTransform(virtualUniverse.transform);
        } catch (BadTransformException e) {
            System.out.println("BadTransformException in CameraBehavior.processStimulus().");
            virtualUniverse.scene.cameraNowS=1.0;
            virtualUniverse.scene.cameraNowQ.set(0,0,0,1);
            virtualUniverse.scene.cameraNowV.set(0,0,0);
            //e.printStackTrace();
        }
        if (Action3DData.soundSystem!=null) {
            Action3DData.soundSystem.setListenerLoc(virtualUniverse.scene.cameraNowV);
            Action3DData.soundSystem.setListenerQuat(virtualUniverse.scene.cameraNowQ);
        }
    }
    void setInterpolateRatio(double ir) {
        interpolateRatio = ir;
    }
}
