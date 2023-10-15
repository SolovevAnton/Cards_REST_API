package com.solovev;

import com.solovev.model.Card;
import com.solovev.model.Category;
import com.solovev.model.User;

import java.util.List;

public class DataConstants {
    public static final List<User> USERS = List.of(
            new User(1, "firstLog", "firstPass", "first"),
            new User(2, "secondLog", "secondPass", "second"),
            new User(3, "thirdLog", "thirdPass", "third")
    );
    public static final List<Category> CATEGORIES = List.of(
            new Category(1, "firstCat", USERS.get(0)),
            new Category(2, "secondCat", USERS.get(0)),
            new Category(3, "thirdCat", USERS.get(1))
    );
    public static final List<Card> CARDS = List.of(
            new Card(1,"Q1", "A1", CATEGORIES.get(0)),
            new Card(2,"Q2", "A2", CATEGORIES.get(0)),
            new Card(3,"Q3", "A3", CATEGORIES.get(1))
    );
}
