package com.nidas.recipesapp.dto;

import com.nidas.recipesapp.model.Ingredient;
import com.nidas.recipesapp.model.Step;
import jakarta.persistence.ElementCollection;
import lombok.Data;

import java.util.List;

@Data
public class RecipeDto {

    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private int nbLike;
    private int nbFavourite;
    @ElementCollection
    private List<Ingredient> ingredients;

    @ElementCollection
    private List<Step> steps;


}


