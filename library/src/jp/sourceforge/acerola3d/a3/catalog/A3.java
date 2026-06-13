//
// このファイルは、JavaTM Architecture for XML Binding(JAXB) Reference Implementation、v2.3.0によって生成されました 
// <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a>を参照してください 
// ソース・スキーマの再コンパイル時にこのファイルの変更は失われます。 
// 生成日: 2019.01.18 時間 01:19:32 PM JST 
//


package jp.sourceforge.acerola3d.a3.catalog;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.w3c.dom.Element;


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
 *         &lt;element ref="{http://acerola3d.sourceforge.jp/a3/catalog}c" minOccurs="0"/&gt;
 *         &lt;element ref="{http://acerola3d.sourceforge.jp/a3/catalog}tag" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{http://acerola3d.sourceforge.jp/a3/catalog}profile" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{http://acerola3d.sourceforge.jp/a3/catalog}thumbnail" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;any processContents='skip' namespace='http://www.w3.org/1999/02/22-rdf-syntax-ns#' minOccurs="0"/&gt;
 *         &lt;element ref="{http://acerola3d.sourceforge.jp/a3/catalog}htmlfile" minOccurs="0"/&gt;
 *         &lt;element ref="{http://acerola3d.sourceforge.jp/a3/catalog}a" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="haltActionNo" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" /&gt;
 *       &lt;attribute name="walkActionNo" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" /&gt;
 *       &lt;attribute name="runActionNo" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" /&gt;
 *       &lt;attribute name="minWalkSpeed" type="{http://www.w3.org/2001/XMLSchema}double" default="0.1" /&gt;
 *       &lt;attribute name="minRunSpeed" type="{http://www.w3.org/2001/XMLSchema}double" default="1.0" /&gt;
 *       &lt;attribute name="billboard" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "c",
    "tag",
    "profile",
    "thumbnail",
    "any",
    "htmlfile",
    "a"
})
@XmlRootElement(name = "a3")
public class A3 {

    protected String c;
    protected List<Tag> tag;
    protected List<Profile> profile;
    protected List<Thumbnail> thumbnail;
    @XmlAnyElement
    protected Element any;
    protected Htmlfile htmlfile;
    @XmlElement(required = true)
    protected List<A> a;
    @XmlAttribute(name = "haltActionNo")
    protected BigInteger haltActionNo;
    @XmlAttribute(name = "walkActionNo")
    protected BigInteger walkActionNo;
    @XmlAttribute(name = "runActionNo")
    protected BigInteger runActionNo;
    @XmlAttribute(name = "minWalkSpeed")
    protected Double minWalkSpeed;
    @XmlAttribute(name = "minRunSpeed")
    protected Double minRunSpeed;
    @XmlAttribute(name = "billboard")
    protected Boolean billboard;

    /**
     * cプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getC() {
        return c;
    }

    /**
     * cプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setC(String value) {
        this.c = value;
    }

    /**
     * Gets the value of the tag property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tag property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTag().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Tag }
     * 
     * 
     */
    public List<Tag> getTag() {
        if (tag == null) {
            tag = new ArrayList<Tag>();
        }
        return this.tag;
    }

    /**
     * Gets the value of the profile property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the profile property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProfile().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Profile }
     * 
     * 
     */
    public List<Profile> getProfile() {
        if (profile == null) {
            profile = new ArrayList<Profile>();
        }
        return this.profile;
    }

    /**
     * Gets the value of the thumbnail property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the thumbnail property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getThumbnail().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Thumbnail }
     * 
     * 
     */
    public List<Thumbnail> getThumbnail() {
        if (thumbnail == null) {
            thumbnail = new ArrayList<Thumbnail>();
        }
        return this.thumbnail;
    }

    /**
     * anyプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link Element }
     *     
     */
    public Element getAny() {
        return any;
    }

    /**
     * anyプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link Element }
     *     
     */
    public void setAny(Element value) {
        this.any = value;
    }

    /**
     * htmlfileプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link Htmlfile }
     *     
     */
    public Htmlfile getHtmlfile() {
        return htmlfile;
    }

    /**
     * htmlfileプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link Htmlfile }
     *     
     */
    public void setHtmlfile(Htmlfile value) {
        this.htmlfile = value;
    }

    /**
     * Gets the value of the a property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the a property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getA().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link A }
     * 
     * 
     */
    public List<A> getA() {
        if (a == null) {
            a = new ArrayList<A>();
        }
        return this.a;
    }

    /**
     * haltActionNoプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getHaltActionNo() {
        if (haltActionNo == null) {
            return new BigInteger("0");
        } else {
            return haltActionNo;
        }
    }

    /**
     * haltActionNoプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setHaltActionNo(BigInteger value) {
        this.haltActionNo = value;
    }

    /**
     * walkActionNoプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getWalkActionNo() {
        if (walkActionNo == null) {
            return new BigInteger("0");
        } else {
            return walkActionNo;
        }
    }

    /**
     * walkActionNoプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setWalkActionNo(BigInteger value) {
        this.walkActionNo = value;
    }

    /**
     * runActionNoプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRunActionNo() {
        if (runActionNo == null) {
            return new BigInteger("0");
        } else {
            return runActionNo;
        }
    }

    /**
     * runActionNoプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRunActionNo(BigInteger value) {
        this.runActionNo = value;
    }

    /**
     * minWalkSpeedプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public double getMinWalkSpeed() {
        if (minWalkSpeed == null) {
            return  0.1D;
        } else {
            return minWalkSpeed;
        }
    }

    /**
     * minWalkSpeedプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMinWalkSpeed(Double value) {
        this.minWalkSpeed = value;
    }

    /**
     * minRunSpeedプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public double getMinRunSpeed() {
        if (minRunSpeed == null) {
            return  1.0D;
        } else {
            return minRunSpeed;
        }
    }

    /**
     * minRunSpeedプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMinRunSpeed(Double value) {
        this.minRunSpeed = value;
    }

    /**
     * billboardプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isBillboard() {
        if (billboard == null) {
            return false;
        } else {
            return billboard;
        }
    }

    /**
     * billboardプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBillboard(Boolean value) {
        this.billboard = value;
    }

}
