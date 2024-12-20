import React, {useState, useEffect}from 'react';
import { filterListings } from '../../utils/api';
import { useLocation } from 'react-router-dom';
import FilterBar from '../FilterBar';
import ListingCard from '../ListingCard';
import ListingModal from '../ListingModal';
import { PulseLoader } from 'react-spinners';


export default function SearchResultsPage () {


  // ********* Bigger card with more information on click: *********
  const [selectedListing, setSelectedListing] = useState<any | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");
  
  const handleCardClick = (listing: any) => {
    setSelectedListing(listing);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedListing(null);
  };

  // **************** Handle filtering: ***************

  // keep track of what conditions are clicked:
  const toggleCondition = (condition: string) => {
      const newConditions = selectedConditions.includes(condition)
      ? selectedConditions.filter((c) => c !== condition) // Remove condition
      : [...selectedConditions, condition]; // Add condition
      setSelectedConditions(newConditions);
  };

  const conditionFilters = ["New", "Like New", "Used"];
  // Array of condition filters selected:
  const [selectedConditions, setSelectedConditions] = useState<string[]>([]); 

  // Search Term and Results passed from parent:
  const location = useLocation();
  const { searchTerm, filteredPosts} = location.state || {}; // Retrieve passed data

  // Array to store relevant results: 
  const [posts, setPosts] = useState(filteredPosts || []); // initialized to search results

  useEffect(() => {
    // only able to filter by condition within search results page
    const conditionsString = selectedConditions.length == 0 ? "ignore" : selectedConditions.join(",");
    setIsLoading(true);
    filterListings(searchTerm, "ignore", "ignore", conditionsString) 
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
          console.error("Error fetching clothes listings", err);
        })
        .finally(() => setIsLoading(false));
    }, [selectedConditions, filteredPosts]); // call server when conditions or searchTerm changes

  return (
    <div className="flex">
      {/* Filter by Condition */}
      <div>
        <div className="bg-gray-200 p-4 w-64 rounded-xl ml-5 mr-5 mt-5">
          <h2 className="text-xl font-bold mb-4 text-center ">
            {" "}
            Search Results for: {searchTerm}{" "}
          </h2>

          <div aria-label="filter" className="content-center">
            <h3 className="font-semibold mb-2">Condition Filters</h3>
            {conditionFilters.map((condition, index) => (
              <button
                aria-label="condition"
                key={index}
                onClick={() => toggleCondition(condition)}
                className={`block py-2 px-4 rounded-3xl mb-2 w-full text-left ${
                  selectedConditions.includes(condition)
                    ? "bg-blue-500 text-white"
                    : "bg-white text-black"
                }`}
              >
                {condition}
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* Render posts based on filters */}
      <div aria-label="results" className="w-full h-full p-5 grid grid-cols-1 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 2xl:grid-cols-5 gap-8">
        {/* Display error message */}
        {errorMessage && (
          <div className="flex justify-center items-center min-w-full h-dvh p-5">
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
        ) : posts.length === 0 ? (
          <div aria-label="message" className="flex justify-center min-w-full h-dvh p-5">
            <p className="p-4 text-3xl font-ibm-plex-sans text-center text-red-600">
              {" "}
              No listings matched search term: {searchTerm}
            </p>
          </div>
        ) : (
          posts.map((post: any) => (
            <ListingCard
              key={post.id}
              email={post.email}
              listingId={post.listingId}
              userId={post.userId}
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
