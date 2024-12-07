import { SignInButton } from "@clerk/clerk-react";
export default function SignInPage() {
    return (
      <div className="flex flex-col justify-center items-center min-h-screen bg-slate-100 bg-gradient-to-r from-blue-200 to-pink-200">
        <div className="flex flex-col justify-center items-center bg-white/70 rounded-lg p-16 shadow-lg space-y-6">
          <img
            src="src/assets/brown-university-logo-transparent.png"
            alt="Brown University Logo"
            className="h-48"
          />
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
      </div>
    );

}
