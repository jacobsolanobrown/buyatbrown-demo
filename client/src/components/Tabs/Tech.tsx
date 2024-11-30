import React from 'react';
import FilterBar from '../FilterBar'; // Adjust the import path as needed

export default function Tech() {
  const clothesFilters = ["Outerwear", "Tops", "Sweaters", "Pants", "Dresses & Skirts", "Shoes", "Bags", "Accessories", "Other"];
  const conditionFilters = ["New", "Like New", "Used", "Good"];

  return (
    <div>
      <FilterBar title="Tech" filters={clothesFilters} conditionFilters={conditionFilters} />
    </div>
  );
}