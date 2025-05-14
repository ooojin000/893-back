package com.samyookgoo.palgoosam.auction.repository;

import com.samyookgoo.palgoosam.auction.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
