//
// このファイルは、JavaTM Architecture for XML Binding(JAXB) Reference Implementation、v2.3.0によって生成されました 
// <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a>を参照してください 
// ソース・スキーマの再コンパイル時にこのファイルの変更は失われます。 
// 生成日: 2019.01.18 時間 01:19:32 PM JST 
//


package jp.sourceforge.acerola3d.a3.catalog;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>anonymous complex typeのJavaクラス。
 * 
 * <p>次のスキーマ・フラグメントは、このクラス内に含まれる予期されるコンテンツを指定します。
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://acerola3d.sourceforge.jp/a3/catalog}p" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{http://acerola3d.sourceforge.jp/a3/catalog}s" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="an" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="bvh" type="{http://www.w3.org/2001/XMLSchema}string" default="none" /&gt;
 *       &lt;attribute name="loop" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="scale" type="{http://www.w3.org/2001/XMLSchema}double" default="1.0" /&gt;
 *       &lt;attribute name="offset" type="{http://acerola3d.sourceforge.jp/a3/catalog}real3" default="0.0 0.0 0.0" /&gt;
 *       &lt;attribute name="rot" type="{http://acerola3d.sourceforge.jp/a3/catalog}real3" default="0.0 0.0 0.0" /&gt;
 *       &lt;attribute name="rightBalloonOffset" type="{http://acerola3d.sourceforge.jp/a3/catalog}real2" default="0.0 0.0" /&gt;
 *       &lt;attribute name="leftBalloonOffset" type="{http://acerola3d.sourceforge.jp/a3/catalog}real2" default="0.0 0.0" /&gt;
 *       &lt;attribute name="topBalloonOffset" type="{http://acerola3d.sourceforge.jp/a3/catalog}real2" default="0.0 0.0" /&gt;
 *       &lt;attribute name="bottomBalloonOffset" type="{http://acerola3d.sourceforge.jp/a3/catalog}real2" default="0.0 0.0" /&gt;
 *       &lt;attribute name="labelOffset" type="{http://acerola3d.sourceforge.jp/a3/catalog}real2" default="0.0 0.0" /&gt;
 *       &lt;attribute name="segno" type="{http://www.w3.org/2001/XMLSchema}double" default="0.0" /&gt;
 *       &lt;attribute name="dalsegno" type="{http://www.w3.org/2001/XMLSchema}double" default="-1.0" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "p",
    "s"
})
@XmlRootElement(name = "a")
public class A {

    protected List<P> p;
    protected S s;
    @XmlAttribute(name = "an", required = true)
    protected String an;
    @XmlAttribute(name = "bvh")
    protected String bvh;
    @XmlAttribute(name = "loop")
    protected Boolean loop;
    @XmlAttribute(name = "scale")
    protected Double scale;
    @XmlAttribute(name = "offset")
    protected String offset;
    @XmlAttribute(name = "rot")
    protected String rot;
    @XmlAttribute(name = "rightBalloonOffset")
    protected String rightBalloonOffset;
    @XmlAttribute(name = "leftBalloonOffset")
    protected String leftBalloonOffset;
    @XmlAttribute(name = "topBalloonOffset")
    protected String topBalloonOffset;
    @XmlAttribute(name = "bottomBalloonOffset")
    protected String bottomBalloonOffset;
    @XmlAttribute(name = "labelOffset")
    protected String labelOffset;
    @XmlAttribute(name = "segno")
    protected Double segno;
    @XmlAttribute(name = "dalsegno")
    protected Double dalsegno;

    /**
     * Gets the value of the p property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the p property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getP().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link P }
     * 
     * 
     */
    public List<P> getP() {
        if (p == null) {
            p = new ArrayList<P>();
        }
        return this.p;
    }

    /**
     * sプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link S }
     *     
     */
    public S getS() {
        return s;
    }

    /**
     * sプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link S }
     *     
     */
    public void setS(S value) {
        this.s = value;
    }

    /**
     * anプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAn() {
        return an;
    }

    /**
     * anプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAn(String value) {
        this.an = value;
    }

    /**
     * bvhプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBvh() {
        if (bvh == null) {
            return "none";
        } else {
            return bvh;
        }
    }

    /**
     * bvhプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBvh(String value) {
        this.bvh = value;
    }

    /**
     * loopプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isLoop() {
        if (loop == null) {
            return false;
        } else {
            return loop;
        }
    }

    /**
     * loopプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLoop(Boolean value) {
        this.loop = value;
    }

    /**
     * scaleプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public double getScale() {
        if (scale == null) {
            return  1.0D;
        } else {
            return scale;
        }
    }

    /**
     * scaleプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setScale(Double value) {
        this.scale = value;
    }

    /**
     * offsetプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOffset() {
        if (offset == null) {
            return "0.0 0.0 0.0";
        } else {
            return offset;
        }
    }

    /**
     * offsetプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOffset(String value) {
        this.offset = value;
    }

    /**
     * rotプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRot() {
        if (rot == null) {
            return "0.0 0.0 0.0";
        } else {
            return rot;
        }
    }

    /**
     * rotプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRot(String value) {
        this.rot = value;
    }

    /**
     * rightBalloonOffsetプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRightBalloonOffset() {
        if (rightBalloonOffset == null) {
            return "0.0 0.0";
        } else {
            return rightBalloonOffset;
        }
    }

    /**
     * rightBalloonOffsetプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRightBalloonOffset(String value) {
        this.rightBalloonOffset = value;
    }

    /**
     * leftBalloonOffsetプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLeftBalloonOffset() {
        if (leftBalloonOffset == null) {
            return "0.0 0.0";
        } else {
            return leftBalloonOffset;
        }
    }

    /**
     * leftBalloonOffsetプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLeftBalloonOffset(String value) {
        this.leftBalloonOffset = value;
    }

    /**
     * topBalloonOffsetプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTopBalloonOffset() {
        if (topBalloonOffset == null) {
            return "0.0 0.0";
        } else {
            return topBalloonOffset;
        }
    }

    /**
     * topBalloonOffsetプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTopBalloonOffset(String value) {
        this.topBalloonOffset = value;
    }

    /**
     * bottomBalloonOffsetプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBottomBalloonOffset() {
        if (bottomBalloonOffset == null) {
            return "0.0 0.0";
        } else {
            return bottomBalloonOffset;
        }
    }

    /**
     * bottomBalloonOffsetプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBottomBalloonOffset(String value) {
        this.bottomBalloonOffset = value;
    }

    /**
     * labelOffsetプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabelOffset() {
        if (labelOffset == null) {
            return "0.0 0.0";
        } else {
            return labelOffset;
        }
    }

    /**
     * labelOffsetプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabelOffset(String value) {
        this.labelOffset = value;
    }

    /**
     * segnoプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public double getSegno() {
        if (segno == null) {
            return  0.0D;
        } else {
            return segno;
        }
    }

    /**
     * segnoプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setSegno(Double value) {
        this.segno = value;
    }

    /**
     * dalsegnoプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public double getDalsegno() {
        if (dalsegno == null) {
            return -1.0D;
        } else {
            return dalsegno;
        }
    }

    /**
     * dalsegnoプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setDalsegno(Double value) {
        this.dalsegno = value;
    }

}
