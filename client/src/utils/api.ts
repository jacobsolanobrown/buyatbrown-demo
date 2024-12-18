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

export async function clearUser(userID: string){
  return await queryAPI("clear-user", { uid: userID });
}

// This function is used to get all the user listings using the server's listAllUserListings handler
export async function getAllListings() {
  return await queryAPI("list-all-listings", {
  });
}

// This function is used to filter through all the listings by title/description, categorgy, tag, or condition 
export async function filterListings(titleDescriptionKeyword: string, categoryKeyword: string, tagKeywords: string, conditionKeywords: string) {
  return await queryAPI("filter-listings", {
    titleDescriptionKeyword: titleDescriptionKeyword, 
    categoryKeyword: categoryKeyword,
    tagKeywords: tagKeywords,
    conditionKeywords: conditionKeywords, 
  });
}

export async function getUserListings(uid: string) {
  return await queryAPI("list-listings", {
    uid: uid,
  });
}

export async function getUserFavorites(uid: string) {
  return await queryAPI("list-user-favorites", {
    uid: uid,
  });
}

export async function addToFavorites(uid: string, listingID: string) {
  return await queryAPI("like-listings", {
    uid: uid,
    listingId: listingID,
  });
}

export async function removeFromFavorites(uid: string, listingID: string) {
  return await queryAPI("remove-liked-listing", {
    uid: uid,
    listingId: listingID,
  });
}

export async function deleteListing(uid: string, listingId: string) {
  return await queryAPI("delete-listings", {
    uid: uid,
    listingId: listingId,
  });
}