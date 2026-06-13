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
 *       &lt;attribute name="file" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="type" type="{http://acerola3d.sourceforge.jp/a3/catalog}soundType" default="PointSound" /&gt;
 *       &lt;attribute name="loop" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="gain" type="{http://www.w3.org/2001/XMLSchema}double" default="1.0" /&gt;
 *       &lt;attribute name="offset" type="{http://acerola3d.sourceforge.jp/a3/catalog}real3" default="0.0 0.0 0.0" /&gt;
 *       &lt;attribute name="direction" type="{http://acerola3d.sourceforge.jp/a3/catalog}real3" default="0.0 0.0 1.0" /&gt;
 *       &lt;attribute name="continue" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "s")
public class S {

    @XmlAttribute(name = "file", required = true)
    protected String file;
    @XmlAttribute(name = "type")
    protected SoundType type;
    @XmlAttribute(name = "loop")
    protected Boolean loop;
    @XmlAttribute(name = "gain")
    protected Double gain;
    @XmlAttribute(name = "offset")
    protected String offset;
    @XmlAttribute(name = "direction")
    protected String direction;
    @XmlAttribute(name = "continue")
    protected Boolean _continue;

    /**
     * fileプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFile() {
        return file;
    }

    /**
     * fileプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFile(String value) {
        this.file = value;
    }

    /**
     * typeプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link SoundType }
     *     
     */
    public SoundType getType() {
        if (type == null) {
            return SoundType.POINT_SOUND;
        } else {
            return type;
        }
    }

    /**
     * typeプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link SoundType }
     *     
     */
    public void setType(SoundType value) {
        this.type = value;
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
     * gainプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public double getGain() {
        if (gain == null) {
            return  1.0D;
        } else {
            return gain;
        }
    }

    /**
     * gainプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setGain(Double value) {
        this.gain = value;
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
     * directionプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDirection() {
        if (direction == null) {
            return "0.0 0.0 1.0";
        } else {
            return direction;
        }
    }

    /**
     * directionプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDirection(String value) {
        this.direction = value;
    }

    /**
     * continueプロパティの値を取得します。
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isContinue() {
        if (_continue == null) {
            return true;
        } else {
            return _continue;
        }
    }

    /**
     * continueプロパティの値を設定します。
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setContinue(Boolean value) {
        this._continue = value;
    }

}
