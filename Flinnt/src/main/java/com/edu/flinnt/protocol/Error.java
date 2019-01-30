package com.edu.flinnt.protocol;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Error response object
 */
public class Error {

	public final String CODE_KEY = "code";
	public final String MESSAGE_KEY = "message";
	public final String FILE_KEY = "file";
	public final String LINE_KEY = "line";
	public final String TRACE_KEY = "trace";

	public int code = Flinnt.INVALID;
	public String message = "";
	public String file = "";
	public int line = Flinnt.INVALID;
	public String trace = "";

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
			returnedJObject.put(CODE_KEY, code);
			returnedJObject.put(MESSAGE_KEY, message);
			returnedJObject.put(FILE_KEY, file);
			returnedJObject.put(LINE_KEY, line);
			returnedJObject.put(TRACE_KEY, trace);
		} catch (Exception e) {
			LogWriter.err(e);
		}
		return returnedJObject;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public String getTrace() {
		return trace;
	}

	public void setTrace(String trace) {
		this.trace = trace;
	}

}
