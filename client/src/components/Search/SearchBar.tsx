import React, { useState } from 'react';
import { IoSearch } from "react-icons/io5";

export const SearchBar: React.FC<{ onSearchSubmit: (term: string) => void }> = ({ onSearchSubmit }) => {
  const [searchTerm, setSearchTerm] = useState('');

  const handleSubmit = (event: React.FormEvent) => {
      event.preventDefault();
      onSearchSubmit(searchTerm); // Pass search term to parent
      setSearchTerm('');
  };

  return (
    // EDIT HERE FOR SEARCH LENGTH 
      <form onSubmit={handleSubmit} className="relative w-full">
          <input
              type="text"
              placeholder="Search for items..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="min-w-full px-4 py-2 pr-10 rounded-full border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-400"
          />  
          {/* Submit Button */}
          <button
            type="submit"
            className="absolute right-2 top-1/2 transform -translate-y-1/2 bg-gray-800 text-white rounded-full p-2 hover:bg-gray-700 focus:ring-2 focus:ring-blue-400"
          >
            <IoSearch size={18} />
          </button>
      </form>
  );
};
