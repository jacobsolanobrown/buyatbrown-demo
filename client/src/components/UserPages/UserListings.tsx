import React from "react";
import ListingCard from "../ListingCard";
import NavUser from "./NavUser";

export default function UserListings() {
  const handleCardClick = () => {
    alert("more info");
  };

  return (
    <div className="min-h-screen flex bg-gradient-to-r from-blue-100 to-pink-100">
    
      <div className="w-1/5">
        <NavUser />
      </div>

      
      <div className="flex-1 p-8">

        <div className="bg-red-500 text-white py-4 px-6 rounded-lg shadow-md mb-6">
          <h1 className="text-lg font-semibold">Hey, username.meow!</h1>
        </div>
        
        <div className="bg-white p-6 rounded-lg shadow-md border">
          <h2 className="text-2xl font-bold mb-2">Your Listings</h2>
          <p className="text-sm text-gray-600 mb-6">Edit, or delete active/sold listings.</p>

          
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
            <ListingCard
              imageUrl="https://via.placeholder.com/300"
              title="Brown Uni Sweatshirt"
              price="$10.99"
              username="username.meow"
              onClick={handleCardClick}
            />
            <ListingCard
              imageUrl="https://via.placeholder.com/300"
              title="Brown Uni Sweatshirt"
              price="$10.99"
              username="username.meow"
              onClick={handleCardClick}
            />
            <ListingCard
              imageUrl="https://via.placeholder.com/300"
              title="Brown Uni Sweatshirt"
              price="$10.99"
              username="username.meow"
              onClick={handleCardClick}
            />
            <ListingCard
              imageUrl="https://via.placeholder.com/300"
              title="Brown Uni Sweatshirt"
              price="$10.99"
              username="username.meow"
              onClick={handleCardClick}
            />
          </div>
        </div>
      </div>
    </div>
  );
}
