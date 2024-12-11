import React, {useState, useEffect}from 'react';
import { filterListings } from '../../utils/api';
import FilterBar from '../FilterBar'; // Adjust the import path as needed
import ListingCard from '../ListingCard';
import ListingModal from '../ListingModal';

export default function Kitchen () {

  // ********* Bigger card with more information on click: *********

  const [selectedListing, setSelectedListing] = useState<any | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  const handleCardClick = (listing: any) => {
    setSelectedListing(listing);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedListing(null);
  };

  // **************** Handle filtering: ***************

  const clothesFilters = ["Appliances", "Kitchenware", "Tableware", "Other"];
  const conditionFilters = ["New", "Like New", "Used"];

  // Array of tag filters selected
  const [selectedFilters, setSelectedFilters] = useState<string[]>([]); 
  // Array of condition filters selected:
  const [selectedConditions, setSelectedConditions] = useState<string[]>([]); 
  // Array to store all listings with the corresponding category
  const [posts, setPosts] = useState<any[]>([]);

  // Update activeFilters state
  const handleFiltersChange = (newFilters: string[]) => {
    setSelectedFilters(newFilters); 
  };

  // Update activeConditions state
  const handleConditionsChange = (newConditions: string[]) => {
    setSelectedConditions(newConditions); 
  };

  // Fetch data from the api:
  useEffect(() => {
    // format tag and condition filters for server 
    const tagsString = selectedFilters.length == 0 ? "ignore" : selectedFilters.join(",");
    const conditionsString = selectedConditions.length == 0 ? "ignore" : selectedConditions.join(",");

    filterListings("ignore", "kitchen", tagsString, conditionsString) // no filtering by title
        .then((data) => {
          console.log("API Response:", data);
          if (data.response_type === "success" && Array.isArray(data.filtered_listings)) {
            setPosts(data.filtered_listings); 
            console.log("success!!!");
            console.log("Posts:", posts);
          } else {
            setPosts([]);
            console.log("not success???");
            console.log("Posts:", posts);
          }
        })
        .catch((err) => {
          console.error("Error fetching kitchen listings", err);
        })
        .finally(() => setIsLoading(false));
    }, [selectedConditions, selectedFilters]); // call server when either list changes (when filters change)

  
  return (
    <div className="flex">
      <FilterBar 
        title="Kitchen" 
        filters={clothesFilters} 
        conditionFilters={conditionFilters}
        onFiltersChange={handleFiltersChange}
        onConditionsChange={handleConditionsChange} />

      {/* Render posts based on filters */}
      <div className="py-8 grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-5 items-start mx-auto">
        {isLoading ? (
            <div className="text-2xl align-center">Loading All Listings...</div>
          ) : posts.length === 0 ? (
            <p>No kitchen listings available</p>
          ) : (
            posts.map((post) => ( // posts should reflect 
            <ListingCard
              key={post.id}
              imageUrl={"src/assets/brown-university-logo-transparent.png"} 
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