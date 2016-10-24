package com.edam.query;

import com.edam.util.Util;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.Map;

public class QueryCondition {
	private Map<String, String> mConditions;
	// Offset start from 0
	private int mOffset;

	public QueryCondition() {
		mConditions = new HashMap<String, String>();
	}

	public QueryCondition(QueryCondition queryCondition) {
		mConditions = new HashMap<String, String>();
		Map<String, String> maps = queryCondition.getConditions();

		mOffset = queryCondition.getOffset();
		for (String value : maps.keySet()) {
			mConditions.put(value, maps.get(value));
		}
	}

	public QueryCondition push(String conditionName, String conditionValue) {
		mConditions.put(conditionName, conditionValue);
		return this;
	}

	public void setQuery(ParseQuery<?> query) {
		query.setLimit(Util.QUERY_LIMIT);
		query.setSkip(Util.QUERY_LIMIT * mOffset);
		for (String value : mConditions.keySet()) {
			if (value.equals(Util.DB_TITLE)) {
				query.whereContains(Util.DB_TITLE, mConditions.get(value));
			} else if (value.equals(Util.DB_CITY)) {
				// No options like "Whole Taiwan" in parse DB
				if (!mConditions.get(value).equals(Util.FILTER_CITY.get(0))) {
					query.whereContains(Util.DB_CITY, mConditions.get(value));
				}
			} else if (value.equals(Util.DB_START_DATE)) {
				query.whereGreaterThanOrEqualTo(Util.DB_END_DATE, mConditions.get(value));
			} else if (value.equals(Util.DB_END_DATE)) {
				query.whereLessThanOrEqualTo(Util.DB_START_DATE, mConditions.get(value));
			}
		}
//		query.whereLessThanOrEqualTo(Util.DB_UPDATEDAT, Util.UPDATE);
		query.whereGreaterThanOrEqualTo(Util.DB_CREATEDAT, Util.UPDATE);
	}

	// =============================================================================
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof QueryCondition)) {
			return false;
		}
		Map<String, String> maps = ((QueryCondition) other).getConditions();
		if (mConditions.size() != maps.size()) {
			return false;
		}
		return mConditions.equals(maps);
	}

	@Override
	public String toString() {
		return mConditions.toString();
	}

	// =============================================================================
	public int getOffset() {
		return mOffset;
	}

	public QueryCondition setOffset(int offset) {
		mOffset = offset;
		return this;
	}

	public Map<String, String> getConditions() {
		return mConditions;
	}
}
