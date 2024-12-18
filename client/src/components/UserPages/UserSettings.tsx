import React from "react";
import ListingCard from "../ListingCard";
import NavUser from "./NavUser";
import DropdownNavUser from "./DropdownNavUser";
import { useUser } from "@clerk/clerk-react";
import { createUser, clearUser } from "../../utils/api";
import PulseLoader from "react-spinners/PulseLoader";

export default function UserSettings({ username }: { username: string }) {
  const [newUsername, setUsername] = React.useState("");
  const [usernameLoading, setUsernameLoading] = React.useState(false);
  const [usernameMessage, setUsernameMessage] = React.useState("");
  const [deleteLoading, setDeleteLoading] = React.useState(true);
  const [deleteMessage, setDeleteMessage] = React.useState("");
  const { user } = useUser();

  /**
   * This function is used to create a new user in the database using the server's createUser handler.
   * On success it returns the userID and the username of the newly created user.
   *
   * @param e This is the event from submitting the form
   */
  const handleChangeUsername = async (e: React.FormEvent) => {
    e.preventDefault(); // prevent the page from refreshing
    setUsernameLoading(true); // set loading state to true
    setUsernameMessage("Checking username availability... ");
    if (username && user) {
      await createUser(
        user.id,
        newUsername,
        user.emailAddresses[0].emailAddress
      )
        .then((data) => {
          if (data.response_type === "success") {
            setUsername(newUsername);
            setUsernameMessage(
              "Username changed successfully! Refresh the page to see the changes."
            );
            // automatically refresh the page
            window.location.reload();
          } else {
            // for username already taken
            setUsernameMessage(
              "Username is already taken. Please try another one."
            );
          }
        })
        // general api error catching
        .catch((error) => {
          console.error("Error creating user: ", error);
          setUsernameMessage(
            "Error creating user. Please try again. (Error:  " + error + ")"
          );
        })
        .finally(() => {
          setUsernameLoading(false); // set loading state to false
        });
    } else {
      setUsernameLoading(false);
      setUsernameMessage("User or username not available. Please try again.");
    }
  };

  const handleDeleteAccount = async () => {
    setDeleteLoading(true);
    setDeleteMessage("Deleting Account...");
    if (username && user) {
      await clearUser(user.id)
        .then((data) => {
          if (data.response_type === "success") {
            setDeleteMessage(
              "Account Successfully Deleted. You will be loggeed out."
            );
            // Sign out from Clerk
            // refresh
            window.location.reload();
            // Redirect to homepage
            //window.location.href = "/";
          } else {
            // for username already taken
            setDeleteMessage("Error deleting account. Please try again.");
          }
        })
        .catch((error) => {
          console.error("Error deleting account: ", error);
          setDeleteMessage(
            "Error deleting account. Please try again. (Error:  " + error + ")"
          );
        })
        .finally(() => {
          setDeleteLoading(false);
        });
    } else {
      setDeleteLoading(false);
      setDeleteMessage("User or username not available. Please try again.");
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
          <div className="bg-red-600 w-96 text-white py-4 px-6 rounded-xl shadow-md mb-6">
            <h1 className="text-lg font-semibold text-center">
              Hey, {username}!
            </h1>
          </div>
        </div>

        {/* Dropdown for smaller screens */}
        <DropdownNavUser></DropdownNavUser>

        <div className="bg-white/50 p-6 rounded-xl shadow-md border">
          <h2 className="text-2xl font-bold mb-5">Settings</h2>


          {/* This form is commented out as editing username do not change post usernames associated with that account */}

          {/* <form onSubmit={handleChangeUsername}>
            <div className="flex flex-col">
              <label className="block mb-5 text-lg font-medium text-gray-900 ">
                Change Username:
              </label>
              <div className="flex flex-row space-x-5">
                <input
                  id="changedUsername"
                  type="text"
                  value={newUsername}
                  onChange={(e) => setUsername(e.target.value)}
                  className="
                  text-md rounded-full focus:ring-red-500 focus:border-red-500 
                   p-3 w-80 h-12 dark:bg-gray-200 dark:border-gray-600
                  dark:placeholder-gray-400 dark:focus:ring-red-500 dark:focus:border-red-500 text-center"
                  placeholder="Enter new username"
                  required
                />
                {usernameLoading ? (
                  <div className="flex items-center justify-center">
                    <PulseLoader
                      color="#ED1C24"
                      margin={4}
                      size={16}
                      speedMultiplier={0.7}
                    />
                  </div>
                ) : (
                  <button
                    type="submit"
                    className="text-sm rounded-full block p-3 w-40 text-center bg-red-600 hover:text-red-500 hover:bg-white border border-red-600 text-white font-ibm-plex-sans font-bold"
                  >
                    Submit
                  </button>
                )}
              </div>
              <div>
                {usernameMessage && usernameLoading ? (
                  <p className="py-4 font-ibm-plex-sans text-red-600">
                    {usernameMessage}
                  </p>
                ) : (
                  <p className="py-4 font-ibm-plex-sans text-red-600">
                    {usernameMessage}
                  </p>
                )}
              </div>
            </div>
          </form> */}

          <div>
            <h2 className="block mt-3 mb-5 text-lg font-medium text-gray-900 ">
              Delete Account:
            </h2>
            <p className="text-sm text-red-600 mt-2 mb-3 ml-3">
              Warning: This action is irreversible.
            </p>
            <button
              className="bg-amber-950 w-80 rounded-full text-white font-ibm-plex-sans  font-bold p-2.5 text-center hover:bg-white border hover:text-amber-950 hover:border-amber-950"
              onClick={handleDeleteAccount}
            >
              Yes, Delete My Account
            </button>
            {deleteMessage && deleteLoading ? (
              <div className="mt-4 flex ml-32">
                <PulseLoader
                  color="#ED1C24"
                  margin={4}
                  size={10}
                  speedMultiplier={0.7}
                />
              </div>
            ) : (
              <p className="py-4 font-ibm-plex-sans text-red-600">
                {deleteMessage}
              </p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
