import React from "react";

interface CardProps {
  imageUrl: string;
  title: string;
  price: string;
  username: string;
  description: string;
  condition: string;
  category: string;
  tags: string;
  listingId: string;
  userId: string;
  email: string;
  onClick: () => void;
}

const ListingCard: React.FC<CardProps> = ({
  imageUrl,
  title,
  price,
  username,
  category,
  condition,
  tags,
  onClick,
}) => {
  return (
    <div 
      aria-label="post"
      onClick={onClick}
      className="cursor-pointer w-64 bg-white shadow-lg rounded-xl overflow-hidden border border-gray-200 hover:shadow-2xl transition-shadow duration-300"
    >
      <div aria-label="photo" className="relative w-full h-48 bg-gray-100">
        <img
          src={imageUrl}
          alt={title}
          className="w-full h-full object-cover"
        />
      </div>
      <div aria-label="post details" className="p-4 text-left">
        <h2 className="text-xl font-bold text-gray-800 truncate">{title}</h2>
        <p className="text-gray-600 font-medium">${price}</p>
        <p className="text-sm font-light text-blue-800">by {username}</p>
      </div>
    </div>
  );
};

export default ListingCard;
