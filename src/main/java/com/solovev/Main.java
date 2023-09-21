package com.solovev;

import com.solovev.model.Card;
import com.solovev.model.Category;
import com.solovev.model.User;

public class Main {
    /*
    * todo
    *   1. how not to force nullable
    *   2. why ID not final?
    *   3. Why use two side connections?
    *   4. noArgs constructor with NonNulls?
    * */
    public static void main(String[] args) {
        System.out.println(new User());
        System.out.println(new Category());
        System.out.println(new Card());

    }
}