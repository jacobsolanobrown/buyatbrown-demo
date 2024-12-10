import React from "react";
import ListingCard from "../ListingCard";
import NavUser from "./NavUser";
import { Link } from "react-router-dom";
import { useState } from "react";

export default function UserFavorites({ username }: { username: string }) {
  const [dropdownVisible, setDropdownVisible] = useState(false);

  const toggleDropdown = () => {
    setDropdownVisible(!dropdownVisible);
  };

  const handleCardClick = () => {
    alert("More info");
  };

  return (
    <div className="min-h-screen flex bg-gradient-to-r from-blue-100 to-pink-100">
      {/* Sidebar for larger screens */}
      <div className="hidden min-[860px]:block">
        <NavUser />
      </div>

      {/* Main content */}
      <div className="flex-1 p-8">
        <div className="flex flex-col items-center min-[860px]:block">
          <div className="bg-red-600 w-96 text-white py-4 px-6 rounded-lg shadow-md mb-6">
            <h1 className="text-lg font-semibold text-center">
              Hey, {username}!
            </h1>
          </div>
        </div>

        {/* Dropdown for smaller screens */}
        <div className="min-[860px]:hidden p-4 flex flex-col items-center">
          <button
            onClick={toggleDropdown}
            className="w-80 bg-white text-black py-2 px-4 rounded-3xl hover:bg-blue-800 focus:ring-4 focus:outline-none 
          focus:ring-blue-300 font-medium text-sm text-center mb-2"
          >
            <h1> Your Profile ⌄ </h1>
          </button>

          {dropdownVisible && (
            <div className="bg-white text-gray-950 w-full rounded-3xl shadow-lg p-4">
              <nav className="space-y-4 text-lg font-semibold text-center">
                <Link
                  to="/yourlistings"
                  className="block text-gray-950 underline hover:text-stone-200"
                >
                  Listings
                </Link>
                <Link
                  to="/favorites"
                  className="block text-gray-950 underline hover:text-stone-200"
                >
                  Favorites
                </Link>
                <Link
                  to="/messages"
                  className="block text-gray-950 underline hover:text-stone-200"
                >
                  Messages
                </Link>
                <Link
                  to="/settings"
                  className="block text-gray-950 underline hover:text-stone-200"
                >
                  Settings
                </Link>
              </nav>
            </div>
          )}
        </div>

        <div className="bg-white/50 p-6 rounded-lg shadow-md border">
          <h2 className="text-2xl font-bold mb-2">Favorites</h2>
          <p className="text-sm text-gray-600 mb-6">
            View, buy, or unfavorite your favorite listings.
          </p>

          <div
            className="grid grid-cols-1 min-h-screen place-items-center 
             min-[1410px]:grid-cols-4 min-[860px]:grid-cols-2 
             min-[1110px]:grid-cols-3 gap-4
             min-[860px]:place-items-start min-[860px]:min-h-0
             
             "
          >
            <ListingCard
              imageUrl="https://via.placeholder.com/300"
              title="Brown Uni Sweatshirt"
              price="$10.99"
              username="username.meow"
              description=""
              condition="New"
              category="Clothes"
              tags={["Brown", "Sweatshirt", "Brown University"]}
              onClick={handleCardClick}
            />
            <ListingCard
              imageUrl="https://via.placeholder.com/300"
              title="Brown Uni Sweatshirt"
              price="$10.99"
              username="username.meow"
              description=""
              condition="New"
              category="Clothes"
              tags={["Brown", "Sweatshirt", "Brown University"]}
              onClick={handleCardClick}
            />
            <ListingCard
              imageUrl="https://via.placeholder.com/300"
              title="Brown Uni Sweatshirt"
              price="$10.99"
              username="username.meow"
              description=""
              condition="New"
              category="Clothes"
              tags={["Brown", "Sweatshirt", "Brown University"]}
              onClick={handleCardClick}
            />
            <ListingCard
              imageUrl="https://via.placeholder.com/300"
              title="Brown Uni Sweatshirt"
              price="$10.99"
              username="username.meow"
              description=""
              condition="New"
              category="Clothes"
              tags={["Brown", "Sweatshirt", "Brown University"]}
              onClick={handleCardClick}
            />
          </div>
        </div>
      </div>
    </div>
  );
}
