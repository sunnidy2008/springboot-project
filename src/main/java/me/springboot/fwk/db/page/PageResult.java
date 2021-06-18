package me.springboot.fwk.db.page;

import java.util.List;

import lombok.Data;

/**
 * Created by Administrator on 2017/10/11 0011.
 */
@Data
public class PageResult {
    private List resultList;
	private int total;
    private int pages;

}
