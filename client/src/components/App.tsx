import { useState, useEffect } from "react";
import { initializeApp } from "firebase/app";
import { Routes, Route, useNavigate } from "react-router-dom";
import Clothes from "./Tabs/Clothes";
import Tech from "./Tabs/Tech";
import Bathroom from "./Tabs/Bathroom";
import Furniture from "./Tabs/Furniture";
import Kitchen from "./Tabs/Kitchen";
import Misc from "./Tabs/Misc";
import Navbar from "./Navbar";
import Homepage from "./Homepage";
import School from "./Tabs/School";
import UserFavorites from "./UserPages/UserFavorites";
import UserListings from "./UserPages/UserListings";
import UserMessages from "./UserPages/UserMessages";
import UserSettings from "./UserPages/UserSettings";
import SignInPage from "./SignInPage";
import ListingForm from "./ListingForm";
import {
  SignedIn,
  SignedOut,
  useUser,
  SignOutButton,
} from "@clerk/clerk-react";
import { createUser, getUser } from "../utils/api";
import "/src/index.css";
import SearchResultsPage from "./Search/SearchResultsPage";
import { FaArrowLeft } from "react-icons/fa";

// import { P } from "@clerk/clerk-react/dist/useAuth-D1ySo1Ar";

const firebaseConfig = {
  apiKey: process.env.API_KEY,
  authDomain: process.env.AUTH_DOMAIN,
  projectId: process.env.PROJECT_ID,
  storageBucket: process.env.STORAGE_BUCKET,
  messagingSenderId: process.env.MESSAGING_SENDER_ID,
  appId: process.env.APP_ID,
};

const app = initializeApp(firebaseConfig);
// const db = getFirestore(app);
// export default db;

function App() {
  const { user } = useUser();

  const [uid, setUID] = useState("");
  const [username, setUsername] = useState("");
  const [isUsernameSet, setIsUsernameSet] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [createUsernameLoad, setCreateUsernameLoad] = useState(false);
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  /**
   * This function is used to check if the user has a username set. If the user has a username set, then we set the state to true and set the username.
   * If the user does not have a username set, then we set the state to false, and the user will be redirected to create a username.
   */
  const checkForUsername = async () => {
    if (user) {
      setLoading(true); // Start loading the account details
      try {
        // The call to the server to check if the user has a username set
        const jsonResponse = await getUser(user.id);
        if (jsonResponse.response_type === "success") {
          const usernameExists = jsonResponse.exists; // Boolean if the username exists
          const fetchedUsername = jsonResponse.username; // The string if the username was set
          if (usernameExists && fetchedUsername) {
            setIsUsernameSet(true);
            setUsername(fetchedUsername);
          } else {
            // the username has not been set
            setIsUsernameSet(false);
          }
        } else {
          // This is the case where the user has inputted an empty string as their username

          setIsUsernameSet(false);
        }
      } catch (error) {
        console.error("Error checking username: ", error);
        setIsUsernameSet(false);
      } finally {
        setLoading(false); // Stop loading the account details
      }
    }
  };

  /**
   * Check for username on app load or when the user changes.
   */
  useEffect(() => {
    if (user) {
      setUID(user.id)
      checkForUsername();
    }
  }, [user]); // Run whenever the `user` object changes

  /**
   * This function is used to create a new user in the database using the server's createUser handler.
   * On success it returns the userID and the username of the newly created user.
   *
   * @param e This is the event from submitting the form
   */
  const handleCreateUsernameSubmit = async (e: React.FormEvent) => {
    e.preventDefault(); // prevent the page from refreshing
    setCreateUsernameLoad(true); // set loading state to true
    if (username && user) {
      await createUser(user.id, username, user.emailAddresses[0].emailAddress)
        .then((data) => {
          if (data.response_type === "success") {
            setIsUsernameSet(true);
            navigate("/");
          } else {
            // for username already taken
            console.error("Failed to create username: ", data.error);
          }
        })
        // general api error catching
        .catch((error) => {
          console.error("Error creating user: ", error);
          setErrorMessage(
            "Error creating user. Please try again. (Error:  " + error + ")"
          );
        })
        .finally(() => {
          setCreateUsernameLoad(false); // set loading state to false
        });
    }
  };

  return (
    <div className="App ">
      <SignedOut>
        <SignInPage />
      </SignedOut>
      <SignedIn>
        {loading ? (
          <div className="flex justify-center items-center min-h-screen bg-gray-100">
            <div className="text-center">
              <img
                className="max-w-24"
                src="src/assets/Spin@1x-1.0s-200px-200px.gif"
                alt="Loading Image"
              />
            </div>
          </div>
        ) : !isUsernameSet ? (
          <div className="flex flex-col justify-center items-center min-h-screen bg-slate-100 bg-gradient-to-r from-blue-200 to-pink-200">
            <div className="flex flex-col justify-center items-center bg-white/50 rounded-3xl p-16 shadow-lg space-y-8">
              <button
                type="submit"
                className="text-xl bg-yellow-500 hover:text-yellow-500 hover:bg-white border border-yellow-500 text-white font-ibm-plex-sans font-bold py-3 px-8 rounded-3xl"
              >
                <SignOutButton> Cancel </SignOutButton>
              </button>
              <img
                src="src/assets/brown-university-logo-transparent.png"
                alt="Brown University Logo"
                className="h-48"
              />
              <h1 className="p-4 text-red-600	text-6xl font-kodchasan font-semibold">
                BUY @ BROWN
              </h1>
              <h2 className="p-4 text-3xl font-ibm-plex-sans text-center">
                Create a username to start selling now!
                <br />
                (Other users will see this username on listings)
              </h2>
              <form
                onSubmit={handleCreateUsernameSubmit}
                className="flex flex-col items-center rounded-3xl space-y-8"
              >
                <input
                  placeholder="Type your username"
                  id="username"
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="text-2xl font-ibm-plex-sans py-4 px-20 rounded-3xl"
                  required
                />
                {createUsernameLoad ? (
                  <img
                    className="w-16 block h-16"
                    src="src/assets/Spin@1x-1.0s-200px-200px.gif"
                    alt="Loading Image"
                  />
                ) : (
                  <div>
                    <button
                      type="submit"
                      className="text-2xl bg-red-600 hover:text-red-600 hover:bg-white border border-red-600 text-white font-ibm-plex-sans font-bold py-6 px-16 rounded-3xl"
                    >
                      Submit
                    </button>
                  </div>
                )}
              </form>
              {errorMessage && (
                <p className="p-4 text-3xl font-ibm-plex-sans text-center text-red-600">
                  {errorMessage}
                </p>
              )}
            </div>
          </div>
        ) : (
          <div className="font-ibm-plex-sans">
            <Navbar username={username} />
            <Routes>
              <Route path="/" element={<Homepage />} />
              <Route
                path="/listing-form"
                element={<ListingForm uid={uid} username={username} />}
              />
              <Route path="/clothes" element={<Clothes />} />
              <Route path="/tech" element={<Tech />} />
              <Route path="/bathroom" element={<Bathroom />} />
              <Route path="/kitchen" element={<Kitchen />} />
              <Route path="/misc" element={<Misc />} />
              <Route path="/school" element={<School />} />
              <Route path="/furniture" element={<Furniture />} />
              <Route
                path="/favorites"
                element={<UserFavorites username={username} />}
              />
              <Route
                path="/yourlistings"
                element={<UserListings username={username} />}
              />
              <Route
                path="/messages"
                element={<UserMessages username={username} />}
              />
              <Route
                path="/settings"
                element={<UserSettings username={username} />}
              />
              <Route path="/search-results" element={<SearchResultsPage />} />
            </Routes>
          </div>
        )}
      </SignedIn>
    </div>
  );
}

export default App;
