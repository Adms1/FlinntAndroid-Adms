package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;
/**
#Request
{
    "first_name": "test",
    "last_name": "test",
    "email": "d@d.com",
    "pwd": "123456"
}
 */

/**
 * Builds json request
 */
public class SignUpRequest {

	public static final String FIRST_NAME_KEY = "first_name";
	public static final String LAST_NAME_KEY = "last_name";
	public static final String EMAIL_KEY = "email";
	public static final String PASSWORD_KEY = "pwd";

	public String firstName = "";
	public String lastName = "";
	public String email = "";
	public String password = "";

    /**
     * Converts the json object to string
     * @return converted json string
     */
    synchronized public String getJSONString() {

		return getJSONObject().toString();
	}

    /**
     * creates json object
     * @return created json object
     */
    synchronized public JSONObject getJSONObject() {
		JSONObject returnedJObject = new JSONObject();
		try {
			returnedJObject.put(FIRST_NAME_KEY, firstName);
			returnedJObject.put(LAST_NAME_KEY, lastName);
			returnedJObject.put(EMAIL_KEY, email);
			returnedJObject.put(PASSWORD_KEY, password);
		} catch (Exception e) {
			LogWriter.err(e);
		}
		return returnedJObject;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
