package com.solovev;

import com.solovev.dao.SessionFactorySingleton;
import com.solovev.model.Card;
import com.solovev.model.Category;
import com.solovev.model.User;

import java.util.List;

public class Main {
    /*
     * Todo:
     *  4. filter acting weird when f5 with debugger
     */
    public static void main(String[] args) {
        SessionFactorySingleton.getInstance();
    }
}