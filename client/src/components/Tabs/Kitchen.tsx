import React, { useState, useEffect } from "react";
import { filterListings } from "../../utils/api";
import FilterBar from "../FilterBar"; // Adjust the import path as needed
import ListingCard from "../ListingCard";
import ListingModal from "../ListingModal";
import { PulseLoader } from "react-spinners";

export default function Kitchen() {
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

    setIsLoading(true);

    // format tag and condition filters for server
    const tagsString =
      selectedFilters.length == 0 ? "ignore" : selectedFilters.join(",");
    const conditionsString =
      selectedConditions.length == 0 ? "ignore" : selectedConditions.join(",");

    filterListings("ignore", "kitchen", tagsString, conditionsString) // no filtering by title
      .then((data) => {
        console.log("API Response:", data);
        if (
          data.response_type === "success" &&
          Array.isArray(data.filtered_listings)
        ) {
          setPosts(data.filtered_listings);
        } else {
          setPosts([]);
          setErrorMessage(data.error);
        }
      })
      .catch((err) => {
        setErrorMessage(
          "Error fetching kitchen listings. (Error:  " + err + ")"
        );
        console.error("Error fetching kitchen listings", err);
      })
      .finally(() => setIsLoading(false));
  }, [selectedConditions, selectedFilters]); // call server when either list changes (when filters change)

  return (
    <div className="flex flex-row">
      <div>
      <FilterBar
        title="Kitchen"
        filters={clothesFilters}
        conditionFilters={conditionFilters}
        onFiltersChange={handleFiltersChange}
        onConditionsChange={handleConditionsChange}
      />
      </div>

      {/* Render posts based on filters */}
      <div className="w-full h-full p-5 grid grid-cols-1 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 2xl:grid-cols-5 gap-8 mx-auto">
        {/* Display error message */}
        {errorMessage && (
          <div className="flex justify-center min-w-full h-dvh p-5">
            <p className="p-4 text-3xl font-ibm-plex-sans text-center text-red-600">
              {errorMessage}
            </p>
          </div>
        )}

        {isLoading ? (
          <div aria-label="loading" className="flex justify-center items-center min-w-full h-dvh p-5">
            <PulseLoader
              color="#ED1C24"
              margin={4}
              size={20}
              speedMultiplier={0.7}
            />
          </div>
        ) : posts.length === 0 && (selectedFilters.length > 0 || selectedConditions.length > 0) ? (
          <div aria-label="message" className="flex justify-center min-w-full h-dvh p-5">
            <p className="p-4 text-3xl font-ibm-plex-sans text-center text-red-600">
              No kitchen listings available with filters: {selectedFilters.join(", ") + ", " + selectedConditions.join(", ")}
            </p>
          </div>  
        ) : posts.length === 0  ? (
          <div aria-label="message" className="flex justify-center min-w-full h-dvh p-5">
            <p className="p-4 text-3xl font-ibm-plex-sans text-center text-red-600">
              No kitchen listings available.
            </p>
          </div>  
        ) :
        (
          posts.map((post) => (
            <ListingCard
              key={post.id}
              listingId={post.id}
              userId={post.userId}
              imageUrl={post.imageUrl}
              title={post.title}
              price={post.price}
              username={post.username}
              description={post.description}
              condition={post.condition}
              category={post.category}
              tags={post.tags}
              email={post.email}
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
