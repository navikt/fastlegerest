package no.nav.syfo.mocks;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;

public class TimeUtils {

    public static XMLGregorianCalendar toXMLGregorianCalendar(LocalDateTime localDateTime)
    {
        XMLGregorianCalendar xmlGregorianCalendar =
                null;
        try {
            xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(localDateTime.toString());
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }

        return xmlGregorianCalendar;
    }
}
