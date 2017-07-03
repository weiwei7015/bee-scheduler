package com.bee.scheduler.model;

import java.io.Serializable;
import java.util.Date;

public class UserLoginSession implements Serializable {

	private static final long serialVersionUID = -8172879662124856674L;
	
	private Integer shopId;// 店铺id
	
	private String shopSn;// 商家编号
	
	private Integer sellerId;// 商家id
	
	private String name;// 店铺名称
	
	private Integer type;// 店铺类型
	
	private String account;// 店铺账号
	
	private Date loginTime;// 登录时间
	
	private Integer uid;// 兼容用户id
	
	private String uname;// 兼容用户名称

	public Integer getShopId() {
		return shopId;
	}

	public void setShopId(Integer shopId) {
		this.shopId = shopId;
	}

	public String getShopSn() {
		return shopSn;
	}

	public void setShopSn(String shopSn) {
		this.shopSn = shopSn;
	}

	public Integer getSellerId() {
		return sellerId;
	}

	public void setSellerId(Integer sellerId) {
		this.sellerId = sellerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public Integer getUid() {
		return shopId;
	}

	public String getUname() {
		return name;
	}

}
