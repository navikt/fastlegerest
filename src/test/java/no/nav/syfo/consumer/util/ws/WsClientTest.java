package no.nav.syfo.consumer.util.ws;

import no.nav.syfo.consumer.ws.util.LogErrorHandler;
import no.nav.syfo.consumer.ws.util.WsClient;
import no.nhn.schemas.reg.flr.IFlrReadOperations;
import org.junit.Test;

import static java.util.Collections.singletonList;

public class WsClientTest {
    static final String PORT_URL =  "http://url.dev";

    @Test
    public void testsomething() {

        IFlrReadOperations port = new WsClient<IFlrReadOperations>()
                .createPort(PORT_URL, IFlrReadOperations.class, singletonList(new LogErrorHandler()));
    }
}
