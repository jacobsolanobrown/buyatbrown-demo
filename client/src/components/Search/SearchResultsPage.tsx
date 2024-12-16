import React, {useState, useEffect}from 'react';
import { filterListings } from '../../utils/api';
import { useLocation } from 'react-router-dom';
import FilterBar from '../FilterBar';
import ListingCard from '../ListingCard';
import ListingModal from '../ListingModal';


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
  const { searchTerm, filteredPosts } = location.state || {}; // Retrieve passed data

  // Array to store relevant results: 
  const [posts, setPosts] = useState(filteredPosts || []); // initialized to search results

  useEffect(() => {
    // only able to filter by condition within search results page
    const conditionsString = selectedConditions.length == 0 ? "ignore" : selectedConditions.join(",");

    filterListings(searchTerm, "ignore", "ignore", conditionsString) // no filtering by title
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
    }, [selectedConditions, searchTerm]); // call server when conditions or searchTerm changes

  return (
    <div className="flex">
        {/* Filter by Condition */}
        <div className="bg-gray-200 p-4 w-64 rounded-xl ml-5 mr-5 mt-5">
            <h2 className="text-xl font-bold mb-4 text-center "> Search Results for: {searchTerm} </h2>

            <div className="content-center">
                <h3 className="font-semibold mb-2">Condition Filters</h3>
                {conditionFilters.map((condition, index) => (
                <button
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
            className="w-14 h-12 align-center"
            src="src/assets/Spin@1x-1.0s-200px-200px.gif"
            alt="Loading Image"
          />
          </div>
            
          ) : posts.length === 0 ? (
            <p className= "p-4 text-3xl font-ibm-plex-sans text-center text-red-600"> No listings matched search term: {searchTerm}</p>
          ) : (
            posts.map((post: any) => ( 
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
