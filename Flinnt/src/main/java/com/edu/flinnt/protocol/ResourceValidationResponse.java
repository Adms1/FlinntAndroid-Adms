package com.edu.flinnt.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
#response:
{
  "status": 1,
  "data": {
    "image": {
      "max_file_size": "5242880",
      "scale_max_width": 800,
      "scale_max_height": 533,
      "min_width": "300",
      "min_height": "200"
    },
    "video": {
      "bitrate": 256000,
      "max_file_size": "10485760",
      "scale_max_width": 360,
      "file_types": "mp4,3gp"
    },
    "audio": {
      "bitrate": 98304,
      "max_file_size": "10485760",
      "file_types": "mp3"
    },
    "doc": {
      "file_types": "pdf",
      "max_file_size": "36700160"
    }
  }
}
*/

/*
{
  "status": 1,
  "data": {
    "image": {
      "max_file_size": "5242880",
      "file_types": "jpg,jpeg,png,bmp,gif",
      "min_width": 50,
      "min_height": 50,
      "crop": {
        "aspect_ratio": "1"
      }
    }
  }
}
*/


/**
 * class to parse response to object
 */
public class ResourceValidationResponse extends BaseResponse {

	public static final String IMAGE_KEY = "image";
	public static final String VIDEO_KEY = "video";
	public static final String AUDIO_KEY = "audio";
	public static final String DOC_KEY = "doc";

	public Validation image = null;
	public Validation video = null;
	public Validation audio = null;
	public Validation doc = null;


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
		JSONObject jsonObject;
		try {
			if( jsonData.has(IMAGE_KEY) ) {
				image = new Validation();
				jsonObject = jsonData.getJSONObject(IMAGE_KEY);
				image.parseJSONObject(jsonObject);
				if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Validation image : " + image.toString());
			}
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			if( jsonData.has(VIDEO_KEY) ) {
				video = new Validation();
				jsonObject = jsonData.getJSONObject(VIDEO_KEY);
				video.parseJSONObject(jsonObject);
				if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Validation video : " + video.toString());
			}
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			if( jsonData.has(AUDIO_KEY) ) {
				audio = new Validation();
				jsonObject = jsonData.getJSONObject(AUDIO_KEY);
				audio.parseJSONObject(jsonObject);
				if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Validation audio : " + audio.toString());
			}
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			if( jsonData.has(DOC_KEY) ) {
				doc = new Validation();
				jsonObject = jsonData.getJSONObject(DOC_KEY);
				doc.parseJSONObject(jsonObject);
				if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Validation doc : " + doc.toString());
			}
		} catch (Exception e) {
			LogWriter.err(e);
		}
	}


	public Validation getImage() {
		return image;
	}


	public void setImage(Validation image) {
		this.image = image;
	}


	public Validation getVideo() {
		return video;
	}


	public void setVideo(Validation video) {
		this.video = video;
	}


	public Validation getAudio() {
		return audio;
	}


	public void setAudio(Validation audio) {
		this.audio = audio;
	}


	public Validation getDoc() {
		return doc;
	}


	public void setDoc(Validation doc) {
		this.doc = doc;
	}


/*
	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("userID : " + userID)
		.append(", userLogin : " + userLogin)
		.append(", firstName : " + firstName)
		.append(", lastName : " + lastName)
		.append(", isActive : " + isActive)
		.append(", userPicture : " + userPicture)
		//.append(", imagePath : " + imagePath)
		.append(", accVerified : " + accVerified)
		.append(", accAuthMode : " + accAuthMode)
		.append(", canAdd : " + canAdd)
		.append(", userPictureUrl : " + userPictureUrl);
		return strBuffer.toString();
	}*/
}
