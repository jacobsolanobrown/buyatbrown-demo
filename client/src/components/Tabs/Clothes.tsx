import React, {useState, useEffect}from 'react';
import { filterListings } from '../../utils/api';
import FilterBar from '../FilterBar'; // Adjust the import path as needed
import ListingCard from '../ListingCard';

export default function Clothes () {
  // call filter by category -- always filtering by category!! 
  // filtering category + filters that are selected 
  const handleCardClick = () => {
    alert("more info");
  };

  const clothesFilters = ["Outerwear", "Tops", "Sweaters", "Pants", "Dresses & Skirts", "Shoes", "Bags", "Accessories", "Other"];
  const conditionFilters = ["New", "Like New", "Used", "Good"];

  // array of tag filters selected
  const [selectedFilters, setSelectedFilters] = useState<string[]>([]); 
  // array of condition filters selected:
  const [selectedConditions, setSelectedConditions] = useState<string[]>([]); 
  // array to store all listings with the corresponding category
  const [posts, setPosts] = useState<any[]>([]);

  // Update activeFilters state
  const handleFiltersChange = (newFilters: string[]) => {
    setSelectedFilters(newFilters); 
  };


  // Update activeConditions state
  const handleConditionsChange = (newConditions: string[]) => {
    setSelectedConditions(newConditions); 
  };

  // fetch data from the api
  useEffect(() => {
    // input to pass into server 
    // const filterTrue = selectedFilters.length == 0 ? "false" : "true";
    // const conditionsTrue = selectedConditions.length == 0 ? "false" : "true";
    // Convert arrays to comma-separated strings to pass into server 
    const tagsString = selectedFilters.join(","); 
    const conditionsString = selectedConditions.join(","); 

    filterListings("nothing", "clothes", tagsString, conditionsString)
        .then((data) => {
          if (data.response_type === "success" && Array.isArray(data.listings)) {
            setPosts(data.listings); 
          } else {
            setPosts([]);
          }
        })
        .catch((err) => {
          console.error("Error fetching clothes listings", err);
        });
    }, [selectedConditions, selectedFilters]); // call server when either list changes (when filters change)


  return (
    <div>
      <FilterBar 
        title="Clothes" 
        filters={clothesFilters} 
        conditionFilters={conditionFilters}
        onFiltersChange={handleFiltersChange}
        onConditionsChange={handleConditionsChange} />

      {/* Render posts based on filters */}
      <div className="p-8 grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
        {posts.map((post) => ( // posts should reflect 
        <ListingCard
          key={post.uid}
          imageUrl={post.imageUrl}
          title={post.title}
          price={post.price}
          username={post.username}
          description={post.description}
          onClick={handleCardClick}
        />
      ))}
    </div>
    </div>
  );
}
