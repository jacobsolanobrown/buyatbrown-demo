import React from 'react';

interface FilterBarProps {
  title: string;
  filters: string[];
  conditionFilters: string[];
}

const FilterBar: React.FC<FilterBarProps> = ({ title, filters, conditionFilters }) => {
  return (
    <div className="bg-gray-200 p-4 w-64 rounded-xl mx-5 mt-5">
      <h2 className="text-xl font-bold mb-4 text-center ">{title}</h2>
      <button className="bg-red-500 text-white py-2 px-4 rounded-3xl mb-4 w-full">Post Listing</button>
      <div className="mb-4">
        <h3 className="font-semibold mb-2">Filters</h3>
        {filters.map((filter, index) => (
          <button key={index} className="block bg-white text-black py-2 px-4 rounded-3xl mb-4 w-full text-left">
            {filter}
          </button>
        ))}
      </div>
      <div>
        <h3 className="font-semibold mb-2">Condition Filters</h3>
        {conditionFilters.map((condition, index) => (
          <button key={index} className="block bg-white text-black py-2 px-4 rounded-3xl mb-2 w-full text-left">
            {condition}
          </button>
        ))}
      </div>
    </div>
  );
}

export default FilterBar;

  
