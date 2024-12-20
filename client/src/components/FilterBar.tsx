import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

interface FilterBarProps {
  title: string;
  filters: string[];
  conditionFilters: string[];
  onFiltersChange: (filters: string[]) => void;
  onConditionsChange: (conditions: string[]) => void;
}

const FilterBar: React.FC<FilterBarProps> = ({
  title,
  filters,
  conditionFilters,
  onFiltersChange,
  onConditionsChange,
}) => {
  const [selectedFilters, setSelectedFilters] = useState<string[]>([]);
  const [selectedConditions, setSelectedConditions] = useState<string[]>([]);

  const navigate = useNavigate();

  const handlePostListingClick = () => {
    navigate("/listing-form");
  };

  // keep track of what filters are clicked:
  const toggleFilter = (filter: string) => {
    const newFilters = selectedFilters.includes(filter)
      ? selectedFilters.filter((f) => f !== filter) // Remove filter
      : [...selectedFilters, filter]; // Add filter
    setSelectedFilters(newFilters);
    onFiltersChange(newFilters); // Notify parent
  };

  // keep track of what conditions are clicked:
  const toggleCondition = (condition: string) => {
    const newConditions = selectedConditions.includes(condition)
      ? selectedConditions.filter((c) => c !== condition) // Remove condition
      : [...selectedConditions, condition]; // Add condition
    setSelectedConditions(newConditions);
    onConditionsChange(newConditions); // Notify parent
  };

  return (
    <div aria-label="filter" className="bg-gray-200 p-4 w-64 rounded-xl ml-5 mr-5 mt-5 min-h-96">
      <h2 className="text-xl font-bold mb-4 text-center ">{title}</h2>
      <button
        aria-label="post listing"
        className="bg-red-500 text-white py-2 px-4 rounded-3xl mb-4 w-full"
        onClick={handlePostListingClick}
      >
        Post Listing
      </button>
      <div aria-label="tag filters" className="mb-4">
        <h3 className="font-semibold mb-2">Filters</h3>
        {filters.map((filter, index) => (
          <button
            aria-label="tag"
            key={index}
            onClick={() => toggleFilter(filter)}
            className={`block py-2 px-4 rounded-3xl mb-4 w-full text-left ${
              selectedFilters.includes(filter)
                ? "bg-blue-500 text-white"
                : "bg-white text-black"
            }`}
          >
            {filter}
          </button>
        ))}
      </div>

      <div aria-label="condition filters">
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
  );
};

export default FilterBar;
