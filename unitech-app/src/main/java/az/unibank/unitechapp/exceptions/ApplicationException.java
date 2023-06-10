/*
 * Copyrighted © 2015 DAAC System Integrator S.R.L. All rights reserved.
 */
package az.unibank.unitechapp.exceptions;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.PrintWriter;
import java.io.Serial;
import java.io.StringWriter;
import java.util.Locale;

public abstract class ApplicationException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 2222880007794158044L;

	protected String errorMessageShort;

	protected String errorMessageFull;

	public ApplicationException() {
		super();
	}

	public ApplicationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApplicationException(String message) {
		super(message);
	}

	/**
	 * @return the errorMessageFull
	 */
	public String getErrorMessageFull() {
		return errorMessageFull;
	}

	/**
	 * @return the errorMessageShort
	 */
	public String getErrorMessageShort() {
		return errorMessageShort;
	}

	/**
	 * @param errorMessageFull
	 *            the errorMessageFull to set
	 */
	public void setErrorMessageFull(String errorMessageFull) {
		this.errorMessageFull = errorMessageFull;
	}

	public void setErrorMessageShort(String errorMessageShort) {
		this.errorMessageShort = errorMessageShort;
	}

	/**
	 * Перерабатывает stacktrace в строку.
	 *
	 * @param ex
	 *            Какая-то ошибка.
	 * @return - stacktrace ошибки как строка.
	 *
	 * @since Sep 18, 2015
	 * @author Mihai.Triboi
	 */
	protected String stackTraceToString(Throwable ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		return sw.toString(); // stack trace as a string
	}
}
