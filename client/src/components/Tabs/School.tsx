import React from 'react';
import FilterBar from '../FilterBar'; // Adjust the import path as needed

export default function School() {
  const clothesFilters = ["Stationary", "Books", "Textbooks", "Printing", "Art Supplies", "Other"];
  const conditionFilters = ["New", "Like New", "Used", "Good"];

  return (
    <div>
      <FilterBar title="School" filters={clothesFilters} conditionFilters={conditionFilters} />
    </div>
  );
}