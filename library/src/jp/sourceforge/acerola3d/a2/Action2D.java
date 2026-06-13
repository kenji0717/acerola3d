package jp.sourceforge.acerola3d.a2;

import java.awt.*;
import java.net.*;
import java.util.*;
import java.io.*;


import org.w3c.dom.*;
import javax.xml.parsers.*;

import jp.sourceforge.acerola3d.*;

public final class Action2D extends A2Object {
    static Toolkit toolkit = null;
    static Image errImg = null;
    int actionCount = 0;
    Hashtable<String,Image> nameHash;
    String nameArray[];
    double x;
    double y;
    int actionNo;

    Action2D(String a2File,boolean interpolating) throws Exception {
        super(interpolating);
        Hashtable<String,Serializable[]> actionHash = new Hashtable<String,Serializable[]>();
        prepareActionHashtable(a2File,actionHash);
        init(a2File,actionHash);
    }
    Action2D(URL url,boolean interpolating) throws Exception {
        super(interpolating);
        Hashtable<String,Serializable[]> actionHash = new Hashtable<String,Serializable[]>();
        prepareActionHashtable(url.toExternalForm(),actionHash);
        init(url,actionHash);
    }
    static boolean isInitialized() {
        if ((toolkit!=null) && (errImg!=null))
            return true;
        else
            return false;
    }
    static void initAction2D() {
        toolkit = Toolkit.getDefaultToolkit();
        URL url = A23.getClassLoader().getResource("jp/sourceforge/acerola3d/resources/error.gif");
        errImg = toolkit.getImage(url);
    }

    void prepareActionHashtable(String a2File,Hashtable<String,Serializable[]> actionHash) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document d = db.parse("x-rzip:"+a2File+"!/CATALOG.XML");

        NodeList nl = d.getChildNodes();
        Node n = null;
        for (int i=0;i<nl.getLength();i++) {
            if (nl.item(i).getNodeName().equals("a2")) {
                n = nl.item(i);
                break;
            }
        }

        int actionCount = 0;
        nl = n.getChildNodes();
        for (int i=0;i<nl.getLength();i++) {
            if (nl.item(i).getNodeName().equals("a")) {
                Node nn = nl.item(i);
                NamedNodeMap nnm = nn.getAttributes();
                Attr aaa = (Attr)nnm.getNamedItem("an");
                String av = aaa.getValue();
                av = av.trim();
                NodeList nl2 = nn.getChildNodes();
                String ttt = null;
                for (int j=0;j<nl2.getLength();j++) {
                    if (nl2.item(j) instanceof Text) {
                        ttt = ((Text)nl2.item(j)).getData();
                        ttt = ttt.trim();
                        break;
                    }
                }
                Serializable seri[] = new Serializable[2];
                seri[0] = ttt;
                seri[1] = new Integer(actionCount);
                actionHash.put(av,seri);
                actionCount++;
            }
        }
    }

    void init(String a2File,Hashtable<String,Serializable[]> actionHash) throws Exception {
        nameHash = new Hashtable<String,Image>();

        Set<String> set = actionHash.keySet();
        nameArray = new String[set.size()];
        Iterator<String> ii = set.iterator();
        while (ii.hasNext()) {
            String k = ii.next();
            Serializable seri[] = (Serializable[])actionHash.get(k);
            String fn = (String)seri[0];
            int j = ((Integer)seri[1]).intValue();
            nameArray[j]=k;
            Image i = toolkit.createImage(new URL("x-rzip:"+a2File+"!/"+fn));
            nameHash.put(k,i);
        }
        A23.clearZipCache();
    }

    void init(URL url,Hashtable<String,Serializable[]> actionHash) throws Exception {
        String a2File = url.toExternalForm();
        nameHash = new Hashtable<String,Image>();

        Set<String> set = actionHash.keySet();
        nameArray = new String[set.size()];
        Iterator<String> ii = set.iterator();
        while (ii.hasNext()) {
            String k = ii.next();
            Serializable seri[] = (Serializable[])actionHash.get(k);
            String fn = (String)seri[0];
            int j = ((Integer)seri[1]).intValue();
            nameArray[j]=k;
            Image i = toolkit.createImage(new URL("x-rzip:"+a2File+"!/"+fn));
            nameHash.put(k,i);
        }
        A23.clearZipCache();
    }

    public Image getImage(String actionName) {
        if (actionName == null)
            return errImg;
        Image i = nameHash.get(actionName);
        if (i == null) {
System.out.println("Action2D.getImage(). Illegal ActionName.");
            return errImg;
        } else {
            return i;
        }
    }

    public Image getImage(int actionNumber) {
        String actionName = getActionName(actionNumber);
        return getImage(actionName);
    }

    public Image getImage(Serializable s) {
        if (s instanceof String) {
            return getImage((String)s);
        } else if (s instanceof Integer) {
            return getImage(((Integer)s).intValue());
        } else {
System.out.println("Action2D.getImage(). Illegal ActionID.");
            return errImg;
        }
    }

    public int getActionCount() {
        return nameArray.length;
    }

    public String[] getActionNames() {
        String ret[] = new String[nameArray.length];
        for (int i=0;i<ret.length;i++)
            ret[i] = nameArray[i];
        return ret;
    }

    public String getActionName(int i) {
        try {
            return nameArray[i];
        } catch (Exception e) {
System.out.println("Action2D.getActionName(). Illegal ActionNumber.");
            return null;
        }
    }
    public String getComment() {
        // TODO
        return null;
    }

    public void paint(Graphics g) {
        String an = getActionName(actionNo);
        Image i = nameHash.get(an);
        g.drawImage(i,0,0,canvas);
        if (!interpolating) {
            needRepaint = false;
        }
    }
    
    public void change(int actionNo) {
        change(getActionName(actionNo));
    }
    public void change(Serializable actionID) {
        if (actionID instanceof Integer) {
            actionNo = ((Integer)actionID).intValue();
        } else if (actionID instanceof String) {
            for (int i=0;i<nameArray.length;i++) {
                if (nameArray[i].equals(actionID)) {
                    actionNo = i;
                    break;
                }
            }
        }
        repaint();
    }
}
