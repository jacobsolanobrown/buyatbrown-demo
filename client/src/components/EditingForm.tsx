import React, { useEffect, useState } from "react";
import { useUser } from "@clerk/clerk-react";
import { useNavigate, useLocation } from "react-router-dom";
import axios from "axios";

interface EditingPageProps {
  uid: string;
  listing?: {
    imageUrl: string;
    title: string;
    price: string;
    username: string;
    description: string;
    condition: string;
    category: string;
    tags: string;
    listingId: string;
  };
}

const EditingPage: React.FC<EditingPageProps> = ({ uid }) => {
  const location = useLocation();
  const { listing } = location.state || {}; // Destructure listing from state

  const navigate = useNavigate();
  const [responseMessage, setResponseMessage] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const [formData, setFormData] = useState({
    uid: uid || "",
    title: "",
    price: "",
    username: "",
    description: "",
    condition: "",
    category: "",
    tags: "",
    imageFile: null,
    listingId: "",
  });

  // Handle form input changes
  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  // Handle form image changes
  const handleImageChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const file = e.target.files[0];
    setFormData({ ...formData, imageFile: file });
  };

  // TODO: add a success screen for posting and allow the user to navigate to their listings

  useEffect(() => {
    // Fetch existing listing details
    const params = new URLSearchParams(window.location.search);
    const uid = params.get("uid") || "";
    const listingId = params.get("listingId") || "";

    setFormData((prevData) => ({
      ...prevData,
      uid,
      listingId,
    }));

    if (uid && listingId) {
      axios
        .get(`http://localhost:3232/get-listing`, {
          params: { uid, listingId },
        })
        .then((response) => {
          console.log("response does it go in here");
          const data = response.data;
          setFormData((prevData) => ({
            ...prevData,
            title: data.title || "",
            price: data.price || "",
            description: data.description || "",
            condition: data.condition || "",
            category: data.category || "",
            tags: data.tags || "",
          }));
        })
        .catch((error) => {
          console.error("Error fetching listing details:", error);
          setResponseMessage("Error fetching listing details.");
        });
    }
  }, []);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    setIsSubmitting(true);
    setResponseMessage("");

    axios
      .post(
        `http://localhost:3232/update-listings`,
        {},
        {
          params: {
            uid: formData.uid,
            listingId: formData.listingId,
            title: formData.title,
            price: formData.price,
            description: formData.description,
            condition: formData.condition,
            category: formData.category,
            tags: formData.tags,
          },
        }
      )
      .then((response) => {
        setResponseMessage(
          response.data.response_type === "success"
            ? "Listing updated successfully!"
            : `Error: ${response.data.error}`
        );
      })
      .catch((error) => {
        console.error("Error updating listing:", error);
        setResponseMessage("An error occurred while updating the listing.");
      })
      .finally(() => {
        setIsSubmitting(false);
      });
  };

  const goBack = () => {
    navigate("/yourlistings");
  };

  return (
    <div className="flex flex-col align-center items-center min-h-screen bg-gradient-to-r from-blue-100 to-pink-100">
      <p>{uid}</p>

      <div className="w-3/4 mx-12 my-14 p-8 rounded-3xl shadow-lg  bg-white/50 ">
        <button className="py-4 px-2 underline" onClick={goBack}>
          Cancel
        </button>
        <h1 className="text-3xl font-bold mb-6 ml-2">Edit Your Listing</h1>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label
              htmlFor="title"
              className="block text-xl font-medium text-gray-700 ml-3"
            >
              Current Listing Title: {listing?.title}
            </label>
            <input
              type="text"
              id="Listing Title"
              name="title"
              placeholder={"Enter a new title"}
              value={formData.title}
              onChange={handleChange}
              className="mt-2 block w-full border border-gray-300 rounded-full shadow-sm px-6  py-4"
            />
          </div>
          {/* Input for price */}
          <div>
            <label
              htmlFor="price"
              className="block text-xl font-medium text-gray-700 ml-3"
            >
              Current Price: ${listing?.price}
            </label>
            <input
              type="text"
              id="price"
              name="price"
              placeholder={"Enter a new price"}
              value={formData.price}
              onChange={handleChange}
              className="mt-2 block w-full border border-gray-300 rounded-full shadow-sm px-6  py-4"
            />
          </div>
          {/* Input for imageUrl */}
          <div>
            <label
              htmlFor="imageUrl"
              className="block text-xl font-medium text-gray-700 ml-3 truncate"
            >
              Current Photo: {listing?.imageUrl}
            </label>
            <input
              type="file"
              accept="image/png, image/jpeg"
              id="imageUrl"
              name="imageUrl"
              placeholder="Upload an image (png or jpeg)"
              onChange={handleImageChange}
              className="mt-2 block w-full border-gray-300  px-2 py-2 rounded-full"
            />
          </div>
          {/* Input for a description */}
          <div>
            <label
              htmlFor="description"
              className="block text-xl font-medium text-gray-700 ml-3"
            >
              Current Description: {listing?.description}
            </label>
            <div>
              <textarea
                id="description"
                name="description"
                placeholder={"Enter a new description"}
                value={formData.description}
                onChange={handleChange}
                className=" block w-full border border-gray-300 rounded-xl shadow-sm px-6  py-4 mt-2"
              />
            </div>
          </div>
          {/* Dropdown menu to choose item condition */}
          <div>
            <label
              htmlFor="condition"
              className="block text-xl font-medium text-gray-700 ml-3"
            >
              Current Condition: {listing?.condition}
            </label>
            <input
              type="text"
              id="condition"
              name="condition"
              placeholder={"Enter a new condition"}
              value={formData.condition}
              onChange={handleChange}
              className="mt-2 block w-full border border-gray-300 rounded-full shadow-sm px-6  py-4"
            />
          </div>
          {/* Dropdown menu to choose item category */}
          <div>
            <label
              htmlFor="category"
              className="block text-xl font-medium text-gray-700 ml-3"
            >
              Current Category: {listing?.category}
            </label>
            <input
              type="text"
              id="category"
              name="category"
              placeholder={"Enter a new category"}
              value={formData.category}
              onChange={handleChange}
              className="mt-2 block w-full border border-gray-300 rounded-full shadow-sm px-6  py-4"
            />
          </div>
          {/* Add tags */}
          <div>
            <label
              htmlFor="tags"
              className="block text-xl font-medium text-gray-700 ml-3"
            >
              Current Tags: {listing?.tags}
            </label>
            <input
              type="text"
              id="tags"
              name="tags"
              placeholder={"Enter new tags"}
              value={formData.tags}
              onChange={handleChange}
              className="mt-2 block w-full border border-gray-300 rounded-full shadow-sm px-6 py-4 "
            />
          </div>
          {/* Submit the listing (Call the Create Listing API endpoint) */}
          <button
            type="submit"
            className="w-full bg-blue-600 text-white py-4 px-6  rounded-full hover:bg-blue-700 text-xl font-bold"
          >
            Update Listing
          </button>
        </form>
      </div>
    </div>
  );
};

export default EditingPage;
