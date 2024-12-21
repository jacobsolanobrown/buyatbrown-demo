import React, { useState } from "react";
import { Link } from "react-router-dom";

export default function DropdownNavUser() {
  const [dropdownVisible, setDropdownVisible] = useState(false);

  const toggleDropdown = () => {
    setDropdownVisible(!dropdownVisible);
  };

  return (
    <div>
      {/* Dropdown for smaller screens */}
      <div aria-label="dropdown" className="min-[860px]:hidden p-4 flex flex-col items-center">
        <button
          aria-label="Profile dropdown"
          onClick={toggleDropdown}
          className="w-80 bg-white text-black py-2 px-4 rounded-3xl hover:bg-blue-800 focus:ring-4 focus:outline-none 
          focus:ring-blue-300 font-medium text-sm text-center mb-2"
        >
          <h1>Your Profile ⌄</h1>
        </button>

        {dropdownVisible && (
          <div className="bg-white text-gray-950 w-full rounded-3xl shadow-lg p-4">
            <nav className="space-y-4 text-lg font-semibold text-center">
              <Link to="/yourlistings" className="block text-gray-950 underline hover:text-stone-200">
                Listings
              </Link>
              <Link to="/favorites" className="block text-gray-950 underline hover:text-stone-200">
                Favorites
              </Link>
              {/* <Link to="/messages" className="block text-gray-950 underline hover:text-stone-200">
                Messages
              </Link> */}
              <Link to="/settings" className="block text-gray-950 underline hover:text-stone-200">
                Settings
              </Link>
            </nav>
          </div>
        )}
      </div>
    </div>
  );
}
