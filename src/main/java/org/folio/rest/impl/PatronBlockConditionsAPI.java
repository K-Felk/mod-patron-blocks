package org.folio.rest.impl;

import static io.vertx.core.Future.succeededFuture;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.folio.rest.tools.utils.ValidationHelper.createValidationErrorMessage;

import java.util.Map;

import javax.ws.rs.core.Response;

import org.folio.rest.annotations.Validate;
import org.folio.rest.jaxrs.model.Errors;
import org.folio.rest.jaxrs.model.PatronBlockCondition;
import org.folio.rest.jaxrs.resource.PatronBlockConditions;
import org.folio.rest.persist.PgUtil;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;

public class PatronBlockConditionsAPI implements PatronBlockConditions {

  private static final String PATRON_BLOCK_CONDITIONS = "patron_block_conditions";

  @Validate
  @Override
  public void getPatronBlockConditions(int offset, int limit, String query,
    String lang, Map<String, String> okapiHeaders,
    Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {

    PgUtil.get(PATRON_BLOCK_CONDITIONS, PatronBlockCondition.class,
      org.folio.rest.jaxrs.model.PatronBlockConditions.class, query, offset, limit,
      okapiHeaders, vertxContext, GetPatronBlockConditionsResponse.class, asyncResultHandler);
  }

  @Validate
  @Override
  public void putPatronBlockConditionsByPatronBlockConditionId(
    String patronBlockConditionId, String lang, PatronBlockCondition entity,
    Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler,
    Context vertxContext) {

    Errors errors = validateEntity(entity);
    if (errors != null) {
      asyncResultHandler.handle(succeededFuture(
        PutPatronBlockConditionsByPatronBlockConditionIdResponse
          .respond422WithApplicationJson(errors)));
      return;
    }

    PgUtil.put(PATRON_BLOCK_CONDITIONS, entity, patronBlockConditionId, okapiHeaders,
      vertxContext, PutPatronBlockConditionsByPatronBlockConditionIdResponse.class, asyncResultHandler);
  }

  @Validate
  @Override
  public void getPatronBlockConditionsByPatronBlockConditionId(
    String patronBlockConditionId, String lang, Map<String, String> okapiHeaders,
    Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {

    PgUtil.getById(PATRON_BLOCK_CONDITIONS, PatronBlockCondition.class, patronBlockConditionId,
      okapiHeaders, vertxContext, GetPatronBlockConditionsByPatronBlockConditionIdResponse.class,
      asyncResultHandler);
  }

  private Errors validateEntity(PatronBlockCondition entity) {

    if (isMessageBlank(entity) && isAnyFlagTrue(entity)) {
      return createValidationErrorMessage("patron block condition id", entity.getId(),
        "Message to be displayed is a required field if one or more blocked actions selected");
    }
    if (!isMessageBlank(entity) && !isAnyFlagTrue(entity)) {
      return createValidationErrorMessage("patron block condition id", entity.getId(),
        "One or more blocked actions must be selected for message to be displayed to be used");
    }
    return null;
  }

  private boolean isMessageBlank(PatronBlockCondition entity) {
    return isBlank(entity.getMessage());
  }

  private boolean isAnyFlagTrue(PatronBlockCondition entity) {
    return entity.getBlockBorrowing() || entity.getBlockRenewals() || entity.getBlockRequests();
  }
}
