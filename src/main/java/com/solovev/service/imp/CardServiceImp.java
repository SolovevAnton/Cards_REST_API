package com.solovev.service.imp;

import com.solovev.model.Card;
import com.solovev.repository.CardRepository;
import com.solovev.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
@Service
@RequiredArgsConstructor
public class CardServiceImp implements CardService {
    private final CardRepository repository;
    @Override
    public Collection<Card> getCardsByCategoryId(long catogoryId) {
        return repository.findAllByCategoryId(catogoryId);
    }
}
