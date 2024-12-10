const HOST = "http://localhost:3232";

async function queryAPI(
  endpoint: string,
  query_params: Record<string, string>
) {
  // query_params is a dictionary of key-value pairs that gets added to the URL as query parameters
  // e.g. { foo: "bar", hell: "o" } becomes "?foo=bar&hell=o"
  const paramsString = new URLSearchParams(query_params).toString();
  const url = `${HOST}/${endpoint}?${paramsString}`;
  const response = await fetch(url);
  if (!response.ok) {
    console.error(response.status, response.statusText);
  }
  return response.json();
}

// This function is used to create a new user in the database using the server's createUser handler
export async function createUser(uid: string, username: string, email: string) {
  return await queryAPI("create-user", {
    uid: uid,
    username: username,
    email: email,
  });
}

// This function is used to get a user's username from their userID. It will also return a boolean regarding whether 
// the user already has set a username or not. 
export async function getUser(userID: string) {
  return await queryAPI("query-username", { uid: userID });
}