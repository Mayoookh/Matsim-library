//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.08.04 at 02:05:46 PM CEST 
//


package playground.gregor.grips.jaxb.inspire.basetypes;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import net.opengis.gml.v_3_2_1.FeaturePropertyType;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the playground.gregor.grips.jaxb.inspire.basetypes package. 
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

    private final static QName _Identifier_QNAME = new QName("urn:x-inspire:specification:gmlas:BaseTypes:3.2", "Identifier");
    private final static QName _SpatialDataSet_QNAME = new QName("urn:x-inspire:specification:gmlas:BaseTypes:3.2", "SpatialDataSet");
    private final static QName _SpatialDataSetTypeMember_QNAME = new QName("urn:x-inspire:specification:gmlas:BaseTypes:3.2", "member");
    private final static QName _SpatialDataSetTypeIdentifier_QNAME = new QName("urn:x-inspire:specification:gmlas:BaseTypes:3.2", "identifier");
    private final static QName _SpatialDataSetTypeMetadata_QNAME = new QName("urn:x-inspire:specification:gmlas:BaseTypes:3.2", "metadata");
    private final static QName _IdentifierTypeVersionId_QNAME = new QName("urn:x-inspire:specification:gmlas:BaseTypes:3.2", "versionId");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: playground.gregor.grips.jaxb.inspire.basetypes
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link IdentifierType.VersionId }
     * 
     */
    public IdentifierType.VersionId createIdentifierTypeVersionId() {
        return new IdentifierType.VersionId();
    }

    /**
     * Create an instance of {@link SpatialDataSetType.Metadata }
     * 
     */
    public SpatialDataSetType.Metadata createSpatialDataSetTypeMetadata() {
        return new SpatialDataSetType.Metadata();
    }

    /**
     * Create an instance of {@link IdentifierType }
     * 
     */
    public IdentifierType createIdentifierType() {
        return new IdentifierType();
    }

    /**
     * Create an instance of {@link SpatialDataSetType }
     * 
     */
    public SpatialDataSetType createSpatialDataSetType() {
        return new SpatialDataSetType();
    }

    /**
     * Create an instance of {@link SpatialDataSetPropertyType }
     * 
     */
    public SpatialDataSetPropertyType createSpatialDataSetPropertyType() {
        return new SpatialDataSetPropertyType();
    }

    /**
     * Create an instance of {@link IdentifierPropertyType }
     * 
     */
    public IdentifierPropertyType createIdentifierPropertyType() {
        return new IdentifierPropertyType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IdentifierType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:x-inspire:specification:gmlas:BaseTypes:3.2", name = "Identifier", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractObject")
    public JAXBElement<IdentifierType> createIdentifier(IdentifierType value) {
        return new JAXBElement<IdentifierType>(_Identifier_QNAME, IdentifierType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SpatialDataSetType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:x-inspire:specification:gmlas:BaseTypes:3.2", name = "SpatialDataSet", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractFeature")
    public JAXBElement<SpatialDataSetType> createSpatialDataSet(SpatialDataSetType value) {
        return new JAXBElement<SpatialDataSetType>(_SpatialDataSet_QNAME, SpatialDataSetType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FeaturePropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:x-inspire:specification:gmlas:BaseTypes:3.2", name = "member", scope = SpatialDataSetType.class)
    public JAXBElement<FeaturePropertyType> createSpatialDataSetTypeMember(FeaturePropertyType value) {
        return new JAXBElement<FeaturePropertyType>(_SpatialDataSetTypeMember_QNAME, FeaturePropertyType.class, SpatialDataSetType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IdentifierPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:x-inspire:specification:gmlas:BaseTypes:3.2", name = "identifier", scope = SpatialDataSetType.class)
    public JAXBElement<IdentifierPropertyType> createSpatialDataSetTypeIdentifier(IdentifierPropertyType value) {
        return new JAXBElement<IdentifierPropertyType>(_SpatialDataSetTypeIdentifier_QNAME, IdentifierPropertyType.class, SpatialDataSetType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SpatialDataSetType.Metadata }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:x-inspire:specification:gmlas:BaseTypes:3.2", name = "metadata", scope = SpatialDataSetType.class)
    public JAXBElement<SpatialDataSetType.Metadata> createSpatialDataSetTypeMetadata(SpatialDataSetType.Metadata value) {
        return new JAXBElement<SpatialDataSetType.Metadata>(_SpatialDataSetTypeMetadata_QNAME, SpatialDataSetType.Metadata.class, SpatialDataSetType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IdentifierType.VersionId }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:x-inspire:specification:gmlas:BaseTypes:3.2", name = "versionId", scope = IdentifierType.class)
    public JAXBElement<IdentifierType.VersionId> createIdentifierTypeVersionId(IdentifierType.VersionId value) {
        return new JAXBElement<IdentifierType.VersionId>(_IdentifierTypeVersionId_QNAME, IdentifierType.VersionId.class, IdentifierType.class, value);
    }

}
