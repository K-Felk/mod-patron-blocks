package org.folio.repository;

import java.util.List;

import org.folio.rest.persist.Criteria.Criteria;
import org.folio.rest.persist.Criteria.Criterion;
import org.folio.rest.persist.Criteria.Limit;
import org.folio.rest.persist.PgUtil;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.tools.PomReader;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public class EventRepository<T> extends BaseRepository<T> {
  private static final int NUMBER_OF_EVENTS_LIMIT = 10000;

  public EventRepository(PostgresClient pgClient, String tableName, Class<T> entityType) {
    super(pgClient, tableName, entityType);
  }

  public Future<List<T>> getByUserId(String userId) {
    return this.get(new Criterion(new Criteria()
      .addField("'userId'")
      .setOperation("=")
      .setVal(userId)
      .setJSONB(true)
    ).setLimit(new Limit(NUMBER_OF_EVENTS_LIMIT)));
  }

  public Future<Void> removeAll(String tenantId) {
    Promise<Void> promise = Promise.promise();

    String deleteAllQuery = String.format("DELETE FROM %s_%s.%s", tenantId,
      PomReader.INSTANCE.getModuleName(), tableName);
    pgClient.execute(deleteAllQuery, reply -> {
      if (reply.failed()) {
        promise.future().failed();
      } else {
        promise.complete();
      }
    });

    return promise.future();
  }

  public Future<Void> removeByUserId(String tenantId, String userId) {
    Promise<Void> promise = Promise.promise();

    String deleteByUserIdQuery = String.format(
      "DELETE FROM %s_%s.%s WHERE jsonb ->> 'userId' = '%s'", tenantId,
      PomReader.INSTANCE.getModuleName(), tableName, userId);
    pgClient.execute(deleteByUserIdQuery, reply -> {
      if (reply.failed()) {
        promise.future().failed();
      } else {
        promise.complete();
      }
    });

    return promise.future();
  }
}
