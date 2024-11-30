import React from "react";

interface CardProps {
  imageUrl: string;
  title: string;
  price: string;
  username: string;
  description: string;
  onClick: () => void;
}

const ListingCard: React.FC<CardProps> = ({ imageUrl, title, price, username, description, onClick }) => {
  return (
    <div
      onClick={onClick}
      className="cursor-pointer w-64 bg-white shadow-lg rounded-md overflow-hidden border border-gray-200 hover:shadow-2xl transition-shadow duration-300"
    >

      <div className="relative w-full h-48 bg-gray-100">
        <img
          src={imageUrl}
          alt={title}
          className="w-full h-full object-cover"
        />
      </div>


      <div className="p-4 text-center">
        <h2 className="text-lg font-bold text-gray-800 truncate">{title}</h2>
        <p className="text-gray-600 font-medium">{price}</p>
        <p className="text-sm text-gray-500">by {username}</p>
      </div>
    </div>
  );
};

export default ListingCard;
