//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.08.01 at 04:42:06 PM CEST 
//


package playground.gregor.grips.jaxb.EDL001;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import playground.gregor.grips.jaxb.gml.LinearRingType;


/**
 * <p>Java class for evacuationAreaType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="evacuationAreaType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SRS" type="{http://matsim.svn.sourceforge.net/viewvc/matsim/playgrounds/trunk/gregor/xsd}SRSType"/>
 *         &lt;element name="area" type="{http://www.opengis.net/gml}LinearRingType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "evacuationAreaType", propOrder = {
    "srs",
    "area"
})
public class XMLEvacuationAreaType {

    @XmlElement(name = "SRS", required = true)
    protected XMLSRSType srs;
    @XmlElement(required = true)
    protected LinearRingType area;

    /**
     * Gets the value of the srs property.
     * 
     * @return
     *     possible object is
     *     {@link XMLSRSType }
     *     
     */
    public XMLSRSType getSRS() {
        return srs;
    }

    /**
     * Sets the value of the srs property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLSRSType }
     *     
     */
    public void setSRS(XMLSRSType value) {
        this.srs = value;
    }

    /**
     * Gets the value of the area property.
     * 
     * @return
     *     possible object is
     *     {@link LinearRingType }
     *     
     */
    public LinearRingType getArea() {
        return area;
    }

    /**
     * Sets the value of the area property.
     * 
     * @param value
     *     allowed object is
     *     {@link LinearRingType }
     *     
     */
    public void setArea(LinearRingType value) {
        this.area = value;
    }

}
