import { SignOutButton } from "@clerk/clerk-react";
import { Link } from "react-router-dom";
import { IoSearch } from "react-icons/io5";
import { useRef } from "react";

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
        {/* Search Bar */}
        <div className="md:hidden"> 
          <IoSearch />
        </div>

        <div className="hidden md:flex md:flex-grow">
          <input
            type="text"
            placeholder="Search..."
            className="w-full max-w-sm px-3 py-2 rounded-full border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-400"
          />
        </div>

        {/* Nav Links with Buttons */}
        <div className="relative flex items-center w-full sm:w-auto">
          {/* Left Scroll Button */}
          <button
            onClick={() => scrollNav("left")}
            className="sm:hidden absolute left-0 top-1/2 transform -translate-y-1/2  text-black bg-white rounded-full p-2 shadow-md z-10"
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
            className="sm:hidden absolute right-0 top-1/2 transform -translate-y-1/2 bg-white text-black rounded-full p-2 shadow-md z-10"
          >
            &gt;
          </button>
        </div>
      </div>
    </header>
  );
}
