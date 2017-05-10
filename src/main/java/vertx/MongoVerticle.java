package vertx;


import entities.Bet;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import jsoup.OddsScraper;

import java.util.List;
import java.util.stream.Collectors;

public class MongoVerticle extends AbstractVerticle {
    public static final String collectionName = "ElectionOdds";
    private static final String collectionUrl = "http://www.paddypower.com/bet/politics/other-politics/uk-politics?ev_oc_grp_ids=282167";
    private MongoClient mongo;

    @Override
    public void start(Future<Void> fut) {
        mongo = MongoClient.createShared(vertx, config());

        extractOdds(
                (nothing) -> startWebApp(
                        (http) -> completeStartup(http, fut)
                ), fut);
    }

    //helper functions
    private void startWebApp(Handler<AsyncResult<HttpServer>> next) {
        //router
        Router router = Router.router(vertx);

        //bind root page
        router.route("/").handler(routingContext ->{
            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "text/html")
                    .end("<h2>Government Composition Odds</h2>");
        });

        //api routing and handling
        router.route("/assets/*").handler(StaticHandler.create("assets"));
        router.get("/api/odds").handler(this::getAll);
        router.route("/api/odds*").handler(BodyHandler.create());
        router.get("/api/odds/:id").handler(this::getOne);

        //init http server and accept
        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        config().getInteger("http.port", 8080),
                        next::handle
                );
    }

    private void completeStartup(AsyncResult<HttpServer> http, Future<Void> fut) {
        if(http.succeeded()) {
            fut.complete();
        } else {
            fut.fail(http.cause());
        }
    }

    @Override
    public void stop() throws Exception {
        mongo.dropCollection(collectionName, event -> {

        });
        mongo.close();
    }


    private void getOne(RoutingContext routingContext) {
        //TODO
    }

    private void getAll(RoutingContext routingContext) {
        mongo.find(collectionName, new JsonObject(), results -> {
            List<JsonObject> objects = results.result();
            List<Bet> bets = objects.stream().map(Bet::new).collect(Collectors.toList());
            routingContext.response()
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(bets));
        });
    }

    private void extractOdds(Handler<AsyncResult<Void>> next, Future<Void> future){
        List<Bet> bets = new OddsScraper(collectionUrl).getBets();
        bets.forEach(bet -> {
            mongo.insert(collectionName, bet.toJson(), event -> {
                next.handle(Future.succeededFuture());
            });
        });
    }
}