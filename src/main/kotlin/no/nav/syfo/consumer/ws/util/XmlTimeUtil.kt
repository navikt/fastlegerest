package no.nav.syfo.consumer.ws.util

import java.time.LocalDateTime
import javax.xml.datatype.*

fun LocalDateTime.toXMLGregorianCalendar(): XMLGregorianCalendar? {
    var xmlGregorianCalendar: XMLGregorianCalendar? = null
    try {
        xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(this.toString())
    } catch (e: DatatypeConfigurationException) {
        e.printStackTrace()
    }
    return xmlGregorianCalendar
}
