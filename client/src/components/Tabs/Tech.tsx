import React, {useState, useEffect}from 'react';
import { filterListings } from '../../utils/api';
import FilterBar from '../FilterBar'; // Adjust the import path as needed
import ListingCard from '../ListingCard';
import ListingModal from '../ListingModal';

export default function Tech () {

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

  const clothesFilters = ["Computers", "Televisions", "Tablets", "Cell Phones", "Wearables", "Gaming", "Photography", "Cars", "Audio", "Other"];
  const conditionFilters = ["New", "Like New", "Used"];

  // Array of tag filters selected
  const [selectedFilters, setSelectedFilters] = useState<string[]>([]); 
  // Array of condition filters selected:
  const [selectedConditions, setSelectedConditions] = useState<string[]>([]); 
  // Array to store all listings with the corresponding category
  const [posts, setPosts] = useState<any[]>([]);
  // Display error message if failure from server:
  const [errorMessage, setErrorMessage] = useState("");

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

    filterListings("ignore", "tech", tagsString, conditionsString) // no filtering by title
        .then((data) => {
          console.log("API Response:", data);
          if (data.response_type === "success" && Array.isArray(data.filtered_listings)) {
            setPosts(data.filtered_listings); 
          } else {
            setPosts([]);
            setErrorMessage(data.error);
          }
        })
        .catch((err) => {
          setErrorMessage("Error fetching tech listings. (Error:  " + err + ")");
          console.error("Error fetching tech listings", err);
        })
        .finally(() => setIsLoading(false));
    }, [selectedConditions, selectedFilters]); // call server when either list changes (when filters change)

  
  return (
    <div className="flex flex-row">
      <FilterBar 
        title="Tech" 
        filters={clothesFilters} 
        conditionFilters={conditionFilters}
        onFiltersChange={handleFiltersChange}
        onConditionsChange={handleConditionsChange} />

      {/* Render posts based on filters */}
      <div className="w-full h-full p-5 grid grid-cols-1 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 2xl:grid-cols-5 gap-8">

        {/* Display error message */}
        {errorMessage && (
          <p className="p-4 text-3xl font-ibm-plex-sans text-center text-red-600">
            {errorMessage}
          </p>
        )}

        {isLoading ? (
          <div className="align-center">
          <img
            className="w-14 h-12"
            src="src/assets/Spin@1x-1.0s-200px-200px.gif"
            alt="Loading Image"
          />
          </div>

          ) : posts.length === 0 ? (
            <p className="p-4 text-3xl font-ibm-plex-sans text-center text-red-600">No tech listings available</p>
          ) : (
            posts.map((post) => ( // posts should reflect 
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