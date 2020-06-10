package org.folio.rest.handlers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.folio.domain.OpenLoan;
import org.folio.domain.UserSummary;
import org.folio.rest.jaxrs.model.ItemDeclaredLostEvent;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class ItemDeclaredLostEventHandlerTest extends AbstractEventHandlerTest {

  private static final ItemDeclaredLostEventHandler eventHandler =
    new ItemDeclaredLostEventHandler(postgresClient);

  @Test
  public void userSummaryShouldBeCreatedWhenDoesntExist(TestContext context) {
    String userId = randomId();
    String loanId = randomId();

    ItemDeclaredLostEvent event = new ItemDeclaredLostEvent()
      .withUserId(userId)
      .withLoanId(loanId);

    String summaryId = waitFor(eventHandler.handle(event));

    UserSummary userSummaryToCompare = new UserSummary()
      .withUserId(userId)
      .withNumberOfLostItems(1)
      .withOutstandingFeeFineBalance(BigDecimal.ZERO);

    checkUserSummary(summaryId, userSummaryToCompare, context);
  }

  @Test
  public void shouldIncrementLostItemsWhenUserSummaryExists(TestContext context) {
    String userId = randomId();
    String loanId = randomId();

    UserSummary existingUserSummary = new UserSummary()
      .withUserId(userId)
      .withNumberOfLostItems(0)
      .withOutstandingFeeFineBalance(BigDecimal.ZERO);

    waitFor(userSummaryRepository.save(existingUserSummary));

    ItemDeclaredLostEvent event = new ItemDeclaredLostEvent()
      .withUserId(userId)
      .withLoanId(loanId);

    String summaryId = waitFor(eventHandler.handle(event));

    UserSummary userSummaryToCompare = new UserSummary()
      .withUserId(userId)
      .withNumberOfLostItems(1)
      .withOutstandingFeeFineBalance(BigDecimal.ZERO);

    checkUserSummary(summaryId, userSummaryToCompare, context);
  }

  @Test
  public void shouldRemoveOpenLoanFromUserSummaryWhenItExists(TestContext context) {
    String userId = randomId();
    String loanId = randomId();

    List<OpenLoan> existingOpenLoans = new ArrayList<>();
    existingOpenLoans.add(new OpenLoan()
      .withLoanId(loanId)
      .withRecall(false));

    UserSummary existingUserSummary = new UserSummary()
      .withUserId(userId)
      .withNumberOfLostItems(0)
      .withOutstandingFeeFineBalance(BigDecimal.ZERO)
      .withOpenLoans(existingOpenLoans);

    waitFor(userSummaryRepository.save(existingUserSummary));

    ItemDeclaredLostEvent event = new ItemDeclaredLostEvent()
      .withUserId(userId)
      .withLoanId(loanId);

    String summaryId = waitFor(eventHandler.handle(event));

    UserSummary userSummaryToCompare = new UserSummary()
      .withUserId(userId)
      .withNumberOfLostItems(1)
      .withOutstandingFeeFineBalance(BigDecimal.ZERO);

    checkUserSummary(summaryId, userSummaryToCompare, context);
  }
}
