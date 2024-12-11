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
    // format tag and condition filters for server 
    const tagsString = selectedFilters.length == 0 ? "ignore" : selectedFilters.join(",");
    const conditionsString = selectedConditions.length == 0 ? "ignore" : selectedConditions.join(",");

    filterListings("ignore", "clothes", tagsString, conditionsString) // no filtering by title
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
          console.error("Error fetching clothes listings", err);
        });
    }, [selectedConditions, selectedFilters]); // call server when either list changes (when filters change)

  
  return (
    <div className="flex">
      <FilterBar 
        title="Clothes" 
        filters={clothesFilters} 
        conditionFilters={conditionFilters}
        onFiltersChange={handleFiltersChange}
        onConditionsChange={handleConditionsChange} />

      {/* Render posts based on filters */}
      <div className="p-8 grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6 items-start">
        {posts.map((post) => ( // posts should reflect 
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
          onClick={handleCardClick}
        />
      ))}
    </div>
    </div>
  );
}
