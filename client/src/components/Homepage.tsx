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
    <div className="flex flex-row">
      {/* Filter by condition */}
      <div className="bg-slate-200 p-4 min-w-64 max-w-64 rounded-xl max-h-32 ml-5 mt-5 mb-5">
        <h2 className="text-xl font-bold mb-4 text-center">Home</h2>
        <button
          className="bg-red-500 text-white py-2 px-4 rounded-3xl mb-4 w-full"
          onClick={handlePostListingClick}
        >
          Post Listing
        </button>
      </div>

      {/* Main content */}
      <div className=" w-full  h-full p-5 grid grid-cols-1 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 2xl:grid-cols-5 gap-8">
        {isLoading ? (
          <div className="">
            <img
              className="w-14 h-12"
              src="src/assets/Spin@1x-1.0s-200px-200px.gif"
              alt="Loading Image"
            />
          </div>
        ) : posts.length === 0 ? (
          <p>No listings available</p>
        ) : (
          console.log("all listing posts: ", posts),
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
  );
}

export default Homepage;
