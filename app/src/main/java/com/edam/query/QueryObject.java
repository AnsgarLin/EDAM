package com.edam.query;

import com.edam.util.Util;
import com.parse.ParseObject;

public class QueryObject extends ParseObject {
	public String getId() {
		return getString(Util.DB_ID);
	}

	public void setId(String value) {
		put(Util.DB_ID, value);
	}

	public String getTitle() {
		return getString(Util.DB_TITLE);
	}

	public void setTitle(String value) {
		put(Util.DB_TITLE, value);
	}

	public String getBrief() {
		return getString(Util.DB_BRIEF);
	}

	public void setBrief(String value) {
		put(Util.DB_BRIEF, value);
	}

	public String getActURL() {
		return getString(Util.DB_ACT_URL);
	}

	public void setActURL(String value) {
		put(Util.DB_ACT_URL, value);
	}

	public String getIsFree() {
		return getString(Util.DB_IS_FREE);
	}

	public void setIsFree(String value) {
		put(Util.DB_IS_FREE, value);
	}

	public String getTicketURL() {
		return getString(Util.DB_TICKET_URL);
	}

	public void setTicketURL(String value) {
		put(Util.DB_TICKET_URL, value);
	}

	public String getStartDate() {
		return getString(Util.DB_START_DATE);
	}

	public void setStartDate(String value) {
		put(Util.DB_START_DATE, value);
	}

	public String getEndDate() {
		return getString(Util.DB_END_DATE);
	}

	public void setEndDate(String value) {
		put(Util.DB_END_DATE, value);
	}

	public String getStartTime() {
		return getString(Util.DB_START_TIME);
	}

	public void setStartTime(String value) {
		put(Util.DB_START_TIME, value);
	}

	public String getEndTime() {
		return getString(Util.DB_END_TIME);
	}

	public void setEndTime(String value) {
		put(Util.DB_END_TIME, value);
	}

	public String getCity() {
		return getString(Util.DB_CITY);
	}

	public void setCity(String value) {
		put(Util.DB_CITY, value);
	}

	public String getPlace() {
		return getString(Util.DB_PLACE);
	}

	public void setPlace(String value) {
		put(Util.DB_PLACE, value);
	}

	public String getAddress() {
		return getString(Util.DB_ADDRESS);
	}

	public void setAddress(String value) {
		put(Util.DB_ADDRESS, value);
	}

	public String getCoverImgURL() {
		return getString(Util.DB_COVER_IMG_URL);
	}

	public void setCoverImgURL(String value) {
		put(Util.DB_COVER_IMG_URL, value);
	}

	public String getImgURL() {
		return getString(Util.DB_IMG_URL);
	}

	public void setImgURL(String value) {
		put(Util.DB_IMG_URL, value);
	}

	@Override
	public String toString() {
		return "id=" + getId() + ", title=" + getTitle() + ", brief=" + getBrief() + ", act_url=" + getActURL() + ", is_free=" + getIsFree()
				+ ", ticket=" + getTicketURL() + ", start_date=" + getStartDate() + ", end_date=" + getEndDate() + ", start_time=" + getStartTime()
				+ ", end_time=" + getEndTime() + ", city=" + getCity() + ", place=" + getPlace() + ", address=" + getAddress() + ", cover_image_url="
				+ getCoverImgURL() + ", image_url=" + getImgURL();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof QueryObject)) {
			return false;
		}

		return toString().equals(other.toString());
	}

}
