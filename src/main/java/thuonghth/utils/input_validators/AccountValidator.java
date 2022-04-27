package thuonghth.utils.input_validators;

import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import thuonghth.users.AccountDTO;

/**
 * @author thuonghth
 */

public class AccountValidator {
	public static final String EMAIL_PATTERN = "^(?=.{1,320}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
			+ "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
	public static final String EMAIL_FORMAT_ERROR = "Email must be a well-formed email address. ";
	public static final int EMAIL_MAX_LENGTH = 320;
	public static final String EMAIL_MAX_LENGTH_ERROR = "Email must have at most " + EMAIL_MAX_LENGTH
			+ " characters. ";
	public static final String EMAIL_REQUIRED_FIELD_ERROR = "Email field must not be blank. ";
	public static final int PWD_MIN_LENGTH = 5;
	public static final String PWD_LENGTH_ERROR = "Password must have at least " + PWD_MIN_LENGTH
			+ " characters and begin and end with a non-blank character. ";

	public TreeMap<String, String> fieldsMsg;
	
	private String email;
	private String password;
	
	public static String validateEmail(String email) {
		email = email.trim();
		String errorMsg = "";
		if(email.isBlank()) {
			errorMsg = EMAIL_REQUIRED_FIELD_ERROR;
		}
		else if (email.length() <= EMAIL_MAX_LENGTH) {
			Pattern pattern = Pattern.compile(EMAIL_PATTERN);
			Matcher matcher = pattern.matcher(email);
			errorMsg += matcher.matches() ? "" : EMAIL_FORMAT_ERROR;
		} else
			errorMsg = EMAIL_MAX_LENGTH_ERROR;
		return errorMsg;
	}

	public static String validatePassword(String pwd) {
		pwd = pwd.trim();
		String errorMsg = "";
		errorMsg += pwd.length() >= PWD_MIN_LENGTH ? "" : PWD_LENGTH_ERROR;
		return errorMsg;
	}

	public AccountValidator(String email, String password) {
		this.email = email.trim();
		this.password = password.trim();
		this.fieldsMsg = new TreeMap<>();
	}
	
	public TreeMap<String, String> validateFields() {
		fieldsMsg.put(UserRegistrationInfoValidator.EMAIL_FIELD, validateEmail(this.email));
		fieldsMsg.put(UserRegistrationInfoValidator.PWD_FIELD, validatePassword(this.password));
		return fieldsMsg;
	}

	public boolean isFieldsValid() {
		boolean isValid = true;
		isValid = isValid && fieldsMsg.get(UserRegistrationInfoValidator.EMAIL_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(UserRegistrationInfoValidator.PWD_FIELD).equals("");
		return isValid;
	}
	
	public AccountDTO getAccountDTO() {
		return new AccountDTO(this.email, this.password);
	}
}
