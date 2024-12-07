import { useState, useEffect } from "react";
import { initializeApp } from "firebase/app";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  useNavigate,
} from "react-router-dom";
import Clothes from "./components/Tabs/Clothes";
import Tech from "./components/Tabs/Tech";
import Bathroom from "./components/Tabs/Bathroom";
import Furniture from "./components/Tabs/Furniture";
import Kitchen from "./components/Tabs/Kitchen";
import Misc from "./components/Tabs/Misc";
import Navbar from "./components/Navbar";
import Homepage from "./components/Homepage";
import School from "./components/Tabs/School";
import UserFavorites from "./components/UserPages/UserFavorites";
import UserListings from "./components/UserPages/UserListings";
import UserMessages from "./components/UserPages/UserMessages";
import UserSettings from "./components/UserPages/UserSettings";
import SignInPage from "./components/SignInPage";
import { SignedIn, SignedOut, useUser } from "@clerk/clerk-react";
import "./index.css";

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
  const navigate = useNavigate();

  useEffect(() => {
    if (user) {
      const userUsername = user.publicMetadata.username;
      if (userUsername) {
        setIsUsernameSet(true);
      }
    }
  }, [user]);

  const handleUsernameSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (username) {
      await user?.update({
        unsafeMetadata: { username: username },
      });
      setIsUsernameSet(true);
      console.log("Username submitted:", username);
      navigate("/");
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
          <div className="username-form">
            <form onSubmit={handleUsernameSubmit}>
              <label htmlFor="username" className="block text-lg font-medium">
                Enter your username:
              </label>
              <input
                id="username"
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="mt-2 p-2 border rounded"
                required
              />
              <button
                type="submit"
                className="ml-2 p-2 bg-blue-500 text-white rounded"
              >
                Submit
              </button>
            </form>
          </div>
        ) : (
          // <p className="text-center text-2xl font-bold text-red-600">
          //   Welcome to Buy@Brown
          // </p>
          <div>
            <Navbar />
            <h1 className="text-center text-2xl font-bold text-red-600"
            >THIS IS THE USER'S USERNAME: {username} </h1>
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

    // <div className="App">
    //   <SignedOut>
    //     <SignInPage />
    //   </SignedOut>
    //   <SignedIn>
    //     <Router>
    //       <Navbar />
    //       <p className="text-center text-2xl font-bold text-red-600">
    //         Welcome to Buy@Brown
    //       </p>
    //       <Routes>
    //         <Route path="/" element={<Homepage />} />
    //         <Route path="/clothes" element={<Clothes />} />
    //         <Route path="/tech" element={<Tech />} />
    //         <Route path="/bathroom" element={<Bathroom />} />
    //         <Route path="/kitchen" element={<Kitchen />} />
    //         <Route path="/misc" element={<Misc />} />
    //         <Route path="/school" element={<School />} />
    //         <Route path="/furniture" element={<Furniture />} />
    //       </Routes>
    //     </Router>
    //   </SignedIn>
    // </div>
  );
}

export default App;
