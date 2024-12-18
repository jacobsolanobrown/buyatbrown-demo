import React, { useEffect } from "react";
import { FaHeart, FaRegHeart } from "react-icons/fa";
import { useState } from "react";
import { PulseLoader } from "react-spinners";
import {
  addToFavorites,
  getUser,
  getUserFavorites,
  removeFromFavorites,
} from "../utils/api";
import { useUser } from "@clerk/clerk-react";

interface ModalCardProps {
  isOpen: boolean;
  onClose: () => void;
  listing?: {
    imageUrl: string;
    title: string;
    price: string;
    username: string;
    description: string;
    condition: string;
    category: string;
    tags: string;
    listingId: string;
    email: string;
  };
}

// This listing modal represents the bigger card when the listing card is clicked
const ListingModal: React.FC<ModalCardProps> = ({
  isOpen,
  onClose,
  listing,
}) => {
  if (!isOpen || !listing) return null;

  const { user } = useUser();
  const [isFavorited, setIsFavorited] = useState(false);
  const [showEmailPopup, setShowEmailPopup] = useState(false);
  const [showFavoriteMessage, setShowFavoriteMessage] = useState(false);
  const [showRemovedMessage, setShowRemovedMessage] = useState(false);
  const [favoritingLoading, setFavoritingLoading] = useState(false);

  // Check if the listing is already in the user's favorites
  useEffect(() => {
    const fetchFavoriteStatus = async () => {
      if (user) {
        getUserFavorites(user.id).then((data) => {
          if (
            data.response_type === "success" &&
            Array.isArray(data.listings)
          ) {
            const favoriteListings = data.listings;
            console.log("favorite listings", favoriteListings);
            console.log("listing id", listing.listingId);
            // test here
            const isFavorite = favoriteListings.some(
              (fav: any) => fav.listingId === listing.listingId
            );

            if (isFavorite) {
              console.log("SHOULD BE FAVORITED");
              setIsFavorited(true);
            }
            console.log("favorite status", isFavorited);
          }
        });
      }
    };
    fetchFavoriteStatus();
  }, [listing.listingId]);

  // Handle favorite toggle
  const handleFavoriteClick = async () => {
    const newFavoriteStatus = !isFavorited;
    setIsFavorited(newFavoriteStatus);
    if (user) {
      setFavoritingLoading(true);
      if (isFavorited) {
        // Remove from favorites
        removeFromFavorites(user.id, listing.listingId)
          .then((data) => {
            if (data.response_type === "success") {
              setIsFavorited(false);
              setShowRemovedMessage(true);
              setTimeout(() => {
                // show temporary message for 3 seconds
                setShowRemovedMessage(false);
              }, 3000);
            } else {
              setIsFavorited(true);
              setShowFavoriteMessage(true);
              setTimeout(() => {
                // show temporary message for 3 seconds
                setShowFavoriteMessage(false);
              }, 3000);
            }
          })
          .catch((error) => {
            console.error("Error removing from favorites: ", error);
          })
          .finally(() => {
            setFavoritingLoading(false);
          });
      } else {
        addToFavorites(user.id, listing.listingId)
          .then((data) => {
            if (data.response_type === "success") {
              setIsFavorited(true);
              setShowFavoriteMessage(true);
              setTimeout(() => {
                // show temporary message for 5 seconds
                setShowFavoriteMessage(false);
              }, 3000);
            } else {
              console.error("Failed to add to favorites: ", data.error);
            }
          })
          .catch((error) => {
            console.error("Error adding to favorites: ", error);
          })
          .finally(() => {
            setFavoritingLoading(false);
          });
      }
    }
  };

  const handleEmailSellerClick = () => {
    setShowEmailPopup(!showEmailPopup);
  };

  // Close the modal if the user clicks outside of it
  const handleOutsideClick = (e: React.MouseEvent<HTMLDivElement>) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  useEffect(() => {
    const handleEsc = (event: KeyboardEvent) => {
      if (event.key === "Escape") {
        onClose();
      }
    };
    window.addEventListener("keydown", handleEsc);
    return () => window.removeEventListener("keydown", handleEsc);
  }, [onClose]);

  return (
    <div
      className="flex fixed inset-0 bg-black bg-opacity-50 justify-center items-start py-16 z-50 overflow-auto"
      onClick={handleOutsideClick}
      role="dialog"
      aria-modal="true"
    >
      <div className="bg-white p-8 rounded-lg shadow-lg max-w-3xl w-full max-h-[calc(100vh-8rem)] overflow-y-auto space-y-6">
        <button
          onClick={onClose}
          className="text-gray-500 hover:text-gray-800 underline"
          aria-label="Close modal"
        >
          Back to Listings
        </button>
        <div className="flex flex-col text-left space-y-6">
          <div>
            <h2 className="text-3xl font-bold">{listing.title}</h2>
            <h3 className="text-blue-500 text-xl">{listing.condition}</h3>
          </div>
          <h2 className="text-2xl text-gray-600">${listing.price}</h2>
          <img
            src={listing.imageUrl}
            alt={listing.title}
            className="object-cover rounded-md"
          />
          <div className="flex flex-row text-lg">
            <p className="font-bold">{listing.username}:&nbsp;</p>
            <p>{listing.description}</p>
          </div>
          <h3 className="text-gray-500 text-md">
            Tags: {listing.category}, {listing.tags}
          </h3>
          {showFavoriteMessage && !favoritingLoading && (
            <div>
              <p className="text-red-600 ml-1">Added to favorites!</p>
            </div>
          )}
          {showRemovedMessage && !favoritingLoading && (
            <div>
              <p className="text-red-600 ml-1">Removed from favorites!</p>
            </div>
          )}
          {showEmailPopup && (
            <div className="bg-red-600 py-4 rounded-xl p-8">
              <p className="text-lg text-white">
                Email {""}
                <a
                  href={`mailto:${listing.email}`}
                  className="text-white underline"
                >
                  {listing.email}{" "}
                </a>
                to purchase this item.
              </p>
            </div>
          )}

          <div className="flex flex-row space-x-4 w-full">
            {favoritingLoading ? (
              <button className="flex bg-rose-200 text-lg p-4 rounded-xl w-1/6 justify-center items-center">
                <PulseLoader
                  color="#FFFFFF"
                  margin={4}
                  size={10}
                  speedMultiplier={0.7}
                />
              </button>
            ) : (
              <button
                className="flex bg-rose-200 text-lg p-4 rounded-xl w-1/6 justify-center items-center hover:bg-white hover:text-black border-rose-200 border-2"
                onClick={handleFavoriteClick}
              >
                {isFavorited ? <FaHeart color="black" /> : <FaRegHeart color="black" />}
              </button>
            )}
            <button
              className="bg-teal-200 text-lg p-4 rounded-xl w-5/6 hover:bg-white hover:text-black border-teal-200 border-2 "
              onClick={handleEmailSellerClick}
            >
              Message Seller: {listing.username}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ListingModal;
