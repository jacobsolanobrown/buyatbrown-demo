import { http, HttpResponse } from 'msw';

const listings = [
  {
    id: '1',
    listingId: '1',
    title: 'Vintage Jane Austen Book Set',
    description: 'A beautiful set of Jane Austen novels. Perfect for any book lover.',
    price: 50,
    category: 'School',
    imageUrl: '/src/assets/JaneAustenCover.jpeg',
    username: 'janeaustenfan',
    userId: 'user1',
    email: 'jane.doe@brown.edu',
    condition: 'Like New',
    tags: 'books, classic, literature',
    date: new Date('2024-09-01T12:00:00Z'),
  },
  {
    id: '2',
    listingId: '2',
    title: 'Comfy Office Chair',
    description: 'Ergonomic office chair, great for long study sessions. Almost new.',
    price: 75,
    category: 'Furniture',
    imageUrl: '/src/assets/comfy_chair_selling.jpg',
    username: 'studious_student',
    userId: 'user2',
    email: 'john.smith@brown.edu',
    condition: 'Used',
    tags: 'office, chair, furniture',
    date: new Date('2024-09-02T14:30:00Z'),
  },
  {
    id: '3',
    listingId: '3',
    title: 'Mini Fridge',
    description: 'A small mini fridge, perfect for a dorm room. Works great.',
    price: 40,
    category: 'Kitchen',
    imageUrl: '/src/assets/mini_fridge_selling.jpg',
    username: 'dormlife',
    userId: 'user1',
    email: 'jane.doe@brown.edu',
    condition: 'Used',
    tags: 'kitchen, fridge, dorm',
    date: new Date('2024-09-03T10:00:00Z'),
  },
  {
    id: '4',
    listingId: '4',
    title: 'iPhone 13 - Mint Condition',
    description: 'Unlocked 512gb iPhone 13 Pro. Absolutely mint. Looks brand new basically. I think I also have the box as well. If post is up, still available. No trades.',
    price: 600,
    category: 'Tech',
    imageUrl: '/src/assets/iphone_13_mint_condition_selling.jpg',
    username: 'techguru',
    userId: 'user3',
    email: 'tech.guru@brown.edu',
    condition: 'Like New',
    tags: 'iphone, apple, smartphone',
    date: new Date('2024-10-01T10:00:00Z'),
  },
  {
    id: '5',
    listingId: '5',
    title: 'Red 1966 Ford F-100',
    description: '1966 F-100 4x4 for sale. Starts and runs well. Four speed manual. 352 Cu. In. V8. Heater works. Lights and blinkers work. No leaks. Odomoter reads 48000, but I do not know if it has turned over. Have most of the chrome pieces in the garage. They will go with the truck. Selling because I got another truck. If this post is up, the truck is still available. NO TRADES',
    price: 9500,
    category: 'Misc',
    imageUrl: '/src/assets/red_truck_selling.jpg',
    username: 'ford-guy',
    userId: 'user4',
    email: 'ford.guy@brown.edu',
    condition: 'New',
    tags: 'toy, truck, kids',
    date: new Date('2024-10-02T11:00:00Z'),
  },
  {
    id: '6',
    listingId: '6',
    title: 'Dining Table Set',
    description: 'A wooden dining table with four chairs. In good condition.',
    price: 150,
    category: 'Furniture',
    imageUrl: '/src/assets/table_set_selling.jpg',
    username: 'homebody',
    userId: 'user5',
    email: 'home.body@brown.edu',
    condition: 'Used',
    tags: 'table, chairs, dining',
    date: new Date('2024-10-03T12:00:00Z'),
  },
  {
    id: '7',
    listingId: '7',
    title: 'Vintage Brown Leather Sandals',
    description: 'Rare and cool platform shoes! Can be dressed down or up :) message a day and time to pick up :) ',
    price: 25,
    category: 'Clothes',
    imageUrl: '/src/assets/vintage_brown_leather_sandals_selling.jpg',
    username: 'fashionista',
    userId: 'user6',
    email: 'fashion.ista@brown.edu',
    condition: 'Used',
    tags: 'sandals, shoes, vintage',
    date: new Date('2024-10-04T13:00:00Z'),
  },
  {
    id: '8',
    listingId: '8',
    title: 'Xbox One S Console',
    description: 'I upgraded and don’t use anymore. Works great and comes with hdmi cord and a controller',
    price: 120,
    category: 'Tech',
    imageUrl: '/src/assets/xbox_selling.jpg',
    username: 'gamer',
    userId: 'user7',
    email: 'gamer@brown.edu',
    condition: 'Used',
    tags: 'xbox, gaming, console',
    date: new Date('2024-10-05T14:00:00Z'),
  }
];

export const handlers = [
  http.get('http://localhost:3232/list-listings', ({ request }) => {
    const url = new URL(request.url);
    const uid = url.searchParams.get('uid');
    if (uid) {
      const userListings = listings.filter(listing => listing.userId === uid);
      return HttpResponse.json({
        response_type: 'success',
        listings: userListings,
      });
    }
    return HttpResponse.json({
      response_type: 'success',
      listings: [], // Return empty array if no uid
    });
  }),

  http.get('http://localhost:3232/list-all-listings', ({ request, params, cookies }) => {
    return HttpResponse.json({
        response_type: 'success',
        listings: listings,
      })
  }),

  http.get('http://localhost:3232/filter-listings', ({ request }) => {
    const url = new URL(request.url);
    const titleDescriptionKeyword = url.searchParams.get('titleDescriptionKeyword')?.toLowerCase();
    const categoryKeyword = url.searchParams.get('categoryKeyword')?.toLowerCase();
    const tagKeywords = url.searchParams.get('tagKeywords')?.toLowerCase();
    const conditionKeywords = url.searchParams.get('conditionKeywords')?.toLowerCase();

    let filteredListings = listings;

    if (titleDescriptionKeyword && titleDescriptionKeyword !== 'ignore') {
      filteredListings = filteredListings.filter(listing =>
        listing.title.toLowerCase().includes(titleDescriptionKeyword) ||
        listing.description.toLowerCase().includes(titleDescriptionKeyword)
      );
    }

    if (categoryKeyword && categoryKeyword !== 'ignore') {
      filteredListings = filteredListings.filter(listing =>
        listing.category.toLowerCase() === categoryKeyword
      );
    }

    if (tagKeywords && tagKeywords !== 'ignore') {
      const tags = tagKeywords.split(',').map(tag => tag.trim());
      filteredListings = filteredListings.filter(listing =>
        tags.some(tag => listing.tags.toLowerCase().includes(tag))
      );
    }

    if (conditionKeywords && conditionKeywords !== 'ignore') {
      const conditions = conditionKeywords.split(',').map(cond => cond.trim());
      filteredListings = filteredListings.filter(listing =>
          conditions.some(cond => listing.condition.toLowerCase().includes(cond))
      );
    }

    return HttpResponse.json({
      response_type: 'success',
      filtered_listings: filteredListings,
    });
  }),
];