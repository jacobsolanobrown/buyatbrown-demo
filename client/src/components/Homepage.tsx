import React from "react";
import ListingCard from "./ListingCard";

function Homepage() {
  const handleCardClick = () => {
    alert("more info");
  };

  return (
    <div className="p-8 grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
      <ListingCard
        imageUrl="https://via.placeholder.com/300" 
        title="Item 1"
        price="$10"
        username="user1"
        onClick={handleCardClick}
      />
      <ListingCard
        imageUrl="https://via.placeholder.com/300" 
        title="item 2"
        price="$40"
        username="user2"
        onClick={handleCardClick}
      />
    </div>
  );
}

export default Homepage;
