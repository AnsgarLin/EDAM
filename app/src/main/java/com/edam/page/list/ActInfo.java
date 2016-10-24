package com.edam.page.list;

import java.text.ParseException;
import java.util.Calendar;

import com.edam.query.QueryObject;
import com.edam.util.MyUtils;

public class ActInfo {
	/**
	 * Naming should be the same as the original database, or the Gson won't work.
	 */
	private String mId;
	private String mTitle;
	private String mBrief;
	private String mActURL;
	private String mIsFree;
	private String mTicketURL;
	private String mStartDate;
	private String mEndDate;
	private String mStartTime;
	private String mEndTime;
	private String mCity;
	private String mPlace;
	private String mAddress;
	private String mCoverImgURL;
	private String mImgURL;
	private String mCountDown;

	/**
	 * Record the animation has been triggered or not
	 */
	private boolean mBeenShow;

	public ActInfo() {}

	public ActInfo(String id) {
		mId = id;
	}

	public ActInfo(QueryObject queryObject) {
		try {
			mId = queryObject.getId();
			mTitle = queryObject.getTitle();
			mBrief = queryObject.getBrief();
			mActURL = queryObject.getActURL();
			mIsFree = queryObject.getIsFree();
			mTicketURL = queryObject.getTicketURL();
			mStartDate = queryObject.getStartDate();
			mEndDate = queryObject.getEndDate();
			mStartTime = queryObject.getStartTime();
			mEndTime = queryObject.getEndTime();
			mCity = queryObject.getCity();
			mPlace = queryObject.getPlace();
			mAddress = queryObject.getAddress();
			mCoverImgURL = queryObject.getCoverImgURL();
			mImgURL = queryObject.getImgURL();
			mCountDown = String.valueOf(MyUtils.DateUtil.getDateDuration(Calendar.getInstance().getTime(),
					MyUtils.DateUtil.getDateByFormat(mEndDate, "yyyy-MM-dd")));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		this.mId = id;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		this.mTitle = title;
	}

	public String getBrief() {
		return mBrief;
	}

	public void setBrief(String brief) {
		this.mBrief = brief;
	}

	public String getActURL() {
		return mActURL;
	}

	public void setActURL(String actURL) {
		this.mActURL = actURL;
	}

	public String getIsFree() {
		return mIsFree;
	}

	public void setIsFree(String isFree) {
		this.mIsFree = isFree;
	}

	public String getTicketURL() {
		return mTicketURL;
	}

	public void setTicketURL(String ticketURL) {
		this.mTicketURL = ticketURL;
	}

	public String getStartDate() {
		return mStartDate;
	}

	public void setStartDate(String startDate) {
		this.mStartDate = startDate;
	}

	public String getEndDate() {
		return mEndDate;
	}

	public void setEndDate(String endDate) {
		this.mEndDate = endDate;
	}

	public String getStartTime() {
		return mStartTime;
	}

	public void setStartTime(String startTime) {
		this.mStartTime = startTime;
	}

	public String getEndTime() {
		return mEndTime;
	}

	public void setEndTime(String endTime) {
		this.mEndTime = endTime;
	}

	public String getCity() {
		return mCity;
	}

	public void setCity(String city) {
		this.mCity = city;
	}

	public String getPlace() {
		return mPlace;
	}

	public void setPlace(String place) {
		this.mPlace = place;
	}

	public String getAddress() {
		return mAddress;
	}

	public void setAddress(String address) {
		this.mAddress = address;
	}

	public String getCoverImgURL() {
		return mCoverImgURL;
	}

	public void setCoverImgURL(String coverImgURL) {
		this.mCoverImgURL = coverImgURL;
	}

	public String getImgURL() {
		return mImgURL;
	}

	public void setImgURL(String imgURL) {
		this.mImgURL = imgURL;
	}

	public String getCountDown() {
		return mCountDown;
	}

	public void setCountDown(String countDown) {
		this.mCountDown = countDown;
	}

	@Override
	public String toString() {
		return "ActivityJson [id=" + mId + ", title" + mTitle + ", brief=" + mBrief + ", start=" + mStartDate + ", end=" + mEndDate +
				", startTime=" + mStartTime + ", endTime=" + mEndTime + ", city=" + mCity + ", place=" + mPlace + ", address=" + mAddress +
				", coverImgURL=" + mCoverImgURL + ", ImgURL=" + mImgURL + ", countDown=" + mCountDown + "]";
	}

	public boolean isBeenShow() {
		return mBeenShow;
	}

	public void setBeenShow(boolean beenShow) {
		mBeenShow = beenShow;
	}
}
