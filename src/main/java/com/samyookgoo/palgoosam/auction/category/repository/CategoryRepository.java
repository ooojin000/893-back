package com.samyookgoo.palgoosam.auction.category.repository;

import com.samyookgoo.palgoosam.auction.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
