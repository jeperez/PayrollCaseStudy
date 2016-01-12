package hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.main.integrationtests.usecaseswithinterfaceadapters.find;

import hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.app.usecase.usecases.find.GetEmployeeUseCase;
import hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.main.integrationtests.config.DatabaseProvider;
import hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.ports.primary.ui.requestresponse.request.GetEmployeeRequest;
import hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.ports.primary.ui.requestresponse.response.GetEmployeeResponse;
import hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.ports.primary.ui.requestresponse.response.employee.EmployeeItem;

public class GetEmployeesUseCaseITTest extends AbstractFindEmployeesUseCaseITTest<GetEmployeeResponse> {
	public GetEmployeesUseCaseITTest(DatabaseProvider databaseProvider) {
		super(databaseProvider);
	}

	@Override
	protected GetEmployeeResponse whenExecuteUseCase() {
		GetEmployeeUseCase useCase = useCaseFactory.getEmployeeUseCase();
		useCase.execute(new GetEmployeeRequest(employeeId));
		return useCase.getResponse();
	}

	@Override
	protected EmployeeItem getSingleResultEmployeeItem(GetEmployeeResponse response) {
		return response.employeeItem;
	}


}
