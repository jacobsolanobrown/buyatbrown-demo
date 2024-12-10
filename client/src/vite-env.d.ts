interface ImportMetaEnv {
  VITE_APP_NODE_ENV: string;
  // define more env variables if needed
}

interface ImportMeta {
  env: ImportMetaEnv;
}


interface ImportMetaEnv {
  readonly VITE_CLERK_PUBLISHABLE_KEY: string;
  // Add other environment variables as needed
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}