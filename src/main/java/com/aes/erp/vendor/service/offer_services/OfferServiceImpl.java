package com.aes.erp.vendor.service.offer_services;

import com.aes.erp.scm.Entities.PriceQuotation;
import com.aes.erp.scm.repositories.OfferItemRepository;
import com.aes.erp.scm.repositories.PriceQuotationRepository;
import com.aes.erp.vendor.dto.OfferCreateDTO;
import com.aes.erp.vendor.entity.RFQ_Negotiation.Offer;
import com.aes.erp.vendor.entity.RFQ_Negotiation.OfferItem;
import com.aes.erp.vendor.utils.GenericModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OfferServiceImpl implements OfferService{

    private final GenericModelMapper genericModelMapper;
    private final PriceQuotationRepository priceQuotationRepository;
    private final OfferItemRepository offerItemRepository;

    public OfferServiceImpl(GenericModelMapper genericModelMapper, PriceQuotationRepository priceQuotationRepository, OfferItemRepository offerItemRepository) {
        this.genericModelMapper = genericModelMapper;
        this.priceQuotationRepository = priceQuotationRepository;
        this.offerItemRepository = offerItemRepository;
    }

    @Override
    public void createInitialOffer(OfferCreateDTO createDTO, String tenderId) {
        Offer offer = genericModelMapper.map(createDTO, Offer.class);
        List<OfferItem> savedItems = new ArrayList<>();
        for(OfferItem item : offer.getOfferItems()){
            PriceQuotation priceQuotation = item.getPriceQuotation();
            priceQuotation = priceQuotationRepository.save(priceQuotation);
            item.setPriceQuotation(priceQuotation);
            item = offerItemRepository.save(item);
            savedItems.add(item);
        }
//        if()
    }
}
