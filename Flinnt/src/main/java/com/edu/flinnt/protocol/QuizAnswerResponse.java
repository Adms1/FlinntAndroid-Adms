package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * class to parse response to object
 */
public class QuizAnswerResponse extends BaseResponse {
	
	public static final String SUBMITTED_KEY 						= "submitted";
	public static final String CORRECT_ANSWER_KEY 				= "correct_answer";
	public static final String IS_CORRECT_KEY 					= "is_correct";
	
	private int isSubmitted = Flinnt.INVALID;
	private String correctAnswer = "";
	private int isCorrect = Flinnt.INVALID;

    /**
     * Converts json string to json object
     * @param jsonData json string
     */
    public synchronized void parseJSONString(String jsonData) {

		if( TextUtils.isEmpty(jsonData) ) {
			if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("jsonData is empty. so return");
			return;
		}

		try {
			JSONObject jsonObject = new JSONObject(jsonData);
			parseJSONObject(jsonObject); 
		} catch (Exception e) {
			LogWriter.err(e);
		}
	}

    /**
     * parse json object to suitable data types
     * @param jsonData json object
     */
    public synchronized void parseJSONObject(JSONObject jsonData) {
		
		try {
			setIsSubmitted( jsonData.getInt(SUBMITTED_KEY) );
		} 
		catch (Exception e) {	LogWriter.err(e);}
		
		try {
			setCorrectAnswer( jsonData.getString(CORRECT_ANSWER_KEY) );
		} 
		catch (Exception e) {	LogWriter.err(e);}
		
		try {
			setIsCorrect( jsonData.getInt(IS_CORRECT_KEY) );
		} 
		catch (Exception e) {	LogWriter.err(e);}
	}
	
	public int getIsSubmitted() {
		return isSubmitted;
	}
	public void setIsSubmitted(int isSubmitted) {
		this.isSubmitted = isSubmitted;
	}
	public String getCorrectAnswer() {
		return correctAnswer;
	}
	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}
	public int getIsCorrect() {
		return isCorrect;
	}
	public void setIsCorrect(int isCorrect) {
		this.isCorrect = isCorrect;
	}

}
