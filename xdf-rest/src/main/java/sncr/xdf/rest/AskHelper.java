package sncr.xdf.rest;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import sncr.xdf.rest.messages.ActorMessage;

import java.util.concurrent.CompletableFuture;

public class AskHelper {
    @SuppressWarnings("unchecked")
    public static  <T extends ActorMessage> T ask(T what, ActorSelection whom, Long timeoutMillis) throws Exception{
        CompletableFuture<Object> wrapper
            = akka.pattern.PatternsCS.ask(whom, what, timeoutMillis).toCompletableFuture();
        CompletableFuture<Object> result
            = CompletableFuture.allOf(wrapper).thenApply(v -> wrapper.join()
        );
        return (T)result.get();
    }

    @SuppressWarnings("unchecked")
    public static  <T> T ask(T what, ActorRef whom, Long timeoutMillis) throws Exception{
        CompletableFuture<Object> wrapper
            = akka.pattern.PatternsCS.ask(whom, what, timeoutMillis).toCompletableFuture();
        CompletableFuture<Object> result =
            CompletableFuture.allOf(wrapper).thenApply(v -> wrapper.join());
        return (T)result.get();
    }

}
