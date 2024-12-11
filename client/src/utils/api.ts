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

// This function is used to get all the user listings using the server's listAllUserListings handler
export async function getAllListings() {
  return await queryAPI("list-all-listings", {
  });
}

// This function is used to filter through all the listings by title/description, categorgy, tag, or condition 
export async function filterListings(titleDesc: string, category: string, tag: string, condition: string) {
  return await queryAPI("filter-listings", {
    titleDesc: titleDesc, 
    category: category,
    tag:tag,
    condition: condition, 
  });
}