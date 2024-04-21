package com.solovev.service;

import com.solovev.model.Card;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Transactional
public interface CardService {
    Card getById(long id);
    Collection<Card> getCardsByCategoryId(long catogoryId);
    Collection<Card> getCardsByUserId(long userId);
    Card addCard(long categoryId,Card card);
    Card deleteById(long cardId);
    Card update(Card card);
}
