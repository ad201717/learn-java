package com.howe.learn.spider.basic.domain;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.GsonBuilder;

public class PageResult {

	// 页下标
	private int pageIndex;

	// 总记录数
	private int totalRecords;

	// 每页记录数
	private int recordsPerpage = 10;

	private List<Object> list = new ArrayList<Object>();

	public PageResult() {

	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	public int getRecordsPerpage() {
		return recordsPerpage;
	}

	public void setRecordsPerpage(int recordsPerpage) {
		this.recordsPerpage = recordsPerpage;
	}

	public List<Object> getList() {
		return list;
	}

	public void setList(List<Object> list) {
		this.list = list;
	}

	/**
	 * 总页数
	 * @return
	 */
	public int getTotalPages() {
		if (totalRecords % recordsPerpage == 0) {
			return totalRecords / recordsPerpage;
		} else {
			return totalRecords / recordsPerpage + 1;
		}
	}

	public String toString() {
		return new GsonBuilder().setPrettyPrinting().create().toJson(this);
	}
}
