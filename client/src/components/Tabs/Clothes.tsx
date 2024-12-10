import React, {useState, useEffect}from 'react';
import { filterListings } from '../../utils/api';
import FilterBar from '../FilterBar'; // Adjust the import path as needed

export default function Clothes() {
  const clothesFilters = ["Outerwear", "Tops", "Sweaters", "Pants", "Dresses & Skirts", "Shoes", "Bags", "Accessories", "Other"];
  const conditionFilters = ["New", "Like New", "Used", "Good"];

  // call filter by category -- always filtering by category!! 
  // filtering category + filters that are selected 

  // array to store all listings with the corresponding category
  const [posts, setPosts] = useState<any[]>([]);

  // current filters array (buttons that are clicked in the filter bar)
  // maybe map?? 
  // tags: [] # by commas??
  // condition: [] # array 



  // iterate through this map and set corresponding variables to pass into filter Listings


  // fetch data from the api
    useEffect(() => {
      filterListings("", "", "", "", "sweater,jackets", "clothes")
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
    }, []); 


  return (
    <div>
      <FilterBar title="Clothes" filters={clothesFilters} conditionFilters={conditionFilters} />
    </div>
  );
}
