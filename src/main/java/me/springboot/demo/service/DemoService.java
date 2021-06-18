package me.springboot.demo.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import me.springboot.demo.dao.DemoRepository;
import me.springboot.demo.vo.DemoVO;
import me.springboot.fwk.db.DbUtils;
import me.springboot.fwk.utils.BeanUtils;

@Slf4j
@Service
public class DemoService {

	@Autowired
	private DemoRepository demoRepository;
	@Autowired
	private DbUtils dbUtils;
	
	@Cacheable(value = "cacheable_DemoVO",key="#id")
    public DemoVO get(int id) throws Exception {
		Map<String,Object> map = dbUtils.querySql("select * from t_demo where id=?", id).get(0);
		DemoVO demo= new DemoVO();
		BeanUtils.map2Bean(map, demo);;
		log.info("从数据库读取得到");
		return demo;
    }

    @CachePut(value = "cacheable_DemoVO",key="#bean.id")
    public DemoVO saveOrUpdate(DemoVO bean) {
        this.demoRepository.save(bean);
    	return bean;
    }
    
    @CacheEvict(value = "cacheable_DemoVO", key = "#id")
    public void delete(Long id) {
        this.demoRepository.deleteById(1);
    }
}
