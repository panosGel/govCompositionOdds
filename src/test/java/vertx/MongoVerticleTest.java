package vertx;

import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.process.runtime.Network;
import entities.Bet;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import de.flapdoodle.embed.mongo.distribution.Version;
import org.junit.*;
import org.junit.runner.RunWith;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;

import java.net.ServerSocket;

@RunWith(VertxUnitRunner.class)
public class MongoVerticleTest {

    private Vertx vertx;
    private Integer port;
    private static MongodProcess mongo;
    private static int mongo_port = 12345;

    @BeforeClass
    public static void initialize() throws Exception {
        MongodStarter starter = MongodStarter.getDefaultInstance();

        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(mongo_port, Network.localhostIsIPv6()))
                .build();

        MongodExecutable mongodExecutable = starter.prepare(mongodConfig);
        mongo = mongodExecutable.start();
    }

    @AfterClass
    public static void shutdown() {
        mongo.stop();
    }

    @Before
    public void setup(TestContext context) throws Exception {
        vertx = Vertx.vertx();

        //pick a random port - don't do that in production !
        ServerSocket socket = new ServerSocket(0);
        port = socket.getLocalPort();
        socket.close();

        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject()
                    .put("http.port", port)
                    .put("db_name", "odds-test")
                    .put("connection_string", "mongodb://localhost:" + mongo_port)
                );
        vertx.deployVerticle(MongoVerticle.class.getName(), options, context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void test_application_init(TestContext context) {

        Async async = context.async();
        vertx.createHttpClient().getNow(port, "localhost", "/", response -> {
            response.handler(body -> {
               context.assertTrue(body.toString().contains("<h2>Government Composition Odds</h2>"));
               async.complete();
            });
        });
    }

    @Test
    public void test_index_html(TestContext context) {
        Async async = context.async();
        vertx.createHttpClient().getNow(port,"localhost", "/assets/index.html", response -> {
            context.assertEquals(response.statusCode(), 200);
            context.assertEquals(response.headers().get("content-type"), "text/html;charset=UTF-8");
            response.bodyHandler(body -> {
               context.assertTrue(body.toString().contains("<title>Government Composition After Next Election</title>"));
               async.complete();
            });

        });
    }

    @Ignore
    @Test
    public void add_an_item_to_db(TestContext context) {
        Async async = context.async();
        final String json = Json.encodePrettily(new Bet("testBet","testBetOdds"));
        vertx.createHttpClient().post(port, "localhost", "/api/odds")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", Integer.toString(json.length()))
                .handler(response -> {
                    context.assertEquals(response.statusCode(), 201);
                    context.assertTrue(response.headers().get("content-type").contains("application/json"));
                    response.bodyHandler(body -> {
                       final Bet bet = Json.decodeValue(body.toString(), Bet.class);
                        context.assertNotNull(bet.getId());
                        context.assertEquals(bet.getTitle(),"testBet");
                        context.assertEquals(bet.getOdds(),"testBetOdds");
                        async.complete();
                    });
                })
                .write(json)
                .end();
    }
}