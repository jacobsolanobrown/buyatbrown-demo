import React, { useState, useEffect } from "react";
import { getAllListings } from "../utils/api";
import ListingCard from "./ListingCard";

function Homepage() {
  const handleCardClick = () => {
    alert("more info");
  };

  // array to store all users listings 
  const [posts, setPosts] = useState<any[]>([]);

  // fetch data from the api
  useEffect(() => {
    getAllListings()
      .then((data) => {
        if (data.response_type === "success" && Array.isArray(data.listings)) {
          setPosts(data.listings); // set all users listings
        } else {
          setPosts([]);
        }
      })
      .catch((err) => {
        console.error("Error fetching all the users listings:", err);
      });
  }, []);

  return (
    <div className="p-8 grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
        {posts.map((post) => (
          <ListingCard
            imageUrl={post.imageUrl}
            title={post.title}
            price={post.price}
            username={post.uid}
            description={post.description}
            onClick={handleCardClick}
          ></ListingCard>
        ))}
    </div>
  );
}

export default Homepage;
