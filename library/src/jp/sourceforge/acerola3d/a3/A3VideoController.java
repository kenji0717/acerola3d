package jp.sourceforge.acerola3d.a3;
//このコードはSUNのサイトにあるJMFのサンプルのDemoJMFJ3D.javaを
//ベースに少し手を加えた物です．
import javax.media.*;
import javax.media.format.*;
import javax.media.control.*;

class A3VideoController implements ControllerListener {
    Processor p;
    int[] waitSync = new int[0];
    boolean stateTransOK = true;
    A3VideoRenderer a3VideoRenderer;

    public A3VideoController() {
    }

    public boolean open(MediaLocator ml) {
        try {
            p = Manager.createProcessor(ml);
        } catch (Exception ex) {
            System.out.println("failed to create a processor for movie " + ml);
            return false;
        }

        p.addControllerListener(this);
        
        p.configure();
        
        if ( !waitForState(Processor.Configured)) {
            System.out.println("Failed to configure the processor");
            return false;
        }
        
        // use processor as a player
        p.setContentDescriptor(null);
        
        // obtain the track control
        TrackControl[] tc = p.getTrackControls();

        if ( tc == null ) {
            System.out.println("Failed to get the track control from processor");
            return false;
        }

        TrackControl vtc = null;
        
        for ( int i =0; i < tc.length; i++ ) {
            if (tc[i].getFormat() instanceof VideoFormat ) {
                vtc = tc[i];
                break;
            }
            
        }

        if ( vtc == null ) {
            System.out.println("can't find video track");
            return false;
        }
        
        try {
            a3VideoRenderer = new A3VideoRenderer();
            //a3VideoRenderer.getComponent().setSize(296, 240);
            vtc.setRenderer(a3VideoRenderer);
        } catch ( Exception ex) {
            ex.printStackTrace();
            System.out.println("the processor does not support effect");
            return false;
        }

        // prefetch
        p.prefetch();
        if ( !waitForState(Processor.Prefetched)) {
            System.out.println("Failed to prefech the processor");
            return false;
        }
        System.out.println("end of prefetch");

        return true;
    }

    public void start() {
        p.start();
        System.out.println("p start");
    }

    public void stop() {
        p.stop();
        System.out.println("p stop");
    }

    // -------------------
    public boolean waitForState(int state) {
        synchronized (waitSync) {
            try {
                while ( p.getState() != state && stateTransOK ) {
                    waitSync.wait();
                }
            } catch (Exception ex) {}
            
            return stateTransOK;
        }
    }


    // -----------------------
    public void controllerUpdate(ControllerEvent evt) {
        if ( evt instanceof ConfigureCompleteEvent ||
             evt instanceof RealizeCompleteEvent ||
             evt instanceof PrefetchCompleteEvent ) {
            synchronized (waitSync) {
                stateTransOK = true;
                waitSync.notifyAll();
            }
        } else if ( evt instanceof ResourceUnavailableEvent) {
            synchronized (waitSync) {
                stateTransOK = false;
                waitSync.notifyAll();
            }
        } else if ( evt instanceof EndOfMediaEvent) {
            p.setMediaTime(new Time(0));
            p.start();
            // p.close();
            // System.exit(0);
        }
    }
}
