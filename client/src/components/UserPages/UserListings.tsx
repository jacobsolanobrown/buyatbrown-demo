import React, { useEffect } from "react";
import ListingCard from "../ListingCard";
import NavUser from "./NavUser";
import { Link } from "react-router-dom";
import { useState } from "react";
import DropdownNavUser from "./DropdownNavUser";
import {useUser} from "@clerk/clerk-react";
import { getUserListings } from "../../utils/api";
import EditListingModal from "../EditListingModal";

export default function UserListings({ username }: { username: string }) {


  // const toggleDropdown = () => {
  //   setDropdownVisible(!dropdownVisible);
  // };

  const handleCardClick = (listing: any) => {
    setSelectedListing(listing);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedListing(null);
  };

  const [selectedListing, setSelectedListing] = useState<any | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [posts, setPosts] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const { user } = useUser();

  useEffect(() => {
    if (user) {
      getUserListings(user.id)
        .then((data) => {
          console.log("json response: ", data);
          console.log("listings: ", data.listings);
          if (data.response_type === "success" && Array.isArray(data.listings)) {
            setPosts(data.listings);
          } else {
            setPosts([]);
          }
        })
        .catch((err) => {
          console.error("Error fetching listings:", err);
        })
        .finally(() => setIsLoading(false));
    }
  }, []);

  return (
    <div className="min-h-screen flex bg-gradient-to-r from-blue-100 to-pink-100">
      {/* Sidebar for larger screens */}
      <div className="hidden min-[860px]:block">
        <NavUser />
      </div>

      {/* Main content */}
      <div className="flex-1 p-8">
        <div className="flex flex-col items-center min-[860px]:block">
          <div className="bg-red-600 w-96 text-white py-4 px-6 rounded-xl shadow-md mb-6">
            <h1 className="text-lg font-semibold text-center">
              Hey, {username}!
            </h1>
          </div>
        </div>

        {/* Dropdown for smaller screens */}
        <DropdownNavUser></DropdownNavUser>

        <div className="bg-white/50 p-6 rounded-lg shadow-md border">
          <h2 className="text-2xl font-bold mb-2">Your Listings</h2>
          <p className="text-sm text-gray-600 mb-6">
            Edit, or delete active/sold listings.
          </p>

          <div
            className="grid grid-cols-1 min-h-screen place-items-center 
             min-[1410px]:grid-cols-4 min-[860px]:grid-cols-2 
             min-[1110px]:grid-cols-3 gap-4
             min-[860px]:place-items-start min-[860px]:min-h-0
             "
          >
            {isLoading ? (
              <div className="text-2xl align-center">
                Loading All Listings...
              </div>
            ) : posts.length === 0 ? (
              <p>No listings available</p>
            ) : (
              (console.log("all user posts", posts)),
              (posts.map((post) => (

                <ListingCard
                  key={post.id}
                  imageUrl={post.imageUrl}
                  title={post.title}
                  price={post.price}
                  username={post.username}
                  description={post.description}
                  condition={post.condition}
                  category={post.category}
                  tags={post.tags}
                  listingId={post.listingId}
                  userId={post.userId}
                  onClick={() => handleCardClick(post)}
                />
              ))
            ))}
            {isModalOpen && (
              <EditListingModal
                isOpen={isModalOpen}
                onClose={handleCloseModal}
                listing={selectedListing}
              />
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
