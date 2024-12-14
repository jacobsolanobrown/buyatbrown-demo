import React, { useState, useEffect } from "react";
import { getAllListings } from "../utils/api";
import ListingCard from "./ListingCard";
import ListingModal from "./ListingModal";
import { useNavigate } from "react-router-dom";

function Homepage() {
  const [posts, setPosts] = useState<any[]>([]);
  const [selectedListing, setSelectedListing] = useState<any | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    getAllListings()
      .then((data) => {
        if (data.response_type === "success" && Array.isArray(data.listings)) {
          setPosts(data.listings);
        } else {
          setPosts([]);
        }
      })
      .catch((err) => {
        console.error("Error fetching listings:", err);
      })
      .finally(() => setIsLoading(false));
  }, []);

  const handleCardClick = (listing: any) => {
    setSelectedListing(listing);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedListing(null);
  };

  // Post Listing:
  const navigate = useNavigate();
  const handlePostListingClick = () => {
    navigate("/listing-form");
  };

  return (
    <div className="flex">
    {/* Filter by condition */}
      <div className="bg-gray-200 p-4 w-64 rounded-xl max-h-32 ml-5 mt-5">
        <h2 className="text-xl font-bold mb-4 text-center "> Home </h2>
        <button
          className="bg-red-500 text-white py-2 px-4 rounded-3xl mb-4 w-full"
          onClick={handlePostListingClick}
        >
          Post Listing
        </button>
      </div>

      <div className="py-8 grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-5 items-start mx-auto">
        {isLoading ? (
          <div className="text-2xl align-center">Loading All Listings...</div>
        ) : posts.length === 0 ? (
          <p>No listings available</p>
        ) : (
          posts.map((post) => (
            <ListingCard
              key={post.id}
              imageUrl={post.imageUrl}
              title={post.title}
              price={post.price}
              username={post.username}
              description={post.description}
              condition={post.condition}
              category={post.category}
              tags={post.tags}
              onClick={() => handleCardClick(post)}
            />
          ))
        )}
        {isModalOpen && (
          <ListingModal
            isOpen={isModalOpen}
            onClose={handleCloseModal}
            listing={selectedListing}
          />
        )}
      </div>
    </div>



    // <div className="p-8 grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
    //   {isLoading ? (
    //     <div className="text-2xl align-center">Loading All Listings...</div>
    //   ) : posts.length === 0 ? (
    //     <p>No listings available</p>
    //   ) : (
    //     posts.map((post) => (
    //       <ListingCard
    //         key={post.id}
    //         imageUrl={post.imageUrl}
    //         title={post.title}
    //         price={post.price}
    //         username={post.username}
    //         description={post.description}
    //         condition={post.condition}
    //         category={post.category}
    //         tags={post.tags}
    //         onClick={() => handleCardClick(post)}
    //       />
    //     ))
    //   )}
    //   {isModalOpen && (
    //     <ListingModal
    //       isOpen={isModalOpen}
    //       onClose={handleCloseModal}
    //       listing={selectedListing}
    //     />
    //   )}
    // </div>
  );
}

export default Homepage;
