package com.edu.flinnt.protocol;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
/**
#Request
{
    "user_id": "273",
    "course_id": "114"	
    "post_type": [1,6]
}
	Note : post_type (Optional)
 */

/**
 * Builds json request
 */
public class PostStatisticsRequest {

	public static final String USER_ID_KEY = "user_id";
	public static final String COURSE_ID_KEY = "course_id";
	public static final String POST_TYPE_KEY = "post_type";

	public String userId = "";
	public String courseId = "";
	private ArrayList<Integer> postType = new ArrayList<Integer>();

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
			returnedJObject.put(USER_ID_KEY, userId);
			returnedJObject.put(COURSE_ID_KEY, courseId);

			JSONArray postTypeArray = getPostTypeJsonArray();
			if( postTypeArray.length() > 0 ) {
				returnedJObject.put(POST_TYPE_KEY, postTypeArray);
			}
		} catch (Exception e) {
			LogWriter.err(e);
		}
		return returnedJObject;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public ArrayList<Integer> getPostType() {
		return postType;
	}

	public void setPostType(ArrayList<Integer> postType) {
		this.postType = postType;
	}

	private JSONArray getPostTypeJsonArray() {
		JSONArray jArray = new JSONArray();
		if(null != postType){
			for (int i = 0; i < postType.size(); i++) {
				jArray.put( postType.get(i) );
			}
		}
		return jArray;
	}

}
