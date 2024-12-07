import React from 'react';
import FilterBar from '../FilterBar'; // Adjust the import path as needed

export default function Tech() {
  const clothesFilters = ["Computers", "Televisions", "Tablets", "Cell Phones", "Wearables", "Gaming", "Photography", "Cars", "Audio", "Other"];
  const conditionFilters = ["New", "Like New", "Used", "Good"];

  return (
    <div>
      <FilterBar title="Tech" filters={clothesFilters} conditionFilters={conditionFilters} />
    </div>
  );
}