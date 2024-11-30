import {
    SignOutButton,
  } from "@clerk/clerk-react";
  import { Link } from "react-router-dom";
  
  export default function Navbar() {
    return (
      <header className="sticky top-0 z-50 bg-red-600 shadow-md">
        {/* App name and sign out */}
        <div className="flex justify-between items-center px-6 py-3">
          <div className="text-2xl font-bold text-white">Buy@Brown</div>
          <div className="flex items-center space-x-4">
            <span className="text-white">username</span>
            <SignOutButton />
          </div>
        </div>
  
        
        <div className="flex items-center justify-between px-6 py-2">
          {/* Search Bar */}
          <div className="flex-grow">
            <input
              type="text"
              placeholder="Search..."
              className="w-full max-w-sm px-3 py-2 rounded-full border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-400"
            />
          </div>
  
          {/* Nav Links */}
          <nav className="flex justify-center gap-6 ml-6">
            <Link to="/clothes" className="text-white underline hover:text-stone-200">
              Clothes
            </Link>
            <Link to="/tech" className="text-white underline hover:text-stone-200">
              Tech
            </Link>
            <Link to="/school" className="text-white underline hover:text-stone-200">
              School
            </Link>
            <Link to="/furniture" className="text-white underline hover:text-stone-200">
              Furniture
            </Link>
            <Link to="/kitchen" className="text-white underline hover:text-stone-200">
              Kitchen
            </Link>
            <Link to="/bathroom" className="text-white underline hover:text-stone-200">
              Bathroom
            </Link>
            <Link to="/misc" className="text-white underline hover:text-stone-200">
              Misc
            </Link>
          </nav>
        </div>
      </header>
    );
  }
  
  