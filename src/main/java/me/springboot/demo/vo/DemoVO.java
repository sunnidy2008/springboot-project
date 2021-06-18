package me.springboot.demo.vo;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.data.annotation.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Levin
 * @since 2018/5/7 0007
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "t_demo")
public class DemoVO implements Serializable {

    private static final long serialVersionUID = 8655851615465363473L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String password;
    /**
     * TODO 忽略该字段的映射
     */
    @Transient
    private String  email;

    // TODO  省略get set
}