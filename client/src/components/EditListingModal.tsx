import React, { useEffect } from "react";
import { deleteListing } from "../utils/api";
import { useUser } from "@clerk/clerk-react";
import { useNavigate } from "react-router-dom";
import { PulseLoader } from "react-spinners";

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
  const navigate = useNavigate();
  const [deleteLoading, setDeleteLoading] = React.useState(false);

  const handleDeleteListingClick = () => {
    if (user) {
      setDeleteLoading(true);
      deleteListing(user.id, listing.listingId)
        .then((data) => {
          if (data.response_type === "success") {
            window.location.reload();
          } else {
            console.error("Error deleting listing: ", data);
          }
        })
        .catch((err) => {
          console.error("Error deleting listing: ", err);
          ``;
        })
        .finally(() => {
          setDeleteLoading(false);
          onClose();
        });
    }
  };

  const handleEditListingClick = () => {
    navigate("/editing-form", { state: { listing } });
  };

  // Close the modal if the user clicks outside of it
  const handleOutsideClick = (e: React.MouseEvent<HTMLDivElement>) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  useEffect(() => {
    console.log();
  }, []);

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
          <button
            className="rounded-xl text-white bg-red-600 text-lg p-4 hover:bg-white hover:text-red-600 border-red-600 border-2 "
            onClick={handleEditListingClick}
            aria-label="Edit Listing Button"
          >
            Edit Listing
          </button>
          {deleteLoading ? (
            <button
              className="rounded-xl text-white bg-amber-950 p-4"
            >
              <PulseLoader
                color="#FFFFFF"
                margin={4}
                size={10}
                speedMultiplier={0.7}
              />
            </button>
          ) : (
            <button
              className="rounded-xl text-white bg-amber-950 text-lg p-4 hover:bg-white hover:text-amber-950 hover:border-2 border-amber-950  "
              onClick={handleDeleteListingClick}
              aria-label="Delete Listing Button"
            >
              Delete Listing
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default ListingModal;
