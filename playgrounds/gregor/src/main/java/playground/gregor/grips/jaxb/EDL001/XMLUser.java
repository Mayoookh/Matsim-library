//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.08.01 at 04:42:06 PM CEST 
//


package playground.gregor.grips.jaxb.EDL001;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://matsim.svn.sourceforge.net/viewvc/matsim/playgrounds/trunk/gregor/xsd}home" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="account_created" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="display_name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "home"
})
@XmlRootElement(name = "user")
public class XMLUser {

    protected XMLHome home;
    @XmlAttribute(name = "account_created", required = true)
    protected String accountCreated;
    @XmlAttribute(name = "display_name", required = true)
    protected String displayName;

    /**
     * Gets the value of the home property.
     * 
     * @return
     *     possible object is
     *     {@link XMLHome }
     *     
     */
    public XMLHome getHome() {
        return home;
    }

    /**
     * Sets the value of the home property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLHome }
     *     
     */
    public void setHome(XMLHome value) {
        this.home = value;
    }

    /**
     * Gets the value of the accountCreated property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountCreated() {
        return accountCreated;
    }

    /**
     * Sets the value of the accountCreated property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountCreated(String value) {
        this.accountCreated = value;
    }

    /**
     * Gets the value of the displayName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the value of the displayName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisplayName(String value) {
        this.displayName = value;
    }

}
