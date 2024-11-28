import React from 'react';
import FilterBar from '../FilterBar'; // Adjust the import path as needed

export default function Kitchen() {
  const clothesFilters = ["Outerwear", "Tops", "Sweaters", "Pants", "Dresses & Skirts", "Shoes", "Bags", "Accessories", "Other"];
  const conditionFilters = ["New", "Like New", "Used", "Good"];

  return (
    <div>
      <FilterBar title="Kitchen" filters={clothesFilters} conditionFilters={conditionFilters} />
    </div>
  );
}