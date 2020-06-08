package org.folio.rest.impl;

import static org.folio.domain.EventType.FEE_FINE_BALANCE_CHANGED;

import java.lang.invoke.MethodHandles;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.folio.domain.EventType;
import org.folio.rest.handlers.FeeFineBalanceChangedEventHandler;
import org.folio.rest.handlers.ItemCheckedOutEventHandler;
import org.folio.rest.jaxrs.model.FeeFineBalanceChangedEvent;
import org.folio.rest.jaxrs.model.ItemCheckedOutEvent;
import org.folio.rest.jaxrs.resource.AutomatedPatronBlocksHandlers;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class EventHandlersAPI implements AutomatedPatronBlocksHandlers {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Override
  public void postAutomatedPatronBlocksHandlersFeeFineBalanceChanged(
    FeeFineBalanceChangedEvent payload, Map<String, String> okapiHeaders,
    Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {

    asyncResultHandler.handle(Future.succeededFuture(
      PostAutomatedPatronBlocksHandlersFeeFineBalanceChangedResponse.respond204()));

    logEventReceived(FEE_FINE_BALANCE_CHANGED);

    new FeeFineBalanceChangedEventHandler(okapiHeaders, vertxContext.owner())
      .handle(payload);
  }

  @Override
  public void postAutomatedPatronBlocksHandlersItemCheckedOut(ItemCheckedOutEvent payload,
    Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler,
    Context vertxContext) {

    asyncResultHandler.handle(Future.succeededFuture(
      PostAutomatedPatronBlocksHandlersItemCheckedOutResponse.respond204()));

    logEventReceived(EventType.ITEM_CHECKED_OUT);

    new ItemCheckedOutEventHandler(okapiHeaders, vertxContext.owner()).handle(payload);
  }

  private static void logEventReceived(EventType eventType) {
    log.info("Received {} event", eventType.name());
  }
}
