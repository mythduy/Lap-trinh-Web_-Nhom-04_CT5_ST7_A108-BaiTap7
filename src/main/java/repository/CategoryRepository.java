package repository;

import entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface CategoryRepository extends JpaRepository<Category, Integer> {

	List<Category> findByCategoryNameContaining(String name);

	Page<Category> findByCategoryNameContaining(String name, Pageable pageable);
	
	List<Category> findByCategoryNameContainingIgnoreCase(String name);

}