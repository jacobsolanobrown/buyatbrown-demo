import { SignOutButton } from "@clerk/clerk-react";
import { Link, useNavigate } from "react-router-dom";
import { IoSearch } from "react-icons/io5";
import { useRef, useState } from "react";
import { SearchBar } from "./Search/SearchBar";
import { filterListings } from "../utils/api";

export default function Navbar({ username }: { username: string }) {
  const navScrollRef = useRef<HTMLDivElement | null>(null);
  console.log("USERNAME: ", username);
  const scrollNav = (direction: "left" | "right") => {
    if (navScrollRef.current) {
      const scrollAmount = 150; // Adjust the scroll amount
      if (direction === "left") {
        navScrollRef.current.scrollBy({
          left: -scrollAmount,
          behavior: "smooth",
        });
      } else if (direction === "right") {
        navScrollRef.current.scrollBy({
          left: scrollAmount,
          behavior: "smooth",
        });
      }
    }
  };

  const [loading, setLoading] = useState(false);
  const [results, setResults] = useState([]);
  const navigate = useNavigate();

  const handleSearchSubmit = (term: string) => {
      setLoading(true);
      filterListings(term, "ignore", "ignore", "ignore")
          .then((data) => {
              setResults(data.filtered_listings || []);
              navigate('/search-results', { state: { searchTerm: term, filteredPosts: data.filtered_listings || []} });
          })
          .catch(console.error)
          .finally(() => setLoading(false));
  };

  const [showSearch, setShowSearch] = useState(false);



  return (
    <header className="sticky top-0 z-50 bg-red-600 shadow-md">
      {/* App name and sign out */}
      <div className="flex justify-between items-center px-6 py-3">
        <Link
          to="/"
          className="text-2xl text-white font-kodchasan font-semibold"
        >
          {" "}
          BUY@BROWN
        </Link>
        <div className="flex items-center space-x-4">
          {/* <p className="text-white">Account: </p> */}
          <Link to="/yourListings" className="text-white underline">
            Account: {username}
          </Link>
          <div className="text-white underline">
            <SignOutButton />
          </div>
        </div>
      </div>

      <div className="flex items-center justify-between px-6 py-2 pb-4">
        {/* Search Bar for small screens*/}
        <div className="md:hidden">
          {!showSearch ? (
            <button onClick={() => setShowSearch(true)}>
              <IoSearch size={24} />
            </button>
          ) : (
            <div className="flex flex-grow items-center">
              <SearchBar onSearchSubmit={handleSearchSubmit} />
              <button
                onClick={() => setShowSearch(false)}
                className="ml-2 text-red-500 font-bold"
              >
                X
              </button>
            </div>
          )}
        </div>
        
        {/* Search Bar for large screens*/}
        <div className="hidden md:flex md:flex-grow">
          <SearchBar onSearchSubmit={handleSearchSubmit} />
          {loading && <p>Searching...</p>}
        </div>

        {/* Nav Links with Buttons */}
        <div className="relative flex items-center w-full sm:w-auto">
          {/* Left Scroll Button */}
          <button
            onClick={() => scrollNav("left")}
            className="md:hidden absolute left-0 top-1/2 transform -translate-y-1/2  text-white p-2 z-10"
          >
            &lt;
          </button>

          {/* Nav Links */}
          <nav
            ref={navScrollRef}
            className="flex overflow-x-auto whitespace-nowrap scrollbar-hidden gap-6 mx-12"
          >
            <Link
              to="/clothes"
              className="text-white underline hover:text-stone-200"
            >
              Clothes
            </Link>
            <Link
              to="/tech"
              className="text-white underline hover:text-stone-200"
            >
              Tech
            </Link>
            <Link
              to="/school"
              className="text-white underline hover:text-stone-200"
            >
              School
            </Link>
            <Link
              to="/furniture"
              className="text-white underline hover:text-stone-200"
            >
              Furniture
            </Link>
            <Link
              to="/kitchen"
              className="text-white underline hover:text-stone-200"
            >
              Kitchen
            </Link>
            <Link
              to="/bathroom"
              className="text-white underline hover:text-stone-200"
            >
              Bathroom
            </Link>
            <Link
              to="/misc"
              className="text-white underline hover:text-stone-200"
            >
              Misc
            </Link>
          </nav>

          {/* Right Scroll Button */}
          <button
            onClick={() => scrollNav("right")}
            className="md:hidden absolute right-0 top-1/2 transform -translate-y-1/2 text-white p-2"
          >
            &gt;
          </button>
        </div>
      </div>
    </header>
  );
}
