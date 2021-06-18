package me.springboot.fwk.sys.dao.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.springboot.fwk.sys.vo.SysAddressVO;

@Repository
public interface SysAddressJpa extends JpaRepository<SysAddressVO, java.lang.String> {

	public List<SysAddressVO> findBycParent(String parent);
}
