//
// このファイルは、JavaTM Architecture for XML Binding(JAXB) Reference Implementation、v2.3.0によって生成されました 
// <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a>を参照してください 
// ソース・スキーマの再コンパイル時にこのファイルの変更は失われます。 
// 生成日: 2019.01.18 時間 01:19:32 PM JST 
//


package jp.sourceforge.acerola3d.a3.catalog;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the jp.sourceforge.acerola3d.a3.catalog package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _C_QNAME = new QName("http://acerola3d.sourceforge.jp/a3/catalog", "c");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: jp.sourceforge.acerola3d.a3.catalog
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link A3 }
     * 
     */
    public A3 createA3() {
        return new A3();
    }

    /**
     * Create an instance of {@link Tag }
     * 
     */
    public Tag createTag() {
        return new Tag();
    }

    /**
     * Create an instance of {@link Profile }
     * 
     */
    public Profile createProfile() {
        return new Profile();
    }

    /**
     * Create an instance of {@link Thumbnail }
     * 
     */
    public Thumbnail createThumbnail() {
        return new Thumbnail();
    }

    /**
     * Create an instance of {@link Htmlfile }
     * 
     */
    public Htmlfile createHtmlfile() {
        return new Htmlfile();
    }

    /**
     * Create an instance of {@link A }
     * 
     */
    public A createA() {
        return new A();
    }

    /**
     * Create an instance of {@link P }
     * 
     */
    public P createP() {
        return new P();
    }

    /**
     * Create an instance of {@link S }
     * 
     */
    public S createS() {
        return new S();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "http://acerola3d.sourceforge.jp/a3/catalog", name = "c")
    public JAXBElement<String> createC(String value) {
        return new JAXBElement<String>(_C_QNAME, String.class, null, value);
    }

}
