package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.exceptions.CustomException;
import com.nineleaps.leaps.exceptions.ProductExistInWishlist;
import com.nineleaps.leaps.model.products.Product;
import com.nineleaps.leaps.model.Wishlist;
import com.nineleaps.leaps.repository.WishlistRepository;
import com.nineleaps.leaps.service.WishlistServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistService implements WishlistServiceInterface {
    private final WishlistRepository wishlistRepository;

    @Autowired
    public WishlistService(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    @Override
    public void createWishlist(Wishlist wishlist) throws ProductExistInWishlist {
        //check if the product is already present in the wishlist
        List<Wishlist> body = readWishlist(wishlist.getUser().getId());
        for (Wishlist itrWishlist : body) {
            if (wishlist.getProduct().getId().equals(itrWishlist.getProduct().getId())) {
                throw new ProductExistInWishlist("Product already exists in wishlist");
            }
        }
        wishlistRepository.save(wishlist);
    }

    @Override
    public List<Wishlist> readWishlist(Long userId) {
        return wishlistRepository.findAllByUserIdOrderByCreateDateDesc(userId);
    }

    @Override
    public void removeFromWishlist(Long userId, Product product) throws CustomException {
        Wishlist wishlist = wishlistRepository.findByUserIdAndProductId(userId, product.getId());
        if (!Helper.notNull(wishlist)) {
            throw new CustomException("Item not found");
        }
        wishlistRepository.deleteById(wishlist.getId());
    }


}
