package thuonghth.utils.input_validators;

import java.util.TreeMap;

public class WrapperInputObject<T> {
	private T inputObject;
	private boolean isValid;
	private TreeMap<String, String> fieldsMsg;

	public WrapperInputObject() {
		
	}

	public T getInputObject() {
		return inputObject;
	}

	public void setInputObject(T inputObject) {
		this.inputObject = inputObject;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public TreeMap<String, String> getFieldsMsg() {
		return fieldsMsg;
	}

	public void setFieldsMsg(TreeMap<String, String> fieldsMsg) {
		this.fieldsMsg = fieldsMsg;
	}
	
}
