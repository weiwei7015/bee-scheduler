package com.bee.scheduler.consolenode.model;

import java.util.List;

/**
 * @author weiwei 分页支持类
 */
public class Pageable<T> {
    private Integer page;
    private Integer pageSize;
    private Integer resultTotal;
    private List<T> result;

    public Pageable(Integer page, Integer pageSize, Integer resultTotal, List<T> result) {
        this.page = page;
        this.pageSize = pageSize;
        this.resultTotal = resultTotal;
        this.result = result;
    }

    public Pageable(Integer page, Integer resultTotal, List<T> result) {
        this(page, 20, resultTotal, result);
    }


    public Integer getPageTotal() {
        if (resultTotal % pageSize == 0) {
            return resultTotal / pageSize;
        } else {
            return resultTotal / pageSize + 1;
        }
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getResultTotal() {
        return resultTotal;
    }

    public void setResultTotal(Integer resultTotal) {
        this.resultTotal = resultTotal;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }
}
