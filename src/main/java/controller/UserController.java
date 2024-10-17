package controller;

import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RouteBase;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.core.file.FileSystem;
import jakarta.inject.Inject;
import model.GetUserCountCreationPerCompanyPerYear;
import org.hibernate.reactive.mutiny.Mutiny;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.quarkus.vertx.web.Route.HttpMethod.GET;

@RouteBase(path = "/rest", produces = "application/json")
public class UserController {
    private static final org.jboss.logging.Logger LOG = Logger.getLogger(UserController.class);

    @Inject
    private Vertx vertx;

    @Inject
    private Mutiny.SessionFactory sessionFactory;


    public Uni<List<GetUserCountCreationPerCompanyPerYear>> getDBData(Uni<List<Integer>> companyIds) {
        String sql = "SELECT companyId, EXTRACT(YEAR FROM createDate) as year, COUNT(*) as userCount "
                + "FROM User WHERE companyId IN :companyIds "
                + "GROUP BY companyId, year "
                + "ORDER BY companyId, year";

        LOG.info("Session Factory: " + sessionFactory);

        return companyIds.flatMap(ids -> {
            LOG.info("Company IDs: " + ids);
            return sessionFactory.withSession(session -> {
                LOG.info("Session started");
                // Ensure the session createQuery is non-blocking
                return session.createQuery(sql, Object[].class)
                        .setParameter("companyIds", ids)
                        .getResultList()
                        .onItem().invoke(results -> LOG.info("Results size: " + results.size()))
                        .onFailure().invoke(ex -> LOG.info("Error occurred during query"))
                        .onItem().transformToUni(results -> {
                            List<GetUserCountCreationPerCompanyPerYear> dtoList = results.stream()
                                    .map(result -> new GetUserCountCreationPerCompanyPerYear(
                                            ((Number) result[0]).intValue(),
                                            result[1].toString(),
                                            ((Number) result[2]).intValue()
                                    ))
                                    .collect(Collectors.toList());
                            return Uni.createFrom().item(dtoList);
                        });
            });
        });
    }


//    private Uni<List<GetUserCountCreationPerCompanyPerYear>> getDBData(Uni<List<Integer>> companyIds) {
//
//        String sql = "SELECT companyId, EXTRACT(YEAR FROM createDate) as year, COUNT(*) as userCount " + "FROM User WHERE companyId IN :companyIds " + "GROUP BY companyId, year " + "ORDER BY companyId, year";
//
//        LOG.info("Session Factory: " + sessionFactory);
//        LOG.info("Before flatMap: companyIds fetched: " + companyIds);
//        return companyIds.flatMap(ids -> {
//            LOG.info("Company IDs: " + ids);  // Add logs here to see if execution reaches this point
//            return sessionFactory.withSession(session -> {
//                LOG.info("Session started");  // Add log here to confirm session creation
//                return session.createQuery(sql, Object[].class).setParameter("companyIds", ids).getResultList().onItem().invoke(results -> LOG.info("Results size: " + results.size())).onFailure().invoke(ex -> LOG.info("Error occurred during query")).onItem().transformToUni(results -> {
//                    LOG.info("Processing results: " + results.size());  // Check if processing reaches here
//                    List<GetUserCountCreationPerCompanyPerYear> dtoList = results.stream().map(result -> new GetUserCountCreationPerCompanyPerYear(((Number) result[0]).intValue(), result[1].toString(), ((Number) result[2]).intValue())).collect(Collectors.toList());
//                    return Uni.createFrom().item(dtoList);
//                });
//            });
//        });
//    }

    @Route(path = "/companies/user/count", methods = GET, type = Route.HandlerType.BLOCKING)
    public Uni<List<GetUserCountCreationPerCompanyPerYear>> getUserCountPerCompany() throws IOException {
        final FileSystem fileSystem = vertx.fileSystem();

        return fileSystem.readFile("files/input.txt").onItem().transform(Buffer::toString) // Transform buffer to string
                .onItem().invoke(content -> LOG.info("Input value: " + content)) // Log the content
                .onItem().transform(content -> // Process the content
                        Arrays.stream(content.split(",")).map(String::trim).map(Integer::parseInt).collect(Collectors.toList())).onFailure().invoke(err -> LOG.error("Failed to read or process file" + err)) // Error handling
                .flatMap(companyIds -> getDBData(Uni.createFrom().item(companyIds))); // Call repo
    }

    @Route(path = "/hello", methods = GET)
    public String hello()
    {
        return "hello";
    }
}
