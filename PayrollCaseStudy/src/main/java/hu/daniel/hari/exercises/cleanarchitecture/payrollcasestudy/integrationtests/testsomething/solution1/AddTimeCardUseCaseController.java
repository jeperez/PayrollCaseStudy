package hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.integrationtests.testsomething.solution1;

import java.time.LocalDate;

import hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.core.boundary.userapi.requestmodels.AddTimeCardRequest;
import hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.core.usecase.AddTimeCardUseCase;

public class AddTimeCardUseCaseController {
	
	private AddTimeCardUseCaseFactory addTimeCardUseCaseFactory;

	public AddTimeCardUseCaseController(AddTimeCardUseCaseFactory addTimeCardUseCaseFactory) {
		this.addTimeCardUseCaseFactory = addTimeCardUseCaseFactory;
	}
	
	public void addTimeCard(int employeeId, LocalDate date, int workingHoursQty) {
		addTimeCardUseCaseFactory.create()
			.execute(new AddTimeCardRequest(employeeId, date, workingHoursQty));
		
	}
	
}
