package com.nidas.recipesapp.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChiefDto {

    private String email;
    private String pseudo;

    private List<RecipeDto> recipes;

}
