package dandi.dandi.clothes.application.port.out.persistence;

import dandi.dandi.clothes.domain.Clothes;

public interface ClothesPersistencePort {

    void save(Clothes clothes, Long memberId);
}