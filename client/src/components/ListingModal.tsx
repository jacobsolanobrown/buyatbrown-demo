import React, { useEffect } from "react";
import { FaRegHeart } from "react-icons/fa";


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
  };
}

// This listing modal represents the bigger card when the listing card is clicked
const ListingModal: React.FC<ModalCardProps> = ({
  isOpen,
  onClose,
  listing,
}) => {
  if (!isOpen || !listing) return null;

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
      className="fixed inset-0 bg-black bg-opacity-60 flex justify-center items-center z-50 min-w-screen"
      onClick={handleOutsideClick}
      role="dialog"
      aria-modal="true"
    >
      <div className="bg-white p-8 rounded-lg shadow-lg max-w-lg w-full relative space-y-6 min-w-fit">
        <div className="flex flex-col text-left space-y-6">
          <button
            onClick={onClose}
            className=" text-gray-500 hover:text-gray-800 underline"
            aria-label="Close modal"
          >
            Back to Listings
          </button>
          <div>
            <h2 className="text-2xl font-bold">{listing.title}</h2>
            <h3 className="text-gray-500 text-lg">{listing.condition}</h3>
          </div>
          <h2 className="text-xl text-gray-600">${listing.price}</h2>
          <img
            src={listing.imageUrl}
            alt={listing.title}
            className="w-full h-72 object-cover rounded-md"
          />
          <p className="text-gray-700 text-md">
            <span className="font-bold">{listing.username}</span>:{" "}
            {listing.description}
          </p>
          {/* <h3 className="text-gray-500 text-md">
            Category: {listing.category}
          </h3> */}
          <h3 className="text-gray-500 text-md">Tags: {listing.category}, {listing.tags}</h3>
          <div className="flex flex-row space-x-4 w-full">
            <button className="flex bg-rose-200 text-lg p-4 rounded-xl w-1/6 justify-center items-center">
              <FaRegHeart />
            </button>
            <button className="bg-teal-200 text-lg p-4 rounded-xl w-5/6">
              Message Seller: {listing.username}
            </button>
          </div>
          {/* <div className="flex justify-center items-center">
            <p>Added to Favorites!</p>
          </div>
          <p>TODO add a message to indicate added to favorites </p> */}
        </div>
      </div>
    </div>
  );
};

export default ListingModal;
