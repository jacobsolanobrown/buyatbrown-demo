import { useState, useEffect } from "react";
import { initializeApp } from "firebase/app";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  useNavigate,
} from "react-router-dom";
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
import { SignedIn, SignedOut, useUser } from "@clerk/clerk-react";
import { createUser } from "../utils/api";
import "/src/index.css";

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
  const [username, setUsername] = useState("");
  const [isUsernameSet, setIsUsernameSet] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [checkingUsername, setCheckingUsername] = useState(false);
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (checkingUsername) {
      console.log("LOADING?: ", checkingUsername);
  }}, []);

  /**
   * This function is used to create a new user in the database using the server's createUser handler.
   * On success it returns the userID and the username of the newly created user.
   *
   * @param e This is the event from submitting the form
   */
  const handleUserSubmit = async (e: React.FormEvent) => {
    setCheckingUsername(true);
    e.preventDefault(); // prevent the page from refreshing
    console.log("Checking Username Availability");
    console.log("LOADING?: ", checkingUsername); 
    setLoading(true);
    console.log("LOADING?: ", loading);
    if (username && user) {
      await createUser(user.id, username, user.emailAddresses[0].emailAddress)
        .then((data) => {
          if (data.response_type === "success") {
            setCheckingUsername(false);
            console.log("LOADING?: ", checkingUsername); /// TODO
            // remap the markers to the lat and long
            setIsUsernameSet(true);
            navigate("/");
          } else {
            // for username already taken
            console.error("Failed to create username: ", data.error);
            setErrorMessage(
              "Username is already taken. Please try another one."
            );
            // alert("Failed to create username " + data.error);
          }
        })
        // general api error catching
        .catch((error) => {
          setCheckingUsername(false);
          console.log("LOADING?: ", checkingUsername);
          console.error("Error creating user: ", error);
          setErrorMessage(
            "Error creating user. Please try again. (Error:  " + error + ")"
          );
        });
    }
  };

  return (
    <div className="App ">
      <SignedOut>
        <SignInPage />
      </SignedOut>
      <SignedIn>
        {!isUsernameSet ? (
          // If the user is set, then we go to the homepage, otherwise we go to the createusername page
          <div className="flex flex-col justify-center items-center min-h-screen bg-slate-100 bg-gradient-to-r from-blue-200 to-pink-200">
            <div className="flex flex-col justify-center items-center bg-white/50 rounded-3xl p-16 shadow-lg space-y-8">
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
                onSubmit={handleUserSubmit}
                className="flex flex-col items-center rounded-3xl space-y-8"
              >
                <input
                  placeholder="Type your username..."
                  id="username"
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="text-2xl font-ibm-plex-sans py-4 px-12 rounded-3xl"
                  required
                />
                <button
                  type="submit"
                  className="text-2xl bg-red-600 hover:text-red-600 hover:bg-white border border-red-600 text-white font-ibm-plex-sans font-bold py-6 px-10 rounded-3xl"
                >
                  Submit
                </button>
              </form>
              {errorMessage && (
                <p className="p-4 text-3xl font-ibm-plex-sans text-center text-red-600">
                  {errorMessage}
                </p>
              )}
            </div>
          </div>
        ) : (
          <div>
            <Navbar />
            <h1 className="text-center text-2xl font-bold text-red-600">
              THIS IS THE USER'S USERNAME: {username}{" "}
            </h1>
            <Routes>
              <Route path="/" element={<Homepage />} />
              <Route path="/" element={<Homepage />} />
              <Route path="/clothes" element={<Clothes />} />
              <Route path="/tech" element={<Tech />} />
              <Route path="/bathroom" element={<Bathroom />} />
              <Route path="/kitchen" element={<Kitchen />} />
              <Route path="/misc" element={<Misc />} />
              <Route path="/school" element={<School />} />
              <Route path="/furniture" element={<Furniture />} />
              <Route path="/favorites" element={<UserFavorites />} />
              <Route path="/yourlistings" element={<UserListings />} />
              <Route path="/messages" element={<UserMessages />} />
              <Route path="/settings" element={<UserSettings />} />
            </Routes>
          </div>
        )}
      </SignedIn>
    </div>
  );
}

export default App;
