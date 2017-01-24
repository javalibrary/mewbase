package io.mewbase.client;

import io.mewbase.bson.BsonArray;
import io.mewbase.bson.BsonObject;
import io.mewbase.client.spi.ClientFactory;
import io.mewbase.common.SubDescriptor;
import io.vertx.core.ServiceHelper;
import io.vertx.core.Vertx;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by tim on 21/09/16.
 */
public interface Client {

    static Client newClient(ClientOptions options) {
        return factory.newClient(options);
    }

    static Client newClient(Vertx vertx, ClientOptions options) {
        return factory.newClient(vertx, options);
    }

    // Error codes

    int ERR_AUTHENTICATION_FAILED = 1;
    int ERR_NOT_AUTHORISED = 2;
    int ERR_NO_SUCH_CHANNEL = 3;
    int ERR_NO_SUCH_BINDER = 4;
    int ERR_AUTHORISATION_FAILED = 5;
    int ERR_SERVER_ERROR = 100;


    ClientFactory factory = ServiceHelper.loadFactory(ClientFactory.class);

    // Query operations

    CompletableFuture<BsonObject> findByID(String binderName, String id);

    // TODO use Reactive streams for this instead
    void findMatching(String binderName, BsonObject matcher,
                      Consumer<QueryResult> resultHandler, Consumer<Throwable> exceptionHandler);

    // Pub/sub operations

    CompletableFuture<Subscription> subscribe(SubDescriptor subDescriptor, Consumer<ClientDelivery> handler);

    Producer createProducer(String channel);

    CompletableFuture<Void> publish(String channel, BsonObject event);

    CompletableFuture<Void> publish(String channel, BsonObject event, Function<BsonObject, String> partitionFunc);

    // Admin operations

    CompletableFuture<BsonArray> listBinders();

    CompletableFuture<Boolean> createBinder(String binderName);

    CompletableFuture<BsonArray> listChannels();

    CompletableFuture<Boolean> createChannel(String binderName);

    CompletableFuture<Void> close();

}
