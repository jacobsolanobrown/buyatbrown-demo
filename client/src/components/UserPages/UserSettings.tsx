import React from "react";
import ListingCard from "../ListingCard";
import NavUser from "./NavUser";
import DropdownNavUser from "./DropdownNavUser";
import { useUser } from "@clerk/clerk-react";
import { createUser } from "../../utils/api";

export default function UserSettings({ username }: { username: string }) {
  const [newUsername, setUsername] = React.useState("");
  const [message, setMessage] = React.useState("");
  const { user } = useUser();

  /**
   * This function is used to create a new user in the database using the server's createUser handler.
   * On success it returns the userID and the username of the newly created user.
   *
   * @param e This is the event from submitting the form
   */
  const handleChangeUsername = async (e: React.FormEvent) => {
    e.preventDefault(); // prevent the page from refreshing
    if (username && user) {
      await createUser(user.id, newUsername, user.emailAddresses[0].emailAddress)
        .then((data) => {
          if (data.response_type === "success") {
            setUsername(newUsername);
            setMessage("Username changed successfully! Refresh the page to see the changes.");

          } else {
            // for username already taken
            setMessage(
              "Username is already taken. Please try another one."
            );
          }
        })
        // general api error catching
        .catch((error) => {
          console.error("Error creating user: ", error);
          setMessage(
            "Error creating user. Please try again. (Error:  " + error + ")"
          );
        });
    }
  };

  return (
    <div className="min-h-screen flex bg-gradient-to-r from-blue-100 to-pink-100">
      {/* Sidebar for larger screens */}
      <div className="hidden sm:block">
        <NavUser />
      </div>

      <div className="flex-1 p-8">
        <div className="flex flex-col items-center sm:block">
          <div className="bg-red-600 w-96 text-white py-4 px-6 rounded-lg shadow-md mb-6">
            <h1 className="text-lg font-semibold text-center">
              Hey, {username}!
            </h1>
          </div>
        </div>

        {/* Dropdown for smaller screens */}
        <DropdownNavUser></DropdownNavUser>

        <div className="bg-white/50 p-6 rounded-xl shadow-md border">
          <h2 className="text-2xl font-bold mb-5">Settings</h2>
          <form onSubmit={handleChangeUsername}>
            <div>
              <label className="block mb-5 text-lg font-medium text-gray-900 ">
                Change Username:
              </label>
              <div className="flex flex-row space-x-4">
                <input
                  id="changedUsername"
                  type="text"
                  value={newUsername}
                  onChange={(e) => setUsername(e.target.value)}
                  className="
                  text-sm rounded-full focus:ring-red-500 focus:border-red-500 
                  block p-3 w-80 dark:bg-gray-200 dark:border-gray-600
                  dark:placeholder-gray-400 
                    dark:focus:ring-red-500 dark:focus:border-red-500 mb-5 text-center"
                  placeholder="Enter new username..."
                  required
                />
                <button
                  type="submit"
                  className=" text-sm rounded-full block p-3 w-40  mb-5 text-center bg-red-600 hover:text-red-500 hover:bg-white border border-red-600 text-white font-ibm-plex-sans font-bold"
                >
                  Submit
                </button>
              </div>
              // TODO automatically refresh for the user
              {message && (
                <p className="py-4 font-ibm-plex-sans text-red-600">{message}</p>
              )}
            </div>
          </form>

          <h2 className="block mb-5 text-lg font-medium text-gray-900 ">
            Delete Account:
          </h2>
          <button className="bg-yellow-500 w-80 rounded-full text-white font-ibm-plex-sans  font-bold p-2.5 text-center hover:bg-white border hover:text-yellow-500 hover:border-yellow-500">
            Yes, Delete My Account
          </button>
        </div>
      </div>
    </div>
  );
}
