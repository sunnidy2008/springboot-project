package me.springboot.demo.dao;

import java.util.List;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.springboot.demo.vo.DemoVO;

@Repository
public interface DemoRepository extends JpaRepository<DemoVO, Integer> {

	public List<DemoVO> findByUsername(String username);
}
