/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}", "client/src/App.tsx"],
  theme: {
    extend: {
      fontFamily: {
        kodchasan: ["Kodchasan", "sans-serif"],
        "ibm-plex-sans": ["IBM Plex Sans", "sans-serif"],
      },
      margin: {
        "30px": "30px",
      },
    },
  },
  plugins: [],
};
