package com.edu.flinnt.protocol;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONObject;

/**
 * Template response object class
 */
public class Template {

	public static final String POST_TEMPLATE_ID_KEY						= "post_template_id";
	public static final String POST_CATEGORY_ID_KEY						= "post_category_id";
	public static final String POST_TEMPLATE_CATEGORY_KEY				= "post_template_category";
	public static final String POST_TEMPLATE_NAME_KEY					= "post_template_name";
	public static final String POST_TEMPLATE_TITLE_KEY					= "post_template_title";
	public static final String POST_TEMPLATE_TAGS_KEY					= "post_template_tags";
	public static final String POST_TEMPLATE_DESCRIPTION_KEY			= "post_template_description";
	
	public String templateId 									= "";
	public String categoryId 									= "";
	public String templateCategory 								= "";
	public String templateName 									= "";
	public String templateTitle 								= "";
	public String templateTags 									= "";
	public String templateDescription 							= "";
	
	public int isHeader = Flinnt.INVALID;
	
	public int getIsHeader() {
		return isHeader;
	}

	public void setIsHeader(int isHeader) {
		this.isHeader = isHeader;
	}

    /**
     * parse json object to suitable data types
     * @param jObject json object
     */
    public synchronized void parseJSONObject(JSONObject jObject) {

		try {
			setTemplateId( jObject.getString(POST_TEMPLATE_ID_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		try {
			setCategoryId( jObject.getString(POST_CATEGORY_ID_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		try {
			setTemplateCategory( jObject.getString(POST_TEMPLATE_CATEGORY_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		try {
			setTemplateName( jObject.getString(POST_TEMPLATE_NAME_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		try {
			setTemplateTitle( jObject.getString(POST_TEMPLATE_TITLE_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		try {
			setTemplateTags( jObject.getString(POST_TEMPLATE_TAGS_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}
		try {
			setTemplateDescription( jObject.getString(POST_TEMPLATE_DESCRIPTION_KEY) );
		}
		catch(Exception e){
			LogWriter.err(e);
		}

	}
	
	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String TemplateId) {
		this.templateId = TemplateId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String CategoryId) {
		this.categoryId = CategoryId;
	}

	public String getTemplateCategory() {
		return templateCategory;
	}

	public void setTemplateCategory(String TemplateCategory) {
		this.templateCategory = TemplateCategory;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String TemplateName) {
		this.templateName = TemplateName;
	}

	public String getTemplateTitle() {
		return templateTitle;
	}

	public void setTemplateTitle(String TemplateTitle) {
		this.templateTitle = TemplateTitle;
	}

	public String getTemplateTags() {
		return templateTags;
	}

	public void setTemplateTags(String TemplateTags) {
		this.templateTags = TemplateTags;
	}

	public String getTemplateDescription() {
		return templateDescription;
	}

	public void setTemplateDescription(String TemplateDescription) {
		this.templateDescription = TemplateDescription;
	}

	@Override
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("templateId : " + templateId)
		.append(", categoryId : " + categoryId)
		.append(", templateCategory : " + templateCategory)
		.append(", templateName : " + templateName)
		.append(", templateTitle : " + templateTitle)
		.append(", templateTags : " + templateTags)
		.append(", templateDescription : " + templateDescription);
		return strBuffer.toString();
	}
}
