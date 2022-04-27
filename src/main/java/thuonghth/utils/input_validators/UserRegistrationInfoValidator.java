package thuonghth.utils.input_validators;

import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import thuonghth.users.UserDTO;
import thuonghth.utils.MyConstants;
import thuonghth.utils.PBKDF2PasswordHasher;

/**
 * @author thuonghth
 */

public class UserRegistrationInfoValidator {
	private String email, password, confirmPassword, phone, name, address;

	public final static String EMAIL_FIELD = "email";
	public final static String PWD_FIELD = "password";
	public final String CONFIRM_PWD_FIELD = "confirmPassword";
	public final String PHONE_FIELD = "phone";
	public final String NAME_FIELD = "name";
	public final String ADDRESS_FIELD = "address";

	public final String REQUIRED_FIELD_ERROR = "This field must not be blank. ";

	public final String CONFIRM_PWD_NOT_MATCHED_ERROR = "Confirm password must match the password field. ";

	public final String PHONE_PATTERN = "^[0-9]{10}$";

	public final String PHONE_FORMAT_ERROR = "Phone number must contain 10 digits. ";

	public final String NAME_PATTERN = "^[a-z0-9A-Z_\\u00C0-\\u00FF\\\\u1EA0-\\u1EFF\\s]+$";
	public final int NAME_MAX_LENGTH = 100;

	public final String NAME_FORMAT_ERROR = "Name must contain only Vietnamese characters, Latin characters and blank spaces. ";

	public final int ADDRESS_MAX_LENGTH = 150;

	public TreeMap<String, String> fieldsMsg;

	public UserRegistrationInfoValidator() {
		fieldsMsg = new TreeMap<String, String>();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMaxLengthError(int length) {
		return "This field must not be exceed " + length + " characters. ";
	}

	public String validateRequiredString(String str) {
		return str.isBlank() ? REQUIRED_FIELD_ERROR : "";
	}

	public String validatePhone(String phone) {
		phone = phone.trim();
		String errorMsg = "";
		errorMsg += validateRequiredString(phone);
		Pattern pattern = Pattern.compile(PHONE_PATTERN);
		errorMsg += pattern.matcher(phone).matches() ? "" : PHONE_FORMAT_ERROR;
		return errorMsg;

	}

	public String checkConfirmPassword() {
		return this.password.trim().equals(this.confirmPassword.trim()) ? "" : CONFIRM_PWD_NOT_MATCHED_ERROR;
	}

	public String validateAddress(String address) {
		address = address.trim();
		String errorMsg = "";
		errorMsg += validateRequiredString(address);
		errorMsg += address.length() <= ADDRESS_MAX_LENGTH ? "" : getMaxLengthError(ADDRESS_MAX_LENGTH);
		return errorMsg;
	}

	public String validateName(String name) {
		name = name.trim();
		String errorMsg = "";

		errorMsg += validateRequiredString(name);

		Pattern pattern = Pattern.compile(NAME_PATTERN);
		Matcher matcher = pattern.matcher(name);
		errorMsg += matcher.matches() ? "" : NAME_FORMAT_ERROR;

		errorMsg += name.length() <= NAME_MAX_LENGTH ? "" : getMaxLengthError(NAME_MAX_LENGTH);

		return errorMsg;
	}

	public UserRegistrationInfoValidator(String email, String password, String confirmPassword, String name,
			String phone, String address) {
		this.email = email.trim();
		this.password = password.trim();
		this.confirmPassword = confirmPassword.trim();
		this.name = name.trim();
		this.phone = phone.trim();
		this.address = address.trim();
		this.fieldsMsg = new TreeMap<String, String>();
	}

	public TreeMap<String, String> validateUser() {
		fieldsMsg.put(EMAIL_FIELD, AccountValidator.validateEmail(this.email));
		fieldsMsg.put(PWD_FIELD, AccountValidator.validatePassword(this.password));
		fieldsMsg.put(CONFIRM_PWD_FIELD, checkConfirmPassword());
		fieldsMsg.put(PHONE_FIELD, validatePhone(this.phone));
		fieldsMsg.put(NAME_FIELD, validateName(this.name));
		fieldsMsg.put(ADDRESS_FIELD, validateAddress(this.address));
		return fieldsMsg;
	}

	public boolean isUserInfoValid() {
		boolean isValid = true;
		isValid = isValid && fieldsMsg.get(EMAIL_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(PWD_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(CONFIRM_PWD_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(PHONE_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(NAME_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(ADDRESS_FIELD).equals("");
		return isValid;
	}

	public UserDTO getUserDTO() {
		String hashedPwd = new PBKDF2PasswordHasher().hash(this.password);
		return new UserDTO(email, hashedPwd, name, phone, address, MyConstants.ROLE_CUSTOMER_USER,
				MyConstants.STATUS_NEW);
	}

}
