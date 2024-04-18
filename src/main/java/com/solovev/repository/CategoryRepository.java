package com.solovev.repository;

import com.solovev.model.Category;
import com.solovev.model.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    Collection<Category> findCategoriesByUserId(long user_id);

}
