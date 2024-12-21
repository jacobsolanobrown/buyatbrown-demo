import { SignInButton } from "@clerk/clerk-react";
export default function SignInPage() {
  return (
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

        <h2 className="p-4 text-3xl font-ibm-plex-sans">
          Buy & Sell Exclusively at Brown By Students, For Students
        </h2>

        <SignInButton>
          <button className=" text-2xl bg-red-600 hover:text-red-600 hover:bg-white border border-red-600 text-white font-ibm-plex-sans font-bold py-6 px-10 rounded-3xl">
            Sign in with Clerk
          </button>
        </SignInButton>
      </div>
    </div>
  );
}