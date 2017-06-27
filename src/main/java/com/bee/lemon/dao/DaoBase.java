package com.bee.lemon.dao;

import org.springframework.stereotype.Repository;

/**
 * @author weiwei1
 *
 */
@Repository
public class DaoBase {

	protected int pageSize = 20;

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

}
