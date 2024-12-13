import React, { useState } from 'react';

export const SearchBar: React.FC<{ onSearchSubmit: (term: string) => void }> = ({ onSearchSubmit }) => {
  const [searchTerm, setSearchTerm] = useState('');

  const handleSubmit = (event: React.FormEvent) => {
      event.preventDefault();
      onSearchSubmit(searchTerm); // Pass search term to parent
  };

  return (
      <form onSubmit={handleSubmit}>
          <input
              type="text"
              placeholder="Search..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full max-w-sm px-3 py-2 rounded-full border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-400"
          />  
          {/* <button type="submit">Search</button> */}
      </form>
  );
};
