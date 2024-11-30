import React from "react";
import ListingCard from "../ListingCard";
import NavUser from "./NavUser"

export default function UserMessages() {
  const handleCardClick = () => {
    alert("more info");
  };

  return (
    <div>
        <NavUser></NavUser>
    </div>
  );
}