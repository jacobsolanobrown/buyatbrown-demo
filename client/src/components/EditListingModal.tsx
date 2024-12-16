import React, { useEffect } from "react";
import { FaHeart, FaRegHeart } from "react-icons/fa";
import { useState } from "react";
import { deleteListing } from "../utils/api";
import { useUser } from "@clerk/clerk-react";
// TODO: can pass in

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
  const [showEmailPopup, setShowEmailPopup] = useState(false);

  const handleEmailSellerClick = () => {
    setShowEmailPopup(true);
  };

  const handleDeleteListingClick = () => {
    if (user) {
      deleteListing(user.id, listing.listingId);
    }
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
      {/* //TODO: ASK WETHER TO KEEP LARGE WIDE CARDS OR SKINNY ONES  */}
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
            <h3 className="text-gray-500 text-xl">{listing.condition}</h3>
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
          <button className="rounded-xl text-white bg-red-600 text-lg p-4">
            Edit Listing
          </button>
          <button
            className="rounded-xl text-white bg-yellow-500 text-lg p-4"
            onClick={handleDeleteListingClick}
          >
            Delete Listing
          </button>
        </div>
      </div>
    </div>
  );
};

export default ListingModal;
