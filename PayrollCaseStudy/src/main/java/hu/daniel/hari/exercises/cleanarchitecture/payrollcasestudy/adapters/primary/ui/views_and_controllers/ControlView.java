package hu.daniel.hari.exercises.cleanarchitecture.payrollcasestudy.adapters.primary.ui.views_and_controllers;

public interface ControlView<VL> extends 
	View
{
	void setViewListener(VL listener);
}