package com.mingdao.sdk;

import android.text.TextUtils;

/**
 * Mason Liu 天盟
 */
public class MDFile {

	private byte[] data;

	private String filename;

	private String formname;

	private String contentType = "application/octet-stream";
	
	public MDFile(String tag, byte[] data,String filename ) {
		this.data = data;
		if(TextUtils.isEmpty(filename)){
			this.filename="1.png";
		}else
			this.filename = filename;
		this.formname = tag;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFormname() {
		return formname;
	}

	public void setFormname(String formname) {
		this.formname = formname;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
}