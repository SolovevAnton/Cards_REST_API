package com.solovev.repository;

import com.solovev.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface CardRepository extends JpaRepository<Card,Long> {
    Collection<Card> findAllByCategoryId(long id);
}
