import React, { useState, useEffect } from "react";
import { getAllListings } from "../utils/api";
import ListingCard from "./ListingCard";
import ListingModal from "./ListingModal";

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

  return (
    <div className="p-8 grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
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
  );
}

export default Homepage;
