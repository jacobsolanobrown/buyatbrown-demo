import { initializeApp } from "firebase/app";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
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
import {
  SignedIn,
  SignedOut,
  SignInButton,
} from "@clerk/clerk-react";

const firebaseConfig = {
  apiKey: process.env.API_KEY,
  authDomain: process.env.AUTH_DOMAIN,
  projectId: process.env.PROJECT_ID,
  storageBucket: process.env.STORAGE_BUCKET,
  messagingSenderId: process.env.MESSAGING_SENDER_ID,
  appId: process.env.APP_ID,
};

initializeApp(firebaseConfig);

function App() {
  return (
    <div className="App">
      <SignedOut>
        <SignInButton />
      </SignedOut>

      <SignedIn>
        <Router>
          <Navbar />
          <Routes>
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
        </Router>
      </SignedIn>
    </div>
  );
}

export default App;


