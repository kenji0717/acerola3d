//
// このファイルは、JavaTM Architecture for XML Binding(JAXB) Reference Implementation、v2.3.0によって生成されました 
// <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a>を参照してください 
// ソース・スキーマの再コンパイル時にこのファイルの変更は失われます。 
// 生成日: 2019.01.18 時間 01:19:32 PM JST 
//


package jp.sourceforge.acerola3d.a3.catalog;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>soundTypeのJavaクラス。
 * 
 * <p>次のスキーマ・フラグメントは、このクラス内に含まれる予期されるコンテンツを指定します。
 * <p>
 * <pre>
 * &lt;simpleType name="soundType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="PointSound"/&gt;
 *     &lt;enumeration value="BackgroundSound"/&gt;
 *     &lt;enumeration value="ConeSound"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "soundType")
@XmlEnum
public enum SoundType {

    @XmlEnumValue("PointSound")
    POINT_SOUND("PointSound"),
    @XmlEnumValue("BackgroundSound")
    BACKGROUND_SOUND("BackgroundSound"),
    @XmlEnumValue("ConeSound")
    CONE_SOUND("ConeSound");
    private final String value;

    SoundType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SoundType fromValue(String v) {
        for (SoundType c: SoundType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
