import React from "react";
import ListingCard from "../ListingCard";
import NavUser from "./NavUser";
import DropdownNavUser from "./DropdownNavUser";
import { Link } from "react-router-dom";
import { useState } from "react";


export default function UserMessages() {

  const [dropdownVisible, setDropdownVisible] = useState(false);

  const toggleDropdown = () => {
    setDropdownVisible(!dropdownVisible);
  };

  return (
    <div className="min-h-screen flex bg-gradient-to-r from-blue-100 to-pink-100">
        {/* Sidebar for larger screens */}
        <div className="hidden sm:block">
          <NavUser />
        </div>

        {/* Main content */}
        <div className="flex-1 p-8">
          <div className="flex flex-col items-center sm:block">
          <div className="bg-red-500 w-96 text-white py-4 px-6 rounded-lg shadow-md mb-6">
            <h1 className="text-lg font-semibold text-center">
              Hey, username.meow!
            </h1>
          </div>
          </div>

        {/* Dropdown for smaller screens */}
        <DropdownNavUser></DropdownNavUser>


        <div className="bg-white/50 p-6 rounded-lg shadow-md border">
              <h2 className="text-2xl font-bold mb-2">Messages</h2>
        </div>
      </div>
    </div>
  );
}