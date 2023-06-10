
package az.unibank.unitechapp.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serial;
import java.util.Arrays;


@JsonIgnoreProperties(value = {"stackTrace","suppressed","cause","localizedMessage"})
public class BusinessException extends ApplicationException {

	@Serial
	private static final long serialVersionUID = 2696292738876062661L;

	private boolean businessException = true;

	private Object[] args;

	private String key;



	/**
	 * @param errorMessageShort
	 *            -
	 * @param errorMessageFull
	 */
	public BusinessException(String errorMessageShort, String errorMessageFull) {
		super(errorMessageFull);
		this.errorMessageShort = errorMessageShort;
		this.errorMessageFull = errorMessageFull;
	}



	private BusinessException(String errorMessageShort, String errorMessageFull,String key, Object... args) {
		super(errorMessageFull);
		this.errorMessageShort = errorMessageShort;
		this.errorMessageFull = errorMessageFull;
		this.args = args;
		this.key = key;
	}


	public BusinessException(String businessMessageSummary, Throwable tx) {
		super(businessMessageSummary, tx);
		this.errorMessageShort = "Ошибка" ;
		this.errorMessageFull = businessMessageSummary;
		this.setStackTrace(tx.getStackTrace());
	}

	@Override
	public String toString() {
		return "BusinessException{" +
				"businessException=" + businessException +
				", args=" + Arrays.toString(args) +
				", key='" + key + '\'' +
				'}';
	}

	/**
	 * @return the businessException
	 */
	public boolean isBusinessException() {
		return businessException;
	}

	/**
	 * @param businessException
	 *            the businessException to set
	 */
	public void setBusinessException(boolean businessException) {
		this.businessException = businessException;
	}
	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
