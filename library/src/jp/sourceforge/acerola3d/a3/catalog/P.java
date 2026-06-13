//
// このファイルは、JavaTM Architecture for XML Binding(JAXB) Reference Implementation、v2.3.0によって生成されました 
// <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a>を参照してください 
// ソース・スキーマの再コンパイル時にこのファイルの変更は失われます。 
// 生成日: 2019.01.18 時間 01:19:32 PM JST 
//


package jp.sourceforge.acerola3d.a3.catalog;

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
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="wrl" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="scale" type="{http://www.w3.org/2001/XMLSchema}double" default="1.0" /&gt;
 *       &lt;attribute name="offset" type="{http://acerola3d.sourceforge.jp/a3/catalog}real3" default="0.0 0.0 0.0" /&gt;
 *       &lt;attribute name="rot" type="{http://acerola3d.sourceforge.jp/a3/catalog}real3" default="0.0 0.0 0.0" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "p")
public class P {

    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "wrl", required = true)
    protected String wrl;
    @XmlAttribute(name = "scale")
    protected Double scale;
    @XmlAttribute(name = "offset")
    protected String offset;
    @XmlAttribute(name = "rot")
    protected String rot;

    /**
     * nameプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * nameプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * wrlプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWrl() {
        return wrl;
    }

    /**
     * wrlプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWrl(String value) {
        this.wrl = value;
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

}
