package com.howe.learn.spider.basic.domain;
import com.google.gson.GsonBuilder;

/**
 * 行程单类
 */
public class Route {
	
	//主键
	private Long routeId;
	
	//编号
	private String routeNo;
	
	//行程单名
	private String routeName;

	//行程描述
	private String descr;
	
	//行程价格
	private Double price;
	
	//爬取的网络路径
	private String sourceUrl;

	public Long getRouteId() {
		return routeId;
	}

	public void setRouteId(Long routeId) {
		this.routeId = routeId;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
	
	public void setPrice(Double price) {
		this.price = price;
	}
	
	public Double getPrice() {
		return price;
	}
	
	

	public String getRouteNo() {
		return routeNo;
	}

	public void setRouteNo(String routeNo) {
		this.routeNo = routeNo;
	}

	public String toString() {
		return new GsonBuilder().setPrettyPrinting().create().toJson(this);
	}
}
