package fr.eletutour.tavern.archives.resource;

import fr.eletutour.tavern.archives.dto.CreationDossier;
import fr.eletutour.tavern.archives.dto.DossierDto;
import fr.eletutour.tavern.archives.dto.NouvelleProuesse;
import fr.eletutour.tavern.archives.dto.PageDossiers;
import fr.eletutour.tavern.archives.dto.StatistiquesClasse;
import fr.eletutour.tavern.archives.model.ClasseAventurier;
import fr.eletutour.tavern.archives.model.Rang;
import fr.eletutour.tavern.archives.service.ArchivesService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("/archives/aventuriers")
@Produces(MediaType.APPLICATION_JSON)
public class ArchivesResource {

    @Inject
    ArchivesService service;

    @GET
    public PageDossiers lister(
            @QueryParam("classe") ClasseAventurier classe,
            @QueryParam("rang") Rang rang,
            @QueryParam("page") @DefaultValue("0") @Min(0) int page,
            @QueryParam("taille") @DefaultValue("10") @Min(1) @Max(100) int taille) {
        return service.lister(classe, rang, page, taille);
    }

    @GET
    @Path("/statistiques")
    public List<StatistiquesClasse> statistiques() {
        return service.statistiques();
    }

    @GET
    @Path("/{id}")
    public DossierDto dossier(@PathParam("id") String id) {
        return service.trouver(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response creer(@Valid CreationDossier creation) {
        DossierDto dossier = service.creer(creation);
        return Response.created(URI.create("/archives/aventuriers/" + dossier.id()))
                .entity(dossier)
                .build();
    }

    @POST
    @Path("/{id}/prouesses")
    @Consumes(MediaType.APPLICATION_JSON)
    public DossierDto ajouterProuesse(@PathParam("id") String id, @Valid NouvelleProuesse prouesse) {
        return service.ajouterProuesse(id, prouesse);
    }
}
