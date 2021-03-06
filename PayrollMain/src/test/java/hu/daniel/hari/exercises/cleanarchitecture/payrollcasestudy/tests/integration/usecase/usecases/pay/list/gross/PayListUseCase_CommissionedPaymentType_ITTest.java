package hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.tests.integration.usecase.usecases.pay.list.gross;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.app.entity.Constants;
import hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.app.entity.PayCheck;
import hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.ports.primary.admin.usecase.request.AddSalesReceiptRequest;
import hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.ports.primary.admin.usecase.request.addemployee.AddCommissionedEmployeeRequest;
import hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.ports.primary.admin.usecase.response.PayListResponse.PayListResponseItem;
import hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.tests.integration.testutil.TestUtils;

public class PayListUseCase_CommissionedPaymentType_ITTest extends PayListUseCase_AbstractPaymentTypeITTest {
	private static final LocalDate AN_EVEN_FRIDAY = Constants.BIWEEKLY_PAYMENT_SCHEDULE_REFERENCE_FRIDAY;
	private static final LocalDate AN_ODD_FRIDAY = AN_EVEN_FRIDAY.plusDays(7);

	private static final LocalDate A_PAYDAY = AN_EVEN_FRIDAY;
	private static final LocalDate NOT_A_PAYDAY = AN_ODD_FRIDAY;
	
	private static final LocalDate A_PAYPERIOD_START = AN_EVEN_FRIDAY.minusDays(13);
	private static final LocalDate A_PAYPERIOD_END = AN_EVEN_FRIDAY;
	
	private int employeeId = 1;
	private int biWeeklyBaseSalary = 500;
	private double commissionRate = 0.1d;

	private abstract class Case {
		List<AddSalesReceiptRequest> salesReceipts;
		int thenPayCheckGrossAmountSum;
	}
	
	public PayListUseCase_CommissionedPaymentType_ITTest(TestDatabaseProvider testDatabaseProvider) {
		super(testDatabaseProvider);
	}
	
	@Test
	public void testPaySingleCommissionedEmployee_NoSalesReceipt_ThenBiWeeklyAmountPayCheck() throws Exception {
		givenWhenThen(new Case() {{
			salesReceipts = Collections.emptyList();
			thenPayCheckGrossAmountSum = biWeeklyBaseSalary;
		}});
	}
	
	@Test
	public void testPaySingleCommissionedEmployee_OneSalesReceipt() throws Exception {
		givenWhenThen(new Case() {{
			salesReceipts = new ArrayList<AddSalesReceiptRequest>() {{
				add(new AddSalesReceiptRequest(employeeId, AN_EVEN_FRIDAY, 1000));
			}};
			int commisionAmount = (int) (commissionRate * (1000));
			thenPayCheckGrossAmountSum = biWeeklyBaseSalary + commisionAmount;
		}});
	}
	
	@Test
	public void testPaySingleCommissionedEmployee_TwoSalesReceipt_AtBordersOfPayPeriod() throws Exception {
		givenWhenThen(new Case() {{
			salesReceipts = new ArrayList<AddSalesReceiptRequest>() {{
				add(new AddSalesReceiptRequest(employeeId, A_PAYPERIOD_START, 1000));
				add(new AddSalesReceiptRequest(employeeId, A_PAYPERIOD_END, 1100));
			}};
			int commisionAmount = (int) (commissionRate * (1000 + 1100));
			thenPayCheckGrossAmountSum = biWeeklyBaseSalary + commisionAmount;
		}});
	}
	
	@Test
	public void testPaySingleCommissionedEmployee_ThreeSalesReceipts_SpanningThreePayPeriods__ThenShouldIgnoreOtherPeriods() throws Exception {
		givenWhenThen(new Case() {{
				salesReceipts = new ArrayList<AddSalesReceiptRequest>() {{
					add(new AddSalesReceiptRequest(employeeId, A_PAYPERIOD_START.minusDays(1), 1100));  //should be ignored
					add(new AddSalesReceiptRequest(employeeId, A_PAYPERIOD_END, 1200));
					add(new AddSalesReceiptRequest(employeeId, A_PAYPERIOD_END.plusDays(1), 1300));		//should be ignored
				}};
				int commisionAmount = (int) (commissionRate * (1200));
				thenPayCheckGrossAmountSum = biWeeklyBaseSalary + commisionAmount;
			}}
		);
	}
	
	private void givenWhenThen(Case theCase) {
		givenACommissionedEmployee();
		givenSalesReceipts(theCase.salesReceipts);
		
		Collection<PayListResponseItem> payChecks = whenGeneratePayUseCaseExecuted(getAPayday());
		
		thenPayCheckGrossAmountShouldBe(payChecks, theCase.thenPayCheckGrossAmountSum);
	}
	
	private void givenACommissionedEmployee() {
		useCaseFactories.addCommissionedEmployeeUseCase().execute(new AddCommissionedEmployeeRequest(employeeId, "", "", biWeeklyBaseSalary, commissionRate));
	}

	private void givenSalesReceipts(List<AddSalesReceiptRequest> salesReceipts) {
		for (AddSalesReceiptRequest addSalesReceiptRequest : salesReceipts) {
			useCaseFactories.addSalesReceiptUseCaseFactory().execute(addSalesReceiptRequest);
		}
	}

	@Override
	protected void givenAnEmployee() {
		givenACommissionedEmployee();
	}
	
	@Override
	protected LocalDate getNotAPayday() {
		return NOT_A_PAYDAY;
	}

	@Override
	protected LocalDate getAPayday() {
		return A_PAYDAY;
	}

}
