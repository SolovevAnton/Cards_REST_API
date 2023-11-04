package com.solovev;

import com.solovev.dao.SessionFactorySingleton;
import com.solovev.model.Card;
import com.solovev.model.Category;
import com.solovev.model.User;

import java.util.List;

public class Main {
    /*
     * Todo:
     *  2. how to send Passwords in URL? or not?
     *  3. без очистки куков на сервере не работает фильтр
     * 4. filter to one user interact with others tables
     */
    public static void main(String[] args) {
        SessionFactorySingleton.getInstance();
    }
}