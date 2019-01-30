package com.edu.flinnt.protocol;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
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

public class Validation {

	public static final String MIN_WIDTH_KEY = "min_width";
	public static final String MIN_HEIGHT_KEY = "min_height";
	public static final String SCALE_MAX_WIDTH_KEY = "scale_max_width";
	public static final String SCALE_MAX_HEIGHT_KEY = "scale_max_height";
	public static final String BITRATE_KEY = "bitrate";
	public static final String FILE_TYPES_KEY = "file_types";
	public static final String MAX_FILE_SIZE_KEY = "max_file_size";
	public static final String CROP_KEY = "crop";
	public static final String ASPECT_RATIO_KEY = "aspect_ratio";
	public static final String MAX_ALBUM_IMAGES_KEY = "max_album_images";
	
	
	private int minWidth = Flinnt.INVALID;
	private int minHeight = Flinnt.INVALID;
	private int scaleMaxWidth = Flinnt.INVALID;
	private int scaleMaxHeight = Flinnt.INVALID;
	private int bitrate = Flinnt.INVALID;
	private String maxFileSize = "";
	private String aspectRatio = "";
	private String fileTypes = "";  // comma separated string
	private String maxAlbumImages = "";

    /**
     * parse json object to suitable data types
     * @param jsonData json object
     */
    public synchronized void parseJSONObject(JSONObject jsonData) {
		try {
			if(jsonData.has(MIN_WIDTH_KEY)) setMinWidth(jsonData.getInt(MIN_WIDTH_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			if(jsonData.has(MIN_HEIGHT_KEY)) setMinHeight(jsonData.getInt(MIN_HEIGHT_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			if(jsonData.has(SCALE_MAX_WIDTH_KEY)) setScaleMaxWidth(jsonData.getInt(SCALE_MAX_WIDTH_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			if(jsonData.has(SCALE_MAX_HEIGHT_KEY)) setScaleMaxHeight(jsonData.getInt(SCALE_MAX_HEIGHT_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			if(jsonData.has(BITRATE_KEY)) setBitrate(jsonData.getInt(BITRATE_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			if(jsonData.has(FILE_TYPES_KEY)) setFileTypes(jsonData.getString(FILE_TYPES_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}

		try {
			if(jsonData.has(MAX_FILE_SIZE_KEY)) setMaxFileSize(jsonData.getString(MAX_FILE_SIZE_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
		try {
			if(jsonData.has(MAX_ALBUM_IMAGES_KEY)) setMaxAlbumImages(jsonData.getString(MAX_ALBUM_IMAGES_KEY));
		} catch (Exception e) {
			LogWriter.err(e);
		}
		
		try {
			if( jsonData.has(CROP_KEY) ) {
				JSONObject jsonObject = jsonData.getJSONObject(CROP_KEY);
				if(jsonObject.has(ASPECT_RATIO_KEY)) setAspectRatio(jsonObject.getString(ASPECT_RATIO_KEY));
			}
		} catch (Exception e) {
			LogWriter.err(e);
		}
	}


	public int getMinWidth() {
		return minWidth;
	}


	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}


	public int getMinHeight() {
		return minHeight;
	}


	public void setMinHeight(int minHeight) {
		this.minHeight = minHeight;
	}


	public int getScaleMaxWidth() {
		return scaleMaxWidth;
	}


	public void setScaleMaxWidth(int scaleMaxWidth) {
		this.scaleMaxWidth = scaleMaxWidth;
	}


	public int getScaleMaxHeight() {
		return scaleMaxHeight;
	}


	public void setScaleMaxHeight(int scaleMaxHeight) {
		this.scaleMaxHeight = scaleMaxHeight;
	}


	public int getBitrate() {
		return bitrate;
	}


	public void setBitrate(int bitrate) {
		this.bitrate = bitrate;
	}


	public String getMaxFileSize() {
		return maxFileSize;
	}

	public Long getMaxFileSizeLong() {
		long size = Flinnt.INVALID;
		try {
			size = Long.parseLong(maxFileSize);
		} catch (Exception e) {
		}
		return size;
	}


	public void setMaxFileSize(String maxFileSize) {
		this.maxFileSize = maxFileSize;
	}


	public String getAspectRatio() {
		return aspectRatio;
	}


	public void setAspectRatio(String aspectRatio) {
		this.aspectRatio = aspectRatio;
	}


	public String getFileTypes() {
		return fileTypes;
	}


	public void setFileTypes(String fileTypes) {
		this.fileTypes = fileTypes;
	};
	
	
	public String getMaxAlbumImages() {
		return maxAlbumImages;
	}


	public void setMaxAlbumImages(String maxAlbumImages) {
		this.maxAlbumImages = maxAlbumImages;
	}

	public Long getMaxAlbumImagesCount() {
		long count = Flinnt.INVALID;
		try {
			count = Integer.parseInt(maxAlbumImages);
		} catch (Exception e) {
		}
		return count;
	}
	
	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("minWidth : " + minWidth)
		.append(", minHeight : " + minHeight)
		.append(", scaleMaxWidth : " + scaleMaxWidth)
		.append(", scaleMaxHeight : " + scaleMaxHeight)
		.append(", bitrate : " + bitrate)
		.append(", maxFileSize : " + maxFileSize)
		.append(", aspectRatio : " + aspectRatio)
		.append(", fileTypes : " + fileTypes)
		.append(", maxAlbumImages : " + maxAlbumImages);
		return strBuffer.toString();
	}


}
