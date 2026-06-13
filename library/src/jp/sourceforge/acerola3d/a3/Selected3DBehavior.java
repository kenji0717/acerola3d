package jp.sourceforge.acerola3d.a3;

import java.util.*;
import javax.media.j3d.*;

class Selected3DBehavior extends Behavior {
    TransformGroup tg;

    public Selected3DBehavior(TransformGroup tg) {
        this.tg = tg;
    }

    public void initialize() {
        WakeupOnElapsedFrames w = new WakeupOnElapsedFrames(10);
        wakeupOn(w);
    }

    double dtime;
    @SuppressWarnings("unchecked")
    public void processStimulus(Enumeration criteria) {
        dtime=dtime+0.01;
        Transform3D tX = new Transform3D();
        tX.rotX(dtime);
        Transform3D tY = new Transform3D();
        tY.rotY(dtime);
        Transform3D tZ = new Transform3D();
        tZ.rotZ(dtime);
        tX.mul(tY);
        tX.mul(tZ);
        tg.setTransform(tX);
        WakeupOnElapsedFrames w = new WakeupOnElapsedFrames(10);
        wakeupOn(w);
    }

    public Node cloneNode(boolean forceDuplication) {
       Selected3DBehavior newBehavior = new Selected3DBehavior(tg);
       newBehavior.duplicateNode(this,forceDuplication);
       return newBehavior;
    }
  
    public void duplicateNode(Node node, boolean forceDuplication) {
      super.duplicateNode(node,forceDuplication);
      //dtime = ((Selected3DBehavior)node).dtime+1.0;
    }

    public void updateNodeReferences(NodeReferenceTable table) {
      super.updateNodeReferences(table);
      TransformGroup newTG = (TransformGroup)table.getNewObjectReference(tg);
      tg = newTG;
    }
}
