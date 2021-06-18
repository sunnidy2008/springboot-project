package ${package};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ${jpa_package};
import lombok.Getter;
import lombok.Setter;
import me.springboot.fwk.db.DbUtils;
@Repository
@Getter
@Setter
public class ${class_name}{

	@Autowired
	private ${jpa_name} ${jpa_variable_name};
	@Autowired
	private DbUtils dbUtils;
}
