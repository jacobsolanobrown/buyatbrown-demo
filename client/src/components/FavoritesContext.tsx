import React, { createContext, useContext, useState } from 'react';

const FavoritesContext = createContext();

export function useFavorites() {
  return useContext(FavoritesContext);
}

export function FavoritesProvider({ children }) {
  const [likedListings, setLikedListings] = useState([]);

  const toggleFavorite = (listing) => {
    setLikedListings((prev) => {
      const isLiked = prev.some((l) => l.listingId === listing.listingId);
      if (isLiked) {
        return prev.filter((l) => l.listingId !== listing.listingId);
      } else {
        return [...prev, listing];
      }
    });
  };

  const isFavorited = (listingId) => {
    return likedListings.some((l) => l.listingId === listingId);
  };

  const value = {
    likedListings,
    toggleFavorite,
    isFavorited,
  };

  return (
    <FavoritesContext.Provider value={value}>
      {children}
    </FavoritesContext.Provider>
  );
}
