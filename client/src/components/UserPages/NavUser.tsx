import React from "react";
import { Link } from "react-router-dom";

export default function NavUser() {
  return (
    <div className="bg-red-500 text-white w-48 rounded-3xl shadow-lg p-4 mt-8 ml-8 space-y-6">
      <Link
        to="/"
        className="flex  justify-center space-x-2 text-sm font-medium hover:underline"
      >
        <span className="text-xl">←</span>
        <span>Back</span>
      </Link>

      <nav className="space-y-4 text-lg font-semibold text-center">
        <Link to="/yourlistings" className="block text-white underline hover:text-stone-200">
          Listings
        </Link>
        <Link to="/favorites" className="block text-white underline hover:text-stone-200">
          Favorites
        </Link>
        <Link to="/messages" className="block text-white underline hover:text-stone-200">
          Messages
        </Link>
        <Link to="/settings" className="block text-white underline hover:text-stone-200">
          Settings
        </Link>
      </nav>
    </div>
  );
}


