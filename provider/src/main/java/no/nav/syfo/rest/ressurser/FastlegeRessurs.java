package no.nav.syfo.rest.ressurser;

import io.swagger.annotations.Api;
import no.nav.metrics.aspects.Count;
import no.nav.metrics.aspects.Timed;
import no.nav.syfo.domain.Fastlege;
import no.nav.syfo.domain.Fastlegekontor;
import no.nav.syfo.domain.Pasient;
import no.nav.syfo.domain.Pasientforhold;
import no.nav.syfo.services.FastlegeService;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import java.time.LocalDate;

import static java.lang.System.*;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.ok;
import static no.nav.metrics.MetricsFactory.createEvent;
import static org.springframework.util.StringUtils.*;

@Path("/fastlege/v1")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Api(value = "fastlege", description = "Endepunkt for henting av fastlege")
@Controller
public class FastlegeRessurs {

    @Inject
    private FastlegeService fastlegeService;

    @GET
    @Timed(name = "finnFastlege")
    @Count(name = "finnFastlege")
    public Fastlege finnFastlege(@QueryParam("fnr") String fnr) {
        if ("true".equals(getProperty("test.local"))) {
            return new Fastlege()
                    .withNavn("Lars Legesen")
                    .withFastlegekontor(new Fastlegekontor()
                            .withAdresse("Bjerrgardsgate 7, 4047 Oslo")
                            .withEpost("test@nav.no")
                            .withTelefon("90762514")
                            .withNavn("St. hanshaugen legesenter")
                            .withOrgnummer(***REMOVED***)
                    )
                    .withPasient(new Pasient()
                            .withFnr("***REMOVED***")
                            .withNavn("Ole Thomas Tørresen")
                    )
                    .withPasientforhold(new Pasientforhold()
                            .withFom(LocalDate.now().minusYears(6))
                            .withTom(LocalDate.now().plusYears(2))
                    );
        }
        Fastlege fastlege = fastlegeService.hentBrukersFastlege(fnr);
        sjekkForTommeData(fastlege);
        return fastlege;
    }

    private void sjekkForTommeData(Fastlege fastlege) {
        if (isEmpty(fastlege.fastlegekontor.navn)) {
            createEvent("manglerNavn").report();
        }

        if (isEmpty(fastlege.fastlegekontor.adresse)) {
            createEvent("manglerAdresse").report();
        }

        if (isEmpty(fastlege.fastlegekontor.telefon)) {
            createEvent("manglerTelefon").report();
        }
    }

    @GET
    @Path("/ping")
    public Response ping() {
        return Response.ok().build();
    }
}
