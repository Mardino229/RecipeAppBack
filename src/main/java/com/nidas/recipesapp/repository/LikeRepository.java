package com.nidas.recipesapp.repository;

import com.nidas.recipesapp.model.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Likes, Long> {

        boolean existsByChiefIdAndRecipeId(Long chiefId, Long recipeId);
        long countByChiefIdAndRecipeId(Long chiefId, Long recipeId);
        Likes findByChiefIdAndRecipeId(Long chiefId, Long recipeId);
        void deleteAllByRecipe_Id(Long id);
        void deleteByRecipe_Id(Long id);
}
