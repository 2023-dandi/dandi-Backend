package dandi.dandi.clothes.application.service;

import dandi.dandi.clothes.application.port.in.ClothesRegisterCommand;
import dandi.dandi.clothes.application.port.in.ClothesUseCase;
import dandi.dandi.clothes.application.port.out.persistence.ClothesPersistencePort;
import dandi.dandi.clothes.domain.Clothes;
import dandi.dandi.common.exception.ForbiddenException;
import dandi.dandi.common.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClothesService implements ClothesUseCase {

    private final ClothesPersistencePort clothesPersistencePort;
    private final ClothesImageService clothesImageService;

    public ClothesService(ClothesPersistencePort clothesPersistencePort, ClothesImageService clothesImageService) {
        this.clothesPersistencePort = clothesPersistencePort;
        this.clothesImageService = clothesImageService;
    }

    @Override
    @Transactional
    public void registerClothes(Long memberId, ClothesRegisterCommand clothesRegisterCommand) {
        Clothes clothes = clothesRegisterCommand.toClothes(memberId);
        clothesPersistencePort.save(clothes);
    }

    @Override
    @Transactional
    public void deleteClothes(Long memberId, Long clothesId) {
        Clothes clothes = clothesPersistencePort.findById(clothesId)
                .orElseThrow(NotFoundException::clothes);
        validateOwner(clothes, memberId);
        clothesPersistencePort.deleteById(clothesId);
        clothesImageService.deleteClothesImage(clothes);
    }

    private void validateOwner(Clothes clothes, Long memberId) {
        if (!clothes.isOwnedBy(memberId)) {
            throw ForbiddenException.clothesDeletion();
        }
    }
}
