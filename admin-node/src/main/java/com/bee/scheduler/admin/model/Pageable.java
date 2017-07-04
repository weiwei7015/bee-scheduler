package com.bee.scheduler.admin.model;

import java.util.List;

/**
 * @author weiwei 分页支持类
 */
public class Pageable<T> {
    private int page;
    private int pageSize;
    private int resultTotal;
    private List<T> result;

    public Pageable(int page, int pageSize, int resultTotal, List<T> result) {
        this.page = page;
        this.pageSize = pageSize;
        this.resultTotal = resultTotal;
        this.result = result;
    }

    public Pageable(int page, int resultTotal, List<T> result) {
        this(page, 20, resultTotal, result);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void refresh() {

    }

    public int getPageTotal() {
        if (resultTotal % pageSize == 0) {
            return resultTotal / pageSize;
        } else {
            return resultTotal / pageSize + 1;
        }
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getResultTotal() {
        return resultTotal;
    }

    public void setResultTotal(int resultTotal) {
        this.resultTotal = resultTotal;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

}
