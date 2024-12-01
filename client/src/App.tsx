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
import { SignedIn, SignedOut, SignInButton } from "@clerk/clerk-react";
import "./index.css";

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
        <div className="flex flex-col justify-center items-center min-h-screen bg-slate-100">
          <h1 className="p-4 text-red-600	text-6xl font-kodchasan font-semibold">
            BUY @ BROWN
          </h1>

          <h2 className="p-4 text-2xl">
            Buy & Sell Exclusively at Brown By Students, For Students
          </h2>

          <SignInButton>
            <button className="p-4 text-2xl bg-red-600 hover:text-red-600 hover:bg-white border border-red-600 text-white font-bold py-4 px-8 rounded-lg">
              Sign in with Clerk
            </button>
          </SignInButton>
        </div>
      </SignedOut>

      <SignedIn>
        <Router>
          <Navbar />
          <p className="text-center text-2xl font-bold text-red-600">
            Welcome to Buy@Brown
          </p>
          <Routes>
            <Route path="/" element={<Homepage />} />
            <Route path="/clothes" element={<Clothes />} />
            <Route path="/tech" element={<Tech />} />
            <Route path="/bathroom" element={<Bathroom />} />
            <Route path="/kitchen" element={<Kitchen />} />
            <Route path="/misc" element={<Misc />} />
            <Route path="/school" element={<School />} />
            <Route path="/furniture" element={<Furniture />} />
          </Routes>
        </Router>
      </SignedIn>
    </div>
  );
}

export default App;
