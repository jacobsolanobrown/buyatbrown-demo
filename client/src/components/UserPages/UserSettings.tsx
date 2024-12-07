import React from "react";
import ListingCard from "../ListingCard";
import NavUser from "./NavUser";
import DropdownNavUser from "./DropdownNavUser";

export default function UserSettings() {
  const handleCardClick = () => {
    alert("more info");
  };
  return (
    <div className="min-h-screen flex bg-gradient-to-r from-blue-100 to-pink-100">
    
        {/* Sidebar for larger screens */}
        <div className="hidden sm:block">
          <NavUser />
        </div>

      
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
        
        <div className="bg-white/50 p-6 rounded-xl shadow-md border">
          <h2 className="text-2xl font-bold mb-5">Settings</h2>
            <form>
              <div>
                  <label className="block mb-5 text-lg font-medium text-gray-900 dark:text-white">Change Username:</label>
                  <input type="text" id="first_name" 
                  className="bg-white border border-gray-200 text-gray-900 
                  text-sm rounded-full focus:ring-blue-500 focus:border-blue-500 
                  block p-2.5 w-80 dark:bg-gray-700 dark:border-gray-600
                  dark:placeholder-gray-400 dark:text-white
                    dark:focus:ring-blue-500 dark:focus:border-blue-500 mb-5 text-center" 
                    placeholder="Enter new username..." required />
              </div>
            </form>
            <h2 className="block mb-5 text-lg font-medium text-gray-900 dark:text-white">Delete Account:</h2>
            <button className ="bg-yellow-300 w-80 rounded-full text-white font-bold p-2.5 text-center">Yes, permanetely delete my account.</button>
        </div>


      </div>
    </div>
  );
}