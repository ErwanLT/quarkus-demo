package fr.eletutour.tavern.batch.resource;

import fr.eletutour.tavern.batch.model.DayReport;
import fr.eletutour.tavern.batch.model.NightlyBatchResult;
import fr.eletutour.tavern.batch.model.StockSnapshot;
import fr.eletutour.tavern.batch.resource.dto.DayConsumptionRequestDto;
import fr.eletutour.tavern.batch.resource.error.ApiProblem;
import fr.eletutour.tavern.batch.service.TavernBreweryService;
import jakarta.batch.operations.JobOperator;
import jakarta.batch.runtime.BatchStatus;
import jakarta.batch.runtime.JobExecution;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Properties;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/tavern/brewery")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Tavern Brewery", description = "Gestion du stock et lancement du batch nocturne de brassage")
public class TavernBatchResource {

    private static final String NIGHTLY_JOB_NAME = "nightly-brew-job";

    @Inject
    TavernBreweryService tavernBreweryService;

    @Inject
    JobOperator jobOperator;

    @GET
    @Path("/stocks")
    @Operation(summary = "Consulter les stocks", description = "Retourne l'etat instantane des stocks et de la consommation du jour")
    @APIResponse(
        responseCode = "200",
        description = "Etat courant des stocks",
        content = @Content(schema = @Schema(implementation = StockSnapshot.class))
    )
    public StockSnapshot stocks() {
        return tavernBreweryService.currentStock();
    }

    @POST
    @Path("/day-consumptions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Enregistrer la consommation de journee",
        description = "Applique les commandes de la journee et retourne le rapport servi/rupture"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Consommation traitee",
            content = @Content(schema = @Schema(implementation = DayReport.class))
        ),
        @APIResponse(
            responseCode = "400",
            description = "Requete invalide",
            content = @Content(schema = @Schema(implementation = ApiProblem.class))
        )
    })
    public DayReport registerDayConsumption(@Valid @NotNull DayConsumptionRequestDto request) {
        return tavernBreweryService.consumeDuringDay(request.orders());
    }

    @POST
    @Path("/nightly-batches")
    @Operation(summary = "Lancer un lot nocturne", description = "Declenche le job JBeret de brassage nocturne et attend sa fin")
    @APIResponses({
        @APIResponse(
            responseCode = "201",
            description = "Lot termine",
            content = @Content(schema = @Schema(implementation = NightlyBatchResult.class))
        ),
        @APIResponse(
            responseCode = "500",
            description = "Echec technique pendant l'execution du lot",
            content = @Content(schema = @Schema(implementation = ApiProblem.class))
        )
    })
    public Response runNightlyBatch() {
        long executionId = jobOperator.start(NIGHTLY_JOB_NAME, new Properties());
        JobExecution execution = waitForCompletion(executionId);

        int totalBrewed = tavernBreweryService.getLastBrewedByStyle().values().stream()
            .mapToInt(Integer::intValue)
            .sum();

        NightlyBatchResult result = new NightlyBatchResult(
            executionId,
            NIGHTLY_JOB_NAME,
            execution.getBatchStatus().name(),
            totalBrewed,
            tavernBreweryService.getLastBrewedByStyle()
        );

        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    @PUT
    @Path("/stocks/reset")
    @Operation(summary = "Reinitialiser la simulation", description = "Remet les stocks au niveau initial pour rejouer un scenario")
    @APIResponse(
        responseCode = "200",
        description = "Stocks reinitialises",
        content = @Content(schema = @Schema(implementation = StockSnapshot.class))
    )
    public StockSnapshot resetStocks() {
        tavernBreweryService.resetSimulation();
        return tavernBreweryService.currentStock();
    }

    private JobExecution waitForCompletion(long executionId) {
        JobExecution execution = jobOperator.getJobExecution(executionId);
        int maxWaitCycles = 120;

        while (execution != null && isRunning(execution.getBatchStatus()) && maxWaitCycles-- > 0) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Batch interrupted while waiting for completion", interruptedException);
            }
            execution = jobOperator.getJobExecution(executionId);
        }

        if (execution == null) {
            throw new IllegalStateException("Nightly batch execution not found: " + executionId);
        }
        if (isRunning(execution.getBatchStatus())) {
            throw new IllegalStateException("Nightly batch still running after timeout: " + executionId);
        }

        return execution;
    }

    private boolean isRunning(BatchStatus status) {
        return status == BatchStatus.STARTED || status == BatchStatus.STARTING || status == BatchStatus.STOPPING;
    }
}
