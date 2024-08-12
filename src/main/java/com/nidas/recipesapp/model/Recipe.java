package com.nidas.recipesapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String imageUrl;

    @ElementCollection
    private List<Ingredient> ingredients;

    @ElementCollection
    private List<Step> steps;

    @ManyToOne
    @JoinColumn(name = "chief_id")
    private Chief chief;

    @ManyToMany(mappedBy = "recipeLiked", cascade = CascadeType.ALL)
    private Set<Chief> chiefLike;

    @ManyToMany(mappedBy = "recipeFavourites", cascade = CascadeType.ALL)
    private Set<Chief> chiefFavorites;

    private int nbLike = 0;

    private int nbFavourite = 0;

    public void liker (Boolean bool){
        if (bool){
            nbLike-=1;
        } else {
            nbLike+=1;
        }
    }
    public void favourite (Boolean bool){
        if (bool){
            nbFavourite-=1;
        } else {
            nbFavourite+=1;
        }
    }

    public int nbLike(){
        return chiefLike.size();
    }
    public int nbFavourite(){
        return chiefFavorites.size();
    }

    public void update (Recipe recipe) {
        this.title = recipe.getTitle();
        this.description = recipe.getDescription();
        if (recipe.getImageUrl() != null) {
            this.imageUrl = recipe.getImageUrl();
        }
        this.ingredients = recipe.getIngredients();
        this.steps = recipe.getSteps();
    }

}

