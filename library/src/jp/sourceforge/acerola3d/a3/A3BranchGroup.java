package jp.sourceforge.acerola3d.a3;

import javax.media.j3d.*;

class A3BranchGroup extends BranchGroup {
    public A3BranchGroup() {
        super();
        setCapability(ENABLE_PICK_REPORTING);
        setCapability(BranchGroup.ALLOW_DETACH);
    }

    public void setA3(A3Object a3) {
        setUserData(a3);
    }

    public A3Object getA3() {
        return (A3Object)getUserData();
    }
}
