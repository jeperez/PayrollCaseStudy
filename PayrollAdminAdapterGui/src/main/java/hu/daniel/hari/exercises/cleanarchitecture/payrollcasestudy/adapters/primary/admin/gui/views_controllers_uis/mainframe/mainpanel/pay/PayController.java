package hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.adapters.primary.admin.gui.views_controllers_uis.mainframe.mainpanel.pay;

import java.time.LocalDate;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.common.eventbus.EventBus;

import hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.adapters.primary.admin.gui.events.PaymentsFulfilledEvent;
import hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.adapters.primary.admin.gui.formatters.controller.msg.ConfirmMessageFormatter;
import hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.adapters.primary.admin.gui.views_controllers_uis.AbstractController;
import hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.adapters.primary.admin.gui.views_controllers_uis.Observable;
import hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.adapters.primary.admin.gui.views_controllers_uis.Observable.ChangeListener;
import hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.adapters.primary.admin.gui.views_controllers_uis.dialog.common.confirm.ConfirmDialogUI;
import hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.adapters.primary.admin.gui.views_controllers_uis.dialog.common.confirm.ConfirmDialogUI.ConfirmDialogListener;
import hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.adapters.primary.admin.gui.views_controllers_uis.mainframe.mainpanel.pay.PayView.PayViewListener;
import hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.adapters.primary.admin.gui.views_controllers_uis.mainframe.mainpanel.pay.PayView.PayViewModel;
import hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.adapters.primary.admin.gui.views_controllers_uis.mainframe.mainpanel.pay.paylist.PayListState;
import hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.ports.primary.admin.usecase.factories.PaymentFulfillUseCaseFactory;
import hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.ports.primary.admin.usecase.request.PaymentFulfillRequest;
import hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.ports.primary.admin.usecase.response.PaymentFulfillResponse;

public class PayController extends 
	AbstractController<PayView, PayViewListener> implements
	PayViewListener, 
	ChangeListener<PayListState> 
{

	private PaymentFulfillUseCaseFactory paymentFulfillUseCaseFactory;
	private Observable<LocalDate> observableCurrentDate;
	private Observable<PayListState> observablePayListState;
	private Provider<ConfirmDialogUI> confirmDialogUIProvider;
	private ConfirmMessageFormatter confirmMessageFormatter;
	private EventBus eventBus;

	@Inject
	public PayController(
			PaymentFulfillUseCaseFactory paymentFulfillUseCaseFactory,
			Provider<ConfirmDialogUI> confirmDialogUIProvider,
			ConfirmMessageFormatter confirmMessageFormatter,
			EventBus eventBus
			) {
		this.paymentFulfillUseCaseFactory = paymentFulfillUseCaseFactory;
		this.confirmDialogUIProvider = confirmDialogUIProvider;
		this.confirmMessageFormatter = confirmMessageFormatter;
		this.eventBus = eventBus;
	}

	public void setObservableCurrentDate(Observable<LocalDate> observableCurrentDate) {
		this.observableCurrentDate = observableCurrentDate;
	}

	public void setObservablePayListState(Observable<PayListState> observablePayListState) {
		this.observablePayListState = observablePayListState;
		observablePayListState.addChangeListener(this);
	}

	@Override
	public void onFulfillPayAction() {
		confirmDialogUIProvider.get().show(confirmMessageFormatter.fulfillPayments(observablePayListState.get().itemCount), new ConfirmDialogListener() {
			@Override
			public void onOk() {
				PaymentFulfillResponse response = paymentFulfillUseCaseFactory.paymentFulfillUseCase().execute(
						new PaymentFulfillRequest(observableCurrentDate.get()));
				eventBus.post(new PaymentsFulfilledEvent(observableCurrentDate.get(), 
						response.payCheckCount, response.totalNetAmount));
			}
		});
	}

	@Override
	public void onChanged(PayListState payListState) {
		getView().setModel(present(observablePayListState.get()));
	}

	private PayViewModel present(PayListState payListState) {
		return new PayViewModel() {{
			isFulfillButtonEnabled = !payListState.isEmpty;
		}};
	}

	@Override
	protected PayViewListener getViewListener() {
		return this;
	}

}
