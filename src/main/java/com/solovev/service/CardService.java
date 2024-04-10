package com.solovev.service;

import com.solovev.model.Card;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Transactional
public interface CardService {
    Collection<Card> getCardsByCategoryId(long catogoryId);
}
