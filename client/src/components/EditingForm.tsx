import React, { useEffect, useState } from "react";
import { useUser } from "@clerk/clerk-react";
import { useNavigate, useLocation } from "react-router-dom";
import axios from "axios";
import { editListing } from "../utils/api";
import { PulseLoader } from "react-spinners";

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
    // imageFile: null,
    listingId: "",
  });

  const [originalListing, setOriginalListing] = useState(listing || null);

  // Set form data when listing is available
  useEffect(() => {
    if (listing) {
      setFormData({
        uid: uid || "",
        title: listing.title,
        price: listing.price,
        username: listing.username,
        description: listing.description,
        condition: listing.condition,
        category: listing.category,
        tags: listing.tags,
        // imageFile: null, // Reset to null to handle new uploads
        listingId: listing.listingId,
      });

      setOriginalListing(listing);
    }
  }, [listing, uid]);

  // Handle form input changes
  const handleChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
    >
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

  // Check if any field has changed
  const isFormChanged = () => {
    return (
      formData.title !== originalListing?.title ||
      formData.price !== originalListing?.price ||
      formData.description !== originalListing?.description ||
      formData.condition !== originalListing?.condition ||
      formData.category !== originalListing?.category ||
      formData.tags !== originalListing?.tags
      // || formData.imageFile !== null // Check if image is changed
    );
  };

  // Handle form submission
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Check if the form has changed before submitting
    if (!isFormChanged()) {
      setResponseMessage(
        "Must update at least one field before updating listing."
      );
      return;
    }

    setIsSubmitting(true);

    // If an image file is uploaded, handle the upload process here
    // let imageUrl = formData.imageFile
    //   ? await uploadImage(formData.imageFile)
    //   : listing?.imageUrl;

    // Call the editListing function with the updated form data
    editListing(
      uid,
      listing.listingId,
      formData.title,
      formData.price,
      formData.description,
      // imageUrl, // Make sure imageUrl is passed correctly
      formData.category,
      formData.condition,
      formData.tags
    )
      .then((data) => {
        if (data.response_type === "success") {
          setResponseMessage("Listing updated successfully");
          setIsSubmitting(false);
          navigate("/yourlistings");
        } else {
          console.log("Error updating listing: ", data);
          console.log("condition: ", formData.condition);
          console.log("category: ", formData.category);
          console.log("listing id: ", listing.listingId);
          setResponseMessage("Error updating listing: " + data.response_type);
          setIsSubmitting(false);
          // navigate("/yourlistings");
        }
      })
      .catch((error) => {
        console.error("Error updating listing: ", error);
        setResponseMessage("Error updating listing in server" + error);
        setIsSubmitting(false);
        // navigate("/yourlistings");
      })
      .finally(() => {});
  };

  // Go back to the listings page
  const goBack = () => {
    navigate("/yourlistings");
  };

  // Dummy image upload function (replace with your actual upload logic)
  const uploadImage = async (file: File) => {
    // Example logic: Upload file and get image URL
    // Replace this with your actual file upload logic
    const formData = new FormData();
    formData.append("file", file);
    const response = await axios.post("/upload", formData); // Your upload endpoint
    return response.data.imageUrl; // Assuming your API returns the uploaded image URL
  };

  return (
    <div className="flex flex-col align-center items-center min-h-screen bg-gradient-to-r from-blue-100 to-pink-100">
      <div className="w-3/4 mx-12 my-14 p-8 rounded-3xl shadow-lg bg-white/50 ">
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
              placeholder={listing?.title}
              value={formData.title}
              onChange={handleChange}
              className="mt-2 block w-full border border-gray-300 rounded-full shadow-sm px-6 py-4"
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
              placeholder={listing?.price}
              value={formData.price}
              onChange={handleChange}
              className="mt-2 block w-full border border-gray-300 rounded-full shadow-sm px-6 py-4"
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
              className="mt-2 block w-full border-gray-300 px-2 py-2 rounded-full"
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
                placeholder={listing?.description}
                value={formData.description}
                onChange={handleChange}
                className="block w-full border border-gray-300 rounded-xl shadow-sm px-6 py-4 mt-2"
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
            <select
              id="condition"
              name="condition"
              value={formData.condition}
              onChange={handleChange}
              className="mt-2 block w-full border border-gray-300 rounded-full shadow-sm pl-6 pr-6 py-4 bg-white"
            >
              <option value="" disabled>
                Select a condition
              </option>
              <option value="New">New</option>
              <option value="Like New">Like New</option>
              <option value="Used">Used</option>
            </select>
          </div>
          {/* Dropdown menu to choose item category */}
          <div>
            <label
              htmlFor="category"
              className="block text-xl font-medium text-gray-700 ml-3"
            >
              Current Category: {listing?.category}
            </label>
            <select
              id="category"
              name="category"
              value={formData.category}
              onChange={handleChange}
              className="mt-2 block w-full border border-gray-300 rounded-full shadow-sm pl-6 pr-6 py-4 bg-white"
            >
              <option value="" disabled>
                Select a category
              </option>
              <option value="Clothes">Clothes</option>
              <option value="Tech">Tech</option>
              <option value="School">School</option>
              <option value="Furniture">Furniture</option>
              <option value="Kitchen">Kitchen</option>
              <option value="Bathroom">Bathroom</option>
              <option value="Misc">Miscellaneous</option>
            </select>
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
              className="mt-2 block w-full border border-gray-300 rounded-full shadow-sm px-6 py-4"
            />
          </div>
          {/* Display response message */}
          {responseMessage && (
            <div className="text-xl text-center text-red-600">
              {responseMessage}
            </div>
          )}
          {/* Submit the listing */}
          {isSubmitting ? (
            <button className="w-full bg-blue-600 text-white py-4 px-6 rounded-full hover:bg-blue-700 text-xl font-bold">
              <PulseLoader
                color="#FFFFFF"
                margin={4}
                size={10}
                speedMultiplier={0.7}
              />
            </button>
          ) : (
            <button
              type="submit"
              className="w-full bg-blue-600 text-white py-4 px-6 rounded-full hover:bg-blue-700 text-xl font-bold"
            >
              Update Listing
            </button>
          )}
        </form>
      </div>
    </div>
  );
};

export default EditingPage;
