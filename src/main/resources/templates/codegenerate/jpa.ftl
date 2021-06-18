package ${package};

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ${vo_package};

@Repository
public interface ${class_name} extends JpaRepository<${vo_name}, ${primary_key_data_type}> {

}
